package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.javaclient.BasicJavaClientREST;
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

	@SuppressWarnings("deprecation")
	@Test
	public void testBinaryMetadataBytesHandle() throws IOException
	{
		System.out.println("Running testBinaryMetadataBytesHandle");
		
		String filename = "Simple_ScanTe.png";
		
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
	    String expectedProperties = "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
	    String actualProperties = getDocumentPropertiesString(properties);
	    System.out.println("Actual Prop : "+actualProperties);
	    assertEquals("Document properties difference", expectedProperties, actualProperties);
	    
	    // Permissions
	    String expectedPermissions1 = "size:3|rest-reader:[READ]|app-user:[UPDATE, READ]|rest-writer:[UPDATE]|";
	    String expectedPermissions2 = "size:3|rest-reader:[READ]|app-user:[READ, UPDATE]|rest-writer:[UPDATE]|";
	    String actualPermissions = getDocumentPermissionsString(permissions);
	    if(actualPermissions.contains("[UPDATE, READ]"))
	    	assertEquals("Document permissions difference", expectedPermissions1, actualPermissions);
	    else if(actualPermissions.contains("[READ, UPDATE]"))
	    	assertEquals("Document permissions difference", expectedPermissions2, actualPermissions);
	    else
	    	assertEquals("Document permissions difference", "wrong", actualPermissions);
	    
	    // Collections 
	    String expectedCollections = "size:2|another-collection|my-collection|";
	    String actualCollections = getDocumentCollectionsString(collections);
	    assertEquals("Document collections difference", expectedCollections, actualCollections);
	    
	    // release the client
	    client.release();
	}	
	

	@SuppressWarnings("deprecation")
	@Test	public void testTextMetadataStringHandle() throws IOException
	{
		System.out.println("Running testTextMetadataStringHandle");
		
		String filename = "facebook-10443244874876159931";
		
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
	    String expectedProperties = "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
	    String actualProperties = getDocumentPropertiesString(properties);
	    assertEquals("Document properties difference", expectedProperties, actualProperties);
	    
	    // Permissions
	    String expectedPermissions1 = "size:3|rest-reader:[READ]|app-user:[UPDATE, READ]|rest-writer:[UPDATE]|";
	    String expectedPermissions2 = "size:3|rest-reader:[READ]|app-user:[READ, UPDATE]|rest-writer:[UPDATE]|";
	    String actualPermissions = getDocumentPermissionsString(permissions);
	    if(actualPermissions.contains("[UPDATE, READ]"))
	    	assertEquals("Document permissions difference", expectedPermissions1, actualPermissions);
	    else if(actualPermissions.contains("[READ, UPDATE]"))
	    	assertEquals("Document permissions difference", expectedPermissions2, actualPermissions);
	    else
	    	assertEquals("Document permissions difference", "wrong", actualPermissions);
	    
	    // Collections 
	    String expectedCollections = "size:2|another-collection|my-collection|";
	    String actualCollections = getDocumentCollectionsString(collections);
	    assertEquals("Document collections difference", expectedCollections, actualCollections);
	    
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testXMLMetadataJAXBHandle() throws JAXBException
	{
		System.out.println("Running testXMLMetadataJAXBHandle");
		
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
	    String expectedProperties = "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
	    String actualProperties = getDocumentPropertiesString(properties);
	    assertEquals("Document properties difference", expectedProperties, actualProperties);
	    
	    // Permissions
	    String expectedPermissions1 = "size:3|rest-reader:[READ]|app-user:[UPDATE, READ]|rest-writer:[UPDATE]|";
	    String expectedPermissions2 = "size:3|rest-reader:[READ]|app-user:[READ, UPDATE]|rest-writer:[UPDATE]|";
	    String actualPermissions = getDocumentPermissionsString(permissions);
	    if(actualPermissions.contains("[UPDATE, READ]"))
	    	assertEquals("Document permissions difference", expectedPermissions1, actualPermissions);
	    else if(actualPermissions.contains("[READ, UPDATE]"))
	    	assertEquals("Document permissions difference", expectedPermissions2, actualPermissions);
	    else
	    	assertEquals("Document permissions difference", "wrong", actualPermissions);
	    
	    // Collections 
	    String expectedCollections = "size:2|another-collection|my-collection|";
	    String actualCollections = getDocumentCollectionsString(collections);
	    assertEquals("Document collections difference", expectedCollections, actualCollections);
	    
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testJSONMetadataOutputStreamHandle() throws JAXBException
	{
		System.out.println("Running testJSONMetadataOutputStreamHandle");
		
		String filename = "myJSONFile.json";
		
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
	    String expectedProperties = "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
	    String actualProperties = getDocumentPropertiesString(properties);
	    assertEquals("Document properties difference", expectedProperties, actualProperties);
	    
	    // Permissions
	    String expectedPermissions1 = "size:3|rest-reader:[READ]|app-user:[UPDATE, READ]|rest-writer:[UPDATE]|";
	    String expectedPermissions2 = "size:3|rest-reader:[READ]|app-user:[READ, UPDATE]|rest-writer:[UPDATE]|";
	    String actualPermissions = getDocumentPermissionsString(permissions);
	    if(actualPermissions.contains("[UPDATE, READ]"))
	    	assertEquals("Document permissions difference", expectedPermissions1, actualPermissions);
	    else if(actualPermissions.contains("[READ, UPDATE]"))
	    	assertEquals("Document permissions difference", expectedPermissions2, actualPermissions);
	    else
	    	assertEquals("Document permissions difference", "wrong", actualPermissions);
	    
	    // Collections 
	    String expectedCollections = "size:2|another-collection|my-collection|";
	    String actualCollections = getDocumentCollectionsString(collections);
	    assertEquals("Document collections difference", expectedCollections, actualCollections);
	    
	    // release the client
	    client.release();
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testJSONMetadataQName() throws JAXBException
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
