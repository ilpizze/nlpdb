package it.antonio.memorydb;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Results<T> extends Iterable<T> {

	void close();

	default Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}
}
