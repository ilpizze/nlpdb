package it.antonio.memorydb;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.antonio.memorydb.query.parser.QueryParseException;
import it.antonio.memorydb.query.parser.QueryParser;

public class TestQueryParser {
	
	private QueryParser<User> parser;
	
	@Before
	public void before(){
		Attribute<User> name = (u)->u.getName();
		name = name.withName("name");
		Attribute<User> surname = (u)->u.getSurname();
		surname = surname.withName("surname");
		Attribute<User> age = (u)-> u.getAge();
		age = age.withName("age");
		Attribute<User> def1 = (u)-> u.getDef1();
		def1 = def1.withName("def1");
		
		Map<String, Attribute<User>> attributes = new HashMap<>();
		attributes.put(name.toString(), name);
		attributes.put(surname.toString(), surname);
		attributes.put(age.toString(), age);
		attributes.put(def1.toString(), def1);
		
		Map<String, Class<?>> types = new HashMap<>();
		types.put(name.toString(), String.class);
		types.put(surname.toString(), String.class);
		types.put(age.toString(), Integer.class);
		types.put(def1.toString(), String.class);
		
		parser = new QueryParser<>(attributes, types);
		
	}
	
	@Test
	public void test() {
		
		
		System.out.println(parser.parse("name eq 'antonio'"));
		System.out.println(parser.parse("age eq 25"));
		System.out.println(parser.parse("age eq 25 or name eq 'uu'"));
		System.out.println(parser.parse("(age eq 25 or name eq 'uu')"));
		
		System.out.println(parser.parse("(age eq 25 or name eq 'uu')"));
		
	
		System.out.println(parser.parse("age eq 25 or (name eq 'uu' and name eq 'aa' )"));
		
		System.out.println(parser.parse("age eq 25 or name eq '25' and name eq 'ok'"));
		
	}
	
	@Test
	public void testErrors() {
		Assert.assertTrue(isError("name"));
		Assert.assertTrue(isError("age eq '25'"));
		Assert.assertTrue(isError("age eq 25 or name eq 25"));
		Assert.assertTrue(isError("age eq 25 or name eq 25"));
		Assert.assertTrue(isError("ages eq '25'"));
		Assert.assertTrue(isError("ages eqs '25'"));
			

	}
	
	private boolean isError(String q) {
		try {
			parser.parse(q);
			return false;
		} catch(QueryParseException e) {
			return true;
		}
	} 
	
}
