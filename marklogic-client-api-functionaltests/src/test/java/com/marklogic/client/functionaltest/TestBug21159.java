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

package com.marklogic.client.functionaltest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.TuplesHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.*;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestBug21159 extends BasicJavaClientREST {

  private static String dbName = "TestTuplesRawCombinedQueryDB";
  private static String[] fNames = { "TestTuplesRawCombinedQueryDB-1" };

  @BeforeAll
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);

    addRangeElementIndex(dbName, "string", "", "grandchild", "http://marklogic.com/collation/");
    addRangeElementIndex(dbName, "double", "", "double");
    addRangeElementIndex(dbName, "int", "", "int");
    addRangeElementIndex(dbName, "string", "", "string", "http://marklogic.com/collation/");
  }

  @Test
  public void testBug21159Tuples() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBug21159Tuples");

    String[] filenames = { "tuples-test1.xml", "tuples-test2.xml", "tuples-test3.xml", "tuples-test4.xml", "lexicon-test1.xml", "lexicon-test2.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
    }

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/LexiconOptions.xml");

    String combinedQuery = convertFileToString(file);

    RawCombinedQueryDefinition rawCombinedQueryDefinition;
    QueryManager queryMgr = client.newQueryManager();
    rawCombinedQueryDefinition = queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedQuery).withMimetype("application/xml"));

    StringHandle stringResults = null;
    ValuesDefinition vdef = queryMgr.newValuesDefinition("grandchild");

    vdef.setQueryDefinition(rawCombinedQueryDefinition);

    stringResults = queryMgr.tuples(vdef, new StringHandle());
    System.out.println(stringResults.get());

    ValuesHandle valuesResults = queryMgr.values(vdef, new ValuesHandle());

    assertFalse(valuesResults.getMetrics().getTotalTime() == -1);

    CountedDistinctValue[] values = valuesResults.getValues();

    assertNotNull(values);

    // release client
    client.release();
  }

  @Test
  public void testBug21159Values() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBug21159Values");

    String[] filenames = { "tuples-test1.xml", "tuples-test2.xml", "tuples-test3.xml", "tuples-test4.xml", "lexicon-test1.xml", "lexicon-test2.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/raw-combined-query/", "XML");
    }

    // get the combined query
    File file = new File("src/test/java/com/marklogic/client/functionaltest/combined/LexiconOptions.xml");

    String combinedQuery = convertFileToString(file);

    RawCombinedQueryDefinition rawCombinedQueryDefinition;
    QueryManager queryMgr = client.newQueryManager();
    rawCombinedQueryDefinition = queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedQuery).withMimetype("application/xml"));

    StringHandle stringResults = null;
    ValuesDefinition vdef = queryMgr.newValuesDefinition("n-way");

    vdef.setQueryDefinition(rawCombinedQueryDefinition);

    stringResults = queryMgr.tuples(vdef, new StringHandle());
    System.out.println(stringResults.get());

    TuplesHandle tuplesResults = queryMgr.tuples(vdef,
        new TuplesHandle());
    Tuple[] tuples = tuplesResults.getTuples();
    assertNotNull(tuples);

    // release client
    client.release();
  }

  @Test
  public void testTuplesWithRawCtsQueryDefinition() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testTuplesWithRawCtsQueryDefinition");
    String queryOptionName = "tuplesTest";

    String[] filenames = { "tuples-test1.xml", "tuples-test2.xml", "tuples-test3.xml", "tuples-test4.xml", "lexicon-test1.xml", "lexicon-test2.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());
    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();
    QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
    StringHandle Opthandle = new StringHandle();
    String options = "<options xmlns=\"http://marklogic.com/appservices/search\"> " +
            "<tuples name=\"n-way\">" +
            "<range type=\"xs:double\"> " +
            "<element ns=\"\" name=\"double\"/> " +
            "</range> " +
            "<range type=\"xs:int\"> " +
            "<element ns=\"\" name=\"int\"/> " +
            "</range> " +
            "<range type=\"xs:string\"> " +
            "<element ns=\"\" name=\"string\"/> " +
            "</range> " +
            "<values-option>ascending</values-option> " +
            "</tuples> " +
            "</options>";
    Opthandle.set(options);

    // write the query options to the database
    optionsMgr.writeOptions(queryOptionName, Opthandle);
    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/Tuples-cts-raw-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create a search definition
    String wordQuery = "<cts:word-query xmlns:cts=\"http://marklogic.com/cts\">" +
            "<cts:text>Alaska</cts:text>" +
            "</cts:word-query>";

    ValuesDefinition valuesDef = queryMgr.newValuesDefinition("n-way", queryOptionName);

    StringHandle handle = new StringHandle().with(wordQuery).withFormat(Format.XML);
    // create query def
    RawCtsQueryDefinition queryRawDef = queryMgr.newRawCtsQueryDefinition(handle);
    valuesDef.setQueryDefinition(queryRawDef);

    TuplesHandle tHandle = queryMgr.tuples(valuesDef, new TuplesHandle());
    Tuple[] distinctValues = tHandle.getTuples();

    System.out.println("TuplesHandle length is " + distinctValues.length);
    assertEquals(2, distinctValues.length);
    TypedDistinctValue[] firstdistinctValues = distinctValues[0].getValues();
    TypedDistinctValue[] seconddistinctValues = distinctValues[1].getValues();

    // Sort the distinct values present inside as array elements in each TypedDistinctValue instance.
    ArrayList<Double> arr = new ArrayList<Double>();
    arr.add(firstdistinctValues[0].get(Double.class));
    arr.add(seconddistinctValues[0].get(Double.class));
    arr.sort(null);

    System.out.println("TuplesHandle name is " + tHandle.getName().toString());
    assertTrue(tHandle.getName().toString().trim().contains("n-way"));

    assertEquals(1.1, arr.get(0), 0.0);
    assertEquals(1.2, arr.get(1), 0.0);
    assertTrue(firstdistinctValues[2].get(String.class).trim().contains("Alaska"));
    assertTrue(seconddistinctValues[2].get(String.class).trim().contains("Alaska"));
    // release client
    client.release();
  }

  @Test
  public void testTuplesWithRawCombinedCtsQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testTuplesWithRawCombinedCtsQuery");
    String[] filenames = { "tuples-test1.xml", "tuples-test2.xml", "tuples-test3.xml", "tuples-test4.xml", "lexicon-test1.xml", "lexicon-test2.xml" };

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());
    // set query option validation to true
    ServerConfigurationManager srvMgr = client.newServerConfigManager();
    srvMgr.readConfiguration();
    srvMgr.setQueryOptionValidation(true);
    srvMgr.writeConfiguration();
    StringHandle Opthandle = new StringHandle();
    String options = "<options xmlns=\"http://marklogic.com/appservices/search\"> " +
            "<tuples name=\"n-way\">" +
            "<range type=\"xs:double\"> " +
            "<element ns=\"\" name=\"double\"/> " +
            "</range> " +
            "<range type=\"xs:int\"> " +
            "<element ns=\"\" name=\"int\"/> " +
            "</range> " +
            "<range type=\"xs:string\"> " +
            "<element ns=\"\" name=\"string\"/> " +
            "</range> " +
            "<values-option>ascending</values-option> " +
            "</tuples> " +
            "</options>";
    Opthandle.set(options);

    // write docs
    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/Tuples-raw-cts-combined-query/", "XML");
    }

    QueryManager queryMgr = client.newQueryManager();

    // create a combined search definition
    String wordQuery = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">" +
            "<cts:word-query xmlns:cts=\"http://marklogic.com/cts\">" +
            "<cts:text>Alaska</cts:text>" +
            "</cts:word-query>"+
            options +
            "</search:search>";

    ValuesDefinition valuesDef = queryMgr.newValuesDefinition("n-way");

    StringHandle handle = new StringHandle().with(wordQuery).withFormat(Format.XML);
    // create query def
    RawCtsQueryDefinition queryRawDef = queryMgr.newRawCtsQueryDefinition(handle);
    valuesDef.setQueryDefinition(queryRawDef);

    TuplesHandle tHandle = queryMgr.tuples(valuesDef, new TuplesHandle());
    Tuple[] distinctValues = tHandle.getTuples();

    System.out.println("TuplesHandle length is " + distinctValues.length);
    assertEquals(2, distinctValues.length);
    TypedDistinctValue[] firstdistinctValues = distinctValues[0].getValues();
    TypedDistinctValue[] seconddistinctValues = distinctValues[1].getValues();

    // Sort the distinct values present inside as array elements in each TypedDistinctValue instance.
    ArrayList<Double> arr = new ArrayList<Double>();
    arr.add(firstdistinctValues[0].get(Double.class));
    arr.add(seconddistinctValues[0].get(Double.class));
    arr.sort(null);

    System.out.println("TuplesHandle name is " + tHandle.getName().toString());
    assertTrue(tHandle.getName().toString().trim().contains("n-way"));

    assertEquals(1.1, arr.get(0), 0.0);
    assertEquals(1.2, arr.get(1), 0.0);
    assertTrue(firstdistinctValues[2].get(String.class).trim().contains("Alaska"));
    assertTrue(seconddistinctValues[2].get(String.class).trim().contains("Alaska"));
    // release client
    client.release();
  }

  @AfterAll
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}
