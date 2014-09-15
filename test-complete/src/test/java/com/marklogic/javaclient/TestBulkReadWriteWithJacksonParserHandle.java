package com.marklogic.javaclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Calendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.ReaderHandle;

/*
 * This test is designed to to test all of bulk reads and write of JSON  with JacksonHandle Manager by passing set of uris
 * and also by descriptors.
 */

public class TestBulkReadWriteWithJacksonParserHandle extends
		BasicJavaClientREST {

	private static final int BATCH_SIZE = 100;
	private static final String DIRECTORY = "/bulkread/";
	private static String dbName = "TestBulkReadWriteWithJacksonParserDB";
	private static String[] fNames = { "TestBulkReadWriteWithJacksonParserDB-1" };
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private DatabaseClient client;

	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName, restPort);
		setupAppServicesConstraint(dbName);

	}

	@Before
	public void testSetup() throws Exception {
		// create new connection for each test below
		client = DatabaseClientFactory.newClient("localhost", restPort,
				"rest-admin", "x", Authentication.DIGEST);
	}

	@After
	public void testCleanUp() throws Exception {
		System.out.println("Running CleanUp script");
		// release client
		client.release();
	}

	public DocumentMetadataHandle setMetadata() {
		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		metadataHandle.getCollections().addAll("my-collection1",
				"my-collection2");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
				Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar",
				Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);
		return metadataHandle;
	}

	public void validateMetadata(DocumentMetadataHandle mh) {
		// get metadata values
		DocumentProperties properties = mh.getProperties();
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();

		// Properties
		// String expectedProperties =
		// "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
		String actualProperties = getDocumentPropertiesString(properties);
		boolean result = actualProperties.contains("size:5|");
		assertTrue("Document properties count", result);

		// Permissions
		String actualPermissions = getDocumentPermissionsString(permissions);
		System.out.println(actualPermissions);

		assertTrue("Document permissions difference in size value",
				actualPermissions.contains("size:3"));
		//assertTrue(
		//		"Document permissions difference in flexrep-eval permission",
		//		actualPermissions.contains("flexrep-eval:[READ]"));
		assertTrue("Document permissions difference in rest-reader permission",
				actualPermissions.contains("rest-reader:[READ]"));
		assertTrue("Document permissions difference in rest-writer permission",
				actualPermissions.contains("rest-writer:[UPDATE]"));
		assertTrue(
				"Document permissions difference in app-user permissions",
				(actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions
						.contains("app-user:[READ, UPDATE]")));

		// Collections
		String actualCollections = getDocumentCollectionsString(collections);
		System.out.println(collections);

		assertTrue("Document collections difference in size value",
				actualCollections.contains("size:2"));
		assertTrue("my-collection1 not found",
				actualCollections.contains("my-collection1"));
		assertTrue("my-collection2 not found",
				actualCollections.contains("my-collection2"));
	}

	public void validateDefaultMetadata(DocumentMetadataHandle mh){
		// get metadata values
		DocumentProperties properties = mh.getProperties();
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();

		// Properties
		String actualProperties = getDocumentPropertiesString(properties);
		boolean result =actualProperties.contains("size:0|");
		System.out.println(actualProperties +result);
		assertTrue("Document default last modified properties count1?", result);

		// Permissions	    
		String actualPermissions = getDocumentPermissionsString(permissions);

		assertTrue("Document permissions difference in size value", actualPermissions.contains("size:2"));
		//assertTrue("Document permissions difference in flexrep-eval permission", actualPermissions.contains("flexrep-eval:[READ]"));
		assertTrue("Document permissions difference in rest-reader permission", actualPermissions.contains("rest-reader:[READ]"));
		assertTrue("Document permissions difference in rest-writer permission", actualPermissions.contains("rest-writer:[UPDATE]"));

		// Collections 
		String expectedCollections = "size:0|";
		String actualCollections = getDocumentCollectionsString(collections);

		assertEquals("Document collections difference", expectedCollections, actualCollections);
	}

	@Test
	public void testWriteMultipleJSONDocs() throws Exception {
		String docId[] = { "/a.json", "/b.json", "/c.json" };
		String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
		String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
		String json3 = new String("{\"animal\":\"rat\", \"says\":\"keek\"}");
		
		JsonFactory f = new JsonFactory();    

		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentWriteSet writeset = docMgr.newWriteSet();
		
		JacksonParserHandle jacksonParserHandle1 = new JacksonParserHandle();
		JacksonParserHandle jacksonParserHandle2 = new JacksonParserHandle();
		JacksonParserHandle jacksonParserHandle3 = new JacksonParserHandle();
		
		jacksonParserHandle1.set(f.createJsonParser(json1));
		jacksonParserHandle2.set(f.createJsonParser(json2));
		jacksonParserHandle3.set(f.createJsonParser(json3));

		writeset.add(docId[0], jacksonParserHandle1);
		writeset.add(docId[1], jacksonParserHandle2);
		writeset.add(docId[2], jacksonParserHandle3);

		docMgr.write(writeset);
		
		JacksonHandle r1 = new JacksonHandle();
		docMgr.read(docId[0], r1);	
		JSONAssert.assertEquals(json1, r1.toString(), true);
		
		docMgr.read(docId[1], r1);
		JSONAssert.assertEquals(json2, r1.toString(), true);
		
		docMgr.read(docId[2], r1);
		JSONAssert.assertEquals(json3, r1.toString(), true);
	}

	/*
	 * 
	 * Use JacksonHandle to load json strings using bulk write set. Test Bulk
	 * Read to see you can read the document specific meta-data.
	 */

	@Test
	public void testWriteMultipleJSONDocsWithDefaultMetadata() throws Exception {
		String docId[] = { "/a.json", "/b.json", "/c.json" };
		String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
		String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
		String json3 = new String("{\"animal\":\"rat\", \"says\":\"keek\"}");

		JsonFactory f = new JsonFactory();
		
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		
		DocumentWriteSet writeset = docMgr.newWriteSet();
		// put metadata
		DocumentMetadataHandle mh = setMetadata();

		JacksonParserHandle jacksonParserHandle1 = new JacksonParserHandle();
		JacksonParserHandle jacksonParserHandle2 = new JacksonParserHandle();
		JacksonParserHandle jacksonParserHandle3 = new JacksonParserHandle();
		
		jacksonParserHandle1.set(f.createJsonParser(json1));
		jacksonParserHandle2.set(f.createJsonParser(json2));
		jacksonParserHandle3.set(f.createJsonParser(json3));
		
		writeset.addDefault(mh);
		writeset.add(docId[0], jacksonParserHandle1);
		writeset.add(docId[1], jacksonParserHandle2);
		writeset.add(docId[2], jacksonParserHandle3);		
		
		docMgr.write(writeset);

		DocumentPage page = docMgr.read(docId);

		while (page.hasNext()) {
			DocumentRecord rec = page.next();
			docMgr.readMetadata(rec.getUri(), mh);
			System.out.println(rec.getUri());
			validateMetadata(mh);
		}
		validateMetadata(mh);
	}

	/*
	 * 
	 * Use JacksonHandle to load json strings using bulk write set. Test Bulk
	 * Read to see you can read all the documents
	 */
	@Test
	public void testWriteMultipleJSONDocsWithDefaultMetadata2()
			throws Exception {
		// Synthesize input content
		String doc1 = new String("{\"animal\": \"cat\", \"says\": \"meow\"}");
		String doc2 = new String("{\"animal\": \"dog\", \"says\": \"bark\"}");
		String doc3 = new String(
				"{\"animal\": \"eagle\", \"says\": \"squeak\"}");
		String doc4 = new String("{\"animal\": \"lion\", \"says\": \"roar\"}");
		String doc5 = new String("{\"animal\": \"man\", \"says\": \"hello\"}");

		// Synthesize input metadata
		DocumentMetadataHandle defaultMetadata1 = new DocumentMetadataHandle()
				.withQuality(1);
		DocumentMetadataHandle defaultMetadata2 = new DocumentMetadataHandle()
				.withQuality(2);
		DocumentMetadataHandle docSpecificMetadata = new DocumentMetadataHandle()
				.withCollections("mySpecificCollection");

		// Create and build up the batch
		JSONDocumentManager jdm = client.newJSONDocumentManager();
		jdm.setMetadataCategories(Metadata.ALL);
		
		DocumentWriteSet batch = jdm.newWriteSet();
		
		JsonFactory f = new JsonFactory();

		JacksonParserHandle jacksonParserHandle1 = new JacksonParserHandle();
		JacksonParserHandle jacksonParserHandle2 = new JacksonParserHandle();
		JacksonParserHandle jacksonParserHandle3 = new JacksonParserHandle();
		JacksonParserHandle jacksonParserHandle4 = new JacksonParserHandle();
		JacksonParserHandle jacksonParserHandle5 = new JacksonParserHandle();
		
		jacksonParserHandle1.set(f.createJsonParser(doc1));
		jacksonParserHandle2.set(f.createJsonParser(doc2));
		jacksonParserHandle3.set(f.createJsonParser(doc3));
		jacksonParserHandle4.set(f.createJsonParser(doc4));
		jacksonParserHandle5.set(f.createJsonParser(doc5));

		// use system default metadata
		batch.add("doc1.json", jacksonParserHandle1);

		// using batch default metadata
		batch.addDefault(defaultMetadata1);
		batch.add("doc2.json", jacksonParserHandle2); // batch default metadata
		batch.add("doc3.json", docSpecificMetadata, jacksonParserHandle3);
		batch.add("doc4.json", jacksonParserHandle4); // batch default metadata

		// replace batch default metadata with new metadata
		batch.addDefault(defaultMetadata2);
		batch.add("doc5.json", jacksonParserHandle5); // batch default

		// Execute the write operation
		jdm.write(batch);
		DocumentPage page;
		DocumentRecord rec;
		// Check the results
		// Doc1 should have the system default quality of 0
		page = jdm.read("doc1.json");
		DocumentMetadataHandle mh = new DocumentMetadataHandle();
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		validateDefaultMetadata(mh);
		assertEquals("default quality", 0, mh.getQuality());

		// Doc2 should use the first batch default metadata, with quality 1
		page = jdm.read("doc2.json");
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		System.out.print(mh.getCollections().isEmpty());
		assertEquals("default quality", 1, mh.getQuality());
		assertTrue("default collections reset", mh.getCollections().isEmpty());

		// Doc3 should have the system default document quality (0) because
		// quality
		// was not included in the document-specific metadata. It should be in
		// the
		// collection "mySpecificCollection", from the document-specific
		// metadata.

		page = jdm.read("doc3.json");
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		assertEquals("default quality", 0, mh.getQuality());
		assertEquals("default collection must change",
				"[mySpecificCollection]", mh.getCollections().toString());

		DocumentMetadataHandle doc3Metadata = jdm.readMetadata("doc3.json",
				new DocumentMetadataHandle());
		System.out.println("doc3 quality: Expected=0, Actual="
				+ doc3Metadata.getPermissions());
		System.out.print("doc3 collections: Expected: myCollection, Actual=");
		for (String collection : doc3Metadata.getCollections()) {
			System.out.print(collection + " ");
		}
		System.out.println();

		// Doc 4 should also use the 1st batch default metadata, with quality 1
		page = jdm.read("doc4.json");
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		assertEquals("default quality", 1, mh.getQuality());
		assertTrue("default collections reset", mh.getCollections().isEmpty());
		// Doc5 should use the 2nd batch default metadata, with quality 2
		page = jdm.read("doc5.json");
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		assertEquals("default quality", 2, mh.getQuality());

	}

	@AfterClass
	public static void tearDown() throws Exception {
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

	public void validateRecord(DocumentRecord record, Format type) {

		assertNotNull("DocumentRecord should never be null", record);
		assertNotNull("Document uri should never be null", record.getUri());
		assertTrue("Document uri should start with " + DIRECTORY, record
				.getUri().startsWith(DIRECTORY));
		assertEquals("All records are expected to be in same format", type,
				record.getFormat());

	}
}