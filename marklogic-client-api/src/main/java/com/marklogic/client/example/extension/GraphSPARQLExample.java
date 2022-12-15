
/*
 * Copyright (c) 2022 MarkLogic Corporation
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
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;

public class GraphSPARQLExample {
  private static String GRAPH_URI = "com.marklogic.client.example.extension.GraphSPARQLExample";

  public static void main(String... args) throws IOException {
    ExampleProperties props = Util.loadProperties();

    DatabaseClient appClient = DatabaseClientFactory.newClient(
      props.host, props.port,
      props.writerUser, props.writerPassword, props.authType);
    DatabaseClient adminClient = DatabaseClientFactory.newClient(
      props.host, props.port,
      props.adminUser, props.adminPassword, props.authType);
    run(appClient, adminClient);
    appClient.release();
    adminClient.release();
  }

  public static void run(DatabaseClient appClient, DatabaseClient adminClient) throws IOException {
    insertGraph(appClient);

    runQuery(appClient);

    deleteGraph(appClient);
  }

  public static void runQuery(DatabaseClient appClient) throws IOException {
    SPARQLQueryManager sparqlMgr = appClient.newSPARQLQueryManager();

    InputStream queryStream = Util.openStream(
      "scripts"+File.separator+"whoKnowsSwarthmore.sparql");
    if (queryStream == null)
      throw new RuntimeException("Could not read SPARQL query");

    InputStreamHandle queryHandle = new InputStreamHandle(queryStream);

    StringHandle result = new StringHandle();

    System.out.println("running query");
    SPARQLQueryDefinition query = sparqlMgr.newQueryDefinition(queryHandle);
    query.setCollections(GRAPH_URI);

    sparqlMgr.executeSelect(query, result);

    System.out.println(result.get());
  }

  public static void insertGraph(DatabaseClient appClient) throws IOException {
    InputStream tripleStream = Util.openStream(
      "data"+File.separator+"foaf1.nt");
    if (tripleStream == null)
      throw new RuntimeException("Could not read triples");

    GraphManager graphMgr = appClient.newGraphManager();

    System.out.println("inserting graph");

    graphMgr.write(GRAPH_URI, new InputStreamHandle(tripleStream).withMimetype(RDFMimeTypes.NQUADS));

    System.out.println("inserted graph");
  }

  public static void deleteGraph(DatabaseClient appClient) throws IOException {
    GraphManager graphMgr = appClient.newGraphManager();

    System.out.println("deleting graph");

    graphMgr.delete(GRAPH_URI);;

    System.out.println("deleted graph");
  }
}

