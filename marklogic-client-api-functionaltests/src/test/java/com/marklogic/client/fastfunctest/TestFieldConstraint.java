/*
 * Copyright (c) 2023 MarkLogic Corporation
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
