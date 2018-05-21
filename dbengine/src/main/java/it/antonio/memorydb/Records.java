package it.antonio.memorydb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import it.antonio.memorydb.id.UUIDObjectIdFactory;
import it.antonio.memorydb.index.DefaultIndexEngine;
import it.antonio.memorydb.lock.LockManager;
import it.antonio.memorydb.storage.OnHeapStorage;

public class Records<T> {
	
	private Storage<T> storage = new OnHeapStorage<T>();
	private IndexEngine<T> indexEngine = new DefaultIndexEngine<>();
	private LockManager lockManager;
	private ObjectIdFactory objectIdFactory = new UUIDObjectIdFactory();
	
	
	public Records(LockManager lockManager) {
		super();
		this.lockManager = lockManager;
	}

	public Records(Storage<T> storage, IndexEngine<T> indexEngine, LockManager lockManager,	ObjectIdFactory objectIdFactory) {
		super();
		this.storage = storage;
		this.indexEngine = indexEngine;
		this.lockManager = lockManager;
		this.objectIdFactory = objectIdFactory;
	}



	public void addIndex(Index<T> index ) {
		indexEngine.add(index);
	}
	
	public void insert(T o) {
		lockManager.acquireWriteLock();
		try {
			ObjectId id = objectIdFactory.create();
			indexEngine.addObject(id, o);
			storage.save(id, o);
		} finally {
			lockManager.releaseWriteLock();
		}
		
	}
	
	
	public Results<T> find(Query<T> query){
		lockManager.acquireReadLock();
		
		return new Results<T>() {

			@Override
			public Iterator<T> iterator() {
				Iterator<ObjectId> it = indexEngine.find(query, storage).iterator();
				return new Iterator<T>() {

					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@SuppressWarnings("unchecked")
					@Override
					public T next() {
						return (T) storage.get(it.next());
					}
				};
			}

			@Override
			public void close() {
				lockManager.releaseReadLock();
			}
			
		};
	}

	public void remove(Query<T> query){
		lockManager.acquireWriteLock();
		try {
			List<ObjectId> ids = new LinkedList<>();
			
			indexEngine.find(query, storage).iterator().forEachRemaining(ids::add);
			
			ids.forEach(oid -> {
				indexEngine.remove(oid);
				storage.remove(oid);
			});
		} finally {
			lockManager.releaseWriteLock();
		}
		
		
	}
	
	public void update(Query<T> query, T o) {
		lockManager.acquireWriteLock();
		try {
			List<ObjectId> ids = new LinkedList<>();
			
			indexEngine.find(query, storage).iterator().forEachRemaining(ids::add);
			
			ids.forEach(oid -> {
				indexEngine.update(oid, o);
				storage.update(oid, o);
			});
		} finally {
			lockManager.releaseWriteLock();
		}
		
	}
	
	
	public long count(Query<T> query){
		lockManager.acquireReadLock();
		try {
			ResultIterator rit = indexEngine.find(query, storage);
			if(rit.sizeExtimated()) {
				Iterator<ObjectId> it = rit.iterator();
				long size = 0;
				while(it.hasNext()) {
					it.next(); size++;
				}
				return size;
			} else {
				return rit.size();
			}
			
			
		} finally {
			lockManager.releaseReadLock();
		}
		
		
		
	}
	
	public long size() {
		return storage.size();
	}
	
	
}
