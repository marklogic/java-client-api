package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	private DatabaseClientFactory.Bean buildBean() {
		DatabaseClientPropertySource source = new DatabaseClientPropertySource(propertyName -> props.get(propertyName));
		return source.newClientBean();
	}
}
