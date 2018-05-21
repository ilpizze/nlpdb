package it.antonio.nlp.pos;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/*
 * hmm tagger
 */

public class POSTagger {
  private static final String TRAINING_WORDS_PATH = "src/main/resources/brown-words.txt";
  private static final String TRAINING_TAGS_PATH = "src/main/resources/brown-tags.txt";
  private static final int MAX_LINES = 10000;

  private static final String INITIAL_TAG = "*";
  private static final int UNSEEN_SCORE = -100;

  private Map<String,Map<String,Double>> emissions = new HashMap<>();
  private Map<String,Map<String,Double>> transitions = new HashMap<>();

  public POSTagger () throws IOException {
    
  }

  public void train(List<PosTrainData> data) throws IOException {


    for (PosTrainData trainData : data) {
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

  public List<PosTrainData> parseTrainingData() throws IOException {
	  List<PosTrainData> trainingData = new LinkedList<>();

    BufferedReader wordsIn = new BufferedReader(new FileReader(TRAINING_WORDS_PATH));
    BufferedReader tagsIn = new BufferedReader(new FileReader(TRAINING_TAGS_PATH));

    String wordLine, tagLine;
    int n = 0;
    while (n < MAX_LINES) {
      wordLine = wordsIn.readLine();
      tagLine = tagsIn.readLine();

      if (wordLine == null || tagLine == null) {
        break;
      }

      String[] words = wordLine.toLowerCase().split(" ");
      String[] tags = tagLine.split(" ");

      if (words.length != tags.length) {
        continue;
      }

      trainingData.add(new PosTrainData(words, tags));
      n++;
    }

    wordsIn.close();
    tagsIn.close();

    return trainingData;
  }
  
  public List<PosTrainData> parseItalianTrainingData() throws IOException {
	  List<PosTrainData> trainingData = new LinkedList<>();

    BufferedReader wordFile = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("it-train.pos")));
    

   
    String line = "START";
    while (line != null) {
    	line = wordFile.readLine();
    	if(line ==null || !line.contains("_") ) continue;
    	
    	String[] wordsAndTags = line.split(" ");
    	
    	String[] words = new String[wordsAndTags.length];
    	String[] tags = new String[wordsAndTags.length];
        
    	for(int i = 0; i < wordsAndTags.length; i++) {
    		String[] wordAndTag = wordsAndTags[i].split("_");
    		words[i] = wordAndTag[0].toLowerCase();
    		tags[i] = wordAndTag[1];
    		
    	}
    	
    	trainingData.add(new PosTrainData(words, tags));
    	
    }
    
    return trainingData;
    
  }

  public TaggingResult tag(String input) {
    String[] words = sanitizeInputs(input); // Need to add spaces before each comma, period question mark, etc.

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

          if (emissions.get(nextState).containsKey(words[i])) {
            score += emissions.get(nextState).get(words[i]);
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

    return TaggingResult.of(words, results);
  }

  private static String[] sanitizeInputs(String input) {
    String[] punctuations = { "\\.", "\\?", "\\!", "\\,", "\\;", "\\:", "\\\"", "\\'", "\\(", "\\)" };

    for (String punctuation : punctuations) {
      input = input.replaceAll(punctuation, " " + punctuation + " ");
    }

    List<String> results = new ArrayList<String>(Arrays.asList(input.split(" ")));
    results.removeAll(Arrays.asList("", null));

    return results.toArray(new String[results.size()]);
  }
  
  
  public static void main(String...args) throws IOException, InterruptedException {
	  /*POSTagger tagger = new POSTagger();
	  
	  List<PosTrainData> trainData = tagger.parseTrainingData();
	  tagger.train(trainData);
	  TaggingResult tag = tagger.tag("he is smoking");
	  
	  System.out.println(Arrays.toString(tag.words));
	  System.out.println(Arrays.toString(tag.results));*/
	  
  POSTagger tagger2 = new POSTagger();
	  
	  List<PosTrainData> trainData2 = tagger2.parseItalianTrainingData();
	  tagger2.train(trainData2);
	  TaggingResult tag2 = tagger2.tag("la mail non è stata mangiata da leasing fpa".toLowerCase());
	  
	  System.gc();
	  long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	  System.out.printf("%.3fMB\n", usedMemory/ (1024.0 * 1024.0 ));
	  
	  System.out.println(Arrays.toString(tag2.words));
	  System.out.println(Arrays.toString(tag2.results));
	  
	  
  }
}