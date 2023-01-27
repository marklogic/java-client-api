/*
 * Copyright (c) 2022 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Contains the implementation for the {@code DatabaseClientFactory.newClient} method that accepts a function as a
 * property source. Implementation is here primarily to ease readability and avoid making the factory class any larger
 * than it already is.
 *
 * @since 6.1.0
 */
public class DatabaseClientPropertySource {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseClientPropertySource.class);
	private static final String PREFIX = DatabaseClientBuilder.PREFIX;

	private final Function<String, Object> propertySource;

	// Map of consumer functions that handle properties related to the connection to MarkLogic, but not to authentication
	private static Map<String, BiConsumer<DatabaseClientFactory.Bean, Object>> connectionPropertyHandlers;

	static {
		connectionPropertyHandlers = new LinkedHashMap<>();
		connectionPropertyHandlers.put(PREFIX + "host", (bean, value) -> bean.setHost((String) value));
		connectionPropertyHandlers.put(PREFIX + "port", (bean, value) -> {
			if (value instanceof String) {
				bean.setPort(Integer.parseInt((String) value));
			} else {
				bean.setPort((int) value);
			}
		});
		connectionPropertyHandlers.put(PREFIX + "database", (bean, value) -> bean.setDatabase((String) value));
		connectionPropertyHandlers.put(PREFIX + "basePath", (bean, value) -> bean.setBasePath((String) value));
		connectionPropertyHandlers.put(PREFIX + "connectionType", (bean, value) -> {
			if (value instanceof DatabaseClient.ConnectionType) {
				bean.setConnectionType((DatabaseClient.ConnectionType) value);
			} else if (value instanceof String) {
				String val = (String) value;
				if (val.trim().length() > 0) {
					bean.setConnectionType(DatabaseClient.ConnectionType.valueOf(val.toUpperCase()));
				}
			} else
				throw new IllegalArgumentException("Connection type must either be a String or an instance of ConnectionType");
		});
	}

	public DatabaseClientPropertySource(Function<String, Object> propertySource) {
		this.propertySource = propertySource;
	}

	public DatabaseClient newClient() {
		DatabaseClientFactory.Bean bean = newClientBean();
		// For consistency with how clients have been created - i.e. not via a Bean class, but via
		// DatabaseClientFactory.newClient methods - this does not make use of the bean.newClient() method but rather
		// uses the fully-overloaded newClient method. This ensures that later calls to e.g.
		// DatabaseClientFactory.getHandleRegistry() will still impact the DatabaseClient returned by this method
		// (and this behavior is expected by some existing tests).
		return DatabaseClientFactory.newClient(bean.getHost(), bean.getPort(), bean.getBasePath(), bean.getDatabase(),
			bean.getSecurityContext(), bean.getConnectionType());

	}

	public DatabaseClientFactory.Bean newClientBean() {
		final DatabaseClientFactory.Bean bean = new DatabaseClientFactory.Bean();
		connectionPropertyHandlers.forEach((propName, consumer) -> {
			Object propValue = propertySource.apply(propName);
			if (propValue != null) {
				consumer.accept(bean, propValue);
			}
		});
		bean.setSecurityContext(newSecurityContext());
		return bean;
	}

	private DatabaseClientFactory.SecurityContext newSecurityContext() {
		DatabaseClientFactory.SecurityContext securityContext = (DatabaseClientFactory.SecurityContext)
			propertySource.apply(PREFIX + "securityContext");
		if (securityContext != null) {
			return securityContext;
		}

		String type = (String) propertySource.apply(PREFIX + "securityContextType");
		if (type == null || type.trim().length() == 0) {
			throw new IllegalArgumentException("Must define a security context or security context type");
		}
		securityContext = newSecurityContext(type);

		X509TrustManager trustManager = determineTrustManager();
		SSLContext sslContext = determineSSLContext(trustManager);
		if (sslContext != null) {
			securityContext.withSSLContext(sslContext, trustManager);
		}

		securityContext.withSSLHostnameVerifier(determineHostnameVerifier());
		return securityContext;
	}

	private DatabaseClientFactory.SecurityContext newSecurityContext(String type) {
		switch (type.toLowerCase()) {
			case DatabaseClientBuilder.SECURITY_CONTEXT_TYPE_BASIC:
				return newBasicAuthContext();
			case DatabaseClientBuilder.SECURITY_CONTEXT_TYPE_DIGEST:
				return newDigestAuthContext();
			case DatabaseClientBuilder.SECURITY_CONTEXT_TYPE_MARKLOGIC_CLOUD:
				return newCloudAuthContext();
			case DatabaseClientBuilder.SECURITY_CONTEXT_TYPE_KERBEROS:
				return newKerberosAuthContext();
			case DatabaseClientBuilder.SECURITY_CONTEXT_TYPE_CERTIFICATE:
				return newCertificateAuthContext();
			case DatabaseClientBuilder.SECURITY_CONTEXT_TYPE_SAML:
				return newSAMLAuthContext();
			default:
				throw new IllegalArgumentException("Unrecognized security context type: " + type);
		}
	}

	private DatabaseClientFactory.SecurityContext newBasicAuthContext() {
		return new DatabaseClientFactory.BasicAuthContext(
			(String) propertySource.apply(PREFIX + "username"),
			(String) propertySource.apply(PREFIX + "password")
		);
	}

	private DatabaseClientFactory.SecurityContext newDigestAuthContext() {
		return new DatabaseClientFactory.DigestAuthContext(
			(String) propertySource.apply(PREFIX + "username"),
			(String) propertySource.apply(PREFIX + "password")
		);
	}

	private DatabaseClientFactory.SecurityContext newCloudAuthContext() {
		return new DatabaseClientFactory.MarkLogicCloudAuthContext(
			(String) propertySource.apply(PREFIX + "cloud.apiKey")
		);
	}

	private DatabaseClientFactory.SecurityContext newCertificateAuthContext() {
		try {
			return new DatabaseClientFactory.CertificateAuthContext(
				(String) propertySource.apply(PREFIX + "certificate.file"),
				(String) propertySource.apply(PREFIX + "certificate.password"),
				determineTrustManager()
			);
		} catch (Exception e) {
			throw new RuntimeException("Unable to create CertificateAuthContext; cause " + e.getMessage(), e);
		}
	}

	private DatabaseClientFactory.SecurityContext newKerberosAuthContext() {
		return new DatabaseClientFactory.KerberosAuthContext(
			(String) propertySource.apply(PREFIX + "kerberos.principal")
		);
	}

	private DatabaseClientFactory.SecurityContext newSAMLAuthContext() {
		return new DatabaseClientFactory.SAMLAuthContext(
			(String) propertySource.apply(PREFIX + "saml.token")
		);
	}

	private SSLContext determineSSLContext(X509TrustManager trustManager) {
		SSLContext sslContext = (SSLContext) propertySource.apply(PREFIX + "sslContext");
		if (sslContext != null) {
			return sslContext;
		}
		String protocol = (String) propertySource.apply(PREFIX + "sslProtocol");
		if (protocol != null) {
			if ("default".equalsIgnoreCase(protocol)) {
				try {
					return SSLContext.getDefault();
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException("Unable to obtain default SSLContext; cause: " + e.getMessage(), e);
				}
			}
			try {
				sslContext = SSLContext.getInstance(protocol);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("Unable to get SSLContext instance with protocol: " + protocol
					+ "; cause: " + e.getMessage(), e);
			}
			// Note that if only a protocol is specified, and not a TrustManager, an attempt will later be made
			// to use the JVM's default TrustManager
			if (trustManager != null) {
				try {
					sslContext.init(null, new X509TrustManager[]{trustManager}, null);
				} catch (KeyManagementException e) {
					throw new RuntimeException("Unable to initialize SSLContext; protocol: " + protocol + "; cause: " + e.getMessage(), e);
				}
			}
			return sslContext;
		}
		return null;
	}

	private X509TrustManager determineTrustManager() {
		Object trustManagerObject = propertySource.apply(PREFIX + "trustManager");
		if (trustManagerObject != null) {
			if (trustManagerObject instanceof X509TrustManager) {
				return (X509TrustManager) trustManagerObject;
			}
			throw new IllegalArgumentException(
				String.format("Trust manager must be an instance of %s", X509TrustManager.class.getName()));
		}
		// If the user chooses the "default" SSLContext, then it's already been initialized - but OkHttp still
		// needs a separate X509TrustManager, so use the JVM's default trust manager. The assumption is that the
		// default SSLContext was initialized with the JVM's default trust manager. A user can of course always override
		// this by simply providing their own trust manager.
		if ("default".equalsIgnoreCase((String) propertySource.apply(PREFIX + "sslProtocol"))) {
			X509TrustManager defaultTrustManager = SSLUtil.getDefaultTrustManager();
			if (logger.isDebugEnabled() && defaultTrustManager != null && defaultTrustManager.getAcceptedIssuers() != null) {
				logger.debug("Count of accepted issuers in default trust manager: {}",
					defaultTrustManager.getAcceptedIssuers().length);
			}
			return defaultTrustManager;
		}
		return null;
	}

	private DatabaseClientFactory.SSLHostnameVerifier determineHostnameVerifier() {
		Object verifierObject = propertySource.apply(PREFIX + "sslHostnameVerifier");
		if (verifierObject instanceof DatabaseClientFactory.SSLHostnameVerifier) {
			return (DatabaseClientFactory.SSLHostnameVerifier) verifierObject;
		} else if (verifierObject instanceof String) {
			String verifier = (String) verifierObject;
			if ("ANY".equalsIgnoreCase(verifier)) {
				return DatabaseClientFactory.SSLHostnameVerifier.ANY;
			} else if ("COMMON".equalsIgnoreCase(verifier)) {
				return DatabaseClientFactory.SSLHostnameVerifier.COMMON;
			} else if ("STRICT".equalsIgnoreCase(verifier)) {
				return DatabaseClientFactory.SSLHostnameVerifier.STRICT;
			}
			throw new IllegalArgumentException(String.format("Unrecognized value for SSLHostnameVerifier: %s", verifier));
		}
		return null;
	}
}
