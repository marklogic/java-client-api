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
package com.marklogic.client.example.handle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.dom4j.DocumentException;
import org.jdom.JDOMException;

import com.marklogic.client.DatabaseClientFactory.Authentication;

/**
 * AllHandleExamples executes all of the examples of content handles.
 * Please set up a REST server and configure Example.properties
 * before running any example.
 */
public class AllHandleExamples {
	public static void main(String[] args)
	throws IOException, DocumentException, JDOMException, ValidityException, ParsingException {
		Properties props = loadProperties();

		// connection parameters for writer and admin users
		String         host            = props.getProperty("example.host");
		int            port            = Integer.parseInt(props.getProperty("example.port"));
		String         writer_user     = props.getProperty("example.writer_user");
		String         writer_password = props.getProperty("example.writer_password");
		Authentication authType        = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		// execute the examples
		DOM4JHandleExample.run(host, port, writer_user, writer_password, authType);
		HTMLCleanerHandleExample.run(host, port, writer_user, writer_password, authType);
		JacksonHandleExample.run(host, port, writer_user, writer_password, authType);
		JDOMHandleExample.run(host, port, writer_user, writer_password, authType);
		URIHandleExample.run(host, port, writer_user, writer_password, authType);
		XOMHandleExample.run(host, port, writer_user, writer_password, authType);
	}

	// get the configuration for the examples
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			AllHandleExamples.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}
}
