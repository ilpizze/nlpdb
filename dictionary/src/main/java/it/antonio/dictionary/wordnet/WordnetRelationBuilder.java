package it.antonio.dictionary.wordnet;

import it.antonio.dictionary.RelationType;

public class WordnetRelationBuilder {
	public static RelationType decode(String relation) {
		switch(relation) {
			case "also": return RelationType.ALSO_SEE; 
			case "glos": return RelationType.WORD_DEFINITION;
			case "syns": return RelationType.SYNSET_WORDS;
			case "hype": return RelationType.HYPERNYMS;
			case "inst": return RelationType.INSTANCE_OF;
			case "hypes": return RelationType.HYPERNYMS_INSTANCE_OF; //"INSTANCE_OF"
			case "hypo": return RelationType.HYPONYMS;
			case "hasi": return RelationType.HAS_INSTANCE;
			case "hypos": return RelationType.HYPONUMS_HAS_INSTANCE; //"HAS_INSTANCE"
			case "mmem": return RelationType.MEMBER_MERONYMS;
			case "msub": return RelationType.SUBSTANCE_MERONYMS;
			case "mprt": return RelationType.PART_MERONYMS;
			case "mero": return RelationType.ALL_MERONYMS;
			case "hmem": return RelationType.MEMBER_HOLONYMS;
			case "hsub": return RelationType.SUBSTANCE_HOLONYMS;
			case "hprt": return RelationType.PART_HOLONYMS;
			case "holo": return RelationType.ALL_HOLONYMS;
			case "attr": return RelationType.ATTRIBUTES;
			case "ants": return RelationType.ANTONYMS;
			case "self": return RelationType.SELFS;
			case "sim": return RelationType.SIMILAR_TO; //(ADJECTIVES_ONLY)
			case "enta": return RelationType.ENTAILMENT; //(VERBS_ONLY)
			case "caus": return RelationType.CAUSE;
			case "domn": return RelationType.DOMAIN_ALL;
			case "dmnc": return RelationType.DOMAIN_CATEGORY;
			case "dmnu": return RelationType.DOMAIN_USAGE;
			case "dmnr": return RelationType.DOMAIN_REGION;
			case "domt": return RelationType.MEMBER_OF_DOMAIN_ALL; //(NOUNS_ONLY)
			case "dmtc": return RelationType.MEMBER_OF_DOMAIN_CATEGORY; //(NOUNS_ONLY)
			case "dmtu": return RelationType.MEMBER_OF_DOMAIN_USAGE; //(NOUNS_ONLY)
			case "dmtr": return RelationType.MEMBER_OF_DOMAIN_REGION;
			default: throw new IllegalArgumentException(relation + " relation non found");
		
		}	
		
	}
}
