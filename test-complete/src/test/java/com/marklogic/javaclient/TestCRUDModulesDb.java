package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ExtensionLibraryDescriptor;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.DatabaseClient;
import org.junit.*;
public class TestCRUDModulesDb extends BasicJavaClientREST {

	private static String dbName = "Modules";
	private static String [] fNames = {"Modules"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	System.out.println("In setup");
	assocRESTServer(restServerName, dbName,8011);
	}

@SuppressWarnings("deprecation")
@Test
	public void testXQueryModuleCRUDDuplicateFile()
	{	
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// get a manager
		//ExtensionLibrariesManager libsMgr = Common.client.newServerConfigManager().newExtensionLibrariesManager();
		ExtensionLibrariesManager libsMgr = client.newServerConfigManager().newExtensionLibrariesManager();

		String Path = "/ext/my/path/to/my/module.xqy";
		
		// write XQuery file to the modules database
		libsMgr.write(Path, new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/module.xqy")).withFormat(Format.TEXT));

		// read it back
		String xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		assertTrue("module read and read back", xqueryModuleAsString.startsWith("xquery version \"1.0-ml\";"));
		
		// write Duplicate XQuery file to the modules database with different content
		libsMgr.write(Path, new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/module.xqy")).withFormat(Format.TEXT));
		
		// read it back to check overwritten
		String xqueryModuleAsDuplicateString = libsMgr.read(Path, new StringHandle()).get();
		assertTrue("module read and read back", xqueryModuleAsDuplicateString.startsWith("xquery version \"1.0-ml\";"));
		
		// get the list of descriptors
		ExtensionLibraryDescriptor[] descriptors = libsMgr.list();
		assertEquals("number of modules installed", descriptors.length, 1);
		
		for (ExtensionLibraryDescriptor descriptor : descriptors) {
			assertEquals(descriptor.getPath(), Path);
		}

		// delete it
		libsMgr.delete(Path);
		
		try {
			// read deleted module
			xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		} catch (ResourceNotFoundException e) {
			// pass;
		}
	
	}

@SuppressWarnings("deprecation")
@Test	public void testXQueryModuleCRUDDifferentPath() {
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// get a manager
		ExtensionLibrariesManager libsMgr = client.newServerConfigManager().newExtensionLibrariesManager();
		String firstPath = "/ext/my/path/to/my/module.xqy";
		String secondPath = "/ext/my/path/to/my/other/module.xqy" ;
		// write XQuery file to the modules database
		libsMgr.write(firstPath, new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/module.xqy")).withFormat(Format.TEXT));
		
		// write XQuery file to the modules database Different Path 
		libsMgr.write(secondPath, new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/module.xqy")).withFormat(Format.TEXT));
		
		// read 1st file back
		String xqueryModuleAsString = libsMgr.read(firstPath, new StringHandle()).get();
		assertTrue("module read and read back", xqueryModuleAsString.startsWith("xquery version \"1.0-ml\";"));
		
		// read 2nd file back
		String xqueryModuleAsString1 = libsMgr.read(secondPath, new StringHandle()).get();
		assertTrue("module read and read back", xqueryModuleAsString1.startsWith("xquery version \"1.0-ml\";"));
		
		// get the list of descriptors
		ExtensionLibraryDescriptor[] descriptors = libsMgr.list();
		assertEquals("number of modules installed", descriptors.length, 2);
		assertEquals(descriptors[0].getPath(), firstPath );
		assertEquals(descriptors[1].getPath(), secondPath);
		
		// delete it
		libsMgr.delete(firstPath );
		libsMgr.delete(secondPath);
		
		try {
			// read deleted module
			xqueryModuleAsString = libsMgr.read(firstPath, new StringHandle()).get();
			xqueryModuleAsString = libsMgr.read(secondPath, new StringHandle()).get();
		} catch (ResourceNotFoundException e) {
			// pass;
		}
		
	}

@SuppressWarnings("deprecation")
@Test
public void testXQueryModuleCRUDBinaryFile() {
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// get a manager
		ExtensionLibrariesManager libsMgr = client.newServerConfigManager().newExtensionLibrariesManager();
		
		String Path = "/ext/my/path/to/my/module.xqy";
		
		// write XQuery file to the modules database
		libsMgr.write(Path, new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/binary.jpg")).withFormat(Format.BINARY));

		// read it back
			FileHandle f = new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/binary.jpg"));
		assertEquals(f.getByteLength(),libsMgr.read(Path, new StringHandle()).getByteLength());
		
		// get the list of descriptors
		ExtensionLibraryDescriptor[] descriptors = libsMgr.list();
		assertEquals("number of modules installed", descriptors.length, 1);
		
		for (ExtensionLibraryDescriptor descriptor : descriptors) {
			assertEquals(descriptor.getPath(), Path);
		}

		// delete it
		libsMgr.delete(Path);
		
		try {
			// read deleted module
		String xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		} catch (ResourceNotFoundException e) {
			// pass;
		}
		
	}

@SuppressWarnings("deprecation")
@Test	public void testXQueryModuleCRUDTextFile() {
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// get a manager
		ExtensionLibrariesManager libsMgr = client.newServerConfigManager().newExtensionLibrariesManager();
		
		String Path = "/ext/my/path/to/my/module.xqy";
		
		// write XQuery file to the modules database
		libsMgr.write(Path, new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/readme.txt")).withFormat(Format.TEXT));

		// read it back
		String xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		assertTrue("module read and read back", xqueryModuleAsString.startsWith("Copyright 2012 MarkLogic Corporation"));
		
		// get the list of descriptors
		ExtensionLibraryDescriptor[] descriptors = libsMgr.list();
		assertEquals("number of modules installed", descriptors.length, 1);
		
		for (ExtensionLibraryDescriptor descriptor : descriptors) {
			assertEquals(descriptor.getPath(), Path);
		}

		// delete it
		libsMgr.delete(Path);

		try {
			// read deleted module
		 xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		} catch (ResourceNotFoundException e) {
			// pass;
		}
		
	}

@SuppressWarnings("deprecation")
@Test	public void testXQueryModuleCRUDXmlFile() {
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// get a manager
		ExtensionLibrariesManager libsMgr = client.newServerConfigManager().newExtensionLibrariesManager();
		
		String Path = "/ext/my/path/to/my/module.xqy";
		FileHandle f = new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/all_well.xml")).withFormat(Format.XML);

		// write XQuery file to the modules database
		libsMgr.write(Path, f);

		// read it back
		assertEquals(f.getByteLength(), libsMgr.read(Path, new StringHandle()).getByteLength());
		
		// get the list of descriptors
		ExtensionLibraryDescriptor[] descriptors = libsMgr.list();
		assertEquals("number of modules installed", descriptors.length, 1);
		
		for (ExtensionLibraryDescriptor descriptor : descriptors) {
		assertEquals(descriptor.getPath(), Path);
		}

		// delete it
		libsMgr.delete(Path);
		
		try {
			// read deleted module
		 String xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		} catch (ResourceNotFoundException e) {
			// pass;
		}
		
	}

@SuppressWarnings("deprecation")
@Test	public void testXQueryModuleReadModulesDb() {
		
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// get a manager
		ExtensionLibrariesManager libsMgr = client.newServerConfigManager().newExtensionLibrariesManager();
		
		String Path = "/ext/my/path/to/my/module.xqy";
		FileHandle f = new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/module.xqy")).withFormat(Format.TEXT);

		// write XQuery file to the modules database
		libsMgr.write(Path, f);

		// read it back

		String xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		assertTrue("module read and read back", xqueryModuleAsString.contains("let $x := (1,2,3,4,5)"));
			
		// get the list of descriptors
		ExtensionLibraryDescriptor[] descriptors = libsMgr.list();	
		assertEquals("number of modules installed", descriptors.length, 1);
		
		for (ExtensionLibraryDescriptor descriptor : descriptors) {
			assertEquals(descriptor.getPath(), Path);
			System.out.println("Path returned by Descriptor "+ descriptor.getPath());
		}
		System.out.println("Path"+Path);
		// delete it
		libsMgr.delete(Path);
		try {
			// read deleted module
		xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		} catch (ResourceNotFoundException e) {
			System.out.println("Reading deleted file Failed");
			// pass;
		}
		try{
			libsMgr.delete(Path);
		}catch(Exception e){
			System.out.println("Attempt to Delete Non exsting file Failed");
			e.printStackTrace();
		}
		
	}

@SuppressWarnings("deprecation")
@Test
public void testXQueryModuleReadExtensionLibraryDescriptor () {
		System.out.println("testXQueryModuleReadExtensionLibraryDescriptor");
		
DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		
		// get a manager
		ExtensionLibrariesManager libsMgr = client.newServerConfigManager().newExtensionLibrariesManager();
		
		String Path = "/ext/my/path/to/my/module.xqy";
		FileHandle f = new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/module.xqy")).withFormat(Format.TEXT);

		// write XQuery file to the modules database
		libsMgr.write(Path, f);

		// read it back

		String xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		assertTrue("module read and read back", xqueryModuleAsString.contains("let $x := (1,2,3,4,5)"));
			
		// get the list of descriptors
		ExtensionLibraryDescriptor[] descriptors = libsMgr.list();	
		assertEquals("number of modules installed", descriptors.length, 1);
		
		for (ExtensionLibraryDescriptor descriptor : descriptors) {
			descriptor.setPath("/ext/my/path/to/my/new/module.xqy");
			String xqueryModuleAsStringNew = libsMgr.read(Path, new StringHandle()).get();		
			System.out.println("Path returned by Descriptor "+ descriptor.getPath()+"Document returned by Descriptor"+xqueryModuleAsStringNew);
			libsMgr.delete(descriptor.getPath());
		}
		
		// delete it
		libsMgr.delete(Path);

		try {
			// read deleted module
		xqueryModuleAsString = libsMgr.read(Path, new StringHandle()).get();
		} catch (ResourceNotFoundException e) {
			System.out.println("Reading deleted file Failed");
			// pass;
		}
		try{
			libsMgr.delete(Path);
		}catch(Exception e){
			System.out.println("Attempt to Delete Non exsting file Failed");
			e.printStackTrace();
		}
		
	}

@SuppressWarnings("deprecation")
@Test
public void testXQueryModuleCRUDXmlFileNegative() {
	
	DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
	
	// get a manager
	ExtensionLibrariesManager libsMgr = client.newServerConfigManager().newExtensionLibrariesManager();
	
	String Path = "/foo/my/path/to/my/module.xqy";
	FileHandle f = new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/all_well.xml")).withFormat(Format.XML);

	// write XQuery file to the modules database
	try{
	libsMgr.write(Path, f);
	}catch(ResourceNotFoundException e){
		assertEquals("Negative test Passed as","Local message: Could not write resource at /foo/my/path/to/my/module.xqy. Server Message: /v1/foo/my/path/to/my/module.xqy" , e.getMessage());
	}
	// delete it
	try{
		libsMgr.delete(Path);
	}catch(Exception e){
		assertEquals("", "Local message: Could not delete resource at /foo/my/path/to/my/module.xqy. Server Message: /v1/foo/my/path/to/my/module.xqy", e.getMessage());
	}
		
}
@AfterClass
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		try{
			deleteRESTServer(restServerName); 
		}catch(Exception e){
			e.printStackTrace(); 
		}	
	}
}

