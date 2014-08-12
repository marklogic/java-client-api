package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.javaclient.TestCRUDModulesDb;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.Transaction;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentMetadataPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.DocumentMetadataPatchBuilder.Cardinality;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.query.StructuredQueryBuilder.FragmentScope;
import com.marklogic.client.query.ValuesDefinition.Frequency;
import org.junit.*;


public class TestPartialUpdate extends BasicJavaClientREST {

	private static String dbName = "TestPartialUpdateDB";
	private static String [] fNames = {"TestPartialUpdateDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
@BeforeClass
	public static void setUp() throws Exception 
	{
	  System.out.println("In setup");
	  setupJavaRESTServer(dbName, fNames[0], restServerName,8011);
	  setupAppServicesConstraint(dbName);
	}

@SuppressWarnings("deprecation")
@Test
	public void testPartialUpdateXML() throws IOException
	{	
		System.out.println("Running testPartialUpdateXML");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		}
		
		String docId = "/partial-update/constraint1.xml";
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		
		patchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2013-03-21</modified>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertTrue("fragment is not inserted", content.contains("<modified>2013-03-21</modified></root>"));
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateJSON() throws IOException
	{	
		System.out.println("Running testPartialUpdateJSON");
		
		String[] filenames = {"json-original.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		String docId = "/partial-update/json-original.json";
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		
		ObjectNode fragmentNode = mapper.createObjectNode();
		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("insertedKey", 9);
		String fragment = mapper.writeValueAsString(fragmentNode);
		
		patchBldr.insertFragment("$.employees", Position.LAST_CHILD, fragment);
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertTrue("fragment is not inserted", content.contains("{\"insertedKey\":9}]"));
		
		// release client
		client.release();		
	}


@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateContent() throws IOException
	{	
		System.out.println("Running testPartialUpdateContent");
		
		String filename = "constraint1.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		
		String docId = "/partial-update/constraint1.xml";
		
		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
		
		////
		//Updating Content
		////
		//Inserting Node
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2013-03-21</modified>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println(" Before Updating "+ contentBefore );

		//Updating inserted Node
		DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
		DocumentPatchHandle xmlPatchForNode = xmlPatchBldr.replaceFragment("/root/modified", "<modified>2012-11-5</modified>").build();
		xmlDocMgr.patch(docId, xmlPatchForNode);
		String contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After Updating" + contentAfter);
		
		assertTrue("fragment is not inserted", contentAfter.contains("<modified>2012-11-5</modified></root>"));
		
		////
		//Updating Doc Element
		////
		String contentBeforeElement = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBeforeElement );
		DocumentPatchHandle xmlPatchForElement = xmlPatchBldr.replaceValue("/root/popularity", 10).build();
		xmlDocMgr.patch(docId, xmlPatchForElement);
		contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After Updating" + contentAfter);
		
		//Check
		assertTrue("Element Value has not Changed", contentAfter.contains("<popularity>10</popularity>"));
		
		////
		//Updating Doc Attribute
		////
		String contentBeforeAttribute = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBeforeAttribute );

		//Updating Attribute Value
		xmlPatchBldr.replaceValue("/root/*:price/@amt",0.5);
		//xmlPatchBldr.replaceValue("/root/*:price/@xmlns","http://marklogic.com");
		DocumentPatchHandle xmlPatchForValue = xmlPatchBldr.build();
		xmlDocMgr.patch(docId, xmlPatchForValue);
		contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After Updating" + contentAfter);
		//Check
		assertTrue("Value of amt has not Chenged", contentAfter.contains("<price amt=\"0.5\" xmlns=\"http://cloudbank.com\"/>"));
		
		
		////
		//Updating Doc Namespace
		////
		String contentBeforeNamespace = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBeforeNamespace );

		//Changing Element Value
		DocumentPatchHandle xmlPatch = xmlPatchBldr.replaceValue("/root/*:date", "2006-02-02").build();
		xmlDocMgr.patch(docId, xmlPatch);
		contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After Updating" + contentAfter);
		//Check
		assertTrue("Element Value has not Changed", contentAfter.contains("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2006-02-02</date>"));
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateDeletePath() throws IOException
	{	
		System.out.println("Running testPartialUpdateDeletePath");

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		String filename = "constraint1.xml";
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		String docId = "/partial-update/constraint1.xml";
		
		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		
		String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBefore );

