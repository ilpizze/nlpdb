package it.antonio.memorydb.storage;

import java.util.HashMap;
import java.util.Map;

import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.Storage;

public class OnHeapStorage<T> implements Storage<T>{
	
	private Map<ObjectId, T> map = new HashMap<>();

	@Override
	public void save(ObjectId id, T obj) {
		map.put(id, obj);
	}

	@Override
	public Object get(ObjectId id) {
		return map.get(id);
	}

	@Override
	public void remove(ObjectId id) {
		map.remove(id);
	}

	@Override
	public long size() {
		return map.size();
	}

	@Override
	public void update(ObjectId id, T obj) {
		map.put(id, obj);	
	}


}
