package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

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
	void validConnection() {
		DatabaseClient client = DatabaseClientFactory.newClient(propertyName ->
			"marklogic.client.connectionString".equals(propertyName) ?
			String.format("%s:%s@%s:%d", Common.USER, Common.PASS, Common.HOST, Common.PORT) : null);
		DatabaseClient.ConnectionResult result = client.checkConnection();
		assertNull(result.getErrorMessage());
		assertTrue(result.isConnected());
	}

	@Test
	void connectionString() {
		bean = new DatabaseClientBuilder()
			.withConnectionString("user:password@localhost:8000")
			.buildBean();

		assertEquals("localhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertNull(bean.getDatabase());

		DatabaseClientFactory.DigestAuthContext context = (DatabaseClientFactory.DigestAuthContext) bean.getSecurityContext();
		assertEquals("user", context.getUser());
		assertEquals("password", context.getPassword());
	}

	@Test
	void connectionStringWithDatabase() {
		bean = new DatabaseClientBuilder()
			.withConnectionString("user:password@localhost:8000/Documents")
			.buildBean();

		assertEquals("localhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertEquals("Documents", bean.getDatabase());

		DatabaseClientFactory.DigestAuthContext context = (DatabaseClientFactory.DigestAuthContext) bean.getSecurityContext();
		assertEquals("user", context.getUser());
		assertEquals("password", context.getPassword());
	}

	@Test
	void connectionStringWithSeparateDatabase() {
		bean = new DatabaseClientBuilder()
			.withDatabase("SomeDatabase")
			.withConnectionString("user:password@localhost:8000")
			.buildBean();

		assertEquals("localhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertEquals("SomeDatabase", bean.getDatabase());
	}

	@Test
	void usernameAndPasswordBothRequireDecoding() {
		bean = new DatabaseClientBuilder()
			.withConnectionString("test-user%40:sp%40r%3Ak@localhost:8000/Documents")
			.buildBean();

		assertEquals("localhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertEquals("Documents", bean.getDatabase());

		DatabaseClientFactory.DigestAuthContext context = (DatabaseClientFactory.DigestAuthContext) bean.getSecurityContext();
		assertEquals("test-user@", context.getUser());
		assertEquals("sp@r:k", context.getPassword(), "Verifies that the user must encode username and password " +
			"values that contain ':' or '@'. The builder is then expected to decode them into the correct values.");
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"user@host@port",
		"user@host:port",
		"user:password@host",
		"user:password:something@host:port",
		"user:password@host:port:something"
	})
	void invalidConnectionString(String value) {
		DatabaseClientBuilder builder = new DatabaseClientBuilder();
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
			() -> builder.withConnectionString(value));
		assertEquals("Invalid value for connection string; must be username:password@host:port/optionalDatabaseName",
			ex.getMessage());
	}

	@Test
	void nonNumericPortInConnectionString() {
		DatabaseClientBuilder builder = new DatabaseClientBuilder();
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
			() -> builder.withConnectionString("user:password@host:nonNumericPort"));
		assertEquals("Invalid value for connection string; port must be numeric, but was 'nonNumericPort'",
			ex.getMessage());
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
		assertEquals("Security context should be set, or auth type must be of type String", ex.getMessage());
	}

	@Test
	void invalidAuthType() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new DatabaseClientBuilder()
			.withHost("another-host")
			.withPort(200)
			.withAuthType("invalid-type")
			.buildBean());
		assertEquals("Unrecognized auth type: invalid-type", ex.getMessage());
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
	void digestNoUsername() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
			Common.newClientBuilder().withDigestAuth(null, "my-password").buildBean());
		assertEquals("Must specify a username when using digest authentication.", ex.getMessage());
	}

	@Test
	void digestNoPassword() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
			Common.newClientBuilder().withDigestAuth("my-user", null).buildBean());
		assertEquals("Must specify a password when using digest authentication.", ex.getMessage());
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
	void basicNoUsername() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
			Common.newClientBuilder().withBasicAuth(null, "my-password").buildBean());
		assertEquals("Must specify a username when using basic authentication.", ex.getMessage());
	}

	@Test
	void basicNoPassword() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
			Common.newClientBuilder().withBasicAuth("my-user", null).buildBean());
		assertEquals("Must specify a password when using basic authentication.", ex.getMessage());
	}

	@Test
	void cloudWithBasePath() {
		bean = Common.newClientBuilder()
			.withCloudAuth("my-key", "/my/path")
			.buildBean();

		DatabaseClientFactory.MarkLogicCloudAuthContext context =
			(DatabaseClientFactory.MarkLogicCloudAuthContext) bean.getSecurityContext();
		assertEquals("my-key", context.getApiKey());
		assertEquals("/my/path", bean.getBasePath());

		assertNotNull(context.getSSLContext(), "If no sslProtocol or sslContext is set, the JVM's default SSL " +
			"context should be used");

		assertNotNull(context.getSSLContext().getSocketFactory(), "Since the JVM's default SSL context is expected " +
			"to be used, it should already be initialized, and thus able to return a socket factory");

		assertNotNull(context.getTrustManager(), "Since the JVM's default SSL context is used, the JVM's default " +
			"trust manager should be used as well if the user doesn't provide their own");
	}

	@Test
	void cloudWithDuration() {
		bean = Common.newClientBuilder().withCloudAuth("abc123", "/my/path", 10).buildBean();
		DatabaseClientFactory.MarkLogicCloudAuthContext context =
			(DatabaseClientFactory.MarkLogicCloudAuthContext) bean.getSecurityContext();
		assertEquals("abc123", context.getApiKey());
		assertEquals("/my/path", bean.getBasePath());
		assertEquals(10, context.getTokenDuration());
	}

	@Test
	void cloudNoApiKey() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Common.newClientBuilder()
			.withAuthType("cloud")
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
	void certificateValidFile() {
		DatabaseClient client = Common.newClientBuilder()
			.withCertificateAuth("src/test/resources/test_certificate.p12", "abc")
			.build();

		assertNotNull(client);
		assertNotNull(client.getSecurityContext().getSSLContext(), "An SSLContext should have been created based " +
			"on the test keystore.");
	}

	@Test
	void certificateInvalidFile() {
		DatabaseClientBuilder builder = Common.newClientBuilder()
			.withCertificateAuth("not.found", "passwd");

		Exception ex = assertThrows(Exception.class, () -> builder.buildBean());
		assertEquals("Unable to create CertificateAuthContext; cause not.found (No such file or directory)",
			ex.getMessage(), "Should fail because the certificate file path is not valid, and thus a keystore " +
				"cannot be created from it.");
	}

	@Test
	void certificateWithNoFile() throws NoSuchAlgorithmException {
		SSLContext defaultContext = SSLContext.getDefault();
		X509TrustManager trustManager = new SimpleX509TrustManager();
		DatabaseClientBuilder builder = Common.newClientBuilder()
			.withCertificateAuth(defaultContext, trustManager)
			.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.STRICT);

		// Verify the SSL-related objects are the same ones passed in above.
		DatabaseClientFactory.Bean bean = builder.buildBean();
		assertSame(defaultContext, bean.getSecurityContext().getSSLContext());
		assertSame(trustManager, bean.getSecurityContext().getTrustManager());
		assertSame(DatabaseClientFactory.SSLHostnameVerifier.STRICT, bean.getSecurityContext().getSSLHostnameVerifier());

		DatabaseClient client = bean.newClient();
		assertNotNull(client, "The client can be instantiated because a certificate file and password aren't " +
			"required. In this scenario, it's expected that a user will provide their own SSLContext to use for " +
			"certificate authentication.");
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
	void oauth() {
		bean = Common.newClientBuilder()
			.withOAuth("abc123")
			.buildBean();

		DatabaseClientFactory.OAuthContext context = (DatabaseClientFactory.OAuthContext) bean.getSecurityContext();
		assertEquals("abc123", context.getToken());
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
