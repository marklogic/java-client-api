/*
 * Copyright 2012-2013 MarkLogic Corporation
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
package com.marklogic.client.test.util;

import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.example.util.Bootstrapper;
import com.marklogic.client.io.InputStreamHandle;

/**
 * This test manages a REST instance to support Java unit tests. It installs a resource
 * extension called boostrap.xqy. then invokes it to create users and indexes
 * needed for Java unit tests. This XQuery module contains user and index setup.
 * 
 * Calling the main method with no arguments sets up a REST server on port
 * 8012 for the test harness.
 * 
 * TODO make a suitable command-line interface for end-users
 * 
 */
public class TestServerBootstrapper {

	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(TestServerBootstrapper.class);

	private String username = "admin";
	private String password = "admin";
	private String host = "localhost";
	private int port = 8012;

	private void bootstrapRestServer() throws ClientProtocolException,
			IOException, XMLStreamException, FactoryConfigurationError {

		Bootstrapper.main(new String[] {"-configuser", username, "-configpassword", password, "-confighost", host, "-restserver", "java-unittest", "-restport", ""+port, "-restdb", "java-unittest"});
	
		
		logger.info("Bootstrapped rest server for unit tests on port 8012");
	}

	private void deleteRestServer() throws ClientProtocolException, IOException {

		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(
				new AuthScope("localhost", 8002),
				new UsernamePasswordCredentials(username, password));

		HttpDelete delete = new HttpDelete(
				"http://"
						+ host
						+ ":8002/v1/rest-apis/java-unittest?include=modules&include=content");

		client.execute(delete);
	}

	private void invokeBootstrapExtension() throws ClientProtocolException,
			IOException {

		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(
				new AuthScope("localhost", port),
				new UsernamePasswordCredentials(username, password));

		HttpPost post = new HttpPost("http://" + host + ":" + port
				+ "/v1/resources/bootstrap");

		HttpResponse response = client.execute(post);
		@SuppressWarnings("unused")
		HttpEntity entity = response.getEntity();
		logger.info("Invoked bootstrap extension.  Response is "
				+ response.toString());
	}

	private void installBootstrapExtension() throws IOException {

		ResourceExtensionsManager extensionMgr = DatabaseClientFactory
				.newClient(host, port, username, password,
						Authentication.DIGEST).newServerConfigManager()
				.newResourceExtensionsManager();

		InputStreamHandle handle = new InputStreamHandle();

		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Test Resource Services");
		metadata.setDescription("This library supports all methods on the test resource");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");

		MethodParameters[] params = new MethodParameters[1];
		params[0] = new MethodParameters(MethodType.POST);

		handle.set(TestServerBootstrapper.class.getClassLoader().getResourceAsStream("bootstrap.xqy"));

		try {
			extensionMgr.writeServices("bootstrap", handle, metadata, params);
		} catch (FailedRequestException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws ClientProtocolException,
			IOException, XMLStreamException, FactoryConfigurationError {
		TestServerBootstrapper bootstrapper = new TestServerBootstrapper();

		if ((args.length == 1) && (args[0].equals("teardown"))) {
			bootstrapper.teardown();
		} else {
			bootstrapper.bootstrapRestServer();
			bootstrapper.installBootstrapExtension();
			bootstrapper.invokeBootstrapExtension();
		}

	};

	public void teardown() throws ClientProtocolException, IOException {
		deleteRestServer();
	}

}
