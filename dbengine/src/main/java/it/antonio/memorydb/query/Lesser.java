package it.antonio.memorydb.query;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Query;

public class Lesser<T> implements Query<T> {

	Attribute<T> attribute;
	Comparable<? extends Comparable<?>> value;
	
	public Lesser(Attribute<T> attribute, Comparable<? extends Comparable<?>> value) {
		super();
		this.attribute = attribute;
		this.value = value;
	}
	
	public Comparable<?> getValue() {
		return value;
	}

	public Attribute<?> getAttribute() {
		return attribute;
	}
	
	public boolean match(T obj) {
		if(value == null) {
			return false;
		} else {
			return false;//return value.compareTo(attribute.val(obj)) > 0;
		}
	}

}
