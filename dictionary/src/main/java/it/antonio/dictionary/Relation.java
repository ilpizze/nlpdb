package it.antonio.dictionary;

public class Relation {
	RelationType type;
	DictionaryWord word;

	public Relation(RelationType type, DictionaryWord word) {
		super();
		this.type = type;
		this.word = word;
	}

	public RelationType getType() {
		return type;
	}

	public DictionaryWord getWord() {
		return word;
	}

}