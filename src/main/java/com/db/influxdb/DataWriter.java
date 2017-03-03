package com.db.influxdb;

import static com.db.influxdb.Constants.BACKSLASH_QUOTATION;
import static com.db.influxdb.Constants.COMMA;
import static com.db.influxdb.Constants.EQUAL;
import static com.db.influxdb.Constants.INSUFFICIENT_INFORMATION_TO_WRITE_DATA;
import static com.db.influxdb.Constants.SPACE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class DataWriter {

	private Configuration configuration;

	private String tableName;

	private Map<String, Object> fields;
	
	private Map<String, String> tags;

	private TimeUnit timeUnit;

	private Long time;

	public DataWriter(Configuration configuration) throws Exception {
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
			for (Entry<String, String> tag : tags.entrySet()) {
				sb.append(COMMA).append(tag.getKey()).append(EQUAL).append(tag.getValue());
			}
		}
		sb.append(Constants.SPACE);
		Entry<String, Object> e;
		// Adding Columns
		Iterator<Entry<String, Object>> fieldsIterator = fields.entrySet().iterator();
		if (fieldsIterator.hasNext()) {
			e = fieldsIterator.next();
			sb.append(e.getKey()).append(EQUAL).append(parseValue(e.getValue()));
		}
		while (fieldsIterator.hasNext()) {
			e = fieldsIterator.next();
			sb.append(COMMA).append(e.getKey()).append(EQUAL).append(parseValue(e.getValue()));
		}
		sb.append(SPACE);
		if (time != null) {
			sb.append(time);
		} else {
			sb.append(System.currentTimeMillis() / 1000);
		}
		CloseableHttpClient httpClient = null;
		
		try {
			// Sending data
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(getURL());
			httpPost.setEntity(new StringEntity(sb.toString(), ContentType.DEFAULT_BINARY));
			CloseableHttpResponse response = httpClient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != 204) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ResultSet resultSet = new Gson().fromJson(EntityUtils.toString(responseEntity), ResultSet.class);
					String error = resultSet.getError();
					if (error != null && !error.isEmpty()) {
						throw new Exception(error);
					}
				}
			}
		} finally {
			fields.clear();
			if (tags != null) {
				tags.clear();
			}
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}
	
	private String parseValue(Object value) {
		if (value instanceof Integer) {
			return String.valueOf(value) + Constants.I;
		} else if (value instanceof Double) {
			return String.valueOf(value);
		} else if (value instanceof Boolean) {
			return String.valueOf((boolean) value);
		} else {
			return BACKSLASH_QUOTATION + value + BACKSLASH_QUOTATION;
		}
	}
	
	// create and get url to send post request
	private URI getURL() throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder();
		int port = Integer.parseInt(configuration.getPort());
		if (timeUnit == null) {
			timeUnit = TimeUnit.SECONDS;
		}
		uriBuilder.setScheme(configuration.getProtocol()).setHost(configuration.getHost())
				.setPort(port).setPath(Constants.WRITE)
				.setParameter(Constants.DB, configuration.getDatabase())
				.setParameter(Constants.U, configuration.getUsername())
				.setParameter(Constants.P, configuration.getPassword())
				.setParameter(Constants.PRECISION, TimeUtil.toTimePrecision(timeUnit));
		return uriBuilder.build();
	}
	
	public void setMeasurement(String measurementName) {
		this.tableName = measurementName;
	}

	@Deprecated
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

	/**
	 * Set time unit, Default is in seconds.
	 * @param timeUnit
	 */
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