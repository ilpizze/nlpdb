package it.antonio.memorydb.index.common;

import java.util.function.Consumer;

public class SimpleTokenizer implements Tokenizer {

	@Override
	public void onToken(String text, Consumer<String> cs) {
		int i = 0;
		String t = "";
		char c;
		while(i < text.length()) {
			c = text.charAt(i);
			//if(Character.isWhitespace(c)) {
			if(!Character.isLetterOrDigit(c)) {
				if(!t.isEmpty() && t.length() > 3) {
					cs.accept(t);	
				}
				t = "";
			} else {
				t += Character.toLowerCase(c);
			}
			i++;
			
		}
		if(!t.isEmpty()&& t.length() > 3) {
			cs.accept(t);	
		}
	}

}
