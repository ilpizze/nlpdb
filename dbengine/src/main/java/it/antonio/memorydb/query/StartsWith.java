package it.antonio.memorydb.query;

import java.util.function.Predicate;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Query;

public class StartsWith<T> implements Query<T>{

	Attribute<T> attribute;
	String word;
	Predicate<String> predicate;
	
	public StartsWith(Attribute<T> attribute, String word) {
		super();
		this.attribute = attribute;
		this.word = word;
		this.predicate = (s) -> s!= null && s.startsWith(word);
	}

	public String getWord() {
		return word;
	}
	
	public Attribute<T> getAttribute() {
		return attribute;
	}

	@Override
	public boolean match(T obj) {
		return attribute.asTextStream(obj).allMatch(predicate);
	}

	
}
