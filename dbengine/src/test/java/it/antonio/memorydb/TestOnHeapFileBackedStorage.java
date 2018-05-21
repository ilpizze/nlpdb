package it.antonio.memorydb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.antonio.memorydb.id.UUIDObjectIdFactory;
import it.antonio.memorydb.index.DefaultIndexEngine;
import it.antonio.memorydb.index.FullScanIndex;
import it.antonio.memorydb.index.HashIndex;
import it.antonio.memorydb.index.TextPrefixIndex;
import it.antonio.memorydb.lock.ReentrantLockManager;
import it.antonio.memorydb.query.And;
import it.antonio.memorydb.query.Eq;
import it.antonio.memorydb.query.Or;
import it.antonio.memorydb.query.StartsWith;
import it.antonio.memorydb.storage.OnHeapFileBackedStorage;
import it.antonio.memorydb.storage.OnHeapFileBackedStorage.FileBackedStorageAdapter;

public class TestOnHeapFileBackedStorage {
	
	File file = new File("prova.dat");
	
	@Before
	public void before() throws IOException {
		if(file.exists()) file.delete();
		file.createNewFile();
	}
	
	@Test
	public void test() {
		ReentrantLockManager lockManager = new ReentrantLockManager();
		
		
		IndexEngine<User> indexEngine = new DefaultIndexEngine<>();
		ObjectIdFactory objectIdFactory = new UUIDObjectIdFactory();
		FileBackedStorageAdapter<User> adapter = new FileBackedStorageAdapter<User>() {
			
			int size = /*name*/ 1000 + /*surname*/ 1000 + /*age*/ 4 + /*def1*/ 1000;      
			private Charset charset = Charset.forName("UTF-8");
			
			@Override
			public int size() {
				return size;
			}

			@Override
			public void serialize(User u, RandomAccessFile file) {
				try {
					writeString(u.getName(), file);
					writeString(u.getSurname(), file);
					file.writeInt(u.getAge());
					writeString(u.getDef1(), file);
					
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
				
			}

			@Override
			public User deserialize(RandomAccessFile file) {
				try {
					User user = new User(readString(file), readString(file), file.readInt(), readString(file));
					//System.out.println(user.getName());
					return user;
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			private void writeString(String s, RandomAccessFile file) throws IOException {
				long position = file.getFilePointer();
				if(s == null) {
					file.writeInt(-1);
				} else {
					byte[] nameBytes = s.getBytes(charset);
					file.writeInt(nameBytes.length);
					file.write(nameBytes);
					
				}
				file.seek(position + 1000);
				
			}
			
			private String readString(RandomAccessFile file) throws IOException {
				long position = file.getFilePointer();
				String res;
				int l = file.readInt();
				if(l == -1) {
					res = null;
				} else {
					byte[] bytes = new byte[l];
					file.read(bytes);
					file.seek(file.getFilePointer() + (1000- 4 -bytes.length));
					res = new String(bytes, charset);
				}
				
				file.seek(position + 1000);
				return res;
			}
			
		};
		
		OnHeapFileBackedStorage<User> storage = new OnHeapFileBackedStorage<>(file , adapter, indexEngine , lockManager, objectIdFactory );
		
		Records<User> users = new Records<>( storage, indexEngine, lockManager, objectIdFactory);
		
		Attribute<User> name = (u)->Arrays.asList(u.getName());
		name = name.withName("name");
		Attribute<User> surname = (u)->Arrays.asList(u.getSurname());
		surname = surname.withName("surname");
		Attribute<User> age = (u)-> Arrays.asList(u.getAge());
		age = age.withName("age");
		Attribute<User> def1 = (u)-> Arrays.asList(u.getDef1());
		age = age.withName("def1");
		users.addIndex(new FullScanIndex<>());
		users.addIndex(new HashIndex<>(name));
		users.addIndex(new HashIndex<>(surname));
		users.addIndex(new HashIndex<>(age));
		users.addIndex(new HashIndex<>(def1));
		users.addIndex(new TextPrefixIndex<>(def1));
		storage.init();
		
		int size = 1000;
		
		insert(users, size);
		
		Query<User> q1 = new And<User>(Arrays.asList(
							new Eq<User>(name,  "User" + 5), 
							new Eq<User>(surname,  "surname" + 5), 
							new Eq<User>(age,  5) ));
		
		Query<User> q2 = new Eq<User>(name,  "User" + 5);
		Assert.assertEquals(2, users.find(q2).stream().collect(Collectors.toList()).size()) ;
		Assert.assertEquals(1, users.find(q1).stream().collect(Collectors.toList()).size()) ;
		
		Query<User> q3 = new StartsWith<>(def1, "pariUser");
		Assert.assertEquals(size/2, users.find(q3).stream().collect(Collectors.toList()).size()) ;
		
		
		
		Query<User> q4 = new And<User>(Arrays.asList(
				new Eq<User>(name,  "User" + 5), 
				new Or<User>(Arrays.asList(
						new Eq<User>(surname,  "surname" + 5),
						new Eq<User>(surname,  "surname")
						))
				));
		Assert.assertEquals(2, users.find(q4).stream().collect(Collectors.toList()).size()) ;
				

		storage.close();
		
		
		ReentrantLockManager lockManager2 = new ReentrantLockManager();
		IndexEngine<User> indexEngine2 = new DefaultIndexEngine<>();
		ObjectIdFactory objectIdFactory2 = new UUIDObjectIdFactory();
		OnHeapFileBackedStorage<User> storage2 = new OnHeapFileBackedStorage<>(file , adapter, indexEngine2 , lockManager2, objectIdFactory2 );
		users = new Records<>( storage2, indexEngine2, lockManager2, objectIdFactory2);
		users.addIndex(new FullScanIndex<>());
		users.addIndex(new HashIndex<>(name));
		users.addIndex(new HashIndex<>(surname));
		users.addIndex(new HashIndex<>(age));
		users.addIndex(new HashIndex<>(def1));
		users.addIndex(new TextPrefixIndex<>(def1));
		storage2.init();
		
		Assert.assertEquals(2, toList(users.find(q2)).size()) ;
		Assert.assertEquals(1, toList(users.find(q1)).size()) ;
		Assert.assertEquals(size/2, toList(users.find(q3)).size()) ;
		Assert.assertEquals(2, toList(users.find(q4)).size()) ;
	
		insert(users, size);
		
		storage2.close();
		
		size = size * 2;
		
		ReentrantLockManager lockManager3 = new ReentrantLockManager();
		IndexEngine<User> indexEngine3 = new DefaultIndexEngine<>();
		ObjectIdFactory objectIdFactory3 = new UUIDObjectIdFactory();
		OnHeapFileBackedStorage<User> storage3 = new OnHeapFileBackedStorage<>(file , adapter, indexEngine3 , lockManager3, objectIdFactory3 );
		users = new Records<>( storage3, indexEngine3, lockManager3, objectIdFactory3);
		users.addIndex(new FullScanIndex<>());
		users.addIndex(new HashIndex<>(name));
		users.addIndex(new HashIndex<>(surname));
		users.addIndex(new HashIndex<>(age));
		users.addIndex(new HashIndex<>(def1));
		users.addIndex(new TextPrefixIndex<>(def1));
		storage3.init();
		
		Assert.assertEquals(4, users.find(q2).stream().collect(Collectors.toList()).size()) ;
		Assert.assertEquals(2, users.find(q1).stream().collect(Collectors.toList()).size()) ;
		Assert.assertEquals(size/2, users.find(q3).stream().collect(Collectors.toList()).size()) ;
		Assert.assertEquals(4, users.find(q4).stream().collect(Collectors.toList()).size()) ;
	
		
		storage3.close();
	}
	
	private void insert(Records<User> users, int size) {
		
		for(int i = 0; i < size; i++) {
			users.insert(new User("User" + i, "surname", i, i%2 == 0 ? "pariUser": "dispariUser" ));
			users.insert(new User("User" + i, "surname" + i, i));
		}
		
	}
	
	private <T> List<T> toList(Results<T> rit){
		List<T> l = rit.stream().collect(Collectors.toList());
		rit.close();
		return l;
	}

	
}
