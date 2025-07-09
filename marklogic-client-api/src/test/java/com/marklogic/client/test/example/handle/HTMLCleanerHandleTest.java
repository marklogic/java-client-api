/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.example.handle;

import com.marklogic.client.example.handle.HTMLCleanerHandleExample;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class HTMLCleanerHandleTest {
  @Test
  public void testMain() {
    boolean succeeded = false;
    try {
      HTMLCleanerHandleExample.main(new String[0]);
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertTrue( succeeded);
  }
}
