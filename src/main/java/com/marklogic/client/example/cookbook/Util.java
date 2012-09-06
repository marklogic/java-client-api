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
import java.util.Properties;

import com.marklogic.client.DatabaseClientFactory.Authentication;

/**
 * Utilities to support and simplify examples.
 */
public class Util {
	/**
	 * ExampleProperties represents the configuration for the examples.
	 */
	static public class ExampleProperties {
		public String         host;
		public int            port = -1;
		public String         adminUser;
		public String         adminPassword;
		public String         writerUser;
		public String         writerPassword;
		public Authentication authType;
		public ExampleProperties(Properties props) {
			super();
			host           = props.getProperty("example.host");
			port           = Integer.parseInt(props.getProperty("example.port"));
			adminUser      = props.getProperty("example.admin_user");
			adminPassword  = props.getProperty("example.admin_password");
			writerUser     = props.getProperty("example.writer_user");
			writerPassword = props.getProperty("example.writer_password");
			authType       = Authentication.valueOf(
					props.getProperty("example.authentication_type").toUpperCase()
					);
		}
	}

	/**
	 * Read the configuration properties for the example.
	 * @return	the configuration object
	 */
	public static ExampleProperties loadProperties() throws IOException {
		String propsName = "Example.properties";

		InputStream propsStream = openStream(propsName);
		if (propsStream == null)
			throw new IOException("Could not read properties "+propsName);

		Properties props = new Properties();
		props.load(propsStream);

		return new ExampleProperties(props);
	}

	/**
	 * Read a resource for an example.
	 * @param fileName	the name of the resource
	 * @return	an input stream for the resource
	 * @throws IOException
	 */
	public static InputStream openStream(String fileName) throws IOException {
		return Util.class.getClassLoader().getResourceAsStream(fileName);
	}
}
