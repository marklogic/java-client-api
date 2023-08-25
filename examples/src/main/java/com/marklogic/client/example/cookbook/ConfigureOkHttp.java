package com.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.extra.okhttpclient.OkHttpClientConfigurator;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Provides examples of configuring the underlying OkHttp client for various use cases.
 */
public class ConfigureOkHttp {

	/**
	 * OkHttp 4.x includes a header of "Accept-Encoding=gzip" by default. The following shows how to use an OkHttp
	 * interceptor to remove this header from every request. By doing this via a configurator passed to
	 * {@code DatabaseClientFactory}, every {@code DatabaseClient} created via the factory will inherit this
	 * interceptor.
	 *
	 * Note that while the Accept-Encoding header can contain multiple values - see
	 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Encoding for examples - the MarkLogic Java
	 * Client does not set this header and OkHttp only sets it to "gzip" for every request. Thus, removing the
	 * header should not result in losing any other values besides "gzip" for the header. You are free to
	 * customize this as you wish though; this is primarily intended as an example for how to customize OkHttp
	 * when using the MarkLogic Java Client.
	 *
	 * As of Java Client 6.3.0, this can now be accomplished via the {@code DatabaseClientFactory} class and
	 * {@code RemoveAcceptEncodingConfigurator}.
 	 */
	public static void removeAcceptEncodingGzipHeader() {
		DatabaseClientFactory.addConfigurator(new OkHttpClientConfigurator() {
			@Override
			public void configure(OkHttpClient.Builder builder) {
				builder.addNetworkInterceptor(chain -> {
					Request newRequest = chain.request().newBuilder().removeHeader("Accept-Encoding").build();
					return chain.proceed(newRequest);
				});
			}
		});
	}
}
