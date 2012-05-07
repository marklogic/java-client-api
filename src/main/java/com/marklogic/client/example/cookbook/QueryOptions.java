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
import java.util.List;
import java.util.Properties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.QueryOptionsManager;
import com.marklogic.client.configpojos.Constraint;
import com.marklogic.client.configpojos.Value;
import com.marklogic.client.io.QueryOptionsHandle;
import com.marklogic.client.io.StringHandle;

/**
 * QueryOptions illustrates writing, reading, and deleting query options.
 */
public class QueryOptions {

	public static void main(String[] args) throws IOException {
		Properties props = loadProperties();

		// connection parameters for admin user
		String         host           = props.getProperty("example.host");
		int            port           = Integer.parseInt(props.getProperty("example.port"));
		String         admin_user     = props.getProperty("example.admin_user");
		String         admin_password = props.getProperty("example.admin_password");
		Authentication authType       = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		run(host, port, admin_user, admin_password, authType);
	}

	public static void run(String host, int port, String user, String password, Authentication authType) {
		System.out.println("example: "+QueryOptions.class.getName());

		String optionsName = "products";

		// connect the client
		DatabaseClient client = DatabaseClientFactory.connect(host, port, user, password, authType);

		// create a manager for writing, reading, and deleting query options
		QueryOptionsManager optionsMgr = client.newQueryOptionsManager();

		// create the query options
		QueryOptionsHandle options = new QueryOptionsHandle()
				.withConstraintDefinition(new Value()
					.withElement("industry")
					.inside(new Constraint("industry")));
		
//		StringBuilder builder = new StringBuilder();
//		builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
//		builder.append("<options xmlns=\"http://marklogic.com/appservices/search\">\n");
//		builder.append("  <constraint name=\"industry\">\n");
//		builder.append("    <value>\n");
//		builder.append("      <element ns=\"\" name=\"industry\"/>\n");
//		builder.append("    </value>\n");
//		builder.append("  </constraint>\n");
//		builder.append("</options>\n");

		// initialize a handle with the query options
		//StringHandle writeHandle = new StringHandle(builder.toString());

		// write the query options to the database
		optionsMgr.writeOptions(optionsName, options);

		// create a handle to receive the query options
		QueryOptionsHandle readHandle = new QueryOptionsHandle();

		// read the query options from the database
		optionsMgr.readOptions(optionsName, readHandle);

		// access the query options
		List<Constraint> readConstraints = readHandle.getConstraints();

		String constraintName = readConstraints.get(0).getName();
		optionsMgr.deleteOptions(optionsName);

		System.out.println(
				"Wrote, read, and deleted '"+optionsName+"' query options with '"+constraintName+"' constraint");

		// release the client
		client.release();
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream =
			QueryOptions.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}

}
