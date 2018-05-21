package it.antonio.memorydb.id;

import java.util.UUID;

import it.antonio.memorydb.ObjectId;

public class UUIDObjectId implements ObjectId{
	private UUID uuid;

	public UUIDObjectId(UUID uuid) {
		super();
		this.uuid = uuid;
	}

	public int hashCode() {
		return uuid.hashCode();
	}
	
	
}
