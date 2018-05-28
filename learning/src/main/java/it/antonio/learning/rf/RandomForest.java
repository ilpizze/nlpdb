package it.antonio.learning.rf;

import java.util.LinkedList;
import java.util.List;

import it.antonio.learning.data.Data;
import it.antonio.learning.dt.DecisionTree;
import it.antonio.learning.dt.DecisionTreeResult;

public class RandomForest {
	private List<DecisionTree> trees = new LinkedList<>();

	public RandomForest(List<DecisionTree> trees) {
		super();
		this.trees = trees;
	}
	
	public DecisionTreeResult classify(Data data) {
		DecisionTreeResult lastRes = null;
		
		for(DecisionTree tree: trees) {
			DecisionTreeResult res = tree.classify(data);
			
			if(lastRes == null || res.percentage > lastRes.percentage) {
				lastRes = res;
			}
		}
		
		
		return lastRes;
	}
	
}
