/*
 * Copyright 2014-2018 MarkLogic Corporation
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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.SuggestDefinition;

public class TestSearchSuggestion extends BasicJavaClientREST {

  private static String dbName = "SearchSuggestionDB";
  private static String[] fNames = { "SearchSuggestionDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");

    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);
    addRangeElementIndex(dbName, "string", "http://action/", "title", "http://marklogic.com/collation/");
    addRangeElementIndex(dbName, "string", "http://noun/", "title", "http://marklogic.com/collation/");
    addRangeElementAttributeIndex(dbName, "decimal", "http://cloudbank.com", "price", "", "amt", "http://marklogic.com/collation/");
    addFieldExcludeRoot(dbName, "para");
    includeElementFieldWithWeight(dbName, "para", "", "p", 5, "", "", "");
  }

  @After
  public void testCleanUp() throws Exception
  {
    clearDB();
    System.out.println("Running clear script");
  }

  @Test
  public void testSearchSuggestion() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestion");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "suggestionOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/ss/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    SuggestDefinition def = queryMgr.newSuggestDefinition("V", queryOptionName);

    String[] suggestions = queryMgr.suggest(def);

    for (int i = 0; i < suggestions.length; i++)
    {
      System.out.println(suggestions[i]);
    }

    assertTrue("suggestion is wrong", suggestions[0].contains("Vannevar Bush"));
    assertTrue("suggestion is wrong", suggestions[1].contains("Vannevar served"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionWithSettersAndQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionWithSettersAndQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "suggestionOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/ss/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    SuggestDefinition def = queryMgr.newSuggestDefinition();
    def.setOptionsName(queryOptionName);
    def.setStringCriteria("V");
    def.setQueryStrings("policymaker");
    def.setLimit(2);

    String[] suggestions = queryMgr.suggest(def);

    for (int i = 0; i < suggestions.length; i++)
    {
      System.out.println(suggestions[i]);
    }

    assertTrue("suggestion is wrong", suggestions[0].contains("Vannevar served"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionMultiByte() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionMultiByte");

    String[] filenames = { "multibyte1.xml", "multibyte2.xml", "multibyte3.xml" };
    String queryOptionName = "suggestionOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/ss/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    SuggestDefinition def = queryMgr.newSuggestDefinition("上海", queryOptionName);

    String[] suggestions = queryMgr.suggest(def);

    for (int i = 0; i < suggestions.length; i++)
    {
      System.out.println(suggestions[i]);
    }

    assertTrue("suggestion is wrong", suggestions[0].contains("上海"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionOnAttribute() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionOnAttribute");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "suggestionOpt2.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/ss/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    SuggestDefinition def = queryMgr.newSuggestDefinition("12", queryOptionName);
    // SuggestDefinition def = queryMgr.newSuggestDefinition();

    String[] suggestions = queryMgr.suggest(def);

    for (int i = 0; i < suggestions.length; i++)
    {
      System.out.println(suggestions[i]);
    }

    assertTrue("suggestion is wrong", suggestions[0].contains("12.34"));
    assertTrue("suggestion is wrong", suggestions[1].contains("123.45"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionWithNS() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionWithNS");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "suggestionOpt3.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/ss/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    SuggestDefinition def = queryMgr.newSuggestDefinition("2005-01-01", queryOptionName);

    String[] suggestions = queryMgr.suggest(def);

    for (int i = 0; i < suggestions.length; i++)
    {
      System.out.println(suggestions[i]);
    }

    assertTrue("suggestion is wrong", suggestions[0].contains("2005-01-01"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionWithNSNonDefault() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionWithNSNonDefault");

    String[] filenames = { "suggestion1.xml" };
    String queryOptionName = "suggestionOpt4.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/ss/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    // SuggestDefinition def = queryMgr.newSuggestDefinition();
    SuggestDefinition def = queryMgr.newSuggestDefinition();
    def.setOptionsName(queryOptionName);
    def.setStringCriteria("noun:a");

    String[] suggestions = queryMgr.suggest(def);

    for (int i = 0; i < suggestions.length; i++)
    {
      System.out.println(suggestions[i]);
    }

    assertTrue("suggestion is wrong", suggestions[0].contains("noun:actor"));
    assertTrue("suggestion is wrong", suggestions[1].contains("noun:actress"));
    assertTrue("suggestion is wrong", suggestions[2].contains("noun:apricott"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionWithNSDefault() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionWithNSDefault");

    String[] filenames = { "suggestion1.xml" };
    String queryOptionName = "suggestionOpt4.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);

    // write docs
    for (String filename : filenames)
    {
      writeDocumentUsingInputStreamHandle(client, filename, "/ss/", "XML");
    }

    setQueryOption(client, queryOptionName);

    QueryManager queryMgr = client.newQueryManager();

    SuggestDefinition def = queryMgr.newSuggestDefinition();
    def.setOptionsName(queryOptionName);
    def.setStringCriteria("a");

    String[] suggestions = queryMgr.suggest(def);

    for (int i = 0; i < suggestions.length; i++)
    {
      System.out.println(suggestions[i]);
    }

    assertTrue("suggestion is wrong", suggestions[0].contains("act"));
    assertTrue("suggestion is wrong", suggestions[1].contains("acting"));

    // release client
    client.release();
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
  }
}
