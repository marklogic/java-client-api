/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.functionaltest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;

public class ThreadClass extends BasicJavaClientREST implements Runnable {

  String msg;

  public void run() {
    DatabaseClient client;
    try {
      client = getDatabaseClient("rest-admin", "x", getConnType());

      TextDocumentManager docMgr = client.newTextDocumentManager();

      for (int i = 1; i <= 5; i++) {
        System.out.println("Writing document from: " + msg);

        if (msg == "Thread A") {
          // write docs
          String docId = "/multithread-content-A/filename" + i + ".txt";
          docMgr.write(docId, new StringHandle().with("This is so foo"));
        }
        else if (msg == "Thread B") {
          // write docs
          String docId = "/multithread-content-B/filename" + i + ".txt";
          docMgr.write(docId, new StringHandle().with("This is so foo"));
        }
      }

      // release client
      client.release();
    } catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
      e.printStackTrace();
    }

  }

  public ThreadClass(String mg) {
    msg = mg;
  }
}
