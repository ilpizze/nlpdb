package it.antonio.memorydb;

@FunctionalInterface
public interface Query<T> {
	
	boolean match(T obj);
	
}
