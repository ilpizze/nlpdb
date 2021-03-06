package it.antonio.textprocessing.pos.hmm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.antonio.textprocessing.pos.PosTagger;
import it.antonio.textprocessing.pos.TaggingResult;

/*
 * hmm tagger
 */
public class HMMPosTagger implements PosTagger {
	private static final String INITIAL_TAG = "*";
	  private static final int UNSEEN_SCORE = -100;

	  private Map<String,Map<String,Double>> emissions = new HashMap<>();
	  private Map<String,Map<String,Double>> transitions = new HashMap<>();


	  public void train(List<HMMPosTrainData> data)  {


	    for (HMMPosTrainData trainData : data) {
	      String[] words = trainData.words;
	      String[] tags = trainData.tags;

	      String previousTag = INITIAL_TAG;
	      for (int i = 0; i < tags.length; i++) {
	        String nextWord = words[i];
	        String nextTag = tags[i];

	        if (!transitions.containsKey(previousTag)) {
	          transitions.put(previousTag, new HashMap<String, Double>());
	        }

	        if (!transitions.get(previousTag).containsKey(nextTag)) {
	          transitions.get(previousTag).put(nextTag, 1.0);
	        } else {
	          transitions.get(previousTag).put(nextTag, transitions.get(previousTag).get(nextTag) + 1);
	        }

	        if (!emissions.containsKey(nextTag)) {
	          emissions.put(nextTag, new HashMap<String, Double>());
	        }
	        if (!emissions.get(nextTag).containsKey(nextWord)) {
	          emissions.get(nextTag).put(nextWord, 1.0);
	        } else {
	          emissions.get(nextTag).put(nextWord, emissions.get(nextTag).get(nextWord) + 1);
	        }

	        previousTag = nextTag;
	      }
	    }

	    // Normalize data.
	    for (String tag : emissions.keySet()) {
	      double total = 0;
	      for (String word : emissions.get(tag).keySet())
	        total += emissions.get(tag).get(word);
	      for (String word : emissions.get(tag).keySet())
	        emissions.get(tag).put(word, Math.log(emissions.get(tag).get(word) / total));
	    }
	    for (String tag : transitions.keySet()) {
	      double total = 0;
	      for (String tag2 : transitions.get(tag).keySet())
	        total += transitions.get(tag).get(tag2);
	      for (String tag2 : transitions.get(tag).keySet())
	        transitions.get(tag).put(tag2, Math.log(transitions.get(tag).get(tag2) / total));
	    }
	  }


	  

	  @Override
	  public TaggingResult tag(String[] words) {
	    
	    Map<String, Double> previousScores = new HashMap<String, Double>();
	    ArrayList<Map<String,String>> backtrace = new ArrayList<Map<String,String>>();
	    previousScores.put(INITIAL_TAG, 0D);

	    for (int i = 0; i < words.length; i++) {
	      Map<String, Double> scores = new HashMap<String, Double>();
	      backtrace.add(new HashMap<String,String>());

	      for (String prevState : previousScores.keySet()) {
	        if (!transitions.containsKey(prevState)) {
	          continue;
	        }

	        for (String nextState : transitions.get(prevState).keySet()) {
	          double score = previousScores.get(prevState) + transitions.get(prevState).get(nextState);

	          if (emissions.get(nextState).containsKey(words[i].toLowerCase())) {
	            score += emissions.get(nextState).get(words[i].toLowerCase());
	          }else {
	            score += UNSEEN_SCORE;
	          }

	          if (!scores.containsKey(nextState) || score > scores.get(nextState)) {
	            scores.put(nextState,score);
	            backtrace.get(i).put(nextState, prevState);
	          }
	        }
	      }

	      previousScores = scores;
	    }

	    // Find the best ending state
	    double s = 1;
	    String curr = "";
	    for (String state : previousScores.keySet()) {
	      if (s>0 || previousScores.get(state) > s) {
	        curr = state;
	        s = previousScores.get(curr);
	      }
	    }

	    // backtrace from there, extracting the tags
	    String[] results = new String[words.length];
	    for (int i = words.length - 1; i>=0; i--) {
	      results[i] = curr;
	      curr = backtrace.get(i).get(curr);
	    }
	    
	    String[] simpleResults = simplify(results);
	    return TaggingResult.of(words, results, simpleResults);
	  }

	  
	  
	  private String[] simplify(String[] results) {
		  String[] simples = new String[results.length];
		  for(int i = 0; i < results.length; i++) {
			  simples[i] = results[i] != null ? results[i].substring(0, 1).toUpperCase() : null;
		  }
		  return simples;
	}

}
