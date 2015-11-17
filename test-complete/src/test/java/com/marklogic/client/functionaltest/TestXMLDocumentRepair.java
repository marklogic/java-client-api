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

import java.io.*;
import java.util.Scanner;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.XMLDocumentManager.DocumentRepair;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import org.junit.*;
public class TestXMLDocumentRepair extends BasicJavaClientREST {
	@BeforeClass 
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		setupJavaRESTServerWithDB( "REST-Java-Client-API-Server-withDB", 8015);

	}

	@Test	
	public void testXMLDocumentRepairFull() throws IOException
	{
		// acquire the content 
		File file = new File("repairXMLFull.xml");
		file.delete();
		boolean success = file.createNewFile();
		if(success)
			System.out.println("New file created on " + file.getAbsolutePath());
		else
			System.out.println("Cannot create file");

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		String xmlContent = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<repair>\n" +
						"<p>This is <b>bold and <i>italic</b> within the paragraph.</p>\n" + 
						"<p>This is <b>bold and <i>italic</i></b></u> within the paragraph.</p>\n" +
						"<p>This is <b>bold and <i>italic</b></i> within the paragraph.</p>\n" +
						"</repair>";

		String repairedContent =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<repair>\n" +
						"<p>This is <b>bold and <i>italic</i></b> within the paragraph.</p>\n" + 
						"<p>This is <b>bold and <i>italic</i></b> within the paragraph.</p>\n" +
						"<p>This is <b>bold and <i>italic</i></b> within the paragraph.</p>\n" +
						"</repair>";				

		out.write(xmlContent);
		out.close();

		// create database client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8015, "rest-writer", "x", Authentication.DIGEST);

		// create doc id
		String docId = "/repair/xml/" + file.getName();

		// create document manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// set document repair
		docMgr.setDocumentRepair(DocumentRepair.FULL);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);

		// write the document content
		docMgr.write(docId, handle);

		System.out.println("Write " + docId + " to database");

		// read the document
		docMgr.read(docId, handle);

		// access the document content
		File fileRead = handle.get();

		Scanner scanner = new Scanner(fileRead).useDelimiter("\\Z");
		String readContent = scanner.next();
		assertEquals("XML document write difference", repairedContent, readContent);
		scanner.close();

		// release the client
		client.release();
	}
	@AfterClass	
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		deleteRESTServerWithDB("REST-Java-Client-API-Server-withDB");
	}
}
