package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Calendar;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.StringHandle;

/*
 * This test is designed to to test optimistic locking simple bulk writes with different types of 
 * Managers and different content type like JSON,text,binary,XMl
 * 
 *  TextDocumentManager
 *  XMLDocumentManager
 *  BinaryDocumentManager
 *  JSONDocumentManager
 *  GenericDocumentManager
 */

public class TestBulkWriteOptimisticLocking extends BasicJavaClientREST {
	private static String dbName = "TestBulkWriteOptLockDB";
	private static String[] fNames = { "TestBulkWriteOptLockDB-1" };
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private DatabaseClient client;

	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName, restPort);
	}

	@Before
	public void testSetup() throws Exception {
		// create new connection for each test below
		client = DatabaseClientFactory.newClient("localhost", restPort,
				"rest-admin", "x", Authentication.DIGEST);

		// create server configuration manager
		ServerConfigurationManager configMgr = client.newServerConfigManager();

		// read the server configuration from the database
		configMgr.readConfiguration();

		// require content versions for updates and deletes
		// use UpdatePolicy.VERSION_OPTIONAL to allow but not
		// require identifier use. Use UpdatePolicy.MERGE_METADATA
		// (the default) to deactive identifier use
		configMgr.setUpdatePolicy(UpdatePolicy.VERSION_REQUIRED);

		// write the server configuration to the database
		configMgr.writeConfiguration();

		// release the client
		// client.release();

	}

	@After
	public void testCleanUp() throws Exception {
		System.out.println("Running clear script");
		// release client
		client.release();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
	
	public DocumentMetadataHandle setMetadata(){
		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		metadataHandle.getCollections().addAll("my-collection1","my-collection2");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);
		return	metadataHandle;
	}

	@Test
	public void testWriteSingleOptimisticLocking() throws Exception {

		String bookFilename = "book.xml";
		String bookURI = "/book-xml-handle/";
		
		String docId = bookURI + bookFilename;
		GenericDocumentManager docMgr = client.newDocumentManager();		

		writeDocumentUsingInputStreamHandle(client, bookFilename, bookURI,
				"XML");

		// create a descriptor for versions of the document
		DocumentDescriptor desc = docMgr.newDescriptor(docId);

		// provide a handle for updating the content of the document
		FileHandle updateHandle = new FileHandle();

		// read the document, capturing the initial version with the descriptor
		docMgr.read(desc, updateHandle);

		long descriptorFirstVersion = desc.getVersion();
		System.out.println("created " + docId + " as version "
				+ descriptorFirstVersion);
		

		// modify the document
		Document document = expectedXMLDocument(bookFilename);
		document.getDocumentElement().setAttribute("modified", "true");

		// update the document, specifying the current version with the
		// descriptor
		// if the document changed after reading, write() throws an exception
		docMgr.write(desc, updateHandle);

		// get the updated version without getting the content
		desc = docMgr.exists(docId);
		
		long descriptorSecondVersion = desc.getVersion();
		System.out.println("updated " + docId + " as version "
				+ descriptorSecondVersion);

		// delete the document, specifying the current version with the
		// descriptor
		// if the document changed after exists(), delete() throws an exception
		docMgr.delete(desc);

		// release the client
		client.release();
		
		assertTrue("The document descriptors are equal. They need to be different.", (descriptorFirstVersion != descriptorSecondVersion));
	}
	
	@Test
	public void testWriteBulkOptimisticLocking() throws Exception {

		String nameId[] = {"property1.xml","property2.xml","property3.xml"};
		String docId[] = {"/opt/lock/property1.xml","/opt/lock/property2.xml","/opt/lock/property3.xml"};
		
		TextDocumentManager docMgr = client.newTextDocumentManager();		

		// Write the documents.
		DocumentWriteSet writeset = docMgr.newWriteSet();
		// Set meta-data
		DocumentMetadataHandle mh = setMetadata();
		
		writeset.addDefault(mh); 
		writeset.add(docId[0], new StringHandle().with("This is so foo 1"));
		writeset.add(docId[1], new StringHandle().with("This is so foo 2"));
		writeset.add(docId[2], new StringHandle().with("This is so foo 3"));
		
		docMgr.write(writeset);

		DocumentPage page = docMgr.read(docId);
		
		DocumentDescriptor[] docDescriptor = new DocumentDescriptor[3];
		long[] descOrigLongArray = new long[10];
		long[] descNewLongArray = new long[10];		
		
		// provide a handle for updating the content of the document
		FileHandle updateHandle = null;
		
		    for(int i=0;i<3;i++) {
									
			// create a descriptor for versions of the document
			docDescriptor[i] = docMgr.newDescriptor(docId[i]);
			// provide a handle for updating the content of the document
			updateHandle = new FileHandle();
			// read the document, capturing the initial version with the descriptor
			docMgr.read(docDescriptor[i], updateHandle);
			descOrigLongArray[i] = docDescriptor[i].getVersion();
			
			// modify the document
			Document document = expectedXMLDocument(nameId[i]);
			document.getDocumentElement().setAttribute("modified", "true");
					
			
			// if the document changed after reading, write() throws an exception
			docMgr.write(docDescriptor[i], updateHandle);
			
			// get the updated version without getting the content
			docDescriptor[i] = docMgr.exists(docId[i]);
			descNewLongArray[i] = docDescriptor[i].getVersion();						
		}			
		//docMgr.delete(desc);
		assertFalse("The document descriptors are equal. They need to be different.",Arrays.equals(descOrigLongArray, descOrigLongArray));

		// release the client
		client.release();
	}
	/*
	*
	* Use UpdatePolicy.VERSION_OPTIONAL and write test methods
	*
	*/
}