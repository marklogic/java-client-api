/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentMetadataValues;
import com.marklogic.client.io.InputStreamHandle;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * DocumentMetadataReader illustrates how to read the metadata and content of a database document
 * in a single request.
 */
public class DocumentMetadataRead {
  public static void main(String[] args) throws IOException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props) throws IOException {
    System.out.println("example: "+DocumentMetadataRead.class.getName());

    String filename = "flipper.xml";

	  DatabaseClient client = Util.newClient(props);

    // create a manager for XML documents
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create an identifier for the document
    String docId = "/example/"+filename;

    setUpExample(docMgr, docId, filename);

    // create a handle to receive the document metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

    // create a handle to receive the document content
    DOMHandle contentHandle = new DOMHandle();

    // read the document metadata and content
    docMgr.read(docId, metadataHandle, contentHandle);

    // access the document metadata
    DocumentCollections collections = metadataHandle.getCollections();

    // access the document metadata-values
    DocumentMetadataValues metadataValues = metadataHandle.getMetadataValues();
    System.out.println(metadataValues.get("key1"));

    // access the document content
    Document document = contentHandle.get();

    String collFirst = collections.toArray(new String[collections.size()])[0];
    String rootName = document.getDocumentElement().getTagName();
    System.out.println("Read /example/"+filename +
      " metadata and content in the '"+collFirst+"' collection with the <"+rootName+"/> root element");

    tearDownExample(docMgr, docId);

    // release the client
    client.release();
  }

  // set up by writing document metadata and content for the example to read
  public static void setUpExample(XMLDocumentManager docMgr, String docId, String filename) throws IOException {
    InputStream docStream = Util.openStream("data"+File.separator+filename);
    if (docStream == null)
      throw new IOException("Could not read document example");

    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    metadataHandle.getCollections().addAll("products", "real-estate");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.setQuality(1);
    metadataHandle.getMetadataValues().add("key1", "value1");

    InputStreamHandle handle = new InputStreamHandle();
    handle.set(docStream);

    docMgr.write(docId, metadataHandle, handle);
  }

  // clean up by deleting the document read by the example
  public static void tearDownExample(XMLDocumentManager docMgr, String docId) {
    docMgr.delete(docId);
  }
}
