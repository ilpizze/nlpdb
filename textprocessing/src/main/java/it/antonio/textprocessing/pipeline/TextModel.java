package it.antonio.textprocessing.pipeline;

import java.util.Iterator;

import it.antonio.util.LazyIterator;

public class TextModel implements Iterable<TextModelWord>{
	public String[] tokens;
	public String[] posTags;
	public String[] lemmas;
	
	public TextModel(String[] tokens, String[] posTags, String[] lemmas) {
		super();
		this.tokens = tokens;
		this.posTags = posTags;
		this.lemmas = lemmas;
	}

	@Override
	public Iterator<TextModelWord> iterator() {
		return new LazyIterator<TextModelWord>() {
			int i = 0;
			@Override
			protected TextModelWord computeNext() {
				if(i < tokens.length) {
					int index = i++;
					return new TextModelWord(tokens[index], posTags[index], lemmas[index]);
				}
				return endOfData();
			}
		};
	}

	
	
}
