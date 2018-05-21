package it.antonio.memorydb;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import it.antonio.memorydb.index.HashIndex;
import it.antonio.memorydb.lock.NonLockingLockManager;
import it.antonio.memorydb.lock.ReentrantLockManager;
import it.antonio.memorydb.query.Eq;

public class TestBenchmark {
	
	DecimalFormat format;
	
	@Before
	public void before() {
		 format = new DecimalFormat("###,###");
		 format.getDecimalFormatSymbols().setGroupingSeparator('.');
	}
	
	@Test
	public void testReadInOneSecond() {
		
		benchmarkRead("NON LOCKING", new Records<>(new NonLockingLockManager()));
		//benchmark("STAMPED", new Collection<>(new StampedLockManager()));
		benchmarkRead("REETRANT",new Records<>(new ReentrantLockManager()));
	}
	
	
	@Test
	public void testWritesOneSecond() {
		
		benchmarkWrite("NON LOCKING", new Records<>(new NonLockingLockManager()));
		//benchmark("STAMPED", new Collection<>(new StampedLockManager()));
		benchmarkWrite("REETRANT",new Records<>(new ReentrantLockManager()));
	}
	
	private void benchmarkRead(String type, Records<User> users) {
		
		Attribute<User> name = (u)->Arrays.asList(u.getName());
		users.addIndex(new HashIndex<User>((u) -> Collections.singleton(u.getName())));
		
		
		long time = System.currentTimeMillis();
		for(int i = 0; i < 1000000; i++) {
			
			users.insert(new User("User" + i));
			users.insert(new User("User" + i)); 
		}
		
		long elapsed = System.currentTimeMillis() - time;
		
		System.out.println(type + " TIME TO WRITE " + elapsed);
		time = System.currentTimeMillis();
		
		elapsed = 0;
		
		long reads = 0;
		Eq<User> query = new Eq<User>(name, "User800000");
		while(elapsed < 1000) {
			users.find(query).close();
			
			reads++;
			elapsed = System.currentTimeMillis() - time;
		}
		System.out.println(type + " READ NUMBER " + format.format(reads));
		
	}
	
	
	private void benchmarkWrite(String type, Records<User> users) {
		
		users.addIndex(new HashIndex<User>((u) -> Collections.singleton(u.getName())));
		
		
		long elapsed = 0;
		long write = 0;
		long time = System.currentTimeMillis();
		while(elapsed < 1000) {
			users.insert(new User("User"));
			
			write++;
			elapsed = System.currentTimeMillis() - time;
		}
		System.out.println(type + " WRITE NUMBER " + format.format(write));
		
		
		
	}
	
	public static class User {
		String name;

		public User(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "User [name=" + name + "]";
		}
		
		
	}
}
