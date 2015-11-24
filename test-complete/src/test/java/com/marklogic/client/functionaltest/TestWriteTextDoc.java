/*
 * Copyright 2014-2015 MarkLogic Corporation
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

import static org.junit.Assert.*;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;
import org.junit.*;
public class TestWriteTextDoc extends BasicJavaClientREST
{

	@BeforeClass 
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		setupJavaRESTServerWithDB( "REST-Java-Client-API-Server-withDB", 8015);

	}

	@Test  
	public void testWriteTextDoc()  
	{
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8015, "admin", "admin", Authentication.DIGEST);

		String docId = "/foo/test/myFoo.txt";
		TextDocumentManager docMgr = client.newTextDocumentManager();
		docMgr.write(docId, new StringHandle().with("This is so foo"));
		assertEquals("Text document write difference", "This is so foo", docMgr.read(docId, new StringHandle()).get());
	}
	
	@AfterClass	
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		deleteRESTServerWithDB("REST-Java-Client-API-Server-withDB");
	}
}
