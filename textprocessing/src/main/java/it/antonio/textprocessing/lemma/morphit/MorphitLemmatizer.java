package it.antonio.textprocessing.lemma.morphit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import it.antonio.textprocessing.lemma.Lemmatizer;

public class MorphitLemmatizer implements Lemmatizer {

	private final Map<String, String> lemmas;

	public MorphitLemmatizer(Map<String, String> lemmas) {
		super();
		this.lemmas = lemmas;
	}

	@Override
	public String getLemma(String word) {
		return lemmas.get(word);
	}

	public static MorphitLemmatizer create() {

		try {
			Map<String, String> lemmas = new HashMap<>();

			InputStream stream = MorphitLemmatizer.class.getResourceAsStream("morph-it_048.txt");
			BufferedReader wordFile = new BufferedReader(new InputStreamReader(stream));

			String line = "START";
			while (line != null) {
				line = wordFile.readLine();
				if (line == null || !line.contains("\t"))
					continue;

				String[] words = line.split("\t");

				lemmas.put(words[0], words[1]);

			}

			return new MorphitLemmatizer(lemmas);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error reading morph it file", e);
		}
	}

}
