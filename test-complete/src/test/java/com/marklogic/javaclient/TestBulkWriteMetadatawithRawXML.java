/**
 * 
 */
package com.marklogic.javaclient;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.FailedRequestException;
/**
 * @author skottam
 * This test is test the DocumentWriteSet function 
 * public DocumentWriteSet add(String uri, DocumentMetadataWriteHandle metadataHandle);
 * This test intention is to test the system default , request wide,  and document specific update to metadata will overwrite the existing metadata
 * setup: create a usr1 with system level meta data as default
 * client make a connection with usr1 to do bulk loading 
 * load set of documents where default metadata is set with raw xml or json documents 
 * test disableDefault().
 * 
 */
public class TestBulkWriteMetadatawithRawXML extends  BasicJavaClientREST{

	private static String dbName = "TestBulkWriteDefaultMetadataDB3";
	private static String [] fNames = {"TestBulkWriteDefaultMetadataDB-3"};
	private static String restServerName = "TestBulkWriteDefaultMetadata3-RESTServer";
	private static int restPort = 8011;
	private  DatabaseClient client ;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		  System.out.println("In Setup");
		  setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		  createRESTUser("app-user", "password","rest-writer","rest-reader"  );
		  createRESTUserWithPermissions("usr1", "password",getPermissionNode("eval",Capability.READ),getCollectionNode("http://permission-collections/"), "rest-writer","rest-reader" );
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		deleteRESTUser("app-user");
		deleteRESTUser("usr1");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// create new connection for each test below
		  client = DatabaseClientFactory.newClient("localhost", restPort, "usr1", "password", Authentication.DIGEST);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("Running clear script");	
    	// release client
    	client.release();
	}
	
	public DocumentMetadataHandle setMetadata(){
		  // create and initialize a handle on the metadata
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

	    // get metadata values
	    DocumentProperties properties = mh.getProperties();
	    DocumentPermissions permissions = mh.getPermissions();
	    DocumentCollections collections = mh.getCollections();
	    
	    // Properties
	   // String expectedProperties = "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
	    String actualProperties = getDocumentPropertiesString(properties);
	    boolean result = actualProperties.contains("size:5|");
	    assertTrue("Document properties count", result);
	    
	    // Permissions
	    String expectedPermissions1 = "size:4|rest-reader:[READ]|eval:[READ]|app-user:[UPDATE, READ]|rest-writer:[UPDATE]|";
	    String expectedPermissions2 = "size:4|rest-reader:[READ]|eval:[READ]|app-user:[READ, UPDATE]|rest-writer:[UPDATE]|";
	    String actualPermissions = getDocumentPermissionsString(permissions);
	    System.out.println(actualPermissions);
	    if(actualPermissions.contains("[UPDATE, READ]"))
	    	assertEquals("Document permissions difference", expectedPermissions1, actualPermissions);
	    else if(actualPermissions.contains("[READ, UPDATE]"))
	    	assertEquals("Document permissions difference", expectedPermissions2, actualPermissions);
	    else
	    	assertEquals("Document permissions difference", "wrong", actualPermissions);
	    
	    // Collections 
	    String expectedCollections = "size:2|my-collection1|my-collection2|";
	    String actualCollections = getDocumentCollectionsString(collections);
	    assertEquals("Document collections difference", expectedCollections, actualCollections);
	    
	}
	public void validateDefaultMetadata(DocumentMetadataHandle mh){

	    // get metadata values
	  
	    DocumentPermissions permissions = mh.getPermissions();
	    DocumentCollections collections = mh.getCollections();
	    
	    // Permissions
	    
	    String expectedPermissions1 = "size:3|rest-reader:[READ]|eval:[READ]|rest-writer:[UPDATE]|";
	    String actualPermissions = getDocumentPermissionsString(permissions);
	   	assertEquals("Document permissions difference", expectedPermissions1, actualPermissions);
	    
	    // Collections 
	    String expectedCollections = "size:1|http://permission-collections/|";
	    String actualCollections = getDocumentCollectionsString(collections);
	    
	    assertEquals("Document collections difference", expectedCollections, actualCollections);
//	    System.out.println(actualPermissions);
	}
	@Test  
	public void testWriteMultipleTextDocWithXMLMetadata() throws Exception
	  {
		DocumentMetadataHandle mh1,mh2;
		 String docId[] = {"/foo/test/myFoo1.txt","/foo/test/myFoo2.txt","/foo/test/myFoo3.txt",
				 			"/foo/test/myFoo4.txt","/foo/test/myFoo5.txt","/foo/test/myFoo6.txt",
				 			"/foo/test/myFoo7.txt","/foo/test/myFoo8.txt","/foo/test/myFoo9.txt"};
	    
	    TextDocumentManager docMgr = client.newTextDocumentManager();
	         
	    DocumentWriteSet writeset =docMgr.newWriteSet();
	    // get the original metadata
	 	Document docMetadata = getXMLMetadata("metadata-original.xml");
	 		// create handle to write metadata
	 	DOMHandle writeMetadataHandle = new DOMHandle();
	 	writeMetadataHandle.set(docMetadata);
	 	
	 	writeset.addDefault(writeMetadataHandle);
	    
	 	writeset.add(docId[0], new StringHandle().with("This is so foo1"));
	    writeset.add(docId[1], new StringHandle().with("This is so foo2"));
	    writeset.add(docId[2], new StringHandle().with("This is so foo3"));
	    docMgr.write(writeset);
	   //Default properties for are document set
	   
	    DocumentPage page = docMgr.read(docId);
	    DOMHandle mh= new DOMHandle();
	    while(page.hasNext()){
	    	DocumentRecord rec = page.next();
	    	docMgr.readMetadata(rec.getUri(), mh);
	    	 Document docReadMetadata = mh.get();
	    	assertXpathEvaluatesTo("coll1", "string(//*[local-name()='collection'][1])", docReadMetadata);
	 	    assertXpathEvaluatesTo("coll2", "string(//*[local-name()='collection'][2])", docReadMetadata);
	 	    assertXpathEvaluatesTo("MarkLogic", "string(//*[local-name()='Author'])", docReadMetadata);
	    }
	   
	  
	    //Adding document specific properties in document set2
	        	
	    	writeset =docMgr.newWriteSet();
		 // put metadata
		    mh2 = setMetadata();
		    
		    writeset.add(docId[3], new StringHandle().with("This is so foo4"));
		    writeset.add(docId[4],mh2, new StringHandle().with("This is so foo5"));
		    writeset.add(docId[5],writeMetadataHandle, new StringHandle().with("This is so foo6"));
		    docMgr.write(writeset);
		     

		    page = docMgr.read(docId[3]);
		    DocumentRecord rec = page.next();
		    docMgr.readMetadata(rec.getUri(), mh2);
		    validateDefaultMetadata(mh2);
		    
		    page = docMgr.read(docId[4]);
		    mh2=new DocumentMetadataHandle();
		    rec = page.next();
		    docMgr.readMetadata(rec.getUri(), mh2);
		    validateMetadata(mh2);
		    
		    
		    page = docMgr.read(docId[5]);
		    rec = page.next();
		    docMgr.readMetadata(rec.getUri(), mh);
	    	Document docReadMetadata = mh.get();
	    	assertXpathEvaluatesTo("coll1", "string(//*[local-name()='collection'][1])", docReadMetadata);
	 	    assertXpathEvaluatesTo("coll2", "string(//*[local-name()='collection'][2])", docReadMetadata);
	 	    assertXpathEvaluatesTo("MarkLogic", "string(//*[local-name()='Author'])", docReadMetadata);	 
			 
	  }
	@Test  
	public void testWriteMultipleXMLDocWithXMLMetadata() throws Exception  
	  {  
		// put metadata
	    
	    String docId[] = {"/foo/test/Foo1.xml","/foo/test/Foo2.xml","/foo/test/Foo3.xml",
	    					"/foo/test/Foo4.xml","/foo/test/Foo5.xml","/foo/test/Foo6.xml",
	    					"/foo/test/Foo7.xml","/foo/test/Foo8.xml","/foo/test/Foo8.xml"};
	    XMLDocumentManager docMgr = client.newXMLDocumentManager();
	    DocumentWriteSet writeset =docMgr.newWriteSet();
	 // get the original metadata
	 	Document docMetadata = getXMLMetadata("metadata-updated.xml");
	 	// create handle to write metadata
	 	DOMHandle writeMetadataHandle = new DOMHandle();
	 	writeMetadataHandle.set(docMetadata);
	 	 	
	 	writeset.addDefault(writeMetadataHandle);
	    
	    DocumentDescriptor docdisc = docMgr.newDescriptor("test1");
	    	    
	    writeset.add(docdisc, new DOMHandle(getDocumentContent("This is so foo1")));
	    writeset.add(docId[1], new DOMHandle().with(getDocumentContent("This is so foo2")));
	    writeset.add(docId[2], new DOMHandle().with(getDocumentContent("This is so foo3")));
	    
	    docMgr.write(writeset);
	    	    
	    DocumentPage page = docMgr.read(docId);
	    DocumentRecord rec;
	    Document docReadMetadata =null;
	    DOMHandle mh= new DOMHandle();
	    while(page.hasNext()){
	    	rec = page.next();
	    	docMgr.readMetadata(rec.getUri(), mh);
	    	docReadMetadata = mh.get();
	    	 assertXpathEvaluatesTo("coll1", "string(//*[local-name()='collection'][1])", docReadMetadata);
	 	    assertXpathEvaluatesTo("coll3", "string(//*[local-name()='collection'][2])", docReadMetadata);
	 	    assertXpathEvaluatesTo("23", "string(//*[local-name()='quality'])", docReadMetadata);
	 	    assertXpathEvaluatesTo("Aries", "string(//*[local-name()='Author'])", docReadMetadata);
	    }
	    assertXpathEvaluatesTo("coll1", "string(//*[local-name()='collection'][1])", docReadMetadata);
	 
	    // put metadata
	    DocumentMetadataHandle mh2 = setMetadata();
	    writeset =docMgr.newWriteSet();
	    
	    writeset.add(docId[3], new DOMHandle(getDocumentContent("This is so foo4")));
	    writeset.addDefault(mh2);
	    writeset.add(docId[4],writeMetadataHandle ,new DOMHandle().with(getDocumentContent("This is so foo5")));
	    writeset.add(docId[5], new DOMHandle().with(getDocumentContent("This is so foo6")));
	    docMgr.write(writeset);
	    
	    page = docMgr.read(docId[3]);
		mh2=new DocumentMetadataHandle();
		rec = page.next();
		docMgr.readMetadata(rec.getUri(), mh2);
		validateDefaultMetadata(mh2);
		    
		page = docMgr.read(docId[4]);
		rec= page.next();
		docMgr.readMetadata(rec.getUri(), mh);
    	docReadMetadata = mh.get();
    	System.out.println(rec.getUri()+ " "+ docReadMetadata+docId[4]);
    	 assertXpathEvaluatesTo("coll1", "string(//*[local-name()='collection'][1])", docReadMetadata);
 	    assertXpathEvaluatesTo("coll3", "string(//*[local-name()='collection'][2])", docReadMetadata);
 	    assertXpathEvaluatesTo("23", "string(//*[local-name()='quality'])", docReadMetadata);
 	    assertXpathEvaluatesTo("Aries", "string(//*[local-name()='Author'])", docReadMetadata);
		    
		 page = docMgr.read(docId[5]);
		 rec = page.next();
		 docMgr.readMetadata(rec.getUri(), mh2);
		 validateMetadata(mh2);
		
	  }
	
	
	/*
	 * This is test is made up for the github issue 41
	 */
