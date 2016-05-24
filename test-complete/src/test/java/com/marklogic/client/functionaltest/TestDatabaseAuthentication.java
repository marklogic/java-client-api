/*
 * Copyright 2014-2016 MarkLogic Corporation
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

package com.marklogic.client.functionaltest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import java.io.InputStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.InputStreamHandle;

import org.junit.*;

import static org.junit.Assert.*;
/*
 * The tests here run against normal a no SSL enabled REST Server.
 * This is because, there is no point in enabling a SSL on a REST Server and then testing
 * for Basic and None digests.
 */

public class TestDatabaseAuthentication extends BasicJavaClientREST {

	private static String dbName = "DatabaseAuthenticationDB";
	private static String [] fNames = {"DatabaseAuthenticationDB-1"};
	private static int restPort;
	private static String restServerName;
	
	 @BeforeClass
	public static void setUp() throws Exception {
		System.out.println("In setup");
	    // Setup non - SSL server, if not available.
		loadGradleProperties();
	    restPort =  getHttpPort();
		restServerName = getAppServerName();
		setupJavaRESTServer(dbName, fNames[0], restServerName, restPort);
		setupAppServicesConstraint(dbName);
	}
	 
	 @After
	public  void testCleanUp() throws Exception {
		clearDB();
		System.out.println("Running clear script");
	}

	 @Test 
	 public void testAuthenticationNone() throws KeyManagementException, NoSuchAlgorithmException, IOException
	 {
		 setAuthentication("application-level",restServerName);
		 setDefaultUser("rest-admin",restServerName);

		 System.out.println("Running testAuthenticationNone");

		 String filename = "text-original.txt";

		 // connect the client		 
		 DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011);

		 // write doc
		 writeDocumentUsingStringHandle(client, filename, "/write-text-doc-app-level/", "Text");

		 // read docs
		 InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, "/write-text-doc-app-level/" + filename, "Text");

		 // get the contents
		 InputStream fileRead = contentHandle.get();

		 String readContent = convertInputStreamToString(fileRead);

		 String expectedContent = "hello world, welcome to java API";

		 assertEquals("Write Text difference", expectedContent.trim(), readContent.trim());

		 // release client
		 client.release();

		 setAuthentication("digest",restServerName);
		 setDefaultUser("nobody",restServerName);
	 }
		
	
    @Test
    public void testAuthenticationBasic() throws KeyManagementException, NoSuchAlgorithmException, IOException
	{
		setAuthentication("basic",restServerName);
		setDefaultUser("rest-writer",restServerName);
		
		System.out.println("Running testAuthenticationBasic");
		
		String filename = "text-original.txt";
		
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.BASIC);
		
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
		
		setAuthentication("digest",restServerName);
		setDefaultUser("nobody",restServerName);
	}

    @AfterClass
	public static void tearDown() throws Exception {
		System.out.println("In tear down" );
		
		setAuthentication("digest",restServerName);
		setDefaultUser("nobody",restServerName);
		
		// Tear down non-ssl server.
		tearDownJavaRESTServer(dbName, fNames, restServerName);	
	}
}

