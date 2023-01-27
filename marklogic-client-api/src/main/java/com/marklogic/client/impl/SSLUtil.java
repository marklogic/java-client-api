package com.marklogic.client.impl;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public interface SSLUtil {

	static X509TrustManager getDefaultTrustManager() {
		return (X509TrustManager) getDefaultTrustManagers()[0];
	}

	/**
	 * @return a non-empty array of TrustManager instances based on the JVM's default trust manager algorithm, with the
	 * first trust manager guaranteed to be an instance of X509TrustManager.
	 */
	static TrustManager[] getDefaultTrustManagers() {
		final String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory trustManagerFactory;
		try {
			trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to obtain trust manager factory using JVM's default trust manager algorithm: " + defaultAlgorithm, e);
		}

		try {
			trustManagerFactory.init((KeyStore) null);
		} catch (KeyStoreException e) {
			throw new RuntimeException("Unable to initialize trust manager factory obtained using JVM's default trust manager algorithm: " + defaultAlgorithm
				+ "; cause: " + e.getMessage(), e);
		}

		TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
		if (trustManagers == null || trustManagers.length == 0) {
			throw new RuntimeException("No trust managers found using the JVM's default trust manager algorithm: " + defaultAlgorithm);
		}
		if (!(trustManagers[0] instanceof X509TrustManager)) {
			throw new RuntimeException("Default trust manager is not an X509TrustManager: " + trustManagers[0]);
		}
		return trustManagers;
	}
}
