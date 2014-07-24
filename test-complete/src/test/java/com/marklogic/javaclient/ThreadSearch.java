package com.marklogic.javaclient;

import java.util.Random;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.io.SearchHandle;

public class ThreadSearch extends Thread {

    String msg;
    long totalResultsArray[] = new long[10];
    long totalAllResults = 0;

    public void run()
    {	
    	long totalResults = 0;
    	
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8013, "rest-reader", "x", Authentication.DIGEST);
        
        QueryManager queryMgr = client.newQueryManager();
        StringQueryDefinition querydef = queryMgr.newStringDefinition(null);
        
		// create handle
		SearchHandle resultsHandle = new SearchHandle();        

        for(int i=1; i<=10; i++)
        {
            System.out.println("Searching document " + i + " from: " + msg);
            	
            // search docs
            querydef.setCriteria("alert");
            queryMgr.search(querydef, resultsHandle);
            
            totalResults = resultsHandle.getTotalResults();
            
            System.out.println("Results: " + totalResults + " documents returned");
            
            totalResultsArray[i-1] = totalResults;
            
            totalAllResults = totalAllResults + totalResults;
            
            Random rand = new Random();
            int r = rand.nextInt(3000) + 1000;
            
            try {
				Thread.sleep(r);
			} catch (InterruptedException e) { e.printStackTrace(); }
        }
        
        // release client
     	client.release();
    }

    ThreadSearch(String mg)
    {
        msg=mg;
    }
}
