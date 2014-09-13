/**
 * This tests meta-data changes for text documents.
 */
package com.marklogic.javaclient;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

/**
 * 
 * This test is designed to update meta-data bulk writes and reads with one type of Manager 
 * and one content type text.
 * 
 *  TextDocumentManager
 *  
 */
public class TestBulkReadWriteMetaDataChange  extends BasicJavaClientREST {
	private static String dbName = "TestBulkReadWriteMetaDataChangeDB";
	private static String [] fNames = {"TestBulkReadWriteMetaDataChangeDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("In Setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		createRESTUser("app-user", "password", "rest-writer","rest-reader" );
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		deleteRESTUser("app-user");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// create new connection for each test below
		client = DatabaseClientFactory.newClient("localhost", restPort, "app-user", "password", Authentication.DIGEST);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("Running clear script");	
		// release client
		client.release();
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

	public DocumentMetadataHandle setUpdatedMetadataProperties() {
		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

		metadataHandle.getProperties().put("reviewed", false);
		metadataHandle.getProperties().put("myString", "bar");
		metadataHandle.getProperties().put("myInteger", 20);
		metadataHandle.getProperties().put("myDecimal", 3459.012678);
		metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(12);
		return	metadataHandle;
	}

	public DocumentMetadataHandle setUpdatedMetadataCollections() {
		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle().withCollections("my-collection3","my-collection4");
		//metadataHandle.getCollections().addAll("my-collection1","my-collection2");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);
		return	metadataHandle;
	}

	public void validateMetadata(DocumentMetadataHandle mh) {
		// get metadata values
		DocumentProperties properties = mh.getProperties();
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();

		// Properties
		String actualProperties = getDocumentPropertiesString(properties);
		System.out.println("Returned properties: " + actualProperties);

		assertTrue("Document properties difference in size value", actualProperties.contains("size:5"));
		assertTrue("Document property reviewed not found or not correct", actualProperties.contains("reviewed:true"));
		assertTrue("Document property myInteger not found or not correct", actualProperties.contains("myInteger:10"));
		assertTrue("Document property myDecimal not found or not correct", actualProperties.contains("myDecimal:34.56678"));
		assertTrue("Document property myCalendar not found or not correct", actualProperties.contains("myCalendar:2014"));
		assertTrue("Document property myString not found or not correct", actualProperties.contains("myString:foo"));

		// Permissions
		String actualPermissions = getDocumentPermissionsString(permissions);
		System.out.println("Returned permissions: " + actualPermissions);

		assertTrue("Document permissions difference in size value", actualPermissions.contains("size:3"));
		assertTrue("Document permissions difference in rest-reader permission", actualPermissions.contains("rest-reader:[READ]"));
		assertTrue("Document permissions difference in rest-writer permission", actualPermissions.contains("rest-writer:[UPDATE]"));
		assertTrue("Document permissions difference in app-user permissions", 
				(actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));

		// Collections 
		String actualCollections = getDocumentCollectionsString(collections);
		System.out.println("Returned collections: " + actualCollections);

		assertTrue("Document collections difference in size value", actualCollections.contains("size:2"));
		assertTrue("my-collection1 not found", actualCollections.contains("my-collection1"));
		assertTrue("my-collection2 not found", actualCollections.contains("my-collection2"));	    
	}

	public void validateUpdatedMetadataProperties(DocumentMetadataHandle mh) {
		// get metadata values
		DocumentProperties properties = mh.getProperties();
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();

		// Properties
		String actualProperties = getDocumentPropertiesString(properties);
		System.out.println("Returned properties after Meta-data only update: " + actualProperties);

		assertTrue("Document properties difference in size value", actualProperties.contains("size:5"));
		assertTrue("Document property reviewed not found or not correct", actualProperties.contains("reviewed:true"));
		assertTrue("Document property myInteger not found or not correct", actualProperties.contains("myInteger:10"));
		assertTrue("Document property myDecimal not found or not correct", actualProperties.contains("myDecimal:34.56678"));
		assertTrue("Document property myCalendar not found or not correct", actualProperties.contains("myCalendar:2014"));
		assertTrue("Document property myString not found or not correct", actualProperties.contains("myString:foo"));

		// Permissions
		String actualPermissions = getDocumentPermissionsString(permissions);
		System.out.println("Returned permissions: " + actualPermissions);

		assertTrue("Document permissions difference in size value", actualPermissions.contains("size:3"));
		assertTrue("Document permissions difference in rest-reader permission", actualPermissions.contains("rest-reader:[READ]"));
		assertTrue("Document permissions difference in rest-writer permission", actualPermissions.contains("rest-writer:[UPDATE]"));
		assertTrue("Document permissions difference in app-user permissions", 
				(actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));

		// Collections 
		String actualCollections = getDocumentCollectionsString(collections);
		System.out.println("Returned collections: " + actualCollections);

		assertTrue("Document collections difference in size value", actualCollections.contains("size:2"));
		assertTrue("my-collection1 not found", actualCollections.contains("my-collection1"));
		assertTrue("my-collection2 not found", actualCollections.contains("my-collection2"));	    
	}

	public void validateUpdatedMetadataCollections(DocumentMetadataHandle mh) {
		// get metadata values
		DocumentProperties properties = mh.getProperties();
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();

		// Properties
		String actualProperties = getDocumentPropertiesString(properties);
		System.out.println("Returned properties: " + actualProperties);

		assertTrue("Document properties difference in size value", actualProperties.contains("size:5"));
		assertTrue("Document property reviewed not found or not correct", actualProperties.contains("reviewed:true"));
		assertTrue("Document property myInteger not found or not correct", actualProperties.contains("myInteger:10"));
		assertTrue("Document property myDecimal not found or not correct", actualProperties.contains("myDecimal:34.56678"));
		assertTrue("Document property myCalendar not found or not correct", actualProperties.contains("myCalendar:2014"));
		assertTrue("Document property myString not found or not correct", actualProperties.contains("myString:foo"));

		// Permissions
		String actualPermissions = getDocumentPermissionsString(permissions);
		System.out.println("Returned permissions: " + actualPermissions);

		assertTrue("Document permissions difference in size value", actualPermissions.contains("size:3"));
		assertTrue("Document permissions difference in rest-reader permission", actualPermissions.contains("rest-reader:[READ]"));
		assertTrue("Document permissions difference in rest-writer permission", actualPermissions.contains("rest-writer:[UPDATE]"));
		assertTrue("Document permissions difference in app-user permissions", 
				(actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));

		// Collections 
		String actualCollections = getDocumentCollectionsString(collections);
		System.out.println("Returned collections: " + actualCollections);

		assertTrue("Document collections difference in size value", actualCollections.contains("size:2"));
		assertTrue("my-collection1 not found", actualCollections.contains("my-collection3"));
		assertTrue("my-collection2 not found", actualCollections.contains("my-collection4"));	    
	}

	/* 
	 * This test verifies that properties do not change when new meta data is used in a bulk write set.
	 * Verified by reading individual documents. User does not have permission to update the meta-data.
	 */

	@Test(expected=com.marklogic.client.ForbiddenUserException.class)	
	public void testWriteMultipleTextDocWithChangedMetadataProperties() {
		String docId[] = {"/foo/test/myFoo1.txt","/foo/test/myFoo2.txt","/foo/test/myFoo3.txt"};

		TextDocumentManager docMgr = client.newTextDocumentManager();

		DocumentWriteSet writeset = docMgr.newWriteSet();
		// put metadata
		DocumentMetadataHandle mh = setMetadata();
		DocumentMetadataHandle mhRead = null;		

		writeset.addDefault(mh);
		writeset.add(docId[0], new StringHandle().with("This is so foo1"));
		writeset.add(docId[1], new StringHandle().with("This is so foo2"));
		writeset.add(docId[2], new StringHandle().with("This is so foo3"));
		docMgr.write(writeset);
		DocumentPage page = docMgr.read(docId);

		while(page.hasNext()){
			DocumentRecord rec = page.next();
			docMgr.readMetadata(rec.getUri(), mhRead);
			validateMetadata(mh);
		}
		validateMetadata(mh);

		// Add new meta-data
		DocumentMetadataHandle mhUpdated = setUpdatedMetadataProperties();
		writeset.addDefault(mhUpdated);

		docMgr.write(writeset);
		DocumentMetadataHandle mhUpd = null;

		for(String docURI : docId){		
			docMgr.readMetadata(docURI, mhUpd);
			validateUpdatedMetadataProperties(mhUpd);
		}
		validateUpdatedMetadataProperties(mhUpd);
	}

	/* 
	 * Purpose: To validate: DocumentManager::read(Transaction, uri....)
	 * This test verifies document meta-data reads from an open database transaction in the representation provided by the handle to call readMetadata.
	 * Verified by reading meta-data of individual document records from Document Page.
	 * read method performs the bulk read
	 */
	@Test
	public void testBulkReadUsingMultipleUri() {
		String docId[] = {"/foo/test/transactionURIFoo1.txt","/foo/test/transactionURIFoo2.txt","/foo/test/transactionURIFoo3.txt"};
		Transaction transaction = client.openTransaction();
		try {
			TextDocumentManager docMgr = client.newTextDocumentManager();
			docMgr.setMetadataCategories(Metadata.ALL);
			DocumentWriteSet writeset = docMgr.newWriteSet();
			// put metadata
			DocumentMetadataHandle mh = setMetadata();
			DocumentMetadataHandle mhRead = new DocumentMetadataHandle();		

			writeset.addDefault(mh);
			writeset.add(docId[0], new StringHandle().with("This is so transactionURIFoo 1"));
			writeset.add(docId[1], new StringHandle().with("This is so transactionURIFoo 2"));
			writeset.add(docId[2], new StringHandle().with("This is so transactionURIFoo 3"));
			docMgr.write(writeset, transaction);
			transaction.commit();
			transaction = client.openTransaction();

			DocumentPage page = docMgr.read(transaction, docId[0], docId[1], docId[2]);

			while(page.hasNext()){
				DocumentRecord rec = page.next();
				mhRead = rec.getMetadata(mhRead);
				validateMetadata(mhRead);
			}

			validateMetadata(mhRead);
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
			throw exp;
		}
		finally {
			transaction.rollback();
		}
	}


	/* 
	 * * Purpose: To validate: DocumentManager::readMetadata(uri, MetdataHandle, Transaction)
	 * This test verifies document meta-data reads from an open database transaction in the representation provided by the handle to call readMetadata.
	 * Verified by reading meta-data for individual documents.
	 */

	@Test	
	public void testReadUsingMultipleUriAndMetadataHandleInTransaction() throws Exception {
		String docId[] = {"/foo/test/multipleURIFoo1.txt","/foo/test/multipleURIFoo2.txt","/foo/test/multipleURIFoo3.txt"};
		Transaction transaction = client.openTransaction();
		try {
			TextDocumentManager docMgr = client.newTextDocumentManager();
			docMgr.setMetadataCategories(Metadata.ALL);

			DocumentWriteSet writeset = docMgr.newWriteSet();
			// put metadata
			DocumentMetadataHandle mh = setMetadata();

			writeset.addDefault(mh);
			writeset.add(docId[0], new StringHandle().with("This is so multipleURI foo 1"));
			writeset.add(docId[1], new StringHandle().with("This is so multipleURI foo 2"));
			writeset.add(docId[2], new StringHandle().with("This is so multipleURI foo 3"));
			docMgr.write(writeset, transaction);
			transaction.commit();
			transaction = client.openTransaction();

			DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

			for(String docStrId : docId) {
				docMgr.readMetadata(docStrId, mhRead, transaction);
				validateMetadata(mhRead);
			}
			validateMetadata(mhRead);
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
			throw exp;
		}
		finally {
			transaction.rollback();
		}
	}


	/* 
	 * * Purpose: To validate: DocumentManager readMetadata(String... uris)
	 * This test verifies document meta-data reads from an open database in the representation provided by the handle to call readMetadata.
	 * Verified by reading meta-data for individual documents.
	 */

	@Test	
	public void testBulkReadMetadataUsingMultipleUriNoTransaction() throws Exception {
		String docId[] = {"/foo/test/URIFoo1.txt","/foo/test/URIFoo2.txt","/foo/test/URIFoo3.txt"};

		try {
			TextDocumentManager docMgr = client.newTextDocumentManager();
			docMgr.setMetadataCategories(Metadata.ALL);

			DocumentWriteSet writeset = docMgr.newWriteSet();
			// put metadata
			DocumentMetadataHandle mh = setMetadata();

			writeset.addDefault(mh);
			writeset.add(docId[0], new StringHandle().with("This is so URI foo 1"));
			writeset.add(docId[1], new StringHandle().with("This is so URI foo 2"));
			writeset.add(docId[2], new StringHandle().with("This is so URI foo 3"));
			docMgr.write(writeset);

			DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

			DocumentPage page = docMgr.readMetadata(docId[0], docId[1], docId[2]);

			while(page.hasNext()){
				DocumentRecord rec = page.next();
				rec.getMetadata(mhRead);
				validateMetadata(mhRead);
			}

			validateMetadata(mhRead);
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
			throw exp;
		}
		finally {
			//transaction.rollback();
		}
	}

	/* 
	 * This test verifies that collections do change when new meta data is used in a bulk write set.
	 * Verified by reading individual documents
	 */
	@Test  
	public void testWriteMultipleTextDocWithChangedMetadataCollections() {
		String docId[] = {"/foo/test/myFoo4.txt","/foo/test/myFoo5.txt","/foo/test/myFoo6.txt"};
		TextDocumentManager docMgr = client.newTextDocumentManager();

		DocumentWriteSet writeset = docMgr.newWriteSet();
		// put metadata
		DocumentMetadataHandle mh = setMetadata();

		writeset.addDefault(mh);
		writeset.add(docId[0], new StringHandle().with("This is so foo4"));
		writeset.add(docId[1], new StringHandle().with("This is so foo5"));
		writeset.add(docId[2], new StringHandle().with("This is so foo6"));
		docMgr.write(writeset);		

		// Add new meta-data
		DocumentMetadataHandle mhUpdated = setUpdatedMetadataCollections();

		docMgr.writeMetadata(docId[0], mhUpdated);
		docMgr.writeMetadata(docId[1], mhUpdated);
		docMgr.writeMetadata(docId[2], mhUpdated);

		DocumentPage page = docMgr.read(docId);
		DocumentMetadataHandle metadataHandleRead = new DocumentMetadataHandle();

		while(page.hasNext()){
			DocumentRecord rec = page.next();
			docMgr.readMetadata(rec.getUri(), metadataHandleRead);
			validateUpdatedMetadataCollections(metadataHandleRead);
		}
		validateUpdatedMetadataCollections(metadataHandleRead);
	}
}
