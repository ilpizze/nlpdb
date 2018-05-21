package it.antonio.memorydb.query;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Query;

public class FuzzyText<OBJ> implements Query<OBJ>{

	Attribute<OBJ> attribute;
	String word;
	
	public FuzzyText(Attribute<OBJ> attribute, String word) {
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

	public boolean match(OBJ feature) {
		throw new UnsupportedOperationException();
	}


	
}
