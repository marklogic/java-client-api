package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * These tests only verify that the Bean instance is built correctly, as in order to verify each connection type, we
 * need working test setups for all the different authentication types, which we don't yet have.
 */
public class DatabaseClientBuilderTest {

	private DatabaseClientFactory.Bean bean;

	@Test
	void minimumConnectionProperties() {
		bean = new DatabaseClientBuilder()
			.withHost("myhost")
			.withPort(8000)
			.withSecurityContextType("digest")
			.buildBean();

		assertEquals("myhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertNull(bean.getDatabase());
		assertNull(bean.getBasePath());
		assertNull(bean.getConnectionType());
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.DigestAuthContext);
	}

	@Test
	void allConnectionProperties() {
		bean = new DatabaseClientBuilder()
			.withHost("myhost")
			.withPort(8000)
			.withSecurityContextType("digest")
			.withBasePath("/my/path")
			.withDatabase("Documents")
			.withConnectionType(DatabaseClient.ConnectionType.DIRECT)
			.buildBean();

		assertEquals("myhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertEquals("Documents", bean.getDatabase());
		assertEquals("/my/path", bean.getBasePath());
		assertEquals(DatabaseClient.ConnectionType.DIRECT, bean.getConnectionType());
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.DigestAuthContext);
	}

	@Test
	void noSecurityContextOrType() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new DatabaseClientBuilder()
			.withHost("some-host")
			.withPort(10)
			.buildBean());
		assertEquals("Must define a security context or security context type", ex.getMessage());
	}

	@Test
	void invalidSecurityContextType() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new DatabaseClientBuilder()
			.withHost("another-host")
			.withPort(200)
			.withSecurityContextType("invalid-type")
			.buildBean());
		assertEquals("Unrecognized security context type: invalid-type", ex.getMessage());
	}

	@Test
	void digest() {
		bean = Common.newClientBuilder()
			.withDigestAuth("my-user", "my-password")
			.buildBean();

		DatabaseClientFactory.DigestAuthContext context = (DatabaseClientFactory.DigestAuthContext) bean.getSecurityContext();
		assertEquals("my-user", context.getUser());
		assertEquals("my-password", context.getPassword());
	}

	@Test
	void basic() {
		bean = Common.newClientBuilder()
			.withBasicAuth("my-user", "my-password")
			.buildBean();

		DatabaseClientFactory.BasicAuthContext context = (DatabaseClientFactory.BasicAuthContext) bean.getSecurityContext();
		assertEquals("my-user", context.getUser());
		assertEquals("my-password", context.getPassword());
	}

	@Test
	void cloudWithBasePath() {
		bean = Common.newClientBuilder()
			.withMarkLogicCloudAuth("my-key", "/my/path")
			.buildBean();

		DatabaseClientFactory.MarkLogicCloudAuthContext context =
			(DatabaseClientFactory.MarkLogicCloudAuthContext) bean.getSecurityContext();
		assertEquals("my-key", context.getKey());
		assertEquals("/my/path", bean.getBasePath());
	}

	@Test
	void cloudNoApiKey() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Common.newClientBuilder()
			.withSecurityContextType("cloud")
			.withBasePath("/my/path")
			.build());
		assertEquals("No API key provided", ex.getMessage());
	}

	@Test
	void kerberos() {
		bean = Common.newClientBuilder()
			.withKerberosAuth("someone")
			.buildBean();

		DatabaseClientFactory.KerberosAuthContext context = (DatabaseClientFactory.KerberosAuthContext) bean.getSecurityContext();
		assertEquals("someone", context.getKrbOptions().get("principal"));
	}

	@Test
	void certificate() {
		DatabaseClientBuilder builder = Common.newClientBuilder()
			.withCertificateAuth("not.found", "passwd");

		Exception ex = assertThrows(Exception.class, () -> builder.buildBean());
		assertTrue(ex.getMessage().contains("Unable to create CertificateAuthContext"),
			"We don't yet have a real test for certificate authentication, so there's not yet a certificate store " +
				"to test against; just making sure that an attempt is made to create a CertificateAuthContext");
	}

	@Test
	void saml() {
		bean = Common.newClientBuilder()
			.withSAMLAuth("my-token")
			.buildBean();

		DatabaseClientFactory.SAMLAuthContext context = (DatabaseClientFactory.SAMLAuthContext) bean.getSecurityContext();
		assertEquals("my-token", context.getToken());
	}

	@Test
	void defaultSslContext() throws Exception {
		bean = Common.newClientBuilder()
			.withSSLContext(SSLContext.getDefault())
			.buildBean();

		assertNotNull(bean.getSecurityContext().getSSLContext());
	}

	@Test
	void sslProtocol() {
		bean = Common.newClientBuilder()
			.withSSLProtocol("TLSv1.2")
			.buildBean();

		assertNotNull(bean.getSecurityContext().getSSLContext());
		assertNull(bean.getSecurityContext().getTrustManager());
		assertNull(bean.getSecurityContext().getSSLHostnameVerifier());
	}

	@Test
	void invalidSslProtocol() {
		RuntimeException ex = assertThrows(RuntimeException.class, () -> Common.newClientBuilder()
			.withSSLProtocol("not-valid-value")
			.buildBean());

		assertTrue(ex.getMessage().startsWith("Unable to get SSLContext instance with protocol: not-valid-value"),
			"Unexpected error message: " + ex.getMessage());
	}

	@Test
	void sslProtocolAndTrustManager() {
		bean = Common.newClientBuilder()
			.withSSLProtocol("TLSv1.2")
			.withTrustManager(Common.TRUST_ALL_MANAGER)
			.buildBean();

		assertNotNull(bean.getSecurityContext().getSSLContext());
		assertNotNull(bean.getSecurityContext().getTrustManager());
		assertEquals(Common.TRUST_ALL_MANAGER, bean.getSecurityContext().getTrustManager());
		assertNull(bean.getSecurityContext().getSSLHostnameVerifier());
	}

	@Test
	void sslProtocolAndTrustManagerAndHostnameVerifier() {
		bean = Common.newClientBuilder()
			.withSSLProtocol("TLSv1.2")
			.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.COMMON)
			.withTrustManager(Common.TRUST_ALL_MANAGER)
			.buildBean();

		DatabaseClientFactory.SecurityContext context = bean.getSecurityContext();
		assertNotNull(context.getSSLContext());
		assertNotNull(context.getTrustManager());
		assertEquals(Common.TRUST_ALL_MANAGER, context.getTrustManager());
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.COMMON, context.getSSLHostnameVerifier());
	}
}
