/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

import com.marklogic.client.impl.DatabaseClientPropertySource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Intended to support programmatically building a {@code DatabaseClient} via chained "with" methods for setting
 * each possible input allowed for connecting to and authenticating with MarkLogic. While the
 * {@code DatabaseClientFactory.Bean} class is intended for use in a context such as a Spring container, it requires
 * that a user have already assembled the appropriate {@code DatabaseClientFactory.SecurityContext}. This builder
 * instead is intended for a more dynamic environment - in particular, one where the desired authentication strategy is
 * not known until runtime. A client can then collect inputs from a user at runtime and call the appropriate methods on
 * this builder. The builder will handle constructing the correct {@code DatabaseClientFactory.SecurityContext} and
 * using that to construct a {@code DatabaseClient}.
 *
 * @since 6.1.0
 */
public class DatabaseClientBuilder {

	public final static String PREFIX = "marklogic.client.";
	public final static String AUTH_TYPE_BASIC = "basic";
	public final static String AUTH_TYPE_DIGEST = "digest";
	public final static String AUTH_TYPE_MARKLOGIC_CLOUD = "cloud";
	public final static String AUTH_TYPE_KERBEROS = "kerberos";
	public final static String AUTH_TYPE_CERTIFICATE = "certificate";
	public final static String AUTH_TYPE_SAML = "saml";
	public final static String AUTH_TYPE_OAUTH = "oauth";

	private final Map<String, Object> props;

	public DatabaseClientBuilder() {
		this.props = new HashMap<>();
	}

	/**
	 * Initialize the builder with the given set of properties.
	 *
	 * @param props
	 */
	public DatabaseClientBuilder(Map<String, Object> props) {
		this();
		this.props.putAll(props);
	}

	/**
	 * @return a {@code DatabaseClient} based on the inputs that have been provided via the "with" builder methods
	 * and any inputs provided via this instance's constructor
	 */
	public DatabaseClient build() {
		return DatabaseClientFactory.newClient(getPropertySource());
	}

	/**
	 * @return an instance of {@code DatabaseClientFactory.Bean}  based on the inputs that have been provided via
	 * the "with" builder methods and any inputs provided via this instance's constructor
	 */
	public DatabaseClientFactory.Bean buildBean() {
		return new DatabaseClientPropertySource(getPropertySource()).newClientBean();
	}

	/**
	 * @return a function that acts as a property source, specifically for use with the
	 * {@code DatabaseClientFactory.newClient} method that accepts this type of function.
	 */
	private Function<String, Object> getPropertySource() {
		return propertyName -> props.get(propertyName);
	}

	public DatabaseClientBuilder withHost(String host) {
		props.put(PREFIX + "host", host);
		return this;
	}

	public DatabaseClientBuilder withPort(int port) {
		props.put(PREFIX + "port", port);
		return this;
	}

	public DatabaseClientBuilder withBasePath(String basePath) {
		props.put(PREFIX + "basePath", basePath);
		return this;
	}

	public DatabaseClientBuilder withDatabase(String database) {
		props.put(PREFIX + "database", database);
		return this;
	}

	public DatabaseClientBuilder withUsername(String username) {
		props.put(PREFIX + "username", username);
		return this;
	}

	public DatabaseClientBuilder withPassword(String password) {
		props.put(PREFIX + "password", password);
		return this;
	}

	public DatabaseClientBuilder withSecurityContext(DatabaseClientFactory.SecurityContext securityContext) {
		props.put(PREFIX + "securityContext", securityContext);
		return this;
	}

	/**
	 * @param type must be one of "basic", "digest", "cloud", "kerberos", "certificate", or "saml"
	 * @return
	 */
	public DatabaseClientBuilder withAuthType(String type) {
		props.put(PREFIX + "authType", type);
		return this;
	}

	public DatabaseClientBuilder withBasicAuth(String username, String password) {
		return withAuthType(AUTH_TYPE_BASIC)
			.withUsername(username)
			.withPassword(password);
	}

	public DatabaseClientBuilder withDigestAuth(String username, String password) {
		return withAuthType(AUTH_TYPE_DIGEST)
			.withUsername(username)
			.withPassword(password);
	}

	public DatabaseClientBuilder withCloudAuth(String apiKey, String basePath) {
		return withAuthType(AUTH_TYPE_MARKLOGIC_CLOUD)
			.withCloudApiKey(apiKey)
			.withBasePath(basePath);
	}

	/**
	 * @param apiKey
	 * @param basePath
	 * @param tokenDuration length in minutes until the generated access token expires
	 * @return
	 * @since 6.3.0
	 */
	public DatabaseClientBuilder withCloudAuth(String apiKey, String basePath, Integer tokenDuration) {
		return withAuthType(AUTH_TYPE_MARKLOGIC_CLOUD)
			.withCloudApiKey(apiKey)
			.withBasePath(basePath)
			.withCloudTokenDuration(tokenDuration != null ? tokenDuration.toString() : null);
	}

	public DatabaseClientBuilder withKerberosAuth(String principal) {
		return withAuthType(AUTH_TYPE_KERBEROS)
			.withKerberosPrincipal(principal);
	}

	public DatabaseClientBuilder withCertificateAuth(String file, String password) {
		return withAuthType(AUTH_TYPE_CERTIFICATE)
			.withCertificateFile(file)
			.withCertificatePassword(password);
	}

