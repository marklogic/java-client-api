/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;

public class ThreadWrite extends BasicJavaClientREST implements Runnable {

  public String msg;

  public void run()
  {
    String filename = "flipper.xml";
    try {

      DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

      File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);

      XMLDocumentManager docMgr = client.newXMLDocumentManager();

      for (int i = 1; i <= 15; i++)
      {
        System.out.println("Writing document " + i + " from: " + msg);

        // write docs
        String docId = "/multithread-write/filename" + i + ".xml";
        docMgr.write(docId, new FileHandle().with(file));

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

  public ThreadWrite(String mg)
  {
    msg = mg;
  }
}
