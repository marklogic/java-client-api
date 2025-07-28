/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * DocumentReader illustrates how to read the content of a database document.
 */
public class DocumentRead {
  public static void main(String[] args) throws IOException, XPathExpressionException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props) throws IOException, XPathExpressionException {
    System.out.println("example: "+DocumentRead.class.getName());

	  DatabaseClient client = Util.newClient(props);

    setUpExample(client);

    // use either shortcut or strong typed IO
    runShortcut(client);
    runStrongTyped(client);

    tearDownExample(client);

    // release the client
    client.release();
  }
  public static void runShortcut(DatabaseClient client) throws IOException {
    // create a manager for XML documents
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create an identifier for the document
    String docId = "/example/flipper.xml";

    // read the document content
    Document document = docMgr.readAs(docId, Document.class);

    // access the document content
    String rootName = document.getDocumentElement().getTagName();
    System.out.println("(Shortcut) Read "+docId+" content with the <"+rootName+"/> root element");
  }
  public static void runStrongTyped(DatabaseClient client) throws IOException, XPathExpressionException {
    // create a manager for XML documents
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create an identifier for the document
    String docId = "/example/flipper.xml";

    // create a handle to receive the document content
    DOMHandle handle = new DOMHandle();

    // read the document content
    docMgr.read(docId, handle);
    Document document = handle.get();

    // apply an XPath 1.0 expression to the document
    String productName = handle.evaluateXPath("string(/product/name)", String.class);

    // access the document content
    String rootName = document.getDocumentElement().getTagName();
    System.out.println("(Strong Typed) Read /example/"+docId+" content with the <"+rootName+"/> root element for the "+productName+" product");
  }

  // set up by writing document content for the example to read
  public static void setUpExample(DatabaseClient client) throws IOException {
    String filename = "flipper.xml";

    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    String docId = "/example/"+filename;

    InputStream docStream = Util.openStream("data"+File.separator+filename);
    if (docStream == null)
      throw new IOException("Could not read document example");

    InputStreamHandle handle = new InputStreamHandle();
    handle.set(docStream);

    docMgr.write(docId, handle);
  }

  // clean up by deleting the document read by the example
  public static void tearDownExample(DatabaseClient client) {
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    String docId = "/example/flipper.xml";

    docMgr.delete(docId);
  }
}
