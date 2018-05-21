package it.antonio.memorydb;

public interface IndexEngine<T> {

	void add(Index<T> e);

	void addObject(ObjectId id, T o);

	ResultIterator find(Query<T> query, Storage<T> storage);

	void remove(ObjectId oid);

	void update(ObjectId oid, T o);

}
