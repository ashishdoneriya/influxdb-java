package com.db.influxdb;

public interface Constants {

	// Constants used in InfluxDataReader class

	char T = 'T';
	char S = 's';
	char M = 'm';
	char H = 'h';
	char D = 'd';
	char AND = '&';
	char COLON = ':';
	char SPACE = ' ';
	char COMMA = ',';
	char EQUAL = '=';
	char OPENING_BRACKET = '(';
	char CLOSING_BRACKET = ')';

	String Z = "Z";
	String TIME = "time";
	String HTTP = "http://";
	String NEW_LINE = "\n";
	String QUERY_DB = "/query?db=";
	String COMMA_space = ", ";
	String SELECT_space = "select ";
	String EMPTY_STRING = "";
	String DOUBLE_COLON = "\"";
	String space_FILL_0 = " fill(0)";
	String AND_Q_EQUAL_TO = "&q=";
	String YYYY_MM_DD_HH_MM = "yyyy/MM/dd HH:mm";
	String SELECT_STAR_space = "select * ";
	String space_GROUP_BY_TIME = " group by time(";
	String BACKSLASH_QUOTATION = "\"";
	String CLOSING_BRACKET_space = ") ";
	String INFLUX_DB_CONF_IS_NULL = "Configuration is null";
	String TABLE_NAME_NOT_SPECIFIED = "Table name not specified";
	String FROM_BACKSLASH_QUOTATION = "from \"";
	String YYYY_MM_DD_space_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	String space_AND_TIME_LESS_THAN_space = " and time < ";
	String space_WHERE_TIME_LESS_THAN_space = "where time < ";
	String space_WHERE_TIME_GREATER_THAN_space = " where time > ";
	String ERROR_WHILE_FETCHING_DATA_FROM_INFLUX_DB = "Error while fetching data from influxDB";
	String space_WHERE_TIME_GREATER_THAN_NOW_MINUS_space = " where time > now() - ";

	// Constants used in InfluxDataWriter class

	String WRITE_U = "/write?u=";
	String AND_U_EQUAL_TO = "&u=";
	String AND_P_EQUAL_TO = "&p=";
	String AND_DB_EQUAL_TO = "&db=";
	String AND_PRECISION_EQUAL_TO = "&precision=";
	String NEWlINE_JSON_EQUAL_TO = "\njson=";
	String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
	String SENDING_POST_REQUEST_TO_INFLUX_DB_URL = "Sending post request to influxDB...\nURL=";
	String INSUFFICIENT_INFORMATION_TO_WRITE_DATA = "Insufficient information to write data";
	String ERROR_WHILE_SENDING_POST_REQUEST_TO_INFLUXDB = "Error while sending post request to influxdb, ";
}
