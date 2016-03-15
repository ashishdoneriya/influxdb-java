package com.db.influxdb;

import static com.db.influxdb.Constants.AND_P_EQUAL_TO;
import static com.db.influxdb.Constants.APPLICATION_JSON_CHARSET_UTF_8;
import static com.db.influxdb.Constants.COLON;
import static com.db.influxdb.Constants.EMPTY_STRING;
import static com.db.influxdb.Constants.HTTP;
import static com.db.influxdb.Constants.INFLUX_DB_CONF_IS_NULL;
import static com.db.influxdb.Constants.INSUFFICIENT_INFORMATION_TO_WRITE_DATA;
import static com.db.influxdb.Constants.WRITE_U;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class DataWriter {

	private Configuration configuration;
	
	private String tableName;
	
	private Map<String, Object> fields;
	
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
		if (timeUnit == null) {
			setTimeUnit(TimeUnit.SECONDS);
		}
		Point point = new Point(tableName);
		point.setFields(fields);
		point.setPrecision(TimeUtil.toTimePrecision(timeUnit));
		point.setTime(time);
		BatchPoints batchPoints = new BatchPoints();
		batchPoints.addPoint(point);
		batchPoints.setDatabase(configuration.getDatabase());
		sendData(batchPoints);
		fields = null;
	}

	// create and get url to send post request
	private String getURL() throws Exception {
		String host = configuration.getHost();
		String port = configuration.getPort();
		String username = configuration.getUsername();
		String password = configuration.getPassword();

		StringBuffer url = new StringBuffer();
		url.append(HTTP).append(host.trim()).append(COLON).append(port).append(WRITE_U).append(username)
				.append(AND_P_EQUAL_TO).append(password);
		return url.toString();
	}

	// send post request
	private void sendData(BatchPoints batchPoints) throws Exception {
		Gson gson = new Gson();
		String json = gson.toJson(batchPoints);
		String url = getURL();

		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(MediaType.parse(APPLICATION_JSON_CHARSET_UTF_8), json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();

		BufferedReader reader = null;
		String line = EMPTY_STRING;
		StringBuffer reponseJSON = new StringBuffer();
		if (response != null) {
			try {
				reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
				while ((line = reader.readLine()) != null) {
					reponseJSON.append(line);
				}
				String temp = reponseJSON.toString();
				if (!temp.trim().isEmpty()) {
					throw new Exception(temp);
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
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