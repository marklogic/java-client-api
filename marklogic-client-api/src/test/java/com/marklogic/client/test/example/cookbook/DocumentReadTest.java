/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.example.cookbook;

import com.marklogic.client.example.cookbook.DocumentRead;
import org.junit.jupiter.api.Test;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class DocumentReadTest {
  @Test
  public void testMain() {
    boolean succeeded = false;
    try {
      DocumentRead.main(new String[0]);
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }
    assertTrue( succeeded);
  }
}
