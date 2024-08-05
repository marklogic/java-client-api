/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.SuggestDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



public class TestSearchSuggestion extends AbstractFunctionalTest {

  @AfterEach
  public void testCleanUp() throws Exception
  {
    deleteDocuments(connectAsAdmin());
  }

  @Test
  public void testSearchSuggestion() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestion");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "suggestionOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    assertTrue( suggestions[0].contains("Vannevar Bush"));
    assertTrue( suggestions[1].contains("Vannevar served"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionWithSettersAndQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionWithSettersAndQuery");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "suggestionOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    assertTrue( suggestions[0].contains("Vannevar served"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionMultiByte() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionMultiByte");

    String[] filenames = { "multibyte1.xml", "multibyte2.xml", "multibyte3.xml" };
    String queryOptionName = "suggestionOpt.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    assertTrue( suggestions[0].contains("上海"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionOnAttribute() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionOnAttribute");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "suggestionOpt2.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    assertTrue( suggestions[0].contains("12.34"));
    assertTrue( suggestions[1].contains("123.45"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionWithNS() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionWithNS");

    String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
    String queryOptionName = "suggestionOpt3.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    assertTrue( suggestions[0].contains("2005-01-01"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionWithNSNonDefault() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionWithNSNonDefault");

    String[] filenames = { "suggestion1.xml" };
    String queryOptionName = "suggestionOpt4.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    assertTrue( suggestions[0].contains("noun:actor"));
    assertTrue( suggestions[1].contains("noun:actress"));
    assertTrue( suggestions[2].contains("noun:apricott"));

    // release client
    client.release();
  }

  @Test
  public void testSearchSuggestionWithNSDefault() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchSuggestionWithNSDefault");

    String[] filenames = { "suggestion1.xml" };
    String queryOptionName = "suggestionOpt4.xml";

    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

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

    assertTrue( suggestions[0].contains("act"));
    assertTrue( suggestions[1].contains("acting"));

    // release client
    client.release();
  }
}
