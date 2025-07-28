/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.extra;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.extra.gson.GSONHandle;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GSONHandleTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testReadWrite() {
    // create an identifier for the database document
    String docId = "/example/gson-test.json";

    // create a manager for JSON documents
    JSONDocumentManager docMgr = Common.client.newJSONDocumentManager();

    // construct a GSON JSON structure
    JsonObject childObj = new JsonObject();
    childObj.addProperty("key", "value");
    JsonArray childArray = new JsonArray();
    childArray.add(new JsonPrimitive("item1"));
    childArray.add(new JsonPrimitive("item2"));
    JsonObject writeRoot = new JsonObject();
    writeRoot.add("object", childObj);
    writeRoot.add("array",  childArray);

    // create a handle for the JSON structure
    GSONHandle writeHandle = new GSONHandle(writeRoot);

    // write the document to the database
    docMgr.write(docId, writeHandle);

    // create a handle to receive the database content as a GSON structure
    GSONHandle readHandle = new GSONHandle();

    // read the document content from the database as a GSON structure
    docMgr.read(docId, readHandle);

    // access the document content
    JsonObject readRoot = readHandle.get().getAsJsonObject();
    Assertions.assertNotNull( readRoot);
    Assertions.assertTrue(
      readRoot.equals(writeRoot));

    // delete the document
    docMgr.delete(docId);
  }
}
