package com.marklogic.client.impl.okhttp;

import okhttp3.HttpUrl;
import org.junit.Test;

import javax.net.ssl.SSLContext;

import static org.junit.Assert.assertEquals;

public class NewBaseUrlTest {

	@Test
	public void nullBasePath() {
		HttpUrl url = HttpUrlBuilder.newBaseUrl("anyhost", 8123, null, null);
		assertEquals("OkHttpServices has always defaulted to including /v1/ping on the base URL; other " +
				"parts of it expect that to be present, including the code for checkConnection",
			"http://anyhost:8123/v1/ping", url.toString());
		assertEquals("http://anyhost:8123/v1/documents", url.resolve("documents").toString());
	}

	@Test
	public void nullBasePathWithSSL() throws Exception {
		HttpUrl url = HttpUrlBuilder.newBaseUrl("anyhost", 8123, null, SSLContext.getDefault());
		assertEquals("https://anyhost:8123/v1/ping", url.toString());
		assertEquals("https://anyhost:8123/v1/documents", url.resolve("documents").toString());
	}

	@Test
	public void emptyBasePath() {
		HttpUrl url = HttpUrlBuilder.newBaseUrl("anyhost", 8123, "", null);
		assertEquals("http://anyhost:8123/v1/ping", url.toString());
	}

	@Test
	public void basePathNoSlashes() {
		HttpUrl url = HttpUrlBuilder.newBaseUrl("localhost", 8123, "noSlashes", null);
		assertEquals("http://localhost:8123/noSlashes/v1/ping", url.toString());
		assertEquals("http://localhost:8123/noSlashes/v1/documents", url.resolve("documents").toString());
	}

	@Test
	public void basePathWithForwardSlashes() {
		HttpUrl url = HttpUrlBuilder.newBaseUrl("localhost", 8123, "has/forward/slashes", null);
		assertEquals("http://localhost:8123/has/forward/slashes/v1/ping", url.toString());
		assertEquals("http://localhost:8123/has/forward/slashes/v1/documents", url.resolve("documents").toString());
	}

	@Test
	public void basePathStartsWithSlash() {
		HttpUrl url = HttpUrlBuilder.newBaseUrl("localhost", 8123, "/starts/withSlash", null);
		assertEquals("http://localhost:8123/starts/withSlash/v1/ping", url.toString());
		assertEquals("http://localhost:8123/starts/withSlash/v1/documents", url.resolve("documents").toString());
	}
}
