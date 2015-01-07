
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
package com.marklogic.client.example.extension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.example.extension.GraphManager.GraphFormat;
import com.marklogic.client.example.extension.SPARQLManager.QueryFormat;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;

public class GraphSPARQLExample {
	public static void main(String... args) throws IOException {
		ExampleProperties props = Util.loadProperties();

		installExtensions(props);

		DatabaseClient appClient = DatabaseClientFactory.newClient(
				props.host, props.port,
				props.writerUser, props.writerPassword, props.authType);

		insertGraph(appClient);

		runQuery(appClient);

		deleteGraph(appClient);

		appClient.release();

		uninstallExtensions(props);
	}
	public static void runQuery(DatabaseClient appClient) throws IOException {
		SPARQLManager sparqlMgr = new SPARQLManager(appClient);

		InputStream queryStream = Util.openStream(
			"scripts"+File.separator+"whoKnowsSwarthmore.sparql");
		if (queryStream == null)
			throw new RuntimeException("Could not read SPARQL query");

		InputStreamHandle queryHandle = new InputStreamHandle(queryStream);

		StringHandle result = new StringHandle();

		System.out.println("running query");

		sparqlMgr.search(queryHandle, QueryFormat.NQUAD, result);

		System.out.println(result.get());
	}
	public static void insertGraph(DatabaseClient appClient) throws IOException {
		InputStream tripleStream = Util.openStream(
				"data"+File.separator+"foaf1.nt");
		if (tripleStream == null)
			throw new RuntimeException("Could not read triples");

		GraphManager graphMgr = new GraphManager(appClient);
		
		System.out.println("inserting graph");

		graphMgr.insert(
				GraphFormat.NQUAD, new InputStreamHandle(tripleStream)
				);

		System.out.println("inserted graph");
	}
	public static void deleteGraph(DatabaseClient appClient) throws IOException {
		GraphManager graphMgr = new GraphManager(appClient);

		System.out.println("deleting graph");

		graphMgr.delete();

		System.out.println("deleted graph");
	}
	public static void installExtensions(ExampleProperties props) throws IOException {
		DatabaseClient adminClient = DatabaseClientFactory.newClient(
				props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);

		System.out.println("installing extensions");

		GraphManager.install(adminClient);
		SPARQLManager.install(adminClient);

		System.out.println("installed extensions");

		adminClient.release();
	}
	public static void uninstallExtensions(ExampleProperties props) throws IOException {
		DatabaseClient adminClient = DatabaseClientFactory.newClient(
				props.host, props.port,
				props.adminUser, props.adminPassword, props.authType);

		System.out.println("uninstalling extensions");

		GraphManager.uninstall(adminClient);
		SPARQLManager.uninstall(adminClient);

		System.out.println("uninstalled extensions");

		adminClient.release();
	}
}

