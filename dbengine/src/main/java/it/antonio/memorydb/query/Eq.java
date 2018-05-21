package it.antonio.memorydb.query;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Query;

public class Eq<T> implements Query<T> {

	Attribute<T> attribute;
	Object value;
	
	public Eq(Attribute<T> attribute, Object value) {
		super();
		this.attribute = attribute;
		this.value = value;
	}

	public Object getValue() {
		return value;
	}
	
	public Attribute<?> getAttribute() {
		return attribute;
	}
	@Override
	public boolean match(T obj) {
		return value.equals(attribute.val(obj));
	}
	
	@Override
	public String toString() {
		return attribute + " eq " + value;
	}
}
