package it.antonio.learning.dt;

import java.util.HashMap;
import java.util.Map;

import it.antonio.learning.dt.DecisionTree.DecisionTreeVisitor;
import it.antonio.learning.dt.data.Data;

public interface DecisionTreeNode {
	
	DecisionTreeResult classify(Data data);
	void accept(DecisionTreeVisitor v);

	public static class FeatureDecisionTreeNode implements DecisionTreeNode {
		
		public Feature<?> feature;
		public Map<Object, DecisionTreeNode> children = new HashMap<>();

		public FeatureDecisionTreeNode(Feature<?> feature) {
			super();
			this.feature = feature;
		}



		@Override
		public DecisionTreeResult classify(Data data) {
			Object obj = feature.calculate(data);

			if(children.containsKey(obj)) {
				return children.get(obj).classify(data);
			} else {
				return null;	
			}
			
		}



		public void add(Object obj, DecisionTreeNode childNode) {
			children.put(obj, childNode);
		}
		public void accept(DecisionTreeVisitor v) {
			v.visit(this);
		}
		
	}
	
	public static class LeafValueDecisionTreeNode implements DecisionTreeNode {
		public DecisionTreeResult result;
		
		public LeafValueDecisionTreeNode(DecisionTreeResult result) {
			super();
			this.result = result;
		}

		@Override
		public DecisionTreeResult classify(Data data) {
			return result;
		}
		
		public void accept(DecisionTreeVisitor v) {
			v.visit(this);
		}
		
	}

	
	

	
}
