package it.antonio.dictionary;

import java.util.LinkedList;
import java.util.List;

public class DictionaryWord {
	private String lemma;
	private String partOfSpeech;
	private List<Relation> relations = new LinkedList<>();
	
	public DictionaryWord(String lemma, String partOfSpeech) {
		super();
		this.lemma = lemma;
		this.partOfSpeech = partOfSpeech;
	}

	public String getLemma() {
		return lemma;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public List<Relation> getRelations() {
		return relations;
	}
	
	
	
	
	
	
}
