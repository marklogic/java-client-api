package com.marklogic.javaclient;

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
	@BeforeClass public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServerWithDB( "REST-Java-Client-API-Server", 8011);
	 
	}
	
	@SuppressWarnings("deprecation")
	@Test	public void testXMLDocumentRepairFull() throws IOException
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
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
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
	@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		deleteRESTServerWithDB("REST-Java-Client-API-Server");
	}
}
