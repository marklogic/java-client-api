/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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

  public String msg;
  public long totalResultsArray[] = new long[10];
  public long totalAllResults = 0;

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
        int r = rand.nextInt(200) + 100;

        try {
          Thread.sleep(r);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      // release client
      client.release();
    } catch (KeyManagementException | NoSuchAlgorithmException | IOException e1) {
      e1.printStackTrace();
    }
  }

  public ThreadSearch(String mg)
  {
    msg = mg;
  }
}
