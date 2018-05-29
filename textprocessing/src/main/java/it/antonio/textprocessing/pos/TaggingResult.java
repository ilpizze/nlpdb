package it.antonio.textprocessing.pos;

public class TaggingResult {

	public String[] words;
	public String[] posTagsComplex;
	public String[] posTags;

	public TaggingResult(String[] words, String[] posTagsComplex, String[] posTags) {
		super();
		this.words = words;
		this.posTagsComplex = posTagsComplex;
		this.posTags = posTags;
	}

	public static TaggingResult of(String[] words, String[] posTagsComplex, String[] posTags) {
		return new TaggingResult(words, posTagsComplex, posTags);
	}
}