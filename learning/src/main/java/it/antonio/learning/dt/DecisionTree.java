package it.antonio.learning.dt;

import it.antonio.learning.data.Data;
import it.antonio.learning.dt.DecisionTreeNode.FeatureDecisionTreeNode;
import it.antonio.learning.dt.DecisionTreeNode.LeafValueDecisionTreeNode;

public class DecisionTree {
	private DecisionTreeNode root;

	public DecisionTree(DecisionTreeNode root) {
		super();
		this.root = root;
	}
	
	public void accept(DecisionTreeVisitor v) {
		root.accept(v);
	}
	
	
	public DecisionTreeResult classify(Data data) {
		return root.classify(data);
	}
	
	public static interface DecisionTreeVisitor{
		void visit(FeatureDecisionTreeNode n);
		void visit(LeafValueDecisionTreeNode n);
	}
}
