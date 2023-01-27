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
			.withBasicAuth("someuser", "someword")
			.buildBean();

		assertEquals("myhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertNull(bean.getDatabase());
		assertNull(bean.getBasePath());
		assertNull(bean.getConnectionType());
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.BasicAuthContext);
	}

	@Test
	void allConnectionProperties() {
		bean = new DatabaseClientBuilder()
			.withHost("myhost")
			.withPort(8000)
			.withDigestAuth("someuser", "someword")
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
		assertEquals("Security context should be set, or security context type must be of type String", ex.getMessage());
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

		assertNotNull(context.getSSLContext(), "If no sslProtocol or sslContext is set, the JVM's default SSL " +
			"context should be used");

		assertNotNull(context.getSSLContext().getSocketFactory(), "Since the JVM's default SSL context is expected " +
			"to be used, it should already be initialized, and thus able to return a socket factory");

		assertNotNull(context.getTrustManager(), "Since the JVM's default SSL context is used, the JVM's default " +
			"trust manager should be used as well if the user doesn't provide their own");
	}

	@Test
	void cloudNoApiKey() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Common.newClientBuilder()
			.withSecurityContextType("cloud")
			.withBasePath("/my/path")
			.build());
		assertEquals("cloud.apiKey must be of type String", ex.getMessage());
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

		assertThrows(IllegalStateException.class, () -> bean.getSecurityContext().getSSLContext().getSocketFactory(),
			"If an SSL protocol is provided with no trust manager, the builder is expected to create an instance of " +
				"SSLContext but not to initialize it. Later on - via OkHttpUtil - the Java Client will attempt to " +
				"initialize the SSLContext before using it by using the JVM's default trust manager.");
	}

	@Test
	void defaultSslProtocolAndNoTrustManager() {
		bean = Common.newClientBuilder()
			.withSSLProtocol("default")
			.buildBean();

		DatabaseClientFactory.SecurityContext context = bean.getSecurityContext();
		assertNotNull(context);

		SSLContext sslContext = context.getSSLContext();
		assertNotNull(sslContext);
		assertNotNull(sslContext.getSocketFactory(), "A protocol of 'default' should result in the JVM's default " +
			"SSLContext being used, which is expected to have been initialized already and can thus return a socket " +
			"factory");

		assertNotNull(context.getTrustManager(), "If the user specifies a protocol of 'default' but does not " +
			"provide a trust manager, the assumption is that the JVM's default trust manager should be used, thus " +
			"saving the user from having to do the work of providing this themselves.");
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

		assertNotNull(bean.getSecurityContext().getSSLContext().getSocketFactory(),
			"Since a protocol was provided with a trust manager, the builder is expected to initialize the " +
				"SSLContext created via the protocol using the given trust manager. This is primarily intended to " +
				"support a use case of providing a custom trust manager (often a 'trust all' one in a development or " +
				"test environment) without forcing the user to initialize an SSLContext themselves.");
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
