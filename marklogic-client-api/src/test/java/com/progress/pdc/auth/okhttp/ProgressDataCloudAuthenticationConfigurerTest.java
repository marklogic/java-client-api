/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.progress.pdc.auth.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Since we don't yet have a reliable way to test against a Progress Data Cloud instance, including some small unit tests
 * to ensure that certain things are built as expected.
 * <p>
 * This is in the "pdc" package even though it depends on a MarkLogic Java Client-specific class - this is so that it
 * can access protected methods of DefaultTokenGenerator.
 */
class ProgressDataCloudAuthenticationConfigurerTest {

	@Test
	void buildTokenUrl() throws Exception {
		TokenGenerator client = new TokenGenerator("somehost",
			new DatabaseClientFactory.ProgressDataCloudAuthContext("doesnt-matter")
				.withSSLContext(SSLContext.getDefault(), null),
			() -> null
		);

		HttpUrl tokenUrl = client.buildTokenUrl();
		assertEquals("https://somehost/token", tokenUrl.toString());
	}

	/**
	 * It is not yet known how fixed the token path is, so the constructor is allowing for it to be overridden
	 * as an escape hatch.
	 */
	@Test
	void buildTokenUrlWithCustomTokenPath() throws Exception {
		TokenGenerator client = new TokenGenerator("otherhost",
			new DatabaseClientFactory.ProgressDataCloudAuthContext("doesnt-matter", "/customToken", "doesnt-matter")
				.withSSLContext(SSLContext.getDefault(), null),
			() -> null
		);

		HttpUrl tokenUrl = client.buildTokenUrl();
		assertEquals("https://otherhost/customToken", tokenUrl.toString());
	}

	@Test
	void buildTokenUrlWithDuration() throws Exception {
		Integer duration = 10;
		TokenGenerator client = new TokenGenerator("somehost",
			new DatabaseClientFactory.ProgressDataCloudAuthContext("doesnt-matter", duration)
				.withSSLContext(SSLContext.getDefault(), null),
			() -> null
		);

		HttpUrl tokenUrl = client.buildTokenUrl();
		assertEquals("https://somehost/token?duration=10", tokenUrl.toString());
	}

	@Test
	void buildTokenUrlWithDurationAndCustomPath() throws Exception {
		Integer duration = 10;
		TokenGenerator client = new TokenGenerator("somehost",
			new DatabaseClientFactory.ProgressDataCloudAuthContext("doesnt-matter", "/customToken", "doesnt-matter", duration)
				.withSSLContext(SSLContext.getDefault(), null),
			() -> null
		);

		HttpUrl tokenUrl = client.buildTokenUrl();
		assertEquals("https://somehost/customToken?duration=10", tokenUrl.toString());
	}

	@Test
	void newFormBody() {
		FormBody body = new TokenGenerator("host-doesnt-matter",
			new DatabaseClientFactory.ProgressDataCloudAuthContext("myKey"), () -> null)
			.newFormBody();
		assertEquals("grant_type", body.name(0));
		assertEquals("apikey", body.value(0));
		assertEquals("key", body.name(1));
		assertEquals("myKey", body.value(1));
	}

	/**
	 * It is not yet known how fixed the grant_type value is, so the constructor is allowing for it to be overridden
	 * as an escape hatch.
	 */
	@Test
	void newFormBodyWithOverrides() {
		FormBody body = new TokenGenerator("host-doesnt-matter",
			new DatabaseClientFactory.ProgressDataCloudAuthContext("myKey", "doesnt-matter", "custom-grant-type"), () -> null)
			.newFormBody();
		assertEquals("grant_type", body.name(0));
		assertEquals("custom-grant-type", body.value(0));
		assertEquals("key", body.name(1));
		assertEquals("myKey", body.value(1));
	}
}
