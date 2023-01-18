package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
			put(PREFIX + "securityContextType", "digest");
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

	private DatabaseClientFactory.Bean buildBean() {
		DatabaseClientPropertySource source = new DatabaseClientPropertySource(propertyName -> props.get(propertyName));
		return source.newClientBean();
	}
}
