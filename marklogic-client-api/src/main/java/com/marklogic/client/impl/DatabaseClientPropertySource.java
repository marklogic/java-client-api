/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.extra.okhttpclient.RemoveAcceptEncodingConfigurator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.KeyStore;
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
		connectionPropertyHandlers.put(PREFIX + "connectionString", (bean, value) -> {
			if (value instanceof String) {
				ConnectionString cs = new ConnectionString((String) value, "connection string");
				bean.setHost(cs.getHost());
				bean.setPort(cs.getPort());
				if (cs.getDatabase() != null && cs.getDatabase().trim().length() > 0) {
					bean.setDatabase(cs.getDatabase());
				}
			} else {
				throw new IllegalArgumentException("Connection string must be of type String");
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

	private ConnectionString makeConnectionString() {
		String value = (String) propertySource.apply(PREFIX + "connectionString");
		return value != null && value.trim().length() > 0 ? new ConnectionString(value, "connection string") : null;
	}

	private DatabaseClientFactory.SecurityContext newSecurityContext() {
		Object securityContextValue = propertySource.apply(PREFIX + "securityContext");
		if (securityContextValue != null) {
			if (securityContextValue instanceof DatabaseClientFactory.SecurityContext) {
				return (DatabaseClientFactory.SecurityContext) securityContextValue;
			}
			throw new IllegalArgumentException("Security context must be of type " + DatabaseClientFactory.SecurityContext.class.getName());
		}

		ConnectionString connectionString = makeConnectionString();
		final String authType = determineAuthType(connectionString);

		final SSLUtil.SSLInputs sslInputs = buildSSLInputs(authType);
		DatabaseClientFactory.SecurityContext securityContext = newSecurityContext(authType, connectionString, sslInputs);
		if (sslInputs.getSslContext() != null) {
			securityContext.withSSLContext(sslInputs.getSslContext(), sslInputs.getTrustManager());
		}
		securityContext.withSSLHostnameVerifier(determineHostnameVerifier());
		return securityContext;
	}

	private String determineAuthType(ConnectionString connectionString) {
		Object value = propertySource.apply(PREFIX + "authType");
		if (value == null && connectionString != null) {
			return "digest";
		}
		if (value == null || !(value instanceof String)) {
			throw new IllegalArgumentException("Security context should be set, or auth type must be of type String");
		}
		return (String) value;
	}

	private DatabaseClientFactory.SecurityContext newSecurityContext(String type, ConnectionString connectionString, SSLUtil.SSLInputs sslInputs) {
		switch (type.toLowerCase()) {
			case DatabaseClientBuilder.AUTH_TYPE_BASIC:
				return newBasicAuthContext(connectionString);
			case DatabaseClientBuilder.AUTH_TYPE_DIGEST:
				return newDigestAuthContext(connectionString);
			case DatabaseClientBuilder.AUTH_TYPE_MARKLOGIC_CLOUD:
				return newCloudAuthContext();
			case DatabaseClientBuilder.AUTH_TYPE_KERBEROS:
				return newKerberosAuthContext();
			case DatabaseClientBuilder.AUTH_TYPE_CERTIFICATE:
				return newCertificateAuthContext(sslInputs);
			case DatabaseClientBuilder.AUTH_TYPE_SAML:
				return newSAMLAuthContext();
			case DatabaseClientBuilder.AUTH_TYPE_OAUTH:
				return newOAuthContext();
			default:
				throw new IllegalArgumentException("Unrecognized auth type: " + type);
		}
	}

	private String getRequiredStringValue(String propertyName) {
		return getRequiredStringValue(propertyName, String.format("%s must be of type String", propertyName));
	}

	private String getRequiredStringValue(String propertyName, String errorMessage) {
		Object value = propertySource.apply(PREFIX + propertyName);
		if (value == null || !(value instanceof String)) {
			throw new IllegalArgumentException(errorMessage);
		}
		return (String) value;
	}

	private String getNullableStringValue(String propertyName) {
		return getNullableStringValue(propertyName, null);
	}

	private String getNullableStringValue(String propertyName, String defaultValue) {
		Object value = propertySource.apply(PREFIX + propertyName);
		if (value != null && !(value instanceof String)) {
			throw new IllegalArgumentException(propertyName + " must be of type String");
		}
		return value != null ? (String) value : defaultValue;
	}

	private DatabaseClientFactory.SecurityContext newBasicAuthContext(ConnectionString connectionString) {
		if (connectionString != null) {
			return new DatabaseClientFactory.BasicAuthContext(
				connectionString.getUsername(), connectionString.getPassword()
			);
		}
		return new DatabaseClientFactory.BasicAuthContext(
			getRequiredStringValue("username", "Must specify a username when using basic authentication."),
			getRequiredStringValue("password", "Must specify a password when using basic authentication.")
		);
	}

	private DatabaseClientFactory.SecurityContext newDigestAuthContext(ConnectionString connectionString) {
		if (connectionString != null) {
			return new DatabaseClientFactory.DigestAuthContext(
				connectionString.getUsername(), connectionString.getPassword()
			);
		}
		return new DatabaseClientFactory.DigestAuthContext(
			getRequiredStringValue("username", "Must specify a username when using digest authentication."),
			getRequiredStringValue("password", "Must specify a password when using digest authentication.")
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

	private DatabaseClientFactory.SecurityContext newCertificateAuthContext(SSLUtil.SSLInputs sslInputs) {
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
		if (sslInputs.getSslContext() == null) {
			throw new RuntimeException("An SSLContext is required for certificate authentication.");
		}
		return new DatabaseClientFactory.CertificateAuthContext(sslInputs.getSslContext(), sslInputs.getTrustManager());
	}

	private DatabaseClientFactory.SecurityContext newKerberosAuthContext() {
		return new DatabaseClientFactory.KerberosAuthContext(getRequiredStringValue("kerberos.principal"));
	}

	private DatabaseClientFactory.SecurityContext newSAMLAuthContext() {
		return new DatabaseClientFactory.SAMLAuthContext(getRequiredStringValue("saml.token"));
	}

	private DatabaseClientFactory.SecurityContext newOAuthContext() {
		return new DatabaseClientFactory.OAuthContext(getRequiredStringValue("oauth.token"));
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
	private SSLUtil.SSLInputs buildSSLInputs(String authType) {
		X509TrustManager userTrustManager = getTrustManager();

		// Approach 1 - user provides an SSLContext object, in which case there's nothing further to check.
		SSLContext sslContext = getSSLContext();
		if (sslContext != null) {
			return new SSLUtil.SSLInputs(sslContext, userTrustManager);
		}

		// Approach 2 - user wants two-way SSL via a keystore.
		final String keyStorePath = getNullableStringValue("ssl.keystore.path");
		if (keyStorePath != null && keyStorePath.trim().length() > 0) {
			return useKeyStoreForTwoWaySSL(keyStorePath, userTrustManager);
		}

		// Approaches 3 and 4 - user defines an SSL protocol.
		// Approach 3 - "default" is a convenience for using the JVM's default SSLContext.
		// Approach 4 - create a new SSLContext, and initialize it if the user-provided TrustManager is not null.
		final String sslProtocol = getSSLProtocol(authType);
		if (sslProtocol != null) {
			return "default".equalsIgnoreCase(sslProtocol) ?
				useDefaultSSLContext(userTrustManager) :
				useNewSSLContext(sslProtocol, userTrustManager);
		}

		// Approach 5 - still return the user-defined TrustManager as that may be needed for certificate authentication,
		// which has its own way of constructing an SSLContext from a PKCS12 file.
		return new SSLUtil.SSLInputs(null, userTrustManager);
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

		String path = getNullableStringValue("ssl.truststore.path");
		if (path != null && path.trim().length() > 0) {
			return buildTrustManagerFromTrustStorePath(path);
		}

		return null;
	}

	/**
	 * Added in 6.5.0 to support configuring a trust manager via properties.
	 *
	 * @param path
	 * @return
	 */
	private X509TrustManager buildTrustManagerFromTrustStorePath(String path) {
		final String password = getNullableStringValue("ssl.truststore.password");
		final String type = getNullableStringValue("ssl.truststore.type", "JKS");
		final String algorithm = getNullableStringValue("ssl.truststore.algorithm", "SunX509");
		KeyStore trustStore = SSLUtil.getKeyStore(path, password != null ? password.toCharArray() : null, type);
		return (X509TrustManager) SSLUtil.getTrustManagers(algorithm, trustStore)[0];
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

	private SSLUtil.SSLInputs useKeyStoreForTwoWaySSL(String keyStorePath, X509TrustManager userTrustManager) {
		final String password = getNullableStringValue("ssl.keystore.password");
		final String keyStoreType = getNullableStringValue("ssl.keystore.type", "JKS");
		final String algorithm = getNullableStringValue("ssl.keystore.algorithm", "SunX509");
		final char[] charPassword = password != null ? password.toCharArray() : null;
		final String sslProtocol = getNullableStringValue("sslProtocol", "TLSv1.2");
		return SSLUtil.createSSLContextFromKeyStore(keyStorePath, charPassword, keyStoreType, algorithm, sslProtocol, userTrustManager);
	}

	/**
	 * Uses the JVM's default SSLContext. Because OkHttp requires a separate TrustManager, this approach will either
	 * user the user-provided TrustManager or it will assume that the JVM's default TrustManager should be used.
	 */
	private SSLUtil.SSLInputs useDefaultSSLContext(X509TrustManager userTrustManager) {
		SSLContext sslContext;
		try {
			sslContext = SSLContext.getDefault();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to obtain default SSLContext; cause: " + e.getMessage(), e);
		}
		X509TrustManager trustManager = userTrustManager != null ? userTrustManager : SSLUtil.getDefaultTrustManager();
		return new SSLUtil.SSLInputs(sslContext, trustManager);
	}

	/**
	 * Constructs a new SSLContext based on the given protocol (e.g. TLSv1.2). The SSLContext will be initialized if
	 * the user's TrustManager is not null. Otherwise, OkHttpUtil will eventually initialize the SSLContext using the
	 * JVM's default TrustManager.
	 */
	private SSLUtil.SSLInputs useNewSSLContext(String sslProtocol, X509TrustManager userTrustManager) {
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
		return new SSLUtil.SSLInputs(sslContext, userTrustManager);
	}
}
