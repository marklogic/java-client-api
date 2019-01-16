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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class ThreadSearch extends BasicJavaClientREST implements Runnable {

  String msg;
  long totalResultsArray[] = new long[10];
  long totalAllResults = 0;

  public void run()
  {
    long totalResults = 0;

    DatabaseClient client;
    try {

      client = getDatabaseClient("rest-reader", "x", getConnType());
      QueryManager queryMgr = client.newQueryManager();
      StringQueryDefinition querydef = queryMgr.newStringDefinition(null);

      // create handle
      SearchHandle resultsHandle = new SearchHandle();

      for (int i = 1; i <= 10; i++)
      {
        System.out.println("Searching document " + i + " from: " + msg);

        // search docs
        querydef.setCriteria("alert");
        queryMgr.search(querydef, resultsHandle);

        totalResults = resultsHandle.getTotalResults();

        System.out.println("Results: " + totalResults + " documents returned");

        totalResultsArray[i - 1] = totalResults;

        totalAllResults = totalAllResults + totalResults;

        Random rand = new Random();
        int r = rand.nextInt(3000) + 1000;

        try {
          Thread.sleep(r);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      // release client
      client.release();
    } catch (KeyManagementException | NoSuchAlgorithmException
        | IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  ThreadSearch(String mg)
  {
    msg = mg;
  }
}
