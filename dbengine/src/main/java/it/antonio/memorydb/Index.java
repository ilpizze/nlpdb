package it.antonio.memorydb;

public interface Index<OBJ> {

	ResultIterator find(Query<OBJ> query, Storage<OBJ> storage);

	void insert(OBJ o, ObjectId id);
	
	boolean supports(Query<?> q);
	
	default boolean check(Query<?> query, Class<?> clz) {
		return clz.equals(query.getClass()); 
	}
	
	default <Q extends Query<?>> Q cast(Query<?> query, Class<Q> clz) {
		return clz.cast(query); 
	}

	void remove(ObjectId oid);

	void update(ObjectId oid, OBJ o);
	
}
