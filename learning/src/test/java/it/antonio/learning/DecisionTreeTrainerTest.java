package it.antonio.learning;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import it.antonio.learning.data.Data;
import it.antonio.learning.data.SimpleData;
import it.antonio.learning.data.SimpleDataSet;
import it.antonio.learning.dt.DecisionTree;
import it.antonio.learning.dt.DecisionTreeTrainer;
import it.antonio.learning.dt.Feature;
import it.antonio.learning.dt.DecisionTree.DecisionTreeVisitor;
import it.antonio.learning.dt.DecisionTreeNode.FeatureDecisionTreeNode;
import it.antonio.learning.dt.DecisionTreeNode.LeafValueDecisionTreeNode;

public class DecisionTreeTrainerTest {

	@Test
	public void test() {
		DecisionTreeTrainer trainer = new DecisionTreeTrainer();
		
		SimpleDataSet data = new SimpleDataSet("temp", "weather", "loc");
		
		Random random = new Random();
		for(int i = 0; i < 100; i++) {
			int temp = random.nextInt(33);
			boolean sunny = random.nextBoolean();
			
			String loc = "nord";
			if(temp > 12 && sunny) {
				loc = "sud";
			} 
			
			data.add(new Object[] { temp, sunny ? "sunny" : "rain", loc });
		}
		
		System.out.println(print("temp", 5) + "|" +print( "weather", 5) + "|" +print( "loc" , 6));
		for(Data row: data) {
			System.out.println(print(row.getValue("temp"), 5) + "|" +print( row.getValue("weather"), 5) + "|" +print( row.getValue("loc") , 6));
				
		}
		
		List<Feature> features = new LinkedList<>();
		features.add(Feature.withName("temperature", (row) -> ((Integer) row.getValue("temp")) > 20   ) ); 
		features.add(Feature.withName("sunny", (row) -> row.getValue("weather") ) ); 
		
		Feature classification = Feature.withName("loc", (row) -> row.getValue("loc") );
		
		
		DecisionTree tree = trainer.train(data, features, classification);
		
		
		tree.accept(new DecisionTreeVisitor() {
			private String indent = ""; 
			@Override
			public void visit(LeafValueDecisionTreeNode n) {
				System.out.println(indent + n.result.value+ "/" + n.result.percentage);
			}
			
			@Override
			public void visit(FeatureDecisionTreeNode n) {
				System.out.println(indent + n.feature);
				indent += " ";
				n.children.values().forEach(c -> c.accept(this));
				indent = indent.substring(1);
			}
		});
		
		
		Assert.assertEquals(tree.classify(SimpleDataSet.from("temp", "weather" , 30, "sunny")).value ,  "sud");
	}

	
	private String print(Object o, int size) {
		String ret = o.toString();
		for(int i = ret.length(); i < size; i++) {
			ret += " ";
		}
		return ret;
	}
}
