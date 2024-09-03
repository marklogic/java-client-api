/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvalidUserTest {
  @Test
  public void testInvalidUserAuth() {
    // create the client
    DatabaseClient client = Common.makeNewClient(Common.HOST, Common.PORT,
        Common.newSecurityContext("MyFooUser", "x"));


    String expectedException = "com.marklogic.client.FailedRequestException: " +
      "Local message: write failed: Unauthorized. Server Message: Unauthorized";
    String exception = "";

    String docId = "/example/text.txt";
    TextDocumentManager docMgr = client.newTextDocumentManager();
    try {
      // make use of the client connection so we get an auth error
      StringHandle handle = new StringHandle();
      handle.set("A simple text document");
      docMgr.write(docId, handle);
      // the next line will only run if write doesn't throw an exception
      docMgr.delete(docId);
    }
    catch (FailedRequestException e) {
      exception = e.toString();
    } finally {
      client.release();
    }
    assertEquals(expectedException, exception);

  }
}
