package it.antonio.learning.rf;

import it.antonio.learning.data.Data;
import it.antonio.learning.dt.DecisionTree;
import it.antonio.learning.dt.DecisionTreeResult;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class RandomForest {
	private List<DecisionTree> trees = new LinkedList<>();

	public RandomForest(List<DecisionTree> trees) {
		super();
		this.trees = trees;
	}
	
	public DecisionTreeResult classify(Data data) {
		TreeSet<DecisionTreeResult> set = new TreeSet<>((r1, r2) -> r1.percentage.compareTo(r2.percentage));
		
		if(set.size() != trees.size()) {
			throw new IllegalStateException();
		}
		
		return set.first();
	}
	
}
