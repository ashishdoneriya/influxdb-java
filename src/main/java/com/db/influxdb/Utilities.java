package com.db.influxdb;

import java.lang.reflect.Type;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Utilities {

	public static Gson gson = new Gson();

	public static Type resultSetType = new TypeToken<ResultSet>() {
	}.getType();

	public void createDatabase(Configuration configuration) throws Exception {

		URIBuilder uriBuilder = new URIBuilder();
		int port = Integer.parseInt(configuration.getPort());

		String query = "q=create database \"" + configuration.getDatabase() + Constants.BACKSLASH_QUOTATION;
		uriBuilder.setScheme(configuration.getProtocol())
					.setHost(configuration.getHost())
					.setPort(port)
					.setPath(Constants.QUERY);

		HttpPost httpPost = new HttpPost(uriBuilder.build());
		httpPost.setEntity(new StringEntity(query, ContentType.APPLICATION_FORM_URLENCODED));
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != 204) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ResultSet resultSet = gson.fromJson(
							EntityUtils.toString(responseEntity), ResultSet.class);
					String error = resultSet.getError();
					if (error != null && !error.isEmpty()) {
						throw new Exception(error);
					}
				}
			}
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	public void dropDatabase(Configuration configuration) throws Exception {

		URIBuilder uriBuilder = new URIBuilder();
		int port = Integer.parseInt(configuration.getPort());

		String query = "drop database \"" + configuration.getDatabase() + Constants.BACKSLASH_QUOTATION;
		uriBuilder.setScheme(configuration.getProtocol())
			.setHost(configuration.getHost())
			.setPort(port)
			.setPath(Constants.QUERY)
			.setParameter(Constants.Q, query);

		HttpGet httpGet = new HttpGet(uriBuilder.build());
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != 204) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ResultSet resultSet = gson.fromJson(
							EntityUtils.toString(responseEntity), ResultSet.class);
					String error = resultSet.getError();
					if (error != null && !error.isEmpty()) {
						throw new Exception(error);
					}
				}
			}
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	/**
	 * Retention period is the duration for which the old records will be
	 * deleted. Old records means records older than specified retention period
	 * / days).
	 * 
	 * @param configuration
	 * @param retentionPolicyName
	 *            name of retention policy
	 * @param retentionPeriod
	 *            Retention Period is in days. If 0 then it means infinity
	 * @param replication
	 *            REPLICATION clause determines how many independent copies of
	 *            each point are stored in the cluster, where n is the number of
	 *            data nodes. (pass 1 or null if you don't know)
	 * @param setDefault
	 *            Sets the new retention policy as the default retention policy
	 *            for the database.
	 * @throws Exception
	 */
	public void createRetentionPolicy(Configuration configuration,
			String retentionPolicyName, int retentionPeriod,
			Integer replication, Boolean setDefault) throws Exception {

		if (replication == null) {
			replication = 1;
		}
		
		URIBuilder uriBuilder = new URIBuilder();
		int port = Integer.parseInt(configuration.getPort());

		String query = "CREATE RETENTION POLICY \"" + retentionPolicyName + "\" ON \"" + configuration.getDatabase()
				+ "\" DURATION " + retentionPeriod + "d REPLICATION " + replication;
		if (setDefault != null && setDefault == true) {
			query = query + " DEFAULT";
		}
		uriBuilder.setScheme(configuration.getProtocol())
				.setHost(configuration.getHost())
				.setPort(port)
				.setPath(Constants.QUERY)
				.setParameter(Constants.U, configuration.getUsername())
				.setParameter(Constants.P, configuration.getPassword())
				.setParameter(Constants.Q, query);

		HttpGet httpGet = new HttpGet(uriBuilder.build());
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != 204) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ResultSet resultSet = gson.fromJson(
							EntityUtils.toString(responseEntity), ResultSet.class);
					String error = resultSet.getError();
					if (error != null && !error.isEmpty()) {
						throw new Exception(error);
					}
				}
			}
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	/**
	 * Updates retention policy
	 * 
	 * @param configuration
	 * @param retentionPolicyName
	 *            name of retention policy
	 * @param retentionPeriod
	 *            Retention Period is in days. If 0 then it means infinity
	 * @param replication
	 *            REPLICATION clause determines how many independent copies of
	 *            each point are stored in the cluster, where n is the number of
	 *            data nodes. (pass 1 or null if you don't know)
	 * @param setDefault
	 *            Sets the new retention policy as the default retention policy
	 *            for the database.
	 * @throws Exception
	 */
	public void updateRetentionPolicy(Configuration configuration,
			String retentionPolicyName, int retentionPeriod,
			Integer replication, Boolean setDefault) throws Exception {
		if (replication == null) {
			replication = 1;
		}

		URIBuilder uriBuilder = new URIBuilder();
		int port = Integer.parseInt(configuration.getPort());

		String query = "ALTER RETENTION POLICY \"" + retentionPolicyName + "\" ON \"" + configuration.getDatabase()
				+ "\" DURATION " + retentionPeriod + "d REPLICATION " + replication;
		
		if (setDefault != null && setDefault == true) {
			query = query + " DEFAULT";
		}
		uriBuilder.setScheme(configuration.getProtocol())
				.setHost(configuration.getHost())
				.setPort(port)
				.setPath(Constants.QUERY)
				.setParameter(Constants.U, configuration.getUsername())
				.setParameter(Constants.P, configuration.getPassword())
				.setParameter(Constants.Q, query);

		HttpGet httpGet = new HttpGet(uriBuilder.build());
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != 204) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ResultSet resultSet = gson.fromJson(
							EntityUtils.toString(responseEntity), ResultSet.class);
					String error = resultSet.getError();
					if (error != null && !error.isEmpty()) {
						throw new Exception(error);
					}
				}
			}
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	/**
	 * Drop measurement from the database
	 * 
	 * @param configuration
	 * @param measurementName
	 *            mesurement which you want to delete
	 * @throws Exception
	 */
	public void dropMeasurement(Configuration configuration, String measurementName) throws Exception {

		URIBuilder uriBuilder = new URIBuilder();
		int port = Integer.parseInt(configuration.getPort());

		String query = "drop measurement \"" + measurementName + Constants.BACKSLASH_QUOTATION;
		uriBuilder.setScheme(configuration.getProtocol())
				.setHost(configuration.getHost())
				.setPort(port)
				.setPath(Constants.QUERY)
				.setParameter(Constants.U, configuration.getUsername())
				.setParameter(Constants.P, configuration.getPassword())
				.setParameter(Constants.DB, configuration.getDatabase())
				.setParameter(Constants.Q, query);

		HttpGet httpGet = new HttpGet(uriBuilder.build());
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != 204) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ResultSet resultSet = gson.fromJson(
							EntityUtils.toString(responseEntity), ResultSet.class);
					String error = resultSet.getError();
					if (error != null && !error.isEmpty()) {
						throw new Exception(error);
					}
				}
			}
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	public boolean isInfluxdbAlive(Configuration configuration) throws Exception {
		URIBuilder uriBuilder = new URIBuilder();
		int port = Integer.parseInt(configuration.getPort());

		uriBuilder.setScheme(configuration.getProtocol())
				.setHost(configuration.getHost())
				.setPort(port)
				.setPath(Constants.PING);
		CloseableHttpClient httpClient = null;
		try {
			HttpGet httpGet = new HttpGet(uriBuilder.build());
			httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(httpGet);
			Header[] headers = response.getHeaders("X-Influxdb-Version");
			if (headers == null || headers.length == 0) {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
		return true;
	}

}
