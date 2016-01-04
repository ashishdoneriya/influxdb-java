# influxdb
API for influx database to fetch data.

<p>This is the Java Client library which is only compatible with InfluxDB 0.9 and higher.
Typical usage looks like:</p>
<pre>// For fetching data
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

ResultSet resultSet = dataReader.getResult();</pre>

You can use https://jitpack.io to add influxdb-java to your project.

<h3>Build Requirements<h3>
<ul>
<li>Java 1.7+</li>
<li>Maven 3.0+</li>
</ul>

<p>You can build influxdb with all tests with:</p>
<pre>$ mvn clean install</pre>


<p>You can skip tests with -DskipTests flag set to true:</p>
<pre>$ mvn clean install -DskipTests=true</pre>
