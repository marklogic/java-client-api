/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.InputStreamHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * DocumentMetadataWriter illustrates how to write metadata and content to a database document
 * in a single request.
 */
public class DocumentMetadataWrite {
  public static void main(String[] args) throws IOException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props) throws IOException {
    System.out.println("example: "+DocumentMetadataWrite.class.getName());

    String filename = "flipper.xml";

	  DatabaseClient client = Util.newClient(props);

    // acquire the content
    InputStream docStream = Util.openStream("data"+File.separator+filename);
    if (docStream == null)
      throw new IOException("Could not read document example");

    // create a manager for XML documents
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create an identifier for the document
    String docId = "/example/"+filename;

    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    metadataHandle.getCollections().addAll("products", "real-estate");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.setQuality(1);
    metadataHandle.getMetadataValues().add("key1", "value1");

    // create a handle on the content
    InputStreamHandle contentHandle = new InputStreamHandle();
    contentHandle.set(docStream);

    // write the document metadata and content
    docMgr.write(docId, metadataHandle, contentHandle);

    System.out.println("Wrote /example/"+filename+" metadata and content");

    tearDownExample(docMgr, docId);

    // release the client
    client.release();
  }

  // clean up by deleting the document that the example wrote
  public static void tearDownExample(XMLDocumentManager docMgr, String docId) {
    docMgr.delete(docId);
  }
}
