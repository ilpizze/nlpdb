package it.antonio.memorydb.index;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Index;
import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.Query;
import it.antonio.memorydb.ResultIterator;
import it.antonio.memorydb.Storage;
import it.antonio.memorydb.index.common.KGramTree;
import it.antonio.memorydb.query.Wildcard;

public class TextKGramIndex<OBJ> implements Index<OBJ> {

	private Attribute<OBJ> feature;
	private KGramTree<ObjectId> kgram = new KGramTree<>();
	private Set<ObjectId> removed = new HashSet<>(); 
	
	
	public TextKGramIndex(Attribute<OBJ> feature) {
		super();
		this.feature = feature;
	}

	@Override
	public void insert(OBJ o, ObjectId id) {
		Iterable<String> texts = feature.asTextIterable(o);
		
		for(String text: texts) {
			kgram.add(text, id);
		}
		
	}
	
	public ResultIterator find(Query<OBJ> query, Storage<OBJ> storage) {
		
			
			Set<ObjectId> records = kgram.find(cast(query, Wildcard.class).getWord());
			
			if(records == null) {
				return ResultIterator.empty();
			}
			
			List<ObjectId> filteredRecords = records.stream().filter(id -> !removed.contains(id)).collect(Collectors.toList());
			return new ResultIterator() {

				@Override
				public Iterator<ObjectId> iterator() {
					return filteredRecords.iterator();	
					
				}
				@Override
				public Long size() {
					return (long) filteredRecords.size(); 
				}
				@Override
				public boolean contains(ObjectId id) {
					return filteredRecords.contains(id);
				}
				
			};

		
	}
	
	@Override
	public boolean supports(Query<?> q) {
		if(check(q, Wildcard.class)) {
			return feature.equals(cast(q, Wildcard.class).getAttribute());
		}
		return false;
	}

	@Override
	public void remove(ObjectId oid) {
		removed.add(oid);
	}
	
	@Override
	public void update(ObjectId oid, OBJ o) {
		throw new UnsupportedOperationException();
	}

}
