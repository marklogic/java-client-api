/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.example.extension;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.example.extension.OpenCSVBatcherExample;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class OpenCSVBatcherTest {
  @Test
  public void testMain()
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    boolean succeeded = false;
    try {
      OpenCSVBatcherExample.main(new String[0]);
      succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    assertTrue( succeeded);
  }
}