		//Deleting Element Value
		DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
		//DocumentPatchHandle xmlPatch = xmlPatchBldr.replaceValue("/root/*:date", "2006-02-02").build();
		DocumentPatchHandle xmlPatch = xmlPatchBldr.delete("/root/*:date").build();
		xmlDocMgr.patch(docId, xmlPatch);
		
		//Delete invalid Path
		try{
		xmlPatch = xmlPatchBldr.delete("InvalidPath").build();
		xmlDocMgr.patch(docId, xmlPatch);
		}
		catch (Exception e){
			System.out.println(e.toString());
			assertTrue("Haven't deleted Invalid path", e.toString().contains(" invalid path: //InvalidPath"));
		}
		String contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After Updating" + contentAfter);
		assertFalse("Element is not Deleted", contentAfter.contains("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2005-01-01</date>"));
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateFragments() throws Exception{
		System.out.println("Running testPartialUpdateFragments");
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		String filename = "constraint1.xml";
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		String docId = "/partial-update/constraint1.xml";

		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBefore );
		//Inserting Fragments with valid path
		DocumentPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder();
		patchBldr.insertFragment("/root/title", Position.BEFORE , "<start>Hi</start>\n  ");
		patchBldr.insertFragment("/root/id", Position.AFTER , "\n  <modified>2013-03-21</modified>");
		patchBldr.insertFragment("/root", Position.LAST_CHILD , "  <End>bye</End>\n");
		//Inserting Fragments with invalid path
		patchBldr.insertFragment("/root/someinvalidpath", Position.BEFORE, "<false>Entry</false>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		xmlDocMgr.patch(docId, patchHandle);
		String content = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertTrue("fragment is not inserted Before", content.contains("<start>Hi</start>"));
		assertTrue("fragment is not inserted After", content.contains("<modified>2013-03-21</modified>"));
		assertTrue("fragment is not inserted as Last Child", content.contains("<End>bye</End>"));
		assertFalse("fragment with invalid path has entered", content.contains("<false>Entry</false>"));
		// release client
		client.release();	
		
	
	}

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateInsertFragments() throws Exception{
		System.out.println("Running testPartialUpdateInsertFragments");
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		String filename = "constraint1.xml";
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		String docId = "/partial-update/constraint1.xml";

		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBefore );
		//Replacing Fragments with valid path
		DocumentPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder();
		patchBldr.replaceFragment("/root/title", "<replaced>foo</replaced>");
		//Replacing Fragments with invalid path
		patchBldr.replaceFragment("/root/invalidpath", "<replaced>FalseEntry</replaced>");
		patchBldr.replaceInsertFragment("/root/nonexist", "/root", Position.LAST_CHILD, "  <foo>bar</foo>\n ");
		DocumentPatchHandle patchHandle = patchBldr.build();
		xmlDocMgr.patch(docId, patchHandle);
		String content = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertTrue("fragment is not Replaced", content.contains("<replaced>foo</replaced>"));
		assertFalse("fragment is not Replaced", content.contains("<replaced>FalseEntry</replaced>"));
		assertTrue("replaceInsertFragment has Failed", content.contains("<foo>bar</foo>"));
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateInsertExistingFragments() throws Exception{
		System.out.println("Running testPartialUpdateInsertExistingFragments");
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		String filename = "constraint1.xml";
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		String docId = "/partial-update/constraint1.xml";

		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBefore );
		//Replacing Fragments with valid path
		DocumentPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder();
		patchBldr.replaceInsertFragment("/root/title", "/root", Position.LAST_CHILD, "<foo>LastChild</foo>");
		patchBldr.replaceInsertFragment("/root/id", "/root", Position.BEFORE, "<foo>Before</foo>");
		patchBldr.replaceInsertFragment("/root/p", "/root", Position.AFTER, "<foo>After</foo>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		xmlDocMgr.patch(docId, patchHandle);
		String content = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println(content);
		
		assertTrue("replaceInsertFragment Failed at Position.LAST_CHILD", content.contains("<foo>LastChild</foo>"));
		assertTrue("replaceInsertFragment Failed at Position.BEFORE", content.contains("<foo>Before</foo>"));
		assertTrue("replaceInsertFragment Failed at Position.AFTER", content.contains("<foo>After</foo>"));

		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateReplaceApply() throws Exception{
		System.out.println("Running testPartialUpdateReplaceApply");
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-admin", "x", Authentication.DIGEST);
		ExtensionLibrariesManager libsMgr =  client.newServerConfigManager().newExtensionLibrariesManager();
	
		libsMgr.write("/ext/patch/custom-lib.xqy", new FileHandle(new File("src/test/java/com/marklogic/javaclient/data/custom-lib.xqy")).withFormat(Format.TEXT));
		// write docs
		String filename = "constraint6.xml";
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		//writeDocumentUsingInputStreamHandle(client, "custom-lib.xqy", "/partial-update/", "XML");
		String docId = "/partial-update/constraint6.xml";

		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBefore );
		// Executing different operations on XML
		DocumentPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder();
		patchBldr.replaceApply("/root/add", patchBldr.call().add(10));
		patchBldr.replaceApply("/root/subtract", patchBldr.call().subtract(2));
		patchBldr.replaceApply("/root/multiply", patchBldr.call().multiply(2));
		patchBldr.replaceApply("/root/divide", patchBldr.call().divideBy(2));
		patchBldr.replaceApply("/root/concatenateAfter", patchBldr.call().concatenateAfter(" ML7"));
		patchBldr.replaceApply("/root/concatenateBetween", patchBldr.call().concatenateBetween("ML "," 7"));
		patchBldr.replaceApply("/root/concatenateBefore", patchBldr.call().concatenateBefore("ML "));
		patchBldr.replaceApply("/root/substringAfter", patchBldr.call().substringAfter("Version"));
		patchBldr.replaceApply("/root/substringBefore", patchBldr.call().substringBefore("Version"));
		patchBldr.replaceApply("/root/replaceRegex", patchBldr.call().replaceRegex("[a-m]","1"));
		patchBldr.replaceApply("/root/applyLibrary", patchBldr.call().applyLibraryFragments("underwrite","<applyLibrary>API</applyLibrary>")).library("http://marklogic.com/ext/patch/custom-lib","/ext/patch/custom-lib.xqy");
		//patchBldr.replaceApply("/root/applyLibrary", patchBldr.call().applyLibraryValues("any-content","<applyLibraryValues>")).library("http://marklogic.com/ext/patch/custom-lib","/ext/patch/custom-lib.xqy");
		DocumentPatchHandle patchHandle = patchBldr.build();
		xmlDocMgr.patch(docId, patchHandle);
		String content = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After Update" + content);
		//Check
		assertTrue("Add Failed", content.contains("<add>15</add>"));
		assertTrue("Subtract Failed", content.contains("<subtract>3</subtract>"));
		assertTrue("Multiplication Failed", content.contains("<multiply>4</multiply>"));
		assertTrue("Division Failed", content.contains("<divide>10</divide>"));
		assertTrue("concatenateAfter Failed", content.contains("<concatenateAfter>Hi ML7</concatenateAfter>"));
		assertTrue("concatenateBefore Failed", content.contains("<concatenateBefore>ML 7</concatenateBefore>"));
		assertTrue("substringAfter Failed", content.contains(" <substringAfter> 7</substringAfter>"));
		assertTrue("substringBefore Failed", content.contains("<substringBefore>ML </substringBefore>"));
		assertTrue("concatenateBetween Failed", content.contains("<concatenateBetween>ML Version 7</concatenateBetween>"));
		assertTrue("Ragex Failed", content.contains("<replaceRegex>C111nt</replaceRegex>"));
		assertTrue("Apply Library Fragments Failed ", content.contains("<applyLibrary>APIAPI</applyLibrary>"));
		// release client
		libsMgr.delete("/ext/patch/custom-lib.xqy");
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	 public void testPartialUpdateCombination() throws Exception{
		System.out.println("Running testPartialUpdateCombination");
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		String filename = "constraint1.xml";
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		String docId = "/partial-update/constraint1.xml";

		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBefore );
	
		DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
		DocumentPatchHandle xmlPatch = xmlPatchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2012-11-5</modified>").delete("/root/*:date").replaceApply("/root/popularity", xmlPatchBldr.call().multiply(2)).build();
		xmlDocMgr.patch(docId, xmlPatch);
		String content = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println(" After Updating "+ content);
		//Check
		assertTrue("Multiplication Failed", content.contains("<popularity>10</popularity>"));
		assertFalse("Deletion Failed", content.contains("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2005-01-01</date>"));
		assertTrue("Insertion Failed", content.contains("<modified>2012-11-5</modified>"));
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateCombinationTransc() throws Exception{
		System.out.println("Running testPartialUpdateCombination");
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		Transaction t = client.openTransaction("Transac");
		// write docs
		String filename = "constraint1.xml";
		//writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/","XML");
		//t.commit();
		String docId = "/partial-update/constraint1.xml";

		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentBefore );
		//Transaction t1 = client.openTransaction();
		DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
		DocumentPatchHandle xmlPatch = xmlPatchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2012-11-5</modified>").delete("/root/*:date").replaceApply("/root/popularity", xmlPatchBldr.call().multiply(2)).build();
		xmlDocMgr.patch(docId, xmlPatch,t);
		t.commit();
		String content = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println(" After Updating "+ content);
		
		//Check
		assertTrue("Multiplication Failed", content.contains("<popularity>10</popularity>"));
		assertFalse("Deletion Failed", content.contains("<date xmlns=\"http://purl.org/dc/elements/1.1/\">2005-01-01</date>"));
		assertTrue("Insertion Failed", content.contains("<modified>2012-11-5</modified>"));
		
		// release client
		client.release();		
		
	}

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateCombinationTranscRevert() throws Exception{
		System.out.println("Running testPartialUpdateCombinationTranscRevert");
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		// write docs
		String[] filenames = {"constraint1.xml", "constraint2.xml"};
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		}
		String docId1= "/partial-update/constraint1.xml";
		String docId2 = "/partial-update/constraint2.xml";
		//Creating Manager
		XMLDocumentManager xmlDocMgr1 = client.newXMLDocumentManager();
		XMLDocumentManager xmlDocMgr2= client.newXMLDocumentManager();
		String contentBefore1 = xmlDocMgr1.read(docId1, new StringHandle()).get();
		String contentBefore2 = xmlDocMgr2.read(docId2, new StringHandle()).get();
		System.out.println(" Before Updating Document 1 "+ contentBefore1 );
		System.out.println(" Before Updating Document 2 "+ contentBefore2 );
		
		DocumentPatchBuilder xmlPatchBldr1 = xmlDocMgr1.newPatchBuilder();
		DocumentPatchBuilder xmlPatchBldr2 = xmlDocMgr2.newPatchBuilder();
		
		DocumentPatchHandle xmlPatch1 = xmlPatchBldr1.insertFragment("/root", Position.LAST_CHILD, "<modified>2012-11-5</modified>").build();
		DocumentPatchHandle xmlPatch2 = xmlPatchBldr2.insertFragment("/root", Position.LAST_CHILD, "<modified>2012-11-5</modified>").build();
		
		Transaction t1 = client.openTransaction();
		xmlDocMgr1.patch(docId1, xmlPatch1,t1);
		t1.commit();
		String content1 = xmlDocMgr1.read(docId1, new StringHandle()).get();
		System.out.println(" After Updating Documant 1 : Transaction Commit"+ content1);
		Transaction t2 = client.openTransaction();
		xmlDocMgr1.patch(docId2, xmlPatch2,t2);
		t2.rollback();
		
		String content2 = xmlDocMgr2.read(docId2, new StringHandle()).get();
		System.out.println(" After Updating Document 2 : Transaction Rollback"+ content2);
		
		//Check
//		assertTrue("Insertion Failed", content.contains("<modified>2012-11-5</modified>"));

		// release client
		client.release();		
		
	}


@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateCombinationJSON() throws Exception{
		System.out.println("Running testPartialUpdateCombinationJSON");
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		String[] filenames = {"json-original.json"};
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
		}
		String docId = "/partial-update/json-original.json";
		
		ObjectMapper mapper = new ObjectMapper();

		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		String content1 = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println("Before" + content1);
		ObjectNode fragmentNode = mapper.createObjectNode();
		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("insertedKey", 9);
		String fragment = mapper.writeValueAsString(fragmentNode);
		patchBldr.insertFragment("$.employees", Position.LAST_CHILD, fragment).delete("$.employees[2]").replaceApply("$.employees[1].firstName", patchBldr.call().concatenateAfter("Hi"));
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After" + content);
		
		assertTrue("fragment is not inserted", content.contains("{\"insertedKey\":9}]"));
		assertTrue("fragment is not inserted", content.contains("{\"firstName\":\"AnnHi\", \"lastName\":\"Smith\"}"));
		assertFalse("fragment is not deleted",content.contains("{\"firstName\":\"Bob\", \"lastName\":\"Foo\"}"));
		// release client
		client.release();	
				
	}
	

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateMetadata() throws Exception{
		System.out.println("Running testPartialUpdateMetadata");
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
		
		// write docs
		String filename = "constraint1.xml";
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		String docId = "/partial-update/constraint1.xml";

		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		String contentMetadata = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" Before Updating "+ contentMetadata);
		
		DocumentMetadataPatchBuilder patchBldr = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldr.addCollection("/document/collection3");
		patchBldr.addPermission("admin", Capability.READ);
		patchBldr.addPropertyValue("Hello","Hi");
		DocumentPatchHandle patchHandle = patchBldr.build();
		xmlDocMgr.patch(docId, patchHandle);
	
		String contentMetadata1 = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Changing "+ contentMetadata1);
		
		//Check
		assertTrue("Collection not added", contentMetadata1.contains("<rapi:collection>/document/collection3</rapi:collection>"));
		assertTrue("Permission not added", contentMetadata1.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Property not added", contentMetadata1.contains("<Hello xsi:type=\"xs:string\">Hi</Hello>"));
		
		////
		//replacing Metadata Values
		////
		DocumentMetadataPatchBuilder patchBldrRep = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrRep.replaceCollection("/document/collection3", "/document/collection4");
		patchBldrRep.replacePermission("admin",Capability.UPDATE);
		patchBldrRep.replacePropertyValue("Hello", "Bye");
		DocumentPatchHandle patchHandleRep = patchBldrRep.build();
		xmlDocMgr.patch(docId, patchHandleRep);
		String contentMetadataRep = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Updating "+ contentMetadataRep);
		
		//Check
		assertTrue("Collection not added", contentMetadataRep.contains("<rapi:collection>/document/collection4</rapi:collection>"));
		assertTrue("Permission not added", contentMetadataRep.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertTrue("Property not added", contentMetadataRep.contains("<Hello xsi:type=\"xs:string\">Bye</Hello>"));
	
		////
		//Deleting Metadata Values
		////
		DocumentMetadataPatchBuilder patchBldrDel = xmlDocMgr.newPatchBuilder(Format.XML);
		patchBldrDel.deleteCollection("/document/collection4");
		patchBldrDel.deletePermission("admin");
		patchBldrDel.deleteProperty("Hello");
		DocumentPatchHandle patchHandleDel = patchBldrDel.build();
		xmlDocMgr.patch(docId, patchHandleDel);
		String contentMetadataDel = xmlDocMgr.readMetadata(docId, new StringHandle()).get();
		System.out.println(" After Deleting "+ contentMetadataDel);
		
		//Check
		assertFalse("Collection not deleted", contentMetadataDel.contains("<rapi:collection>/document/collection4</rapi:collection>"));
		assertFalse("Permission not deleted", contentMetadataDel.contains("<rapi:role-name>admin</rapi:role-name>"));
		assertFalse("Property not deleted", contentMetadataDel.contains("<Hello xsi:type=\"xs:string\">Bye</Hello>"));
			
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateXMLDscriptor() throws IOException
	{	
		System.out.println("Running testPartialUpdateXMLDescriptor");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		}

		String docId = "/partial-update/constraint1.xml";
		// create doc manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		
		//Create Document Descriptor
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2013-03-21</modified>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		
		docMgr.patch(desc, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After"+content);
		
		assertTrue("fragment is not inserted", content.contains("<modified>2013-03-21</modified></root>"));
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateJSONDescriptor() throws IOException
	{	
		System.out.println("Running testPartialUpdateJSONDescriptor");
		
		String[] filenames = {"json-original.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
		}
		
		
		
		ObjectMapper mapper = new ObjectMapper();
		String docId = "/partial-update/json-original.json";
		// create doc manager
		JSONDocumentManager docMgr = client.newJSONDocumentManager();

		//Create Document Descriptor
		DocumentDescriptor desc = docMgr.newDescriptor(docId);
		
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		
		ObjectNode fragmentNode = mapper.createObjectNode();
		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("insertedKey", 9);
		String fragment = mapper.writeValueAsString(fragmentNode);
		
		patchBldr.insertFragment("$.employees", Position.LAST_CHILD, fragment);
		DocumentPatchHandle patchHandle = patchBldr.build();

		docMgr.patch(desc, patchHandle);
		
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After"+content);
		
		assertTrue("fragment is not inserted", content.contains("{\"insertedKey\":9}]"));
		
		// release client
		client.release();		
	}

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateXMLDscriptorTranc() throws IOException
	{	
		System.out.println("Running testPartialUpdateXMLDescriptorTranc");
		
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		}

		String docId = "/partial-update/constraint1.xml";
		// create doc manager
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		// create template
		DocumentUriTemplate template = docMgr.newDocumentUriTemplate("xml");
		template.withDirectory(docId);

		DocumentDescriptor desc = docMgr.newDescriptor(template.getDirectory());
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root", Position.LAST_CHILD, "<modified>2013-03-21</modified>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		Transaction t = client.openTransaction("Tranc");
		docMgr.patch(desc, patchHandle, t);
		t.commit();
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After"+content);
		
		assertTrue("fragment is not inserted", content.contains("<modified>2013-03-21</modified></root>"));
		
		// release client
		client.release();		
	}
	

@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateJSONDescriptorTranc() throws IOException
	{	
		System.out.println("Running testPartialUpdateJSONDescriptorTranc");
		
		String[] filenames = {"json-original.json"};

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		for(String filename : filenames)
		{
			writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
		}
		ObjectMapper mapper = new ObjectMapper();
		String docId = "/partial-update/json-original.json";
		// create doc manager
		JSONDocumentManager docMgr = client.newJSONDocumentManager();
		// create template
		DocumentUriTemplate template = docMgr.newDocumentUriTemplate("JSON");
		template.withDirectory(docId);
		//Create Document Descriptor
		DocumentDescriptor desc = docMgr.newDescriptor(template.getDirectory());
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		
		ObjectNode fragmentNode = mapper.createObjectNode();
		fragmentNode = mapper.createObjectNode();
		fragmentNode.put("insertedKey", 9);
		String fragment = mapper.writeValueAsString(fragmentNode);
		
		patchBldr.insertFragment("$.employees", Position.LAST_CHILD, fragment);
		DocumentPatchHandle patchHandle = patchBldr.build();
		Transaction t = client.openTransaction("Tranc");
		docMgr.patch(desc, patchHandle,t);
			t.commit();
		String content = docMgr.read(docId, new StringHandle()).get();
		
		System.out.println("After"+content);
		
		assertTrue("fragment is not inserted", content.contains("{\"insertedKey\":9}]"));
		
		// release client
		client.release();		
	}


@SuppressWarnings("deprecation")
@Test	public void testPartialUpdateCardinality() throws IOException
	{	
		System.out.println("Running testPartialUpdateCardinality");
		
		String filename = "constraint1.xml";

		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);
				
		// write docs
		writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "XML");
		
		String docId = "/partial-update/constraint1.xml";
		
		//Creating Manager
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
				
		//Inserting Node
		DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
		patchBldr.insertFragment("/root", Position.LAST_CHILD, Cardinality.ONE, "<modified>2013-03-21</modified>");
		DocumentPatchHandle patchHandle = patchBldr.build();
		docMgr.patch(docId, patchHandle);
		String contentBefore = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println(" Content after Updating with Cardinality.ONE : "+ contentBefore );
		assertTrue("Insertion Failed ", contentBefore.contains("</modified></root>"));
		//Updating again
		DocumentPatchBuilder xmlPatchBldr = xmlDocMgr.newPatchBuilder();
		DocumentPatchHandle xmlPatchForNode = xmlPatchBldr.insertFragment("/root/id", Position.BEFORE , Cardinality.ONE_OR_MORE, "<modified>1989-04-06</modified>").build();
		xmlDocMgr.patch(docId, xmlPatchForNode);
		String contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println("Content after Updating with Cardinality.ONE_OR_MORE" + contentAfter);
		assertTrue("Insertion Failed ", contentAfter.contains("1989-04-06"));
		//Updating again
		DocumentPatchBuilder xmlPatchBldr1 = xmlDocMgr.newPatchBuilder();
		DocumentPatchHandle xmlPatchForNode1 = xmlPatchBldr1.insertFragment("/root/id", Position.AFTER , Cardinality.ZERO_OR_ONE, "<modified>2013-07-29</modified>").build();
		xmlDocMgr.patch(docId, xmlPatchForNode1);
		contentAfter = xmlDocMgr.read(docId, new StringHandle()).get();
		
		System.out.println("Content after Updating with Cardinality.ZERO_OR_ONE" + contentAfter);
		assertTrue("Insertion Failed ", contentAfter.contains("</id><modified>2013-07-29"));
		
		// release client
		client.release();		
	}	
@AfterClass	
public static void tearDown() throws Exception
	{
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
