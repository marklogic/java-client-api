package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Scanner;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.SourceHandle;
import org.junit.*;

public class TestTransformXMLWithXSLT extends BasicJavaClientREST {


	@BeforeClass public static void setUp() throws Exception 
		{
		  System.out.println("In setup");
		  setupJavaRESTServerWithDB( "REST-Java-Client-API-Server", 8011);
		 
		}
		
	@SuppressWarnings("deprecation")
	@Test	public void testWriteXMLWithXSLTransform() throws TransformerException, FileNotFoundException
	{	
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// get the doc
		Source source = new StreamSource("src/test/java/com/marklogic/javaclient/data/employee.xml");
		
		// get the xslt
		Source xsl = new StreamSource("src/test/java/com/marklogic/javaclient/data/employee-stylesheet.xsl");
				
		// create transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(xsl);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		// create a doc manager
	    XMLDocumentManager docMgr = client.newXMLDocumentManager();
	 
	    // create an identifier for the document
	    String docId = "/example/trans/transform.xml";
	    
	    // create a handle on the content
	    SourceHandle handle = new SourceHandle();
	    handle.set(source);
	    
	    // set the transformer
	    handle.setTransformer(transformer);
	    
	    // write the document content
	    docMgr.write(docId, handle);
	    
	    System.out.println("Write " + docId + " to database");

	    // create a handle on the content
	    FileHandle readHandle = new FileHandle();
	    
	    // read the document
	    docMgr.read(docId, readHandle);
	    
	    // access the document content
	    File fileRead = readHandle.get();
	    
	    Scanner scanner = new Scanner(fileRead).useDelimiter("\\Z");
	    String readContent = scanner.next();
	    String transformedContent = readContent.replaceAll("^name$", "firstname");
	    assertEquals("XML document write difference", transformedContent, readContent);
	    scanner.close();	    
	    
	    // release client
	    client.release();
	}
	@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		deleteRESTServerWithDB("REST-Java-Client-API-Server");
	}
}
