package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.Policy;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.document.XMLDocumentManager;
import org.junit.*;
public class TestOptimisticLocking extends BasicJavaClientREST{

	private static String dbName = "TestOptimisticLockingDB";
	private static String [] fNames = {"TestOptimisticLockingDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	@BeforeClass
	public static void setUp() throws Exception
	{
		System.out.println("In setup");
		
		setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testRequired() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testRequired");
		
		String filename = "xml-original.xml";
		String updateFilename = "xml-updated.xml";
		String uri = "/optimistic-locking/";
		String docId = uri + filename;
		long badVersion = 1111;
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create a manager for the server configuration
		ServerConfigurationManager configMgr = client.newServerConfigManager();

		// read the server configuration from the database
		configMgr.readConfiguration();

		// require content versions for updates and deletes
		// use Policy.OPTIONAL to allow but not require versions
		configMgr.setContentVersionRequests(Policy.REQUIRED);

		// write the server configuration to the database
		configMgr.writeConfiguration();

		System.out.println("set optimistic locking to required");
		
		// create document manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		// create document descriptor
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
		
		desc.setVersion(badVersion);
		
		String exception = "";
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";
		
		// CREATE
		// write document with bad version
		try 
		{
			docMgr.write(desc, handle);
		} catch (FailedRequestException e) { exception = e.toString(); }
		
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", isExceptionThrown);
		
		// write document with unknown version
		desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
		docMgr.write(desc, handle);
		
		StringHandle readHandle = new StringHandle();
		docMgr.read(desc, readHandle);
		String content = readHandle.get();
		assertTrue("Wrong content", content.contains("<name>noodle</name>"));
		    	
    	// get the good version
    	long goodVersion = desc.getVersion();
    	
    	System.out.println("version before create: " + goodVersion);
		
    	// UPDATE
		File updateFile = new File("src/test/java/com/marklogic/javaclient/data/" + updateFilename);

		// create a handle on the content
		FileHandle updateHandle = new FileHandle(updateFile);
		updateHandle.set(updateFile);
    	
    	// update with bad version
    	desc.setVersion(badVersion);
    	
		String updateException = "";
		String expectedUpdateException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";

    	try
    	{
    		docMgr.write(desc, updateHandle);
    	} catch (FailedRequestException e) { updateException = e.toString(); }
    	
		boolean isUpdateExceptionThrown = updateException.contains(expectedUpdateException);
		assertTrue("Exception is not thrown", isUpdateExceptionThrown);
		
		// update with unknown version
		desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);

		String updateUnknownException = "";
		String expectedUpdateUnknownException = "com.marklogic.client.FailedRequestException: Local message: Content version required to write document. Server Message: You do not have permission to this method and URL";

		try
		{
			docMgr.write(desc, updateHandle);
		} catch (FailedRequestException e) { updateUnknownException = e.toString(); }
		
		boolean isUpdateUnknownExceptionThrown = updateUnknownException.contains(expectedUpdateUnknownException);
		assertTrue("Exception is not thrown", isUpdateUnknownExceptionThrown);

		desc = docMgr.exists(docId);
		goodVersion = desc.getVersion();
		
		System.out.println("version before update: " + goodVersion);
		
    	// update with good version
		desc.setVersion(goodVersion);
		docMgr.write(desc, updateHandle);
		
		StringHandle updateReadHandle = new StringHandle();
		docMgr.read(desc, updateReadHandle);
		String updateContent = updateReadHandle.get();
		assertTrue("Wrong content", updateContent.contains("<name>fried noodle</name>"));
		
		// DELETE
		// delete using bad version
		desc.setVersion(badVersion);
		
		String deleteException = "";
		String expectedDeleteException = "com.marklogic.client.FailedRequestException: Content version must match to delete document";
		
		try
		{
			docMgr.delete(desc);
		} catch (FailedRequestException e) { deleteException = e.toString(); }
		
		boolean isDeleteExceptionThrown = deleteException.contains(expectedDeleteException);
		assertTrue("Exception is not thrown", isDeleteExceptionThrown);
		
		// delete using unknown version
		desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
		
		String deleteUnknownException = "";
		String expectedDeleteUnknownException = "com.marklogic.client.FailedRequestException: Local message: Content version required to delete document. Server Message: You do not have permission to this method and URL";
		
		try
		{
			docMgr.delete(desc);
		} catch (FailedRequestException e) { deleteUnknownException = e.toString(); }
		
		boolean isDeleteUnknownExceptionThrown = deleteUnknownException.contains(expectedDeleteUnknownException);
		assertTrue("Exception is not thrown", isDeleteUnknownExceptionThrown);
		
		// delete using good version
		desc = docMgr.exists(docId);
		goodVersion = desc.getVersion();
		
		System.out.println("version before delete: " + goodVersion);
		
		docMgr.delete(desc);
		
		String verifyDeleteException = "";
		String expectedVerifyDeleteException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document";
		
		StringHandle deleteHandle = new StringHandle();
		try
		{
			docMgr.read(desc, deleteHandle);
		} catch (ResourceNotFoundException e) { verifyDeleteException = e.toString(); }
		
		boolean isVerifyDeleteExceptionThrown = verifyDeleteException.contains(expectedVerifyDeleteException);
		assertTrue("Exception is not thrown", isVerifyDeleteExceptionThrown);
		
		// release client
		client.release();
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(configMgr.getContentVersionRequests());

	}

