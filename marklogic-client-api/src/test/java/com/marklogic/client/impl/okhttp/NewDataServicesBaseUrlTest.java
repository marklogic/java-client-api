package com.marklogic.client.impl.okhttp;

import okhttp3.HttpUrl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewDataServicesBaseUrlTest {

	@Test
	public void nullBasePath() {
		HttpUrl url = HttpUrlBuilder.newDataServicesBaseUri(
			HttpUrlBuilder.newBaseUrl("anyhost", 8000, null, null)
		);
		assertEquals("http://anyhost:8000/", url.toString(),
			"For DS, the expectation is that the v1 stuff doesn't exist, as endpoints are expected " +
				"to be resolved from the root of the app server");
		assertEquals("http://anyhost:8000/my/service/endpoint.sjs",
			url.resolve("my/service/endpoint.sjs").toString());
	}

	@Test
	public void basePathWithNoSlashes() {
		HttpUrl url = HttpUrlBuilder.newDataServicesBaseUri(
			HttpUrlBuilder.newBaseUrl("anyhost", 8000, "noSlashes", null)
		);
		assertEquals("http://anyhost:8000/noSlashes/", url.toString());
		assertEquals("http://anyhost:8000/noSlashes/my/service/endpoint.sjs",
			url.resolve("my/service/endpoint.sjs").toString());
	}

	@Test
	public void basePathWithSlashes() {
		HttpUrl url = HttpUrlBuilder.newDataServicesBaseUri(
			HttpUrlBuilder.newBaseUrl("anyhost", 8000, "my/base/path", null)
		);
		assertEquals("http://anyhost:8000/my/base/path/", url.toString());
		assertEquals("http://anyhost:8000/my/base/path/my/service/endpoint.sjs",
			url.resolve("my/service/endpoint.sjs").toString());
	}

	@Test
	public void basePathStartsWithSlash() {
		HttpUrl url = HttpUrlBuilder.newDataServicesBaseUri(
			HttpUrlBuilder.newBaseUrl("anyhost", 8000, "/starts/withSlash", null)
		);
		assertEquals("http://anyhost:8000/starts/withSlash/", url.toString());
		assertEquals("http://anyhost:8000/starts/withSlash/my/service/endpoint.sjs",
			url.resolve("my/service/endpoint.sjs").toString());
	}
}
