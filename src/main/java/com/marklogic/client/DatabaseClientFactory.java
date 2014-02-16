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
package com.marklogic.client;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.impl.JerseyServices;
import com.marklogic.client.impl.RESTServices;

/**
 * A Database Client Factory configures a database client for making
 * database requests.
 */
public class DatabaseClientFactory {
	static final private Logger logger = LoggerFactory.getLogger(DatabaseClientFactory.class);

	/**
	 * Authentication enumerates the methods for verifying a user and
	 * password with the database.
	 */
	public enum Authentication {
	    /**
	     * Minimal security unless used with SSL.
	     */
		BASIC,
		/**
		 * Moderate security without SSL.
		 */
		DIGEST;
	}
	/**
	 * An SSLHostnameVerifier checks whether a hostname is acceptable
	 * during SSL authentication.
	 */
	public interface SSLHostnameVerifier {
		/**
		 * The ANY SSLHostnameVerifier allows any hostname, which
		 * can be useful during initial development but is not
		 * recommended for production.
		 */
		final static public Builtin ANY    = new Builtin("ANY");
		/**
		 * The COMMON SSLHostnameVerifier applies common rules
		 * for checking hostnames during SSL authentication (similar
		 * to org.apache.http.conn.ssl.BrowserCompatHostnameVerifier).
		 */
		final static public Builtin COMMON = new Builtin("COMMON");
		/**
		 * The STRICT SSLHostnameVerifier applies strict rules
		 * for checking hostnames during SSL authentication (similar
		 * to org.apache.http.conn.ssl.StrictHostnameVerifier).
		 */
		final static public Builtin STRICT = new Builtin("STRICT");

		/**
		 * Checks a hostname during SSL authentication.
		 * @param hostname	the name of the checked host
		 * @param cns	common names for the checked host
		 * @param subjectAlts	alternative subject names for the checked host
		 */
		public void verify(String hostname, String[] cns, String[] subjectAlts) throws SSLException;

		/**
		 * Builtin supports builtin implementations of SSLHostnameVerifier.
		 */
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
	 * Creates a client to access the database by means of a REST server
	 * without any authentication. Such clients can be convenient for
	 * experimentation but should not be used in production. 
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port) {
		return newClient(host, port, null, null, null, null, null);
	}

	/**
	 * Creates a client to access the database by means of a REST server.
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @param user	the user with read, write, or administrative privileges
	 * @param password	the password for the user
	 * @param type	the type of authentication applied to the request
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port, String user, String password, Authentication type) {
		return newClient(host, port, user, password, type, null, null);
	}
	/**
	 * Creates a client to access the database by means of a REST server.
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @param user	the user with read, write, or administrative privileges
	 * @param password	the password for the user
	 * @param type	the type of authentication applied to the request
	 * @param context	the SSL content for authenticating with the server
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port, String user, String password, Authentication type, SSLContext context) {
		return newClient(host, port, user, password, type, context, SSLHostnameVerifier.COMMON);
	}
	/**
	 * Creates a client to access the database by means of a REST server.
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @param user	the user with read, write, or administrative privileges
	 * @param password	the password for the user
	 * @param type	the type of authentication applied to the request
	 * @param context	the SSL content for authenticating with the server
	 * @param verifier	a callback for checking hostnames
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port, String user, String password, Authentication type, SSLContext context, SSLHostnameVerifier verifier) {
		logger.debug("Creating new database client for server at "+host+":"+port);
		RESTServices services = new JerseyServices();
		services.connect(host, port, user, password, type, context, verifier);

		return new DatabaseClientImpl(services);		
	}
}
