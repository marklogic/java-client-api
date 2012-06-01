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
package com.marklogic.client;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.impl.JerseyServices;
import com.marklogic.client.impl.RESTServices;

/**
 * A Database Client Factory connects to a database to create a database client.
 */
public class DatabaseClientFactory {
	static final private Logger logger = LoggerFactory.getLogger(DatabaseClientFactory.class);

	public enum Authentication {
	    BASIC, DIGEST, NONE;
	}
	public enum HostVerificationPolicy {
	    ANY, COMMON, STRICT;
	}
	public interface SSLHostnameVerifier {
		public void verify(String hostname, String[] cns, String[] subjectAlts) throws SSLException;
	}

	private DatabaseClientFactory() {
	}

	/**
	 * Connects to a database, creating a client to access the database.
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @param type
	 * @return
	 */
	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type) {
		return connect(host, port, user, password, type, null, (SSLHostnameVerifier) null);		
	}
	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type, SSLContext context) {
		return connect(host, port, user, password, type, context, HostVerificationPolicy.COMMON);
	}
	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type, SSLContext context, HostVerificationPolicy policy) {
		RESTServices services = new JerseyServices();
		services.connect(host, port, user, password, type, context, policy);

		return new DatabaseClientImpl(services);		
	}
	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type, SSLContext context, SSLHostnameVerifier verifier) {
		RESTServices services = new JerseyServices();
		services.connect(host, port, user, password, type, context, verifier);

		return new DatabaseClientImpl(services);		
	}
}
