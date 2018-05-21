import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Records;
import it.antonio.memorydb.index.HashIndex;
import it.antonio.memorydb.lock.NonLockingLockManager;
import it.antonio.memorydb.query.Eq;

public class TestNLP {

	//@Test
	public void test2() throws SAXException, IOException, ParserConfigurationException {
		File file = new File("/home/antonio/Scaricati/ita+xml/iwn/wn-ita-lmf.xml");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		
		SAXParser parser = factory.newSAXParser();
		
		parser.parse(file, new DefaultHandler() {
			
			long time; 
			@Override
			public void startDocument() throws SAXException {
				System.out.println("start");
				time = System.currentTimeMillis();
			}
			@Override
			public void endDocument() throws SAXException {
				System.out.println("end");
				System.out.println(System.currentTimeMillis() - time);
			}
			
		});
	}
	
	
	@Test
	public void test() throws IOException {
		File file = new File("/home/antonio/Scaricati/ita+xml/iwn/wn-data-ita.tab");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		StringBuilder bld = new StringBuilder();
		String line;
		
		Attribute<Word> idAttr = (w) -> Collections.singleton(w.id);
		
		Records<Word> c = new Records<>(new NonLockingLockManager());
		c.addIndex(new HashIndex<Word>(idAttr));
		
		int j = 0;
		while ((line = reader.readLine()) != null) {
	        if(line.charAt(0) == '#') continue; // comment
	        String id = consume(bld, line, 0, 8); 
	        String type = consume(bld, line, 9, 10); 
	        String language = consume(bld, line, 11, 13); 
	        String defType = consume(bld, line, 15, 20); 
	        String wordValue = consume(bld, line, 21, line.length()); 
		    
	        
	        Optional<Word> word = c.find(new Eq<Word>(idAttr, id)).stream().findFirst();
	        if(word.isPresent()) {
	        	if("lemma".equals(defType)) {
	        		word.get().lemmas.add(wordValue);	
	        	} else if(defType.startsWith("def")) {
	        		word.get().definitions.add(wordValue);
	        	} else {
	        		throw new IllegalArgumentException("defType not supported: " + defType);
	        	}
	        	
	        } else {
	        	Word newWord = new Word(id, type, language);
	        	if("lemma".equals(defType)) {
	        		newWord.lemmas.add(wordValue);	
	        	} else if(defType.startsWith("def")) {
	        		newWord.definitions.add(wordValue);
	        	} else {
	        		throw new IllegalArgumentException("defType not supported: " + defType);
	        	}
	        	c.insert(newWord);
	        }
	        
	        
	        
	        /*
	        String line1 = "";
	        String line2 = "";
	        for(int i = 0; i < line.length(); i ++) {
	        	line1 += (line.charAt(i) == '\t' ? "   " : line.charAt(i) + "  ");
	        	line2 += (i < 10 ? (i + "  ") : (i + " "));
	        }
	        System.out.println(line);
	        System.out.println(line1);
	        System.out.println(line2);
	        */
	        //if(j == 35) {
	        	//return;
	        //}
	        j++;
	        //return;
	    }
	    
	    reader.close();
	    long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	    System.out.printf("%.3fMB", usedMemory/ (1024.0 * 1024.0 ));
	        
	}
	
	
	private String consume(StringBuilder bld, String line, int from, int to) {
		bld.setLength(0);
		bld.append(line.substring(from, to));
		return bld.toString();
	}
	
	
	private static class Word {
		String id;
		String type;
		String language;
		List<String> lemmas = new LinkedList<>();
		List<String> definitions = new LinkedList<>();
		List<Word> sysnonims = new LinkedList<>();
		
		public Word(String id, String type, String language) {
			super();
			this.id = id;
			this.type = type;
			this.language = language;
		}
	
		
		
	}
	
}
