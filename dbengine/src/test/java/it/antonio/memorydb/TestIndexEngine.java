package it.antonio.memorydb;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import it.antonio.memorydb.index.FullScanIndex;
import it.antonio.memorydb.index.HashIndex;
import it.antonio.memorydb.index.TextPrefixIndex;
import it.antonio.memorydb.lock.NonLockingLockManager;
import it.antonio.memorydb.query.And;
import it.antonio.memorydb.query.Eq;
import it.antonio.memorydb.query.Or;
import it.antonio.memorydb.query.StartsWith;

public class TestIndexEngine {
	
	
	@Test
	public void testDefaultIndexEngine() {
		
		
		
		Records<User> users = new Records<>(new NonLockingLockManager());
		
		Attribute<User> name = (u)->u.getName();
		name = name.withName("name");
		Attribute<User> surname = (u)->u.getSurname();
		surname = surname.withName("surname");
		Attribute<User> age = (u)-> u.getAge();
		age = age.withName("age");
		Attribute<User> def1 = (u)-> u.getDef1();
		age = age.withName("def1");
		
		users.addIndex(new FullScanIndex<>());
		users.addIndex(new HashIndex<>(name));
		users.addIndex(new HashIndex<>(surname));
		users.addIndex(new HashIndex<>(age));
		users.addIndex(new HashIndex<>(def1));
		users.addIndex(new TextPrefixIndex<>(def1));
		
		//int size = 1000000;
		int size = 10000;
		for(int i = 0; i < size; i++) {
			users.insert(new User("User" + i, "surname", i, i%2 == 0 ? "pariUser": "dispariUser" ));
			users.insert(new User("User" + i, "surname" + i, i));
		}
		
		Query<User> q1 = new And<User>(Arrays.asList(
							new Eq<User>(name,  "User" + 50), 
							new Eq<User>(surname,  "surname" + 50), 
							new Eq<User>(age,  50) ));
		
		Query<User> q2 = new Eq<User>(name,  "User" + 50);
		Assert.assertEquals(2, users.find(q2).stream().collect(Collectors.toList()).size()) ;
		Assert.assertEquals(1, users.find(q1).stream().collect(Collectors.toList()).size()) ;
		
		Query<User> q3 = new StartsWith<>(def1, "pariUser");
		Assert.assertEquals(size/2, users.find(q3).stream().collect(Collectors.toList()).size()) ;
		
		
		
		Query<User> q4 = new And<User>(Arrays.asList(
				new Eq<User>(name,  "User" + 50), 
				new Or<User>(Arrays.asList(
						new Eq<User>(surname,  "surname" + 50),
						new Eq<User>(surname,  "surname")
						))
				));
		Assert.assertEquals(2, users.find(q4).stream().collect(Collectors.toList()).size()) ;
				

	}
	

	
}
