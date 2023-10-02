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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public interface SSLUtil {

	static X509TrustManager getDefaultTrustManager() {
		X509TrustManager trustManager = (X509TrustManager) getDefaultTrustManagers()[0];
		Logger logger = LoggerFactory.getLogger(SSLUtil.class);
		if (logger.isDebugEnabled() && trustManager.getAcceptedIssuers() != null) {
			logger.debug("Count of accepted issuers in default trust manager: {}", trustManager.getAcceptedIssuers().length);
		}
		return trustManager;
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
