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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.XMLStreamReaderHandle;
public class TestXMLStreamReaderHandle extends BasicJavaClientREST {
	private static String dbName = "XMLStreamReaderHandleDB";
	private static String [] fNames = {"XMLStreamReaderHandleDB-1"};
	

	@BeforeClass	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
	}

	@Test	
	public void testXmlCRUD() throws KeyManagementException, NoSuchAlgorithmException, IOException,  SAXException, ParserConfigurationException, TransformerException, XMLStreamException
	{	
		String filename = "xml-original-test.xml";
		String uri = "/write-xml-XMLStreamReaderHandle/";

		System.out.println("Running testXmlCRUD");

		// connect the client
		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

		// write the doc
		writeDocumentReaderHandle(client, filename, uri, "XML");

		// read the document
		XMLStreamReaderHandle readHandle = readDocumentUsingXMLStreamReaderHandle(client, uri + filename, "XML");

		// access the document content
		XMLStreamReader fileRead = readHandle.get();
		String readContent = convertXMLStreamReaderToString(fileRead);

		// get xml document for expected result
		Document expectedDoc = expectedXMLDocument(filename);
		String expectedContent = convertXMLDocumentToString(expectedDoc);
		expectedContent = "null"+expectedContent.substring(expectedContent.indexOf("<name>")+6, expectedContent.indexOf("</name>"));
		assertEquals("Write XML difference", expectedContent,readContent);	    

		// delete the document
		deleteDocument(client, uri + filename, "XML");

		String exception = "";
		try
		{
			readDocumentReaderHandle(client, uri + filename, "XML");
		} 
		catch (Exception e) { exception = e.toString(); }

		String expectedException = "Could not read non-existent document";
		boolean documentIsDeleted = exception.contains(expectedException);
		assertTrue("Document is not deleted", documentIsDeleted);

		// release the client
		client.release();
	}

	@AfterClass	
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
	}
}