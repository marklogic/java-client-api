/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryByExampleTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    setupData();
  }

  @AfterAll
  public static void afterClass() {
    cleanUpData();
  }

  @Test
  public void jsonQbe() {
    JSONDocumentManager jdm = Common.client.newJSONDocumentManager();
    QueryManager qm = Common.client.newQueryManager();
    String queryAsString = "{ \"$query\": { \"kind\": \"bird\" }    }";
    StringHandle handle = new StringHandle();
    handle.withFormat(Format.JSON).set(queryAsString);
    RawQueryByExampleDefinition query =
      qm.newRawQueryByExampleDefinition(handle);

    StringHandle report = qm.validate(query, new StringHandle());
    System.out.println(report.toString());

    SearchHandle results = qm.search(query, new SearchHandle());
    assertEquals(results.getTotalResults(), 6);

    DocumentPage documents = jdm.search(query, 1);
    assertEquals(documents.getTotalSize(), 6);

    documents = jdm.search(query, 1, new JacksonHandle());
    assertEquals(documents.getTotalSize(), 6);

    documents = jdm.search(query, 1, new SearchHandle());
    assertEquals(documents.getTotalSize(), 6);
  }

  @Test
  public void xmlQbe() {
    XMLDocumentManager xdm = Common.client.newXMLDocumentManager();
    QueryManager qm = Common.client.newQueryManager();
    String queryAsString =
      "<q:qbe xmlns:q='http://marklogic.com/appservices/querybyexample'>" +
        "<q:query>" +
          "<kind>bird</kind>" +
        "</q:query>" +
      "</q:qbe>";
    RawQueryByExampleDefinition query =
      qm.newRawQueryByExampleDefinition(new StringHandle(queryAsString).withFormat(Format.XML));

    StringHandle report = qm.validate(query, new StringHandle());
    System.out.println(report.toString());

    SearchHandle results = qm.search(query, new SearchHandle());
    assertEquals(results.getTotalResults(), 0);

    DocumentPage documents = xdm.search(query, 1);
    assertEquals(documents.getTotalSize(), 0);

    documents = xdm.search(query, 1, new DOMHandle());
    assertEquals(documents.getTotalSize(), 0);
  }

  public static void setupData() {
    JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();
    DocumentWriteSet writeSet = docMgr.newWriteSet();
    String[] animals = new String[] {
      "{ \"name\": \"aardvark\",  \"kind\": \"mammal\" }",
      "{ \"name\": \"badger\",    \"kind\": \"mammal\" }",
      "{ \"name\": \"camel\",     \"kind\": \"mammal\" }",
      "{ \"name\": \"duck\",      \"kind\": \"bird\" }",
      "{ \"name\": \"emu\",       \"kind\": \"bird\" }",
      "{ \"name\": \"fox\",       \"kind\": \"mammal\" }",
      "{ \"name\": \"goose\",     \"kind\": \"bird\" }",
      "{ \"name\": \"hare\",      \"kind\": \"mammal\" }",
      "{ \"name\": \"ibex\",      \"kind\": \"bird\" }",
      "{ \"name\": \"jaguar\",    \"kind\": \"mammal\" }",
      "{ \"name\": \"kangaroo\",  \"kind\": \"marsupial\" }",
      "{ \"name\": \"lemur\",     \"kind\": \"mammal\" }",
      "{ \"name\": \"moose\",     \"kind\": \"mammal\" }",
      "{ \"name\": \"nighthawk\", \"kind\": \"bird\" }",
      "{ \"name\": \"ocelot\",    \"kind\": \"mammal\" }",
      "{ \"name\": \"panda\",     \"kind\": \"mammal\" }",
      "{ \"name\": \"rhino\",     \"kind\": \"mammal\" }",
      "{ \"name\": \"snake\",     \"kind\": \"reptile\" }",
      "{ \"name\": \"turtle\",    \"kind\": \"reptile\" }",
      "{ \"name\": \"urial\",     \"kind\": \"mammal\" }",
      "{ \"name\": \"vulture\",   \"kind\": \"bird\" }",
      "{ \"name\": \"wallaby\",   \"kind\": \"marsupial\" }",
      "{ \"name\": \"yak\",       \"kind\": \"mammal\" }",
      "{ \"name\": \"zebra\",     \"kind\": \"mammal\" }"
    };
    for ( int i=0; i < animals.length; i++ ) {
      String animal = animals[i];
      writeSet.add( "/animals/" + i + ".json", new StringHandle(animal).withFormat(Format.JSON));
    }
    docMgr.write(writeSet);
  }

  public static void cleanUpData() {
    QueryManager qm = Common.client.newQueryManager();
    DeleteQueryDefinition deleteQuery = qm.newDeleteDefinition();
    deleteQuery.setDirectory("/animals/");
    qm.delete(deleteQuery);
  }
}
