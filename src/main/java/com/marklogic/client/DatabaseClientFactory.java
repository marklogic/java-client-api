/*
 * Copyright 2012-2015 MarkLogic Corporation
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
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.extra.httpclient.HttpClientConfigurator;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.impl.HandleFactoryRegistryImpl;
import com.marklogic.client.impl.JerseyServices;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;

/**
 * A Database Client Factory configures a database client for making
 * database requests.
 */
public class DatabaseClientFactory {
	static final private Logger logger = LoggerFactory.getLogger(DatabaseClientFactory.class);

	static private ClientConfigurator<?> clientConfigurator;
	static private HandleFactoryRegistry handleRegistry =
		HandleFactoryRegistryImpl.newDefault();

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
	 * A HandleFactoryRegistry associates IO representation classes
	 * with handle factories. The API uses the registry to create 
	 * a content handle to act as an adapter for a supported
	 * IO representation class.  The IO class and its instances can
	 * then be passed directly to convenience methods.
	 * The default registry associates the content handles provided 
	 * by the API and the supported IO representation classes.
	 * Create instances of this interface only if you need to modify
	 * the default registry.
	 * @see DatabaseClientFactory#getHandleRegistry()
	 * @see DatabaseClientFactory.Bean#getHandleRegistry()
	 */
	public interface HandleFactoryRegistry {
		/**
		 * Associates a factory for content handles with the classes
		 * for IO representations known to the factory.
		 * @param factory	a factory for creating content handle instances
		 */
		public void register(ContentHandleFactory factory);
		/**
		 * Associates a factory for content handles with the specified classes
		 * for IO representations.
		 * @param factory	a factory for creating content handle instances
		 * @param ioClasses	the IO classes for which the factory should create handles
		 */
		public void register(ContentHandleFactory factory, Class<?>... ioClasses);
		/**
		 * Returns whether the registry associates the class with a factory.
		 * @param ioClass	the class for an IO representation
		 * @return	true if a factory has been registered
		 */
		public boolean isRegistered(Class<?> ioClass);
		/**
		 * Returns the classes for the IO representations for which a factory
		 * has been registered.
		 * @return	the IO classes
		 */
		public Set<Class<?>> listRegistered();
		/**
		 * Creates a ContentHandle if the registry has a factory
		 * for the class of the IO representation.
		 * @param type	the class for an IO representation
		 * @return	a content handle or null if no factory supports the class
		 */
		public <C> ContentHandle<C> makeHandle(Class<C> type);
		/**
		 * Removes the classes from the registry
		 * @param ioClasses	one or more registered classes for an IO representation
		 */
		public void unregister(Class<?>... ioClasses);
		/**
		 * Create a copy of the current registry
		 * @return	a copy of the current registry
		 */
		public HandleFactoryRegistry copy();
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
	 * Creates a client to access the database by means of a REST server
	 * without any authentication. Such clients can be convenient for
	 * experimentation but should not be used in production. 
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port) {
		return newClient(host, port, null, null, null, null, null, null);
	}

