package com.marklogic.client.iml;

import javax.net.ssl.SSLContext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;

public class DatabaseClientFactoryImpl extends DatabaseClientFactory {
	public DatabaseClientFactoryImpl() {
	}

	public DatabaseClient connect(String host, int port, String user, String password, Authentication type) {
		RESTServices services = new JerseyServices();
		services.connect(host, port, user, password, type);

		return new DatabaseClientImpl(services);
	}
	public DatabaseClient connect(String host, int port, String user, String password, SSLContext context) {
		// TODO
		return null;
	}
}
