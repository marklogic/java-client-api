/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.example.extension;

import com.marklogic.client.example.extension.GraphSPARQLExample;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GraphSPARQLExampleTest {


  @Test
  public void testMain() {
    boolean succeeded = false;
    try {
      GraphSPARQLExample.run(Common.newClient(), Common.newRestAdminClient());
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assertions.assertTrue( succeeded);
  }

}
