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

import java.io.Serializable;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.extra.httpclient.HttpClientConfigurator;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.impl.JerseyServices;

/**
 * A Database Client Factory configures a database client for making
 * database requests.
 */
public class DatabaseClientFactory {
	static final private Logger logger = LoggerFactory.getLogger(DatabaseClientFactory.class);

	static private ClientConfigurator<?> clientConfigurator;

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

		/**
		 * Returns the enumerated value for the case-insensitive name.
		 * @param name	the name of the enumerated value
		 * @return	the enumerated value
		 */
		static public Authentication valueOfUncased(String name) {
			return Authentication.valueOf(name.toUpperCase());
		}
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
			private String name;
			private Builtin(String name) {
				super();
				this.name = name;
			}
			@Override
			public void verify(String hostname, String[] cns, String[] subjectAlts) throws SSLException {
				throw new MarkLogicInternalException(
						"SSLHostnameVerifier.Builtin called directly instead of passed as parameter");
			}
			/**
			 * Returns the name of the built-in.
			 * @return	the built-in name
			 */
			public String getName() {
				return name;
			}
		}
	}

	/**
	 * A ClientConfigurator provides custom configuration for the communication library
	 * used to sending client requests and receiving server responses.
	 * @see com.marklogic.client.extra.httpclient.HttpClientConfigurator
	 * @param <T>	the configurable class for the communication library
	 */
	public interface ClientConfigurator<T> {
		/**
		 * Called as the last step in configuring the communication library.
		 * @param client	the configurable object for the communication library
		 */
		public void configure(T client);
	}

	private DatabaseClientFactory() {
	}

	/**
	 * Adds a listener that provides custom configuration when a communication library
	 * is created.
	 * @see com.marklogic.client.extra.httpclient.HttpClientConfigurator
	 * @param configurator	the listener for configuring the communication library
	 */
	static public void addConfigurator(ClientConfigurator<?> configurator) {
		if (!HttpClientConfigurator.class.isInstance(configurator)) {
			throw new IllegalArgumentException(
					"Configurator must implement HttpClientConfigurator"
					);
		}

		clientConfigurator = configurator;
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
		JerseyServices services = new JerseyServices();
		services.connect(host, port, user, password, type, context, verifier);

		if (clientConfigurator != null) {
			((HttpClientConfigurator) clientConfigurator).configure(
				services.getClientImplementation()
				);
		}

		return new DatabaseClientImpl(services);		
	}

	/**
	 * A Database Client Factory Bean provides an object for specifying configuration
	 * before creating a client to make database requests.
	 * 
	 * <p>For instance, a Spring configuration file might resemble the following
	 * example:</p>
	 * <pre>
	 * &lt;bean name="databaseClientFactory"
	 * 	   class="com.marklogic.client.DatabaseClientFactory.Bean"&gt;
	 *   &lt;property name="host"                value="localhost"/&gt;
	 *   &lt;property name="port"                value="8012"/&gt;
	 *   &lt;property name="user"                value="rest-writer-user"/&gt;
	 *   &lt;property name="password"            value="rest-writer-password"/&gt;
	 *   &lt;property name="authenticationValue" value="digest"/&gt;
	 * &lt;/bean&gt;
	 * 
	 * &lt;bean name="databaseClient"
	 * 	   class="com.marklogic.client.DatabaseClient"
	 * 	   factory-bean="databaseClientFactory"
	 * 	   factory-method="newClient"/&gt;
	 * </pre>
	 */
	static public class Bean implements Serializable {
		private static final long serialVersionUID = 1L;

		private           String              host;
		private           int                 port;
		private           String              user;
		private           String              password;
		private           Authentication      authentication;
		transient private SSLContext          context;
		transient private SSLHostnameVerifier verifier;

		/**
		 * Zero-argument constructor for bean applications. Other
		 * applications can use the static newClient() factory methods
		 * of DatabaseClientFactory.
		 */
		public Bean() {
			super();
		}

		/**
		 * Returns the host for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @return	the client host
		 */
		public String getHost() {
			return host;
		}
		/**
		 * Specifies the host for clients created from a
		 * DatabaseClientFactory.Bean object.
		 * @param host	the client host
		 */
		public void setHost(String host) {
			this.host = host;
		}
		/**
		 * Returns the port for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @return	the client port
		 */
		public int getPort() {
			return port;
		}
		/**
		 * Specifies the port for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @param port	the client port
		 */
		public void setPort(int port) {
			this.port = port;
		}
		/**
		 * Returns the user authentication for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @return	the user
		 */
		public String getUser() {
			return user;
		}
		/**
		 * Specifies the user authentication for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @param user	the user
		 */
		public void setUser(String user) {
			this.user = user;
		}
		/**
		 * Returns the password authentication for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @return	the password
		 */
		public String getPassword() {
			return password;
		}
		/**
		 * Specifies the password authentication for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @param password	the password
		 */
		public void setPassword(String password) {
			this.password = password;
		}
		/**
		 * Returns the authentication type for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @return	the authentication type 
		 */
		public Authentication getAuthentication() {
			return authentication;
		}
		/**
		 * Specifies the authentication type for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @param authentication	the authentication type
		 */
		public void setAuthentication(Authentication authentication) {
			this.authentication = authentication;
		}
		/**
		 * Specifies the authentication type for clients created with a
		 * DatabaseClientFactory.Bean object based on a string value.
		 * @param authentication	the authentication type
		 */
		public void setAuthenticationValue(String authentication) {
			this.authentication = Authentication.valueOfUncased(authentication);
		}
		/**
		 * Returns the SSLContext for SSL clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @return	the SSL context
		 */
		public SSLContext getContext() {
			return context;
		}
		/**
		 * Specifies the SSLContext for clients created with a
		 * DatabaseClientFactory.Bean object that authenticate with SSL.
		 * @param context	the SSL context
		 */
		public void setContext(SSLContext context) {
			this.context = context;
		}
		/**
		 * Returns the host verifier for clients created with a
		 * DatabaseClientFactory.Bean object.
		 * @return	the host verifier
		 */
		public SSLHostnameVerifier getVerifier() {
			return verifier;
		}
		/**
		 * Specifies the host verifier for clients created with a
		 * DatabaseClientFactory.Bean object that verify hosts for
		 * additional security.
		 * @param verifier	the host verifier
		 */
		public void setVerifier(SSLHostnameVerifier verifier) {
			this.verifier = verifier;
		}

		/**
		 * Creates a client for bean applications based on the properties.
		 * Other applications can use the static newClient() factory methods
		 * of DatabaseClientFactory.
		 * The client accesses the database by means of a REST server.
		 * @return	a new client for making database requests
		 */
		public DatabaseClient newClient() {
			return DatabaseClientFactory.newClient(
					host, port, user, password, authentication, context, verifier
					);
		}
	}
}
