# influxdb
API for influx database to fetch data.

This is the Java Client library which is only compatible with InfluxDB 0.9 and higher.
Typical usage looks like:
<pre>

// For fetching data
Configuration configuration = new Configuration("localhost", "8086", "root", "root", "mydb");

Query query = new Query();
query.setTableName("table");
query.addColumn("column1");
query.addColumn("column2");
query.setAggregateFunction(AggregateFunction.MEAN);
query.setDuration("1h");
query.setGroupByTime("1m");
query.setLimit(1000);
query.fillNullValues("0");

DataReader dataReader = new DataReader(query, configuration);

ResultSet resultSet = dataReader.getResult();
</pre>
