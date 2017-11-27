package com.db.influxdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Query {

	private String tableName;
	
	private List<String> tables;

	private List<String> columns;

	private String duration;

	private Date rangeFrom;

	private Date rangeTo;
	
	private Order orderOfTime = Order.ASC;

	private Integer limit;

	private boolean fillNullValues = false;

	private String fillString;

	private AggregateFunction aggregateFunction = AggregateFunction.NOFUNCTION;;

	private String groupByTime;
	
	private List<String> groupByColumns = null;
	
	private Map<String, String> tagsInWhereClause = null;
	
	private String customQuery;
	
	public Query() {
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	@Deprecated
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setMeasurement(String measurementName) {
		this.tableName = measurementName;
	}

	/**
	 * @param columns
	 *            Name of columns
	 */
	@Deprecated
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	/**
	 * Add Column to fetch
	 * 
	 * @param column
	 */
	@Deprecated
	public void addColumn(String column) {
		if (columns == null) {
			columns = new ArrayList<String>();
		}
		columns.add(column);
	}
	
	public void setFields(List<String> fields) {
		this.columns = fields;
	}
	
	public void addField(String fieldKey) {
		if (columns == null) {
			columns = new ArrayList<String>();
		}
		columns.add(fieldKey);
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

	public void setOrderOfTime(Order orderOfTime) {
		this.orderOfTime = orderOfTime;
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
			list.add(Constants.BACKSLASH_QUOTATION + node + Constants.BACKSLASH_QUOTATION);
		}
		return list;
	}

	// create query
	public StringBuffer getQuery() {
		StringBuffer query = new StringBuffer();
		if (customQuery != null) {
			return query.append(customQuery);
		}
		if (aggregateFunction == null || (rangeFrom == null && rangeTo != null)) {
			aggregateFunction = AggregateFunction.NOFUNCTION;
		}
		if (columns != null && columns.size() > 0) {
			List<String> formattedColumns = getColumnsWithDoubleQuotes();

			// select mean("column1")
			query.append(Constants.SELECT_space).append(aggregateFunction.getFunction())
				.append(Constants.OPENING_BRACKET).append(formattedColumns.get(0))
				.append(Constants.CLOSING_BRACKET);

			// , mean("column2") , mean("column3")
			for (int i = 1; i < formattedColumns.size(); i++) {
				query.append(Constants.COMMA_space).append(aggregateFunction.getFunction())
				.append(Constants.OPENING_BRACKET).append(formattedColumns.get(i))
				.append(Constants.CLOSING_BRACKET);
			}
		} else {
			// select *
			query.append(Constants.SELECT_STAR_space);
			// setting aggregate function to null so that it will not add group
			// by time()
			aggregateFunction = null;
		}
		
		// from "tableName"
		if (!isStringNullOrEmpty(tableName)) {
			query.append(Constants.FROM_BACKSLASH_QUOTATION).append(tableName).append(Constants.BACKSLASH_QUOTATION);
		} else {
			query.append(Constants.FROM_BACKSLASH_QUOTATION).append(tables.get(0)).append(Constants.BACKSLASH_QUOTATION);

			for (int i = 1; i < tables.size(); i++) {
				query.append(Constants.COMMA_QUOTATION).append(tables.get(i)).append(Constants.BACKSLASH_QUOTATION);
			}
		}
		
		long from = 0, to = 0;
		boolean whereAlreadyAdded = false;

		if (rangeFrom != null && rangeTo != null) {
			// where time > 123456s and time < 123567s
			whereAlreadyAdded = true;
			from = getFormatted(rangeFrom);
			to = getFormatted(rangeTo);
			query.append(Constants.space_WHERE_TIME_GREATER_THAN_space).append(from)
				.append(Constants.S).append(Constants.space_AND_TIME_LESS_THAN_space)
				.append(to).append(Constants.S);
		} else if (rangeFrom != null) {
			whereAlreadyAdded = true;
			from = getFormatted(rangeFrom);
			to = System.currentTimeMillis() / 1000;
			query.append(Constants.space_WHERE_TIME_GREATER_THAN_space).append(from).append(Constants.S);
		} else if (rangeTo != null) {
			whereAlreadyAdded = true;
			to = getFormatted(rangeTo);
			query.append(Constants.space_WHERE_TIME_LESS_THAN_space).append(to).append(Constants.S);
		} else if (!isStringNullOrEmpty(duration)) {
			whereAlreadyAdded = true;
			// where time > now() - 1h
			query.append(Constants.space_WHERE_TIME_GREATER_THAN_NOW_MINUS_space).append(duration);
		}
		
		// where jobName = 'PI' (ie. Where tagkey1 = 'tagvalue1' and tagkey2 = 'tagvalue2' and tagkey3 = 'tagValue3')
		if (tagsInWhereClause != null && !tagsInWhereClause.isEmpty()) {
			boolean isFirstTag = true;
			if (!whereAlreadyAdded) {
				query.append(Constants.SPACE).append(Constants.WHERE);
			}
			
			for (Entry<String, String> e : tagsInWhereClause.entrySet()) {
				if (whereAlreadyAdded || !isFirstTag) {
					query.append(Constants.SPACE).append(Constants.AND);
				}
				isFirstTag = false;
				query.append(Constants.SPACE).append(e.getKey()).append(Constants.SPACE)
					.append(Constants.EQUAL).append(Constants.SPACE)
					.append(Constants.SINGLE_QUOTE).append(e.getValue())
					.append(Constants.SINGLE_QUOTE);
			}
		}
		
		boolean isGroupByAlreadyAdded = false;

		// group by time(5m)
		if (aggregateFunction != null
			&& !isStringNullOrEmpty(groupByTime)
			&& (!isStringNullOrEmpty(duration) || rangeFrom != null) ) {
				isGroupByAlreadyAdded = true;
				query.append(Constants.space_GROUP_BY_TIME).append(groupByTime).append(Constants.CLOSING_BRACKET);
		}

		if (groupByColumns != null && !groupByColumns.isEmpty()) {
			if (isGroupByAlreadyAdded) {
				query.append(Constants.COMMA);
			} else {
				query.append(Constants.space_GROUP_BY_space);
			}
			Iterator<String> it = groupByColumns.iterator();
			query.append(Constants.BACKSLASH_QUOTATION).append(it.next()).append(Constants.BACKSLASH_QUOTATION);
			while (it.hasNext()) {
				query.append(Constants.COMMA_QUOTATION).append(it.next()).append(Constants.BACKSLASH_QUOTATION);
			}
		}

		// fill(0)
		if (fillNullValues) {
			query.append(Constants.SPACE).append(fillString);
		}
		query.append(Constants.SPACE).append(" order by time ").append(orderOfTime);
		if (limit != null) {
			query.append(Constants.LIMIT).append(limit);
		}
		return query;
	}
	
	private boolean isStringNullOrEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}
		return false;
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

	public void setGroupByColumns(List<String> groupByColumns) {
		this.groupByColumns = groupByColumns;
	}
	
	public void addGroupByColumn(String columnName) {
		if (groupByColumns == null) {
			groupByColumns = new ArrayList<String>(2);
		}
		groupByColumns.add(columnName);
	}

	/**
	 * Where tagkey1 = 'tagvalue1', tagkey2 = 'tagvalue2', tagkey3 = 'tagValue3'
	 * @param tagsInWhereClause
	 */
	public void setTagsInWhereClause(Map<String, String> tagsInWhereClause) {
		this.tagsInWhereClause = tagsInWhereClause;
	}
	
	/**
	 * Where tagkey1 = 'tagvalue1', tagkey2 = 'tagvalue2', tagkey3 = 'tagValue3'
	 * @param tagKey
	 * @param tagValue
	 */
	public void addTagInWhereClause(String tagKey, String tagValue) {
		if (tagsInWhereClause == null) {
			tagsInWhereClause = new HashMap<String, String>(2);
		}
		tagsInWhereClause.put(tagKey, tagValue);
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
	
	public void setMeasurements(List<String> measurements) {
		this.tables = measurements;
	}
	
	public void addMeasurement(String measurementName) {
		if (tables == null) {
			tables = new ArrayList<String>(5);
		}
		tables.add(measurementName);
	}
	
	@Deprecated
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
