package it.antonio.textprocessing.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokenizer {
	public String[] tokenize(String input) {

		List<String> results = new ArrayList<String>(Arrays.asList(input.split(" ")));
		results.removeAll(Arrays.asList("", null));

		return results.toArray(new String[results.size()]);
	}

}
