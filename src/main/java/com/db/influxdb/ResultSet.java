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

			public void setColumns(List<String> columns) {
				this.columns = columns;
			}

			public void setValues(List<List<String>> values) {
				this.values = values;
			}

		}

		public List<Series> getSeries() {
			return series;
		}

		public String getError() {
			return error;
		}

	}

	public List<Result> getResults() {
		return results;
	}

	public String getError() {
		return error;
	}

}
