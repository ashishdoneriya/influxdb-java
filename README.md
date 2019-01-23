# influxdb
Java API for influx database to write and fetch data.

Typical usage looks like:


### For Writing Data
```java
Configuration configuration = new Configuration("localhost", "8086",
    "root", "root", "Localhost");
//configuration.setProtocol("http");
DataWriter writer = new DataWriter(configuration);
writer.setMeasurement("sampleMeasurement1");

// Default is in seconds
writer.setTimeUnit(TimeUnit.MILLISECONDS);

writer.addField("field1", 12212);
writer.addField("field2", 22.44);
writer.addField("field3", "thisIsString");
writer.addField("field4", false);
writer.addTag("hostname", "server001");

// If we'll not set time, it will set automatically
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
```

### For Fetching Data
```java
Configuration configuration = new Configuration("localhost", "8086",
    "root", "root", "Localhost");

Query query = new Query();
query.setMeasurement("sampleMeasurement1");
// selects all fields by default, if not specified as below.
query.addField("field1");
query.addField("field2");
query.addField("field3");
query.addTagInWhereClause("hostname", "server001");
// fetches results of last 1 hour. (supported format are d, h, m, s)
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
```

### Other Utilities
```java
Configuration configuration = new Configuration("localhost", "8086",
    "root", "root", "Localhost");

Utilities utilities = new Utilities();
// For creating database
utilities.createDatabase(configuration);

// For creating retention policy
utilities.createRetentionPolicy(configuration, "customPolicy", 30, 1, true);

utilities.dropMeasurement(configuration, "measurement1");

System.out.println(utilities.isInfluxdbAlive(configuration));
```

### Build Requirements

*   Java 1.7+
*   Maven 3.0+

You can build influxdb with:

<pre>$ mvn clean install -DskipTests=true</pre>

You can use [jitpack](https://jitpack.io/#ashishdoneriya/influxdb-java/3.0) to add influxdb-java to your project or directly [download jar file](https://github.com/ashishdoneriya/influxdb-java/releases/download/3.0/influxdb-3.0.jar) 

For more details http://csetutorials.com/fetch-and-write-influxdb-data-using-java.html
