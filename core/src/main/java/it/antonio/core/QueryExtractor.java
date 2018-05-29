package it.antonio.core;

import it.antonio.learning.rf.RandomForest;
import it.antonio.textprocessing.pipeline.TextModel;
import it.antonio.textprocessing.pipeline.TextPipeline;

public class QueryExtractor {
	
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

}
