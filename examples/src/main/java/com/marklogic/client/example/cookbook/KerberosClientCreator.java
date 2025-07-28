/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import java.io.IOException;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.KerberosAuthContext;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.StringHandle;


/**
 * KerberosClientCreator illustrates how to create a database client using Kerberos Authentication
 */
class KerberosClientCreator
{
  public static void main(String[] args) throws IOException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props) {
    System.out.println("example: "+KerberosClientCreator.class.getName());

    // create the client
    DatabaseClient client = null;
    client = DatabaseClientFactory.newClient(props.host, props.port, new KerberosAuthContext());

    // make use of the client connection
    TextDocumentManager docMgr = client.newTextDocumentManager();
    String docId = "/example/text.txt";
    StringHandle handle = new StringHandle();
    handle.set("A simple text document");
    docMgr.write(docId, handle);

    System.out.println(
      "Connected to "+props.host+":"+props.port+" using kerberos authentication");

    // clean up the written document
    docMgr.delete(docId);

    // release the client
    client.release();
  }
}
