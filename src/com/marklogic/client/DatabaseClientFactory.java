package com.marklogic.client;

import javax.net.ssl.SSLContext;

import com.marklogic.client.iml.DatabaseClientFactoryImpl;

abstract public class DatabaseClientFactory {
	public enum Authentication {
	    BASIC, DIGEST, NONE;
	}

	public static DatabaseClientFactory newFactory() {
		// TODO: check properties on CLASSPATH for override before defaulting
		return new DatabaseClientFactoryImpl();
	}

	abstract public DatabaseClient connect(String host, int port, String user, String password, Authentication type);
	abstract public DatabaseClient connect(String host, int port, String user, String password, SSLContext context);
}
