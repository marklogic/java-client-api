
/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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

    DatabaseClient appClient = Util.newClient(props);
    DatabaseClient adminClient = Util.newAdminClient(props);
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

