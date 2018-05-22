package it.antonio.nlp.lemma.morphit;

import org.junit.Assert;
import org.junit.Test;

import it.antonio.textprocessing.lemma.Lemmatizer;
import it.antonio.textprocessing.lemma.morphit.MorphitLemmatizer;

public class MorphitLemmatizerTest {

	@Test
	public void testMorphit() {
		Lemmatizer lemmatizer = MorphitLemmatizer.create();

		Assert.assertEquals("guardare", lemmatizer.getLemma("guardami"));

		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.printf("%.3fMB\n", usedMemory / (1024.0 * 1024.0));

	}

}