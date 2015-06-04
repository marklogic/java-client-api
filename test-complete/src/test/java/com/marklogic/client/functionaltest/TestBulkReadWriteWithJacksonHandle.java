/*
 * Copyright 2014-2015 MarkLogic Corporation
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;

/*
 * This test is designed to to test all of bulk reads and write of JSON  with JacksonHandle Manager by passing set of uris
 * and also by descriptors.
 */

public class TestBulkReadWriteWithJacksonHandle extends BasicJavaClientREST  {

	private static final String DIRECTORY ="/bulkread/";
	private static String dbName = "TestBulkReadWriteWithJacksonHandleDB";
	private static String [] fNames = {"TestBulkReadWriteWithJacksonHandleDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;
	@BeforeClass
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		setupAppServicesConstraint(dbName);	  

	}

	@Before  public void testSetup() throws Exception
	{
		// create new connection for each test below
		client = DatabaseClientFactory.newClient("localhost", restPort, "rest-admin", "x", Authentication.DIGEST);
	}
	@After
	public  void testCleanUp() throws Exception
	{
		System.out.println("Running CleanUp script");	
		// release client
		client.release();   	
	}

	public DocumentMetadataHandle setMetadata(){
		// create and initialize a handle on the meta-data
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

	public void validateMetadata(DocumentMetadataHandle mh){
		// get meta-data values
		DocumentProperties properties = mh.getProperties();
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();

		// Properties
		// String expectedProperties = "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
		String actualProperties = getDocumentPropertiesString(properties);
		boolean result = actualProperties.contains("size:5|");
		assertTrue("Document properties count", result);

		// Permissions
		String actualPermissions = getDocumentPermissionsString(permissions);
		System.out.println(actualPermissions);

		assertTrue("Document permissions difference in size value", actualPermissions.contains("size:3"));
		//assertTrue("Document permissions difference in flexrep-eval permission", actualPermissions.contains("flexrep-eval:[READ]"));
		assertTrue("Document permissions difference in rest-reader permission", actualPermissions.contains("rest-reader:[READ]"));
		assertTrue("Document permissions difference in rest-writer permission", actualPermissions.contains("rest-writer:[UPDATE]"));
		assertTrue("Document permissions difference in app-user permissions", 
				(actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));

		// Collections 
		String actualCollections = getDocumentCollectionsString(collections);	
		System.out.println(collections);

		assertTrue("Document collections difference in size value", actualCollections.contains("size:2"));
		assertTrue("my-collection1 not found", actualCollections.contains("my-collection1"));
		assertTrue("my-collection2 not found", actualCollections.contains("my-collection2"));	   		   
	}

	public void validateDefaultMetadata(DocumentMetadataHandle mh){
		// get meta-data values
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
	public void testWriteMultipleJSONDocs() throws Exception 
	{
		String docId[] = { "/a.json", "/b.json", "/c.json" };
		String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
		String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
		String json3 = new String("{\"animal\":\"rat\", \"says\":\"keek\"}");

		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		
		DocumentWriteSet writeset = docMgr.newWriteSet();
		ObjectMapper mapper = new ObjectMapper();

		JacksonHandle jacksonHandle1 = new JacksonHandle();
		JacksonHandle jacksonHandle2 = new JacksonHandle();
		JacksonHandle jacksonHandle3 = new JacksonHandle();

		JsonNode dogNode = mapper.readTree(json1);
		jacksonHandle1.set(dogNode);
		jacksonHandle1.withFormat(Format.JSON);

		JsonNode catNode = mapper.readTree(json2);
		jacksonHandle2.set(catNode);
		jacksonHandle2.withFormat(Format.JSON);

		JsonNode ratNode = mapper.readTree(json3);
		jacksonHandle3.set(ratNode);
		jacksonHandle3.withFormat(Format.JSON);

		writeset.add(docId[0], jacksonHandle1);
		writeset.add(docId[1], jacksonHandle2);
		writeset.add(docId[2], jacksonHandle3);

		docMgr.write(writeset);

		JacksonHandle jacksonhandle = new JacksonHandle();
		docMgr.read(docId[0], jacksonhandle);
		JSONAssert.assertEquals(json1, jacksonhandle.toString(), true);
		
		docMgr.read(docId[1],jacksonhandle);
		JSONAssert.assertEquals(json2, jacksonhandle.toString(), true);
		
		docMgr.read(docId[2], jacksonhandle);
		JSONAssert.assertEquals(json3, jacksonhandle.toString(), true);
	}

	/*
	 * 
	 * Use JacksonHandle to load JSON strings using bulk write set.
	 * Test Bulk Read to see you can read the document specific meta-data.
	 */

	@Test
	public void testWriteMultipleJSONDocsWithDefaultMetadata() throws Exception  
	{
		String docId[] = {"/a.json","/b.json","/c.json"};
		String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
		String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
		String json3 =  new String("{\"animal\":\"rat\", \"says\":\"keek\"}");

		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentWriteSet writeset = docMgr.newWriteSet();
		// put meta-data
		DocumentMetadataHandle mh = setMetadata();

		ObjectMapper mapper = new ObjectMapper();

		JacksonHandle jacksonHandle1 = new JacksonHandle();
		JacksonHandle jacksonHandle2 = new JacksonHandle();
		JacksonHandle jacksonHandle3 = new JacksonHandle();

		JsonNode dogNode = mapper.readTree(json1);
		jacksonHandle1.set(dogNode);
		jacksonHandle1.withFormat(Format.JSON);

		JsonNode catNode = mapper.readTree(json2);
		jacksonHandle2.set(catNode);
		jacksonHandle2.withFormat(Format.JSON);

		JsonNode ratNode = mapper.readTree(json3);
		jacksonHandle3.set(ratNode);
		jacksonHandle3.withFormat(Format.JSON);

		writeset.addDefault(mh);
		writeset.add(docId[0], jacksonHandle1);
		writeset.add(docId[1], jacksonHandle2);
		writeset.add(docId[2], jacksonHandle3);

		docMgr.write(writeset);

		DocumentPage page = docMgr.read(docId);
		// Issue #294 DocumentPage.size() should return correct size
		assertTrue("DocumentPage Size did not return expected value:: returned==  "+page.size(), page.size() == 3 );
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			docMgr.readMetadata(rec.getUri(), mh);
			System.out.println(rec.getUri());
			validateMetadata(mh);
		}
		validateMetadata(mh);
	}

	/*
	 * 
	 * Use JacksonHandle to load JSON strings using bulk write set.
	 * Test Bulk Read to see you can read all the documents
	 */
	@Test
	public void testWriteMultipleJSONDocsWithDefaultMetadata2() throws Exception  
	{
		// Synthesize input content
		String doc1 = new String("{\"animal\": \"cat\", \"says\": \"meow\"}");
		String doc2 = new String("{\"animal\": \"dog\", \"says\": \"bark\"}");
		String doc3 = new String("{\"animal\": \"eagle\", \"says\": \"squeak\"}");
		String doc4 = new String("{\"animal\": \"lion\", \"says\": \"roar\"}");
		String doc5 = new String("{\"animal\": \"man\", \"says\": \"hello\"}");	

		// Synthesize input meta-data
		DocumentMetadataHandle defaultMetadata1 = 
				new DocumentMetadataHandle().withQuality(1);
		DocumentMetadataHandle defaultMetadata2 = 
				new DocumentMetadataHandle().withQuality(2);
		DocumentMetadataHandle docSpecificMetadata = 
				new DocumentMetadataHandle().withCollections("mySpecificCollection");

		// Create and build up the batch
		JSONDocumentManager jdm = client.newJSONDocumentManager();
		jdm.setMetadataCategories(Metadata.ALL);
		DocumentWriteSet batch = jdm.newWriteSet();

		ObjectMapper mapper = new ObjectMapper();

		JacksonHandle jacksonHandle1 = new JacksonHandle();

		JsonNode catNode = mapper.readTree(doc1);
		jacksonHandle1.set(catNode);
		jacksonHandle1.withFormat(Format.JSON);

		JacksonHandle jacksonHandle2 = new JacksonHandle();

		JsonNode dogNode = mapper.readTree(doc2);
		jacksonHandle2.set(dogNode);
		jacksonHandle2.withFormat(Format.JSON);

		JacksonHandle jacksonHandle3 = new JacksonHandle();

		JsonNode eagleNode = mapper.readTree(doc3);
		jacksonHandle3.set(eagleNode);
		jacksonHandle3.withFormat(Format.JSON);

		JacksonHandle jacksonHandle4 = new JacksonHandle();

		JsonNode lionNode = mapper.readTree(doc4);
		jacksonHandle4.set(lionNode);
		jacksonHandle4.withFormat(Format.JSON);

		JacksonHandle jacksonHandle5 = new JacksonHandle();

		JsonNode manNode = mapper.readTree(doc5);
		jacksonHandle5.set(manNode);
		jacksonHandle5.withFormat(Format.JSON);

		// use system default meta-data
		batch.add("doc1.json", jacksonHandle1);

		// using batch default meta-data
		batch.addDefault(defaultMetadata1);  
		batch.add("doc2.json", jacksonHandle2);       // batch default meta-data
		batch.add("doc3.json", docSpecificMetadata, jacksonHandle3);
		batch.add("doc4.json", jacksonHandle4);       // batch default meta-data

		// replace batch default meta-data with new meta-data
		batch.addDefault(defaultMetadata2); 
		batch.add("doc5.json", jacksonHandle5);       // batch default 

		// Execute the write operation
		jdm.write(batch);
		DocumentPage page ;
		DocumentRecord rec;
		// Check the results
		// Doc1 should have the system default quality of 0
		page = jdm.read("doc1.json");
		DocumentMetadataHandle mh = new DocumentMetadataHandle();
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		validateDefaultMetadata(mh);
		assertEquals("default quality",0,mh.getQuality());

		// Doc2 should use the first batch default meta-data, with quality 1
		page = jdm.read("doc2.json");
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		System.out.print(mh.getCollections().isEmpty());
		assertEquals("default quality",1,mh.getQuality());
		assertTrue("default collections reset",mh.getCollections().isEmpty());

		// Doc3 should have the system default document quality (0) because quality
		// was not included in the document-specific meta-data. It should be in the
		// collection "mySpecificCollection", from the document-specific meta-data.

		page = jdm.read("doc3.json");
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		assertEquals("default quality",0,mh.getQuality());
		assertEquals("default collection must change","[mySpecificCollection]",mh.getCollections().toString());

		DocumentMetadataHandle doc3Metadata =  
				jdm.readMetadata("doc3.json", new DocumentMetadataHandle());
		System.out.println("doc3 quality: Expected=0, Actual=" + doc3Metadata.getPermissions());
		System.out.print("doc3 collections: Expected: myCollection, Actual=");
		for (String collection : doc3Metadata.getCollections()) {
			System.out.print(collection + " ");
		}
		System.out.println();

		// Doc 4 should also use the 1st batch default meta-data, with quality 1
		page = jdm.read("doc4.json");
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		assertEquals("default quality",1,mh.getQuality());
		assertTrue("default collections reset",mh.getCollections().isEmpty());
		// Doc5 should use the 2nd batch default meta-data, with quality 2
		page = jdm.read("doc5.json");
		rec = page.next();
		jdm.readMetadata(rec.getUri(), mh);
		assertEquals("default quality",2,mh.getQuality());

	}
	
	/*
	 * Purpose : To read JSON files from file system and write into DB. 
	 * Use JacksonHandle to load JSON files using bulk write set.
	 * Test Bulk Read to see you can read the document specific meta-data.
	 */

	@Test
	public void testWriteMultiJSONFilesDefaultMetadata() throws Exception  
	{
		String docId[] = {"/original.json","/updated.json","/constraint1.json"};
		String jsonFilename1 = "json-original.json";
		String jsonFilename2 = "json-updated.json";
		String jsonFilename3 = "constraint1.json";
				
		File jsonFile1 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + jsonFilename1);
		File jsonFile2 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + jsonFilename2);
		File jsonFile3 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + jsonFilename3);

		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentWriteSet writeset = docMgr.newWriteSet();
		// put meta-data
		DocumentMetadataHandle mh = setMetadata();
		DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

		ObjectMapper mapper = new ObjectMapper();

		JacksonHandle jacksonHandle1 = new JacksonHandle();
		JacksonHandle jacksonHandle2 = new JacksonHandle();
		JacksonHandle jacksonHandle3 = new JacksonHandle();

		JsonNode originalNode = mapper.readTree(jsonFile1);
		jacksonHandle1.set(originalNode);
		jacksonHandle1.withFormat(Format.JSON);

		JsonNode updatedNode = mapper.readTree(jsonFile2);
		jacksonHandle2.set(updatedNode);
		jacksonHandle2.withFormat(Format.JSON);

		JsonNode constraintNode = mapper.readTree(jsonFile3);
		jacksonHandle3.set(constraintNode);
		jacksonHandle3.withFormat(Format.JSON);

		writeset.addDefault(mh);
		writeset.add(docId[0], jacksonHandle1);
		writeset.add(docId[1], jacksonHandle2);
		writeset.add(docId[2], jacksonHandle3);

		docMgr.write(writeset);

		DocumentPage page = docMgr.read(docId);

		while(page.hasNext()){
			DocumentRecord rec = page.next();
			docMgr.readMetadata(rec.getUri(), mhRead);
			System.out.println(rec.getUri());
			validateMetadata(mhRead);
		}
		validateMetadata(mhRead);
		mhRead = null;
	}

	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

	public void validateRecord(DocumentRecord record,Format type) {

		assertNotNull("DocumentRecord should never be null", record);
		assertNotNull("Document uri should never be null", record.getUri());
		assertTrue("Document uri should start with " + DIRECTORY, record.getUri().startsWith(DIRECTORY));
		assertEquals("All records are expected to be in same format", type, record.getFormat());
	}
}