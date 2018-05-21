package it.antonio.memorydb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import it.antonio.memorydb.index.HashIndex;
import it.antonio.memorydb.lock.ReentrantLockManager;
import it.antonio.memorydb.query.Eq;

public class TestCollection {

	
	
	@Test
	public void test() {
		
		List<String> rep = Collections.synchronizedList(new ArrayList<>());
		Records<String> strings = new Records<>(new ReentrantLockManager());
		Attribute<String> name = (s)->Arrays.asList(s);
		strings.addIndex(new HashIndex<>(name));
		
		ExecutorService service = Executors.newFixedThreadPool(10);
		
		for(int i = 0; i < 100000; i++) {
			int j = (int) (Math.random() * 2);
			
			if(j == 1 && !rep.isEmpty()) {
				service.execute(() -> {
					String s = null;
					Integer index = null;
					try {
						index = (int) (rep.size() * Math.random());
						s = rep.get(index);
						
						Results<String> results = strings.find(new Eq<String>(name, s));
						String read = results.iterator().next();
						System.out.println("READ: " + read);
						results.close();
					} catch(Throwable t) {
						System.out.println("READ:" + rep.size() + " " +index +" " + s);
						t.printStackTrace();
					}
					
				});
				
			} else {
				service.execute(() -> {
					try {
						String s = UUID.randomUUID().toString();
						strings.insert(s);
						rep.add(s);	
					} catch(Throwable t) {
						t.printStackTrace();
					}
						
				});
				
			}
			
		}
		service.shutdown();
		while(!service.isTerminated()) {}
		
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
