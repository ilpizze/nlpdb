package it.antonio.memorydb.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.antonio.memorydb.Attribute;
import it.antonio.memorydb.Index;
import it.antonio.memorydb.ObjectId;
import it.antonio.memorydb.Query;
import it.antonio.memorydb.ResultIterator;
import it.antonio.memorydb.Storage;
import it.antonio.memorydb.query.And;
import it.antonio.memorydb.query.Eq;

public class CompoundIndex<OBJ> implements Index<OBJ> {

	private List<Attribute<OBJ>> attribute;

	private Map<List<Object>, List<ObjectId>> map = new HashMap<>();
	private Map<ObjectId, Object> removeMap = new HashMap<>();
	
	public CompoundIndex(List<Attribute<OBJ>> features) {
		super();
		this.attribute = features;
	}

	@Override
	public void insert(OBJ o, ObjectId id) {

		List<Iterable<? extends Object>> values = new ArrayList<>();
		for (Attribute<OBJ> f : attribute) {
			values.add(f.asIterable(o));
		}
		List<List<Object>> combinations = generateCombinations(values);
		for (List<Object> combination : combinations) {
			if (map.containsKey(combination)) {
				map.get(combination).add(id);
			} else {
				List<ObjectId> set = new LinkedList<>();
				set.add(id);
				map.put(combination, set);
			}
		}

	}

	private static List<List<Object>> generateCombinations(List<Iterable<? extends Object>> inputLists) {
		if (inputLists.isEmpty()) {
			return Collections.emptyList();
		}
		List<List<Object>> results = new ArrayList<List<Object>>();
		Iterable<?> currentList = inputLists.get(0);
		if (inputLists.size() == 1) {
			for (Object object : currentList) {
				results.add(new LinkedList<Object>(Collections.singleton(object)));
			}
		} else {
			for (Object object : currentList) {
				List<Iterable<? extends Object>> tail = inputLists.subList(1, inputLists.size());
				for (List<Object> permutations : generateCombinations(tail)) {
					permutations.add(0, object);
					results.add(permutations);
				}
			}
		}
		return results;
	}

	@Override
	public ResultIterator find(Query<OBJ> query, Storage<OBJ> storage) {

		List<Query<OBJ>> eqs = cast(query, And.class).getQueries();
		List<Object> keys = new LinkedList<>();
		for (Query<OBJ> eq : eqs) {
			keys.add(((Eq<OBJ>) eq).getValue());
		}

		List<ObjectId> objs = map.get(keys);

		return new ResultIterator() {

			@Override
			public Iterator<ObjectId> iterator() {
				return objs.iterator();
			}

			@Override
			public Long size() {
				return (long) objs.size();
			}

			@Override
			public boolean contains(ObjectId id) {
				return objs.contains(id);
			}

		};

	}

	@Override
	public boolean supports(Query<?> q) {
		if (check(q, And.class)) {
			List<Query<OBJ>> eqs = cast(q, And.class).getQueries();
			for (Query<OBJ> eq : eqs) {
				if (!check(eq, Eq.class)) {
					return false;
				}
				
				if(!attribute.contains(((Eq<OBJ>) eq).getAttribute())){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void remove(ObjectId oid) {
		Object attributeValue = removeMap.remove(oid);
		List<ObjectId> ids = map.get(attributeValue);
		ids.remove(oid); // expensive for long lists -> linkedlist linear remove
		if(ids.isEmpty()) {
			map.remove(attributeValue);
		}
	}

	@Override
	public void update(ObjectId oid, OBJ o) {
		remove(oid);
		insert(o, oid);
	}
}
