package com.marklogic.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.impl.JerseyServices;
import com.marklogic.client.impl.RESTServices;

/**
 * A Database Client Factory connects to a database to create a database client.
 */
public class DatabaseClientFactory {
	static final private Logger logger = LoggerFactory.getLogger(DatabaseClientFactory.class);

	public enum Authentication {
	    BASIC, DIGEST, NONE;
	}

	private DatabaseClientFactory() {
	}

	/**
	 * Connects to a database, creating a client to access the database.
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @param type
	 * @return
	 */
	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type) {
		return connect(host, port, user, password, type, null);		
	}
	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type, SSLContext context) {
		return connect(host, port, user, password, type, context, null);		
	}
	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type, SSLContext context, HostnameVerifier verifier) {
		RESTServices services = new JerseyServices();
		services.connect(host, port, user, password, type, context, verifier);

		return new DatabaseClientImpl(services);		
	}
}
