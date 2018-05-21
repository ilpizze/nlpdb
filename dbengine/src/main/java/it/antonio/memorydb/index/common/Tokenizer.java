package it.antonio.memorydb.index.common;

import java.util.function.Consumer;

public interface Tokenizer {
	void onToken(String text, Consumer<String> cs);
}
