/*
 * Copyright 2013-2018 MarkLogic Corporation
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
package com.marklogic.client.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawQueryByExampleDefinition;

public class QueryByExampleTest {
  @BeforeClass
  public static void beforeClass() {
    Common.connect();
    setupData();
  }

  @AfterClass
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
    assertEquals("6 json results should have matched", results.getTotalResults(), 6);

    DocumentPage documents = jdm.search(query, 1);
    assertEquals("6 json documents should have matched", documents.getTotalSize(), 6);

    documents = jdm.search(query, 1, new JacksonHandle());
    assertEquals("6 json documents should have matched", documents.getTotalSize(), 6);

    documents = jdm.search(query, 1, new SearchHandle());
    assertEquals("6 json documents should have matched", documents.getTotalSize(), 6);
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
    assertEquals("No XML results should have matched", results.getTotalResults(), 0);

    DocumentPage documents = xdm.search(query, 1);
    assertEquals("No XML documents should have matched", documents.getTotalSize(), 0);

    documents = xdm.search(query, 1, new DOMHandle());
    assertEquals("No XML documents should have matched", documents.getTotalSize(), 0);
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
