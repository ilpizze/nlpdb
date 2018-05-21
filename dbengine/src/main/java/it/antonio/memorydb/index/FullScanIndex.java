package it.antonio.memorydb.index;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import it.antonio.memorydb.Index;
import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.Query;
import it.antonio.memorydb.ResultIterator;
import it.antonio.memorydb.Storage;

public class FullScanIndex<OBJ> implements Index<OBJ> {
	
	private Set<ObjectId> set = new LinkedHashSet<>();
	
	@Override
	public void insert(OBJ o, ObjectId id) {
		set.add(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ResultIterator find(Query<OBJ> query, Storage<OBJ> storage) {
		
		Predicate<ObjectId> filter = (id) -> {
			
			return query.match((OBJ) storage.get(id));
		}; 
		return new ResultIterator() {

			@Override
			public Iterator<ObjectId> iterator() {
				return set.stream().filter(filter).iterator();
			}

			@Override
			public Long size() {
				return (long) set.size(); // always max size
			}

			@Override
			public boolean contains(ObjectId id) {
				return set.contains(id) && query.match((OBJ) storage.get(id));
			}
			
		};
	}

	@Override
	public boolean supports(Query<?> q) {
		return true;
	}

	@Override
	public void remove(ObjectId oid) {
		set.remove(oid);
	}

	@Override
	public void update(ObjectId oid, OBJ o) {
		set.add(oid);
	}

}
