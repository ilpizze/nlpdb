package it.antonio.memorydb;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.antonio.memorydb.util.LazyIterator;

public interface ResultIterator {
	Iterator<ObjectId> iterator();
	Long size();
	boolean contains(ObjectId id);
	public default boolean sizeExtimated() {
		return false;
	}
	
	
	public static ResultIterator empty() {
		return new ResultIterator() {
			
			@Override
			public Long size() {
				return 0l;
			}
			
			@Override
			public Iterator<ObjectId> iterator() {
				return Collections.emptyIterator();
			}

			@Override
			public boolean contains(ObjectId id) {
				return false;
			}
			
			
		};
		
	}
	
	public static ResultIterator and(List<ResultIterator> list) {
		
		Collections.sort(list, (i1, i2) -> i1.size().compareTo(i2.size()));
		
		ResultIterator smaller = list.iterator().next();
		
		return new ResultIterator() {
			
			
			
			@Override
			public Iterator<ObjectId> iterator() {
				Iterator<ObjectId> iterator1 = smaller.iterator();
				
				return new LazyIterator<ObjectId>() {

					@Override
					protected ObjectId computeNext() {
						while(iterator1.hasNext()) {
							ObjectId id = iterator1.next();
							
							
							for(ResultIterator ri: list) {
								if(ri != smaller && !ri.contains(id)) {
									return computeNext();
								}
							}
							
							return id;
						}
						return endOfData();
					}
				};
			}
			
			@Override
			public boolean contains(ObjectId id) {
				for(ResultIterator ri: list) {
					if(!ri.contains(id)) {
						return false;
					}
				}
				return true;
			}
			
			
			@Override
			public Long size() {
				return smaller.size();
			}
			
			@Override
			public boolean sizeExtimated() {
				return true;
			}
		};
		
	}
	
	/*
	private static ResultIterator merge(ResultIterator it1, ResultIterator it2) {
		return new ResultIterator() {
			
			@Override
			public Long size() {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public Iterator<ObjectId> iterator() {
				Iterator<ObjectId> iterator1 = it1.iterator();
				return new LazyIterator<ObjectId>() {

					@Override
					protected ObjectId computeNext() {
						while(iterator1.hasNext()) {
							ObjectId id = iterator1.next();
							if(it2.contains(id)) {
								return id;
							}
						}
						return endOfData();
					}
				};
			}
			
			@Override
			public boolean contains(ObjectId id) {
				return it1.contains(id) && it2.contains(id);
			}
		};
	}*/
	static ResultIterator or(List<ResultIterator> list) {
		return new ResultIterator() {
			
			Set<ObjectId> processed = new HashSet<>();
			Iterator<ResultIterator> rit = list.iterator();
			Iterator<ObjectId> oidit;
			
			
			@Override
			public Iterator<ObjectId> iterator() {
				return new LazyIterator<ObjectId>() {

					@Override
					protected ObjectId computeNext() {
						while(oidit.hasNext()) {
							ObjectId id = oidit.next();
							if(processed.contains(id)) {
								return computeNext();
							} else {
								processed.add(id);
								return id;
							}
								
						}
						while(rit.hasNext()) {
							oidit = rit.next().iterator();
							return computeNext();
						}
						return endOfData();
					}
				};
			}
			
			@Override
			public boolean contains(ObjectId id) {
				for(ResultIterator ri: list) {
					if(ri.contains(id)) {
						return true;
					}
				}
				return false;
			}
			
			@Override
			public Long size() {
				long size = 0;
				for(ResultIterator ri: list) {
					size += ri.size();
				}
				return size;
			}
			
			@Override
			public boolean sizeExtimated() {
				return true;
			}
			
		};
	}
	
	
	
}
