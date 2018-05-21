package it.antonio.memorydb.id;

import java.util.Random;
import java.util.UUID;

import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.ObjectIdFactory;

public class UUIDObjectIdFactory implements ObjectIdFactory {

	@Override
	public ObjectId create() {
		Random r = new Random();
		return new UUIDObjectId(new UUID(r.nextLong(), r.nextLong()));
	}

}
