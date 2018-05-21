package it.antonio.dictionary.wordnet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import it.antonio.dictionary.Dictionary;
import it.antonio.dictionary.DictionaryWord;
import it.antonio.dictionary.Relation;
import it.antonio.dictionary.RelationType;

public class WordNetDictionary implements Dictionary {

	private final Map<String, List<DictionaryWord>> words;

	public WordNetDictionary(Map<String, List<DictionaryWord>> words) {
		super();
		this.words = words;
	}

	@Override
	public List<DictionaryWord> find(String word) {
		return words.get(word);
	}

	public static WordNetDictionary createFrom(InputStream stream) {
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = inputFactory.createXMLStreamReader(stream);

			Map<String, List<DictionaryWord>> words = new HashMap<>();

			String currentLemma = null;
			String parthOfSpeech = null;

			Map<String, List<DictionaryWord>> wordsbySynsetId = new HashMap<>();

			String currentSysnetId = null;

			while (reader.hasNext()) {
				reader.next();
				if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
					if ("Lemma".equals(reader.getLocalName())) {
						if ("writtenForm".equals(reader.getAttributeLocalName(0))) {
							currentLemma = reader.getAttributeValue(0);
						}
						if ("partOfSpeech".equals(reader.getAttributeLocalName(1))) {
							parthOfSpeech = reader.getAttributeValue(1);
						}
						if (currentLemma == null || parthOfSpeech == null) {
							throw new IllegalStateException("lemma or part of speech null");
						}
					}

					if ("Sense".equals(reader.getLocalName())) {
						DictionaryWord word = new DictionaryWord(currentLemma, parthOfSpeech);

						if (words.containsKey(currentLemma)) {
							words.get(currentLemma).add(word);
						} else {
							List<DictionaryWord> wordList = new LinkedList<>();
							wordList.add(word);
							words.put(currentLemma, wordList);
						}

						String currentSysnsetId = null;

						if ("synset".equals(reader.getAttributeLocalName(1))) {
							currentSysnsetId = reader.getAttributeValue(1);
						}
						if (currentSysnsetId == null) {
							throw new IllegalStateException("synset id null");
						}

						if (wordsbySynsetId.containsKey(currentSysnsetId)) {
							wordsbySynsetId.get(currentSysnsetId).add(word);
						} else {
							List<DictionaryWord> synsets = new LinkedList<>();
							synsets.add(word);
							wordsbySynsetId.put(currentSysnsetId, synsets);
						}

					}

					if ("Synset".equals(reader.getLocalName())) {
						currentSysnetId = reader.getAttributeValue(0);
						if (currentSysnetId == null) {
							throw new IllegalStateException("Synset id null");
						}

					}

					if ("SynsetRelation".equals(reader.getLocalName())) {
						String targets = null, relType = null;
						if ("targets".equals(reader.getAttributeLocalName(0))) {
							targets = reader.getAttributeValue(0);
						}
						if ("relType".equals(reader.getAttributeLocalName(1))) {
							relType = reader.getAttributeValue(1);
						}

						if (targets == null && relType == null) {
							throw new IllegalStateException("targets or relType null");

						}

						List<DictionaryWord> currentWords = wordsbySynsetId.get(currentSysnetId);
						List<DictionaryWord> relationWords = wordsbySynsetId.get(targets);

						RelationType type = WordnetRelationBuilder.decode(relType);
						if (currentWords != null && relationWords != null) {
							for (DictionaryWord word : currentWords) {
								for (DictionaryWord relationWord : relationWords) {
									word.getRelations().add(new Relation(type, relationWord));
								}
							}

						}

					}

				}

				if (reader.getEventType() == XMLStreamReader.END_ELEMENT) {

					//if ("Synset".equals(reader.getLocalName())) {
						// synsetId = null;
					//}

					if ("Lexicon".equals(reader.getLocalName())) {

						// System.gc();
						// long usedMemory = Runtime.getRuntime().totalMemory() -
						// Runtime.getRuntime().freeMemory();
						// System.out.printf("%.3fMB\n", usedMemory/ (1024.0 * 1024.0 ));

						return new WordNetDictionary(words);
					}
				}
			}
			throw new IllegalStateException("No end lexicon");
		} catch (XMLStreamException xmle) {
			throw new IllegalStateException(xmle);
		}
		

	}

}
