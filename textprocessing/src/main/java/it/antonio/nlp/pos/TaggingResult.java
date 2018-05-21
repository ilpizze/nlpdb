package it.antonio.nlp.pos;
public class TaggingResult {
  public String[] words;

  public String[] results;

  public TaggingResult(String[] words, String[] results) {
    this.words = words;
    this.results = results;
  }

  public static TaggingResult of(String[] words, String[] results) {
    return new TaggingResult(words, results);
  }
}