package it.antonio.memorydb;

public class User {
	String name;
	String surname;
	int age;
	String def1;
	
	public User(String name) {
		super();
		this.name = name;
	}

	public User(String name, String surname, int age) {
		super();
		this.name = name;
		this.surname = surname;
		this.age = age;
	}
	
	public User(String name, String surname, int age, String def1) {
		super();
		this.name = name;
		this.surname = surname;
		this.age = age;
		this.def1 = def1;
	}

	public String getName() {
		return name;
	}

	
	public String getSurname() {
		return surname;
	}

	public int getAge() {
		return age;
	}
	
	public String getDef1() {
		return def1;
	}

	@Override
	public String toString() {
		return "User [name=" + name + "]";
	}

}