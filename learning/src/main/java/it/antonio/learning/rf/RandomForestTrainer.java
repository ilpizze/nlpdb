package it.antonio.learning.rf;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.antonio.learning.data.DataSet;
import it.antonio.learning.dt.DecisionTree;
import it.antonio.learning.dt.DecisionTreeTrainer;
import it.antonio.learning.dt.Feature;

public class RandomForestTrainer {
	
	private DecisionTreeTrainer treeTrainer = new DecisionTreeTrainer();
	
	public RandomForest train(DataSet data, List<Feature> features, Feature classificationFeature, int size, double maxFeatures) {
		List<DecisionTree> trees = new LinkedList<>();
		
		
		Predicate<? super Feature> filter = f -> Math.random() > maxFeatures;
		
		for(int i = 0; i < size; i++) {
			List<Feature> subFeatures = features.stream().filter(filter).collect(Collectors.toList());
			DecisionTree tree = treeTrainer.train(data, subFeatures, classificationFeature);
			trees.add(tree);
			
		}
		return new RandomForest(trees);
	}
	
}
