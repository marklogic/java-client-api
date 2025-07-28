/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.example.cookbook;

import com.marklogic.client.example.cookbook.JAXBDocument;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class JAXBDocumentTest {
  @Test
  public void testMain() {
    boolean succeeded = false;
    try {
      JAXBDocument.main(new String[0]);
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    assertTrue( succeeded);
  }
}
