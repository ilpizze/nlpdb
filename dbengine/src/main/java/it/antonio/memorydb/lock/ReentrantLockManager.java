package it.antonio.memorydb.lock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantLockManager implements LockManager{
	
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	@Override
	public void acquireReadLock() {
		lock.readLock().lock();
	}

	@Override
	public void acquireWriteLock() {
		lock.writeLock().lock();
	}

	@Override
	public void releaseReadLock() {
		lock.readLock().unlock();
	}

	@Override
	public void releaseWriteLock() {
		lock.writeLock().unlock();
	}

}
