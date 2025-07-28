/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import java.io.IOException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.StringHandle;

/**
 * ClientCreator illustrates how to create a database client.
 */
public class ClientCreator {
  public static void main(String[] args) throws IOException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props) {
    System.out.println("example: "+ClientCreator.class.getName());

    DatabaseClient client = Util.newClient(props);

    // make use of the client connection
    TextDocumentManager docMgr = client.newTextDocumentManager();
    String docId = "/example/text.txt";
    StringHandle handle = new StringHandle();
    handle.set("A simple text document");
    docMgr.write(docId, handle);

    System.out.println(
      "Connected to "+props.host+":"+props.port+" as "+props.writerUser);

    // clean up the written document
    docMgr.delete(docId);

    // release the client
    client.release();
  }
}
