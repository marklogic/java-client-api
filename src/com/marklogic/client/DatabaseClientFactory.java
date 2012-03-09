package com.marklogic.client;

import javax.net.ssl.SSLContext;

import com.marklogic.client.iml.DatabaseClientImpl;
import com.marklogic.client.iml.JerseyServices;
import com.marklogic.client.iml.RESTServices;

public class DatabaseClientFactory {
	public enum Authentication {
	    BASIC, DIGEST, NONE;
	}

	private DatabaseClientFactory() {
	}

	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type) {
		RESTServices services = new JerseyServices();
		services.connect(host, port, user, password, type);

		return new DatabaseClientImpl(services);		
	}
	static public DatabaseClient connect(String host, int port, String user, String password, SSLContext context) {
		// TODO
		return null;		
	}
}
