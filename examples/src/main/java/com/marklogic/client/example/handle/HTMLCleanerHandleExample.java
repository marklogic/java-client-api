/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.handle;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.StringHandle;
import org.htmlcleaner.ITagInfoProvider;
import org.htmlcleaner.TagInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * HTMLCleanerHandleExample illustrates writing HTML content as
 * an indexable XHTML document using the HTMLCleanerHandle example
 * of a content handle extension.
 */
public class HTMLCleanerHandleExample {

  public static void main(String[] args) throws IOException {
    run(Util.loadProperties());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void run(ExampleProperties props) throws IOException {
    System.out.println("example: "+HTMLCleanerHandleExample.class.getName());

    String fileroot = "sentiment";

	  DatabaseClient client = Util.newClient(props);

    // create a manager for documents of any format
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // read the example file
    InputStream docStream = Util.openStream(
      "data"+File.separator+fileroot+".html");
    if (docStream == null)
      throw new IOException("Could not read document example");

    // create an identifier for the document
    String docId = "/example/"+fileroot+".xhtml";

    // create a handle for the content
    HTMLCleanerHandle writeHandle = new HTMLCleanerHandle();

    // configure the parser rules for this content
    ITagInfoProvider rules = writeHandle.getRulesProvider();
    TagInfo rule = rules.getTagInfo("p");
    Set pCloseTags = rule.getMustCloseTags();
    pCloseTags.add("h1");

    // set the handle to the parsed HTML content
    writeHandle.set(docStream, "UTF-8");

    // write the converted XHTML content
    docMgr.write(docId, writeHandle);

    // create a handle to receive the XHTML content
    StringHandle readHandle = new StringHandle();

    // read the document content
    docMgr.read(docId, readHandle);

    // delete the document
    docMgr.delete(docId);

    System.out.println("Wrote /example/"+fileroot+".xhtml using HTMLCleaner\n"+
      readHandle.get());

    // release the client
    client.release();
  }
}
