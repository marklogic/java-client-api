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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.functionaltest.BasicJavaClientREST;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;

import org.junit.*;
public class TestMetadata extends BasicJavaClientREST{

	private static String dbName = "TestMetadataDB";
	private static String [] fNames = {"TestMetadataDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";

	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}

	@Test
	public void testBinaryMetadataBytesHandle() throws IOException
	{
		System.out.println("Running testBinaryMetadataBytesHandle");

		String filename = "Simple_ScanTe.png";

		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
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
	public void testTextMetadataStringHandle() throws IOException
	{
		System.out.println("Running testTextMetadataStringHandle");

		String filename = "facebook-10443244874876159931";
		StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);

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
	public void testXMLMetadataJAXBHandle() throws JAXBException
	{
		System.out.println("Running testXMLMetadataJAXBHandle");
		StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);

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
	public void testJSONMetadataOutputStreamHandle() throws JAXBException
	{
		System.out.println("Running testJSONMetadataOutputStreamHandle");

		String filename = "myJSONFile.json";
		StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);

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
		writeDocumentUsingOutputStreamHandle(client, filename, "/write-json-outputstreamhandle-metadata/", metadataHandle, "JSON");

		// create handle to read metadata
		DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();

		// read metadata
		readMetadataHandle = readMetadataFromDocument(client, "/write-json-outputstreamhandle-metadata/" + filename, "JSON");

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
	public void testJSONMetadataQName() throws JAXBException
	{
		System.out.println("Running testJSONMetadataQName");

		String filename = "myJSONFile.json";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);

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
		tearDownJavaRESTServer(dbName, fNames,  restServerName);

	}
}
