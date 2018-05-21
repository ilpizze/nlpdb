package it.antonio.learning.data;

import java.util.HashMap;
import java.util.Map;

public class SimpleData implements Data{
		private Map<String, Object> row;

		public SimpleData(Map<String, Object> row) {
			super();
			this.row = row;
		}

		public Object getValue(String header) {
			return row.get(header);
		}
		
		@Override
		public String toString() {
			return row.toString();
		}
		

		public static SimpleData from(Object...values) {
			Map<String, Object> map= new HashMap<>();
			for(int i = 0; i < (values.length / 2); i++) {
				map.put((String) values[i], values[i + values.length / 2]);
			}
			return new SimpleData(map);
		}
		
}