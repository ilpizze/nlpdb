
import org.junit.Assert;
import org.junit.Test;

import it.antonio.memorydb.index.common.Trie;

public class TestTrie {

	

	@Test
	public void test() {
		Trie<String> trie = new Trie<>();
		trie.insert("Antonio", "Antonio");
		trie.insert("Anto", "Anto");
		trie.insert("Antonia", "Antonia");
		trie.insert("ok", "ok");
		
		//print(trie.root, 0);
		
		//System.out.println(trie.maxLength);
		
		//System.out.println(compare("Antonio".toCharArray(), 4, "Anto".toCharArray(), 4));
		//System.out.println(compare("Antonio".toCharArray(), 4, "Anto".toCharArray(), 4));
		
		//List<TrieNode> nodes = new ArrayList<>();
		//find(trie.root, "Antonio".toCharArray(), 0, new char[trie.maxLength + 1], nodes, new Integer[] { trie.maxLength + 1});
		
		
		//nodes.forEach(n -> {System.out.println(n.word);   });
		
		//computeMinimumLevenshteinDistance(trie, "Antoni".toCharArray());
		
		//words.forEach(System.out::println);
		
		Assert.assertTrue(trie.findStartsWith("Ant").size() == 3);
		
	}
	
//	
//	
//	private void print(TrieNode node, int indent) {
//		StringBuilder s = new StringBuilder();
//		IntStream.range(0, indent).forEach(i -> s.append("-"));
//		
//		System.out.println(s.toString() + node.letter +  (node.word != null ? ":" +node.word  : ""));
//		
//		node.children.values().forEach(n -> {
//			print(n, indent + 1);
//		});
//	}
//	
//
//	
//	
//	 private static int compare(String left, String right) {
//	        if (left == null || right == null) {
//	            throw new IllegalArgumentException("Strings must not be null");
//	        }
//
//	        /*
//	           This implementation use two variable to record the previous cost counts,
//	           So this implementation use less memory than previous impl.
//	         */
//
//	        int n = left.length(); // length of left
//	        int m = right.length(); // length of right
//
//	        if (n == 0) {
//	            return m;
//	        } else if (m == 0) {
//	            return n;
//	        }
//
//	        if (n > m) {
//	            // swap the input strings to consume less memory
//	            final String tmp = left;
//	            left = right;
//	            right = tmp;
//	            n = m;
//	            m = right.length();
//	        }
//
//	        final int[] p = new int[n + 1];
//
//	        // indexes into strings left and right
//	        int i; // iterates through left
//	        int j; // iterates through right
//	        int upperLeft;
//	        int upper;
//
//	        char rightJ; // jth character of right
//	        int cost; // cost
//
//	        for (i = 0; i <= n; i++) {
//	            p[i] = i;
//	        }
//
//	        for (j = 1; j <= m; j++) {
//	            upperLeft = p[0];
//	            rightJ = right.charAt(j - 1);
//	            p[0] = j;
//
//	            for (i = 1; i <= n; i++) {
//	                upper = p[i];
//	                cost = left.charAt(i - 1) == rightJ ? 0 : 1;
//	                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
//	                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
//	                upperLeft = upper;
//	            }
//	        }
//
//	        return p[n];
//	    }
//	
//	
//	 private static int compare(char[] left, int leftLength, char[] right, int rightLength) {
//	        if (left == null || right == null) {
//	            throw new IllegalArgumentException("Strings must not be null");
//	        }
//
//	        /*
//	           This implementation use two variable to record the previous cost counts,
//	           So this implementation use less memory than previous impl.
//	         */
//
//	        int n = leftLength; // length of left
//	        int m = rightLength; // length of right
//
//	        if (n == 0) {
//	            return m;
//	        } else if (m == 0) {
//	            return n;
//	        }
//
//	        if (n > m) {
//	            // swap the input strings to consume less memory
//	        	final char[] tmp = left;
//	        	final int tmpLength = leftLength;
//	            
//	            left = right;
//	            leftLength = rightLength;
//	            
//	            right = tmp;
//	            rightLength = tmpLength;
//	            
//	            n = m;
//	            m = rightLength;
//	        }
//
//	        final int[] p = new int[n + 1];
//
//	        // indexes into strings left and right
//	        int i; // iterates through left
//	        int j; // iterates through right
//	        int upperLeft;
//	        int upper;
//
//	        char rightJ; // jth character of right
//	        int cost; // cost
//
//	        for (i = 0; i <= n; i++) {
//	            p[i] = i;
//	        }
//
//	        for (j = 1; j <= m; j++) {
//	            upperLeft = p[0];
//	            rightJ = right[j - 1];
//	            p[0] = j;
//
//	            for (i = 1; i <= n; i++) {
//	                upper = p[i];
//	                cost = left[i - 1] == rightJ ? 0 : 1;
//	                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
//	                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
//	                upperLeft = upper;
//	            }
//	        }
//
//	        return p[n];
//	    }
//	 
//	 
//	 int  minLevDist = 0;
//	 
//	 private int computeMinimumLevenshteinDistance(Trie trie, char[] word) {
//
//		    
//		 for (Character c : trie.root.children.keySet()) {
//	            traverseTrie(trie.root.children.get(c), word, 0);
//	        }	   
//		 
//		    
//		    return minLevDist;
//		}
//
//
//		private void traverseTrie(TrieNode node, char[] word, int index) {
//
//				int currentVal = minLevDist + 1;
//				
//				if(index < word.length && word[index] == node.letter ) {
//					currentVal--;
//					minLevDist = currentVal;
//					
//					System.out.println(node.letter);
//					if(node.word != null) {
//						System.out.println(node.word);
//					}
//				}
//				
//				
//				
//				 
//				for (Character c : node.children.keySet()) {
//					traverseTrie(node.children.get(c), word, index+1);
//				}
//				
//			
//			
//		}
}
