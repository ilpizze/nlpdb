package it.antonio.memorydb;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import it.antonio.memorydb.index.CompoundIndex;
import it.antonio.memorydb.index.FullScanIndex;
import it.antonio.memorydb.index.HashIndex;
import it.antonio.memorydb.index.NavigableIndex;
import it.antonio.memorydb.index.TextDistanceIndex;
import it.antonio.memorydb.index.TextInvertedIndex;
import it.antonio.memorydb.index.TextKGramIndex;
import it.antonio.memorydb.index.TextPrefixIndex;
import it.antonio.memorydb.index.common.SimpleTokenizer;
import it.antonio.memorydb.lock.NonLockingLockManager;
import it.antonio.memorydb.query.And;
import it.antonio.memorydb.query.Eq;
import it.antonio.memorydb.query.FuzzyText;
import it.antonio.memorydb.query.Greater;
import it.antonio.memorydb.query.In;
import it.antonio.memorydb.query.StartsWith;
import it.antonio.memorydb.query.Text;
import it.antonio.memorydb.query.Wildcard;

public class TestIndex {
	
	
	@Test
	public void testFullScan() {
		Records<User> users = new Records<>(new NonLockingLockManager());
		users.addIndex(new FullScanIndex<>());
		
		for(int i = 0; i < 100; i++) {
			users.insert(new User("User" + i));
			users.insert(new User("User" + i)); 
		}
		
		Assert.assertEquals(users.find((u) -> true).stream().collect(Collectors.toList()).size(), 200);
		Assert.assertEquals(users.find((u) -> "User30".equals(((User)u).getName())).stream().collect(Collectors.toList()).size(), 2);
		
	}
	

	@Test
	public void testCompundIndex() {
		
		Attribute<User> name = (u)->Arrays.asList(u.getName());
		Attribute<User> surname = (u)->Arrays.asList(u.getSurname());
		Attribute<User> age = (u)-> Arrays.asList(u.getAge());
		
		Records<User> users = new Records<>(new NonLockingLockManager());
		
		
		List<Attribute<User>> features = Arrays.asList(name, surname, age);
		users.addIndex(new CompoundIndex<User>(features));
		
		for(int i = 0; i < 100; i++) {
			users.insert(new User("User" + i, "surname" + i, 100));
			users.insert(new User("User" + i, "surname" + i, 100)); 
		}
		
		
		List<Eq<User>> eqs = Arrays.asList(
				new Eq<User>(name, "User50"), 
				new Eq<User>(surname, "surname50"), 
				new Eq<User>(age, 100)
			);
		And<User> and = new And<User>(eqs );
				
		Assert.assertEquals(users.find(and).stream().collect(Collectors.toList()).size(), 2);
		Assert.assertEquals(users.find(and).stream().findFirst().get().getSurname(), "surname50");
		
	}
	
	
	@Test
	public void testNavigable() {
		Records<Integer> ints = new Records<>(new NonLockingLockManager());
		Attribute<Integer> a = (i) -> Collections.singleton(i);
		ints.addIndex(new NavigableIndex<Integer>(a));
		Random intRandom = new Random();
		for(int i = 0; i < 100; i++) {
			ints.insert(intRandom.nextInt(20)); 
		}
		
		List<Integer> result = ints.find(new Greater<Integer>(a , 5)).stream().collect(Collectors.toList());
		
		for(int i = 1; i < result.size(); i++) {
			Assert.assertTrue(result.get(i -1) > 5); // greater
			Assert.assertTrue(result.get(i -1) <= result.get(i));
		}
		
	}
	
