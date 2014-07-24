package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import org.junit.*;
public class TestRequestLogger extends BasicJavaClientREST {

	private static String dbName = "TestRequestLoggerDB";
	private static String [] fNames = {"TestRequestLoggerDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

@BeforeClass public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	 setupJavaRESTServer(dbName, fNames[0],  restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testRequestLogger()
	{	
		System.out.println("testRequestLogger");
		
		String filename = "bbq1.xml";
		String uri = "/request-logger/";
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);

		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);
		
		// create transaction
		Transaction transaction = client.openTransaction();
		
		// create a manager for XML documents
	    XMLDocumentManager docMgr = client.newXMLDocumentManager();
	 
	    // create an identifier for the document
	    String docId = uri + filename;

	    // create a handle on the content
	    FileHandle handle = new FileHandle(file);
	    handle.set(file);
	    
	    // create logger
		RequestLogger logger = client.newLogger(System.out);
		logger.setContentMax(RequestLogger.ALL_CONTENT);
		
		// start logging
		docMgr.startLogging(logger);

	    // write the document content
	    docMgr.write(docId, handle, transaction);
		
	    // commit transaction
		transaction.commit();		
		
		// stop logging
		docMgr.stopLogging();
		
		String expectedContentMax = "9223372036854775807";
		assertEquals("Content log is not equal", expectedContentMax, Long.toString(logger.getContentMax()));
		
		// release client
	    client.release();
	}
	
@AfterClass	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
