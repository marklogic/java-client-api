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
import com.marklogic.client.extra.okhttpclient.RemoveAcceptEncodingConfigurator;

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
		connectionPropertyHandlers.put(PREFIX + "disableGzippedResponses", (bean, value) -> {
			boolean disableGzippedResponses = false;
			if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
				disableGzippedResponses = true;
			} else if (value instanceof String) {
				disableGzippedResponses = Boolean.parseBoolean((String) value);
			}
			if (disableGzippedResponses) {
				DatabaseClientFactory.addConfigurator(new RemoveAcceptEncodingConfigurator());
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
		final String authType = (String) typeValue;

		final SSLInputs sslInputs = buildSSLInputs(authType);
		DatabaseClientFactory.SecurityContext securityContext = newSecurityContext(authType, sslInputs);
		if (sslInputs.getSslContext() != null) {
			securityContext.withSSLContext(sslInputs.getSslContext(), sslInputs.getTrustManager());
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
		return (String) value;
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
		String apiKey = getRequiredStringValue("cloud.apiKey");
		String val = getNullableStringValue("cloud.tokenDuration");
		Integer duration = null;
		if (val != null) {
			try {
				duration = Integer.parseInt(val);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Cloud token duration must be numeric");
			}
		}
		return new DatabaseClientFactory.MarkLogicCloudAuthContext(apiKey, duration);
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
	 *                 case the user does not define their own SSLContext or SSL protocol
	 * @return
	 */
	private SSLInputs buildSSLInputs(String authType) {
		X509TrustManager userTrustManager = getTrustManager();

		// Approach 1 - user provides an SSLContext object, in which case there's nothing further to check.
		SSLContext sslContext = getSSLContext();
		if (sslContext != null) {
			return new SSLInputs(sslContext, userTrustManager);
		}

		// Approaches 2 and 3 - user defines an SSL protocol.
		// Approach 2 - "default" is a convenience for using the JVM's default SSLContext.
		// Approach 3 - create a new SSLContext, and initialize it if the user-provided TrustManager is not null.
		final String sslProtocol = getSSLProtocol(authType);
		if (sslProtocol != null) {
			return "default".equalsIgnoreCase(sslProtocol) ?
				useDefaultSSLContext(userTrustManager) :
				useNewSSLContext(sslProtocol, userTrustManager);
		}

		// Approach 4 - no SSL connection is needed.
		return new SSLInputs(null, null);
	}

	private X509TrustManager getTrustManager() {
		Object val = propertySource.apply(PREFIX + "trustManager");
		if (val != null) {
			if (val instanceof X509TrustManager) {
				return (X509TrustManager) val;
			} else {
				throw new IllegalArgumentException("Trust manager must be an instanceof " + X509TrustManager.class.getName());
			}
		}
		return null;
	}

	private SSLContext getSSLContext() {
		Object val = propertySource.apply(PREFIX + "sslContext");
		if (val != null) {
			if (val instanceof SSLContext) {
				return (SSLContext) val;
			} else {
				throw new IllegalArgumentException("SSL context must be an instanceof " + SSLContext.class.getName());
			}
		}
		return null;
	}

	private String getSSLProtocol(String authType) {
		String sslProtocol = getNullableStringValue("sslProtocol");
		if (sslProtocol != null) {
			sslProtocol = sslProtocol.trim();
		}
		// For convenience for MarkLogic Cloud users, assume the JVM's default SSLContext should trust the certificate
		// used by MarkLogic Cloud. A user can always override this default behavior by providing their own SSLContext.
		if ((sslProtocol == null || sslProtocol.length() == 0) && DatabaseClientBuilder.AUTH_TYPE_MARKLOGIC_CLOUD.equalsIgnoreCase(authType)) {
			sslProtocol = "default";
		}
		return sslProtocol;
	}

	/**
	 * Uses the JVM's default SSLContext. Because OkHttp requires a separate TrustManager, this approach will either
	 * user the user-provided TrustManager or it will assume that the JVM's default TrustManager should be used.
	 */
	private SSLInputs useDefaultSSLContext(X509TrustManager userTrustManager) {
		SSLContext sslContext;
		try {
			sslContext = SSLContext.getDefault();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to obtain default SSLContext; cause: " + e.getMessage(), e);
		}
		X509TrustManager trustManager = userTrustManager != null ? userTrustManager : SSLUtil.getDefaultTrustManager();
		return new SSLInputs(sslContext, trustManager);
	}

	/**
	 * Constructs a new SSLContext based on the given protocol (e.g. TLSv1.2). The SSLContext will be initialized if
	 * the user's TrustManager is not null. Otherwise, OkHttpUtil will eventually initialize the SSLContext using the
	 * JVM's default TrustManager.
	 */
	private SSLInputs useNewSSLContext(String sslProtocol, X509TrustManager userTrustManager) {
		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance(sslProtocol);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(String.format("Unable to get SSLContext instance with protocol: %s; cause: %s",
				sslProtocol, e.getMessage()), e);
		}
		if (userTrustManager != null) {
			try {
				sslContext.init(null, new X509TrustManager[]{userTrustManager}, null);
			} catch (KeyManagementException e) {
				throw new RuntimeException(String.format("Unable to initialize SSLContext; protocol: %s; cause: %s",
					sslProtocol, e.getMessage()), e);
			}
		}
		return new SSLInputs(sslContext, userTrustManager);
	}

	/**
	 * Captures the inputs provided by the caller that pertain to constructing an SSLContext.
	 */
	private static class SSLInputs {
		private final SSLContext sslContext;
		private final X509TrustManager trustManager;

		public SSLInputs(SSLContext sslContext, X509TrustManager trustManager) {
			this.sslContext = sslContext;
			this.trustManager = trustManager;
		}

		public SSLContext getSslContext() {
			return sslContext;
		}

		public X509TrustManager getTrustManager() {
			return trustManager;
		}
	}
}
