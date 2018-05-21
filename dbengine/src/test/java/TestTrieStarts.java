
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestTrieStarts {

	

	@Test
	public void test() {
		Trie trie = new Trie();
		trie.insert("Antonio");
		trie.insert("Anto");
		trie.insert("Antonia");
		trie.insert("ok");
		
		
		start(trie.root, "Ant".toCharArray(), 0);
		//print(trie.root, 0);
		
		//System.out.println(trie.maxLength);
		
		//System.out.println(compare("Antonio".toCharArray(), 4, "Anto".toCharArray(), 4));
		//System.out.println(compare("Antonio".toCharArray(), 4, "Anto".toCharArray(), 4));
		
		//List<TrieNode> nodes = new ArrayList<>();
		//find(trie.root, "Antonio".toCharArray(), 0, new char[trie.maxLength + 1], nodes, new Integer[] { trie.maxLength + 1});
		
		
		//nodes.forEach(n -> {System.out.println(n.word);   });
		
		
		//words.forEach(System.out::println);
	}
	
	
	private void print(TrieNode node) {
		node.children.values().forEach(n -> {
			if(n.word != null) {
				System.out.println(n.word);
				
			} else {
				print(n);
			}
		});
	}
	
	private void start(TrieNode node, char[] word, int index) {
		node.children.values().forEach(n -> {
			if(index < word.length && word[index] == n.letter) {
				start(n, word, index +1);
			
			}
			if(index == word.length - 1) {
				print(n);
			}
			
		});
	}
	

	
	public class Trie {

	    public TrieNode root;
	    public int maxLength = 0;

	    public Trie() {
	        this.root = new TrieNode(' ');
	    }

	    public void insert(String word) {

	        int length = word.length();
	        
	        if(maxLength < length) {
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
	            }
	        }
	    }
	     
	    
	    
	    
	}
	
	
	public class TrieNode {

	    public final int ALPHABET = 26;

	    public char letter;
	    public String word;
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

	public static abstract class AbstractIterator<T> implements Iterator<T> {

	    T next = null;

	    enum State { READY, NOT_READY, DONE, FAILED }

	    State state = State.NOT_READY;

	    @Override
	    public void remove() {
	        throw new UnsupportedOperationException("Iterator.remove() is not supported");
	    }

	    @SuppressWarnings("incomplete-switch")
		@Override
	    public final boolean hasNext() {
	        if (state == State.FAILED) {
	            throw new IllegalStateException("This iterator is in an inconsistent state, and can no longer be used, " +
	                    "due to an exception previously thrown by the computeNext() method");
	        }
	        switch (state) {
	            case DONE:
	                return false;
	            case READY:
	                return true;
	        }
	        return tryToComputeNext();
	    }

	    private final boolean tryToComputeNext() {
	        state = State.FAILED; // temporary pessimism
	        next = computeNext();
	        if (state != State.DONE) {
	            state = State.READY;
	            return true;
	        }
	        return false;
	    }

	    @Override
	    public final T next() {
	        if (!hasNext()) {
	            throw new NoSuchElementException();
	        }
	        state = State.NOT_READY;
	        return next;
	    }

	    protected final T endOfData() {
	        state = State.DONE;
	        return null;
	    }

	   protected abstract T computeNext();
	}
}
