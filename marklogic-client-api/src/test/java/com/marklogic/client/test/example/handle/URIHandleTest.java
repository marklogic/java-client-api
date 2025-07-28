/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.example.handle;

import com.marklogic.client.example.handle.URIHandleExample;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class URIHandleTest {
  @Test
  public void testMain() {
    boolean succeeded = false;
    try {
      URIHandleExample.main(new String[0]);
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assertions.assertTrue( succeeded);
  }
}
