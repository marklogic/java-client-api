/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.InputStreamHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * DocumentFormats illustrates working with documents in multiple or unknown formats.
 */
public class DocumentFormats {
  public static void main(String[] args) throws IOException {
    run(Util.loadProperties());
  }

  public static void run(ExampleProperties props) throws IOException {
    System.out.println("example: "+DocumentFormats.class.getName());

    // a list of files with the format of each file
    String[][] fileEntries = {
      {"mlfavicon.png",   "binary"},
      {"siccodes.json",   "JSON"},
      {"producturis.txt", "text"},
      {"flipper.xml",     "XML"}
    };

	  DatabaseClient client = Util.newClient(props);

    // iterate over the files
    for (String[] fileEntry: fileEntries) {
      String filename = fileEntry[0];
      String format   = fileEntry[1];  // used only for the log message

      writeReadDeleteDocument(client, filename, format);
    }

    // release the client
    client.release();
  }

  // write, read, and delete the document
  public static void writeReadDeleteDocument(DatabaseClient client, String filename, String format) throws IOException {
    // create a manager for documents of any format
    GenericDocumentManager docMgr = client.newDocumentManager();

    InputStream docStream = Util.openStream("data"+File.separator+filename);
    if (docStream == null)
      throw new IOException("Could not read document example");

    // create an identifier for the document
    String docId = "/example/"+filename;

    // create a handle for the document
    InputStreamHandle writeHandle = new InputStreamHandle();
    writeHandle.set(docStream);

    // write the document
    docMgr.write(docId, writeHandle);

    // create a handle to receive the document content
    BytesHandle readHandle = new BytesHandle();

    // read the document content
    docMgr.read(docId, readHandle);

    // access the document content
    byte[] document = readHandle.get();

    // ... do something with the document content ...

    // delete the document
    docMgr.delete(docId);

    System.out.println("Wrote, read, and deleted /example/"+filename+
      " content with "+document.length+" bytes in the "+format+" format");
  }
}
