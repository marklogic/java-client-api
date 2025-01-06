package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Intent of this test is to cover code that cannot be covered by DatabaseClientBuilderTest.
 */
public class DatabaseClientPropertySourceTest {

	private Map<String, Object> props;
	private DatabaseClientFactory.Bean bean;
	private static final String PREFIX = DatabaseClientBuilder.PREFIX;
	private static final String VERIFIER_PROPERTY = PREFIX + "sslHostnameVerifier";
	private static final String CONNECTION_TYPE_PROPERTY = PREFIX + "connectionType";

	@BeforeEach
	void beforeEach() {
		props = new HashMap() {{
			put(PREFIX + "authType", "digest");
			put(PREFIX + "username", "someuser");
			put(PREFIX + "password", "someword");
		}};
	}

	@Test
	void anyHostnameVerifier() {
		props.put(VERIFIER_PROPERTY, "any");
		bean = buildBean();
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.ANY, bean.getSecurityContext().getSSLHostnameVerifier());
	}

	@Test
	void commonHostnameVerifier() {
		props.put(VERIFIER_PROPERTY, "COMmon");
		bean = buildBean();
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.COMMON, bean.getSecurityContext().getSSLHostnameVerifier());
	}

	@Test
	void strictHostnameVerifier() {
		props.put(VERIFIER_PROPERTY, "STRICT");
		bean = buildBean();
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.STRICT, bean.getSecurityContext().getSSLHostnameVerifier());
	}

	@Test
	void gatewayConnectionType() {
		props.put(CONNECTION_TYPE_PROPERTY, "gateway");
		bean = buildBean();
		assertEquals(DatabaseClient.ConnectionType.GATEWAY, bean.getConnectionType());

		props.put(CONNECTION_TYPE_PROPERTY, "GATEWAY");
		bean = buildBean();
		assertEquals(DatabaseClient.ConnectionType.GATEWAY, bean.getConnectionType());
	}

	@Test
	void stringPort() {
		props.put(PREFIX + "port", "8000");
		bean = buildBean();
		assertEquals(8000, bean.getPort());
	}

	@Test
	void cloudAuthWithNoSslInputs() {
		props.put(PREFIX + "authType", "cloud");
		props.put(PREFIX + "cloud.apiKey", "abc123");
		props.put(PREFIX + "basePath", "/my/path");

		bean = buildBean();

		assertEquals("/my/path", bean.getBasePath());
		assertTrue(bean.getSecurityContext() instanceof DatabaseClientFactory.MarkLogicCloudAuthContext);

		DatabaseClientFactory.MarkLogicCloudAuthContext context = (DatabaseClientFactory.MarkLogicCloudAuthContext) bean.getSecurityContext();
		assertEquals("abc123", context.getApiKey());

		assertNotNull(context.getSSLContext(), "If cloud is chosen with no SSL protocol or context, the default JVM " +
			"SSLContext should be used");

		assertNotNull(context.getSSLContext().getSocketFactory(), "The default JVM SSLContext should already be " +
			"initialized and thus it should be possible to get a socket factory from it");

		assertNotNull(context.getTrustManager(), "If cloud is chosen with no SSL protocol or context, the default JVM " +
			"trust manager should be used");
	}

	@Test
	void cloudWithNonNumericDuration() {
		props.put(PREFIX + "authType", "cloud");
		props.put(PREFIX + "cloud.apiKey", "abc123");
		props.put(PREFIX + "basePath", "/my/path");
		props.put(PREFIX + "cloud.tokenDuration", "abc");

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> buildBean());
		assertEquals("Cloud token duration must be numeric", ex.getMessage());
	}

	@Test
	void disableGzippedResponses() {
		final String prop = PREFIX + "disableGzippedResponses";

		props.put(PREFIX + prop, "true");
		// Won't throw an error, but we can't verify the results because the list of configurators in
		// DatabaseClientFactory is private.
		buildBean();

		// Verifying this doesn't throw an error either; the impl should be using Boolean.parseBoolean which only cares
		// if the value equals 'true'.
		props.put(prop, "123");
		buildBean();
	}

	@Test
	void connectionString() {
		useConnectionString("user:password@localhost:8000");
		DatabaseClientFactory.Bean bean = buildBean();

		assertEquals("localhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertNull(bean.getDatabase());
		DatabaseClientFactory.DigestAuthContext context = (DatabaseClientFactory.DigestAuthContext) bean.getSecurityContext();
		assertEquals("user", context.getUser());
		assertEquals("password", context.getPassword());
	}

	@Test
	void connectionStringWithDatabase() {
		useConnectionString("user:password@localhost:8000/Documents");
		DatabaseClientFactory.Bean bean = buildBean();

		assertEquals("localhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertEquals("Documents", bean.getDatabase());
		DatabaseClientFactory.DigestAuthContext context = (DatabaseClientFactory.DigestAuthContext) bean.getSecurityContext();
		assertEquals("user", context.getUser());
		assertEquals("password", context.getPassword());
	}

	@Test
	void connectionStringWithSeparateDatabase() {
		useConnectionString("user:password@localhost:8000");
		props.put(PREFIX + "database", "SomeDatabase");
		DatabaseClientFactory.Bean bean = buildBean();

		assertEquals("localhost", bean.getHost());
		assertEquals(8000, bean.getPort());
		assertEquals("SomeDatabase", bean.getDatabase());
	}

	@Test
	void usernameAndPasswordBothRequireDecoding() {
		useConnectionString("test-user%40:sp%40r%3Ak@localhost:8000/Documents");
		DatabaseClientFactory.Bean bean = buildBean();

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
	void invalidConnectionString(String connectionString) {
		useConnectionString(connectionString);
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> buildBean());
		assertEquals("Invalid value for connection string; must be username:password@host:port/optionalDatabaseName",
			ex.getMessage());
	}

	@Test
	void nonNumericPortInConnectionString() {
		useConnectionString("user:password@host:nonNumericPort");
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> buildBean());
		assertEquals("Invalid value for connection string; port must be numeric, but was 'nonNumericPort'", ex.getMessage());
	}

	@Test
	void hostTakesPrecedence() {
		props = new HashMap() {{
			put(PREFIX + "host", "somehost");
			put(PREFIX + "connectionString", "user:password@localhost:8000/Documents");
		}};

		DatabaseClientFactory.Bean bean = buildBean();
		assertEquals("somehost", bean.getHost(), "This allows a user to use a connection string as a starting " +
			"point, and then override the host for a direct connection to a particular host in a cluster. This " +
			"capability is used by our Spark connector to support direct connections to multiple hosts.");
	}

	private void useConnectionString(String connectionString) {
		props = new HashMap() {{
			put(PREFIX + "connectionString", connectionString);
		}};
	}

	private DatabaseClientFactory.Bean buildBean() {
		DatabaseClientPropertySource source = new DatabaseClientPropertySource(propertyName -> props.get(propertyName));
		return source.newClientBean();
	}
}
