package it.antonio.memorydb.index;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.antonio.memorydb.Index;
import it.antonio.memorydb.IndexEngine;
import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.Query;
import it.antonio.memorydb.ResultIterator;
import it.antonio.memorydb.Storage;
import it.antonio.memorydb.query.And;
import it.antonio.memorydb.query.Or;

public class DefaultIndexEngine<T> implements IndexEngine<T> {
	private List<CompoundIndex<T>> compundIndexes = new ArrayList<>();
	private List<Index<T>> defaultIndexes = new ArrayList<>();
	private Index<T> fullScanIndex;
	

	public DefaultIndexEngine() {
		super();
	}

	@Override
	public void add(Index<T> i) {
		if(i instanceof CompoundIndex) {
			compundIndexes.add((CompoundIndex<T>) i);
		} else if(i instanceof FullScanIndex) {
			fullScanIndex = i;
		} else {
			defaultIndexes.add(i);
		}
		
	}

	@Override
	public void addObject(ObjectId id, T o) {
		defaultIndexes.forEach((i) -> {
			i.insert(o, id);
		});	
		compundIndexes.forEach((i) -> {
			i.insert(o, id);
		});
		if(fullScanIndex != null) {
			fullScanIndex.insert(o, id);
		}
	}
	
	@Override
	public ResultIterator find(Query<T> query, Storage<T> storage) {
		if(query instanceof And) {
			return findAnd((And<T>) query, storage);	
		} else if(query instanceof Or) {
			return findOr((Or<T>) query, storage);
		} else {
			
			return findSimple(query, storage);
		}
			
	}
	
	private ResultIterator findAnd(And<T> and, Storage<T> storage) {
		List<ResultIterator> its = new LinkedList<>();
		for(Index<T> index: compundIndexes) {
			if(index.supports(and)) {
				its.add(index.find(and, storage));
			}
		}
	

		if(its.size() == 1) {
			// compund index
			return its.get(0);
		} else {
			for(Query<T> subQ: and.getQueries()) {
				its.add(find(subQ, storage));
				
				/*
				for(Index<T> index: defaultIndexes) {
					if(index.supports(subQ)) {
						its.add(index.find(subQ, storage));
					}
				}*/
			}
		
			if(its.size() >= and.getQueries().size()) {
				return ResultIterator.and(its);
			} else {
				return ResultIterator.empty(); 
			}
		
		
		}
	
	}
	
	private ResultIterator findOr(Or<T> or, Storage<T> storage) {
		List<ResultIterator> its = new LinkedList<>();
		for(Query<T> subQ: or.getQueries()) {
			for(Index<T> index: defaultIndexes) {
				if(index.supports(subQ)) {
					its.add(index.find(subQ, storage));
				}
			}
		}
		
		return ResultIterator.or(its);
	}
	
	private ResultIterator findSimple(Query<T> query, Storage<T> storage) {
		List<ResultIterator> its = new LinkedList<>();
		for(Index<T> index: defaultIndexes) {
			if(index.supports(query)) {
				its.add(index.find(query, storage));
			}
		}
		
		if(its.size() != 0) {
			return its.get(0); 
		} else {
			
			// try on full scan index
			if(fullScanIndex != null) { 
				
				
				if(fullScanIndex.supports(query) ) {
					return fullScanIndex.find(query, storage);	
				} else {
					return ResultIterator.empty();		
				}
				
			} else {
				throw new IllegalStateException("Query non supported: " + query.getClass().getSimpleName());
			}
		}
		
		
	}

	@Override
	public void remove(ObjectId oid) {
		defaultIndexes.forEach((i) -> {
			i.remove(oid);
		});	
		compundIndexes.forEach((i) -> {
			i.remove(oid);
		});
		if(fullScanIndex != null) {
			fullScanIndex.remove(oid);
		}
	}

	@Override
	public void update(ObjectId oid, T o) {
		defaultIndexes.forEach((i) -> {
			i.update(oid, o);
		});	
		compundIndexes.forEach((i) -> {
			i.update(oid, o);
		});
		if(fullScanIndex != null) {
			fullScanIndex.update(oid, o);
		}
	}
	
	
}
