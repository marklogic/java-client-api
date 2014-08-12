package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentMetadataPatchBuilder.Cardinality;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import org.junit.*;

public class TestPatchCardinality extends BasicJavaClientREST {

	private static String dbName = "TestPatchCardinalityDB";
	private static String [] fNames = {"TestPatchCardinalityDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}
	

@SuppressWarnings("deprecation")
@Test	public void testOneCardinalityNegative() throws IOException
	{	
		System.out.println("Running testOneCardinalityNegative");
		
		String[] filenames = {"cardinal1.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
		}
		
		String docId = "/cardinal/cardinal1.xml";
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ONE, "<bar>added</bar>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		
		String exception = "";
		try
		{
			docMgr.patch(docId, patchHandle);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			exception = e.getMessage();
		}
		
		String expectedException = "Local message: write failed: Bad Request. Server Message: RESTAPI-INVALIDREQ: (err:FOER0000) Invalid request:  reason: invalid content patch operations for uri /cardinal/cardinal1.xml: invalid cardinality of 5 nodes for: /root/foo";
		
		assertTrue("Exception is not thrown", exception.contains(expectedException));
				
		// release client
		client.release();		
	}


@SuppressWarnings("deprecation")
@Test	public void testOneCardinalityPositve() throws IOException
	{	
		System.out.println("Running testOneCardinalityPositive");
		
		String[] filenames = {"cardinal2.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
		}
		
		String docId = "/cardinal/cardinal2.xml";
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ONE, "<bar>added</bar>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertTrue("fragment is not inserted", content.contains("<bar>added</bar>"));
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testOneOrMoreCardinalityPositve() throws IOException
	{	
		System.out.println("Running testOneOrMoreCardinalityPositive");
		
		String[] filenames = {"cardinal1.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
		}
		
		String docId = "/cardinal/cardinal1.xml";
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ONE_OR_MORE, "<bar>added</bar>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertTrue("fragment is not inserted", content.contains("<foo>one</foo><bar>added</bar>"));
		assertTrue("fragment is not inserted", content.contains("<foo>two</foo><bar>added</bar>"));
		assertTrue("fragment is not inserted", content.contains("<foo>three</foo><bar>added</bar>"));
		assertTrue("fragment is not inserted", content.contains("<foo>four</foo><bar>added</bar>"));
		assertTrue("fragment is not inserted", content.contains("<foo>five</foo><bar>added</bar>"));
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testOneOrMoreCardinalityNegative() throws IOException
	{	
		System.out.println("Running testOneOrMoreCardinalityNegative");
		
		String[] filenames = {"cardinal3.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
		}
		
		String docId = "/cardinal/cardinal3.xml";
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ONE_OR_MORE, "<bar>added</bar>");
		DocumentPatchHandle patchHandle = patchBldr.build();

		String exception = "";
		try
		{
			docMgr.patch(docId, patchHandle);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			exception = e.getMessage();
		}
		
		String expectedException = "Local message: write failed: Bad Request. Server Message: RESTAPI-INVALIDREQ: (err:FOER0000) Invalid request:  reason: invalid content patch operations for uri /cardinal/cardinal3.xml: invalid cardinality of 0 nodes for: /root/foo";
		
		assertTrue("Exception is not thrown", exception.contains(expectedException));
		
		// release client
		client.release();		
	}


@SuppressWarnings("deprecation")
@Test	public void testZeroOrOneCardinalityNegative() throws IOException
	{	
		System.out.println("Running testZeroOrOneCardinalityNegative");
		
		String[] filenames = {"cardinal1.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
		}
		
		String docId = "/cardinal/cardinal1.xml";
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ZERO_OR_ONE, "<bar>added</bar>");
		DocumentPatchHandle patchHandle = patchBldr.build();

		String exception = "";
		try
		{
			docMgr.patch(docId, patchHandle);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			exception = e.getMessage();
		}
		
		String expectedException = "Local message: write failed: Bad Request. Server Message: RESTAPI-INVALIDREQ: (err:FOER0000) Invalid request:  reason: invalid content patch operations for uri /cardinal/cardinal1.xml: invalid cardinality of 5 nodes for: /root/foo";
		
		assertTrue("Exception is not thrown", exception.contains(expectedException));
		
		// release client
		client.release();		
	}


@SuppressWarnings("deprecation")
@Test	public void testZeroOrOneCardinalityPositive() throws IOException
	{	
		System.out.println("Running testZeroOrOneCardinalityPositive");
		
		String[] filenames = {"cardinal2.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
		}
		
		String docId = "/cardinal/cardinal2.xml";
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ZERO_OR_ONE, "<bar>added</bar>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertTrue("fragment is not inserted", content.contains("<foo>one</foo><bar>added</bar>"));
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testZeroOrOneCardinalityPositiveWithZero() throws IOException
	{	
		System.out.println("Running testZeroOrOneCardinalityPositiveWithZero");
		
		String[] filenames = {"cardinal3.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
		}
		
		String docId = "/cardinal/cardinal3.xml";
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ZERO_OR_ONE, "<bar>added</bar>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertFalse("fragment is inserted", content.contains("<baz>one</baz><bar>added</bar>"));
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testZeroOrMoreCardinality() throws IOException
	{	
		System.out.println("Running testZeroOrMoreCardinality");
		
		String[] filenames = {"cardinal1.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
		}
		
		String docId = "/cardinal/cardinal1.xml";
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root/foo", Position.AFTER, Cardinality.ZERO_OR_MORE, "<bar>added</bar>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertTrue("fragment is not inserted", content.contains("<foo>one</foo><bar>added</bar>"));
		assertTrue("fragment is not inserted", content.contains("<foo>two</foo><bar>added</bar>"));
		assertTrue("fragment is not inserted", content.contains("<foo>three</foo><bar>added</bar>"));
		assertTrue("fragment is not inserted", content.contains("<foo>four</foo><bar>added</bar>"));
		assertTrue("fragment is not inserted", content.contains("<foo>five</foo><bar>added</bar>"));
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testBug23843() throws IOException
	{	
		System.out.println("Running testBug23843");
		
		String[] filenames = {"cardinal1.xml","cardinal4.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/cardinal/", "XML");
			
			String docId = "";
			
			XMLDocumentManager docMgr = client.newXMLDocumentManager();
			DocumentMetadataHandle readMetadataHandle = new DocumentMetadataHandle();
			
			DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
			if (filename == "cardinal1.xml"){
				patchBldr.insertFragment("/root", Position.LAST_CHILD, Cardinality.ONE, "<bar>added</bar>");
			}
			else if (filename == "cardinal4.xml") {
				patchBldr.insertFragment("/root", Position.LAST_CHILD, "<bar>added</bar>");
			}
			DocumentPatchHandle patchHandle = patchBldr.build();
			String RawPatch = patchHandle.toString();
			System.out.println("Before"+RawPatch);
			
			String exception = "";
			if (filename == "cardinal1.xml"){
				try
				{	docId= "/cardinal/cardinal1.xml";
					docMgr.patch(docId, patchHandle);
					System.out.println("After"+docMgr.readMetadata(docId, new DocumentMetadataHandle()).toString());
					assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?><rapi:metadata xmlns:rapi=\"http://marklogic.com/rest-api\" xmlns:prop=\"http://marklogic.com/xdmp/property\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><rapi:collections></rapi:collections><rapi:permissions><rapi:permission><rapi:role-name>rest-reader</rapi:role-name><rapi:capability>read</rapi:capability></rapi:permission><rapi:permission><rapi:role-name>rest-writer</rapi:role-name><rapi:capability>update</rapi:capability></rapi:permission></rapi:permissions><prop:properties></prop:properties><rapi:quality>0</rapi:quality></rapi:metadata>", docMgr.readMetadata(docId, new DocumentMetadataHandle()).toString());
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
					exception = e.getMessage();
				}
			}
			else if (filename == "cardinal4.xml") {
				try
				{	
					docId = "/cardinal/cardinal4.xml";
					docMgr.clearMetadataCategories();
					docMgr.patch(docId, new StringHandle(patchHandle.toString()));
					docMgr.setMetadataCategories(Metadata.ALL);
					System.out.println("After"+docMgr.readMetadata(docId, new DocumentMetadataHandle()).toString());
					assertEquals("", "<?xml version=\"1.0\" encoding=\"utf-8\"?><rapi:metadata xmlns:rapi=\"http://marklogic.com/rest-api\" xmlns:prop=\"http://marklogic.com/xdmp/property\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><rapi:collections></rapi:collections><rapi:permissions><rapi:permission><rapi:role-name>rest-reader</rapi:role-name><rapi:capability>read</rapi:capability></rapi:permission><rapi:permission><rapi:role-name>rest-writer</rapi:role-name><rapi:capability>update</rapi:capability></rapi:permission></rapi:permissions><prop:properties></prop:properties><rapi:quality>0</rapi:quality></rapi:metadata>", docMgr.readMetadata(docId, new DocumentMetadataHandle()).toString());
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
					exception = e.getMessage();
				}
			}

			String actual = docMgr.read(docId, new StringHandle()).get();
			
			System.out.println("Actual : "+actual);
		}
		
		// release client
		client.release();		
	}
@AfterClass
public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		
	}
}
