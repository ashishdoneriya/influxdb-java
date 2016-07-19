package com.db.influxdb.Testing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import com.db.influxdb.Configuration;
import com.db.influxdb.DataReader;
import com.db.influxdb.DataWriter;
import com.db.influxdb.Query;
import com.db.influxdb.ResultSet;

public class Test {
	public static void main(String[] args) throws Exception {
		writeData();
		readData();
	}

	public static void writeData() throws Exception {
		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "mydb");
		DataWriter writer = new DataWriter(configuration);
		writer.setTableName("sampleTable1");
		writer.setTimeUnit(TimeUnit.SECONDS);
		
		writer.addField("column1", 12212);
		writer.addField("column2", 22.44);
		writer.addField("column3", "thisIsString");
		writer.addField("column4", false);
		
		
		// If we don not set time it will set automatically
		writer.setTime(System.currentTimeMillis() / 1000);
		writer.writeData();

		writer.addField("column1", 112);
		writer.addField("column2", 21.44);
		writer.addField("column3", "thisIsString1");
		writer.addField("column4", true);
		
		// Influxdb saves one point at one time. Therefore we have to add another point at another time.
		writer.setTime(System.currentTimeMillis() / 1000 + 1);
		writer.writeData();

	}

	public static void readData() throws IOException, URISyntaxException {
		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "mydb");

		Query query = new Query();
		query.setTableName("sampleTable1");
		// selects all columns by default, if not specified as below.
		query.addColumn("column1");
		query.addColumn("column2");
		query.addColumn("column3");
		query.addColumn("column4");
		// fetches reaults of last 1 hour. (supported format are d, h, m, s)
		// query.setDuration("1h");

		// uncomment below line to apply aggregate functions and grouping
		// query.setAggregateFunction(AggregateFunction.MEAN);
		// query.setGroupByTime("1m");
		query.setLimit(1000);
		query.fillNullValues("0");

		DataReader dataReader = new DataReader(query, configuration);

		ResultSet resultSet = dataReader.getResult();
		System.out.println(resultSet);
	}
}
