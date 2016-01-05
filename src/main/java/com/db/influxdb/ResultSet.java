package com.db.influxdb;

import java.util.List;

public class ResultSet {

	private List<Result> results;

	private String error;

	public class Result {

		private List<Series> series;

		private String error;

		public class Series {

			private List<String> columns;

			private List<List<String>> values;

			public List<String> getColumns() {
				return columns;
			}

			public List<List<String>> getValues() {
				return values;
			}

			@Override
			public String toString() {
				return "Series [columns=" + columns + ", values=" + values + "]";
			}
			
		}

		public List<Series> getSeries() {
			return series;
		}

		public String getError() {
			return error;
		}

		@Override
		public String toString() {
			return "Result [series=" + series + ", error=" + error + "]";
		}
		
	}

	public List<Result> getResults() {
		return results;
	}

	public String getError() {
		return error;
	}

	@Override
	public String toString() {
		return "ResultSet [results=" + results + ", error=" + error + "]";
	}

	
}
