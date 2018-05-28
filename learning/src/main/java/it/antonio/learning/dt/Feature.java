package it.antonio.learning.dt;

import java.util.function.Function;

import it.antonio.learning.data.Data;


public interface Feature<T> {
	T calculate(Data row);
	String name();
	
	
	public static <T> Feature<T> withName(String name, Function<Data, T> f){
		return new Feature<T>() {

			@Override
			public T calculate(Data row) {
				return f.apply(row);
			}

			@Override
			public String name() {
				return name;
			}
			
			
			@Override
			public String toString() {
				return name();
			}
			
		};
	}
	
	
}
