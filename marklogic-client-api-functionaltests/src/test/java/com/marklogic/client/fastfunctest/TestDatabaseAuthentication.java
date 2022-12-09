/*
 * Copyright (c) 2022 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.io.InputStreamHandle;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/*
 * The tests here run against normal a no SSL enabled REST Server.
 * This is because, there is no point in enabling a SSL on a REST Server and then testing
 * for Basic and None digests.
 */

public class TestDatabaseAuthentication extends AbstractFunctionalTest {

  private static String restServerName = "java-functest";

  @After
  public void teardown() throws Exception {
    setAuthentication(securityContextType, restServerName);
    setDefaultUser("nobody", restServerName);
  }

  // Should throw exceptions when none specified.
  @Test
  public void testAuthenticationNone() throws IOException
  {
    System.out.println("Running testAuthenticationNone");
    if (!IsSecurityEnabled()) {
      setAuthentication("application-level", restServerName);
      setDefaultUser("rest-admin", restServerName);
      // connect the client
      StringBuilder str = new StringBuilder();
      try {
    	  DatabaseClient client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort());
      } catch (Exception ex) {
        str.append(ex.getMessage());
      }
      assertEquals("Write Text difference", "makeSecurityContext should only be called with BASIC or DIGEST Authentication",
          str.toString().trim());
    }
  }

  @Test
  public void testAuthenticationBasic() throws IOException
  {
    if (!IsSecurityEnabled()) {
      setAuthentication("basic", restServerName);
      setDefaultUser("rest-writer", restServerName);

      System.out.println("Running testAuthenticationBasic");

      String filename = "text-original.txt";

      // connect the client
      SecurityContext secContext = new DatabaseClientFactory.BasicAuthContext("rest-writer", "x");
      DatabaseClient client = DatabaseClientFactory.newClient(getRestServerHostName(), getRestServerPort(), secContext, getConnType());

      // write doc
      writeDocumentUsingStringHandle(client, filename, "/write-text-doc-basic/", "Text");

      // read docs
      InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, "/write-text-doc-basic/" + filename, "Text");

      // get the contents
      InputStream fileRead = contentHandle.get();

      String readContent = convertInputStreamToString(fileRead);

      String expectedContent = "hello world, welcome to java API";

      assertEquals("Write Text difference", expectedContent.trim(), readContent.trim());

      // release client
      client.release();
    }
  }
}
