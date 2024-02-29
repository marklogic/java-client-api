package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import okhttp3.Request;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OAuthAuthenticationConfigurerTest {

	@Test
	void test() {
		DatabaseClientFactory.OAuthContext authContext = new DatabaseClientFactory.OAuthContext("abc123");
		Request request = new Request.Builder().url(new MockWebServer().url("/url-doesnt-matter")).build();

		Request authenticatedRequest = new OAuthAuthenticationConfigurer().makeAuthenticatedRequest(request, authContext);
		assertEquals("Bearer abc123", authenticatedRequest.header("Authorization"));
	}
}
