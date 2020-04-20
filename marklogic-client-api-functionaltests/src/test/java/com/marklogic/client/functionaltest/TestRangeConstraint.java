/*
 * Copyright (c) 2019 MarkLogic Corporation
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.SearchHandle;

public class TestRangeConstraint extends BasicJavaClientREST {
  static String filenames[] = { "bbq1.xml", "bbq2.xml", "bbq3.xml", "bbq4.xml", "bbq5.xml" };
  static String queryOptionName = "rangeConstraintOpt.xml";
  private static String dbName = "RangeConstraintDB";
  private static String[] fNames = { "RangeConstraintDB-1" };

  @BeforeClass
  public static void setUp() throws Exception
  {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    addRangeElementIndex(dbName, "decimal", "http://example.com", "rating");
  }

  @Test
  public void testElementRangeConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    DatabaseClient client = getDatabaseClient("rest-admin", "x", getConnType());

    // write docs
    for (String filename : filenames)
    {
      writeDocumentReaderHandle(client, filename, "/range-constraint/", "XML");
    }

    // write the query options to the database
    setQueryOption(client, queryOptionName);

    // run the search
    SearchHandle resultsHandle = runSearch(client, queryOptionName, "rating:5");

    // search result
    String searchResult = returnSearchResult(resultsHandle);

    String expectedSearchResult = "|Matched 1 locations in /range-constraint/bbq4.xml|Matched 1 locations in /range-constraint/bbq3.xml";
    System.out.println(searchResult);

    assertEquals("Search result difference", expectedSearchResult, searchResult);

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
