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

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
public class TestBug18736 extends BasicJavaClientREST {

	private static String dbName = "Bug18736DB";
	private static String [] fNames = {"Bug18736DB-1"};
	
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  configureRESTServer(dbName, fNames);
	  setupAppServicesConstraint(dbName);
	}

@Test
	public void testBug18736() throws KeyManagementException, NoSuchAlgorithmException, XpathException, TransformerException, ParserConfigurationException, SAXException, IOException
	{	
		System.out.println("Running testBug18736");
		
		String filename = "constraint1.xml";
		String docId = "/content/without-xml-ext";
		//XpathEngine xpathEngine;
		
		/*Map<String,String> xpathNS = new HashMap<>();
		xpathNS.put("", "http://purl.org/dc/elements/1.1/");
		SimpleNamespaceContext xpathNsContext = new SimpleNamespaceContext(xpathNS);

		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalize(true);
		XMLUnit.setNormalizeWhitespace(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		
		xpathEngine = XMLUnit.newXpathEngine();
		xpathEngine.setNamespaceContext(xpathNsContext);*/

		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);
		
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
		// create write handle
		InputStreamHandle writeHandle = new InputStreamHandle();

		// get the file
		InputStream inputStream = new FileInputStream("src/test/java/com/marklogic/client/functionaltest/data/" + filename);
		
		writeHandle.set(inputStream);
		
		// create doc descriptor
		DocumentDescriptor docDesc = docMgr.newDescriptor(docId); 
		
		docMgr.write(docDesc, writeHandle);

		docDesc.setFormat(Format.XML);
        DOMHandle readHandle = new DOMHandle();
        docMgr.read(docDesc, readHandle);
        Document readDoc = readHandle.get();
        String out = convertXMLDocumentToString(readDoc);
        System.out.println(out);
        
        assertTrue("Unable to read doc", out.contains("0011"));
        
        // get xml document for expected result
        //Document expectedDoc = expectedXMLDocument(filename);
     		
     	//assertXMLEqual("Write XML difference", expectedDoc, readDoc);
                
		// release client
	    client.release();	
	}
@AfterClass	
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
		
	}
}
