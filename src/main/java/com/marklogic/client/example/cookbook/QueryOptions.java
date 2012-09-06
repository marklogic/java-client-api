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
import java.util.List;

import javax.xml.namespace.QName;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.config.QueryOptions.QueryConstraint;
import com.marklogic.client.admin.config.QueryOptionsBuilder;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.QueryOptionsHandle;

/**
 * QueryOptions illustrates writing, reading, and deleting query options.
 */
public class QueryOptions {
	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props) throws IOException {
		System.out.println("example: "+QueryOptions.class.getName());

		String optionsName = "products";

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);

		// create a manager for writing, reading, and deleting query options
		QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

		// Create a builder for constructing query configurations.
		QueryOptionsBuilder qob = new QueryOptionsBuilder();

		// create the query options
		QueryOptionsHandle writeHandle;
		writeHandle = new QueryOptionsHandle().withConstraints(
				qob.constraint("industry",
						qob.value(
								qob.elementTermIndex(new QName("industry")))));

		// write the query options to the database
		optionsMgr.writeOptions(optionsName, writeHandle);

		// create a handle to receive the query options
		QueryOptionsHandle readHandle = new QueryOptionsHandle();

		// read the query options from the database
		optionsMgr.readOptions(optionsName, readHandle);

		// access the query options
		List<QueryConstraint> readConstraints = readHandle.getConstraints();

		String constraintName = readConstraints.get(0).getName();
		optionsMgr.deleteOptions(optionsName);

		System.out.println(
				"Wrote, read, and deleted '"+optionsName+"' query options with '"+constraintName+"' constraint");

		// release the client
		client.release();
	}
}
