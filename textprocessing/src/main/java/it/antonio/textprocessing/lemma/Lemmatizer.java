package it.antonio.textprocessing.lemma;

public interface Lemmatizer {
	String lemma(String word);

	String[] lemmas(String[] tokens);
}
