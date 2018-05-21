package it.antonio.memorydb.query.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Query;
import it.antonio.memorydb.query.And;
import it.antonio.memorydb.query.Eq;
import it.antonio.memorydb.query.Greater;
import it.antonio.memorydb.query.Lesser;
import it.antonio.memorydb.query.Or;
import it.antonio.memorydb.util.LazyIterator;

public class QueryParser<T> {

	private final static String AND = "and", OR = "or";

	private Map<String, Attribute<T>> attributes;
	private Map<String, Class<?>> types;

	public QueryParser(Map<String, Attribute<T>> attributes, Map<String, Class<?>> types) {
		super();
		this.attributes = attributes;
		this.types = types;
	}

	public Query<T> parse(String query) {

		Lexer lexer = new Lexer(query.toCharArray());
		
		return parseTerm(lexer, attributes, types);
	}

	private Query<T> parseTerm(Lexer lexer, Map<String, Attribute<T>> attributes,	Map<String, Class<?>> types){
		
		boolean open = lexer.isSpecial("(");
		if(open) lexer.next(); // open
		
		Query<T> q = parseSimple(lexer, attributes, types);

		if (lexer.isSpecial(AND)) {
			q = parseAnd(q, lexer, attributes, types);
		}

		if (lexer.isSpecial(OR)) {
			q =  parseOr(q, lexer, attributes, types);
		}

		if(open && !lexer.isSpecial(")")) {
			throw new QueryParseException("Expected close ')'");
		}
		if(open) lexer.next(); // close
		
			
		return q;	
			
	}
	
	private And<T> parseAnd(Query<T> prev, Lexer lexer, Map<String, Attribute<T>> attributes,	Map<String, Class<?>> types) {
		
		List<Query<T>> list = new LinkedList<>();
		And<T> and = new And<>(list);
		list.add(prev);
		
		
		while (lexer.isSpecial(AND)) {
			lexer.next(); //and
			prev = parseTerm(lexer, attributes, types);
			list.add(prev);
			
			if(lexer.isSpecial(OR)) {
				throw new QueryParseException("Invalid OR after AND");
			}
		}

		return and;
	}

	private Or<T> parseOr(Query<T> prev, Lexer lexer, Map<String, Attribute<T>> attributes,	Map<String, Class<?>> types) {
		
		List<Query<T>> list = new LinkedList<>();
		Or<T> or = new Or<>(list);
		list.add(prev);

		while (lexer.isSpecial(OR)) {
			lexer.next(); // or
			prev = parseTerm(lexer, attributes, types);
			list.add(prev);
			
			if(lexer.isSpecial(AND)) {
				throw new QueryParseException("Invalid AND after OR");
			}
		}

		return or;
	}

	@SuppressWarnings("unchecked")
	private Query<T> parseSimple(Lexer tokens, Map<String, Attribute<T>> attributes, Map<String, Class<?>> types) {
		if(!tokens.hasNext()) {
			throw new QueryParseException("Expected attribute");
		}
		String attribute = tokens.next();
		if (!attributes.containsKey(attribute)) {
			throw new QueryParseException("Attribute non supported: " + attribute);
		}
		
		if(!tokens.hasNext()) {
			throw new QueryParseException("Expected query operator");
		}
		
		String queryType = tokens.next();
		
		if(!tokens.hasNext()) {
			throw new QueryParseException("Expected value for " + attribute + " " + queryType);
		}
		
		String value = tokens.next();
		
		switch (queryType) {
			case "eq":
			case "==": {
				Object resolvedType = resolveType(value, types.get(attribute));
				return new Eq<>(attributes.get(attribute), resolvedType);
			}
			case "le":
			case "<=": {
				Object resolvedType = resolveType(value, types.get(attribute));
				return new Lesser<>(attributes.get(attribute), (Comparable<? extends Comparable<?>>) resolvedType);
			}
			case "ge":
			case ">=": {
				Object resolvedType = resolveType(value, types.get(attribute));
				return new Greater<T>(attributes.get(attribute), (Comparable<? extends Comparable<?>>) resolvedType);
			}
			default: {
				throw new QueryParseException("query type not supported: " + queryType);
			}
		}
	}

	private Object resolveType(String value, Class<?> type) {
		if (type.equals(String.class) && value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'') {
			return value.substring(1, value.length() - 1);
		}
		if (type.equals(Character.class) || type.equals(char.class) && value.charAt(0) == '\''
				&& value.charAt(value.length() - 1) == '\'' && value.length() == 3) {
			return value.charAt(1);
		}
		try {
			if (type.equals(Integer.class) || type.equals(int.class)) {
				return Integer.parseInt(value);
			}
			if (type.equals(Long.class) || type.equals(long.class)) {
				return Long.parseLong(value);
			}
			if (type.equals(Double.class) || type.equals(double.class)) {
				return Double.parseDouble(value);
			}
			if (type.equals(Float.class) || type.equals(float.class)) {
				return Float.parseFloat(value);
			}
		} catch (NumberFormatException e) {
			throw new QueryParseException(value + " is not numerical");
		}
		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
				return Boolean.parseBoolean(value);
			} else {
				throw new QueryParseException(value + " is not boolean");
			}
		}

		throw new QueryParseException("Type not supported for" + value);

	}

	private class Lexer extends LazyIterator<String> {
		private char[] chars;
		private int index = 0;
		private int revertIndex = 0;
		
		StringBuilder bld = new StringBuilder();

		
		private char open = '(';
		private char close = ')';
		
		
		
		public Lexer(char[] chars) {
			super();
			this.chars = chars;
			while (Character.isWhitespace(chars[index])) {
				index++;
			}
		}


		public boolean isSpecial(String special) {
			boolean ret = false;
			
			if(hasNext()) {
				String next = next();
				ret = next.equalsIgnoreCase(special);
			} 
			revert();
			return ret;
			
		}


		@Override
		protected String computeNext() {
			if(index == chars.length) {
				return endOfData();
			}
			
			revertIndex = index;
			if(open == chars[index]) {
				index++;
				return "(";
			}
			if(close == chars[index]) {
				index++;
				return ")";
			}

			bld.setLength(0);
			while (index < chars.length &&  
					!Character.isWhitespace(chars[index]) 
					 && chars[index] != '(' && chars[index] != ')') {
				
				bld.append(chars[index]);
				index++;
			}
			while (index < chars.length && Character.isWhitespace(chars[index])) {
				index++;
			}
			return bld.length() > 0 ? bld.toString() : computeNext();
		}
		
		public void revert() {
			index = revertIndex;
		}

	}
}
