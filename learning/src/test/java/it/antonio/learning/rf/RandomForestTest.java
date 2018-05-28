package it.antonio.learning.rf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import it.antonio.learning.data.DataSet;
import it.antonio.learning.data.SimpleData;
import it.antonio.learning.data.SimpleDataSet;
import it.antonio.learning.dt.DecisionTreeResult;
import it.antonio.learning.dt.Feature;

public class RandomForestTest {

	@Test
	public void test() {
		
		int size = 30;
		String[] headers = new String[size + 1];
		List<Feature<?>> features = new LinkedList<>();
		for(int i = 0; i < size; i++) {
			String h = "h" + i;
			headers[i] = h;
			features.add(Feature.withName(h, r -> r.getValue(h)));
		}
		
		headers[size] = "res"; 
		Feature<?> cf = Feature.withName("res", r -> r.getValue("res"));
		
		
		SimpleDataSet dataset = new SimpleDataSet(headers);
		for(int j = 0; j < 10000; j++) {
			Object[] row = new Object[size + 1];
			
			int index = (int) (size * Math.random());
			row[size] = index;
			for(int i = 0; i < size; i++) {
				row[i] = (i == index ? 1 : 0);
			}
			System.out.println(Arrays.toString(row));		
			dataset.add(row);
		}
		
		RandomForest rf = new RandomForestTrainer().train(dataset, features, cf, 100, 0.5);
		
		for(int j = 0; j < 100; j++) {
			Object[] row = new Object[size];
			
			
			
			int index = (int) (size * Math.random());
			for(int i = 0; i < size; i++) {
				row[i] = (i == index ? 1 : 0);
			}
			SimpleData data = SimpleDataSet.fromHeaders(dataset, row);
			
			//long time = System.currentTimeMillis();
			DecisionTreeResult result = rf.classify(data);
			//System.out.println(System.currentTimeMillis() - time);
			
			Assert.assertEquals(result.value, index);
			Assert.assertEquals(result.percentage, Double.valueOf(1d));
		}
		
		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.printf("%.3fMB\n", usedMemory / (1024.0 * 1024.0));

		
	}
}