	/**
	 * Creates a client to access the database by means of a REST server
	 * without any authentication. Such clients can be convenient for
	 * experimentation but should not be used in production. 
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @param database	the database to access (default: configured database for the REST server)
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port, String database) {
		return newClient(host, port, database, null, null, null, null, null);
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
		return newClient(host, port, null, user, password, type, null, null);
	}
	/**
	 * Creates a client to access the database by means of a REST server.
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @param database	the database to access (default: configured database for the REST server)
	 * @param user	the user with read, write, or administrative privileges
	 * @param password	the password for the user
	 * @param type	the type of authentication applied to the request
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port, String database, String user, String password, Authentication type) {
		return newClient(host, port, database, user, password, type, null, null);
	}
	/**
	 * Creates a client to access the database by means of a REST server.
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @param user	the user with read, write, or administrative privileges
	 * @param password	the password for the user
	 * @param type	the type of authentication applied to the request
	 * @param context	the SSL context for authenticating with the server
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port, String user, String password, Authentication type, SSLContext context) {
		return newClient(host, port, null, user, password, type, context, SSLHostnameVerifier.COMMON);
	}
	/**
	 * Creates a client to access the database by means of a REST server.
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @param database	the database to access (default: configured database for the REST server)
	 * @param user	the user with read, write, or administrative privileges
	 * @param password	the password for the user
	 * @param type	the type of authentication applied to the request
	 * @param context	the SSL context for authenticating with the server
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port, String database, String user, String password, Authentication type, SSLContext context) {
		return newClient(host, port, database, user, password, type, context, SSLHostnameVerifier.COMMON);
	}
	/**
	 * Creates a client to access the database by means of a REST server.
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @param user	the user with read, write, or administrative privileges
	 * @param password	the password for the user
	 * @param type	the type of authentication applied to the request
	 * @param context	the SSL context for authenticating with the server
	 * @param verifier	a callback for checking hostnames
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port, String user, String password, Authentication type, SSLContext context, SSLHostnameVerifier verifier) {
		DatabaseClientImpl client = newClientImpl(host, port, null, user, password, type, context, verifier);
		client.setHandleRegistry(getHandleRegistry().copy());
		return client;
	}
	/**
	 * Creates a client to access the database by means of a REST server.
	 * 
	 * @param host	the host with the REST server
	 * @param port	the port for the REST server
	 * @param database	the database to access (default: configured database for the REST server)
	 * @param user	the user with read, write, or administrative privileges
	 * @param password	the password for the user
	 * @param type	the type of authentication applied to the request
	 * @param context	the SSL context for authenticating with the server
	 * @param verifier	a callback for checking hostnames
	 * @return	a new client for making database requests
	 */
	static public DatabaseClient newClient(String host, int port, String database, String user, String password, Authentication type, SSLContext context, SSLHostnameVerifier verifier) {
		DatabaseClientImpl client = newClientImpl(host, port, database, user, password, type, context, verifier);
		client.setHandleRegistry(getHandleRegistry().copy());
		return client;
	}
	static private DatabaseClientImpl newClientImpl(String host, int port, String database, String user, String password, Authentication type, SSLContext context, SSLHostnameVerifier verifier) {
		logger.debug("Creating new database client for server at "+host+":"+port);
		JerseyServices services = new JerseyServices();
		services.connect(host, port, database, user, password, type, context, verifier);

		if (clientConfigurator != null) {
			((HttpClientConfigurator) clientConfigurator).configure(
				services.getClientImplementation()
				);
		}

		return new DatabaseClientImpl(services);		
	}

	/**
	 * Returns the default registry with factories for creating handles
	 * as adapters for IO representations. To create custom registries,
	 * use 
	 * @return	the default registry
	 */
	static public HandleFactoryRegistry getHandleRegistry() {
		return handleRegistry;
	}
	/**
	 * Removes the current registered associations so the
	 * handle registry is empty.
	 */
	static public void clearHandleRegistry() {
		handleRegistry = new HandleFactoryRegistryImpl();
	}
	/**
	 * Initializes a handle registry with the default associations
	 * between the content handles provided by the API and the supported
	 * IO representation classes.  Use this method only after clearing
	 * or overwriting associations in the handle registry.
	 */
	static public void registerDefaultHandles() {
		HandleFactoryRegistryImpl.registerDefaults(getHandleRegistry());
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

		private           String                host;
		private           int                   port;
		private           String                database;
		private           String                user;
		private           String                password;
		private           Authentication        authentication;
		private           HandleFactoryRegistry handleRegistry =
			HandleFactoryRegistryImpl.newDefault();

		transient private SSLContext            context;
		transient private SSLHostnameVerifier   verifier;

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
		 * Returns the registry for associating 
		 * IO representation classes with handle factories.
		 * @return	the registry instance
		 */
		public HandleFactoryRegistry getHandleRegistry() {
			return handleRegistry;
		}
		/**
		 * Removes the current registered associations so the
		 * handle registry is empty.
		 */
		public void clearHandleRegistry() {
			this.handleRegistry = new HandleFactoryRegistryImpl();
		}
		/**
		 * Initializes a handle registry with the default associations
		 * between the content handles provided by the API and the supported
		 * IO representation classes.  Use this method only after clearing
		 * or overwriting associations in the handle registry.
		 */
		public void registerDefaultHandles() {
			HandleFactoryRegistryImpl.registerDefaults(getHandleRegistry());
		}

		/**
		 * Creates a client for bean applications based on the properties.
		 * Other applications can use the static newClient() factory methods
		 * of DatabaseClientFactory.
		 * The client accesses the database by means of a REST server.
		 * @return	a new client for making database requests
		 */
		public DatabaseClient newClient() {
			DatabaseClientImpl client = newClientImpl(host, port, database, user, password, authentication, context, verifier);
			client.setHandleRegistry(getHandleRegistry().copy());

			return client;
		}
	}
}
