package it.antonio.memorydb.lock;

public interface LockManager {
	public void acquireReadLock();
	public void acquireWriteLock();
	public void releaseReadLock();
	public void releaseWriteLock();
}
