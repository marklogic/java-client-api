/*
 * Copyright (c) 2023 MarkLogic Corporation
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
import com.marklogic.client.io.InputStreamHandle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;



/*
 * The tests here run against normal a no SSL enabled REST Server.
 * This is because, there is no point in enabling a SSL on a REST Server and then testing
 * for Basic and None digests.
 */

public class TestDatabaseAuthentication extends AbstractFunctionalTest {

	private static String restServerName = "java-functest";
	private String originalServerAuthentication;

	@BeforeEach
	public void before() {
		originalServerAuthentication = getServerAuthentication(restServerName);
	}

	@AfterEach
	public void teardown() throws Exception {
		setAuthenticationAndDefaultUser(restServerName, originalServerAuthentication, "nobody");
	}

  @Test
  public void testAuthenticationBasic() throws IOException
  {
    if (!IsSecurityEnabled()) {
		setAuthenticationAndDefaultUser(restServerName, "basic", "rest-writer");

      System.out.println("Running testAuthenticationBasic");

      String filename = "text-original.txt";

      // connect the client
      DatabaseClient client = newDatabaseClientBuilder().withBasicAuth("rest-writer", "x").build();

      // write doc
      writeDocumentUsingStringHandle(client, filename, "/write-text-doc-basic/", "Text");

      // read docs
      InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, "/write-text-doc-basic/" + filename, "Text");

      // get the contents
      InputStream fileRead = contentHandle.get();

      String readContent = convertInputStreamToString(fileRead);

      String expectedContent = "hello world, welcome to java API";

      assertEquals( expectedContent.trim(), readContent.trim());

      // release client
      client.release();
    }
  }
}
