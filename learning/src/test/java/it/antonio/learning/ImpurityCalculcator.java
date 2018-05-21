package it.antonio.learning;

import static it.antonio.learning.ImpurityCalculator.*;

import org.junit.Test;

import it.antonio.learning.ImpurityCalculator;

public class ImpurityCalculcator implements ImpurityCalculator {

	@Test
	public void test() {
		
		//System.out.println(calculateImpurityMultiple(100, 25,25, 25,25));
		//System.out.println(calculateImpurityMultiple(100, 33, 33,34));
		//System.out.println(calculateEntropy(100, 50, 50));
		//System.out.println(calculateEntropy(100, 100, 0));
		
		
		//System.out.println(calculateEntropy(200, 50, 50));
		//System.out.println(calculateEntropy(200, 100, 100));
		
		System.out.println(calculateEntropy(100, 33, 33, 34));
		System.out.println(calculateEntropy(100, 25, 25, 25, 25));
		
		
		System.out.println(calculateEntropy(100, 50, 25, 25));
		
		System.out.println(calculateEntropy(100, 100, 0, 0));
		
		System.out.println(calculateEntropy(100, 1, 1, 1 , 96, 1, 0));
		
		System.out.println(calculateEntropy(100, 4, 96));
		System.out.println(calculateEntropy(100, 96, 4));
		System.out.println(calculateEntropy(100, 99, 1));
		
		
		/*
		System.out.println(calculateImpurity(0));
		System.out.println(calculateImpurity(0.1));
		System.out.println(calculateImpurity(0.5)); // worst impurity
		System.out.println(calculateImpurity(0.7));
		System.out.println(calculateImpurity(0.8));
		System.out.println(calculateImpurity(0.9));
		System.out.println(calculateImpurity(0.99)); // best impurity
		
		System.out.println();
		System.out.println(calculateImpurityMultiple(100, 50,50)); 
		System.out.println(calculateImpurityMultiple(100, 90,10)); 
		System.out.println(calculateImpurityMultiple(100, 10,90));
		System.out.println(calculateImpurityMultiple(100, 1,99));
		System.out.println(calculateImpurityMultiple(100, 0,100));
		
		System.out.println(calculateImpurityMultiple(100, 25,25, 25,25));
		System.out.println(calculateImpurityMultiple(100, 25,25, 0,50));
		System.out.println(calculateImpurityMultiple(100, 100,0, 0,0));
		
		System.out.println(calculateImpurityMultiple(100, 33, 33,34));
		
		System.out.println(calculateImpurityMultiple(100, 50, 50));
		
		System.out.println(calculateImpurityMultiple(100, 25,25, 25,25));
		*/
	}
	
	
	
}
