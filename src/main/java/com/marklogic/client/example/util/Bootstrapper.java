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
package com.marklogic.client.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

import com.marklogic.client.DatabaseClientFactory.Authentication;

/**
 * Bootstrapper provides an example of how to create a REST server
 * using an HTTP client.
 */
public class Bootstrapper {
	/**
	 * Command-line invocation.
	 * @param args	command-line arguments specifying the configuration and REST server
	 */
	public static void main(String[] args)
	throws ClientProtocolException, IOException, FactoryConfigurationError {
		Properties properties = new Properties();
		for (int i=0; i < args.length; i++) {
			String name = args[i];
			if (name.startsWith("-") && name.length() > 1 && ++i < args.length) {
				name = name.substring(1);
				if ("properties".equals(name)) {
					InputStream propsStream = Bootstrapper.class.getClassLoader().getResourceAsStream(name);
					if (propsStream == null)
						throw new IOException("Could not read bootstrapper properties");
					Properties props = new Properties();
					props.load(propsStream);
					props.putAll(properties);
					properties = props;
				} else {
					properties.put(name, args[i]);
				}
			} else {
				System.err.println("invalid argument: "+name);
				System.err.println(getUsage());
				System.exit(1);
			}
		}

		String invalid = joinList(listInvalidKeys(properties));
		if (invalid != null && invalid.length() > 0) {
			System.err.println("invalid arguments: "+invalid);
			System.err.println(getUsage());
			System.exit(1);
		}

// TODO: catch invalid argument exceptions and provide feedback
		new Bootstrapper().makeServer(properties);

		System.out.println(
				"Created "+properties.getProperty("restserver")+
				" server on "+properties.getProperty("restport")+
				" port for "+properties.getProperty("restdb")+
				" database"
				);
	}

	/**
	 * Invocation based on properties.
	 * @param properties	the specification of the configuration and REST server
	 */
	public void makeServer(Properties properties)
	throws ClientProtocolException, IOException, FactoryConfigurationError {
		makeServer(new ConfigServer(properties), new RESTServer(properties));
	}
	/**
	 * Programmatic invocation.
	 * @param configServer	the configuration server for creating the REST server
	 * @param restServer	the specification of the REST server
	 */
	public void makeServer(ConfigServer configServer, RESTServer restServer)
	throws ClientProtocolException, IOException, FactoryConfigurationError {
        
        DefaultHttpClient client = new DefaultHttpClient();

        String host       = configServer.getHost();
        int    configPort = configServer.getPort();

// TODO: SSL
        Authentication authType = configServer.getAuthType();
        if (authType != null) {
            List<String> prefList = new ArrayList<String>();
            if (authType == Authentication.BASIC)
        		prefList.add(AuthPolicy.BASIC);
        	else if (authType == Authentication.DIGEST)
        		prefList.add(AuthPolicy.DIGEST);
        	else
        		throw new IllegalArgumentException(
        				"Unknown authentication type: "+authType.name()
        				);
            client.getParams().setParameter(
            		AuthPNames.PROXY_AUTH_PREF, prefList
            		);

            String configUser     = configServer.getUser();
            String configPassword = configServer.getPassword();
            client.getCredentialsProvider().setCredentials(
                    new AuthScope(host, configPort),
                    new UsernamePasswordCredentials(configUser, configPassword)
                    );
        }

        BasicHttpContext context = new BasicHttpContext();

        StringEntity content;
		try {
			content = new StringEntity(restServer.toXMLString());
		} catch (XMLStreamException e) {
			throw new IOException("Could not create payload to bootstrap server.");
		}
        content.setContentType("application/xml");

        HttpPost poster = new HttpPost("http://"+host+":"+configPort+"/v1/rest-apis");
        poster.setEntity(content);

        HttpResponse response = client.execute(poster, context);
        //poster.releaseConnection();

        StatusLine status = response.getStatusLine();

        int    statusCode   = status.getStatusCode();
        String statusPhrase = status.getReasonPhrase();

        client.getConnectionManager().shutdown();

        if (statusCode >= 300) {
        	throw new RuntimeException(
        			"Failed to create REST server: "+
        			statusCode+" "+
        			statusPhrase+"\n"+
        			"Please check the server log for detail"
        			);
        }
	}

