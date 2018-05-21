package it.antonio.dictionary;

import java.util.List;

public interface Dictionary {
	List<DictionaryWord> find(String word);
}
