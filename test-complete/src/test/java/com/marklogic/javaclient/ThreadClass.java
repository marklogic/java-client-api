package com.marklogic.javaclient;

import org.junit.BeforeClass;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.StringHandle;

public class ThreadClass extends Thread{

    String msg;
	
    public void run()
    {	
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8013, "rest-admin", "x", Authentication.DIGEST);
        
        TextDocumentManager docMgr = client.newTextDocumentManager();

        for(int i=1; i<=5; i++)
        {
            System.out.println("Writing document from: "+ msg);
            
            if(msg == "Thread A")
            {	
            	// write docs
            	String docId = "/multithread-content-A/filename" + i + ".txt";
            	docMgr.write(docId, new StringHandle().with("This is so foo"));
            }
            else if(msg == "Thread B")
            {	
            	// write docs
            	String docId = "/multithread-content-B/filename" + i + ".txt";
            	docMgr.write(docId, new StringHandle().with("This is so foo"));
            }
        }
        
        // release client
     	client.release();
    }

    ThreadClass(String mg)
    {
        msg=mg;
    }
}
