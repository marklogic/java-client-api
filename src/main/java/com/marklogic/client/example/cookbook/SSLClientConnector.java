/*
 * Copyright 2012 MarkLogic Corporation
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
package com.marklogic.client.example.cookbook;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.TextDocumentManager;
import com.marklogic.client.io.StringHandle;

/**
 * SSLClientConnector illustrates the basic approach for an SSL connection to a database.
 * 
 * Note:  to run this example, you must modify the REST server by specifying a SSL certificate template.
 */
public class SSLClientConnector {

	public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		Properties props = loadProperties();

		// connection parameters for writer user
		String         host            = props.getProperty("example.host");
		int            port            = Integer.parseInt(props.getProperty("example.port"));
		String         writer_user     = props.getProperty("example.writer_user");
		String         writer_password = props.getProperty("example.writer_password");
		Authentication authType        = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		run(host, port, writer_user, writer_password, authType);
	}

	public static void run(String host, int port, String user, String password, Authentication authType) throws NoSuchAlgorithmException, KeyManagementException {
		System.out.println("example: "+SSLClientConnector.class.getName());

		// create a trust manager
		// (note: a real application should verify certificates)
		TrustManager naiveTrustMgr = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};

		// create an SSL context
		SSLContext sslContext = SSLContext.getInstance("SSLv3");
		sslContext.init(null, new TrustManager[] { naiveTrustMgr }, null);

		// connect the client
		// (note: a real application should use a COMMON, STRICT, or implemented hostname verifier)
		DatabaseClient client = DatabaseClientFactory.connect(host, port, user, password, Authentication.DIGEST, sslContext, SSLHostnameVerifier.ANY);

		// make use of the client connection
		TextDocumentManager docMgr = client.newTextDocumentManager();
		DocumentIdentifier docId = client.newDocId("/example/text.txt");
		StringHandle handle = new StringHandle();
		handle.set("A simple text document");
		docMgr.write(docId, handle);

		System.out.println("Connected by SSL to "+host+":"+port+" as "+user);

		// clean up the written document
		docMgr.delete(docId);

		// release the client
		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			SSLClientConnector.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
