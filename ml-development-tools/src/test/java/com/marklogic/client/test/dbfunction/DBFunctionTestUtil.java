package com.marklogic.client.test.dbfunction;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;

public class DBFunctionTestUtil {

	// used for test endpoints that inspect request and generate response
	public final static DatabaseClient db = makeTestClientImpl(
		new DatabaseClientFactory.DigestAuthContext("rest-reader", "x")
	);

	// used for test endpoints that need an elevated privilege
	public final static DatabaseClient adminDb = makeTestClientImpl(
		new DatabaseClientFactory.DigestAuthContext("admin", "admin")
	);

	public static DatabaseClient makeAdminClient(String database) {
		return makeClientImpl(new DatabaseClientFactory.DigestAuthContext("admin", "admin"), 8000, database);
	}

	private static DatabaseClient makeTestClientImpl(DatabaseClientFactory.DigestAuthContext auth) {
		return makeClientImpl(auth, 8013, null);
	}

	private static DatabaseClient makeClientImpl(DatabaseClientFactory.SecurityContext auth, int defaultPort, String database) {
		String host = System.getProperty("TEST_HOST", "localhost");
		int port = Integer.parseInt(System.getProperty("TEST_PORT", Integer.toString(defaultPort)));

		return database != null ?
			DatabaseClientFactory.newClient(host, port, database, auth) :
			DatabaseClientFactory.newClient(host, port, auth);
	}
}
