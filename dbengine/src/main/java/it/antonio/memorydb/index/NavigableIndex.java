package it.antonio.memorydb.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Index;
import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.Query;
import it.antonio.memorydb.ResultIterator;
import it.antonio.memorydb.Storage;
import it.antonio.memorydb.query.Greater;
import it.antonio.memorydb.query.Lesser;

public class NavigableIndex<OBJ> implements Index<OBJ> {

	private Attribute<OBJ> attribute;
	private NavigableMap<Object, List<ObjectId>> map = new TreeMap<>();
	private Map<ObjectId, Object> removeMap = new HashMap<>();
	
	public NavigableIndex(Attribute<OBJ> feature) {
		super();
		this.attribute = feature;
	}

	@Override
	public void insert(OBJ o, ObjectId id) {
		Iterable<? extends Object> fs = attribute.asIterable(o);
		for(Object f: fs) {
			if(map.containsKey(f)) {
				map.get(f).add(id);
			} else {
				List<ObjectId> set = new LinkedList<>();
				set.add(id);
				map.put(f, set);
				removeMap.put(id, f);
			}
		}
		
	}
	
	public ResultIterator find(Query<OBJ> query, Storage<OBJ> storage) {
		if(check(query, Greater.class )) {
		
			Comparable<?> value = (Comparable<?>) cast(query, Greater.class).getValue();
			SortedMap<Object, List<ObjectId>> objs = map.tailMap(value, false);
			
			return new ResultIterator() {

				@Override
				public Iterator<ObjectId> iterator() {
					return objs.values().stream().flatMap((list) -> list.stream()). iterator();
				}
				
				@Override
				public Long size() {
					return objs != null ? objs.size() : 0l; 
				}
					
				@Override
				public boolean contains(ObjectId id) {
					return objs != null && objs.values().stream().flatMap((list) -> list.stream()).anyMatch((listId) -> listId.equals(id));
				}	
			};
		}
		if(check(query, Lesser.class )) {
			Comparable<?> value = (Comparable<?>) cast(query, Greater.class).getValue();
			SortedMap<Object, List<ObjectId>> objs = map.headMap(value, false);
			
			return new ResultIterator() {

				@Override
				public Iterator<ObjectId> iterator() {
					return objs.values().stream().flatMap((list) -> list.stream()). iterator();
				}
				
				@Override
				public Long size() {
					return objs != null ? objs.size() : 0l; 
				}

				@Override
				public boolean contains(ObjectId id) {
					return objs != null && objs.values().stream().flatMap((list) -> list.stream()).anyMatch((listId) -> listId.equals(id));
				}
					
			};
		}
		return null;
	}
	
	@Override
	public boolean supports(Query<?> q) {
		if(check(q, Greater.class)) {
			return attribute.equals(cast(q, Greater.class).getAttribute());
		}
		if(check(q, Lesser.class)) {
			return attribute.equals(cast(q, Lesser.class).getAttribute());
		}
		return false;
	}

	@Override
	public void remove(ObjectId oid) {
		Object attributeValue = removeMap.remove(oid);
		List<ObjectId> ids = map.get(attributeValue);
		ids.remove(oid); // expensive for long lists -> linkedlist linear remove
		if(ids.isEmpty()) {
			map.remove(attributeValue);
		}
	}
	
	@Override
	public void update(ObjectId oid, OBJ o) {
		remove(oid);
		insert(o, oid);
	}
}
