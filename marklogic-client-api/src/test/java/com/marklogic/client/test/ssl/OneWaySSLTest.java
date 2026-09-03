/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.ssl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.SSLUtil;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.MarkLogicVersion;
import com.marklogic.client.test.junit5.DisabledWhenUsingReverseProxyServer;
import com.marklogic.client.test.junit5.RequireSSLExtension;
import com.marklogic.client.test.junit5.RequiresML11OrLower;
import com.marklogic.client.test.junit5.RequiresML12Dot0;
import com.marklogic.client.test.junit5.RequiresML12Dot1;
import com.marklogic.client.test.junit5.RequiresML12;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.util.Map;

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
	private static ManageClient manageClient;

	@BeforeAll
	static void setup() {
		manageClient = Common.newManageClient();
	}

	@AfterEach
	void teardown() {
		MarkLogicVersion markLogicVersion = Common.getMarkLogicVersion();
		if (markLogicVersion.getMajor() >= 12) {
			setAppServerMinimumTLSVersion("TLSv1.2");
		}
	}

	private static void setAppServerMinimumTLSVersion(String minTLSVersion) {
		new ServerManager(manageClient).save(
			Common.newServerPayload().put("ssl-min-allow-tls", minTLSVersion).toString()
		);
	}

	/**
	 * Simple check for ensuring that an SSL connection can be made when the app server requires SSL to be used. This
	 * uses a naive test-only "trust all" approach for trusting certificates. That is fine for this test, as the intent
	 * is simply to ensure that some kind of SSL connection can be made. In production, a user would be expected to
	 * use a real TrustManager.
	 *
	 * @throws Exception - if an error occurs with building the SSLContext object.
	 */
	@Test
	void trustAllManager() throws Exception {
		SSLContext sslContext = SSLContext.getInstance(SSLUtil.DEFAULT_PROTOCOL);
		sslContext.init(null, new TrustManager[]{Common.TRUST_ALL_MANAGER}, null);

		DatabaseClient client = newSslClient(Map.of(
			"marklogic.client.sslContext", sslContext,
			"marklogic.client.trustManager", Common.TRUST_ALL_MANAGER,
			"marklogic.client.sslHostnameVerifier", DatabaseClientFactory.SSLHostnameVerifier.ANY
		));

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
		DatabaseClient client = newSslClient(Map.of(
			"marklogic.client.sslProtocol", SSLUtil.DEFAULT_PROTOCOL,
			"marklogic.client.trustManager", RequireSSLExtension.newSecureTrustManager(),
			"marklogic.client.sslHostnameVerifier", DatabaseClientFactory.SSLHostnameVerifier.ANY
		));

		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertEquals(0, result.getStatusCode());
		assertNull(result.getErrorMessage());
	}

	@Test
	void defaultSslContext() throws Exception {
		DatabaseClient client = newSslClient(Map.of(
			"marklogic.client.sslContext", SSLContext.getDefault(),
			"marklogic.client.trustManager", Common.TRUST_ALL_MANAGER,
			"marklogic.client.sslHostnameVerifier", DatabaseClientFactory.SSLHostnameVerifier.ANY
		));

		MarkLogicIOException ex = assertThrows(MarkLogicIOException.class, () -> client.checkConnection(),
			"The connection should fail because the JVM's default SSL Context does not have a CA certificate that " +
				"corresponds to the test-only certificate that the app server is using for this test");
		assertTrue(ex.getCause() instanceof SSLException, "Unexpected cause: " + ex.getCause());
	}

	@ExtendWith(RequiresML11OrLower.class)
	@Test
	void noSslContextWithMarkLogic11OrLower() {
		DatabaseClient client = newSslClient(Map.of());

		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertEquals("Forbidden", result.getErrorMessage(), "MarkLogic 11 or lower is expected to return a 403 Forbidden when the " +
			"user tries to access an HTTPS app server using HTTP.");
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

	@ExtendWith(RequiresML12Dot0.class)
	@Test
	void noSslContextWithMarkLogic12Dot0() {
		DatabaseClient client = newSslClient(Map.of());

		MarkLogicIOException ex = assertThrows(MarkLogicIOException.class, () -> client.checkConnection());
		assertTrue(ex.getMessage().contains("unexpected end of stream"), "Per MLE-17505, a change in the openssl " +
			"library used by the server results in an IO exception when the client tries to connect to an " +
			"app server that requires SSL, but the client does not use SSL. This impacts all ML 12.0.x versions as of 12.0.3. " +
			"Actual message: " + ex.getMessage());
	}

	@ExtendWith(RequiresML12Dot1.class)
	@Test
	void noSslContextWithMarkLogic12Dot1OrHigher() {
		DatabaseClient client = newSslClient(Map.of());

		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertEquals("Forbidden", result.getErrorMessage(), "MarkLogic 12.1 or higher is expected to return a 403 Forbidden when the " +
			"user tries to access an HTTPS app server using HTTP.");
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

	@Test
	void tLS13ClientWithTLS12Server() {
		DatabaseClient client = buildTrustAllClientWithSSLProtocol(SSLUtil.DEFAULT_PROTOCOL);
		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertEquals(0, result.getStatusCode(), "A value of zero implies that a connection was successfully made, " +
			"which should happen since a 'trust all' manager is being used");
		assertNull(result.getErrorMessage());
	}

	@ExtendWith(RequiresML12.class)
	@Test
	void tLS13ClientWithTLS13Server() {
		setAppServerMinimumTLSVersion("TLSv1.3");

		DatabaseClient client = buildTrustAllClientWithSSLProtocol("TLSv1.3");
		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertEquals(0, result.getStatusCode(), "A value of zero implies that a connection was successfully made, " +
			"which should happen since a 'trust all' manager is being used");
		assertNull(result.getErrorMessage());
	}

	@ExtendWith(RequiresML12.class)
	@Test
	void tLS12ClientWithTLS13ServerShouldFail() {
		setAppServerMinimumTLSVersion("TLSv1.3");

		DatabaseClient client = buildTrustAllClientWithSSLProtocol("TLSv1.2");
		MarkLogicIOException ex = Assertions.assertThrows(MarkLogicIOException.class, () -> client.checkConnection());
		String expected = "Error occurred while calling https://localhost:8012/v1/ping; " +
			"javax.net.ssl.SSLHandshakeException: Received fatal alert: protocol_version ; possible reasons for the " +
			"error include that a MarkLogic app server may not be listening on the port, or MarkLogic was stopped or " +
			"restarted during the request; check the MarkLogic server logs for more information.";
		assertEquals(expected, ex.getMessage());
	}

	/**
	 * Verifies that a {@link DatabaseClient} backed by a truststore that does NOT contain
	 * the server's CA certificate fails with an {@link SSLHandshakeException}. This confirms
	 * that proper certificate validation is active — i.e. the trust manager is not a no-op.
	 *
	 * <p>The JVM default CA bundle is used as the trust store. It contains real certificates
	 * but not the test-only MarkLogic CA, so the TLS handshake starts and then fails with
	 * {@link SSLHandshakeException} when the server's certificate cannot be verified.</p>
	 */
	@Test
	void untrustedCertificateThrowsSSLHandshakeException() throws Exception {
		// Use the JVM default trust managers (standard CA bundle). The MarkLogic
		// test certificate is issued by a test CA that is not present in that bundle,
		// so the TLS handshake will fail with SSLHandshakeException.
		TrustManager[] trustManagers = SSLUtil.getDefaultTrustManagers();
		SSLContext sslContext = SSLContext.getInstance(SSLUtil.DEFAULT_PROTOCOL);
		sslContext.init(null, trustManagers, null);

		DatabaseClient client = newSslClient(Map.of(
			"marklogic.client.sslContext", sslContext,
			"marklogic.client.trustManager", (X509TrustManager) trustManagers[0],
			"marklogic.client.sslHostnameVerifier", DatabaseClientFactory.SSLHostnameVerifier.ANY
		));

		MarkLogicIOException ex = assertThrows(MarkLogicIOException.class, () -> client.checkConnection(),
			"Connection must fail because the JVM default trust store does not contain the test-only MarkLogic CA certificate");
		assertTrue(ex.getCause() instanceof SSLHandshakeException,
			"Expected SSLHandshakeException caused by an untrusted server certificate; actual cause: " + ex.getCause());
	}

	private DatabaseClient newSslClient(Map<String, Object> sslProps) {
		return DatabaseClientFactory.newClient(propertyName -> {
			if (sslProps.containsKey(propertyName)) {
				return sslProps.get(propertyName);
			}
			return switch (propertyName) {
				case "marklogic.client.host" -> Common.HOST;
				case "marklogic.client.port" -> Common.PORT;
				case "marklogic.client.basePath" -> Common.BASE_PATH;
				case "marklogic.client.authType" -> Common.AUTH_TYPE;
				case "marklogic.client.username" -> Common.USER;
				case "marklogic.client.password" -> Common.PASS;
				case "marklogic.client.connectionType" -> Common.CONNECTION_TYPE;
				default -> null;
			};
		});
	}

	DatabaseClient buildTrustAllClientWithSSLProtocol(String sslProtocol) {
		return newSslClient(Map.of(
			"marklogic.client.sslProtocol", sslProtocol,
			"marklogic.client.trustManager", Common.TRUST_ALL_MANAGER,
			"marklogic.client.sslHostnameVerifier", DatabaseClientFactory.SSLHostnameVerifier.ANY
		));
	}
}
