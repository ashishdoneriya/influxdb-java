package com.db.influxdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

	private URI getURL() throws URISyntaxException {

		URIBuilder uriBuilder = new URIBuilder();
		int port = Integer.parseInt(configuration.getPort());
		uriBuilder.setScheme("http").setHost(configuration.getHost()).setPort(port).setPath("/query")
				.setParameter("db", configuration.getDatabase())
				.setParameter("u", configuration.getUsername())
				.setParameter("p", configuration.getPassword())
				.setParameter("q", query.toString());
		return uriBuilder.build();
	}

	private String sendGetRequest(URI url) throws IOException {
		HttpGet httpget = new HttpGet(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(httpget);

		InputStream in = response.getEntity().getContent();
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
		httpclient.close();
		return output;
	}

	public ResultSet getResult() throws IOException, URISyntaxException {
		URI url = getURL();
		String json = sendGetRequest(url);
		Type type = new TypeToken<ResultSet>() {
		}.getType();
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}

}
