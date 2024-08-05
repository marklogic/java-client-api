/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextDocumentTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testReadWrite() throws IOException {
    String docId = "/test/testWrite1.txt";
    String text  = "A simple text document";

    TextDocumentManager docMgr = Common.client.newTextDocumentManager();
    docMgr.write(docId, new StringHandle().with(text));
    assertEquals(text,docMgr.read(docId, new StringHandle()).get());

    BytesHandle bytesHandle = new BytesHandle();
    docMgr.read(docId, bytesHandle);
    assertEquals( bytesHandle.get().length,text.length());

    InputStreamHandle inputStreamHandle = new InputStreamHandle();
    docMgr.read(docId, inputStreamHandle);
    byte[] b = Common.streamToBytes(inputStreamHandle.get());
    assertEquals(new String(b),text);

    Reader reader = docMgr.read(docId, new ReaderHandle()).get();
    String s = Common.readerToString(reader);
    assertEquals(s,text);

    File file = docMgr.read(docId, new FileHandle()).get();
    assertEquals(text.length(),file.length());
  }

}