	@Test
	public void testText() {
		Attribute<String> att = s -> s;
		Records<String> ss = new Records<>(new NonLockingLockManager());
		ss.addIndex(new TextInvertedIndex<String>(new SimpleTokenizer(),att));
		
		ss.insert("Antonio is ok");
		ss.insert("Ciao Ciao ciao");
		
		ss.insert("ANTONIO ANTONIO");
		
		ss.insert("Ciao antonio ciao");
		ss.insert("AntoniO");
		
		
		List<String> result = ss.find(new Text<>(att, "Antonio")).stream().collect(Collectors.toList());
		
		
		Assert.assertTrue(result.size() == 4); 
		Assert.assertTrue(result.contains("ANTONIO ANTONIO"));
		Assert.assertTrue(result.contains("Antonio is ok"));
		
		//result.forEach(System.out::println);
		
		result = ss.find(new Text<>(att, "CIAO")).stream().collect(Collectors.toList());
		Assert.assertTrue(result.size() == 2); 
		Assert.assertTrue(result.contains("Ciao Ciao ciao"));
		
		ss.remove(new Text<>(att, "CIAO"));
		result = ss.find(new Text<>(att, "CIAO")).stream().collect(Collectors.toList());
		Assert.assertTrue(result.size() == 0); 
		
		ss.insert("Ciao antonio ciao");
		
		result = ss.find(new Text<>(att, "CIAO")).stream().collect(Collectors.toList());
		Assert.assertTrue(result.size() == 1); 
		
		result = ss.find(new Text<>(att, "Antonio")).stream().collect(Collectors.toList());
		Assert.assertTrue(result.size() == 4); 
		
		
	}
	
	@Test
	public void testTextStartsWith() {
		Attribute<String> att = s -> s;
		Records<String> ss = new Records<>(new NonLockingLockManager());
		ss.addIndex(new TextPrefixIndex<String>(att));
		
		ss.insert("Antonio");
		ss.insert("Anto");
		
		ss.insert("ok");
		
		ss.insert("AntoniO");
		
		
		List<String> result = ss.find(new StartsWith<>(att, "Anto")).stream().collect(Collectors.toList());
		
		
		Assert.assertTrue(result.size() == 3); 
		Assert.assertTrue(result.contains("AntoniO"));
		Assert.assertTrue(result.contains("Anto"));
		
		
		result = ss.find(new StartsWith<>(att, "no")).stream().collect(Collectors.toList());
		Assert.assertTrue(result.size() == 0); 
		
		
	}
	@Test
	public void testFuzzyText() {
		Attribute<String> att = s -> s;
		Records<String> ss = new Records<>(new NonLockingLockManager());
		ss.addIndex(new TextDistanceIndex<String>(new SimpleTokenizer(), att, 2));
		
		ss.insert("Antonio");
		ss.insert("Anto");
		
		ss.insert("okaa");
		ss.insert("okaa");
		ss.insert("siaa");
		
		ss.insert("AntoniO");
		ss.insert("Antonio");
		
		
		List<String> result = ss.find(new FuzzyText<>(att, "Antoni")).stream().collect(Collectors.toList());
		
		
		
		Assert.assertTrue(result.size() == 4); 
		Assert.assertTrue(result.contains("AntoniO"));
		Assert.assertTrue(result.contains("Anto"));
		Assert.assertTrue(result.contains("Antonio"));
		
		
		result = ss.find(new FuzzyText<>(att, "nn")).stream().collect(Collectors.toList());
		Assert.assertTrue(result.size() == 0); 
		
		
	}
	
	@Test
	public void testWildcardText() {
		Attribute<String> att = s -> s;
		Records<String> ss = new Records<>(new NonLockingLockManager());
		ss.addIndex(new TextKGramIndex<String>(att));
		
		ss.insert("Antonio is ok");
		
		ss.insert("okaa");
		ss.insert("okaa");
		ss.insert("siaa");
		
		ss.insert("e is a");
		
		
		List<String> result = ss.find(new Wildcard<>(att, "*is*")).stream().collect(Collectors.toList());
		
		
		
		Assert.assertTrue(result.size() == 2); 
		Assert.assertTrue(result.contains("Antonio is ok"));
		Assert.assertTrue(result.contains("e is a"));
		
		
		result = ss.find(new Wildcard<>(att, "tt")).stream().collect(Collectors.toList());
		Assert.assertTrue(result.size() == 0); 
		
		
	}
	
	
	@Test
	public void testIn() {
		Records<User> users = new Records<>(new NonLockingLockManager());
		Attribute<User> name = (u)->Arrays.asList(u.getName());
		users.addIndex(new HashIndex<>(name));
		
		for(int i = 0; i < 100; i++) {
			users.insert(new User("User" + i));
			users.insert(new User("User" + i)); 
		}
		
		Assert.assertEquals(users.find(new In<>(name, Arrays.asList("User50" ,"User70", "User2"))).stream().collect(Collectors.toList()).size(), 6);
		
		
	}
}
