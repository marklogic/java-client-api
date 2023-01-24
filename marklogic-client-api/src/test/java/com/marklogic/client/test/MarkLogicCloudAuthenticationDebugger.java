package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.JacksonHandle;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * We don't yet have a way to run tests against a MarkLogic Cloud instance. In the meantime, this program and its
 * related Gradle task can be used for easy manual testing.
 */
public class MarkLogicCloudAuthenticationDebugger {

	public static void main(String[] args) throws Exception {
		String cloudHost = args[0];
		String apiKey = args[1];
		String basePath = args[2];
		int port = 443;
		String database = null;
		DatabaseClient.ConnectionType connectionType = null;
		X509TrustManager trustManager = Common.TRUST_ALL_MANAGER;
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(null, new TrustManager[]{trustManager}, null);

		DatabaseClient client = DatabaseClientFactory.newClient(
			cloudHost, port, basePath, database,
			new DatabaseClientFactory.MarkLogicCloudAuthContext(apiKey)
				.withSSLContext(sslContext, trustManager)
			, connectionType);

		DatabaseClient.ConnectionResult result = client.checkConnection();
		if (result.getStatusCode() != 0) {
			throw new RuntimeException("Unable to connect: " + result.getStatusCode() + ":" + result.getErrorMessage());
		}

		System.out.println(client.newQueryManager().search(
			client.newQueryManager().newStructuredQueryBuilder().directory(0, "/")
			, new JacksonHandle()).get().toPrettyString());

		System.out.println("Successfully finished cloud-based authentication test");
	}
}
