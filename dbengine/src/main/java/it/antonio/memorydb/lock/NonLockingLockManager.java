package it.antonio.memorydb.lock;

import java.util.concurrent.locks.StampedLock;

public class NonLockingLockManager implements LockManager {
	
	public void acquireReadLock() {}
	public void acquireWriteLock() {}
	public void releaseReadLock() {}
	public void releaseWriteLock() {} 
}
