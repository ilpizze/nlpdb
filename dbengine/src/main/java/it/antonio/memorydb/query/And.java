package it.antonio.memorydb.query;

import java.util.List;
import java.util.stream.Collectors;

import it.antonio.memorydb.Query;

public class And<T> implements Query<T>{

	private List<? extends Query<T>> queries;
	
	public And(List<? extends Query<T>> queries) {
		super();
		this.queries = queries;
	}

	public List<? extends Query<T>> getQueries() {
		return queries;
	}

	@Override
	public boolean match(T obj) {
		for(Query<T> query : queries) {
			if(!query.match(obj)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "(" + queries.stream().map(Query::toString).collect(Collectors.joining(" and ")) + ")";
	}
	
}
