package it.antonio.learning.dt;

import java.util.Arrays;

public interface ImpurityCalculator {
	
	public static double calculateImpurityMultiple(double total, double val1, double val2) {
		double sum = 0;
		sum += calculateImpurity(val1 / total);
		sum += calculateImpurity(val2 / total);
		
		return sum / 2;
	}
	
	public static double calculateImpurity(double p) {
		if (p <= 0 || p >= 1)
			return 0;
		return -1.0 * p * log2(p) - ((1.0 - p) * log2(1.0 - p));

	}

	public static double log2(double x) {
		return Math.log(x) / Math.log(2);
	}
	
	public static double logb(double a, double b) {
		if (a == 0)
			return 0;
		return Math.log(a) / Math.log(b);
}
	public static double calculateEntropy(int total, Integer... classRecords ) {
		return calculateEntropy(total, Arrays.asList(classRecords));
	}
	
	public static double calculateEntropy(int total, Iterable<Integer> classRecords ) {

		double entropy = 0;
		for (Integer record: classRecords) {
			double probability = (double) record / total;

			entropy -= probability * logb(probability, 2);

		}
		return entropy;
	}
}
