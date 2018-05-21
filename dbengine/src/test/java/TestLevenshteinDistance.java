
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

public class TestLevenshteinDistance {

	Map<String, Integer> v1 = new HashMap<>();
	

	@Test
	public void test() {
		
		System.out.println(compare("Antonio", "Antonio"));
		System.out.println(similarity("Antonio", "Antonio") );
		
		System.out.println(compare("Antonio", "b"));
		System.out.println(similarity("Antonio", "b") );
		
		System.out.println(compare("ntoni", "Antonio"));
		System.out.println(similarity("ntoni", "Antonio") );
		
	}
	
	


	public static double similarity(String s1, String s2) {
		double length;
		if (s1.length() < s2.length()) { // longer should always have greater length
			length = s2.length();
		} else {
			length = s1.length();
		}
		
		return (length - compare(s1, s2)) / length;
		
	  }
	
	protected String randomString() {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    return buffer.toString();
	}

	 private static int compare(String left, String right) {
	        if (left == null || right == null) {
	            throw new IllegalArgumentException("Strings must not be null");
	        }

	        /*
	           This implementation use two variable to record the previous cost counts,
	           So this implementation use less memory than previous impl.
	         */

	        int n = left.length(); // length of left
	        int m = right.length(); // length of right

	        if (n == 0) {
	            return m;
	        } else if (m == 0) {
	            return n;
	        }

	        if (n > m) {
	            // swap the input strings to consume less memory
	            final String tmp = left;
	            left = right;
	            right = tmp;
	            n = m;
	            m = right.length();
	        }

	        final int[] p = new int[n + 1];

	        // indexes into strings left and right
	        int i; // iterates through left
	        int j; // iterates through right
	        int upperLeft;
	        int upper;

	        char rightJ; // jth character of right
	        int cost; // cost

	        for (i = 0; i <= n; i++) {
	            p[i] = i;
	        }

	        for (j = 1; j <= m; j++) {
	            upperLeft = p[0];
	            rightJ = right.charAt(j - 1);
	            p[0] = j;

	            for (i = 1; i <= n; i++) {
	                upper = p[i];
	                cost = left.charAt(i - 1) == rightJ ? 0 : 1;
	                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
	                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
	                upperLeft = upper;
	            }
	        }

	        return p[n];
	    }
}
