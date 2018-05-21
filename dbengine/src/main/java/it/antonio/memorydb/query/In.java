package it.antonio.memorydb.query;

import java.util.Collection;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Query;

public class In<T> implements Query<T> {

	Attribute<T> attribute;
	Collection<?> values;
	
	public In(Attribute<T> attribute, Collection<?> values) {
		super();
		this.attribute = attribute;
		this.values = values;
	}

	public Collection<?> getValues() {
		return values;
	}
	
	public Attribute<?> getAttribute() {
		return attribute;
	}
	@Override
	public boolean match(T obj) {
		return values.contains(attribute.val(obj));
	}
	
	@Override
	public String toString() {
		return attribute + " in " + values;
	}
}
