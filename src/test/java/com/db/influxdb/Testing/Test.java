package com.db.influxdb.Testing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import com.db.influxdb.Configuration;
import com.db.influxdb.DataReader;
import com.db.influxdb.DataWriter;
import com.db.influxdb.Query;
import com.db.influxdb.ResultSet;
import com.db.influxdb.Utilities;

public class Test {
	public static void main(String[] args) throws Exception {
		initiate();
		writeData();
		readData();
	}
	
	public static void initiate() throws Exception {
		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "mydb");
		Utilities utilities = new Utilities();
		utilities.createDatabase(configuration);
		utilities.createRetentionPolicy(configuration, "customPolicy", 30, 1, true);
		System.out.println(utilities.isInfluxdbAlive(configuration));
	}

	public static void writeData() throws Exception {
		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "mydb");
		DataWriter writer = new DataWriter(configuration);
		writer.setMeasurement("sampleMeasurement1");
		
		// Default is in seconds
		writer.setTimeUnit(TimeUnit.MILLISECONDS);

		writer.addField("field1", 12212);
		writer.addField("field2", 22.44);
		writer.addField("field3", "thisIsString");
		writer.addField("field4", false);
		writer.addTag("hostname", "server001");

		// If we'll set time it will set automatically
		writer.setTime(System.currentTimeMillis());
		writer.writeData();

		writer.addField("field1", 112);
		writer.addField("field2", 21.44);
		writer.addField("field3", "thisIsString1");
		writer.addField("field4", true);
		// Influxdb saves one point at one time. To add another point at same
		// time we can use tags otherwise it will override the previous point.
		writer.addTag("disk_type", "HDD");
		writer.setTime(System.currentTimeMillis());
		
		writer.writeData();

	}

	public static void readData() throws IOException, URISyntaxException {
		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "mydb");

		Query query = new Query();
		query.setMeasurement("sampleMeasurement1");
		// selects all fields by default, if not specified as below.
		query.addField("field1");
		query.addField("field2");
		query.addField("field3");
		query.addTagInWhereClause("hostname", "server001");
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

		Query query1 = new Query();
		query1.setCustomQuery("select * from sampleMeasurement1");
		dataReader.setQuery(query1);
		resultSet = dataReader.getResult();
		System.out.println(resultSet);
	}
}
