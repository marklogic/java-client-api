/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import okhttp3.OkHttpClient;

public interface AuthenticationConfigurer<T extends DatabaseClientFactory.SecurityContext> {

	/**
	 * Configure authentication for the given clientBuilder based on the given securityContext. Intended to keep
	 * OkHttpServices unaware of the details for each authentication strategy.
	 *
	 * @param clientBuilder
	 * @param securityContext
	 */
	void configureAuthentication(OkHttpClient.Builder clientBuilder, T securityContext);
}
