package it.antonio.textprocessing.pipeline.simple;

import it.antonio.textprocessing.lemma.Lemmatizer;
import it.antonio.textprocessing.pipeline.TextModel;
import it.antonio.textprocessing.pipeline.TextPipeline;
import it.antonio.textprocessing.pos.PosTagger;
import it.antonio.textprocessing.pos.TaggingResult;
import it.antonio.textprocessing.tokenizer.Tokenizer;

public class SimpleTextPipeline implements TextPipeline{

	private Tokenizer tokenizer;
	private PosTagger posTagger;
	private Lemmatizer lemmatizer;
	
	public SimpleTextPipeline(Tokenizer tokenizer, PosTagger posTagger, Lemmatizer lemmatizer) {
		super();
		this.tokenizer = tokenizer;
		this.posTagger = posTagger;
		this.lemmatizer = lemmatizer;
	}
	
	@Override
	public TextModel createModel(String input) {
		String[] tokens = tokenizer.tokenize(input);
		TaggingResult posResult = posTagger.tag(tokens);
		String[] lemmas = lemmatizer.lemmas(tokens);
		
		return new TextModel(tokens, posResult.posTags, posResult.posTagsComplex, lemmas);
	}
	
	
}
