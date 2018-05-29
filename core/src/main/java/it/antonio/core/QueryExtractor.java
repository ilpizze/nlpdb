package it.antonio.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

import org.junit.Test;

import it.antonio.learning.data.SimpleData;
import it.antonio.learning.data.SimpleDataSet;
import it.antonio.learning.dt.DecisionTreeResult;
import it.antonio.learning.dt.Feature;
import it.antonio.learning.rf.RandomForest;
import it.antonio.learning.rf.RandomForestTrainer;
import it.antonio.textprocessing.lemma.Lemmatizer;
import it.antonio.textprocessing.lemma.morphit.MorphitLemmatizer;
import it.antonio.textprocessing.pipeline.TextModel;
import it.antonio.textprocessing.pipeline.TextPipeline;
import it.antonio.textprocessing.pipeline.simple.SimpleTextPipeline;
import it.antonio.textprocessing.pos.hmm.HMMPosTagger;
import it.antonio.textprocessing.pos.hmm.HMMPosTrainData;
import it.antonio.textprocessing.tokenizer.Tokenizer;

public class QueryExtractor {
	
	@Test
	public void test() throws IOException, URISyntaxException {
		
		
		HMMPosTagger posTagger = new HMMPosTagger();
		List<HMMPosTrainData> trainData = HMMPosTrainData.parse(HMMPosTrainData.class.getResourceAsStream("it-train.pos"));
		posTagger.train(trainData);
		
		Lemmatizer lemmatizer = MorphitLemmatizer.create();
		
		TextPipeline pipeline = new SimpleTextPipeline(new Tokenizer(), posTagger , lemmatizer );
		
		
		URL trainFile = this.getClass().getResource("train.dat");
		List<String> lines = Files.readAllLines(Paths.get(trainFile.toURI()));
		
		SimpleDataSet dataSet = new SimpleDataSet("model", "result");
		lines.forEach(line -> {
			String[] split = line.split("\\|");
			
			TextModel model = pipeline.createModel(split[0]);
			dataSet.add( model, split[1]);
			
			//System.out.println(Arrays.toString(model.tokens));
			//System.out.println(Arrays.toString(model.posTags));
			//System.out.println(Arrays.toString(model.lemmas));
			
		});
		
		Feature classification = Feature.withName("result", d -> d.getValue("result"));
		
		
		List<Feature> features = new LinkedList<>();
		features.add(feature("first word", m -> m.lemmas[0]));
		features.add(feature("second word", m -> m.lemmas[1]));
		features.add(feature("third word", m -> m.lemmas[2]));
		
		features.add(feature("lemmas", m -> Arrays.asList(m.lemmas)));
		
		features.add(feature("posTags", m -> Arrays.asList(m.posTags)));
		features.add(feature("simplePosTags", m -> Arrays.asList(m.posTags).stream().filter(s-> {return "SVBN".contains(s);} )  ));
		
		features.add(feature("third word", m -> m.tokens[2]));
		
		features.add(feature("SV skipgram index", m ->  {
			List<Integer> indexes = new ArrayList<>();
			for(int i = 0; i < m.posTags.length; i++) {
				
				if("SV".contains(m.posTags[i])) {
					indexes.add(i);
				}
			}
			return indexes;
		}));
		
		
		RandomForest rf = new RandomForestTrainer().train(dataSet, features, classification, 100, 0.4);
		
		Scanner sc = new Scanner(System.in);
		System.out.println("inserisci la query");
        while(sc.hasNextLine()) {
        	String query =  sc.nextLine();
        	System.out.println(query);
        	long time = System.currentTimeMillis();
        	TextModel model = pipeline.createModel(query);
        	DecisionTreeResult result = rf.classify(SimpleDataSet.fromHeaders(dataSet, model));
        	System.out.println(System.currentTimeMillis() - time);
        	System.out.println(result.value + " " + result.percentage);
        }
		
	}
	
	private Feature feature(String name, Function<TextModel, Object> f) {
		return Feature.withName(name, d -> {
			return f.apply(d.getValue("model"));	
		});
	}
	
	
	/*
	private TextPipeline textPipeline;
	private RandomForest queryTypeClassifier;
	
	public QueryExtractor(TextPipeline textPipeline, RandomForest queryTypeClassifier) {
		super();
		this.textPipeline = textPipeline;
		this.queryTypeClassifier = queryTypeClassifier;
	}

	public void extract(String query) {
		TextModel model = textPipeline.createModel(query);
	}
	
	*/

}
