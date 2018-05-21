package it.antonio.memorydb.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import it.antonio.memorydb.index.common.Tokenizer;
import it.antonio.memorydb.query.Text;

public class TextInvertedIndex<OBJ> implements Index<OBJ> {

	private Attribute<OBJ> feature;
	private Tokenizer tokenizer;
	private Map<String, Map<ObjectId, Integer>> map = new HashMap<>();
	private Map<ObjectId, Iterable<String>> wordsById = new HashMap<>();
	
	
	public TextInvertedIndex(Tokenizer tokenizer, Attribute<OBJ> feature) {
		super();
		this.feature = feature;
		this.tokenizer = tokenizer;
	}

	@Override
	public void insert(OBJ o, ObjectId id) {
		Iterable<String> texts = feature.asTextIterable(o);
		
		Set<String> tokens = new HashSet<>();
		for(String text: texts) {
			tokenizer.onToken(text, token -> {
				
				if(map.containsKey(token)) {
					Map<ObjectId, Integer> idMap = map.get(token);	
					if(idMap.containsKey(id)) {
						Integer i = idMap.get(id);
						i++;
						idMap.put(id, i);
					} else {
						idMap.put(id, 1);
					}
					
				} else {
					Map<ObjectId, Integer> idMap = new HashMap<>();
					idMap.put(id, 1);
					map.put(token, idMap);
				}
				
				tokens.add(token);
			});
		}
		
		wordsById.put(id, tokens);
		
	}
	
	public ResultIterator find(Query<OBJ> query, Storage<OBJ> storage) {
		
			Map<ObjectId, Integer> records = map.get(cast(query, Text.class).getWord().toLowerCase());	
			
			if(records == null) {
				return ResultIterator.empty();
			}
			
			Set<ObjectId> recordsKeys = records.keySet();
			return new ResultIterator() {

				@Override
				public Iterator<ObjectId> iterator() {
					return recordsKeys.iterator();	
					
				}
				@Override
				public Long size() {
					return (long) recordsKeys.size(); 
				}
				@Override
				public boolean contains(ObjectId id) {
					return recordsKeys.contains(id);
				}
				
			};
	
		
	}
	
	@Override
	public boolean supports(Query<?> q) {
		if(check(q, Text.class)) {
			return feature.equals(cast(q, Text.class).getAttribute());
		}
		return false;
	}

	@Override
	public void remove(ObjectId oid) {
		for(String text: wordsById.get(oid)) {
			Map<ObjectId, Integer> wordMap = map.get(text);
			if(wordMap != null) {
				wordMap.remove(oid);
				if(wordMap.isEmpty()) {
					map.remove(text);
				}	
			}
			
		}
	}

	@Override
	public void update(ObjectId oid, OBJ o) {
		remove(oid);
		insert(o, oid);
	}

	
}
