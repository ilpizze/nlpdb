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

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Index;
import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.Query;
import it.antonio.memorydb.ResultIterator;
import it.antonio.memorydb.Storage;
import it.antonio.memorydb.query.Eq;
import it.antonio.memorydb.query.In;

public class HashIndex<OBJ> implements Index<OBJ> {

	private Attribute<OBJ> attribute;
	private Map<Object, List<ObjectId>> map = new HashMap<>();
	private Map<ObjectId, Object> removeMap = new HashMap<>();
	
	public HashIndex(Attribute<OBJ> feature) {
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
		if(query instanceof Eq) {
			List<ObjectId> objs = map.get(cast(query, Eq.class).getValue());	
			return new ResultIterator() {

				@Override
				public Iterator<ObjectId> iterator() {
					if(objs != null) {
						return objs.iterator();	
					} else {
						return Collections.emptyIterator();
					}
					
				}

				@Override
				public Long size() {
					return objs != null ? objs.size() : 0l; 
				}

				@Override
				public boolean contains(ObjectId id) {
					return objs != null ? objs.contains(id) : false;  // expensive on large collections
				}
					
			};
			
		}	
		if(query instanceof In) {
			Collection<?> values = cast(query, In.class).getValues();
			// expensive on large collections
			
			Set<ObjectId> objs = new HashSet<>();
			for(Object value: values) {
				List<ObjectId> l = map.get(value);
				if(l!= null) {
					objs.addAll(l);
				}
			}
			
			return new ResultIterator() {

				@Override
				public Iterator<ObjectId> iterator() {
					return objs.iterator();	
				}

				@Override
				public Long size() {
					return objs != null ? objs.size() : 0l; 
				}

				@Override
				public boolean contains(ObjectId id) {
					return objs.contains(id); // expensive on large collections
				}
					
			};
			
		}	
		
		
		throw new UnsupportedOperationException();
		
	}

	@Override
	public boolean supports(Query<?> q) {
		if(check(q, Eq.class)) {
			return attribute.equals(cast(q, Eq.class).getAttribute());
		}
		if(check(q, In.class)) {
			return attribute.equals(cast(q, In.class).getAttribute());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Hash index " + attribute;
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