	/**
	 * @param sslContext
	 * @param trustManager
	 * @return
	 * @since 6.2.2
	 */
	public DatabaseClientBuilder withCertificateAuth(SSLContext sslContext, X509TrustManager trustManager) {
		return withAuthType(AUTH_TYPE_CERTIFICATE)
			.withSSLContext(sslContext)
			.withTrustManager(trustManager);
	}

	public DatabaseClientBuilder withSAMLAuth(String token) {
		return withAuthType(AUTH_TYPE_SAML)
			.withSAMLToken(token);
	}

	/**
	 * @param token
	 * @return
	 * @since 6.6.0
	 */
	public DatabaseClientBuilder withOAuth(String token) {
		return withAuthType(AUTH_TYPE_OAUTH).withOAuthToken(token);
	}

	/**
	 * @param token
	 * @return
	 * @since 6.6.0
	 */
	public DatabaseClientBuilder withOAuthToken(String token) {
		props.put(PREFIX + "oauth.token", token);
		return this;
	}

	public DatabaseClientBuilder withConnectionType(DatabaseClient.ConnectionType type) {
		props.put(PREFIX + "connectionType", type);
		return this;
	}

	public DatabaseClientBuilder withCloudApiKey(String cloudApiKey) {
		props.put(PREFIX + "cloud.apiKey", cloudApiKey);
		return this;
	}

	/**
	 * @param tokenDuration length in minutes until the generated access token expires
	 * @return
	 * @since 6.3.0
	 */
	public DatabaseClientBuilder withCloudTokenDuration(String tokenDuration) {
		props.put(PREFIX + "cloud.tokenDuration", tokenDuration);
		return this;
	}

	public DatabaseClientBuilder withCertificateFile(String file) {
		props.put(PREFIX + "certificate.file", file);
		return this;
	}

	public DatabaseClientBuilder withCertificatePassword(String password) {
		props.put(PREFIX + "certificate.password", password);
		return this;
	}

	public DatabaseClientBuilder withKerberosPrincipal(String principal) {
		props.put(PREFIX + "kerberos.principal", principal);
		return this;
	}

	public DatabaseClientBuilder withSAMLToken(String token) {
		props.put(PREFIX + "saml.token", token);
		return this;
	}

	public DatabaseClientBuilder withSSLContext(SSLContext sslContext) {
		props.put(PREFIX + "sslContext", sslContext);
		return this;
	}

	public DatabaseClientBuilder withSSLProtocol(String sslProtocol) {
		props.put(PREFIX + "sslProtocol", sslProtocol);
		return this;
	}

	public DatabaseClientBuilder withTrustManager(X509TrustManager trustManager) {
		props.put(PREFIX + "trustManager", trustManager);
		return this;
	}

	public DatabaseClientBuilder withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier sslHostnameVerifier) {
		props.put(PREFIX + "sslHostnameVerifier", sslHostnameVerifier);
		return this;
	}

	/**
	 * Prevents the underlying OkHttp library from sending an "Accept-Encoding-gzip" request header on each request.
	 *
	 * @return
	 * @since 6.3.0
	 */
	public DatabaseClientBuilder withGzippedResponsesDisabled() {
		props.put(PREFIX + "disableGzippedResponses", true);
		return this;
	}

	/**
	 * Enables 2-way SSL by creating an SSL context based on the given key store path.
	 *
	 * @param path
	 * @return
	 * @since 6.4.0
	 */
	public DatabaseClientBuilder withKeyStorePath(String path) {
		props.put(PREFIX + "ssl.keystore.path", path);
		return this;
	}

	/**
	 * @param password optional password for a key store
	 * @return
	 * @since 6.4.0
	 */
	public DatabaseClientBuilder withKeyStorePassword(String password) {
		props.put(PREFIX + "ssl.keystore.password", password);
		return this;
	}

	/**
	 * @param type e.g. "JKS"
	 * @return
	 * @since 6.4.0
	 */
	public DatabaseClientBuilder withKeyStoreType(String type) {
		props.put(PREFIX + "ssl.keystore.type", type);
		return this;
	}

	/**
	 * @param algorithm e.g. "SunX509"
	 * @return
	 * @since 6.4.0
	 */
	public DatabaseClientBuilder withKeyStoreAlgorithm(String algorithm) {
		props.put(PREFIX + "ssl.keystore.algorithm", algorithm);
		return this;
	}

	/**
	 * Supports constructing an {@code X509TrustManager} based on the given file path, which should point to a Java
	 * key store or trust store.
	 *
	 * @param path
	 * @return
	 * @since 6.5.0
	 */
	public DatabaseClientBuilder withTrustStorePath(String path) {
		props.put(PREFIX + "ssl.truststore.path", path);
		return this;
	}

	/**
	 * @param password optional password for a trust store
	 * @return
	 * @since 6.5.0
	 */
	public DatabaseClientBuilder withTrustStorePassword(String password) {
		props.put(PREFIX + "ssl.truststore.password", password);
		return this;
	}

	/**
	 * @param type e.g. "JKS"
	 * @return
	 * @since 6.5.0
	 */
	public DatabaseClientBuilder withTrustStoreType(String type) {
		props.put(PREFIX + "ssl.truststore.type", type);
		return this;
	}

	/**
	 * @param algorithm e.g. "SunX509"
	 * @return
	 * @since 6.5.0
	 */
	public DatabaseClientBuilder withTrustStoreAlgorithm(String algorithm) {
		props.put(PREFIX + "ssl.truststore.algorithm", algorithm);
		return this;
	}
}



