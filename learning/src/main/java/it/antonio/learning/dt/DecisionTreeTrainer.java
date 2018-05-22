package it.antonio.learning.dt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.antonio.learning.dt.DecisionTreeNode.FeatureDecisionTreeNode;
import it.antonio.learning.dt.DecisionTreeNode.LeafValueDecisionTreeNode;
import it.antonio.learning.dt.data.Data;
import it.antonio.learning.dt.data.DataSet;

public class DecisionTreeTrainer {
	
	public DecisionTree train(DataSet data, List<Feature<?>> features, Feature<?> classificationFeature) {
		
		
		DecisionTreeNode root = growTree(new DataSetCollection(data), features, classificationFeature);
		
		
		return new DecisionTree(root);
	}
	
	
	private DecisionTreeNode growTree(Collection<Data> rows,  List<Feature<?>> features, Feature<?> classificationFeature) {
		
		Collection<Count> classification = count(classificationFeature, rows);
		
		if(features.isEmpty()) {
			DecisionTreeResult obj = getClassificationValue(rows, classificationFeature) ; // calculate percentage
			return new LeafValueDecisionTreeNode(obj);
			
		}
		
		if(classification.size() == 1) {
			
			// LEAF
			DecisionTreeResult obj = getClassificationValue(classification.iterator().next().rows, classificationFeature) ; // calculate percentage
			return new LeafValueDecisionTreeNode(obj);
			
		} else {
			
			// FEATURE
			
			
			Feature<?> bestFeature = findBestSplit(features, classification);
			Collection<Count> bestClassification = count(bestFeature, rows);
			
			FeatureDecisionTreeNode node = new DecisionTreeNode.FeatureDecisionTreeNode(bestFeature);
			
			if(bestClassification.size() == 1) {
				
				DecisionTreeResult obj = getClassificationValue(bestClassification.iterator().next().rows, classificationFeature) ; // calculate percentage
				node.add(obj, new LeafValueDecisionTreeNode(obj));
				
				
			} else {
				List<Feature<?>> filteredFeatures = features.stream().filter(f -> f != bestFeature).collect(Collectors.toList()); 
				
				for(Count bestCount: bestClassification) {
					DecisionTreeNode childNode = growTree(bestCount.rows, filteredFeatures, classificationFeature);
					node.add(bestCount.obj, childNode);
				}
				
					
			}
			
			return node;
			
		}
		
		
		
	
	}
	private DecisionTreeResult getClassificationValue(Collection<Data> rows, Feature<?> classificationFeature) {
		Collection<Count> count = count(classificationFeature, rows);
		Count bestCount = count.stream().max((c1, c2) -> c1.count.compareTo(c2.count)).get();
		
		return new DecisionTreeResult((double)bestCount.count / (double)rows.size(), bestCount.obj) ;
	}


	private Feature<?> findBestSplit(List<Feature<?>> features, Collection<Count> classifications){
		double bestEntropy = Integer.MAX_VALUE;
		Feature<?> bestFeature = null;
		
		for(Feature<?> feature: features) {
			double entropy = 0;
			
			for(Count classification: classifications) {
				
				Collection<Count> featureClassification = count(feature, classification.rows);
				
				entropy += ImpurityCalculator.calculateEntropy(classification.rows.size(), featureClassification.stream().map(c -> c.count).collect(Collectors.toList())  );
				
			}
			if(entropy < bestEntropy) {
				bestEntropy = entropy;
				bestFeature = feature;
			}
		}
		
		/*
		for(Count classification: classifications) {
			
			for(Feature<?> feature: features) {
			
				Collection<Count> featureClassification = count(feature, classification.rows);
				
				double entropy = ImpurityCalculator.calculateEntropy(classification.rows.size(), featureClassification.stream().map(c -> c.count).collect(Collectors.toList())  );
				
				if(entropy < bestEntropy) {
					bestEntropy = entropy;
					bestFeature = feature;
					bestClassification = featureClassification;
				}
			
			
			}
		}
		*/
		//return new BestSplit(bestFeature, null);
		return bestFeature;
	}
	
	private class BestSplit {
		private Feature<?> feature;
		private Collection<Count> count;
		
		public BestSplit(Feature<?> feature, Collection<Count> count) {
			super();
			this.feature = feature;
			this.count = count;
		}
		
		
	}
	
	
	private Collection<Count> count(Feature<?> feature, Iterable<Data> data){
		
		Map<Object, Count> countMap = new HashMap<>();
		for(Data row: data) {
			Object val = feature.calculate(row);
			Count ci = countMap.get(val);
			if(ci == null) {
				countMap.put(val, new Count(val, row));
			} else {
				ci.count++;
				ci.rows.add(row);
			}
		}
		
		return countMap.values(); 
	}
	
	private class Count implements Comparable<Count>{
		private Object obj;
		private Integer count = 1;
		private List<Data> rows;
		
		public Count(Object obj, Data row) {
			super();
			this.obj = obj;
			this.rows = new LinkedList<>();
			this.rows.add(row);
		}



		@Override
		public int compareTo(Count o) {
			return count.compareTo(o.count) * -1;
		}
		
		@Override
		public String toString() {
			return obj + " " + count;
		}
		
	}
	
	
	private class DataSetCollection implements Collection<Data> {

		private DataSet dataSet;
		
		public DataSetCollection(DataSet dataSet) {
			super();
			this.dataSet = dataSet;
		}

		@Override
		public int size() {
			return dataSet.size();
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public boolean contains(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<Data> iterator() {
			return dataSet.iterator();
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Data e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends Data> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
		
	}
	

}
