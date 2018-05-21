package it.antonio.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class WordnetReader_NEW {
			
	
	public static void main(String...args) throws FileNotFoundException, XMLStreamException {
		File file = new File("/home/antonio/Scaricati/ita+xml/iwn/wn-ita-lmf.xml");
		InputStream in = new FileInputStream(file);
        	
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
        
        List<Word> words = new ArrayList<>();
        
        String currentLemma = null;
		String parthOfSpeech = null;
		
		Map<String, List<Word>> wordsbySynsetId = new HashMap<>();
		
		String currentSysnetId = null;
		
		List<Word> selecteds = new ArrayList<>();
		
		String key = "casa";
		
		while (reader.hasNext()) {
        	reader.next();
        	if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {
        		if("Lemma".equals(reader.getLocalName())){
        			if("writtenForm".equals(reader.getAttributeLocalName(0)) ) {
            			currentLemma = reader.getAttributeValue(0);	
            		}
            		if("partOfSpeech".equals(reader.getAttributeLocalName(1)) ) {
            			parthOfSpeech = reader.getAttributeValue(1);	
            		}
        			if(currentLemma == null || parthOfSpeech == null) {
        				throw new IllegalStateException("lemma or part of speech null");
        			}
            	}
        		
        		
        		if("Sense".equals(reader.getLocalName())){
        			Word word = new Word(currentLemma, parthOfSpeech);
        			words.add(word);
        			
        			if(word.lemma.equals(key)) {
        				selecteds.add( word);
        			}
        			
        			String currentSysnsetId = null;
        			
        			if("synset".equals(reader.getAttributeLocalName(1)) ) {
        				currentSysnsetId = reader.getAttributeValue(1);	
            		}
        			if(currentSysnsetId == null ) {
        				throw new IllegalStateException("synset id null");
        			}
        			
        			if(wordsbySynsetId.containsKey(currentSysnsetId)) {
        				wordsbySynsetId.get(currentSysnsetId).add( word);
    				} else {
    					List<Word> synsets= new LinkedList<>();
    					synsets.add(word);
    					wordsbySynsetId.put(currentSysnsetId, synsets);	
    				}
        			
        			
            	}
        		
        		if("Synset".equals(reader.getLocalName())){
        			currentSysnetId = reader.getAttributeValue(0);
        			if(currentSysnetId == null) {
        				throw new IllegalStateException("Synset id null");
        			}
        			
        		}
            		
        		if("SynsetRelation".equals(reader.getLocalName())){
        			String targets = null, relType =null;
        			if("targets".equals(reader.getAttributeLocalName(0)) ) {
        				targets = reader.getAttributeValue(0);	
            		}
            		if("relType".equals(reader.getAttributeLocalName(1)) ) {
            			relType = reader.getAttributeValue(1);	
            		}
        			
        			if(targets == null && relType == null) {
        				throw new IllegalStateException("targets or relType null");
        				
        			}
        			
        			List<Word> currentWords = wordsbySynsetId.get(currentSysnetId);
        			List<Word> relationWords = wordsbySynsetId.get(targets);
                	
        			RelationType type = RelationType.valueOf(relType);
        			if(currentWords != null && relationWords != null) {
        				for(Word word: currentWords) {
            				for(Word relationWord: relationWords) {
            					word.relations.add(new Relation(type, relationWord));
            				}
            			}
            				
        			}
        			
        			
        		}
            	
        		
        		
            }
        	
        	if (reader.getEventType() == XMLStreamReader.END_ELEMENT) {
        		
        		if("Synset".equals(reader.getLocalName())){
            		//synsetId = null;
            	}
        		
            	if("Lexicon".equals(reader.getLocalName())){
            		
            		System.out.println("---------------");
            		for(Word selected: selecteds) {
            			System.out.println(selected.lemma);
                		selected.relations.forEach(r -> System.out.println(" " + r.type + "-" + r.word.lemma ));
                		System.out.println("---------------");	
            		}
            		
            	  System.gc();
            		long	usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            			  System.out.printf("%.3fMB\n", usedMemory/ (1024.0 * 1024.0 ));
            			
            		
            		return;
            	}
            }
		}
        
      
        
    }

	
	private static class Word {
		String lemma;
		String partOfSpeech;
		List<Relation> relations = new LinkedList<>();
		
		public Word(String lemma, String partOfSpeech) {
			super();
			this.lemma = lemma;
			this.partOfSpeech = partOfSpeech;
		}
		
		
	
	}
	
	public static class Relation {
		RelationType type;
		Word word;
		public Relation(RelationType type, Word word) {
			super();
			this.type = type;
			this.word = word;
		}
		
		
	}
	
	public static enum RelationType{
		mmem,
		hype,
		hmem,
		dmnc,
		hasi,
		ants,
		sim,
		dmnu,
		self,
		hprt,
		hypo,
		dmnr,
		hsub,
		inst,
		dmtc,
		attr,
		also,
		mprt,
		enta,
		msub,
		dmtr,
		caus,
		dmtu
	}
	
}

