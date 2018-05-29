package it.antonio.learning.dt;

import java.util.function.Function;

import it.antonio.learning.data.Data;


public interface Feature {
	Object calculate(Data row);
	String name();
	
	
	public static Feature withName(String name, Function<Data, Object> f){
		return new Feature() {

			@Override
			public Object calculate(Data row) {
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
