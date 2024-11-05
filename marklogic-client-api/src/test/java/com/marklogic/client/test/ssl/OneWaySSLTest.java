package com.marklogic.client.test.ssl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.DisabledWhenUsingReverseProxyServer;
import com.marklogic.client.test.junit5.RequireSSLExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies scenarios for "one-way SSL" - i.e. the MarkLogic app server is configured with a certificate template to
 * require an SSL connection, but the client only needs to trust the server - the client does not present its own
 * certificate. See TwoWaySSLTest for scenarios where the client presents its own certificate which the server must
 * trust.
 */
@ExtendWith({
	DisabledWhenUsingReverseProxyServer.class,
	RequireSSLExtension.class
})
class OneWaySSLTest {

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

		DatabaseClient client = Common.newClientBuilder()
			.withSSLContext(sslContext)
			.withTrustManager(Common.TRUST_ALL_MANAGER)
			.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
			.build();

		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertEquals(0, result.getStatusCode(), "A value of zero implies that a connection was successfully made, " +
			"which should happen since a 'trust all' manager is being used");
		assertNull(result.getErrorMessage());
	}

	/**
	 * Demonstrates using a custom X509TrustManager that only accepts the issuer of the public certificate associated
	 * with the certificate template created via RequireSSLExtension.
	 */
	@Test
	void trustManagerThatOnlyTrustsTheCertificateFromTheCertificateTemplate() {
		DatabaseClient client = Common.newClientBuilder()
			.withSSLProtocol("TLSv1.2")
			.withTrustManager(RequireSSLExtension.newSecureTrustManager())
			.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
			.build();

		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertEquals(0, result.getStatusCode());
		assertNull(result.getErrorMessage());
	}

	@Test
	void defaultSslContext() throws Exception {
		DatabaseClient client = Common.newClientBuilder()
			.withSSLContext(SSLContext.getDefault())
			.withTrustManager(Common.TRUST_ALL_MANAGER)
			.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
			.build();

		MarkLogicIOException ex = assertThrows(MarkLogicIOException.class, () -> client.checkConnection(),
			"The connection should fail because the JVM's default SSL Context does not have a CA certificate that " +
				"corresponds to the test-only certificate that the app server is using for this test");
		assertTrue(ex.getCause() instanceof SSLException, "Unexpected cause: " + ex.getCause());
	}

	// Currently failing on 12-nightly due to https://progresssoftware.atlassian.net/browse/MLE-17505 . 
	@Test
	void noSslContext() {
		DatabaseClient client = Common.newClientBuilder().build();

		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertEquals("Forbidden", result.getErrorMessage(), "MarkLogic is expected to return a 403 Forbidden when the " +
			"user tries to access an HTTPS app server using HTTP");
		assertEquals(403, result.getStatusCode());

		ForbiddenUserException ex = assertThrows(ForbiddenUserException.class,
			() -> client.newServerEval().javascript("fn.currentDate()").evalAs(String.class));

		assertEquals(
			"Local message: User is not allowed to apply resource at eval. Server Message: You have attempted to access an HTTPS server using HTTP.",
			ex.getMessage(),
			"The user should get a clear message on why the connection failed as opposed to the previous error " +
				"message of 'Server (not a REST instance?)'."
		);
	}
}
