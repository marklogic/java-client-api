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
	public interface SSLHostnameVerifier {
		final static public Builtin ANY    = new Builtin("ANY");
		final static public Builtin COMMON = new Builtin("COMMON");
		final static public Builtin STRICT = new Builtin("STRICT");

		public void verify(String hostname, String[] cns, String[] subjectAlts) throws SSLException;

		public class Builtin implements SSLHostnameVerifier {
			String name;
			private Builtin(String name) {
				super();
				this.name = name;
			}
			@Override
			public void verify(String hostname, String[] cns, String[] subjectAlts) throws SSLException {
				throw new MarkLogicInternalException(
						"SSLHostnameVerifier.Builtin called directly instead of passed as parameter");
			}
			
		}
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
		return connect(host, port, user, password, type, null, null);
	}
	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type, SSLContext context) {
		return connect(host, port, user, password, type, context, SSLHostnameVerifier.COMMON);
	}
	static public DatabaseClient connect(String host, int port, String user, String password, Authentication type, SSLContext context, SSLHostnameVerifier verifier) {
		RESTServices services = new JerseyServices();
		services.connect(host, port, user, password, type, context, verifier);

		return new DatabaseClientImpl(services);		
	}
}
