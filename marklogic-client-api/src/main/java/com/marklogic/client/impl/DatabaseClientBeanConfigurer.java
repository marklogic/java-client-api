package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Configure a DatabaseClientFactory.Bean via a property source Function.
 * Returning a Bean seems fine here since it's an internal class.
 */
public class DatabaseClientBeanConfigurer {

	private final static String PREFIX = "marklogic.connection.";

	public static DatabaseClientFactory.Bean newClient(Function<String, Object> propertySource) {
		Map<String, BiConsumer<DatabaseClientFactory.Bean, Object>> propertyHandlers = new LinkedHashMap<>();
		propertyHandlers.put(PREFIX + "host", (bean, value) -> bean.setHost((String) value));
		propertyHandlers.put(PREFIX + "port", (bean, value) -> {
			if (value instanceof String) {
				bean.setPort(Integer.parseInt((String) value));
			} else {
				bean.setPort((int) value);
			}
		});
		propertyHandlers.put(PREFIX + "database", (bean, value) -> bean.setDatabase((String) value));
		propertyHandlers.put(PREFIX + "basePath", (bean, value) -> bean.setBasePath((String) value));
		propertyHandlers.put(PREFIX + "type", (bean, value) -> {
			if (value instanceof DatabaseClient.ConnectionType) {
				bean.setConnectionType((DatabaseClient.ConnectionType) value);
			} else if (value instanceof String) {
				String val = (String)value;
				if (val.trim().length() > 0) {
					bean.setConnectionType(DatabaseClient.ConnectionType.valueOf(val.toUpperCase()));
				}
			} else
				throw new IllegalArgumentException("Connection type must either be a String or an instance of ConnectionType");
		});

		final DatabaseClientFactory.Bean bean = new DatabaseClientFactory.Bean();
		propertyHandlers.forEach((propName, consumer) -> {
			Object propValue = propertySource.apply(propName);
			if (propValue != null) {
				consumer.accept(bean, propValue);
			}
		});
		bean.setSecurityContext(newSecurityContext(propertySource));
		return bean;
	}

	/**
	 * TODO Thinking this should be public so that ml-app-deployer can obtain this and then pass it to the
	 * perhaps-public method for creating an OkHttp Client.
	 *
	 * @param propertySource
	 * @return
	 */
	public static DatabaseClientFactory.SecurityContext newSecurityContext(Function<String, Object> propertySource) {
		DatabaseClientFactory.SecurityContext securityContext;
		String type = (String) propertySource.apply(PREFIX + "securityContextType");
		if (type == null) {
			type = "digest";
		}
		switch (type.toLowerCase()) {
			case "basic":
				securityContext = new DatabaseClientFactory.BasicAuthContext(
					(String) propertySource.apply(PREFIX + "username"),
					(String) propertySource.apply(PREFIX + "password")
				);
				break;
			case "digest":
				securityContext = new DatabaseClientFactory.DigestAuthContext(
					(String) propertySource.apply(PREFIX + "username"),
					(String) propertySource.apply(PREFIX + "password")
				);
				break;
			case "cloud":
				securityContext = new DatabaseClientFactory.MarkLogicCloudAuthContext(
					(String) propertySource.apply(PREFIX + "cloud.apiKey")
				);
				break;
			case "kerberos":
				// TODO Should support kerberos.* for all other options in case principal is not specified
				securityContext = new DatabaseClientFactory.KerberosAuthContext(
					(String) propertySource.apply(PREFIX + "kerberos.principal")
				);
				break;
			case "certificate":
				try {
					securityContext = new DatabaseClientFactory.CertificateAuthContext(
						(String) propertySource.apply(PREFIX + "certificate.file"),
						(String) propertySource.apply(PREFIX + "certificate.password"),
						determineTrustManager(propertySource)
					);
				} catch (Exception e) {
					throw new RuntimeException("Unable to create CertificateAuthContext; cause " + e.getMessage(), e);
				}
				break;
			case "saml":
				// TODO The best we can really do here is to support an authorization token, as the other two
				// constructors really require programming to implement.
				Object token = propertySource.apply(PREFIX + "saml.token");
				if (token == null || !(token instanceof String)) {
					throw new IllegalArgumentException("Cannot create SAMLAuthContext, unable to find token");
				}
				securityContext = new DatabaseClientFactory.SAMLAuthContext((String) token);
				break;
			default:
				throw new IllegalArgumentException("Unrecognized security context type: " + type);
		}

		SSLContext sslContext = determineSSLContext(propertySource);
		if (sslContext != null) {
			securityContext.withSSLContext(sslContext, determineTrustManager(propertySource));
		}
		securityContext.withSSLHostnameVerifier(determineHostnameVerifier(propertySource));
		return securityContext;
	}

	private static SSLContext determineSSLContext(Function<String, Object> propertySource) {
		Object sslContext = propertySource.apply(PREFIX + "sslContext");
		if (sslContext instanceof SSLContext) {
			return (SSLContext) sslContext;
		}
		String protocol = (String) propertySource.apply(PREFIX + "sslProtocol");
		// TODO Find out what the deal is here again with when the Java Client will try to initialize the
		// SSLContext itself

		// TODO Do we want to support what ml-javaclient-util does with sslProtocol and trustManagerAlgorithm?
		// Looks like we don't need to, because ml-javaclient-util and ml-app-deployer both provide SSLContext and
		// X509TrustManager instances
		// So this really just becomes a bonus feature, which DatabaseClientConfig already supports in ml-javaclient-util,
		// but we could hold off on for now in the Java Client.
		// The benefit of doing something here is to make life easier for tools that don't yet use ml-javaclient-util and
		// want an easy way of specifying the protocol and not having to provide an SSLContext object
		if (protocol != null) {
			if ("default".equalsIgnoreCase(protocol)) {
				try {
					return SSLContext.getDefault();
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException("Unable to obtain default SSLContext; cause: " + e.getMessage(), e);
				}
			}
			try {
				return SSLContext.getInstance(protocol);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("Unable to get SSLContext instance with protocol: " + protocol
					+ "; cause: " + e.getMessage(), e);
			}
		}
		return null;
	}

	private static X509TrustManager determineTrustManager(Function<String, Object> propertySource) {
		Object trustManagerObject = propertySource.apply(PREFIX + "trustManager");
		if (trustManagerObject != null && trustManagerObject instanceof X509TrustManager) {
			return (X509TrustManager) trustManagerObject;
		}
		// TODO Add "noop"/"simple" support? Doesn't seem necessary, because if this is null, OkHttpClientFactory will
		// try to obtain one from the JVM
		return null;
	}

	private static DatabaseClientFactory.SSLHostnameVerifier determineHostnameVerifier(Function<String, Object> propertySource) {
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
			throw new IllegalArgumentException("Unrecognized value for SSLHostnameVerifier: " + verifier);
		}
		return null;
	}
}
