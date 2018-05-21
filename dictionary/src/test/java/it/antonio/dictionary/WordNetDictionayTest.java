package it.antonio.dictionary;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import it.antonio.dictionary.wordnet.WordNetDictionary;

public class WordNetDictionayTest {

	@Test
	public void test() {
		InputStream stream = WordNetDictionary.class.getResourceAsStream("wn-ita-lmf.xml");
		Dictionary dictionary = WordNetDictionary.createFrom(stream );
		List<DictionaryWord> words = dictionary.find("mangiare");
		
		System.out.println("---------------");
		words.forEach(w -> {
			System.out.println(w.getLemma() + " - " + w.getPartOfSpeech());
    		w.getRelations().forEach(r -> System.out.println(" " + r.type + "-" + r.word.getLemma() ));
    		System.out.println("---------------");	

		});
	}
	
}
