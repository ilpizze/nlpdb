package it.antonio.textprocessing.pos.hmm;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import it.antonio.textprocessing.pos.TaggingResult;

public class HMMPosTaggerTest {

	@Test
	public void testHMMPosTagger() {
		HMMPosTagger tagger2 = new HMMPosTagger();

		List<HMMPosTrainData> trainData2 = HMMPosTrainData
				.parse(HMMPosTrainData.class.getResourceAsStream("it-train.pos"));
		tagger2.train(trainData2);
		TaggingResult tag2 = tagger2.tag("la mail non è stata mangiata da leasing fpa".toLowerCase().split(" "));

		// System.gc();
		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.printf("%.3fMB\n", usedMemory / (1024.0 * 1024.0));

		System.out.println(Arrays.toString(tag2.words));
		System.out.println(Arrays.toString(tag2.posTagsComplex));
	}

}
