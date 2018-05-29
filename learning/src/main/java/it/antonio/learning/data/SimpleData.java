package it.antonio.learning.data;

import java.util.Map;

public class SimpleData implements Data{
		private Map<String, Object> row;

		public SimpleData(Map<String, Object> row) {
			super();
			this.row = row;
		}

		@SuppressWarnings("unchecked")
		public <T> T getValue(String header) {
			return (T) row.get(header);
		}
		
		@Override
		public String toString() {
			return row.toString();
		}
		

		
		
}