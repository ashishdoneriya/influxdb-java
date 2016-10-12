package com.db.influxdb;

import static com.db.influxdb.Constants.AND_DB_EQUAL_TO;
import static com.db.influxdb.Constants.AND_P_EQUAL_TO;
import static com.db.influxdb.Constants.AND_PRECISION_EQUAL_TO;
import static com.db.influxdb.Constants.COLON;
import static com.db.influxdb.Constants.COMMA;
import static com.db.influxdb.Constants.DOUBLE_COLON;
import static com.db.influxdb.Constants.EQUAL;
import static com.db.influxdb.Constants.HTTP;
import static com.db.influxdb.Constants.INFLUX_DB_CONF_IS_NULL;
import static com.db.influxdb.Constants.INSUFFICIENT_INFORMATION_TO_WRITE_DATA;
import static com.db.influxdb.Constants.SPACE;
import static com.db.influxdb.Constants.WRITE_U;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.http.MessageConstraintException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DataWriter {

	private Configuration configuration;

	private String tableName;

	private Map<String, Object> fields;
	
	private Map<String, String> tags;

	private TimeUnit timeUnit;

	private Long time;

	public DataWriter(Configuration configuration) throws Exception {
		if (configuration == null) {
			throw new Exception(INFLUX_DB_CONF_IS_NULL);
		}
		this.configuration = configuration;
	}

	public void writeData() throws Exception {
		if (tableName == null || tableName.isEmpty() || fields == null || fields.isEmpty()) {
			throw new Exception(INSUFFICIENT_INFORMATION_TO_WRITE_DATA);
		}
		// Sending data in the format of
		// tableName,tag_key1=tag_value1,tag_key2=tag_value2 column1=value1,column2=value2,column3=value3 timestamp
		StringBuffer sb = new StringBuffer(tableName);
		
		// Adding Tags
		if (tags != null && !tags.isEmpty()) {
			for (Entry<String, String> e : tags.entrySet()) {
				sb.append(COMMA).append(e.getKey()).append(EQUAL).append(e.getValue());
			}
		}
		sb.append(' ');
		Entry<String, Object> e;
		// Adding Columns
		Iterator<Entry<String, Object>> it = fields.entrySet().iterator();
		if (it.hasNext()) {
			e = it.next();
			sb.append(e.getKey()).append(EQUAL).append(parseValue(e.getValue()));
		}
		while (it.hasNext()) {
			e = it.next();
			sb.append(COMMA).append(e.getKey()).append(EQUAL).append(parseValue(e.getValue()));
		}
		sb.append(SPACE);
		if (time != null) {
			sb.append(time);
		} else {
			sb.append(System.currentTimeMillis() / 1000);
		}
		// Sending data
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(getURL());
		httpPost.setEntity(new StringEntity(sb.toString(), ContentType.DEFAULT_BINARY));
		CloseableHttpResponse response = httpClient.execute(httpPost);
		httpClient.close();
		fields.clear();
		if (tags != null) {
			tags.clear();
		}
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() != 204) {
			throw new Exception("Unable to write data. " + statusLine);
		}
	}
	
	private String parseValue(Object value) {
		if (value instanceof Integer) {
			return String.valueOf(value) + 'i';
		} else if (value instanceof Double) {
			return String.valueOf(value);
		} else if (value instanceof Boolean) {
			return String.valueOf((boolean) value);
		} else {
			return DOUBLE_COLON + value + DOUBLE_COLON;
		}
	}

	// create and get url to send post request
	private String getURL() throws Exception {
		if (timeUnit == null) {
			setTimeUnit(TimeUnit.SECONDS);
		}
		String host = configuration.getHost();
		String port = configuration.getPort();
		String username = configuration.getUsername();
		String password = configuration.getPassword();
		String database = configuration.getDatabase();
		StringBuffer url = new StringBuffer();
		url.append(HTTP).append(host.trim()).append(COLON).append(port).append(WRITE_U)
				.append(username).append(AND_P_EQUAL_TO).append(password).append(AND_DB_EQUAL_TO)
				.append(database).append(AND_PRECISION_EQUAL_TO).append(TimeUtil.toTimePrecision(timeUnit));
		return url.toString();
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
	
	public void addTag(String tagKey, String tagValue) {
		if (tags == null) {
			tags = new HashMap<String, String>();
		}
		tags.put(tagKey, tagValue);
	}

	public void setTime(Long time, TimeUnit timeUnit) {
		this.time = time;
		this.setTimeUnit(timeUnit);
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public void addField(String columnName, Object value) {
		if (fields == null) {
			fields = new HashMap<String, Object>();
		}
		fields.put(columnName, value);
	}

}