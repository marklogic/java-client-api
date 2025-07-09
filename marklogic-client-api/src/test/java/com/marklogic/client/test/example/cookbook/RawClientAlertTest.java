/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.example.cookbook;

import com.marklogic.client.example.cookbook.RawClientAlert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RawClientAlertTest {
  @Test
  public void testMain() {
    boolean succeeded = false;
    try {
      RawClientAlert.main(new String[0]);
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assertions.assertTrue( succeeded);
  }
}
