/*
 * Copyright (c) 2023 MarkLogic Corporation
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
		connectionPropertyHandlers.put(PREFIX + "host", (bean, value) -> {
			if (value instanceof String) {
				bean.setHost((String) value);
			} else {
				throw new IllegalArgumentException("Host must be of type String");
			}
		});
		connectionPropertyHandlers.put(PREFIX + "port", (bean, value) -> {
			if (value instanceof String) {
				bean.setPort(Integer.parseInt((String) value));
			} else if (value instanceof Integer) {
				bean.setPort((int) value);
			} else {
				throw new IllegalArgumentException("Port must be of type String or Integer");
			}
		});
		connectionPropertyHandlers.put(PREFIX + "database", (bean, value) -> {
			if (value instanceof String) {
				bean.setDatabase((String) value);
			} else {
				throw new IllegalArgumentException("Database must be of type String");
			}
		});
		connectionPropertyHandlers.put(PREFIX + "basePath", (bean, value) -> {
			if (value instanceof String) {
				bean.setBasePath((String) value);
			} else {
				throw new IllegalArgumentException("Base path must be of type String");
			}
		});
		connectionPropertyHandlers.put(PREFIX + "connectionType", (bean, value) -> {
			if (value instanceof DatabaseClient.ConnectionType) {
				bean.setConnectionType((DatabaseClient.ConnectionType) value);
			} else if (value instanceof String) {
				String val = (String) value;
				if (val.trim().length() > 0) {
					bean.setConnectionType(DatabaseClient.ConnectionType.valueOf(val.toUpperCase()));
				}
			} else {
				throw new IllegalArgumentException("Connection type must either be a String or an instance of ConnectionType");
			}
		});
	}

	public DatabaseClientPropertySource(Function<String, Object> propertySource) {
		this.propertySource = propertySource;
	}

	/**
	 * @return an instance of {@code DatabaseClient} based on the given property source
	 */
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

	/**
	 * @return an instance of {@code DatabaseClientFactory.Bean} based on the given property source. This is primarily
	 * intended for testing purposes so that the Bean can be verified without creating a client.
	 */
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
		Object securityContextValue = propertySource.apply(PREFIX + "securityContext");
		if (securityContextValue != null) {
			if (securityContextValue instanceof DatabaseClientFactory.SecurityContext) {
				return (DatabaseClientFactory.SecurityContext) securityContextValue;
			}
			throw new IllegalArgumentException("Security context must be of type " + DatabaseClientFactory.SecurityContext.class.getName());
		}

		Object typeValue = propertySource.apply(PREFIX + "authType");
		if (typeValue == null || !(typeValue instanceof String)) {
			throw new IllegalArgumentException("Security context should be set, or auth type must be of type String");
		}
		final String authType = (String)typeValue;
		final SSLInputs sslInputs = buildSSLInputs(authType);

		DatabaseClientFactory.SecurityContext securityContext = newSecurityContext(authType, sslInputs);

		X509TrustManager trustManager = determineTrustManager(sslInputs);
		SSLContext sslContext = sslInputs.getSslContext() != null ?
			sslInputs.getSslContext() :
			determineSSLContext(sslInputs, trustManager);

		if (sslContext != null) {
			securityContext.withSSLContext(sslContext, trustManager);
		}

		securityContext.withSSLHostnameVerifier(determineHostnameVerifier());
		return securityContext;
	}

	private DatabaseClientFactory.SecurityContext newSecurityContext(String type, SSLInputs sslInputs) {
		switch (type.toLowerCase()) {
			case DatabaseClientBuilder.AUTH_TYPE_BASIC:
				return newBasicAuthContext();
			case DatabaseClientBuilder.AUTH_TYPE_DIGEST:
				return newDigestAuthContext();
			case DatabaseClientBuilder.AUTH_TYPE_MARKLOGIC_CLOUD:
				return newCloudAuthContext();
			case DatabaseClientBuilder.AUTH_TYPE_KERBEROS:
				return newKerberosAuthContext();
			case DatabaseClientBuilder.AUTH_TYPE_CERTIFICATE:
				return newCertificateAuthContext(sslInputs);
			case DatabaseClientBuilder.AUTH_TYPE_SAML:
				return newSAMLAuthContext();
			default:
				throw new IllegalArgumentException("Unrecognized auth type: " + type);
		}
	}

	private String getRequiredStringValue(String propertyName) {
		Object value = propertySource.apply(PREFIX + propertyName);
		if (value == null || !(value instanceof String)) {
			throw new IllegalArgumentException(propertyName + " must be of type String");
		}
		return (String) value;
	}

	private String getNullableStringValue(String propertyName) {
		Object value = propertySource.apply(PREFIX + propertyName);
		if (value != null && !(value instanceof String)) {
			throw new IllegalArgumentException(propertyName + " must be of type String");
		}
		return (String)value;
	}

	private DatabaseClientFactory.SecurityContext newBasicAuthContext() {
		return new DatabaseClientFactory.BasicAuthContext(
			getRequiredStringValue("username"), getRequiredStringValue("password")
		);
	}

	private DatabaseClientFactory.SecurityContext newDigestAuthContext() {
		return new DatabaseClientFactory.DigestAuthContext(
			getRequiredStringValue("username"), getRequiredStringValue("password")
		);
	}

	private DatabaseClientFactory.SecurityContext newCloudAuthContext() {
		return new DatabaseClientFactory.MarkLogicCloudAuthContext(getRequiredStringValue("cloud.apiKey"));
	}

	private DatabaseClientFactory.SecurityContext newCertificateAuthContext(SSLInputs sslInputs) {
		String file = getNullableStringValue("certificate.file");
		String password = getNullableStringValue("certificate.password");
		if (file != null && file.trim().length() > 0) {
			try {
				if (password != null && password.trim().length() > 0) {
					return new DatabaseClientFactory.CertificateAuthContext(file, password, sslInputs.getTrustManager());
				}
				return new DatabaseClientFactory.CertificateAuthContext(file, sslInputs.getTrustManager());
			} catch (Exception e) {
				throw new RuntimeException("Unable to create CertificateAuthContext; cause " + e.getMessage(), e);
			}
		}
		return new DatabaseClientFactory.CertificateAuthContext(sslInputs.getSslContext(), sslInputs.getTrustManager());
	}

	private DatabaseClientFactory.SecurityContext newKerberosAuthContext() {
		return new DatabaseClientFactory.KerberosAuthContext(getRequiredStringValue("kerberos.principal"));
	}

	private DatabaseClientFactory.SecurityContext newSAMLAuthContext() {
		return new DatabaseClientFactory.SAMLAuthContext(getRequiredStringValue("saml.token"));
	}

	private SSLContext determineSSLContext(SSLInputs sslInputs, X509TrustManager trustManager) {
		String protocol = sslInputs.getSslProtocol();
		if (protocol != null) {
			if ("default".equalsIgnoreCase(protocol)) {
				try {
					return SSLContext.getDefault();
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException("Unable to obtain default SSLContext; cause: " + e.getMessage(), e);
				}
			}

			SSLContext sslContext;
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

	private X509TrustManager determineTrustManager(SSLInputs sslInputs) {
		if (sslInputs.getTrustManager() != null) {
			return sslInputs.getTrustManager();
		}
		// If the user chooses the "default" SSLContext, then it's already been initialized - but OkHttp still
		// needs a separate X509TrustManager, so use the JVM's default trust manager. The assumption is that the
		// default SSLContext was initialized with the JVM's default trust manager. A user can of course always override
		// this by simply providing their own trust manager.
		if ("default".equalsIgnoreCase(sslInputs.getSslProtocol())) {
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

	/**
	 * Uses the given propertySource to construct the inputs pertaining to constructing an SSLContext and an
	 * X509TrustManager.
	 *
	 * @param authType used for applying "default" as the SSL protocol for MarkLogic cloud authentication in
	 *                            case the user does not define their own SSLContext or SSL protocol
	 * @return
	 */
	private SSLInputs buildSSLInputs(String authType) {
		SSLContext sslContext = null;
		Object val = propertySource.apply(PREFIX + "sslContext");
		if (val != null) {
			if (val instanceof SSLContext) {
				sslContext = (SSLContext) val;
			} else {
				throw new IllegalArgumentException("SSL context must be an instanceof " + SSLContext.class.getName());
			}
		}

		String sslProtocol = getNullableStringValue("sslProtocol");
		if (sslContext == null &&
			(sslProtocol == null || sslProtocol.trim().length() == 0) &&
			DatabaseClientBuilder.AUTH_TYPE_MARKLOGIC_CLOUD.equalsIgnoreCase(authType)) {
			sslProtocol = "default";
		}

		val = propertySource.apply(PREFIX + "trustManager");
		X509TrustManager trustManager = null;
		if (val != null) {
			if (val instanceof X509TrustManager) {
				trustManager = (X509TrustManager) val;
			} else {
				throw new IllegalArgumentException("Trust manager must be an instanceof " + X509TrustManager.class.getName());
			}
		}
		return new SSLInputs(sslContext, sslProtocol, trustManager);
	}

	/**
	 * Captures the inputs provided by the caller that pertain to constructing an SSLContext.
	 */
	private static class SSLInputs {
		private final SSLContext sslContext;
		private final String sslProtocol;
		private final X509TrustManager trustManager;

		public SSLInputs(SSLContext sslContext, String sslProtocol, X509TrustManager trustManager) {
			this.sslContext = sslContext;
			this.sslProtocol = sslProtocol;
			this.trustManager = trustManager;
		}

		public SSLContext getSslContext() {
			return sslContext;
		}

		public String getSslProtocol() {
			return sslProtocol;
		}

		public X509TrustManager getTrustManager() {
			return trustManager;
		}
	}
}
