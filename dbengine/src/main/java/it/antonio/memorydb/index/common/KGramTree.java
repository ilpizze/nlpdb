package it.antonio.memorydb.index.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class KGramTree<T> {

		private Map<String, LinkedList<KGramNode<T>>> map = new HashMap<>();

		public void add(String word, T value) {

			if (word.length() > 1) {

				String kGramString = '$' + word + '$';

				for (int i = 0; i < word.length(); i++) {

					String oneGram = word.charAt(i) + "";
					addKGram(oneGram, word, value);

					String twoGram = kGramString.substring(i, i + 2);
					addKGram(twoGram, word, value);

					String threeGram = kGramString.substring(i, i + 3);
					addKGram(threeGram, word,  value);
				}

				addKGram(kGramString.substring(kGramString.length() - 2), word, value);
			} else if (word.length() == 1) {
				if (!map.containsKey(word)) {
					addKGram(word, word, value);
					addKGram('$' + word, word, value);
					addKGram(word + '$', word, value);
				}
			}
		}

		private void addKGram(String key, String word, T value) {
			if (map.containsKey(key)) {
				map.get(key).add(new KGramNode<T>(word, value));
			} else {
				LinkedList<KGramNode<T>> list = new LinkedList<>();
				list.add(new KGramNode<T>(word, value));
				map.put(key, list);
			}
		}

		public Set<T> find(String word) {

			String wordRegex = word.replace("*", ".*");

			if (word.charAt(0) != '*') {
				word = '$' + word;
			}
			if (word.charAt(word.length() - 1) != '*') {
				word = word + '$';
			}

			String[] sequences = word.split("\\*");
			Set<T> results = new HashSet<T>();
			for (String sequence : sequences) {

				if (sequence.length() > 3) {
					for (int i = 0; i < sequence.length() - 3; i++) {
						String substr = sequence.substring(i, i + 3);
						LinkedList<KGramNode<T>> candidates = map.get(substr);
						if(candidates != null) {
							for (KGramNode<T> candidate : candidates) {
								// do post filter
								if (candidate.word.matches(wordRegex))
									results.add(candidate.value);
							}	
						}
						
					}

				} else if (sequence.length() > 0) {
					LinkedList<KGramNode<T>> candidates = map.get(sequence);
					if(candidates != null) {
						for (KGramNode<T> candidate : candidates) {
							// do post filter
							if (candidate.word.matches(wordRegex))
								results.add(candidate.value);
						}
					}
					
				}

			}
			return results;

		}
		
		
		private static class KGramNode<T> {
			private String word;
			private T value;
			
			public KGramNode(String word, T value) {
				super();
				this.word = word;
				this.value = value;
			}
			
			
		}
	}