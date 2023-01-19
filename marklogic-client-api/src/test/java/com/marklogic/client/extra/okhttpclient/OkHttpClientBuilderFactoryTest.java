package com.marklogic.client.extra.okhttpclient;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.test.Common;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OkHttpClientBuilderFactoryTest {

	@Test
	void smokeTest() {
		DatabaseClientFactory.Bean bean = Common.newClientBuilder().buildBean();
		OkHttpClient.Builder builder = OkHttpClientBuilderFactory.newOkHttpClientBuilder(
			bean.getHost(), bean.getPort(), bean.getSecurityContext());
		assertNotNull(builder);

		OkHttpClient client = builder.build();
		assertNotNull(client, "This is simply verifying that the public/extra method doesn't throw an error. It's " +
			"expected to reuse the same approach that constructing a DatabaseClient does. And it's expected that " +
			"with ml-app-deployer 4.5.0 depending on this method for any calls to MarkLogic, tests will fail in that " +
			"project if there's ever an issue with this method");
	}
}