	@SuppressWarnings("deprecation")
	@Test	
	public void testOptionalWithUnknownVersion() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testOptionalWithUnknownVersion");
		
		String filename = "json-original.json";
		String updateFilename = "json-updated.json";
		String uri = "/optimistic-locking/";
		String docId = uri + filename;
		long badVersion = 1111;
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create a manager for the server configuration
		ServerConfigurationManager configMgr = client.newServerConfigManager();

		// read the server configuration from the database
		configMgr.readConfiguration();

		// use Policy.OPTIONAL to allow but not require versions
		configMgr.setContentVersionRequests(Policy.OPTIONAL);

		// write the server configuration to the database
		configMgr.writeConfiguration();

		System.out.println("set optimistic locking to optional");
		
		// create document manager
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		// create document descriptor
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
		
		desc.setVersion(badVersion);
		
		String exception = "";
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";
		
		// CREATE
		// write document with bad version
		try 
		{
			docMgr.write(desc, handle);
		} catch (FailedRequestException e) { exception = e.toString(); }
		
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", isExceptionThrown);
		
		// write document with unknown version
		desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
		docMgr.write(desc, handle);
		
		StringHandle readHandle = new StringHandle();
		docMgr.read(desc, readHandle);
		String content = readHandle.get();
		assertTrue("Wrong content", content.contains("John"));
		    	
    	// get the unknown version
    	long unknownVersion = desc.getVersion();
    	
    	System.out.println("unknown version after create: " + unknownVersion);
		
    	// UPDATE
		File updateFile = new File("src/test/java/com/marklogic/javaclient/data/" + updateFilename);

		// create a handle on the content
		FileHandle updateHandle = new FileHandle(updateFile);
		updateHandle.set(updateFile);
    	
    	// update with bad version
    	desc.setVersion(badVersion);
    	
		String updateException = "";
		String expectedUpdateException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";

    	try
    	{
    		docMgr.write(desc, updateHandle);
    	} catch (FailedRequestException e) { updateException = e.toString(); }
    	
		boolean isUpdateExceptionThrown = updateException.contains(expectedUpdateException);
		assertTrue("Exception is not thrown", isUpdateExceptionThrown);
		
		// update with unknown version
		desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
		
		docMgr.write(desc, updateHandle);
				
		StringHandle updateReadHandle = new StringHandle();
		docMgr.read(desc, updateReadHandle);
		String updateContent = updateReadHandle.get();
		assertTrue("Wrong content", updateContent.contains("Aries"));
		
		unknownVersion = desc.getVersion();
    	
    	System.out.println("unknown version after update: " + unknownVersion);
    	
    	// read using matched version
    	desc.setVersion(unknownVersion);
    	StringHandle readMatchHandle = new StringHandle();
    	docMgr.read(desc, readMatchHandle);
    	String readMatchContent = readMatchHandle.get();
    	assertTrue("Document does not return null", readMatchContent == null);
    	
		// DELETE
		// delete using bad version
		desc.setVersion(badVersion);
		
