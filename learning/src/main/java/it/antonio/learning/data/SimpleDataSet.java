package it.antonio.learning.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleDataSet implements DataSet {
	private List<Map<String, Object>> dataSet = new LinkedList<>();
	
	private String[] header; 
	
	public SimpleDataSet(String... header) {
		super();
		this.header = header;
	}

	public void add(Object... row) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for(int i = 0; i < row.length; i++) {
			map.put(header[i], row[i]);
		}
		dataSet.add(map);
	}
	
		


	@Override
	public Iterator<Data> iterator() {
		return dataSet.stream().map((m) -> (Data) new SimpleData(m)).collect(Collectors.toList()).iterator();
	}
	
	@Override
	public int size() {
		return dataSet.size();
	}
	
	public static SimpleData from(Object...values) {
		Map<String, Object> map= new HashMap<>();
		for(int i = 0; i < (values.length / 2); i++) {
			map.put((String) values[i], values[i + values.length / 2]);
		}
		return new SimpleData(map);
	}
	
	public static SimpleData fromHeaders(SimpleDataSet set, Object...values) {
		Map<String, Object> map= new HashMap<>();
		for(int i = 0; i < values.length; i++) {
			map.put(set.header[i], values[i]);
		}
		return new SimpleData(map);
	}

}
