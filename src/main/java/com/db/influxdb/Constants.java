package com.db.influxdb;

public interface Constants {

	// Constants used in InfluxDataReader class

	char I = 'i';
	char S = 's';
	char SPACE = ' ';
	char COMMA = ',';
	char EQUAL = '=';
	char SINGLE_QUOTE = '\'';
	char OPENING_BRACKET = '(';
	char CLOSING_BRACKET = ')';

	String Q = "q";
	String P = "p";
	String U = "u";
	String DB = "db";
	String AND = "and";
	String HTTP = "http";
	String PING = "/ping";
	String LIMIT = " limit ";
	String QUERY = "/query";
	String WHERE = "where";
	String WRITE = "/write";
	String PRECISION = "precision";
	String COMMA_space = ", ";
	String SELECT_space = "select ";
	String EMPTY_STRING = "";
	String COMMA_QUOTATION = ", \"";
	String SELECT_STAR_space = "select * ";
	String space_GROUP_BY_TIME = " group by time(";
	String BACKSLASH_QUOTATION = "\"";
	String space_GROUP_BY_space = " group by ";
	String FROM_BACKSLASH_QUOTATION = " from \"";
	String space_AND_TIME_LESS_THAN_space = " and time < ";
	String space_WHERE_TIME_LESS_THAN_space = "where time < ";
	String space_WHERE_TIME_GREATER_THAN_space = " where time > ";
	String INSUFFICIENT_INFORMATION_TO_WRITE_DATA = "Insufficient information to write data";
	String space_WHERE_TIME_GREATER_THAN_NOW_MINUS_space = " where time > now() - ";

}