@Test
	public void testWriteMultipleJSONDocsWithRawJSONMetadata() throws Exception  
	  {
		// Synthesize input content
        
	
		StringHandle doc1 = new StringHandle(
                "{\"number\": 1}").withFormat(Format.JSON);
        StringHandle doc2 = new StringHandle(
                "{\"number\": 2}").withFormat(Format.JSON);
        StringHandle doc3 = new StringHandle(
                "{\"number\": 3}").withFormat(Format.JSON);
        StringHandle doc4 = new StringHandle(
                "{\"number\": 4}").withFormat(Format.JSON);
        StringHandle doc5 = new StringHandle(
                "{\"number\": 5}").withFormat(Format.JSON);

        // Synthesize input metadata
        
       ObjectMapper mapper = new ObjectMapper();
       JacksonHandle defaultMetadata1 = new JacksonHandle();
       Map<String,String> p = new HashMap<String,String>();
       p.put("myString", "json");
       p.put("myInt", "9");
       defaultMetadata1.with(constructJSONPropertiesMetadata(p));
       
       JacksonHandle defaultMetadata2 =  new JacksonHandle();
       JsonNode jn = mapper.readTree("{\"quality\": 20}");
       defaultMetadata2.set(jn);
       
       JacksonHandle docSpecificMetadata =  new JacksonHandle();
       docSpecificMetadata.set(constructJSONCollectionMetadata("http://Json-Uri-spec-collections/"));
       	
        // Create and build up the batch
        JSONDocumentManager jdm = client.newJSONDocumentManager();
        DocumentWriteSet batch = jdm.newWriteSet();

        // use system default metadata
        batch.add("doc1.json", doc1);       // system default metadata
        // using batch default metadata
        batch.addDefault(defaultMetadata1);  
        batch.add("doc2.json", doc2);       // batch default metadata
        batch.add("doc3.json", docSpecificMetadata, doc3);
        batch.add("doc4.json", doc4);       // batch default metadata

        // replace batch default metadata with new metadata
        batch.addDefault(defaultMetadata2); 
        batch.add("doc5.json", doc5);       // batch default 

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
	    System.out.print(mh.getQuality());
	    validateDefaultMetadata(mh);
	    assertEquals("default quality",0,mh.getQuality());
	        
	    // Doc2 should use the first batch default metadata, that has only properties i.e. rest needs to be default to system defaults
	    page = jdm.read("doc2.json");
	    rec = page.next();
	    jdm.readMetadata(rec.getUri(), mh);
	    System.out.print(mh.getCollections().isEmpty());
	    assertEquals("default quality",0,mh.getQuality());
	    assertTrue("default collections reset",mh.getCollections().isEmpty());
	    
        // Doc3 should have the system default document quality (0) because quality
        // was not included in the document-specific metadata. It should be in the
        // collection "mySpecificCollection", from the document-specific metadata.
        
	    page = jdm.read("doc3.json");
	    rec = page.next();
	    jdm.readMetadata(rec.getUri(), mh);
	    assertEquals("default quality",0,mh.getQuality());
	    assertEquals("default collection must change","[http://Json-Uri-spec-collections/]",mh.getCollections().toString());
	  
	    // Doc 4 should also use the 1st batch default metadata, with quality 1
        page = jdm.read("doc4.json");
	    rec = page.next();
	    jdm.readMetadata(rec.getUri(), mh);
	    assertEquals("default quality",0,mh.getQuality());
	    assertTrue("default collections reset",mh.getProperties().containsValue("9"));
	    // Doc5 should use the 2nd batch default metadata, with quality 2
	    page = jdm.read("doc5.json");
	    rec = page.next();
	    jdm.readMetadata(rec.getUri(), mh);
	    assertEquals("default quality",20,mh.getQuality());
	    
	  }
