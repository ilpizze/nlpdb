package it.antonio.memorydb.query;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Query;

public class Wildcard<OBJ> implements Query<OBJ>{

	Attribute<OBJ> attribute;
	String word;
	
	public Wildcard(Attribute<OBJ> attribute, String word) {
		super();
		this.attribute = attribute;
		this.word = word;
	}

	public String getWord() {
		return word;
	}
	
	public Attribute<OBJ> getAttribute() {
		return attribute;
	}

	@Override
	public boolean match(OBJ obj) {
		return false;
	}

	
}
