/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @since 7.1.0; copied from marklogic-spark-connector repository.
 */
public class ConnectionString {

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String database;

	public ConnectionString(String connectionString, String optionNameForErrorMessage) {
		final String errorMessage = String.format(
			"Invalid value for %s; must be username:password@host:port/optionalDatabaseName",
			optionNameForErrorMessage
		);

		String[] parts = connectionString.split("@");
		if (parts.length != 2) {
			throw new IllegalArgumentException(errorMessage);
		}
		String[] tokens = parts[0].split(":");
		if (tokens.length != 2) {
			throw new IllegalArgumentException(errorMessage);
		}
		this.username = decodeValue(tokens[0], "username");
		this.password = decodeValue(tokens[1], "password");

		tokens = parts[1].split(":");
		if (tokens.length != 2) {
			throw new IllegalArgumentException(errorMessage);
		}
		this.host = tokens[0];
		if (tokens[1].contains("/")) {
			tokens = tokens[1].split("/");
			this.port = parsePort(tokens[0], optionNameForErrorMessage);
			this.database = tokens[1];
		} else {
			this.port = parsePort(tokens[1], optionNameForErrorMessage);
			this.database = null;
		}
	}

	private int parsePort(String value, String optionNameForErrorMessage) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format(
				"Invalid value for %s; port must be numeric, but was '%s'", optionNameForErrorMessage, value
			));
		}
	}

	private String decodeValue(String value, String label) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(String.format("Unable to decode '%s'; cause: %s", label, e.getMessage()));
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDatabase() {
		return database;
	}
}
