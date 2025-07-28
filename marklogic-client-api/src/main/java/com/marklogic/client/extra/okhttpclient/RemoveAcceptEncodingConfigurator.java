package com.marklogic.client.extra.okhttpclient;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Can be used with {@code DatabaseClientFactory.addConfigurator} to remove the "Accept-Encoding=gzip" request header
 * that the underlying OkHttp library adds by default. This is useful in a scenario where many small HTTP responses
 * are expected to be returned by MarkLogic, and thus the costs of gzipping the responses may outweigh the benefits.
 *
 * @since 6.3.0
 */
public class RemoveAcceptEncodingConfigurator implements OkHttpClientConfigurator {

	@Override
	public void configure(OkHttpClient.Builder builder) {
		builder.addNetworkInterceptor(chain -> {
			Request newRequest = chain.request().newBuilder().removeHeader("Accept-Encoding").build();
			return chain.proceed(newRequest);
		});
	}
}
