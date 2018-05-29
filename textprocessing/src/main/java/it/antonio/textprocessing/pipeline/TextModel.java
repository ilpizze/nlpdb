package it.antonio.textprocessing.pipeline;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import it.antonio.util.LazyIterator;

public class TextModel implements Iterable<TextModelWord>{
	public String[] tokens;
	public String[] posTags;
	public String[] posTagsComplex;
	public String[] lemmas;
	
	
	public TextModel(String[] tokens, String[] posTags, String[] posTagsComplex, String[] lemmas) {
		super();
		this.tokens = tokens;
		this.posTags = posTags;
		this.posTagsComplex = posTagsComplex;
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


	public Stream<TextModelWord> stream() {
		Iterable<TextModelWord> it = ()-> iterator();
		return StreamSupport.stream(it .spliterator(), false);
	}

	
	
	
	
}
