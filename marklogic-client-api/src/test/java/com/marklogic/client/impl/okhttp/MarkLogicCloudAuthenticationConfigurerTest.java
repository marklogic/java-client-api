package com.marklogic.client.impl.okhttp;

import com.marklogic.client.DatabaseClientFactory;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Since we don't yet have a reliable way to test against a MarkLogic cloud instance, including some small unit tests
 * to ensure that certain things are built as expected.
 */
public class MarkLogicCloudAuthenticationConfigurerTest {

	@Test
	void buildTokenUrl() throws Exception {
		HttpUrl tokenUrl = new MarkLogicCloudAuthenticationConfigurer("somehost").buildTokenUrl(
			new DatabaseClientFactory.MarkLogicCloudAuthContext("doesnt-matter")
				.withSSLContext(SSLContext.getDefault(), null)
		);
		assertEquals("https://somehost/token", tokenUrl.toString());
	}

	/**
	 * It is not yet known how fixed the token path is, so the constructor is allowing for it to be overridden
	 * as an escape hatch.
	 */
	@Test
	void buildTokenUrlWithCustomTokenPath() throws Exception {
		HttpUrl tokenUrl = new MarkLogicCloudAuthenticationConfigurer("otherhost").buildTokenUrl(
			new DatabaseClientFactory.MarkLogicCloudAuthContext("doesnt-matter", "/customToken", "doesnt-matter")
				.withSSLContext(SSLContext.getDefault(), null)
		);
		assertEquals("https://otherhost/customToken", tokenUrl.toString());
	}

	@Test
	void newFormBody() {
		FormBody body = new MarkLogicCloudAuthenticationConfigurer("doesnt-matter")
			.newFormBody(new DatabaseClientFactory.MarkLogicCloudAuthContext("myKey"));
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
		FormBody body = new MarkLogicCloudAuthenticationConfigurer("doesnt-matter")
			.newFormBody(new DatabaseClientFactory.MarkLogicCloudAuthContext("myKey", "doesnt-matter", "custom-grant-type"));
		assertEquals("grant_type", body.name(0));
		assertEquals("custom-grant-type", body.value(0));
		assertEquals("key", body.name(1));
		assertEquals("myKey", body.value(1));
	}
}
