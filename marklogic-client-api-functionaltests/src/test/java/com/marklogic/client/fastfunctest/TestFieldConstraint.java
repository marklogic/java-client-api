/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.SearchHandle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



public class TestFieldConstraint extends AbstractFunctionalTest {
  static String filenames[] = { "bbq1.xml", "bbq2.xml", "bbq3.xml", "bbq4.xml", "bbq5.xml" };
  static String queryOptionName = "fieldConstraintOpt.xml";
  private static String dbName = "FieldConstraintDB";
  private static String[] fNames = { "FieldConstraintDB-1" };

  @AfterEach
  public void testCleanUp() throws Exception
  {
    deleteDocuments(connectAsAdmin());
  }

  @Test
  public void testFieldConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentReaderHandle(client, filename, "/field-constraint/", "XML");
    }

    // write the query options to the database
    setQueryOption(client, queryOptionName);

    // run the search
    SearchHandle resultsHandle = runSearch(client, queryOptionName, "summary:Louisiana AND summary:sweet");

    // search result
    String matchResult = "Matched " + resultsHandle.getTotalResults();
    String expectedMatchResult = "Matched 1";
    assertEquals( expectedMatchResult, matchResult);

    String result = returnSearchResult(resultsHandle);
    String expectedResult = "|Matched 3 locations in /field-constraint/bbq3.xml";

    assertEquals( expectedResult, result);

    // release client
    client.release();
  }
}
