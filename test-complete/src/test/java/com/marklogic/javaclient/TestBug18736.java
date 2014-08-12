package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.Format;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;
import org.junit.*;
public class TestBug18736 extends BasicJavaClientREST {

	private static String dbName = "Bug18736DB";
	private static String [] fNames = {"Bug18736DB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

@SuppressWarnings("deprecation")
@Test
	public void testBug18736() throws XpathException, TransformerException, ParserConfigurationException, SAXException, IOException
	{	
		System.out.println("Running testBug18736");
		
		String filename = "constraint1.xml";
		String docId = "/content/without-xml-ext";
		//XpathEngine xpathEngine;
		
		/*HashMap<String,String> xpathNS = new HashMap<String, String>();
		xpathNS.put("", "http://purl.org/dc/elements/1.1/");
		SimpleNamespaceContext xpathNsContext = new SimpleNamespaceContext(xpathNS);

		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalize(true);
		XMLUnit.setNormalizeWhitespace(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		
		xpathEngine = XMLUnit.newXpathEngine();
		xpathEngine.setNamespaceContext(xpathNsContext);*/

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
		// create write handle
		InputStreamHandle writeHandle = new InputStreamHandle();

		// get the file
		InputStream inputStream = new FileInputStream("src/test/java/com/marklogic/javaclient/data/" + filename);
		
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
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
