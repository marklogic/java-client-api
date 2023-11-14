/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientBuilder;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.JacksonHandle;

/**
 * We don't yet have a way to run tests against a MarkLogic Cloud instance. In the meantime, this program and its
 * related Gradle task can be used for easy manual testing.
 *
 * For local testing against the ReverseProxyServer in the test-app project, which emulates MarkLogic Cloud, use
 * "localhost" as the cloud host, "username:password" (often "admin:the admin password") as the apiKey, and
 * "local/manage" as the basePath.
 */
public class MarkLogicCloudAuthenticationDebugger {

	public static void main(String[] args) throws Exception {
		String cloudHost = args[0];
		String apiKey = args[1];
		String basePath = args[2];

		// Expected to default to the JVM's default SSL context and default trust manager
		DatabaseClient client = new DatabaseClientBuilder()
			.withHost(cloudHost)
			.withCloudAuth(apiKey, basePath)
			// Have to use "ANY", as the default is "COMMON", which won't work for our selfsigned cert
			.withSSLHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY)
			.build();

		DatabaseClient.ConnectionResult result = client.checkConnection();
		if (result.getStatusCode() != 0) {
			throw new RuntimeException("Unable to connect: " + result.getStatusCode() + ":" + result.getErrorMessage());
		}

		System.out.println(client.newQueryManager().search(
			client.newQueryManager().newStructuredQueryBuilder().directory(true, "/")
			, new JacksonHandle()).get().toPrettyString());

		System.out.println("Successfully finished cloud-based authentication test");
	}
}
