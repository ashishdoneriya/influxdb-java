package com.db.influxdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

public class DataReader {

	private Query query;

	private Configuration configuration;

	public DataReader() {
	}

	public DataReader(Query query, Configuration configuration) {
		this.setQuery(query);
		this.setConfiguration(configuration);
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public String getURL() {

		String username = configuration.getUsername();
		String password = configuration.getPassword();
		StringBuffer url = new StringBuffer();
		url.append("http://").append(configuration.getHost()).append(":").append(configuration.getPort())
				.append("/query?db=").append(configuration.getDatabase());

		if (username != null && password != null) {
			url.append("&u=").append(username).append("&p=").append(password);
		}
		url.append("&q=").append(query);
		return url.toString();
	}

	private String sendGetRequest(String url) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(getURL()).build();
		InputStream in = client.newCall(request).execute().body().byteStream();
		BufferedReader reader = null;
		String output = "";
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			output = output + reader.readLine();

		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return output;
	}

	public ResultSet getResult() throws IOException {
		String url = getURL();
		String json = sendGetRequest(url);
		Type type = new TypeToken<ResultSet>() {
		}.getType();
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}

}
