package com.marklogic.javaclient;

import java.io.File;
import java.util.Random;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;

public class ThreadWrite extends Thread{

    String msg;

    public void run()
    {	
    	String filename = "flipper.xml";
    	
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8013, "rest-writer", "x", Authentication.DIGEST);
        
        File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);
        
        XMLDocumentManager docMgr = client.newXMLDocumentManager();

        for(int i=1; i<=15; i++)
        {
            System.out.println("Writing document " + i + " from: " + msg);
            	
            // write docs
            String docId = "/multithread-write/filename" + i + ".xml";
            docMgr.write(docId, new FileHandle().with(file));
            
            Random rand = new Random();
            int r = rand.nextInt(2000) + 1000;
            
            try {
				Thread.sleep(r);
			} catch (InterruptedException e) { e.printStackTrace(); }
        }
        
        // release client
     	client.release();
    }

    ThreadWrite(String mg)
    {
        msg=mg;
    }
}
