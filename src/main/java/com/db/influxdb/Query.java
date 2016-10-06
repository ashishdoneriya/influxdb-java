package com.db.influxdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Query {

	private String tableName;
	
	private List<String> tables;

	private List<String> columns;

	private String duration;

	private Date rangeFrom;

	private Date rangeTo;

	private Integer limit;

	private boolean fillNullValues = false;

	private String fillString;

	private AggregateFunction aggregateFunction = AggregateFunction.NOFUNCTION;;

	private String groupByTime;
	
	private String customQuery;
	
	public Query() {
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getColumns() {
		return columns;
	}

	/**
	 * @param columns
	 *            Name of columns
	 */
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	/**
	 * Add Column to fetch
	 * 
	 * @param column
	 */
	public void addColumn(String column) {
		if (columns == null) {
			columns = new ArrayList<String>();
		}
		columns.add(column);
	}

	/**
	 * Duration. eg. "3h" = fetch records of past 3 hour, "1m" = 1 minute, "1d"
	 * = 1 day, s = seconds (not recommended) Supported formats are (d, h, m, s)
	 * 
	 * @param from
	 *            the from to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setRangeFrom(Date rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public void setRangeTo(Date rangeTo) {
		this.rangeTo = rangeTo;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @param aggregateFunction
	 *            the aggregateFunction to set (min, max, mean or count)
	 */
	public void setAggregateFunction(AggregateFunction aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}

	/**
	 * @param rangeFrom
	 *            the rangeFrom to set
	 */
	public void setRange(Date rangeFrom, Date rangeTo) {
		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
	}

	private List<String> getColumnsWithDoubleQuotes() {
		List<String> list = new ArrayList<String>();
		for (String node : columns) {
			list.add("\"" + node + "\"");
		}
		return list;
	}

	// create query
	public StringBuffer getQuery() {
		StringBuffer query = new StringBuffer();
		if (customQuery != null) {
			return query.append(customQuery);
		}
		if (columns != null && columns.size() > 0) {
			List<String> formattedColumns = getColumnsWithDoubleQuotes();
			
			if (aggregateFunction == AggregateFunction.NOFUNCTION) {
				// select mean("column1")
				query.append("select ").append(formattedColumns.get(0));

				// , mean("column2") , mean("column3")
				for (int i = 1; i < formattedColumns.size(); i++) {
					query.append(", ").append(formattedColumns.get(i));
				}
			} else {

				// select mean("column1")
				query.append("select ").append(aggregateFunction.getFunction()).append('(').append(formattedColumns.get(0)).append(')');

				// , mean("column2") , mean("column3")
				for (int i = 1; i < formattedColumns.size(); i++) {
					query.append(", ").append(aggregateFunction.getFunction()).append('(').append(formattedColumns.get(i)).append(")");
				}
			}
		} else {
			// select *
			query.append("select *");
			// setting aggregate function to null so that it will not add group
			// by time()
			aggregateFunction = null;
		}
		
		// from "tableName"
		if (tableName != null) {
			query.append(" from \"").append(tableName).append("\"");
		} else {
			query.append(" from \"").append(tables.get(0)).append("\"");

			for (int i = 1; i < tables.size(); i++) {
				query.append(", \"").append(tables.get(i)).append(tables.get(i)).append("\"");
			}
		}
		
		long from = 0, to = 0;

		if (rangeFrom != null && rangeTo != null) {
			// where time > 123456s and time < 123567s
			from = getFormatted(rangeFrom);
			to = getFormatted(rangeTo);
			query.append(" where time > ").append(from).append('s').append(" and time < ").append(to).append('s');
		} else if (rangeFrom != null) {
			from = getFormatted(rangeFrom);
			query.append(" where time > ").append(from).append('s');
		} else if (rangeTo != null) {
			to = getFormatted(rangeTo);
			query.append(" where time < ").append(to).append('s');
		} else if (duration != null) {
			// where time > now() - 1h
			query.append(" where time > now() - ").append(duration);
		}

		// group by time(5m)
		if (aggregateFunction != null && aggregateFunction != AggregateFunction.NOFUNCTION
				&& (duration != null || rangeFrom != null || rangeTo != null)) {
			query.append(" group by time(").append(groupByTime).append(')');
		}

		// fill(0)
		if (fillNullValues) {
			query.append(" ").append(fillString);
		}
		if (limit != null) {
			query.append(" limit ").append(limit);
		}
		return query;
	}

	// convert date into particular format
	private long getFormatted(Date date) {
		return date.getTime() / 1000;
	}

	/**
	 * Set group by time ie. group by time("40s") or group by time("1h")
	 * 
	 * @param groupByTime
	 */
	public void setGroupByTime(String groupByTime) {
		this.groupByTime = groupByTime;
	}

	/**
	 * add fill(SOME VALUE) at the end of query eg. fill(0)
	 * 
	 * @param fillString
	 *            eg. 0 or some string like "EMPTY"
	 */
	public void fillNullValues(String fillString) {
		this.fillString = fillString;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}
	
	public String getCustomQuery() {
		return customQuery;
	}

	public void setCustomQuery(String customQuery) {
		this.customQuery = customQuery;
	}

	@Override
	public String toString() {
		return getQuery().toString();
	}

}
