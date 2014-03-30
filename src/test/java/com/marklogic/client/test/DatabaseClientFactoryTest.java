/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.http.client.HttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.extra.httpclient.HttpClientConfigurator;

public class DatabaseClientFactoryTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testConnectStringIntStringStringDigest() {
		assertNotNull("Factory could not create client with digest connection", Common.client);
	}

	@Test
	public void testConfigurator() {
		ConfiguratorImpl configurator = new ConfiguratorImpl();

		DatabaseClientFactory.addConfigurator(configurator);

		DatabaseClientFactory.newClient(
				Common.HOST, Common.PORT, Common.USERNAME, Common.PASSWORD, Authentication.DIGEST
				);

		assertTrue("Factory did not apply custom configurator",
				configurator.isConfigured);
	}

	static class ConfiguratorImpl implements HttpClientConfigurator {
		public boolean isConfigured = false;
		@Override
		public void configure(HttpClient client) {
			if (client != null) {
				isConfigured = true;
			}
		}
		
	}
}
