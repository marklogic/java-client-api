/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
