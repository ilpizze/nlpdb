package it.antonio.memorydb.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Index;
import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.Query;
import it.antonio.memorydb.ResultIterator;
import it.antonio.memorydb.Storage;
import it.antonio.memorydb.index.common.BKTree;
import it.antonio.memorydb.index.common.Tokenizer;
import it.antonio.memorydb.query.FuzzyText;

public class TextDistanceIndex<OBJ> implements Index<OBJ> {

	private Attribute<OBJ> feature;
	private Tokenizer tokenizer;
	private BKTree<List<ObjectId>> tree = new BKTree<>();
	private int distance;
	private Map<String, List<ObjectId>> ids = new HashMap<>(); 
	private Set<ObjectId> removed = new HashSet<>(); 
	
	
	public TextDistanceIndex(Tokenizer tokenizer, Attribute<OBJ> feature, int distance) {
		super();
		this.feature = feature;
		this.tokenizer = tokenizer;
		this.distance = distance;
	}

	@Override
	public void insert(OBJ o, ObjectId id) {
		Iterable<String> texts = feature.asTextIterable(o);
		
		for(String text: texts) {
			tokenizer.onToken(text, token -> {
				if(ids.containsKey(token)) {
					ids.get(token).add(id);
				} else {
					List<ObjectId> ids = new LinkedList<>();
					ids.add(id);
					tree.add(token, ids);
				}
				
			});
		}
		
	}
	
	public ResultIterator find(Query<OBJ> query, Storage<OBJ> storage) {
		
			Collection<List<ObjectId>> records = tree.find(cast(query, FuzzyText.class).getWord().toLowerCase(), distance);	
			
			if(records == null) {
				return ResultIterator.empty();
			}
			
			List<ObjectId> filteredRecords = records.stream().flatMap(l -> l.stream()).filter(id -> !removed.contains(id)).collect(Collectors.toList());
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
		if(check(q, FuzzyText.class)) {
			return feature.equals(cast(q, FuzzyText.class).getAttribute());
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
