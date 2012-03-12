package com.marklogic.client;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.iml.DatabaseClientImpl;
import com.marklogic.client.iml.JerseyServices;
import com.marklogic.client.iml.RESTServices;

public class DatabaseClientFactory {
	static final private Logger logger = LoggerFactory.getLogger(DatabaseClientFactory.class);

	public enum Authentication {
	    BASIC, DIGEST, NONE;
	}

	private DatabaseClientFactory() {
	}

	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type) {
		if (logger.isInfoEnabled())
			logger.info("Connecting to {} at {} as {}",new Object[]{host,port,user});

		RESTServices services = new JerseyServices();
		services.connect(host, port, user, password, type);

		return new DatabaseClientImpl(services);		
	}
	static public DatabaseClient connect(String host, int port, String user, String password, SSLContext context) {
		// TODO
		return null;		
	}
}
