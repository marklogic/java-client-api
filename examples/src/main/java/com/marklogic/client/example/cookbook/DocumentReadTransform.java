/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.*;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * DocumentReadTransform installs a server transform for converting XML documents
 * with a known vocabulary to HTML documents for presentation.
 */
public class DocumentReadTransform {
  static final private String TRANSFORM_NAME = "xml2html";

  public static void main(String[] args)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    run(Util.loadProperties());
  }

  // install the transform and then read a transformed document
  public static void run(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException, ResourceNotResendableException
  {
    System.out.println("example: "+DocumentReadTransform.class.getName());

    installTransform(props);
    readDocument(props);
    tearDownExample(props);
  }

  public static void installTransform(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException
  {
	  DatabaseClient client = Util.newAdminClient(props);

    // create a manager for transform extensions
    TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();

    // specify metadata about the transform extension
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("XML-TO-HTML XSLT Transform");
    metadata.setDescription("This plugin transforms an XML document with a known vocabulary to HTML");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");

    // acquire the transform source code
    InputStream transStream = Util.openStream(
      "scripts"+File.separator+TRANSFORM_NAME+".xsl");
    if (transStream == null)
      throw new IOException("Could not read example transform");

    // create a handle on the transform source code
    InputStreamHandle handle = new InputStreamHandle();
    handle.set(transStream);

    // write the transform extension to the database
    transMgr.writeXSLTransform(TRANSFORM_NAME, handle, metadata);

    System.out.println("Installed the "+TRANSFORM_NAME+" transform");

    // release the client
    client.release();
  }

  public static void readDocument(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    String filename = "flipper.xml";

    DatabaseClient client = Util.newClient(props);

    // create an identifier for the document
    String docId = "/example/"+filename;

    setUpExample(client, docId, filename);

    // create a manager for text documents
    TextDocumentManager docMgr = client.newTextDocumentManager();

    // create a handle on the content
    StringHandle readHandle = new StringHandle();

    // specify the mime type for the content
    readHandle.setMimetype("text/html");

    // specify the transform
    ServerTransform transform = new ServerTransform(TRANSFORM_NAME);

    // read the XML content as HTML by transforming in the database
    docMgr.read(docId, readHandle, transform);

    System.out.println("Read "+docId+" with transform");
    System.out.println(readHandle.get());

    // release the client
    client.release();
  }

  // set up by writing document content for the example to read
  public static void setUpExample(DatabaseClient client, String docId, String filename)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    InputStream docStream = Util.openStream("data"+File.separator+filename);
    if (docStream == null)
      throw new IOException("Could not read document example");

    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    InputStreamHandle handle = new InputStreamHandle();
    handle.set(docStream);

    docMgr.write(docId, handle);
  }

  // clean up by deleting the read document and the example transform
  public static void tearDownExample(ExampleProperties props)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    DatabaseClient client = Util.newAdminClient(props);

    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    docMgr.delete("/example/flipper.xml");

    TransformExtensionsManager transMgr =
      client.newServerConfigManager().newTransformExtensionsManager();

    transMgr.deleteTransform(TRANSFORM_NAME);

    client.release();
  }
}
