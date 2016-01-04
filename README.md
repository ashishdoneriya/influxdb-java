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

<h3>Maven</h3>
<b>Step 1.</b> Add the JitPack repository to your build file


<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
<

<b>Step 2.</b> Add the dependency
<pre>
<dependency>
	<groupId>com.github.ashishdoneriya</groupId>
	<artifactId>influxdb</artifactId>
	<version>1.0</version>
</dependency>
</pre>

<h3>Gradle</h3>
<b>Step 1.</b> Add it in your root build.gradle at the end of repositories:
<pre>
	allprojects {
		repositories {
			maven { url "https://jitpack.io" }
		}
	}
</pre>
<b>Step 2.</b> Add the dependency
<pre>
	dependencies {
		compile 'com.github.ashishdoneriya:influxdb:1.0'
	}
</pre>

<h3>Build Requirements<h3>
<ul>
<li>Java 1.7+</li>
<li>Maven 3.0+</li>
</ul>

<p>You can build influxdb with all tests with:</p>
<pre>$ mvn clean install</pre>


<p>You can skip tests with -DskipTests flag set to true:</p>
<pre>$ mvn clean install -DskipTests=true</pre>
