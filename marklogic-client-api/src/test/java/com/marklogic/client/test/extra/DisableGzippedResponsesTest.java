package com.marklogic.client.test.extra;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator;
import com.marklogic.client.test.Common;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DisableGzippedResponsesTest {

	@AfterEach
	void afterEach() {
		DatabaseClientFactory.removeConfigurators();
	}

	@Test
	void test() {
		// Add a DatabaseClientFactory configurator that uses an OkHttp interceptor to capture the value of the
		// Content-Encoding response header. That is the easiest way to verify whether the Accept-Encoding:gzip request
		// header is being sent or not.
		TestInterceptor testInterceptor = new TestInterceptor();
		DatabaseClientFactory.addConfigurator((OkHttpClientConfigurator) builder -> builder.addNetworkInterceptor(testInterceptor));


		final String testUri = "/optic/test/musician1.json";

		Common.newClient().newJSONDocumentManager().read(testUri);
		assertEquals("gzip", testInterceptor.contentEncoding, "MarkLogic 11 now supports the Accept-Encoding:gzip " +
			"header. The OkHttp library used by the Java Client includes this request header by default. So we " +
			"expect for responses from the REST API to be gzipped, which is indicated by the Content-Encoding " +
			"response header having a value of 'gzip'.");

		Common.newClientBuilder().withGzippedResponsesDisabled().build()
			.newJSONDocumentManager().read(testUri);
		assertNull(testInterceptor.contentEncoding, "When a DatabaseClient is constructed with gzipped responses " +
			"disabled, the Accept-Encoding header added automatically by OkHttp should be removed before the request " +
			"is sent to MarkLogic. This prevents MarkLogic from gzipping the response, which is indicated by the " +
			"HTTP response not having a 'Content-Encoding' header.");
	}

	/**
	 * Used to capture the value of the Content-Encoding response header.
	 */
	private static class TestInterceptor implements Interceptor {

		String contentEncoding;

		@NotNull
		@Override
		public Response intercept(@NotNull Chain chain) throws IOException {
			Response response = chain.proceed(chain.request());
			contentEncoding = response.header("Content-Encoding");
			return response;
		}
	}
}
