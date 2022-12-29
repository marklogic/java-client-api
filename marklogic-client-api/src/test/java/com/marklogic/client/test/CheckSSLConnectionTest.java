package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.test.junit5.RequireSSLExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RequireSSLExtension.class)
class CheckSSLConnectionTest {

	/**
	 * Simple check for ensuring that an SSL connection can be made when the app server requires SSL to be used. This
	 * uses a naive test-only "trust all" approach for trusting certificates. That is fine for this test, as the intent
	 * is simply to ensure that some kind of SSL connection can be made. In production, a user would be expected to
	 * use a real TrustManager.
	 *
	 * @throws Exception
	 */
	@Test
	void trustAllManager() throws Exception {
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(null, new TrustManager[]{Common.TRUST_ALL_MANAGER}, null);

		DatabaseClient client = Common.makeNewClient(Common.HOST, Common.PORT,
			Common.newSecurityContext(Common.USER, Common.PASS)
				.withSSLContext(sslContext, Common.TRUST_ALL_MANAGER)
				.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY));

		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertEquals(0, result.getStatusCode(), "A value of zero implies that a connection was successfully made, " +
			"which should happen since a 'trust all' manager is being used");
		assertNull(result.getErrorMessage());
	}

	@Test
	void defaultSslContext() throws Exception {
		DatabaseClient client = Common.makeNewClient(Common.HOST, Common.PORT,
			Common.newSecurityContext(Common.USER, Common.PASS)
				.withSSLContext(SSLContext.getDefault(), Common.TRUST_ALL_MANAGER)
				.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY));

		assertThrows(MarkLogicIOException.class, () -> client.checkConnection(),
			"The connection should fail because the JVM's default SSL Context does not have a CA certificate that " +
				"corresponds to the test-only certificate that the app server is using for this test");
	}
}
