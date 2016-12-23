package com.db.influxdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
		uriBuilder.setScheme(Constants.HTTP).setHost(configuration.getHost())
				.setPort(port).setPath(Constants.QUERY)
				.setParameter(Constants.DB, configuration.getDatabase())
				.setParameter(Constants.U, configuration.getUsername())
				.setParameter(Constants.P, configuration.getPassword())
				.setParameter(Constants.Q, query.toString());
		return uriBuilder.build();
	}

	private String sendGetRequest(URI url) throws IOException {
		HttpGet httpget = new HttpGet(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(httpget);

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = null;
		String output = Constants.EMPTY_STRING;
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
		
		return Utilities.gson.fromJson(json, Utilities.resultSetType);
	}

}
