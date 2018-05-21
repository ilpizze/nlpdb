package it.antonio.memorydb.index.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Trie<T> {

	public TrieNode root;
	public int maxLength = 0;

	public Trie() {
		this.root = new TrieNode(' ');
	}

	public void insert(String word, T value) {
		
		if(word == null)  {
			return;
		}
		
		int length = word.length();

		if (maxLength < length) {
			maxLength = length;
		}

		TrieNode current = this.root;

		if (length == 0) {
			current.word = word;
		}
		for (int index = 0; index < length; index++) {

			char letter = word.charAt(index);
			TrieNode child = current.getChild(letter);

			if (child != null) {
				current = child;
			} else {
				current.children.put(letter, new TrieNode(letter));
				current = current.getChild(letter);
			}
			if (index == length - 1) {
				current.word = word;
				current.values.add(value);
			}
		}
	}

	
	public List<T> findStartsWith(String word) {
		TrieNode current = this.root;

		for (int index = 0; index < word.length(); index++) {
			char letter = word.charAt(index);
			
			TrieNode child = current.getChild(letter);

			if (child != null) {
				current = child;
			} else {
				return Collections.emptyList();
			}
			
		}
		
		List<T> values = new ArrayList<>();
		onLeaves(current, node -> {
			values.addAll(node.values);
		});
		return values;

	}

	
	
	
	private void onLeaves(TrieNode current, Consumer<TrieNode> c) {
		if(current.word != null) {
			c.accept(current);
		} 
		for(TrieNode child: current.children.values()) {
			onLeaves(child, c);
		}
	}




	private class TrieNode {

		public final int ALPHABET = 26;

		@SuppressWarnings("unused")
		public char letter;
		public String word;
		public List<T> values = new LinkedList<>();
		public Map<Character, TrieNode> children;

		public TrieNode(char letter) {
			this.letter = letter;
			children = new HashMap<Character, TrieNode>(ALPHABET);
		}

		public TrieNode getChild(char letter) {

			if (children != null) {
				if (children.containsKey(letter)) {
					return children.get(letter);
				}
			}
			return null;
		}
	}
}
