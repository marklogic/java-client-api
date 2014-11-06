package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.Transaction;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.Format;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder.TemporalOperator;

public class TestBiTemporal extends BasicJavaClientREST{

	private static String dbName = "TestBiTemporalJava";
	private static String [] fNames = {"TestBiTemporalJava-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;

	private final static String dateTimeDataTypeString = "dateTime";
	
	private final static String systemStartERIName = "javaSystemStartERI";
	private final static String systemEndERIName = "javaSystemEndERI";
	private final static String validStartERIName = "javaValidStartERI";
	private final static String validEndERIName = "javaValidEndERI";
	
	private final static String axisSystemName = "javaERISystemAxis";
	private final static String axisValidName = "javaERIValidAxis";
	
	private final static String temporalCollectionName = "javaERITemporalCollection";
	private final static String temporalLsqtCollectionName = "javaERILsqtTemporalCollection";
	
	private final static String systemNodeName = "System";
	private final static String validNodeName = "Valid";
	private final static String addressNodeName = "Address";
	private final static String uriNodeName = "uri";

	private final static String latestCollectionName = "latest";
	private final static String updateCollectionName = "updateCollection";
	private final static String insertCollectionName = "insertCollection";
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		
		ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "", systemStartERIName);
		ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "", systemEndERIName);
		ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "", validStartERIName);
		ConnectedRESTQA.addRangeElementIndex(dbName, dateTimeDataTypeString, "", validEndERIName);
		
		// Temporal axis must be created before temporal collection associated with those axes is created
		ConnectedRESTQA.addElementRangeIndexTemporalAxis(dbName, axisSystemName, "", systemStartERIName, "", systemEndERIName);
		ConnectedRESTQA.addElementRangeIndexTemporalAxis(dbName, axisValidName, "", validStartERIName, "", validEndERIName);
		ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName, temporalCollectionName, axisSystemName, axisValidName);
		ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName, temporalLsqtCollectionName, axisSystemName, axisValidName);
		ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName, temporalLsqtCollectionName, true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );

		// Delete database first. Otherwise axis and collection cannot be deleted 
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
		// Temporal collection needs to be delete before temporal axis associated with it can be deleted
		ConnectedRESTQA.deleteElementRangeIndexTemporalCollection("Documents", temporalLsqtCollectionName);
		ConnectedRESTQA.deleteElementRangeIndexTemporalCollection("Documents", temporalCollectionName);
		ConnectedRESTQA.deleteElementRangeIndexTemporalAxis("Documents", axisValidName);
		ConnectedRESTQA.deleteElementRangeIndexTemporalAxis("Documents", axisSystemName);

	}

	@Before
	public void setUp() throws Exception {
		client = DatabaseClientFactory.newClient("localhost", restPort, "rest-admin", "x", Authentication.DIGEST);
	}

	@After
	public void tearDown() throws Exception {
		clearDB(restPort);
		client.release();
	}

	public DocumentMetadataHandle setMetadata(boolean update) {
		// create and initialize a handle on the meta-data
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		
		if (update) {
		  metadataHandle.getCollections().addAll("updateCollection");
			metadataHandle.getProperties().put("published", true);

			metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
					Capability.READ);

			metadataHandle.setQuality(99);
		}
		else {
		  metadataHandle.getCollections().addAll("insertCollection");
			metadataHandle.getProperties().put("reviewed", true);

			metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
					Capability.READ, Capability.EXECUTE);

			metadataHandle.setQuality(11);
		}
		
		// metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
		//		Capability.READ);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar",
				Calendar.getInstance().get(Calendar.YEAR));
		
		return metadataHandle;
	}

	public void validateMetadata(DocumentMetadataHandle mh) {
		// get metadata values
		DocumentProperties properties = mh.getProperties();
		DocumentPermissions permissions = mh.getPermissions();
		DocumentCollections collections = mh.getCollections();
		int quality = mh.getQuality();

		// Properties
		// String expectedProperties =
		// "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
		String actualProperties = getDocumentPropertiesString(properties);
		boolean result = actualProperties.contains("size:5|");
		assertTrue("Document properties count", result);

		// Permissions
		String actualPermissions = getDocumentPermissionsString(permissions);
		System.out.println(actualPermissions);

		/***
		assertTrue("Document permissions difference in size value",
				actualPermissions.contains("size:3"));
		
		assertTrue("Document permissions difference in rest-reader permission",
				actualPermissions.contains("rest-reader:[READ]"));
		assertTrue("Document permissions difference in rest-writer permission",
				actualPermissions.contains("rest-writer:[UPDATE]"));
		assertTrue(
				"Document permissions difference in app-user permissions",
				(actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions
						.contains("app-user:[READ, UPDATE]")));
    ***/
		
		/***
		// Collections
		String actualCollections = getDocumentCollectionsString(collections);
		System.out.println(collections);

		assertTrue("Document collections difference in size value",
				actualCollections.contains("size:2"));
		assertTrue("my-collection1 not found",
				actualCollections.contains("my-collection1"));
		assertTrue("my-collection2 not found",
				actualCollections.contains("my-collection2"));
		***/
	}

	public void validateDefaultMetadata(DocumentMetadataHandle mh) {
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
		
		System.out.println("Permissions: " + actualPermissions);

		assertTrue("Document permissions difference in size value", actualPermissions.contains("size:2"));
		//assertTrue("Document permissions difference in flexrep-eval permission", actualPermissions.contains("flexrep-eval:[READ]"));
		assertTrue("Document permissions difference in rest-reader permission", actualPermissions.contains("rest-reader:[READ]"));
		assertTrue("Document permissions difference in rest-writer permission", actualPermissions.contains("rest-writer:[UPDATE]"));

		/***
		// Collections 
		String expectedCollections = "size:0|";
		String actualCollections = getDocumentCollectionsString(collections);

		assertEquals("Document collections difference", expectedCollections, actualCollections);
		***/
	}
	
	// This covers passing transforms and descriptor
	private void insertXMLSingleDocument(String temporalCollection,
			String docId, String transformName) throws Exception { 
		System.out.println("Inside insertXMLSingleDocument");

		DOMHandle handle = getXMLDocumentHandle(
				"2001-01-01T00:00:00", "2011-12-31T23:59:59", "999 Skyway Park - XML", docId);
				
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);

		// put meta-data
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
		DocumentMetadataHandle mh = setMetadata(false);
		
		if (transformName != null) {
			TransformExtensionsManager transMgr = 
					client.newServerConfigManager().newTransformExtensionsManager();
			ExtensionMetadata metadata = new ExtensionMetadata();
			metadata.setTitle("Adding Element xquery Transform");
			metadata.setDescription("This plugin transforms an XML document by adding Element to root node");
			metadata.setProvider("MarkLogic");
			metadata.setVersion("0.1");
			// get the transform file
			File transformFile = new File("src/test/java/com/marklogic/javaclient/transforms/" + transformName + ".xqy");
			FileHandle transformHandle = new FileHandle(transformFile);
			transMgr.writeXQueryTransform(transformName, transformHandle, metadata);
			ServerTransform transformer = new ServerTransform(transformName);
			transformer.put("name", "Lang");
			transformer.put("value", "English");

  		docMgr.write(desc, mh, handle, transformer, null, temporalCollection);
		}
		else {
      docMgr.write(desc, mh, handle, null, null, temporalCollection);
  		
      // docMgr.write(docId, mh, handle, null, null, temporalCollectionName, DatatypeConverter.parseDateTime("2004-06-015T00:00:01"));
		}
	}

	// This covers passing transforms and descriptor
	private void updateXMLSingleDocument(String temporalCollection,
			String docId, String transformName) throws Exception { 
		System.out.println("Inside updateXMLSingleDocument");

		// Update the document
		DOMHandle handle = getXMLDocumentHandle(
				"2003-01-01T00:00:00", "2008-12-31T23:59:59", "1999 Skyway Park - Updated - XML", docId);

		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
		DocumentMetadataHandle mh = setMetadata(true);  // cannot set properties during update

		if (transformName != null) {
			TransformExtensionsManager transMgr = 
					client.newServerConfigManager().newTransformExtensionsManager();
			ExtensionMetadata metadata = new ExtensionMetadata();
			metadata.setTitle("Adding Element xquery Transform");
			metadata.setDescription("This plugin transforms an XML document by adding Element to root node");
			metadata.setProvider("MarkLogic");
			metadata.setVersion("0.1");
			// get the transform file
			File transformFile = new File("src/test/java/com/marklogic/javaclient/transforms/" + transformName + ".xqy");
			FileHandle transformHandle = new FileHandle(transformFile);
			transMgr.writeXQueryTransform(transformName, transformHandle, metadata);
			ServerTransform transformer = new ServerTransform(transformName);
			transformer.put("name", "Lang");
			transformer.put("value", "English");

  		docMgr.write(desc, mh, handle, transformer, null, temporalCollection);
		}
		else {
		  docMgr.write(desc, mh, handle, null, null, temporalCollection);
		}
  }

	// This covers passing descriptor
	public void deleteXMLSingleDocument(String temporalCollection, String docId) throws Exception { 

		System.out.println("Inside deleteXMLSingleDocument");		
				
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // docMgr.delete(docId, null, temporalCollectionName, DatatypeConverter.parseDateTime("2014-06-015T00:00:01"));
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
    docMgr.delete(desc, null, temporalCollection, null);    
	}
	
	private DOMHandle getXMLDocumentHandle(
			String startValidTime, String endValidTime, String address, String uri) throws Exception {

		Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = domDocument.createElement("root");
		
		// System start and End time
		Node systemNode = root.appendChild(domDocument.createElement("system"));
		systemNode.appendChild(domDocument.createElement(systemStartERIName));
		systemNode.appendChild(domDocument.createElement(systemEndERIName));

		// Valid start and End time
		Node validNode = root.appendChild(domDocument.createElement("valid"));
		
		Node validStartNode = validNode.appendChild(domDocument.createElement(validStartERIName));
		validStartNode.appendChild(domDocument.createTextNode(startValidTime));
		validNode.appendChild(validStartNode);

		Node validEndNode = validNode.appendChild(domDocument.createElement(validEndERIName));
		validEndNode.appendChild(domDocument.createTextNode(endValidTime));
		validNode.appendChild(validEndNode);
		
		// Address
		Node addressNode = root.appendChild(domDocument.createElement("Address"));
		addressNode.appendChild(domDocument.createTextNode(address));

		// uri
		Node uriNode = root.appendChild(domDocument.createElement("uri"));
		uriNode.appendChild(domDocument.createTextNode(uri));
		domDocument.appendChild(root);

		String domString = ((DOMImplementationLS) DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.getDOMImplementation()
				).createLSSerializer().writeToString(domDocument);

		System.out.println(domString);

		DOMHandle handle = new DOMHandle().with(domDocument);
		
		return handle;
	}

	public void insertJSONSingleDocument(String temporalCollection,
			String docId, Transaction transaction, java.util.Calendar systemTime) throws Exception { 

		insertJSONSingleDocument(temporalCollection, docId, null, transaction, systemTime);
	}

	public void insertJSONSingleDocument(String temporalCollection,
			String docId, String transformName) throws Exception { 

		insertJSONSingleDocument(temporalCollection, docId, transformName, null, null);
	}
	
	public void insertJSONSingleDocument(String temporalCollection,
			String docId, String transformName,
			Transaction transaction, java.util.Calendar systemTime) throws Exception { 

		System.out.println("Inside insertJSONSingleDocument");
		
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle(
				"2001-01-01T00:00:00", "2011-12-31T23:59:59", "999 Skyway Park - JSON", docId);
				
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);

		// put meta-data
		DocumentMetadataHandle mh = setMetadata(false);
		
		if (transformName != null) {
			TransformExtensionsManager transMgr = 
					client.newServerConfigManager().newTransformExtensionsManager();
			ExtensionMetadata metadata = new ExtensionMetadata();
			metadata.setTitle("Adding sjs Transform");
			metadata.setDescription("This plugin adds 2 properties to JSON document");
			metadata.setProvider("MarkLogic");
			metadata.setVersion("0.1");
			// get the transform file
			File transformFile = new File("src/test/java/com/marklogic/javaclient/transforms/" + transformName + ".js");
			FileHandle transformHandle = new FileHandle(transformFile);
			transMgr.writeJavascriptTransform(transformName, transformHandle, metadata);
			ServerTransform transformer = new ServerTransform(transformName);
			transformer.put("name", "Lang");
			transformer.put("value", "English");

			if (systemTime != null) {
  		  docMgr.write(docId, mh, handle, transformer, null, temporalCollection, systemTime, null);
			}
			else {
  		  docMgr.write(docId, mh, handle, transformer, null, temporalCollection);
			}
		}
		else {
			if (systemTime != null) {
			  docMgr.write(docId, mh, handle, null, transaction, temporalCollection, systemTime, null);
			}
			else {
			  docMgr.write(docId, mh, handle, null, transaction, temporalCollection);
			}
		}
	}

	public void updateJSONSingleDocument(String temporalCollection,
			String docId) throws Exception { 

		updateJSONSingleDocument(temporalCollection, docId, null, null);
	}
	
	public void updateJSONSingleDocument(String temporalCollection,
			String docId, Transaction transaction, java.util.Calendar systemTime) throws Exception { 

		System.out.println("Inside updateJSONSingleDocumentString");
		
		// Update the temporal document
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle(
				"2003-01-01T00:00:00", "2008-12-31T23:59:59", "1999 Skyway Park - Updated - JSON", docId);

		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);
		DocumentMetadataHandle mh = setMetadata(true);  // cannot set properties during update
		docMgr.write(docId, mh, handle, null, transaction, temporalCollection, systemTime, null);
	}

	public void deleteJSONSingleDocument(String temporalCollection,
			String docId, Transaction transaction) throws Exception { 
		deleteJSONSingleDocument(temporalCollection, docId, transaction, null);
	}

	public void deleteJSONSingleDocument(String temporalCollection,
			String docId, Transaction transaction, java.util.Calendar systemTime) throws Exception { 

		System.out.println("Inside deleteJSONSingleDocument");		
				
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		// Doing the logic here to exercise the overloaded methods 
		if (systemTime != null) {
      docMgr.delete(docId, transaction, temporalCollection, systemTime);
		}
		else {
      docMgr.delete(docId, transaction, temporalCollection);
		}
	}



	private JacksonDatabindHandle<ObjectNode> getJSONDocumentHandle(
			String startValidTime, String endValidTime, String address, String uri) throws Exception {

		// Setup for JSON document
		/**
		 * 
		 {
		 		"System": {
		 			systemStartERIName : "",
		 			systemEndERIName : "",
		 		},
		 		"Valid": {
		 			validStartERIName: "2001-01-01T00:00:00",
		 			validEndERIName: "2011-12-31T23:59:59"
		 		},
		 		"Address": "999 Skyway Park",
		 		"uri": "javaSingleDoc1.json"
		 }
		 */
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		// Set system time values
		ObjectNode system = mapper.createObjectNode();
		
		system.put(systemStartERIName, "");
		system.put(systemEndERIName, "");
		rootNode.set(systemNodeName, system);

		// Set valid time values
		ObjectNode valid = mapper.createObjectNode();
		
		valid.put(validStartERIName, startValidTime);
		valid.put(validEndERIName, endValidTime);
		rootNode.set(validNodeName, valid);
		
		// Set Address
		rootNode.put(addressNodeName, address);

		// Set uri
		rootNode.put(uriNodeName, uri);
		
		System.out.println(rootNode.toString());

		JacksonDatabindHandle<ObjectNode> handle = new JacksonDatabindHandle<ObjectNode>(ObjectNode.class).withFormat(Format.JSON);		
		handle.set(rootNode);
		
		return handle;
	}
	
	@Test
	public void testInsertXMLSingleDocumentUsingTemplate() throws Exception { 
		System.out.println("Inside testInsertXMLSingleDocumentUsingTemplate");

		String docId = "javaSingleXMLDoc.xml";
		DOMHandle handle = getXMLDocumentHandle(
				"2001-01-01T00:00:00", "2011-12-31T23:59:59", "777 Skyway Park - XML", docId);
				
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);

		// Create document using using document template
    String dirName = "/java/bitemporal/";
    String fileSuffix = "xml";
    DocumentUriTemplate template = docMgr.newDocumentUriTemplate(fileSuffix);
    template.setDirectory(dirName);
		DocumentMetadataHandle mh = setMetadata(false);
		
    docMgr.create(template, mh, handle, null, null, temporalCollectionName);

		// Make sure there are no documents associated with "latest" collection
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		
		String[] collections = {latestCollectionName, "insertCollection"};
		StructuredQueryDefinition termQuery = sqb.collection(collections);
		
		long start = 1;
		docMgr = client.newXMLDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);	// Get all metadata
		DocumentPage termQueryResults = docMgr.search(termQuery, start);
		

		long count = 0;
    while (termQueryResults.hasNext()) {
    	++count;
    	DocumentRecord record = termQueryResults.next();
    	
    	String uri = record.getUri();
  		System.out.println("URI = " + uri);
  		
  		if (!uri.contains(dirName) && !uri.contains(fileSuffix)) {
  			assertFalse("Uri name does not have the right prefix or suffix", true);
  		}  		
    }
		
		System.out.println("Number of results = " + count);
		assertEquals("Wrong number of results", 1, count);
    
    System.out.println("Done");
	}
	
	@Test
	public void testInsertAndUpdateXMLSingleDocumentUsingInvalidTransform() throws Exception { 

		System.out.println("Inside testXMLWriteSingleDocument");
		String docId = "javaSingleXMLDoc.xml";
		
		insertXMLSingleDocument(temporalCollectionName, docId, null);
		boolean exceptionThrown = false;
		try {
		  updateXMLSingleDocument(temporalCollectionName, docId, "add-element-xquery-invalid-bitemp-transform");
		}
		catch (com.marklogic.client.FailedRequestException ex) {
			exceptionThrown = true;		
			System.out.println(ex.getFailedRequest().getStatusCode());
			System.out.println(ex.getFailedRequest().getMessageCode());
			
			// BUG: Right now this returns 500 error. Bug is open to fix this
			// assert(ex.getFailedRequest().getMessageCode().equals("TEMPORAL-SYSTEMTIME-BACKWARDS"));
			// assert(ex.getFailedRequest().getStatusCode() == 400);
		}
		
		assertTrue("Exception not thrown for invalid transform", exceptionThrown);		
	}

	
	@Test
	// This test validates the following - 
	//   1. Inserts, updates and delete and and also makes sure number of documents in
	//      doc uri collection, latest collection are accurate after those operations. Do this
	//      for more than one document URI (we do this with JSON and XML)
	//   2. Make sure things are correct with transforms
	public void testConsolidated() throws Exception { 

		System.out.println("Inside testXMLConsolidated");
		String xmlDocId = "javaSingleXMLDoc.xml";

		//=============================================================================
		// Check insert works
		//=============================================================================		
		// Insert XML document
		insertXMLSingleDocument(temporalCollectionName, xmlDocId, "add-element-xquery-transform");  // Transforming during insert
		
    // Verify that the document was inserted 
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		DocumentPage readResults = xmlDocMgr.read(xmlDocId); 
		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 1, readResults.size());
		
		// Now insert a JSON document
		String jsonDocId = "javaSingleJSONDoc.json";
		insertJSONSingleDocument(temporalCollectionName, jsonDocId, null);
		
    // Verify that the document was inserted 
		JSONDocumentManager jsonDocMgr = client.newJSONDocumentManager();
		readResults = jsonDocMgr.read(jsonDocId); 
		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 1, readResults.size());

		//=============================================================================
		// Check update works
		//=============================================================================
		// Update XML document
		updateXMLSingleDocument(temporalCollectionName, xmlDocId, null);   // Not transforming during update

		// Make sure there are 2 documents in latest collection
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr. newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery = sqb.collection(latestCollectionName);

		long start = 1;
		DocumentPage termQueryResults = xmlDocMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 2, termQueryResults.getTotalSize());

		// Make sure there are 4 documents in xmlDocId collection with term XML
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.and(
				sqb.and(sqb.term("XML"), sqb.collection(xmlDocId)));

		start = 1;
		termQueryResults = xmlDocMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure transform on insert worked
		 while (termQueryResults.hasNext()) {
	    	DocumentRecord record = termQueryResults.next();
	  		System.out.println("URI = " +record.getUri());
	  		
	  		if (record.getFormat() != Format.XML) {
	  			assertFalse("Format is not JSON: " +  Format.JSON, true);	
	  		}
	  		else {

	  			DOMHandle recordHandle = new DOMHandle(); 				
	  			record.getContent(recordHandle);
		  	  
		  	  String content = recordHandle.toString();
		  	  System.out.println("Content = " + content);
		  	  
		  	  // Check if transform worked. We did transform only with XML document
		  	  if ((content.contains("2001-01-01T00:00:00") && content.contains("2011-12-31T23:59:59") && record.getFormat() != Format.XML)
		  	  		&& (!content.contains("new-element") || !content.contains("2007-12-31T23:59:59"))) {
		  	  	assertFalse("Transform did not work", true);
		  	  }
		  	  else {
		  	  	System.out.println("Transform Worked!");
		  	  }
	  		}
	    }
		 
			// Update JSON document
			updateJSONSingleDocument(temporalCollectionName, jsonDocId);
	
			// Make sure there are still 2 documents in latest collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(latestCollectionName);
	
			start = 1;
			termQueryResults = xmlDocMgr.search(termQuery, start);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
			assertEquals("Wrong number of results", 2, termQueryResults.getTotalSize());
	
			// Docu URIs in latest collection must be the same as the one as the original document  
	    while (termQueryResults.hasNext()) {
	    	DocumentRecord record = termQueryResults.next();
	    	
	    	String uri = record.getUri();
	  		System.out.println("URI = " + uri);
	  		
	  		if (!uri.equals(xmlDocId) && !uri.equals(jsonDocId)) {
	  			assertFalse("URIs are not what is expected", true);
	  		}
	    }		
	
			// Make sure there are 4 documents in jsonDocId collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(jsonDocId);
	
			start = 1;
			termQueryResults = xmlDocMgr.search(termQuery, start);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
			assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
			
			// Make sure there are 8 documents in temporal collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(temporalCollectionName);
	
			start = 1;
			termQueryResults = xmlDocMgr.search(termQuery, start);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
			assertEquals("Wrong number of results", 8, termQueryResults.getTotalSize());
			
			// Make sure there are 8 documents in total. Use string search for this
			queryMgr = client.newQueryManager();
			StringQueryDefinition stringQD = queryMgr.newStringDefinition();
			stringQD.setCriteria("");
	
			start = 1;
			termQueryResults = xmlDocMgr.search(stringQD, start);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
			assertEquals("Wrong number of results", 8, termQueryResults.getTotalSize());
	
			//=============================================================================
			// Check delete works
			//=============================================================================
			// Delete one of the document
			deleteXMLSingleDocument(temporalCollectionName, xmlDocId);
	
			// Make sure there are still 4 documents in xmlDocId collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(xmlDocId);
	
			start = 1;
			termQueryResults = xmlDocMgr.search(termQuery, start);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
			assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
			
			// Make sure there is one document with xmlDocId uri
			XMLDocumentManager docMgr = client.newXMLDocumentManager();
			readResults = docMgr.read(xmlDocId); 
	
			System.out.println("Number of results = " + readResults.size());
			assertEquals("Wrong number of results", 1, readResults.size());
			
			// Make sure there is only 1 document in latest collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(latestCollectionName);
	
			start = 1;
			termQueryResults = xmlDocMgr.search(termQuery, start);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
			assertEquals("Wrong number of results", 1, termQueryResults.getTotalSize());
	
			// Docu URIs in latest collection must be the same as the one as the original document  
	    while (termQueryResults.hasNext()) {
	    	DocumentRecord record = termQueryResults.next();
	    	
	    	String uri = record.getUri();
	  		System.out.println("URI = " + uri);
	  		
	  		if (!uri.equals(jsonDocId)) {
	  			assertFalse("URIs are not what is expected", true);
	  		}
	    }    
	
			// Make sure there are 8 documents in temporal collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(temporalCollectionName);
	
			start = 1;
			termQueryResults = xmlDocMgr.search(termQuery, start);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
			assertEquals("Wrong number of results", 8, termQueryResults.getTotalSize());	
	}
	
	@Test
	public void testJSONConsolidated() throws Exception { 

		System.out.println("Inside testJSONConsolidated");
		
		String docId = "javaSingleJSONDoc.json";
		insertJSONSingleDocument(temporalCollectionName, docId, null);
		
    // Verify that the document was inserted 
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentPage readResults = docMgr.read(docId); 

		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 1, readResults.size());

		DocumentRecord latestDoc = readResults.next();
		System.out.println("URI after insert = " + latestDoc.getUri());
		assertEquals("Document uri wrong after insert", docId, latestDoc.getUri());
		

		// Check if properties have been set. User XML DOcument Manager since properties are written as XML
		JacksonDatabindHandle<ObjectNode> contentHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		docMgr.read (docId, metadataHandle, contentHandle);

		validateMetadata(metadataHandle);
		
		//================================================================
		// Update the document
		updateJSONSingleDocument(temporalCollectionName, docId);
		
    // Verify that the document was updated 
		// Make sure there is 1 document in latest collection
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery = sqb.collection(latestCollectionName);
		long start = 1;
		DocumentPage termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 1, termQueryResults.getTotalSize());

		// Docu URIs in latest collection must be the same as the one as the original document  
    while (termQueryResults.hasNext()) {
    	DocumentRecord record = termQueryResults.next();
    	
    	String uri = record.getUri();
  		System.out.println("URI = " + uri);
  		
  		if (!uri.equals(docId)) {
  			assertFalse("URIs are not what is expected", true);
  		}
    }		

		// Make sure there are 4 documents in jsonDocId collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(docId);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure there are 4 documents in temporal collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(temporalCollectionName);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure there are 4 documents in total. Use string search for this
		queryMgr = client.newQueryManager();
		StringQueryDefinition stringQD = queryMgr.newStringDefinition();
		stringQD.setCriteria("");

		start = 1;
		docMgr.setMetadataCategories(Metadata.ALL);
		termQueryResults = docMgr.search(stringQD, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
    while (termQueryResults.hasNext()) {
    	DocumentRecord record = termQueryResults.next();
  		System.out.println("URI = " + record.getUri());
  		
  		metadataHandle = new DocumentMetadataHandle();
  		record.getMetadata(metadataHandle);
  		Iterator<String> resCollections = metadataHandle.getCollections().iterator();
  		
  		if (record.getUri().equals(docId)) {
  			// Must belong to latest collection as well. So, count must be 4
  			assert(resCollections.equals(4));
  		}
  		else {
  			assert(resCollections.equals(3));
  		}
  		while (resCollections.hasNext()) {
  			String collection = resCollections.next();
  			System.out.println("Collection = " + collection);
  			
  			if (!collection.equals(docId) && !collection.equals(docId) && !collection.equals(latestCollectionName) &&
  					!collection.equals(updateCollectionName) && !collection.equals(insertCollectionName) && 
  					!collection.equals(temporalCollectionName)) {
  				assertFalse("Collection not what is expected: " + collection, true);
  			}
  			
  			if (collection.equals(latestCollectionName)) {
  				// If there is a latest collection, docId must match the URI
  				assert(record.getUri().equals(docId));
  			}
  		}
  			
  		if (record.getFormat() != Format.JSON) {
  			assertFalse("Format is not JSON: " +  Format.JSON, true);	
  		}
  		else {
	  		JacksonDatabindHandle<ObjectNode> recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  				
	  		record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString());
  		}
    }

		//=============================================================================
		// Check delete works
		//=============================================================================
		// Delete one of the document
		deleteJSONSingleDocument(temporalCollectionName, docId, null);
		
		// Make sure there are still 4 documents in docId collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(docId);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure there is one document with docId uri
		docMgr = client.newJSONDocumentManager();
		readResults = docMgr.read(docId); 

		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 1, readResults.size());
		
		// Make sure there are no documents in latest collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(latestCollectionName);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 0, termQueryResults.getTotalSize());

		// Make sure there are 4 documents in temporal collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(temporalCollectionName);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());	

		// Make sure there are 4 documents in total. Use string search for this
		queryMgr = client.newQueryManager();
		
		start = 1;
		termQueryResults = docMgr.search(stringQD, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
	}

	@Test
	// Test if system time can be set. We do this with insert, update and delete
	public void testSystemTime() throws Exception { 

		System.out.println("Inside testSystemTime");
		
		String docId = "javaSingleJSONDoc.json";
		
		Calendar firstInsertTime = DatatypeConverter.parseDateTime("2010-01-01T00:00:01");
		insertJSONSingleDocument(temporalLsqtCollectionName, docId, null, null, firstInsertTime);
		
    // Verify that the document was inserted 
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		JacksonDatabindHandle<ObjectNode> recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		docMgr.read (docId, metadataHandle, recordHandle);	
		DocumentPage readResults = docMgr.read(docId); 

		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 1, readResults.size());  // 2, because includes content and metadata
		
		DocumentRecord record = readResults.next();
		System.out.println("URI after insert = " + record.getUri());
		assertEquals("Document uri wrong after insert", docId, record.getUri());
	  System.out.println("Content = " + recordHandle.toString()); 

		// Make sure System start time was what was set ("2010-01-01T00:00:01")
		if (record.getFormat() != Format.JSON) {
			assertFalse("Invalid document format: " + record.getFormat(), true);		
		}
		else {
  	  JsonFactory factory = new JsonFactory(); 
      ObjectMapper mapper = new ObjectMapper(factory);
      TypeReference<HashMap<String,Object>> typeRef 
              = new TypeReference<HashMap<String,Object>>() {};

      HashMap<String,Object> docObject = mapper.readValue(recordHandle.toString(), typeRef); 
      
      @SuppressWarnings("unchecked")
      HashMap<String, Object> validNode = (HashMap<String, Object>)(docObject.get(systemNodeName));
      
      String systemStartDate = (String)validNode.get(systemStartERIName);
      String systemEndDate = (String)validNode.get(systemEndERIName);
      System.out.println("systemStartDate = " + systemStartDate);
      System.out.println("systemEndDate = " + systemEndDate);
      
      assertTrue("System start date check failed", (systemStartDate.contains("2010-01-01T00:00:01")));
      assertTrue("System end date check failed", (systemEndDate.contains("9999-12-31T23:59:59")));     

      // Validate collections
  		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
  		while (resCollections.hasNext()) {
  			String collection = resCollections.next();
  			System.out.println("Collection = " + collection);
  			
  			if (!collection.equals(docId) &&
  					!collection.equals(insertCollectionName) && 
  					!collection.equals(temporalLsqtCollectionName) &&
  					!collection.equals(latestCollectionName)) {
  				assertFalse("Collection not what is expected: " + collection, true);
  			}
  		}
  		    		
      // Validate permissions
  		DocumentPermissions permissions = metadataHandle.getPermissions();
  		System.out.println("Permissions: " + permissions);

  		String actualPermissions = getDocumentPermissionsString(permissions);
  		System.out.println("actualPermissions: " + actualPermissions);
  		
			assertTrue("Document permissions difference in size value",
					actualPermissions.contains("size:3"));
			
			assertTrue("Document permissions difference in rest-reader permission",
					actualPermissions.contains("rest-reader:[READ]"));
			assertTrue("Document permissions difference in rest-writer permission",
					actualPermissions.contains("rest-writer:[UPDATE]"));
			assertTrue("Document permissions difference in app-user permission",
					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));
			
			// Validate quality
			int quality = metadataHandle.getQuality();
  		System.out.println("Quality: " + quality);
			assertEquals(quality, 11);
  		
  		validateMetadata(metadataHandle);      
		}

		//=============================================================================
		// Check update works
		//=============================================================================
		Calendar updateTime = DatatypeConverter.parseDateTime("2011-01-01T00:00:01");
		updateJSONSingleDocument(temporalLsqtCollectionName, docId, null, updateTime);
		
    // Verify that the document was updated 
		// Make sure there is 1 document in latest collection
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery = sqb.collection(latestCollectionName);
		long start = 1;
		DocumentPage termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 1, termQueryResults.getTotalSize());

		// Document URIs in latest collection must be the same as the one as the original document  
    while (termQueryResults.hasNext()) {
    	record = termQueryResults.next();
    	
    	String uri = record.getUri();
  		System.out.println("URI = " + uri);
  		
  		if (!uri.equals(docId)) {
  			assertFalse("URIs are not what is expected", true);
  		}
    }		

		// Make sure there are 4 documents in jsonDocId collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(docId);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure there are 4 documents in temporal collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(temporalLsqtCollectionName);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure there are 4 documents in total. Use string search for this
		queryMgr = client.newQueryManager();
		StringQueryDefinition stringQD = queryMgr.newStringDefinition();
		stringQD.setCriteria("");

		start = 1;
		docMgr.setMetadataCategories(Metadata.ALL);
		termQueryResults = docMgr.search(stringQD, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
    while (termQueryResults.hasNext()) {
    	record = termQueryResults.next();
  		System.out.println("URI = " + record.getUri());
  		
  		metadataHandle = new DocumentMetadataHandle();
  		record.getMetadata(metadataHandle);
  			
  		if (record.getFormat() != Format.JSON) {
  			assertFalse("Format is not JSON: " +  Format.JSON, true);	
  		}
  		else {
  			// Make sure that system and valid times are what is expected  
	  		recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  				
	  		record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString()); 	  	  	  

	  	  JsonFactory factory = new JsonFactory(); 
	      ObjectMapper mapper = new ObjectMapper(factory);
	      TypeReference<HashMap<String,Object>> typeRef 
	              = new TypeReference<HashMap<String,Object>>() {};

	      HashMap<String,Object> docObject = mapper.readValue(recordHandle.toString(), typeRef); 
	      
	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> systemNode = (HashMap<String, Object>)(docObject.get(systemNodeName));
	      
	      String systemStartDate = (String)systemNode.get(systemStartERIName);
	      String systemEndDate = (String)systemNode.get(systemEndERIName);
	      System.out.println("systemStartDate = " + systemStartDate);
	      System.out.println("systemEndDate = " + systemEndDate);

	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> validNode = (HashMap<String, Object>)(docObject.get(validNodeName));
	      
	      String validStartDate = (String)validNode.get(validStartERIName);
	      String validEndDate = (String)validNode.get(validEndERIName);
	      System.out.println("validStartDate = " + validStartDate);
	      System.out.println("validEndDate = " + validEndDate);

	  		// Permissions
	  		DocumentPermissions permissions = metadataHandle.getPermissions();
	  		System.out.println("Permissions: " + permissions);
	  		
	  		String actualPermissions = getDocumentPermissionsString(permissions);
	  		System.out.println("actualPermissions: " + actualPermissions);

				int quality = metadataHandle.getQuality();
	  		System.out.println("Quality: " + quality);
	      
	      if (validStartDate.contains("2003-01-01T00:00:00") && validEndDate.contains("2008-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("9999-12-31T23:59:59")));      		

      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(updateCollectionName) && 
      					!collection.equals(temporalLsqtCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE")));
	  			assertFalse("Document permissions difference in app-user permission", actualPermissions.contains("EXECUTE"));
	  			
					assertEquals(quality, 99);
  		  }	      		

	      if (validStartDate.contains("2001-01-01T00:00:00") && validEndDate.contains("2003-01-01T00:00:00")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("9999-12-31T23:59:59")));
      		
      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalLsqtCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));
	  			
					assertEquals(quality, 11);
  		  }	 
	      
	      if (validStartDate.contains("2008-12-31T23:59:59") && validEndDate.contains("2011-12-31T23:59:59")) {	      	
	      	// This is the latest document	      	
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("9999-12-31T23:59:59")));      		
      		assertTrue("URI should be the doc uri ", record.getUri().equals(docId));

      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalLsqtCollectionName) &&
      					!collection.equals(latestCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		    		
      		
	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					assertEquals(quality, 11);
      		
      		validateMetadata(metadataHandle);
  		  }	 
	      
	      if (validStartDate.contains("2001-01-01T00:00:00") && validEndDate.contains("2011-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2010-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2011-01-01T00:00:01")));

      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalLsqtCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}      		
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					assertEquals(quality, 11);
  		  }	  
  		}
    }

		//=============================================================================
		// Check delete works
		//=============================================================================
		// Delete one of the document
		Calendar deleteTime = DatatypeConverter.parseDateTime("2012-01-01T00:00:01");
		deleteJSONSingleDocument(temporalLsqtCollectionName, docId, null, deleteTime);
		
		// Make sure there are still 4 documents in docId collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(docId);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure there is one document with docId uri
		docMgr = client.newJSONDocumentManager();
		readResults = docMgr.read(docId); 

		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 1, readResults.size());
		
		// Make sure there are no documents in latest collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(latestCollectionName);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 0, termQueryResults.getTotalSize());

		// Make sure there are 4 documents in temporal collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(temporalLsqtCollectionName);

		start = 1;
		docMgr.setMetadataCategories(Metadata.ALL);
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());	
		
	  while (termQueryResults.hasNext()) {
    	record = termQueryResults.next();
  		System.out.println("URI = " + record.getUri());
  		
  		metadataHandle = new DocumentMetadataHandle();
  		record.getMetadata(metadataHandle);
  		
  		if (record.getFormat() != Format.JSON) {
  			assertFalse("Format is not JSON: " +  Format.JSON, true);	
  		}
  		else {
  			// Make sure that system and valid times are what is expected  			
	  		recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  				
	  		record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString()); 	  	  	  

	  	  JsonFactory factory = new JsonFactory(); 
	      ObjectMapper mapper = new ObjectMapper(factory);
	      TypeReference<HashMap<String,Object>> typeRef 
	              = new TypeReference<HashMap<String,Object>>() {};

	      HashMap<String,Object> docObject = mapper.readValue(recordHandle.toString(), typeRef); 
	      
	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> systemNode = (HashMap<String, Object>)(docObject.get(systemNodeName));
	      
	      String systemStartDate = (String)systemNode.get(systemStartERIName);
	      String systemEndDate = (String)systemNode.get(systemEndERIName);
	      System.out.println("systemStartDate = " + systemStartDate);
	      System.out.println("systemEndDate = " + systemEndDate);

	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> validNode = (HashMap<String, Object>)(docObject.get(validNodeName));
	      
	      String validStartDate = (String)validNode.get(validStartERIName);
	      String validEndDate = (String)validNode.get(validEndERIName);
	      System.out.println("validStartDate = " + validStartDate);
	      System.out.println("validEndDate = " + validEndDate);

	  		// Permissions
	  		DocumentPermissions permissions = metadataHandle.getPermissions();
	  		System.out.println("Permissions: " + permissions);
	  		
	  		String actualPermissions = getDocumentPermissionsString(permissions);
	  		System.out.println("actualPermissions: " + actualPermissions);
	  		
				int quality = metadataHandle.getQuality();
	  		System.out.println("Quality: " + quality);
	      
	      if (validStartDate.contains("2003-01-01T00:00:00") && validEndDate.contains("2008-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2012-01-01T00:00:01")));
      		
      		Iterator<String> resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(updateCollectionName) && 
      					!collection.equals(temporalLsqtCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE")));
	  			assertFalse("Document permissions difference in app-user permission", actualPermissions.contains("EXECUTE"));

					assertEquals(quality, 99);
  		  }	      		

	      if (validStartDate.contains("2001-01-01T00:00:00") && validEndDate.contains("2003-01-01T00:00:00")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2012-01-01T00:00:01")));

      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalLsqtCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					assertEquals(quality, 11);
  		  }	 
	      
	      if (validStartDate.contains("2008-12-31T23:59:59") && validEndDate.contains("2011-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2012-01-01T00:00:01")));
      		
      		assertTrue("URI should be the doc uri ", record.getUri().equals(docId));

      		// Document should not be in latest collection
      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalLsqtCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		    		      		
	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					assertEquals(quality, 11);
					
	  			// Properties should still be associated this document
	  			// BUG ... This should be commented out when properties are not deleted when document is deleted
      		// validateMetadata(metadataHandle);
  		  }	 
	      
	      if (validStartDate.contains("2001-01-01T00:00:00") && validEndDate.contains("2011-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2010-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2011-01-01T00:00:01")));
      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalLsqtCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}      		
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					assertEquals(quality, 11);
  		  }	 
  		}
    }

		// Make sure there are 4 documents in total. Use string search for this
		queryMgr = client.newQueryManager();
		
		start = 1;
		termQueryResults = docMgr.search(stringQD, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
	}
	
	@Test
	public void testReadingSpecificVersionBasedOnTime() throws Exception { 

		/***
		System.out.println("Inside testReadingSpecificVersionBasedOnTime");
		
		String docId = "javaSingleJSONDoc.json";
		
		Calendar firstInsertTime = DatatypeConverter.parseDateTime("2010-01-01T00:00:01");
		insertJSONSingleDocument(docId, firstInsertTime);
		
    // Verify that the document was inserted 
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		JacksonDatabindHandle<ObjectNode> recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		docMgr.read  (docId, metadataHandle, recordHandle, null, null, temporalCollectionName, firstInsertTime);	
		DocumentPage readResults = docMgr.read(docId); 

		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 1, readResults.size());  // 2, because includes content and metadata
		
		DocumentRecord record = readResults.next();
		System.out.println("URI after insert = " + record.getUri());
		assertEquals("Document uri wrong after insert", docId, record.getUri());

		//=============================================================================
		// Check update works
		//=============================================================================
		Calendar updateTime = DatatypeConverter.parseDateTime("2011-01-01T00:00:01");
		updateJSONSingleDocument(temporalCollectionName, docId, null, updateTime);
		
    // Verify that the document was updated 
		// Make sure there is 1 document in latest collection
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition termQuery = sqb.collection(latestCollectionName);
		long start = 1;
		DocumentPage termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 1, termQueryResults.getTotalSize());

		// Document URIs in latest collection must be the same as the one as the original document  
    while (termQueryResults.hasNext()) {
    	record = termQueryResults.next();
    	
    	String uri = record.getUri();
  		System.out.println("URI = " + uri);
  		
  		if (!uri.equals(docId)) {
  			assertFalse("URIs are not what is expected", true);
  		}
    }		

		// Make sure there are 4 documents in jsonDocId collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(docId);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure there are 4 documents in temporal collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(temporalCollectionName);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure there are 4 documents in total. Use string search for this
		queryMgr = client.newQueryManager();
		StringQueryDefinition stringQD = queryMgr.newStringDefinition();
		stringQD.setCriteria("");

		start = 1;
		docMgr.setMetadataCategories(Metadata.ALL);
		termQueryResults = docMgr.search(stringQD, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
    while (termQueryResults.hasNext()) {
    	record = termQueryResults.next();
  		System.out.println("URI = " + record.getUri());
  		
  		metadataHandle = new DocumentMetadataHandle();
  		record.getMetadata(metadataHandle);
  			
  		if (record.getFormat() != Format.JSON) {
  			assertFalse("Format is not JSON: " +  Format.JSON, true);	
  		}
  		else {
  			// Make sure that system and valid times are what is expected  
	  		recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  				
	  		record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString()); 	  	  	  

	  	  JsonFactory factory = new JsonFactory(); 
	      ObjectMapper mapper = new ObjectMapper(factory);
	      TypeReference<HashMap<String,Object>> typeRef 
	              = new TypeReference<HashMap<String,Object>>() {};

	      HashMap<String,Object> docObject = mapper.readValue(recordHandle.toString(), typeRef); 
	      
	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> systemNode = (HashMap<String, Object>)(docObject.get(systemNodeName));
	      
	      String systemStartDate = (String)systemNode.get(systemStartERIName);
	      String systemEndDate = (String)systemNode.get(systemEndERIName);
	      System.out.println("systemStartDate = " + systemStartDate);
	      System.out.println("systemEndDate = " + systemEndDate);

	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> validNode = (HashMap<String, Object>)(docObject.get(validNodeName));
	      
	      String validStartDate = (String)validNode.get(validStartERIName);
	      String validEndDate = (String)validNode.get(validEndERIName);
	      System.out.println("validStartDate = " + validStartDate);
	      System.out.println("validEndDate = " + validEndDate);

	  		// Permissions
	  		DocumentPermissions permissions = metadataHandle.getPermissions();
	  		System.out.println("Permissions: " + permissions);
	  		
	  		String actualPermissions = getDocumentPermissionsString(permissions);
	  		System.out.println("actualPermissions: " + actualPermissions);

				int quality = metadataHandle.getQuality();
	  		System.out.println("Quality: " + quality);
	      
	      if (validStartDate.contains("2003-01-01T00:00:00") && validEndDate.contains("2008-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("9999-12-31T23:59:59")));      		

      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(updateCollectionName) && 
      					!collection.equals(temporalCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE")));
	  			assertFalse("Document permissions difference in app-user permission", actualPermissions.contains("EXECUTE"));
	  			
					assertEquals(quality, 99);
  		  }	      		

	      if (validStartDate.contains("2001-01-01T00:00:00") && validEndDate.contains("2003-01-01T00:00:00")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("9999-12-31T23:59:59")));
      		
      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));
	  			
					// assertEquals(quality, 11);
  		  }	 
	      
	      if (validStartDate.contains("2008-12-31T23:59:59") && validEndDate.contains("2011-12-31T23:59:59")) {	      	
	      	// This is the latest document	      	
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("9999-12-31T23:59:59")));      		
      		assertTrue("URI should be the doc uri ", record.getUri().equals(docId));

      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalCollectionName) &&
      					!collection.equals(latestCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		    		
      		
	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					// assertEquals(quality, 11);
      		
      		validateMetadata(metadataHandle);
  		  }	 
	      
	      if (validStartDate.contains("2001-01-01T00:00:00") && validEndDate.contains("2011-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2010-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2011-01-01T00:00:01")));

      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}      		
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					// assertEquals(quality, 11);
  		  }	  
  		}
    }

		//=============================================================================
		// Check delete works
		//=============================================================================
		// Delete one of the document
		Calendar deleteTime = DatatypeConverter.parseDateTime("2012-01-01T00:00:01");
		deleteJSONSingleDocument(docId, null, deleteTime);
		
		// Make sure there are still 4 documents in docId collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(docId);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		
		// Make sure there is one document with docId uri
		docMgr = client.newJSONDocumentManager();
		readResults = docMgr.read(docId); 

		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 1, readResults.size());
		
		// Make sure there are no documents in latest collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(latestCollectionName);

		start = 1;
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 0, termQueryResults.getTotalSize());

		// Make sure there are 4 documents in temporal collection
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		termQuery = sqb.collection(temporalCollectionName);

		start = 1;
		docMgr.setMetadataCategories(Metadata.ALL);
		termQueryResults = docMgr.search(termQuery, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());	
		
	  while (termQueryResults.hasNext()) {
    	record = termQueryResults.next();
  		System.out.println("URI = " + record.getUri());
  		
  		metadataHandle = new DocumentMetadataHandle();
  		record.getMetadata(metadataHandle);
  		
  		if (record.getFormat() != Format.JSON) {
  			assertFalse("Format is not JSON: " +  Format.JSON, true);	
  		}
  		else {
  			// Make sure that system and valid times are what is expected  			
	  		recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  				
	  		record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString()); 	  	  	  

	  	  JsonFactory factory = new JsonFactory(); 
	      ObjectMapper mapper = new ObjectMapper(factory);
	      TypeReference<HashMap<String,Object>> typeRef 
	              = new TypeReference<HashMap<String,Object>>() {};

	      HashMap<String,Object> docObject = mapper.readValue(recordHandle.toString(), typeRef); 
	      
	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> systemNode = (HashMap<String, Object>)(docObject.get(systemNodeName));
	      
	      String systemStartDate = (String)systemNode.get(systemStartERIName);
	      String systemEndDate = (String)systemNode.get(systemEndERIName);
	      System.out.println("systemStartDate = " + systemStartDate);
	      System.out.println("systemEndDate = " + systemEndDate);

	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> validNode = (HashMap<String, Object>)(docObject.get(validNodeName));
	      
	      String validStartDate = (String)validNode.get(validStartERIName);
	      String validEndDate = (String)validNode.get(validEndERIName);
	      System.out.println("validStartDate = " + validStartDate);
	      System.out.println("validEndDate = " + validEndDate);

	  		// Permissions
	  		DocumentPermissions permissions = metadataHandle.getPermissions();
	  		System.out.println("Permissions: " + permissions);
	  		
	  		String actualPermissions = getDocumentPermissionsString(permissions);
	  		System.out.println("actualPermissions: " + actualPermissions);
	  		
				int quality = metadataHandle.getQuality();
	  		System.out.println("Quality: " + quality);
	      
	      if (validStartDate.contains("2003-01-01T00:00:00") && validEndDate.contains("2008-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2012-01-01T00:00:01")));
      		
      		Iterator<String> resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(updateCollectionName) && 
      					!collection.equals(temporalCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE")));
	  			assertFalse("Document permissions difference in app-user permission", actualPermissions.contains("EXECUTE"));

					// assertEquals(quality, 99);
  		  }	      		

	      if (validStartDate.contains("2001-01-01T00:00:00") && validEndDate.contains("2003-01-01T00:00:00")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2012-01-01T00:00:01")));

      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					// assertEquals(quality, 11);
  		  }	 
	      
	      if (validStartDate.contains("2008-12-31T23:59:59") && validEndDate.contains("2011-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2011-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2012-01-01T00:00:01")));
      		
      		assertTrue("URI should be the doc uri ", record.getUri().equals(docId));

      		// Document should not be in latest collection
      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}
      		    		      		
	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					// assertEquals(quality, 11);
					
	  			// Properties should still be associated this document
	  			// BUG ... This should be commented out when properties are not deleted when document is deleted
      		// validateMetadata(metadataHandle);
  		  }	 
	      
	      if (validStartDate.contains("2001-01-01T00:00:00") && validEndDate.contains("2011-12-31T23:59:59")) {
      		assertTrue("System start date check failed", (systemStartDate.contains("2010-01-01T00:00:01")));
      		assertTrue("System start date check failed", (systemEndDate.contains("2011-01-01T00:00:01")));
      		Iterator<String>  resCollections = metadataHandle.getCollections().iterator();
      		while (resCollections.hasNext()) {
      			String collection = resCollections.next();
      			System.out.println("Collection = " + collection);
      			
      			if (!collection.equals(docId) &&
      					!collection.equals(insertCollectionName) && 
      					!collection.equals(temporalCollectionName)) {
      				assertFalse("Collection not what is expected: " + collection, true);
      			}
      		}      		
      		
	  	  	assertTrue("Properties should be empty", metadataHandle.getProperties().isEmpty());

	  			assertTrue("Document permissions difference in size value",
	  					actualPermissions.contains("size:3"));
	  			
	  			assertTrue("Document permissions difference in rest-reader permission",
	  					actualPermissions.contains("rest-reader:[READ]"));
	  			assertTrue("Document permissions difference in rest-writer permission",
	  					actualPermissions.contains("rest-writer:[UPDATE]"));
	  			assertTrue("Document permissions difference in app-user permission",
	  					(actualPermissions.contains("app-user:[") && actualPermissions.contains("READ") && 
	  					 actualPermissions.contains("UPDATE") && actualPermissions.contains("EXECUTE")));

					// assertEquals(quality, 11);
  		  }	 
  		}
    }

		// Make sure there are 4 documents in total. Use string search for this
		queryMgr = client.newQueryManager();
		
		start = 1;
		termQueryResults = docMgr.search(stringQD, start);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		***/
	}
	
	@Test
	public void testSystemTimeUsingInvalidTime() throws Exception { 

		System.out.println("Inside testSystemTimeUsingInvalidTime");
		
		String docId = "javaSingleJSONDoc.json";
		
		Calendar firstInsertTime = DatatypeConverter.parseDateTime("2010-01-01T00:00:01");
		insertJSONSingleDocument(temporalLsqtCollectionName, docId, null, null, firstInsertTime);
		
		// Update by passing a system time that is less than previous one
		Calendar updateTime = DatatypeConverter.parseDateTime("2010-01-01T00:00:00");
		
		boolean exceptionThrown = false;
		try {
		  updateJSONSingleDocument(temporalLsqtCollectionName, docId, null, updateTime);
		}
		catch (com.marklogic.client.FailedRequestException ex) {
			System.out.println(ex.getMessage());
			
			assert(ex.getFailedRequest().getMessageCode().equals("TEMPORAL-SYSTEMTIME-BACKWARDS"));
			assert(ex.getFailedRequest().getStatusCode() == 400);
			
			exceptionThrown = true;
		}

		assertTrue("Exception not thrown during invalid update of system time", exceptionThrown);		
		
		// Delete by passing invalid time
		Calendar deleteTime = DatatypeConverter.parseDateTime("2010-01-01T00:00:00");

		exceptionThrown = false;
		try {
			deleteJSONSingleDocument(temporalLsqtCollectionName, docId, null, deleteTime);
		}
		catch (com.marklogic.client.FailedRequestException ex) {
			System.out.println(ex.getMessage());
			
			assert(ex.getFailedRequest().getMessageCode().equals("TEMPORAL-SYSTEMTIME-BACKWARDS"));
			assert(ex.getFailedRequest().getStatusCode() == 400);
			
			exceptionThrown = true;
		}		
	}
	
	// @Test
	// BUG: REST API bug around transactions fails this test
	public void TransactionCommit() throws Exception { 

		System.out.println("Inside testTransactionCommit");
		
		String docId = "javaSingleJSONDoc.json";
		
		Transaction transaction = client.openTransaction("Transaction for BiTemporal");
		try {					
	    // Verify that the document was inserted 
			JSONDocumentManager docMgr = client.newJSONDocumentManager();
			DocumentPage readResults = docMgr.read(transaction, docId); 
	
			System.out.println("Number of results = " + readResults.size());
			if (readResults.size() != 1) {
				transaction.rollback();
				
				assertEquals("Wrong number of results", 1, readResults.size());
			}
			
			DocumentRecord latestDoc = readResults.next();
			System.out.println("URI after insert = " + latestDoc.getUri());
			
			if (!docId.equals(latestDoc.getUri())) {
				transaction.rollback();
				
				assertEquals("Document uri wrong after insert", docId, latestDoc.getUri());
			}
			
			// Make sure document is not visible to any other transaction
			readResults = docMgr.read(docId); 
			System.out.println("Number of results = " + readResults.size());
			if (readResults.size() != 0) {
				transaction.rollback();
				
				assertEquals("Wrong number of results", 1, readResults.size());
			}
			
		  updateJSONSingleDocument(temporalCollectionName, docId, transaction, null);		
	
			QueryManager queryMgr = client.newQueryManager();
			StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
			StructuredQueryDefinition termQuery = sqb.collection(latestCollectionName);
			
			long start = 1;
			DocumentPage termQueryResults = docMgr.search(termQuery, start, transaction);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
			
			if ( termQueryResults.getTotalSize() != 1) {
				transaction.rollback();
				
				assertEquals("Wrong number of results", 1, termQueryResults.getTotalSize());
			}
	
			// There should be 4 documents in docId collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(docId);
			
			start = 1;
			termQueryResults = docMgr.search(termQuery, start, transaction);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
	
			if (termQueryResults.getTotalSize() != 4) {
				transaction.rollback();
				
				assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
			}
			
			// Search for documents using doc uri collection and no transaction object passed.
			// There should be 0 documents in docId collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(docId);
			
			start = 1;
			termQueryResults = docMgr.search(termQuery, start);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
	
			if (termQueryResults.getTotalSize() != 0) {
				transaction.rollback();
				
				assertEquals("Wrong number of results", 0, termQueryResults.getTotalSize());
			}
			
		 deleteJSONSingleDocument(temporalCollectionName, docId, transaction);
	
			// There should be no documents in latest collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(latestCollectionName);
			
			start = 1;
			termQueryResults = docMgr.search(termQuery, start, transaction);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
	
			if ( termQueryResults.getTotalSize() != 0) {
				transaction.rollback();
				
				assertEquals("Wrong number of results", 0, termQueryResults.getTotalSize());
			}
			
			transaction.commit();
			
			// There should still be no documents in latest collection
			queryMgr = client.newQueryManager();
			sqb = queryMgr.newStructuredQueryBuilder();
			termQuery = sqb.collection(latestCollectionName);
			
			start = 1;
			termQueryResults = docMgr.search(termQuery, start);
			System.out.println("Number of results = " + termQueryResults.getTotalSize());
			assertEquals("Wrong number of results", 0, termQueryResults.getTotalSize());
		}
		catch (Exception ex) {
			transaction.rollback();
			
			assertTrue("testTransactionCommit failed", false);
		}
	}
	
	// @Test
	// BUG: REST API bug around transactions fails this test
	public void TransactionRollback() throws Exception { 

		System.out.println("Inside testTransaction");
		
		String docId = "javaSingleJSONDoc.json";
		
		Transaction transaction = client.openTransaction("Transaction for BiTemporal");
		
		try {
	  	insertJSONSingleDocument(temporalCollectionName, docId, null, transaction, null);
		}
		catch (Exception ex) {
			transaction.rollback();
			
			assertTrue("insertJSONSingleDocument failed in testTransactionRollback", false);
		}
		
    // Verify that the document was inserted 
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentPage readResults = docMgr.read(transaction, docId); 

		System.out.println("Number of results = " + readResults.size());
		if (readResults.size() != 1) {
			transaction.rollback();			
			
			assertEquals("Wrong number of results", 1, readResults.size());
		}
		
		DocumentRecord latestDoc = readResults.next();
		System.out.println("URI after insert = " + latestDoc.getUri());
		if (!docId.equals(latestDoc.getUri())) {
			transaction.rollback();
			
			assertEquals("Document uri wrong after insert", docId, latestDoc.getUri());			
		}
		
		try {
		  updateJSONSingleDocument(temporalCollectionName, docId, transaction, null);
		}
		catch (Exception ex) {
			transaction.rollback();
			
			assertTrue("updateJSONSingleDocument failed in testTransactionRollback", false);
		}

    // Verify that the document is visible and count is 4
		// Fetch documents associated with a search term (such as XML) in Address element 
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		
		StructuredQueryDefinition termQuery = sqb.collection(docId);
		
		long start = 1;
		DocumentPage termQueryResults = docMgr.search(termQuery, start, transaction);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		if (termQueryResults.getTotalSize() != 4) {
			transaction.rollback();
			
			assertEquals("Wrong number of results", 4, termQueryResults.getTotalSize());
		}
	  
		transaction.rollback();

    // Verify that the document is not there after rollback
		readResults = docMgr.read(docId); 

		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 0, readResults.size());
		
		//=======================================================================
		// Now try rollback with delete
		System.out.println("Test Rollback after delete");
		docId = "javaSingleJSONDocForDelete.json";
		
		transaction = client.openTransaction("Transaction Rollback for BiTemporal Delete");
		
		try {
	  	insertJSONSingleDocument(temporalCollectionName, docId, null, transaction, null);
		}
		catch (Exception ex) {
			transaction.rollback();
			
			assertTrue("insertJSONSingleDocument failed in testTransactionRollback", false);
		}
		
    // Verify that the document was inserted 
		docMgr = client.newJSONDocumentManager();
		readResults = docMgr.read(transaction, docId); 

		System.out.println("Number of results = " + readResults.size());
		if (readResults.size() != 1) {
			transaction.rollback();
			
			assertEquals("Wrong number of results", 1, readResults.size());
		}
		
		latestDoc = readResults.next();
		System.out.println("URI after insert = " + latestDoc.getUri());
		if (!docId.equals(latestDoc.getUri())) {
			transaction.rollback();
			
			assertEquals("Document uri wrong after insert", docId, latestDoc.getUri());
		}
		
		try {
		  deleteJSONSingleDocument(temporalCollectionName, docId, transaction);
		}
		catch (Exception ex) {
			transaction.rollback();
			
			assertTrue("deleteJSONSingleDocument failed in testTransactionRollback", false);
		}

    // Verify that the document is visible and count is 1
		// Fetch documents associated with a search term (such as XML) in Address element 
		queryMgr = client.newQueryManager();
		sqb = queryMgr.newStructuredQueryBuilder();
		
		termQuery = sqb.collection(docId);
		
		start = 1;
		termQueryResults = docMgr.search(termQuery, start, transaction);
		System.out.println("Number of results = " + termQueryResults.getTotalSize());
		if (termQueryResults.getTotalSize() != 1) {
			transaction.rollback();
			
			assertEquals("Wrong number of results", 1, termQueryResults.getTotalSize());
		}
	  
		transaction.rollback();
		
	  // Verify that the document was rolled back and count is 0
		readResults = docMgr.read(docId); 

		System.out.println("Number of results = " + readResults.size());
		assertEquals("Wrong number of results", 0, readResults.size());
		
		System.out.println("Done");
	}
	
	@Test
	public void testPeriodRangeQuerySingleAxisBasedOnALNContains() throws Exception {
		// Read documents based on document URI and ALN Contains. We are just looking for count of documents to be correct

		String docId = "javaSingleJSONDoc.json";
		insertJSONSingleDocument(temporalCollectionName, docId, null);
		updateJSONSingleDocument(temporalCollectionName, docId);
		
		// Fetch documents associated with a search term (such as XML) in Address element 
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		
		StructuredQueryDefinition termQuery = sqb.collection(docId);
		
		StructuredQueryBuilder.Axis validAxis = sqb.axis(axisValidName);
		Calendar start1 = DatatypeConverter.parseDateTime("2001-01-01T00:00:01");
		Calendar end1 = DatatypeConverter.parseDateTime("2011-12-31T23:59:58");
		StructuredQueryBuilder.Period period1 = sqb.period(start1, end1);
		StructuredQueryDefinition periodQuery = sqb.and(termQuery,
			sqb.temporalPeriodRange(validAxis, TemporalOperator.ALN_CONTAINS, period1));
		
		long start = 1;
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);	// Get all metadata
		DocumentPage termQueryResults = docMgr.search(periodQuery, start);

		long count = 0;
    while (termQueryResults.hasNext()) {
    	++count;
    	DocumentRecord record = termQueryResults.next();
  		System.out.println("URI = " + record.getUri());
  		
  		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
  		record.getMetadata(metadataHandle);
  		Iterator<String> resCollections = metadataHandle.getCollections().iterator();
  		while (resCollections.hasNext()) {
  			System.out.println("Collection = " + resCollections.next()); 
  		}

  		if (record.getFormat() == Format.XML) {
  			DOMHandle recordHandle = new DOMHandle(); 				
  			record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString());   		
  		}
  		else {
	  		JacksonDatabindHandle<ObjectNode> recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  				
	  		record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString()); 

	  	  JsonFactory factory = new JsonFactory(); 
	      ObjectMapper mapper = new ObjectMapper(factory);
	      TypeReference<HashMap<String,Object>> typeRef 
	              = new TypeReference<HashMap<String,Object>>() {};

	      HashMap<String,Object> docObject = mapper.readValue(recordHandle.toString(), typeRef); 
	      
	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> systemNode = (HashMap<String, Object>)(docObject.get(systemNodeName));
	      
	      String systemStartDate = (String)systemNode.get(systemStartERIName);
	      String systemEndDate = (String)systemNode.get(systemEndERIName);
	      System.out.println("systemStartDate = " + systemStartDate);
	      System.out.println("systemEndDate = " + systemEndDate);

	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> validNode = (HashMap<String, Object>)(docObject.get(validNodeName));
	      
	      String validStartDate = (String)validNode.get(validStartERIName);
	      String validEndDate = (String)validNode.get(validEndERIName);
	      System.out.println("validStartDate = " + validStartDate);
	      System.out.println("validEndDate = " + validEndDate);

	      assertTrue("Valid start date check failed", 
	      		(validStartDate.equals("2001-01-01T00:00:00") && validEndDate.equals("2011-12-31T23:59:59")));	     
  		}
    }
		
		System.out.println("Number of results using SQB = " + count);
		assertEquals("Wrong number of results", 1, count);		
	}
	
	@Test
	public void testPeriodRangeQueryMultiplesAxesBasedOnALNContains() throws Exception {
		// Read documents based on document URI and ALN_OVERLAPS. We are just looking 
		// for count of documents to be correct

		String docId = "javaSingleJSONDoc.json";
		insertJSONSingleDocument(temporalCollectionName, docId, null);
		updateJSONSingleDocument(temporalCollectionName, docId);
		
		// Fetch documents associated with a search term (such as XML) in Address element 
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		
		StructuredQueryDefinition termQuery = sqb.collection(docId);
		
		StructuredQueryBuilder.Axis validAxis1 = sqb.axis(axisValidName);
		Calendar start1 = DatatypeConverter.parseDateTime("2001-01-01T00:00:01");
		Calendar end1 = DatatypeConverter.parseDateTime("2011-12-31T23:59:58");
		StructuredQueryBuilder.Period period1 = sqb.period(start1, end1);
		
		StructuredQueryBuilder.Axis validAxis2 = sqb.axis(axisValidName);
		Calendar start2 = DatatypeConverter.parseDateTime("2003-01-01T00:00:01");
		Calendar end2 = DatatypeConverter.parseDateTime("2008-12-31T23:59:58");
		StructuredQueryBuilder.Period period2 = sqb.period(start2, end2);
		
		StructuredQueryBuilder.Axis[] axes = new StructuredQueryBuilder.Axis[]{validAxis1, validAxis2};
		StructuredQueryBuilder.Period[] periods = new StructuredQueryBuilder.Period[]{period1, period2};
		
		StructuredQueryDefinition periodQuery = sqb.and(termQuery,
			sqb.temporalPeriodRange(axes, TemporalOperator.ALN_CONTAINS, periods));
		
		// Note that the query will be done for every axis across every period. And the results will be an OR of 
		// the result of each of the query done for every axis across every period
		long start = 1;
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);	// Get all metadata
		DocumentPage termQueryResults = docMgr.search(periodQuery, start);

		long count = 0;
    while (termQueryResults.hasNext()) {
    	++count;
    	DocumentRecord record = termQueryResults.next();
  		System.out.println("URI = " + record.getUri());
  		
  		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
  		record.getMetadata(metadataHandle);
  		Iterator<String> resCollections = metadataHandle.getCollections().iterator();
  		while (resCollections.hasNext()) {
  			System.out.println("Collection = " + resCollections.next()); 
  		}

  		if (record.getFormat() != Format.JSON) {
  			assertFalse("Invalid document format: " + record.getFormat(), true);		
  		}
  		else {
	  		JacksonDatabindHandle<ObjectNode> recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  				
	  		record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString()); 
	  	  
	  	  JsonFactory factory = new JsonFactory(); 
	      ObjectMapper mapper = new ObjectMapper(factory);
	      TypeReference<HashMap<String,Object>> typeRef 
	              = new TypeReference<HashMap<String,Object>>() {};

	      HashMap<String,Object> docObject = mapper.readValue(recordHandle.toString(), typeRef); 
	      
	      @SuppressWarnings("unchecked")
        HashMap<String, Object> validNode = (HashMap<String, Object>)(docObject.get(validNodeName));
	      // HashMap<?, ?> validNode = (HashMap<?, ?>)(docObject.get(validNodeName));
	      
	      String validStartDate = (String)validNode.get(validStartERIName);
	      String validEndDate = (String)validNode.get(validEndERIName);
	      System.out.println("validStartDate = " + validStartDate);
	      System.out.println("validEndDate = " + validEndDate);
	      
	      assertTrue("Valid start date check failed", (validStartDate.equals("2001-01-01T00:00:00") || validStartDate.equals("2003-01-01T00:00:00")));
	      assertTrue("Valid end date check failed", (validEndDate.equals("2011-12-31T23:59:59") || validEndDate.equals("2008-12-31T23:59:59")));
  		}
    }
		
		System.out.println("Number of results using SQB = " + count);
		assertEquals("Wrong number of results", 2, count);		
	}	

	@Test
	public void testPeriodCompareQuerySingleAxisBasedOnALNContains() throws Exception {
		// Read documents based on document URI and ALN Contains. We are just looking for count of documents to be correct

		String docId = "javaSingleJSONDoc.json";
		
		Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");
		insertJSONSingleDocument(temporalCollectionName, docId, null, null, insertTime);

		Calendar updateTime = DatatypeConverter.parseDateTime("2010-01-01T00:00:01");
		updateJSONSingleDocument(temporalCollectionName, docId, null, updateTime);
		
		// Fetch documents associated with a search term (such as XML) in Address element 
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		
		StructuredQueryBuilder.Axis validAxis = sqb.axis(axisValidName);
		StructuredQueryBuilder.Axis systemAxis = sqb.axis(axisSystemName);

		StructuredQueryDefinition termQuery = sqb.collection(docId);
		StructuredQueryDefinition periodQuery = sqb.and(termQuery,
			sqb.temporalPeriodCompare(validAxis, TemporalOperator.ALN_CONTAINS, systemAxis));
		
		long start = 1;
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		docMgr.setMetadataCategories(Metadata.ALL);	// Get all metadata
		DocumentPage termQueryResults = docMgr.search(periodQuery, start);

		long count = 0;
    while (termQueryResults.hasNext()) {
    	++count;
    	DocumentRecord record = termQueryResults.next();
  		System.out.println("URI = " + record.getUri());
  		
  		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
  		record.getMetadata(metadataHandle);
  		Iterator<String> resCollections = metadataHandle.getCollections().iterator();
  		while (resCollections.hasNext()) {
  			System.out.println("Collection = " + resCollections.next()); 
  		}

  		if (record.getFormat() == Format.XML) {
  			DOMHandle recordHandle = new DOMHandle(); 				
  			record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString());   		
  		}
  		else {
	  		JacksonDatabindHandle<ObjectNode> recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  				
	  		record.getContent (recordHandle);
	  	  System.out.println("Content = " + recordHandle.toString()); 
	  	  	  	 
	  	  JsonFactory factory = new JsonFactory(); 
	      ObjectMapper mapper = new ObjectMapper(factory);
	      TypeReference<HashMap<String,Object>> typeRef 
	              = new TypeReference<HashMap<String,Object>>() {};

	      HashMap<String,Object> docObject = mapper.readValue(recordHandle.toString(), typeRef); 
	      
	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> systemNode = (HashMap<String, Object>)(docObject.get(systemNodeName));
	      
	      String systemStartDate = (String)systemNode.get(systemStartERIName);
	      String systemEndDate = (String)systemNode.get(systemEndERIName);
	      System.out.println("systemStartDate = " + systemStartDate);
	      System.out.println("systemEndDate = " + systemEndDate);

	      @SuppressWarnings("unchecked")
	      HashMap<String, Object> validNode = (HashMap<String, Object>)(docObject.get(validNodeName));
	      
	      String validStartDate = (String)validNode.get(validStartERIName);
	      String validEndDate = (String)validNode.get(validEndERIName);
	      System.out.println("validStartDate = " + validStartDate);
	      System.out.println("validEndDate = " + validEndDate);
	      
	      assertTrue("Valid start date check failed", 
	      		(validStartDate.contains("2001-01-01T00:00:00") && validEndDate.contains("2011-12-31T23:59:59")));	 

	      assertTrue("System start date check failed", 
	      		(systemStartDate.contains("2005-01-01T00:00:01") && systemEndDate.contains("2010-01-01T00:00:01")));  			      
    
  		}
    }
    
		System.out.println("Number of results using SQB = " + count);
		assertEquals("Wrong number of results", 1, count);		
	}
	
	@Test
	public void testLsqtQuery() throws Exception {

		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
		
		ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName, temporalCollectionName, true);
		
		try {
			// Read documents based on document URI and ALN Contains. We are just looking for count of documents to be correct
			String docId = "javaSingleJSONDoc.json";
			
			Calendar insertTime = DatatypeConverter.parseDateTime("2005-01-01T00:00:01");
			insertJSONSingleDocument(temporalCollectionName, docId, null, null, insertTime);
	
			Calendar updateTime = DatatypeConverter.parseDateTime("2010-01-01T00:00:01");
			updateJSONSingleDocument(temporalCollectionName, docId, null, updateTime);
			
			// Fetch documents associated with a search term (such as XML) in Address element 
			QueryManager queryMgr = client.newQueryManager();
			StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
	
			Calendar queryTime = DatatypeConverter.parseDateTime("2007-01-01T00:00:01");
			StructuredQueryDefinition periodQuery = sqb.temporalLsqtQuery(temporalCollectionName, 
					queryTime, 0, new String[]{});
			
			long start = 1;
			JSONDocumentManager docMgr = client.newJSONDocumentManager();
			docMgr.setMetadataCategories(Metadata.ALL);	// Get all metadata
			DocumentPage termQueryResults = docMgr.search(periodQuery, start);
			
			long count = 0;
	    while (termQueryResults.hasNext()) {
	    	++count;
	    	DocumentRecord record = termQueryResults.next();
	  		System.out.println("URI = " + record.getUri());
	  		
	  		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
	  		record.getMetadata(metadataHandle);
	  		Iterator<String> resCollections = metadataHandle.getCollections().iterator();
	  		while (resCollections.hasNext()) {
	  			System.out.println("Collection = " + resCollections.next()); 
	  		}
	
	  		if (record.getFormat() == Format.XML) {
	  			DOMHandle recordHandle = new DOMHandle(); 				
	  			record.getContent (recordHandle);
		  	  System.out.println("Content = " + recordHandle.toString());   		
	  		}
	  		else {
		  		JacksonDatabindHandle<ObjectNode> recordHandle =  new JacksonDatabindHandle<ObjectNode>(ObjectNode.class);  				
		  		record.getContent (recordHandle);
		  	  System.out.println("Content = " + recordHandle.toString()); 
	
		  	  JsonFactory factory = new JsonFactory(); 
		      ObjectMapper mapper = new ObjectMapper(factory);
		      TypeReference<HashMap<String,Object>> typeRef 
		              = new TypeReference<HashMap<String,Object>>() {};
	
		      HashMap<String,Object> docObject = mapper.readValue(recordHandle.toString(), typeRef); 
		      
		      @SuppressWarnings("unchecked")
		      HashMap<String, Object> systemNode = (HashMap<String, Object>)(docObject.get(systemNodeName));
		      
		      String systemStartDate = (String)systemNode.get(systemStartERIName);
		      String systemEndDate = (String)systemNode.get(systemEndERIName);
		      System.out.println("systemStartDate = " + systemStartDate);
		      System.out.println("systemEndDate = " + systemEndDate);
	
		      @SuppressWarnings("unchecked")
		      HashMap<String, Object> validNode = (HashMap<String, Object>)(docObject.get(validNodeName));
		      
		      String validStartDate = (String)validNode.get(validStartERIName);
		      String validEndDate = (String)validNode.get(validEndERIName);
		      System.out.println("validStartDate = " + validStartDate);
		      System.out.println("validEndDate = " + validEndDate);
	
		      // assertTrue("Valid start date check failed", 
		      //		(validStartDate.equals("2008-12-31T23:59:59") && validEndDate.equals("2011-12-31T23:59:59")) ||
		      //		(validStartDate.equals("2003-01-01T00:00:00") && validEndDate.equals("2008-12-31T23:59:59")) ||
		      //		(validStartDate.equals("2001-01-01T00:00:00") && validEndDate.equals("2003-01-01T00:00:00")));	     
	  		}
	    }
			
	    // BUG. I believe Java API is doing a get instead of a POST that returns all documents in doc uri collection
			System.out.println("Number of results using SQB = " + count);
			assertEquals("Wrong number of results", 3, count);
	  }
		catch (Exception ex) {
			throw ex;
		}
		
		finally {
			ConnectedRESTQA.updateTemporalCollectionForLSQT(dbName, temporalCollectionName, false);
		}
	}

	@Test
	// Negative test
	public void testJSONTransforms() throws Exception {
		// Now insert a JSON document
		System.out.println("In testJSONTransforms .. testing JSON transforms");
		String jsonDocId = "javaSingleJSONDoc.json";
		
		insertJSONSingleDocument(temporalCollectionName, jsonDocId, "timestampTransform");

		System.out.println("Out testJSONTransforms .. testing JSON transforms");	
	}
	
	@Test
	// Negative test
	public void testInsertJSONDocumentUsingXMLExtension() throws Exception {
		// Now insert a JSON document
		String jsonDocId = "javaSingleJSONDoc.xml";
		
		boolean exceptionThrown = false;
		try {
		  insertJSONSingleDocument(temporalCollectionName, jsonDocId, null);
		}
		catch (com.marklogic.client.FailedRequestException ex) {
			exceptionThrown = true;		
			System.out.println(ex.getFailedRequest().getStatusCode());
			System.out.println(ex.getFailedRequest().getMessageCode());
			
			// BUG: Right now this returns 500 error. Bug is open to fix this
			// assert(ex.getFailedRequest().getMessageCode().equals("TEMPORAL-SYSTEMTIME-BACKWARDS"));
			// assert(ex.getFailedRequest().getStatusCode() == 400);
		}
		
		assertTrue("Exception not thrown for invalid extension", exceptionThrown);	
	}

	@Test
	// Negative test
	public void testInsertJSONDocumentUsingNonExistingTemporalCollection() throws Exception {
		// Now insert a JSON document
		String jsonDocId = "javaSingleJSONDoc.json";
		
		boolean exceptionThrown = false;

		System.out.println("Inside testInsertJSONDocumentUsingNonExistingTemporalCollection");
		
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle(
				"2001-01-01T00:00:00", "2011-12-31T23:59:59", "999 Skyway Park - JSON", jsonDocId);
				
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);

		// put meta-data
		DocumentMetadataHandle mh = setMetadata(false);				

	  try {
	    docMgr.write(jsonDocId, mh, handle, null, null, "invalidCollection");		
		}
		catch (com.marklogic.client.FailedRequestException ex) {
			exceptionThrown = true;		
			System.out.println(ex.getFailedRequest().getStatusCode());
			System.out.println(ex.getFailedRequest().getMessageCode());
			
			// BUG: Right now this returns 500 error. Bug is open to fix this
			// assert(ex.getFailedRequest().getMessageCode().equals("TEMPORAL-SYSTEMTIME-BACKWARDS"));
			// assert(ex.getFailedRequest().getStatusCode() == 400);
		}
		
		assertTrue("Exception not thrown for invalid temporal collection", exceptionThrown);	
	}

	@Test
	// Negative test
	public void testDocumentUsingCollectionNamedLatest() throws Exception {
		// Now insert a JSON document
		String jsonDocId = "javaSingleJSONDoc.json";
		
		boolean exceptionThrown = false;

		System.out.println("Inside testDocumentUsingCollectionNamedLatest");
		
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle(
				"2001-01-01T00:00:00", "2011-12-31T23:59:59", "999 Skyway Park - JSON", jsonDocId);
				
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);

		// put meta-data
		DocumentMetadataHandle mh = setMetadata(false);				

	  try {
	    docMgr.write(jsonDocId, mh, handle, null, null, latestCollectionName);		
		}
		catch (com.marklogic.client.FailedRequestException ex) {
			exceptionThrown = true;		
			System.out.println(ex.getFailedRequest().getStatusCode());
			System.out.println(ex.getFailedRequest().getMessageCode());
			
			// BUG: Right now this returns 500 error. Bug is open to fix this
			// assert(ex.getFailedRequest().getMessageCode().equals("TEMPORAL-SYSTEMTIME-BACKWARDS"));
			// assert(ex.getFailedRequest().getStatusCode() == 400);
		}
		
		assertTrue("Exception not thrown for invalid temporal collection", exceptionThrown);	
	}

	@Test
	// Negative test. Doc URI Id is the same as the temporal collection name
	public void testInsertDocumentUsingDocumentURIAsCollectionName() throws Exception {
		// Now insert a JSON document
		String jsonDocId = "javaSingleJSONDoc.json";
		
		System.out.println("Inside testInserDocumentUsingDocumentURIAsCollectionName");
		
		// First Create collection a collection with same name as doci URI
		ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName, jsonDocId, axisSystemName, axisValidName);
		
		// Insert a document called as insertJSONSingleDocument
		insertJSONSingleDocument(temporalCollectionName, jsonDocId, null);
		
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle(
				"2001-01-01T00:00:00", "2011-12-31T23:59:59", "999 Skyway Park - JSON", jsonDocId);
				
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);

		// put meta-data
		DocumentMetadataHandle mh = setMetadata(false);				

		boolean exceptionThrown = false;
	  try {
	    docMgr.write(jsonDocId, mh, handle, null, null, jsonDocId);		
		}
		catch (com.marklogic.client.FailedRequestException ex) {
			exceptionThrown = true;		
			System.out.println(ex.getFailedRequest().getStatusCode());
			System.out.println(ex.getFailedRequest().getMessageCode());
			
			assert(ex.getFailedRequest().getMessageCode().equals("TEMPORAL-URIALREADYEXISTS"));
			assert(ex.getFailedRequest().getStatusCode() == 400);
		}

		ConnectedRESTQA.deleteElementRangeIndexTemporalCollection("Documents", jsonDocId);
		
		assertTrue("Exception not thrown for invalid temporal collection", exceptionThrown);	
	}

	@Test
	// Negative test. Doc URI Id is the same as the temporal collection name
	// BUG 30173 exists for this non failure. 
	public void testCreateCollectionUsingSameNameAsDocURI() throws Exception {
		// Now insert a JSON document
		String jsonDocId = "javaSingleJSONDoc.json";
		
		System.out.println("Inside testInserDocumentUsingDocumentURIAsCollectionName");
		
		// Insert a document called as insertJSONSingleDocument
		insertJSONSingleDocument(temporalCollectionName, jsonDocId, null);
		
		boolean exceptionThrown = false;
	  try {
			// Create collection a collection with same name as doci URI
			ConnectedRESTQA.addElementRangeIndexTemporalCollection(dbName, jsonDocId, axisSystemName, axisValidName);
		}
		catch (com.marklogic.client.FailedRequestException ex) {
			exceptionThrown = true;		
			System.out.println(ex.getFailedRequest().getStatusCode());
			System.out.println(ex.getFailedRequest().getMessageCode());
			
			// assert(ex.getFailedRequest().getMessageCode().equals("TEMPORAL-URIALREADYEXISTS"));
			// assert(ex.getFailedRequest().getStatusCode() == 400);
		}
		// assertTrue("Exception not thrown for invalid temporal collection", exceptionThrown);	
		
		// Create a document using the collection created above
		// So, we are creating a document under a collection whose name is the same as an existing temporal doc uri
		JacksonDatabindHandle<ObjectNode> handle = getJSONDocumentHandle(
				"2001-01-01T00:00:00", "2011-12-31T23:59:59", "999 Skyway Park - JSON", jsonDocId);
				
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);

		// put meta-data
		DocumentMetadataHandle mh = setMetadata(false);				

		exceptionThrown = false;
	  try {
	    docMgr.write("1.json", mh, handle, null, null, jsonDocId);		
		}
		catch (com.marklogic.client.FailedRequestException ex) {
			exceptionThrown = true;		
			System.out.println(ex.getFailedRequest().getStatusCode());
			System.out.println(ex.getFailedRequest().getMessageCode());
			
			// assert(ex.getFailedRequest().getMessageCode().equals("TEMPORAL-URIALREADYEXISTS"));
			// assert(ex.getFailedRequest().getStatusCode() == 400);
		}

		ConnectedRESTQA.deleteElementRangeIndexTemporalCollection("Documents", jsonDocId);
		assertTrue("Exception not thrown for invalid temporal collection", exceptionThrown);	
	}
	
	public void bulkWrite() throws Exception { 
		
    GenericDocumentManager docMgr = client.newDocumentManager();
    // docMgr.setMetadataCategories(Metadata.ALL);
		DocumentWriteSet writeset = docMgr.newWriteSet();

		// put meta-data
		DocumentMetadataHandle mh = setMetadata(false);
		writeset.addDefault(mh);	
		
		// Setup for JSON document
		/**
		 * 
		 {
		 		"System": {
		 			systemStartERIName : "",
		 			systemEndERIName : "",
		 		},
		 		"Valid": {
		 			validStartERIName: "2001-01-01T00:00:00",
		 			validEndERIName: "2011-12-31T59:59:59"
		 		},
		 		"Address": "999 Skyway Park",
		 		"uri": "javaDoc1.json"
		 }
		 */
		
		String docId[] = { "javaDoc1.json",  "javaDoc2.xml"};
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		// Set system time values
		ObjectNode system = mapper.createObjectNode();
		
		system.put(systemStartERIName, "");
		system.put(systemEndERIName, "");
		rootNode.set(systemNodeName, system);

		// Set valid time values
		ObjectNode valid = mapper.createObjectNode();
		
		valid.put(validStartERIName, "2001-01-01T00:00:00");
		valid.put(validEndERIName, "2011-12-31T59:59:59");
		rootNode.set(validNodeName, valid);
		
		// Set Address
		rootNode.put(addressNodeName, "999 Skyway Park");
		
		// Set uri
		rootNode.put(uriNodeName, docId[0]);
		
		System.out.println(rootNode.toString());

		JacksonDatabindHandle<ObjectNode> handle1 = new JacksonDatabindHandle<ObjectNode>(ObjectNode.class).withFormat(Format.JSON);		
		handle1.set(rootNode);
	  writeset.add(docId[0], handle1);
	  
		// Setup for XML document
		/**
		 <root>
		   <system>
		     <systemStartERIName></systemStartERIName>
		     <systemEndERIName></systemEndERIName>
		   </system>
		   <Valid>
		     <validStartERIName>2001-01-01T00:00:00</systemStartERIName>
		     <validStartERIName>2011-12-31T59:59:59</systemEndERIName>
		   </Valid>
		   </Address>888 SKyway Park"</Address>
		   <uri>javaDoc2.xml</uri>
		 </root>
		 */
		
		Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = domDocument.createElement("root");
		
		// System start and End time
		Node systemNode = root.appendChild(domDocument.createElement("system"));
		systemNode.appendChild(domDocument.createElement(systemStartERIName));
		systemNode.appendChild(domDocument.createElement(systemEndERIName));

		// Valid start and End time
		Node validNode = root.appendChild(domDocument.createElement("valid"));
		
		Node validStartNode = validNode.appendChild(domDocument.createElement(validStartERIName));
		validStartNode.appendChild(domDocument.createTextNode("2001-01-01T00:00:00"));
		validNode.appendChild(validStartNode);

		Node validEndNode = validNode.appendChild(domDocument.createElement(validEndERIName));
		validEndNode.appendChild(domDocument.createTextNode("2011-12-31T59:59:59"));
		validNode.appendChild(validEndNode);
		
		// Address
		Node addressNode = root.appendChild(domDocument.createElement("Address"));
		addressNode.appendChild(domDocument.createTextNode("888 SKyway Park"));

		// Address
		Node uriNode = root.appendChild(domDocument.createElement("uri"));
		uriNode.appendChild(domDocument.createTextNode(docId[1]));
		domDocument.appendChild(root);

		String domString = ((DOMImplementationLS) DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.getDOMImplementation()
				).createLSSerializer().writeToString(domDocument);

		System.out.println(domString);

		writeset.add(docId[1], new DOMHandle().with(domDocument));
    docMgr.write(writeset);
    
				
		/***
		assertTrue("Did not return a iPhone 6", product1.getName().equalsIgnoreCase("iPhone 6"));
		assertTrue("Did not return a Mobile Phone", product1.getIndustry().equalsIgnoreCase("Mobile Phone"));
		assertTrue("Did not return a Mobile Phone", product1.getDescription().equalsIgnoreCase("New iPhone 6"));
		
		docMgr.readMetadata(docId[0], mhRead);
		validateMetadata(mhRead);					
		
		docMgr.read(docId[1],jacksonDBReadHandle);
		Product product2 = (Product) jacksonDBReadHandle.get();
		assertTrue("Did not return a iMac", product2.getName().equalsIgnoreCase("iMac"));
		assertTrue("Did not return a Desktop", product2.getIndustry().equalsIgnoreCase("Desktop"));
		assertTrue("Did not return a Air Book OS X", product2.getDescription().equalsIgnoreCase("Air Book OS X"));
		
		docMgr.readMetadata(docId[1], mhRead);
		validateMetadata(mhRead);			
		
		docMgr.read(docId[2], jacksonDBReadHandle);
		Product product3 = (Product) jacksonDBReadHandle.get();
		assertTrue("Did not return a iPad", product3.getName().equalsIgnoreCase("iPad"));
		assertTrue("Did not return a Tablet", product3.getIndustry().equalsIgnoreCase("Tablet"));
		assertTrue("Did not return a iPad Mini", product3.getDescription().equalsIgnoreCase("iPad Mini"));
		
		docMgr.readMetadata(docId[2], mhRead);
		validateMetadata(mhRead);		
		***/
	}
}
