/*
 * Copyright 2014-2016 MarkLogic Corporation
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Scanner;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.json.XML;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.PathLanguage;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentMetadataValues;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.marker.DocumentPatchHandle;
public class TestMetadata extends BasicJavaClientREST{

	private static String dbName = "TestMetadataDB";
	private static String [] fNames = {"TestMetadataDB-1"};


	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		configureRESTServer(dbName, fNames);
	}

	@Test
	public void testBinaryMetadataBytesHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
	{
		System.out.println("Running testBinaryMetadataBytesHandle");

		String filename = "Simple_ScanTe.png";

		// connect the client
		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);
		StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));
		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		// put metadata
		metadataHandle.getCollections().addAll("my-collection");
		metadataHandle.getCollections().addAll("another-collection");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);
		// Put key, value metadata
		metadataHandle.getMetadataValues().add("Name", "kiran");
		metadataHandle.getMetadataValues().put("Company", "MarkLogic");
		metadataHandle.getMetadataValues().put("Location", "SanCarlos");
		metadataHandle.getMetadataValues().add("zip", "95050");
		metadataHandle.getMetadataValues().add("Locations", "10");

		// write the doc with the metadata
		writeDocumentUsingBytesHandle(client, filename, "/write-bin-byteshandle-metadata/", metadataHandle, "Binary");

		// create handle to read metadata
		DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

		// read metadata
		readMetadataHandle = readMetadataFromDocument(client, "/write-bin-byteshandle-metadata/" + filename, "Binary");

		// get metadata values
		DocumentProperties properties = readMetadataHandle.getProperties();
		DocumentPermissions permissions = readMetadataHandle.getPermissions();
		DocumentCollections collections = readMetadataHandle.getCollections();
		DocumentMetadataValues metadatavalues = readMetadataHandle.getMetadataValues();

		// Properties
		String actualProperties = getDocumentPropertiesString(properties);
		System.out.println("Returned properties: " + actualProperties);

		assertTrue("Document properties difference in size value", actualProperties.contains("size:5"));
		assertTrue("Document property reviewed not found or not correct", actualProperties.contains("reviewed:true"));
		assertTrue("Document property myInteger not found or not correct", actualProperties.contains("myInteger:10"));
		assertTrue("Document property myDecimal not found or not correct", actualProperties.contains("myDecimal:34.56678"));
		assertTrue("Document property myCalendar not found or not correct", actualProperties.contains(calProperty.toString()));
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
		assertTrue("my-collection1 not found", actualCollections.contains("another-collection"));
		assertTrue("my-collection2 not found", actualCollections.contains("my-collection"));	

		// Test MetaData Values
		assertTrue("key=Name not found in metadata",metadatavalues.containsKey("Name"));
		assertTrue("value=kiran not found in metadata",metadatavalues.containsValue("kiran"));
		assertTrue("key=Name not found in metadata",metadatavalues.containsKey("Company"));
		assertTrue("value=kiran not found in metadata",metadatavalues.containsValue("MarkLogic"));
		assertFalse("NO metadat keyvalues found",metadatavalues.isEmpty());
		// Update MetaData Values
		metadataHandle.getMetadataValues().remove("Name");
		metadataHandle.getMetadataValues().replace("Company", "MarkLogic", "marklogicians");
		metadataHandle.getMetadataValues().remove("Location", "SanCarlos");
		metadataHandle.withMetadataValue("HQ", "USA");
		metadataHandle.getMetadataValues().merge("HQ", "CA", String::concat);
		writeDocumentUsingBytesHandle(client, filename, "/write-bin-byteshandle-metadata/", metadataHandle, "Binary");
		readMetadataHandle = readMetadataFromDocument(client, "/write-bin-byteshandle-metadata/" + filename, "Binary");
		metadatavalues = readMetadataHandle.getMetadataValues();		
		assertTrue("key=HQ not found in metadata",metadatavalues.containsKey("HQ"));
		assertTrue("value=USACA not found in metadata",metadatavalues.containsValue("USACA"));
		assertFalse("key=Name found after removing it",metadatavalues.containsKey("Name"));
		assertFalse("key=MarkLogic found after removing it",metadatavalues.containsValue("MarkLogic"));
		assertTrue("value=marklogicians not found in metadata",metadatavalues.containsValue("marklogicians"));
		assertFalse("value=SanCarlos found after removing it",metadatavalues.containsValue("SanCarlos"));

		//  patch MetaData Values
		XMLDocumentManager docManager = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBuilder = docManager.newPatchBuilder();
		DocumentPatchHandle patchHandle = patchBuilder.addMetadataValue("State", "California")
				.deleteMetadataValue("HQ")
				.replaceMetadataValue("Company", "test-patch")
				.replaceMetadataValueApply("Locations", patchBuilder.call().add(5))
				.build();
		docManager.patch("/write-bin-byteshandle-metadata/" + filename, patchHandle);
		readMetadataHandle = docManager.readMetadata("/write-bin-byteshandle-metadata/" + filename, readMetadataHandle);
		metadatavalues = readMetadataHandle.getMetadataValues();	
		BytesHandle contentHandle = new BytesHandle();
		assertTrue("value=test-patch not found in metadata",metadatavalues.containsValue("test-patch"));
		assertTrue("value=15 not found in metadata",metadatavalues.containsValue("15"));
		assertFalse("key=HQ found after removing it",metadatavalues.containsKey("HQ"));
		
		// read metadata values using BinaryDocumentManager ReadAs
		DocumentMetadataHandle metaHandle = new DocumentMetadataHandle();
		DocumentMetadataHandle readmetadata = new DocumentMetadataHandle();
		BinaryDocumentManager docman = client.newBinaryDocumentManager();
		docman.readAs("/write-bin-byteshandle-metadata/" + filename, metaHandle,  byte[].class);
		docman.read("/write-bin-byteshandle-metadata/" + filename, readmetadata,contentHandle, 0, 100);
		metadatavalues = metaHandle.getMetadataValues();
		assertTrue(" metadata doesnot contain expected string test-patch",metadatavalues.containsValue("test-patch"));
		assertTrue(" metadata doesnot contain expected string test-patch",metadatavalues.containsValue("test-patch"));
		
		// release the client
		client.release();
	}	
	
	@Test
	public void testMetadataValuesTransaction() throws Exception{
		String filename = "facebook-10443244874876159931";
		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();
		DocumentMetadataValues metadatavalues = readMetadataHandle.getMetadataValues();
		Transaction t1 = null;
		Transaction t2 = null;
		metadataHandle.getMetadataValues().add("key1", "value1");
		metadataHandle.getMetadataValues().add("key2", "value2");
		metadataHandle.getMetadataValues().add("key3", "value3");

		TextDocumentManager docMgr = client.newTextDocumentManager();
		String uri="/trx-jsonhandle-metadatavalues/";
		String docId = uri + filename;
		File file = new File("src/test/java/com/marklogic/client/functionaltest/data/" + filename);
		FileInputStream fis = new FileInputStream(file);
		Scanner scanner = new Scanner(fis).useDelimiter("\\Z");
		String readContent = scanner.next();
		fis.close();
		scanner.close();
		StringHandle   contentHandle = new StringHandle();
		contentHandle.set(readContent);
		// write the doc
		docMgr.writeAs(docId, metadataHandle, contentHandle);
		DocumentUriTemplate template = docMgr.newDocumentUriTemplate("Text").withDirectory("/trx-jsonhandle-metadatavalues-template/");

		try{
			// Trx with metadata values rollback scenario
			t1 = client.openTransaction();
			metadataHandle.getMetadataValues().add("keyTrx1", "valueTrx1");
			docMgr.writeMetadata(docId, metadataHandle, t1);
			docMgr.readMetadata(docId, readMetadataHandle, t1);
			assertTrue(" metadata doesnot contain expected string valueTrx1",metadatavalues.containsValue("valueTrx1"));
			t1.rollback();
			docMgr.readMetadata(docId, readMetadataHandle);
			metadatavalues = readMetadataHandle.getMetadataValues();
			assertFalse(" metadata  contains unexpected string valueTrx1",metadatavalues.containsValue("valueTrx1"));
			
			// Trx with metadata values commit scenario
			t2 = client.openTransaction();
			metadataHandle.getMetadataValues().add("keyTrx2", "valueTrx2");
			DocumentDescriptor desc = docMgr.create(template, metadataHandle, contentHandle, t2);
			String docId1 = desc.getUri();
			docMgr.read(docId1, readMetadataHandle, contentHandle, t2);
			assertTrue(" metadata doesnot contain expected string valueTrx2",metadatavalues.containsValue("valueTrx2"));
			t2.commit();
			docMgr.readAs(docId1, readMetadataHandle, String.class);
			metadatavalues = readMetadataHandle.getMetadataValues();
			assertTrue(" metadata doesnot contains  string 'valueTrx2' after trx commit",metadatavalues.containsValue("valueTrx2"));

			t1=t2=null;
		} catch (Exception e) {			
			e.printStackTrace();	
		}
		finally {
			if (t1 != null ) {
				t1.rollback();
				t1 = null;

			} else if(t2 !=null){
				t2.rollback();
				t2=null;
			}
		}
	}
	
	

	@Test	
	public void testTextMetadataStringHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
	{
		System.out.println("Running testTextMetadataStringHandle");

		String filename = "facebook-10443244874876159931";
		StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

		// connect the client
		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

		// put metadata
		metadataHandle.getCollections().addAll("my-collection");
		metadataHandle.getCollections().addAll("another-collection");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);

		// write the doc with the metadata
		writeDocumentUsingStringHandle(client, filename, "/write-text-stringhandle-metadata/", metadataHandle, "Text");

		// create handle to read metadata
		DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

		// read metadata
		readMetadataHandle = readMetadataFromDocument(client, "/write-text-stringhandle-metadata/" + filename, "Text");

		// get metadata values
		DocumentProperties properties = readMetadataHandle.getProperties();
		DocumentPermissions permissions = readMetadataHandle.getPermissions();
		DocumentCollections collections = readMetadataHandle.getCollections();

		// Properties
		String actualProperties = getDocumentPropertiesString(properties);
		System.out.println("Returned properties: " + actualProperties);

		assertTrue("Document properties difference in size value", actualProperties.contains("size:5"));
		assertTrue("Document property reviewed not found or not correct", actualProperties.contains("reviewed:true"));
		assertTrue("Document property myInteger not found or not correct", actualProperties.contains("myInteger:10"));
		assertTrue("Document property myDecimal not found or not correct", actualProperties.contains("myDecimal:34.56678"));
		assertTrue("Document property myCalendar not found or not correct", actualProperties.contains(calProperty.toString()));
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
		assertTrue("my-collection1 not found", actualCollections.contains("another-collection"));
		assertTrue("my-collection2 not found", actualCollections.contains("my-collection"));	 

		// release the client
		client.release();
	}

	@Test	
	public void testXMLMetadataJAXBHandle() throws KeyManagementException, NoSuchAlgorithmException, JAXBException, IOException
	{
		System.out.println("Running testXMLMetadataJAXBHandle");
		StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

		Product product1 = new Product();
		product1.setName("iPad");
		product1.setIndustry("Hardware");
		product1.setDescription("Very cool device");

		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

		// put metadata
		metadataHandle.getCollections().addAll("my-collection");
		metadataHandle.getCollections().addAll("another-collection");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);
		metadataHandle.getMetadataValues().add("key1", "value1");
		metadataHandle.getMetadataValues().add("key2", "value2");
		metadataHandle.getMetadataValues().add("key3", "value3");


		// write the doc with the metadata
		writeDocumentUsingJAXBHandle(client, product1, "/write-xml-jaxbhandle-metadata/", metadataHandle, "XML");

		// create handle to read metadata
		DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

		// read metadata
		readMetadataHandle = readMetadataFromDocument(client, "/write-xml-jaxbhandle-metadata/" + product1.getName() + ".xml", "XML");

		// get metadata values
		DocumentProperties properties = readMetadataHandle.getProperties();
		DocumentPermissions permissions = readMetadataHandle.getPermissions();
		DocumentCollections collections = readMetadataHandle.getCollections();
		DocumentMetadataValues metadatavalues = readMetadataHandle.getMetadataValues();
		// Properties
		String actualProperties = getDocumentPropertiesString(properties);
		System.out.println("Returned properties: " + actualProperties);

		assertTrue("Document properties difference in size value", actualProperties.contains("size:5"));
		assertTrue("Document property reviewed not found or not correct", actualProperties.contains("reviewed:true"));
		assertTrue("Document property myInteger not found or not correct", actualProperties.contains("myInteger:10"));
		assertTrue("Document property myDecimal not found or not correct", actualProperties.contains("myDecimal:34.56678"));
		assertTrue("Document property myCalendar not found or not correct", actualProperties.contains(calProperty.toString()));
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
		assertTrue("my-collection1 not found", actualCollections.contains("another-collection"));
		assertTrue("my-collection2 not found", actualCollections.contains("my-collection"));	 

		//metadata Values
		assertTrue("key1 does not exist in metadata values", metadatavalues.containsKey("key1"));
		
		
		// release the client
		client.release();
	}

	@Test	
	public void testJSONMetadataOutputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, JAXBException, IOException
	{
		System.out.println("Running testJSONMetadataOutputStreamHandle");

		String filename = "myJSONFile.json";
		StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

		// put metadata
		metadataHandle.getCollections().addAll("my-collection");
		metadataHandle.getCollections().addAll("another-collection");
		metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
		metadataHandle.getProperties().put("reviewed", true);
		metadataHandle.getProperties().put("myString", "foo");
		metadataHandle.getProperties().put("myInteger", 10);
		metadataHandle.getProperties().put("myDecimal", 34.56678);
		metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
		metadataHandle.setQuality(23);
		metadataHandle.getMetadataValues().add("key11", "value11");
		metadataHandle.getMetadataValues().add("key22", "value22");

		// write the doc with the metadata
		writeDocumentUsingOutputStreamHandle(client, filename, "/write-json-outputstreamhandle-metadata/", metadataHandle, "JSON");

		// create handle to read metadata
		DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

		// read metadata
		readMetadataHandle = readMetadataFromDocument(client, "/write-json-outputstreamhandle-metadata/" + filename, "JSON");

		// get metadata values
		DocumentProperties properties = readMetadataHandle.getProperties();
		DocumentPermissions permissions = readMetadataHandle.getPermissions();
		DocumentCollections collections = readMetadataHandle.getCollections();
		DocumentMetadataValues metadatavalues = readMetadataHandle.getMetadataValues();

		// Properties
		String actualProperties = getDocumentPropertiesString(properties);
		System.out.println("Returned properties: " + actualProperties);

		assertTrue("Document properties difference in size value", actualProperties.contains("size:5"));
		assertTrue("Document property reviewed not found or not correct", actualProperties.contains("reviewed:true"));
		assertTrue("Document property myInteger not found or not correct", actualProperties.contains("myInteger:10"));
		assertTrue("Document property myDecimal not found or not correct", actualProperties.contains("myDecimal:34.56678"));
		assertTrue("Document property myCalendar not found or not correct", actualProperties.contains(calProperty.toString()));
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
		assertTrue("my-collection1 not found", actualCollections.contains("another-collection"));
		assertTrue("my-collection2 not found", actualCollections.contains("my-collection"));	
		
		//Metadata values
		assertTrue("Documentdoes not contain metadatvalue::value22",metadatavalues.containsValue("value22") );
		
		// release the client
		client.release();
	}

	@Test	
	public void testJSONMetadataQName() throws KeyManagementException, NoSuchAlgorithmException, JAXBException, IOException
	{
		System.out.println("Running testJSONMetadataQName");

		String filename = "myJSONFile.json";

		DatabaseClient client = getDatabaseClient("rest-writer", "x", Authentication.DIGEST);

		// create and initialize a handle on the metadata
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

		// put metadata
		metadataHandle.getProperties().put(new QName("http://www.example.com", "foo"), "bar"); 

		// write the doc with the metadata
		writeDocumentUsingOutputStreamHandle(client, filename, "/write-json-outputstreamhandle-metadata/", metadataHandle, "JSON");

		// create handle to read metadata
		DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

		// read metadata
		readMetadataHandle = readMetadataFromDocument(client, "/write-json-outputstreamhandle-metadata/" + filename, "JSON");

		// get metadata values
		DocumentProperties properties = readMetadataHandle.getProperties();

		// Properties
		String expectedProperties = "size:1|{http://www.example.com}foo:bar|";
		String actualProperties = getDocumentPropertiesString(properties);
		System.out.println(actualProperties);
		assertEquals("Document properties difference", expectedProperties, actualProperties);

		// release the client
		client.release();
	}

	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		cleanupRESTServer(dbName, fNames);
	}
}
