package it.antonio.memorydb.storage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.antonio.memorydb.IndexEngine;
import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.ObjectIdFactory;
import it.antonio.memorydb.Storage;
import it.antonio.memorydb.lock.LockManager;

public class OnHeapFileBackedStorage<T> implements Storage<T>{
	
	private Map<ObjectId, T> map = new HashMap<>();
	private ConcurrentHashMap<ObjectId, Long> positionMap = new ConcurrentHashMap<>();
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private RandomAccessFile file;
	private FileBackedStorageAdapter<T> adapter;
	
	private int W1 = 10, W2 = 100, W3 = 1000; // code 
	private long END_HEADER_SIZE = 4 * 3; // W1 + W2 + W3 

	private IndexEngine<T> indexEngine;
	private ObjectIdFactory objectIdFactory;
	private LockManager lockManager;
	
	public OnHeapFileBackedStorage(File file, FileBackedStorageAdapter<T> adapter, IndexEngine<T> indexEngine, LockManager lockManager, ObjectIdFactory objectIdFactory) {
		super();
		this.adapter = adapter;
		this.indexEngine = indexEngine;
		this.lockManager = lockManager;
		this.objectIdFactory = objectIdFactory;
		try {
			this.file = new RandomAccessFile(file, "rw");
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	public void init() {
		try {
			if(this.file.length() == 0) {
				file.writeInt(W1);
				file.writeInt(W2);
				file.writeInt(W3);
			} else {
				lockManager.acquireWriteLock();
				try {
					boolean end = false;
					while(!end) {
						boolean valid = file.readBoolean();
						if(!valid) {
							file.skipBytes(adapter.size());
						} else {
							T obj = adapter.deserialize(file);
							ObjectId id = objectIdFactory.create();
							map.put(id, obj);
							indexEngine.addObject(id, obj);
						}
						long position = file.getFilePointer();
						int w1 = file.readInt();
						int w2 = file.readInt();
						int w3 = file.readInt();
						if(w1 == W1 && w2 == W2 && w3 == W3) {
							end = true;
						} else {
							file.seek(position);
						}
						
						
					} 
					
					
					
					
				}finally {
					lockManager.releaseWriteLock();
				}
				
				
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
			
		
	}

	@Override
	public void save(ObjectId id, T obj) {
		map.put(id, obj);
		service.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					long position = file.length() - END_HEADER_SIZE;
					file.seek(position);
					file.writeBoolean(true); //valid
					adapter.serialize(obj, file);
					
					file.writeInt(W1);
					file.writeInt(W2);
					file.writeInt(W3);
					positionMap.put(id, position);
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
				
				
			}
		});
	}

	@Override
	public Object get(ObjectId id) {
		return map.get(id);
	}

	@Override
	public void remove(ObjectId id) {
		map.remove(id);
		service.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					long position = positionMap.get(id);
					file.seek(position);
					file.writeBoolean(false); // invalid
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
				
				
			}
		});
	}

	@Override
	public long size() {
		return map.size();
	}
	
	public static interface FileBackedStorageAdapter<T>{
		public int size();
		public void serialize(T obj, RandomAccessFile file);
		public T deserialize(RandomAccessFile file);
		
	}
 
	
	public void close() {
		service.shutdown();
		while(!service.isTerminated()) {}
		try {
			this.file.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void update(ObjectId oid, T o) {
		throw new UnsupportedOperationException();
	}


}
