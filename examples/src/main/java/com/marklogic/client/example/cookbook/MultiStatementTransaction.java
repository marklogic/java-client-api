/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.InputStreamHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * MultiStatementTransaction illustrates how to open a transaction, execute
 * multiple statements under the transaction, and commit the transaction.
 */
public class MultiStatementTransaction {
  public static void main(String[] args) throws IOException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props) throws IOException {
    System.out.println("example: "+MultiStatementTransaction.class.getName());

    String beforeFilename = "flipper.xml";
    String afterFilename  = "flapped.xml";

	  DatabaseClient client = Util.newClient(props);

    // create a manager for XML documents
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create an identifier for the document before the move
    String beforeDocId = "/example/"+beforeFilename;

    // create an identifier for the document after the move
    String afterDocId = "/example/"+afterFilename;

    setUpExample(docMgr, beforeDocId, beforeFilename);

    // start the transaction
    Transaction transaction = client.openTransaction();

    // create a handle to receive the document content
    InputStreamHandle handle = new InputStreamHandle();

    // read the document with the old id
    docMgr.read(beforeDocId, handle, transaction);

    // write the document with the new id
    docMgr.write(afterDocId, handle, transaction);

    // delete the document with the old id
    docMgr.delete(beforeDocId, transaction);

    // commit the transaction for the move operation
    transaction.commit();

    System.out.println("Moved document from "+beforeFilename+" to "+afterFilename);

    tearDownExample(docMgr, afterDocId);

    // release the client
    client.release();
  }

  // set up by writing document content for the example to read
  public static void setUpExample(XMLDocumentManager docMgr, String docId, String filename) throws IOException {
    InputStream docStream = Util.openStream("data"+File.separator+filename);
    if (docStream == null)
      throw new IOException("Could not read document example");

    InputStreamHandle handle = new InputStreamHandle();
    handle.set(docStream);

    docMgr.write(docId, handle);
  }

  // clean up by deleting the document read by the example
  public static void tearDownExample(XMLDocumentManager docMgr, String docId) {
    docMgr.delete(docId);
  }
}
