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

package com.marklogic.client.converted;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertTrue;

public class TestSearchMultipleForests extends AbstractFunctionalTest {

  private static String[] fNames = { "java-functest-1", "java-functest-2" };

  @Test
  public void testSearchMultipleForest() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testSearchMultipleForest");
    // connect the client
    DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

    DocumentManager docMgr = client.newDocumentManager();
    StringHandle writeHandle1 = new StringHandle();
    for (int a = 1; a <= 10; a++) {
      writeHandle1.set("<root>hello</root>");
      docMgr.write("/forest-A/file" + a + ".xml", writeHandle1);
    }

    StringHandle writeHandle2 = new StringHandle();
    for (int b = 1; b <= 10; b++) {
      writeHandle2.set("<root>hello</root>");
      docMgr.write("/forest-B/file" + b + ".xml", writeHandle1);
    }

    QueryManager queryMgr = client.newQueryManager();

    // create query def
    StringQueryDefinition querydef = queryMgr.newStringDefinition("");
    querydef.setCriteria("hello");

    SearchHandle sHandle = new SearchHandle();
    // New handles to search for documents on individual forests.
    SearchHandle sHandleOnForest1 = new SearchHandle();
    SearchHandle sHandleOnForest2 = new SearchHandle();

    queryMgr.search(querydef, sHandle);

    // Do forest specific searches.
    queryMgr.search(querydef, sHandleOnForest1, fNames[0]);
    System.out.println("Documents available on Forest 1 is " + sHandleOnForest1.getTotalResults());
    assertTrue("Documents count on Forest 1 is ", sHandleOnForest1.getTotalResults() >= 0);

    queryMgr.search(querydef, sHandleOnForest2, fNames[1]);
    System.out.println("Documents available on Forest 2 is " + sHandleOnForest2.getTotalResults());
    assertTrue("Documents count on Forest 2 is ", sHandleOnForest2.getTotalResults() >= 0);

    // Assert on the total from individual forest counts. Assignment scheme changed as of 10.0.2 ML Server.

    assertTrue("Document count is incorrect", sHandleOnForest1.getTotalResults() + sHandleOnForest2.getTotalResults() == 20);

    System.out.println(sHandle.getTotalResults());
    assertTrue("Document count is incorrect", sHandle.getTotalResults() == 20);

    // Test other overloaded methods of QueryManager search with forest name.

    // Test with start page.
    sHandleOnForest1 = new SearchHandle();
    sHandleOnForest2 = new SearchHandle();

    queryMgr.setPageLength(3);

    queryMgr.search(querydef, sHandleOnForest1, 2, fNames[0]);
    System.out.println("Start Page on first search on Forest 1 is " + sHandleOnForest1.getStart());
    assertTrue("Start Page on first search on Forest 1 is incorrect", sHandleOnForest1.getStart() == 2);

    queryMgr.search(querydef, sHandleOnForest2, 3, fNames[1]);
    System.out.println("Start Page on second search on Forest 2 is " + sHandleOnForest2.getStart());
    assertTrue("Start Page on second search on Forest 2 is incorrect", sHandleOnForest2.getStart() == 3);

    assertTrue("Document count is incorrect", sHandleOnForest1.getTotalResults() + sHandleOnForest2.getTotalResults() == 20);

    // Verify in a transaction.
    Transaction t = client.openTransaction();

    sHandleOnForest1 = new SearchHandle();
    sHandleOnForest2 = new SearchHandle();

    queryMgr.search(querydef, sHandleOnForest1, 0, t, fNames[0]);
    assertTrue("Documents count on Forest 1 is ", sHandleOnForest1.getTotalResults() >= 0);
    queryMgr.search(querydef, sHandleOnForest2, 0, t, fNames[1]);
    assertTrue("Documents count on Forest 2 is ", sHandleOnForest2.getTotalResults() >= 0);
    t.rollback();

    client.release();
  }
}
