package it.antonio.nlp.pos.lemma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Lemmas {
	
	public static void main(String...args) throws IOException {
		Map<String, String> lemmas = new HashMap<>();
		
		
		 
		 BufferedReader wordFile = new BufferedReader(new InputStreamReader(Lemmas.class.getResourceAsStream("morph-it_048.txt")));
		    

		   
	    String line = "START";
	    while (line != null) {
	    	line = wordFile.readLine();
	    	if(line ==null || !line.contains("\t") ) continue;
	    	
	    	String[] words = line.split("\t");
	    	
	    	lemmas.put(words[0], words[1]);
	    	
	    }
	    
	      System.gc();
		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		  System.out.printf("%.3fMB\n", usedMemory/ (1024.0 * 1024.0 ));
		  
	    System.out.println(lemmas.get("guardami"));
	    
	}
	
	
}
