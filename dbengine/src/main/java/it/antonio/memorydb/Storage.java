package it.antonio.memorydb;

public interface Storage<T>  {
	public void save(ObjectId id, T obj);
	public Object get(ObjectId id);
	public void remove(ObjectId oid);
	public long size();
	public void update(ObjectId oid, T o);
}
