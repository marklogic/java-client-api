/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.example.cookbook;

import com.marklogic.client.example.cookbook.DocumentWriteServerURI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class DocumentWriteServerURITest {
  @Test
  public void testMain() {
    boolean succeeded = false;
    try {
      DocumentWriteServerURI.main(new String[0]);
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assertions.assertTrue( succeeded);
  }
}