	static public String getUsage() {
		Map<String,String> propNames = getPropNames();

		StringBuilder buffer = new StringBuilder();
		buffer.append("usage:\n");
		for (Map.Entry<String,String> entry: propNames.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append("\t= ");
			buffer.append(entry.getValue());
			buffer.append("\n");
		}

		return buffer.toString();
	}
	static public void checkProperties(Properties properties) {
		String invalid = joinList(listInvalidKeys(properties));
		if (invalid != null && invalid.length() > 0) {
			throw new IllegalArgumentException(
					"invalid bootstrapping names: "+invalid
					);
		}
	}
	static public List<String> listInvalidKeys(Properties properties) {
		Map<String,String> propNames = getPropNames();

		List<String> invalid = null;
		for (String key: properties.stringPropertyNames()) {
			if (propNames.containsKey(key))
				continue;

			if (invalid == null)
				invalid = new ArrayList<String>();

			invalid.add(key);
		}

		return invalid;
	}
	static public String joinList(List<String> list) {
		return joinList(list, ", ");
	}
	static public String joinList(List<String> list, String sep) {
		if (list == null || list.size() == 0)
			return null;

		StringBuilder buffer = null;
		for (String key: list) {
			if (buffer == null)
				buffer = new StringBuilder();
			else
				buffer.append(sep);
			buffer.append(key);
		}

		return buffer.toString();
	}
	static private Map<String,String> getPropNames() {
		Map<String,String> propNames = new HashMap<String, String>();
		propNames.put("confighost",
			"the host for configuring a new REST server");
		propNames.put("configport",
			"the port (typically 8002) for the configuration server");
		propNames.put("configuser",
			"the user (typically admin) for the configuration server");
		propNames.put("configpassword",
			"the password for the configuration user");
		propNames.put("configauth",
			"the type of authentication (digest or basic) for the configuration server");
		propNames.put("restdb",
			"the name of the database exposed by the new REST server");
		propNames.put("restmodulesdb",
			"the name of the modules database (if any) for new REST server");
		propNames.put("restgroup",
			"the name of the group (on a cluster with many groups) for new REST server");
		propNames.put("restserver",
			"the name of the new REST server");
		propNames.put("restport",
			"the port for the new REST server");

		return propNames;
	}

	/**
	 * ConfigServer specifies a configuration server supporting
	 * requests to create a REST server.
	 */
	static public class ConfigServer {
		private String         host     = "localhost";
		private int            port     = 8002;
		private String         user     = null;
		private String         password = null;
		private Authentication authType = Authentication.DIGEST;

		/**
		 * Construct the configuration server specification based on properties.
		 * @param properties	specifies the configuration server
		 */
		public ConfigServer(Properties properties) {
			for (String key: properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				if ("confighost".equals(key))
					host = value;
				else if ("configport".equals(key))
					port = Integer.parseInt(value);
				else if ("configuser".equals(key))
					user = value;
// TODO: secure password configuration
				else if ("configpassword".equals(key))
					password = value;
				else if ("configauth".equals(key))
					authType = Authentication.valueOf(value.toUpperCase());
			}
			validate();
		}
		/**
		 * Construct the configuration server specification programmatically.
		 * @param host	the host (often localhost) for the configuration and REST server
		 * @param port	the port (usually 8002) for the configuration server
		 * @param user	the user (often admin) for the configuration server
		 * @param password	the password for the configuration server
		 * @param authType	the authentication type (usually DIGEST) for the configuration server
		 */
		public ConfigServer(
				String host, int port, String user, String password, Authentication authType
		) {
			if (host != null)
				this.host = host;
			if (port != -1)
				this.port = port;
			if (user != null)
				this.user = user;
			if (password != null)
				this.password = password;
			if (authType != null)
				this.authType = authType;
			validate();
		}
		private void validate() {
			if (
				(authType != null || user != null || password != null) &&
				(authType == null || user == null || password == null)
			)
				throw new IllegalArgumentException(
						"requires all or no user, password, and authentication type"
						);
		}

