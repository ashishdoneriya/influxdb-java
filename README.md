# influxdb
API for influx database to write and fetch data.

<p>Typical usage looks like:</p>
<pre>
// For Writing Data
Configuration configuration = new Configuration("localhost", "8086", "root", "root", "mydb");
DataWriter writer = new DataWriter(configuration);
writer.setTableName("sampleTable");

writer.setTimeUnit(TimeUnit.SECONDS);
writer.addField("column1", 12212);
writer.addField("column2", 22.44);
writer.setTime(System.currentTimeMillis() / 1000);
writer.writeData();

writer.addField("column1", 112);
writer.addField("column2", 21.44);
// If we don not set time it will set automatically
writer.setTime(System.currentTimeMillis() / 1000);
writer.writeData();



// For fetching data
Configuration configuration = new Configuration("localhost", "8086", "root", "root", "mydb");

Query query = new Query();
query.setTableName("sampleTable");
// selects all columns by default, if not specified as below.
query.addColumn("column1");
query.addColumn("column2");

// fetches reaults of last 1 hour. (supported format are d, h, m, s)
// query.setDuration("1h");

// uncomment below line to apply aggregate functions and grouping
// query.setAggregateFunction(AggregateFunction.MEAN);
// query.setGroupByTime("1m");
query.setLimit(1000);
query.fillNullValues("0");

DataReader dataReader = new DataReader(query, configuration);

ResultSet resultSet = dataReader.getResult();
System.out.println(resultSet);</pre>

You can use https://jitpack.io to add influxdb-java to your project.

<h3>Build Requirements</h3>
<ul>
<li>Java 1.7+</li>
<li>Maven 3.0+</li>
</ul>

<p>You can build influxdb with all tests with:</p>
<pre>$ mvn clean install</pre>


<p>You can skip tests with -DskipTests flag set to true:</p>
<pre>$ mvn clean install -DskipTests=true</pre>

<p>For more details http://csetutorials.com/fetch-and-write-influxdb-data-using-java.html</p>
