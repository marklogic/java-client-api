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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.StringHandle;
import javax.xml.transform.Source;

public class TestDocumentEncoding extends BasicJavaClientREST
{
	private static String dbName = "DocumentEncodingDB";
	private static String [] fNames = {"DocumentEncodingDB-1"};
	
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
	}

	@Test
	public void testEncoding() throws KeyManagementException, NoSuchAlgorithmException, IOException,  TransformerException, ParserConfigurationException
	{
		// connect the client
		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        Document doc = impl.createDocument(null,null,null);
        Element e1 = doc.createElement("howto");
        doc.appendChild(e1);
        Element e2 = doc.createElement("java");
        e1.appendChild(e2);
        e2.setAttribute("url","http://www.rgagnon.com/howto.html");
        Text text = doc.createTextNode("漢字");
        e2.appendChild(text);

        // transform the Document into a String
        Source domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING,"Cp1252");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        java.io.StringWriter sw = new java.io.StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        
        String xml = sw.toString();	
        System.out.println(xml);
        
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        StringHandle writeHandle = new StringHandle();
        writeHandle.set(xml);
        docMgr.write("/doc/foo.xml", writeHandle);
        
        System.out.println(docMgr.read("/doc/foo.xml", new StringHandle()).get());
        int length1 = docMgr.read("/doc/foo.xml", new BytesHandle()).get().length;
        System.out.println(length1);
        
        // ************************
        
        DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder2 = factory2.newDocumentBuilder();
        DOMImplementation impl2 = builder2.getDOMImplementation();

        Document doc2 = impl2.createDocument(null,null,null);
        Element x1 = doc2.createElement("howto");
        doc2.appendChild(x1);
        Element x2 = doc2.createElement("java");
        x1.appendChild(x2);
        x2.setAttribute("url","http://www.rgagnon.com/howto.html");
        Text text2 = doc2.createTextNode("漢字");
        x2.appendChild(text2);
        
        Source domSource2 = new DOMSource(doc2);
        TransformerFactory tf2 = TransformerFactory.newInstance();
        Transformer transformer2 = tf2.newTransformer();
        transformer2.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer2.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer2.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer2.setOutputProperty(OutputKeys.INDENT, "yes");
        java.io.StringWriter sw2 = new java.io.StringWriter();
        StreamResult sr2 = new StreamResult(sw2);
        transformer2.transform(domSource2, sr2);
        String xml2 = sw2.toString();
        
        System.out.println("*********** UTF-8 ************");
        System.out.println(xml2);
        
        StringHandle writeHandle2 = new StringHandle();
        writeHandle2.set(xml2);
        docMgr.write("/doc/bar.xml", writeHandle2);
        System.out.println(docMgr.read("/doc/bar.xml", new StringHandle()).get());
        int length2 = docMgr.read("/doc/bar.xml", new BytesHandle()).get().length;
        System.out.println(length2);
        
        assertEquals("Byte size is not the same", length1, length2);
        
        // **************************
		
		client.release();
	}
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
	}
}
