/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.extra.okhttpclient;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.impl.okhttp.OkHttpUtil;
import okhttp3.OkHttpClient;

/**
 * Exposes the mechanism for constructing an {@code OkHttpClient.Builder} in the same fashion as when a
 * {@code DatabaseClient} is constructed. Primarily intended for reuse in the ml-app-deployer library. If the
 * Java Client moves to a different HTTP client library, this will no longer work.
 *
 * @since 6.1.0
 */
public interface OkHttpClientBuilderFactory {

	static OkHttpClient.Builder newOkHttpClientBuilder(String host, DatabaseClientFactory.SecurityContext securityContext) {
		return OkHttpUtil.newOkHttpClientBuilder(host, securityContext);
	}
}