/*
 * Git Issue #24 is tested here
 * 
 */
	@Test 
	public void testWriteGenericDocMgrWithDefaultMetadata() throws Exception  
	  {
		 String docId[] = {"Sega-4MB.jpg"};
		  
		GenericDocumentManager docMgr = client.newDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		 // put metadata
		ObjectMapper mapper = new ObjectMapper();
	       JacksonHandle mh = new JacksonHandle();
	       Map<String,String> p = new HashMap<String,String>();
	       p.put("myString", "Generic JSON");
	       p.put("myInt", "19");
	       mh.with(constructJSONPropertiesMetadata(p));
		    
		 writeset.addDefault(mh); // Adding default metadata to the entire batch
		 File file1= null;
		 file1 = new File("src/test/java/com/marklogic/javaclient/data/" + docId[0]);
		 FileInputStream fis = new FileInputStream(file1);
		 InputStreamHandle handle1 = new InputStreamHandle(fis);
		 handle1.setFormat(Format.BINARY);
		 
		 writeset.add("/generic/Sega.jpg",handle1); // This document implicitly gets the default metadata that we added in the begining
		 
	     JacksonHandle jh = new JacksonHandle();
	     ObjectMapper objM = new ObjectMapper();
	     JsonNode jn = objM.readTree(new String("{\"animal\":\"dog\", \"says\":\"woof\"}"));
	     jh.set(jn);
	     jh.setFormat(Format.JSON);
	     
	     writeset.add("/generic/dog.json",new JacksonHandle().with(mapper.readTree("{\"quality\": 10}")),jh); //This document suppose to get the in scope metadata quality and should set collections to system default
	     writeset.disableDefault();
	     String foo1 = "This is foo1 of byte Array";
	     byte[] ba = foo1.getBytes();
	     BytesHandle bh = new BytesHandle(ba);
	     bh.setFormat(Format.TEXT);
	     
	     writeset.add("/generic/foo1.txt",bh);
	     
	     DOMSource ds = new DOMSource(getDocumentContent("This is so foo1"));
	     SourceHandle sh = new SourceHandle();
	     sh.set(ds);
	     sh.setFormat(Format.XML);
	     DocumentMetadataHandle mh2 = new DocumentMetadataHandle();
	     writeset.add("/generic/foo.xml", new JacksonHandle().with(constructJSONCollectionMetadata("http://Json-Uri-generic-collections/")), sh); //This document should over write the system default and default collection list and get document specific collection
	     
		 docMgr.write(writeset);
		 DocumentMetadataHandle mh1 = new DocumentMetadataHandle();
		 DocumentPage page = docMgr.read("/generic/Sega.jpg");
	     DocumentRecord rec = page.next();
	     docMgr.readMetadata(rec.getUri(),mh1);
	     assertEquals("default quality",0,mh1.getQuality());
		 assertTrue("Properties contains value 19",mh1.getProperties().containsValue("19"));
	     	
		 	 page = docMgr.read("/generic/dog.json");
			 rec = page.next();
			 docMgr.readMetadata(rec.getUri(), mh1);
			 assertEquals("default quality",10,mh1.getQuality());
			 assertTrue("default collections missing",mh1.getCollections().isEmpty());
			 
			 page = docMgr.read("/generic/foo1.txt");
			 rec = page.next();
			 docMgr.readMetadata(rec.getUri(), mh1);
			// until issue 24 is fixed
			 //this.validateDefaultMetadata(mh1);
			 
			 page = docMgr.read("/generic/foo.xml");
			 rec = page.next();
			 docMgr.readMetadata(rec.getUri(), mh1);
			 assertEquals("default quality",0,mh1.getQuality());
			 assertEquals("default collection must change","[http://Json-Uri-generic-collections/]",mh1.getCollections().toString());
		    
	    }

}
