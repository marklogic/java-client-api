package com.marklogic.javaclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import org.junit.*;
public class TestMetadataXML extends BasicJavaClientREST {
	
	private static String dbName = "TestMetadataXMLDB";
	private static String [] fNames = {"TestMetadataXMLDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}

	
	@Test
	public void testMetadataXMLCRUD() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testMetadataXMLCRUD");
		
		String filename = "Simple_ScanTe.png";
		String uri = "/write-bin-metadata/";
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// create doc manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
		// get the original metadata
		Document docMetadata = getXMLMetadata("metadata-original.xml");

		// WRITE
	    // write the doc
	    writeDocumentUsingBytesHandle(client, filename, uri, "Binary");

		// create handle to write metadata
		DOMHandle writeMetadataHandle = new DOMHandle();
		writeMetadataHandle.set(docMetadata);
		
		// create doc id
		String docId = uri + filename;
	    	    
	    // write original metadata
	    docMgr.writeMetadata(docId, writeMetadataHandle);
	    
	    // create handle to read metadata
	    DOMHandle readMetadataHandle = new DOMHandle();
	    
	    // READ
	    // read metadata
	    docMgr.readMetadata(docId, readMetadataHandle);
	    Document docReadMetadata = readMetadataHandle.get();
	    	    
	    assertXpathEvaluatesTo("coll1", "string(//*[local-name()='collection'][1])", docReadMetadata);
	    assertXpathEvaluatesTo("coll2", "string(//*[local-name()='collection'][2])", docReadMetadata);
	    assertXpathEvaluatesTo("MarkLogic", "string(//*[local-name()='Author'])", docReadMetadata);
	    
	    // UPDATE
		// get the update metadata
		Document docMetadataUpdate = getXMLMetadata("metadata-updated.xml");

		// create handle for metadata update
		DOMHandle writeMetadataHandleUpdate = new DOMHandle();
		writeMetadataHandleUpdate.set(docMetadataUpdate);
		
		// write updated metadata
	    docMgr.writeMetadata(docId, writeMetadataHandleUpdate);
	    
	    // create handle to read updated metadata
	    DOMHandle readMetadataHandleUpdate = new DOMHandle();

	    // read updated metadata
	    docMgr.readMetadata(docId, readMetadataHandleUpdate);
	    Document docReadMetadataUpdate = readMetadataHandleUpdate.get();
	    
	    assertXpathEvaluatesTo("coll1", "string(//*[local-name()='collection'][1])", docReadMetadataUpdate);
	    assertXpathEvaluatesTo("coll3", "string(//*[local-name()='collection'][2])", docReadMetadataUpdate);
	    assertXpathEvaluatesTo("23", "string(//*[local-name()='quality'])", docReadMetadataUpdate);
	    assertXpathEvaluatesTo("Aries", "string(//*[local-name()='Author'])", docReadMetadataUpdate);

	    // DELETE
	    // write default metadata
	    docMgr.writeDefaultMetadata(docId);

	    // create handle to read deleted metadata
	    DOMHandle readMetadataHandleDelete = new DOMHandle();
	    
	    // read deleted metadata
	    docMgr.readMetadata(docId, readMetadataHandleDelete);
	    Document docReadMetadataDelete = readMetadataHandleDelete.get();
	    
	    assertXpathEvaluatesTo("0", "string(//*[local-name()='quality'])", docReadMetadataDelete);
	    
	    // release the client
	    client.release();
	}	


	
	@Test	
	public void testMetadataXMLNegative() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testMetadataXMLNegative");
		
		String filename = "Simple_ScanTe.png";
		String uri = "/write-neg-metadata/";
				
		// connect the client
		DatabaseClient client1 = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
	    // write the doc
	    writeDocumentUsingBytesHandle(client1, filename, uri, "Binary");
		
		// connect with another client to write metadata
		DatabaseClient client2 = DatabaseClientFactory.newClient("localhost", 8011, "rest-reader", "x", Authentication.DIGEST);

		// create doc manager
		XMLDocumentManager docMgr = client2.newXMLDocumentManager();
		
		// get the original metadata
		Document docMetadata = getXMLMetadata("metadata-original.xml");

		// create handle to write metadata
		DOMHandle writeMetadataHandle = new DOMHandle();
		writeMetadataHandle.set(docMetadata);
		
		// create doc id
	    String docId = uri + filename;
	    
	    String expectedException = "You do not have permission to this method and URL";
	    String exception = "";
	    
	    // write original metadata
	    try
	    {
	    	docMgr.writeMetadata(docId, writeMetadataHandle);
	    }
	    catch (Exception e) { exception = e.toString(); } 
	    
	    //assertEquals("Could write metadata with forbidden user", expectedException, exception);
	    
	    boolean exceptionIsThrown = exception.contains(expectedException);
	    assertTrue("Exception is not thrown", exceptionIsThrown);
	    
	    // release the clients
	    client1.release();
	    client2.release();
	}	
@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
