package com.db.influxdb;

public enum AggregateFunction {

	COUNT("COUNT"), MIN("MIN"), MAX("MAX"), MEAN("MEAN"), MODE("MODE"), MEDIAN("MEDIAN"), DISTINCT("DISTINCT"), SUM(
			"SUM"), STDDEV("STDDEV"), FIRST("FIRST"), LAST("LAST"), DIFFERENCE("DIFFERENCE"), NOFUNCTION("");

	private String function;

	private AggregateFunction(String function) {
		this.function = function;
	}

	public String getFunction() {
		return function;
	}
}
