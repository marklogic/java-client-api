/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.SearchHandle;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



public class TestWordConstraint extends AbstractFunctionalTest {

  static String filenames[] = { "word-constraint-doc1.xml", "word-constraint-doc2.xml" };
  static String queryOptionName = "wordConstraintOpt.xml";

  @Test
  public void testElementWordConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentReaderHandle(client, filename, "/word-constraint/", "XML");
    }

    // write the query options to the database
    setQueryOption(client, queryOptionName);

    // run the search
    SearchHandle resultsHandle = runSearch(client, queryOptionName, "my-element-word:paris");

    // search result
    String searchResult = returnSearchResult(resultsHandle);

    String expectedSearchResult = "|Matched 1 locations in /word-constraint/word-constraint-doc1.xml";

    System.out.println(searchResult);

    assertEquals( expectedSearchResult, searchResult);

    // release client
    client.release();
  }
}
