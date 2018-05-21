package it.antonio.memorydb;

import java.util.Collections;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@FunctionalInterface
public interface Attribute<OBJ> {
	Object val(OBJ o);

	public default Attribute<OBJ> withName(String name){
		return withName(name, this);
	}
	
	@SuppressWarnings("unchecked")
	default Iterable<? extends Object> asIterable(OBJ o){
		Object val = val(o);
		if(val instanceof Iterable) {
			return Iterable.class.cast(val);
		} else {
			return Collections.singletonList(val);
		}
	}

	@SuppressWarnings("unchecked")
	default Iterable<String> asTextIterable(OBJ o){
		Object val = val(o);
		if(val instanceof Iterable) {
			return Iterable.class.cast(val);
		} else {
			return Collections.singletonList((String) val);
		}
	}
	
	@SuppressWarnings("unchecked")
	default Stream<String> asTextStream(OBJ o){
		Object val = val(o);
		if(val instanceof Iterable) {
			return StreamSupport.stream(Iterable.class.cast(val).spliterator(), false);
		} else {
			return Stream.of((String) val);
		}
	}
	
	public static <OBJ> Attribute<OBJ> withName(String name, Attribute<OBJ> att) {
		return new Attribute<OBJ>() {
			@Override
			public Object val(OBJ o) {
				return att.val(o);
			}
			
			@Override
			public String toString() {
				return name;
			}
		};
	}
	
}
