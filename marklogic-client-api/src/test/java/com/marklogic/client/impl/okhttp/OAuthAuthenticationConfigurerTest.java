/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import mockwebserver3.MockWebServer;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OAuthAuthenticationConfigurerTest {

	@Test
	void test() throws Exception {
		MockWebServer mockWebServer = new MockWebServer();
		mockWebServer.start();
		Request request = new Request.Builder().url(mockWebServer.url("/url-doesnt-matter")).build();

		DatabaseClientFactory.OAuthContext authContext = new DatabaseClientFactory.OAuthContext("abc123");
		Request authenticatedRequest = new OAuthAuthenticationConfigurer().makeAuthenticatedRequest(request, authContext);
		assertEquals("Bearer abc123", authenticatedRequest.header("Authorization"));
	}
}
