/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * SSL convenience methods that are stored in the "impl" package, but we may eventually want to make these officially
 * public, particular for reuse in connectors.
 */
public abstract class SSLUtil {

	/**
	 * @return an X509TrustManager based on the JVM's default trust manager algorithm. How this is constructed can vary
	 * based on the JVM type and version. One common approach is for the JVM to constructs this based on its
	 * ./jre/lib/security/cacerts file.
	 */
	public static X509TrustManager getDefaultTrustManager() {
		X509TrustManager trustManager = (X509TrustManager) getDefaultTrustManagers()[0];
		Logger logger = LoggerFactory.getLogger(SSLUtil.class);
		if (logger.isDebugEnabled() && trustManager.getAcceptedIssuers() != null) {
			logger.debug("Count of accepted issuers in default trust manager: {}",
				trustManager.getAcceptedIssuers().length);
		}
		return trustManager;
	}

	/**
	 * @return a non-empty array of TrustManager instances based on the JVM's default trust manager algorithm, with the
	 * first trust manager guaranteed to be an instance of X509TrustManager.
	 */
	public static TrustManager[] getDefaultTrustManagers() {
		final String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		return getTrustManagers(defaultAlgorithm, null);
	}

	/**
	 * @param trustManagerAlgorithm e.g. "SunX509".
	 * @param optionalKeyStore      if not null, used to initialize the TrustManagerFactory constructed based on the
	 *                              given algorithm.
	 * @return an array of at least length 1 where the first instance is an {@code X509TrustManager}
	 */
	public static TrustManager[] getTrustManagers(String trustManagerAlgorithm, KeyStore optionalKeyStore) {
		TrustManagerFactory trustManagerFactory;
		try {
			trustManagerFactory = TrustManagerFactory.getInstance(trustManagerAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
				"Unable to obtain trust manager factory using algorithm: " + trustManagerAlgorithm, e);
		}

		try {
			trustManagerFactory.init(optionalKeyStore);
		} catch (KeyStoreException e) {
			throw new RuntimeException(String.format(
				"Unable to initialize trust manager factory obtained using algorithm: %s; cause: %s",
				trustManagerAlgorithm, e.getMessage()), e);
		}

		TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
		if (trustManagers == null || trustManagers.length == 0) {
			throw new RuntimeException("No trust managers found using algorithm: " + trustManagerAlgorithm);
		}
		if (!(trustManagers[0] instanceof X509TrustManager)) {
			throw new RuntimeException("Default trust manager is not an X509TrustManager: " + trustManagers[0]);
		}
		return trustManagers;
	}

	/**
	 * Captures the oft-repeated boilerplate Java code for creating an SSLContext based on a key store.
	 *
	 * @param keyStorePath             required path to a key store file
	 * @param keyStorePassword         optional password, can be null
	 * @param keyStoreType             type of key store, e.g. "JKS"
	 * @param algorithm                key store algorithm, e.g. "SunX509"
	 * @param sslProtocol              e.g. "TLSv1.2"
	 * @param userProvidedTrustManager optional trust manager provided by a user; if not null, will be used to
	 *                                 initialize the SSLContext instead of using the key store as a trust manager.
	 * @return
	 */
	static SSLInputs createSSLContextFromKeyStore(String keyStorePath, char[] keyStorePassword, String keyStoreType,
												  String algorithm, String sslProtocol,
												  X509TrustManager userProvidedTrustManager) {

		KeyStore keyStore = getKeyStore(keyStorePath, keyStorePassword, keyStoreType);
		KeyManagerFactory keyManagerFactory = newKeyManagerFactory(keyStore, keyStorePassword, algorithm);
		SSLContext sslContext = newSSLContext(sslProtocol);

		TrustManager[] trustManagers = userProvidedTrustManager != null
			? new X509TrustManager[]{userProvidedTrustManager}
			: getTrustManagers(algorithm, keyStore);

		try {
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
		} catch (KeyManagementException ex) {
			throw new RuntimeException("Unable to initialize SSL context", ex);
		}

		return new SSLInputs(sslContext, (X509TrustManager) trustManagers[0]);
	}

	/**
	 * @return a Java KeyStore based on the given inputs.
	 */
	public static KeyStore getKeyStore(String keyStorePath, char[] keyStorePassword, String keyStoreType) {
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance(keyStoreType);
		} catch (KeyStoreException ex) {
			throw new RuntimeException("Unable to get instance of key store with type: " + keyStoreType, ex);
		}

		try (InputStream inputStream = new FileInputStream(keyStorePath)) {
			keyStore.load(inputStream, keyStorePassword);
			return keyStore;
		} catch (Exception ex) {
			throw new RuntimeException("Unable to read from key store at path: " + keyStorePath, ex);
		}
	}

	private static KeyManagerFactory newKeyManagerFactory(KeyStore keyStore, char[] keyStorePassword, String algorithm) {
		KeyManagerFactory keyManagerFactory;
		try {
			keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Unable to create key manager factory with algorithm: " + algorithm, ex);
		}

		try {
			keyManagerFactory.init(keyStore, keyStorePassword);
		} catch (Exception ex) {
			throw new RuntimeException("Unable to initialize key manager factory", ex);
		}
		return keyManagerFactory;
	}

	private static SSLContext newSSLContext(String sslProtocol) {
		try {
			return SSLContext.getInstance(sslProtocol);
		} catch (Exception ex) {
			throw new RuntimeException("Unable to create SSL context using protocol: " + sslProtocol, ex);
		}
	}

	/**
	 * Captures the inputs needed by the Java Client for establishing an SSL connection. The need for a separate
	 * X509TrustManager arose from the switch from Apache's HttpClient to OkHttp, where the latter needs access to a
	 * X509TrustManager (as opposed to relying on any trust managers within an SSLContext).
	 */
	public static class SSLInputs {
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
