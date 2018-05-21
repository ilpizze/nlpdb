package it.antonio.memorydb.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Index;
import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.Query;
import it.antonio.memorydb.ResultIterator;
import it.antonio.memorydb.Storage;
import it.antonio.memorydb.query.Eq;

public class UniqueIndex<OBJ> implements Index<OBJ> {

	private Attribute<OBJ> attribute;
	private Map<Object, ObjectId> map = new HashMap<>();
	private Map<ObjectId, Object> idMap = new HashMap<>();
	
	public UniqueIndex(Attribute<OBJ> feature) {
		super();
		this.attribute = feature;
	}

	@Override
	public void insert(OBJ o, ObjectId id) {
		Iterable<? extends Object> fs = attribute.asIterable(o);
		for(Object f: fs) {
			if(map.containsKey(f)) {
				throw new RuntimeException("NOT UNIQUE: " + f);
			}
			map.put(f, id);	
		}
		
	}
	
	public ResultIterator find(Query<OBJ> query, Storage<OBJ> storage) {
		
			ObjectId obj = map.get(cast(query, Eq.class).getValue());	
			
			return new ResultIterator() {

				@Override
				public Iterator<ObjectId> iterator() {
					return Collections.singleton(obj).iterator();
				}
				
				@Override
				public Long size() {
					return obj != null ? 1 : 0l; 
				}
				
				@Override
				public boolean contains(ObjectId id) {
					return obj.equals(id);
				}
			};
	
	}

	@Override
	public boolean supports(Query<?> q) {
		if(check(q, Eq.class)) {
			return attribute.equals(cast(q, Eq.class).getAttribute());
		}
		return false;
	}

	@Override
	public void remove(ObjectId oid) {
		map.remove(idMap.remove(oid));
	}
	
	@Override
	public void update(ObjectId oid, OBJ o) {
		remove(oid);
		insert(o, oid);
	}
	
}
