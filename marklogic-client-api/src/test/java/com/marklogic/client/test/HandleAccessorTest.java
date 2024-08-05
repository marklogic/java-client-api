/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.impl.HandleAccessor;
import com.marklogic.client.io.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class HandleAccessorTest {
  public static boolean fileInputStreamWasClosed;

  @Test
  public void testContentAsString() throws URISyntaxException, IOException {
    // I'm purposely using a string with a non-ascii character to test
    // charset issues
    String hola = "¡Hola!";
    System.out.println("Default Java Charset: " + Charset.defaultCharset());
    assertEquals( hola,
      HandleAccessor.contentAsString(new StringHandle(hola)));
    assertEquals(hola,
      HandleAccessor.contentAsString(new BytesHandle(hola.getBytes("UTF-8"))));
    URL filePath = this.getClass().getClassLoader().getResource("hola.txt");
    assertEquals( hola,
      HandleAccessor.contentAsString(new ReaderHandle(new StringReader(hola))));
    assertEquals( hola,
      HandleAccessor.contentAsString(new FileHandle(new File(filePath.toURI()))));
    assertEquals( hola,
      HandleAccessor.contentAsString(new InputStreamHandle(filePath.openStream())));

    InputStream fileInputStream = new FileInputStream(new File(filePath.toURI())) {
      @Override
      public void close() throws IOException {
          super.close();
          HandleAccessorTest.fileInputStreamWasClosed = true;
      }
    };
    assertEquals( hola,
      HandleAccessor.contentAsString(new InputStreamHandle(fileInputStream)));
    assertTrue(this.fileInputStreamWasClosed);
  }
}
