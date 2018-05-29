package it.antonio.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import it.antonio.learning.data.SimpleDataSet;
import it.antonio.learning.rf.RandomForest;
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
		
		lines.forEach(line -> {
			String[] split = line.split("\\|");
			TextModel model = pipeline.createModel(split[0]);
			
			System.out.println(Arrays.toString(model.tokens));
			System.out.println(Arrays.toString(model.posTags));
			//System.out.println(Arrays.toString(model.lemmas));
			
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
