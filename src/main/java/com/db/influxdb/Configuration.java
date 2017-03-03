package com.db.influxdb;

public class Configuration {
	
	private String protocol = Constants.HTTP;

	private String host;

	private String port;

	private String username;

	private String password;

	private String database;

	public Configuration(String host, String port, String username, String password, String database) {
		this.host = host.toLowerCase();
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
	}
	
	public Configuration(String protocol, String host, String port, String username, String password, String database) {
		this(host, port, username, password, database);
		this.protocol = protocol;
	}

	public String getProtocol() {
		return protocol;
	}

	/**
	 * Set protocol/schema (http or https). Default is http
	 * @param protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol.toLowerCase();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

}