		String deleteException = "";
		String expectedDeleteException = "com.marklogic.client.FailedRequestException: Content version must match to delete document";
		
		try
		{
			docMgr.delete(desc);
		} catch (FailedRequestException e) { deleteException = e.toString(); }
		
		boolean isDeleteExceptionThrown = deleteException.contains(expectedDeleteException);
		assertTrue("Exception is not thrown", isDeleteExceptionThrown);
		
		// delete using unknown version
		desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);		
		docMgr.delete(desc);
		
		unknownVersion = desc.getVersion();
		
		System.out.println("unknown version after delete: " + unknownVersion);
		
		String verifyDeleteException = "";
		String expectedVerifyDeleteException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document";
		
		StringHandle deleteHandle = new StringHandle();
		try
		{
			docMgr.read(desc, deleteHandle);
		} catch (ResourceNotFoundException e) { verifyDeleteException = e.toString(); }
		
		boolean isVerifyDeleteExceptionThrown = verifyDeleteException.contains(expectedVerifyDeleteException);
		assertTrue("Exception is not thrown", isVerifyDeleteExceptionThrown);
		
		// release client
		client.release();
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(configMgr.getContentVersionRequests());

	}

	@SuppressWarnings("deprecation")
	@Test	
	public void testOptionalWithGoodVersion() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testOptionalWithGoodVersion");
		
		String filename = "json-original.json";
		String updateFilename = "json-updated.json";
		String uri = "/optimistic-locking/";
		String docId = uri + filename;
		long badVersion = 1111;
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create a manager for the server configuration
		ServerConfigurationManager configMgr = client.newServerConfigManager();

		// read the server configuration from the database
		configMgr.readConfiguration();

		// use Policy.OPTIONAL to allow but not require versions
		configMgr.setContentVersionRequests(Policy.OPTIONAL);

		// write the server configuration to the database
		configMgr.writeConfiguration();

		System.out.println("set optimistic locking to optional");
		
		// create document manager
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		// create document descriptor
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
		
		desc.setVersion(badVersion);
		
		String exception = "";
		String expectedException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";
		
		// CREATE
		// write document with bad version
		try 
		{
			docMgr.write(desc, handle);
		} catch (FailedRequestException e) { exception = e.toString(); }
		
		boolean isExceptionThrown = exception.contains(expectedException);
		assertTrue("Exception is not thrown", isExceptionThrown);
		
		// write document with unknown version
		desc.setVersion(DocumentDescriptor.UNKNOWN_VERSION);
		docMgr.write(desc, handle);
		
		StringHandle readHandle = new StringHandle();
		docMgr.read(desc, readHandle);
		String content = readHandle.get();
		assertTrue("Wrong content", content.contains("John"));
		    	
    	// get the good version
    	long goodVersion = desc.getVersion();
    	
    	System.out.println("good version after create: " + goodVersion);
		
    	// UPDATE
		File updateFile = new File("src/test/java/com/marklogic/javaclient/data/" + updateFilename);

		// create a handle on the content
		FileHandle updateHandle = new FileHandle(updateFile);
		updateHandle.set(updateFile);
    	
    	// update with bad version
    	desc.setVersion(badVersion);
    	
		String updateException = "";
		String expectedUpdateException = "com.marklogic.client.FailedRequestException: Local message: Content version must match to write document. Server Message: RESTAPI-CONTENTWRONGVERSION";

    	try
    	{
    		docMgr.write(desc, updateHandle);
    	} catch (FailedRequestException e) { updateException = e.toString(); }
    	
		boolean isUpdateExceptionThrown = updateException.contains(expectedUpdateException);
		assertTrue("Exception is not thrown", isUpdateExceptionThrown);
		
		// update with good version
		desc.setVersion(goodVersion);
		
		docMgr.write(desc, updateHandle);
				
		StringHandle updateReadHandle = new StringHandle();
		docMgr.read(desc, updateReadHandle);
		String updateContent = updateReadHandle.get();
		assertTrue("Wrong content", updateContent.contains("Aries"));
		
		goodVersion = desc.getVersion();
    	
    	System.out.println("good version after update: " + goodVersion);
    	
    	// read using matched version
    	desc.setVersion(goodVersion);
    	StringHandle readMatchHandle = new StringHandle();
    	docMgr.read(desc, readMatchHandle);
    	String readMatchContent = readMatchHandle.get();
    	assertTrue("Document does not return null", readMatchContent == null);
    	
		// DELETE
		// delete using bad version
		desc.setVersion(badVersion);
		
		String deleteException = "";
		String expectedDeleteException = "com.marklogic.client.FailedRequestException: Content version must match to delete document";
		
		try
		{
			docMgr.delete(desc);
		} catch (FailedRequestException e) { deleteException = e.toString(); }
		
		boolean isDeleteExceptionThrown = deleteException.contains(expectedDeleteException);
		assertTrue("Exception is not thrown", isDeleteExceptionThrown);
		
		// delete using good version
		desc.setVersion(goodVersion);		
		docMgr.delete(desc);
		
		goodVersion = desc.getVersion();
		
		System.out.println("unknown version after delete: " + goodVersion);
		
		String verifyDeleteException = "";
		String expectedVerifyDeleteException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document";
		
		StringHandle deleteHandle = new StringHandle();
		try
		{
			docMgr.read(desc, deleteHandle);
		} catch (ResourceNotFoundException e) { verifyDeleteException = e.toString(); }
		
		boolean isVerifyDeleteExceptionThrown = verifyDeleteException.contains(expectedVerifyDeleteException);
		assertTrue("Exception is not thrown", isVerifyDeleteExceptionThrown);
		
		// release client
		client.release();
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(configMgr.getContentVersionRequests());
	}
	

	@SuppressWarnings("deprecation")
	@Test	public void testNone() throws IOException, ParserConfigurationException, SAXException, XpathException
	{
		System.out.println("Running testNone");
		
		String filename = "json-original.json";
		String updateFilename = "json-updated.json";
		String uri = "/optimistic-locking/";
		String docId = uri + filename;
		long badVersion = 1111;
				
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// create a manager for the server configuration
		ServerConfigurationManager configMgr = client.newServerConfigManager();

		// read the server configuration from the database
		configMgr.readConfiguration();

		// use Policy.NONE
		configMgr.setContentVersionRequests(Policy.NONE);

		// write the server configuration to the database
		configMgr.writeConfiguration();

		System.out.println("set optimistic locking to none");
		
		// create document manager
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		
		File file = new File("src/test/java/com/marklogic/javaclient/data/" + filename);

		// create a handle on the content
		FileHandle handle = new FileHandle(file);
		handle.set(file);
		
		// create document descriptor
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
		
		desc.setVersion(badVersion);
		
		// CREATE
		// write document with bad version
		
		docMgr.write(desc, handle);
		
		StringHandle readHandle = new StringHandle();
		docMgr.read(desc, readHandle);
		String content = readHandle.get();
		assertTrue("Wrong content", content.contains("John"));
		    			
    	// UPDATE
		File updateFile = new File("src/test/java/com/marklogic/javaclient/data/" + updateFilename);

		// create a handle on the content
		FileHandle updateHandle = new FileHandle(updateFile);
		updateHandle.set(updateFile);
    	
    	// update with bad version
    	desc.setVersion(badVersion);
    	
    	docMgr.write(desc, updateHandle);
				
		StringHandle updateReadHandle = new StringHandle();
		docMgr.read(desc, updateReadHandle);
		String updateContent = updateReadHandle.get();
		assertTrue("Wrong content", updateContent.contains("Aries"));
		    	
		// DELETE
		// delete using bad version
		desc.setVersion(badVersion);
		
		docMgr.delete(desc);
		
		String verifyDeleteException = "";
		String expectedVerifyDeleteException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document";
		
		StringHandle deleteHandle = new StringHandle();
		try
		{
			docMgr.read(desc, deleteHandle);
		} catch (ResourceNotFoundException e) { verifyDeleteException = e.toString(); }
		
		boolean isVerifyDeleteExceptionThrown = verifyDeleteException.contains(expectedVerifyDeleteException);
		assertTrue("Exception is not thrown", isVerifyDeleteExceptionThrown);
		
		// release client
		client.release();
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(configMgr.getContentVersionRequests());
	}
	@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