		public String getHost() {
			return host;
		}
		public int getPort() {
			return port;
		}
		public String getUser() {
			return user;
		}
		public String getPassword() {
			return password;
		}
		public Authentication getAuthType() {
			return authType;
		}
	}

	/**
	 * RESTServer specifies an application providing access to a database
	 * using built-in RESTful services.
	 */
	static public class RESTServer {
		private String database;
		private String modulesDatabase;
		private String group;
		private String server;
		private int    port = -1;

		/**
		 * Construct the REST server specification based on properties.
		 * @param properties	specifies the REST server
		 */
		public RESTServer(Properties properties) {
			for (String key: properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				if ("restdb".equals(key))
					database = value;
				else if ("restmodulesdb".equals(key))
					modulesDatabase = value;
				else if ("restgroup".equals(key))
					group = value;
				else if ("restserver".equals(key))
					server = value;
				else if ("restport".equals(key))
					port = Integer.parseInt(value);
			}
			validate();
		}
		/**
		 * Construct the REST server specification programmatically.
		 * @param database	the database exposed by the REST server
		 * @param modulesDatabase	the modules database for the REST server
		 * @param group	the group containing the REST server
		 * @param server	the name of the REST server
		 * @param port	the port for the REST server
		 */
		public RESTServer(
				String database, String modulesDatabase,
				String group, String server, int port
		) {
			this.database = database;
			if (modulesDatabase != null)
				this.modulesDatabase = modulesDatabase;
			if (group != null)
				this.group = group;
			this.server = server;
			this.port = port;
			validate();
		}
		private void validate() {
			if (database == null)
				throw new IllegalArgumentException("database required");
			if (server == null)
				throw new IllegalArgumentException("server required");
			if (port == -1)
				throw new IllegalArgumentException("port required");
		}

		public String getDatabase() {
			return database;
		}
		public String getModulesDatabase() {
			return modulesDatabase;
		}
		public String getGroup() {
			return group;
		}
		public String getServer() {
			return server;
		}
		public int getPort() {
			return port;
		}

		public String toXMLString()
		throws XMLStreamException, FactoryConfigurationError {
	        StringWriter buffer = new StringWriter();

	        XMLStreamWriter writer =
	        	XMLOutputFactory.newFactory().createXMLStreamWriter(buffer);

	        String server          = getServer();
	        int    port            = getPort();
	        String group           = getGroup();
	        String database        = getDatabase();
	        String modulesDatabase = getModulesDatabase();

	        writer.writeStartDocument();
	        writer.writeStartElement("rest-api");
	        writer.writeDefaultNamespace("http://marklogic.com/rest-api");

	        writer.writeStartElement("name");
	        writer.writeCharacters(server);
	        writer.writeEndElement();

	        if (group != null && group.length() > 0) {
	        	writer.writeStartElement("group");
	        	writer.writeCharacters(group);
	        	writer.writeEndElement();
	        }

	        writer.writeStartElement("database");
	        writer.writeCharacters(database);
	        writer.writeEndElement();

	        if (modulesDatabase != null && modulesDatabase.length() > 0) {
	        	writer.writeStartElement("modules-database");
	        	writer.writeCharacters(modulesDatabase);
	        	writer.writeEndElement();
	        }

	        writer.writeStartElement("port");
	        writer.writeCharacters(String.valueOf(port));
	        writer.writeEndElement();

	        writer.writeEndElement();
	        writer.writeEndDocument();

	        return buffer.toString();
		}
	}
}
