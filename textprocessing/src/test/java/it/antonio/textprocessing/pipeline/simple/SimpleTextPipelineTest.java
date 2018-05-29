package it.antonio.textprocessing.pipeline.simple;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import it.antonio.textprocessing.lemma.Lemmatizer;
import it.antonio.textprocessing.lemma.morphit.MorphitLemmatizer;
import it.antonio.textprocessing.pipeline.TextModel;
import it.antonio.textprocessing.pipeline.TextPipeline;
import it.antonio.textprocessing.pos.hmm.HMMPosTagger;
import it.antonio.textprocessing.pos.hmm.HMMPosTrainData;
import it.antonio.textprocessing.tokenizer.Tokenizer;

public class SimpleTextPipelineTest {

	@Test
	public void test() {
		
		HMMPosTagger posTagger = new HMMPosTagger();
		List<HMMPosTrainData> trainData = HMMPosTrainData.parse(HMMPosTrainData.class.getResourceAsStream("it-train.pos"));
		posTagger.train(trainData);
		
		Lemmatizer lemmatizer = MorphitLemmatizer.create();
		
		TextPipeline pipeline = new SimpleTextPipeline(new Tokenizer(), posTagger , lemmatizer );
		

		TextModel model = pipeline.createModel("La mail non è stata mangiata da leasing S.P.A");
		
		System.out.println(Arrays.toString(model.tokens));
		System.out.println(Arrays.toString(model.posTags));
		System.out.println(Arrays.toString(model.lemmas));
	}

}
