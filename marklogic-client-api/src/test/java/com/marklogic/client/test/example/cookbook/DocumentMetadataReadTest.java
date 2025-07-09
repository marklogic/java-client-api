/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.example.cookbook;

import com.marklogic.client.example.cookbook.DocumentMetadataRead;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class DocumentMetadataReadTest {
  @Test
  public void testMain() {
    boolean succeeded = false;
    try {
      DocumentMetadataRead.main(new String[0]);
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertTrue( succeeded);
  }
}
