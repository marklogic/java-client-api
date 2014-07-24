/**
 * 
 */
package com.marklogic.javaclient;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Calendar;

import javax.xml.transform.dom.DOMSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteSet;
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
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;

/**
 * @author skottam
 *This test is designed to add default meta data bulk writes with different types of Managers and different content type like JSON,text,binary,XMl
 * 
 *  TextDocumentManager
 *  XMLDocumentManager
 *  BinaryDocumentManager
 *  JSONDocumentManager
 *  GenericDocumentManager
 *  
 */
public class TestBulkWriteMetadata1  extends BasicJavaClientREST {
	
	private static String dbName = "TestBulkWriteDefaultMetadataDB";
	private static String [] fNames = {"TestBulkWriteDefaultMetadataDB-1"};
	private static String restServerName = "TestBulkWriteDefaultMetadata-RESTServer";
	private static int restPort = 8011;
	private  DatabaseClient client ;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		  System.out.println("In Setup");
		  setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
		  createRESTUser("app-user", "password", "rest-writer","rest-reader" );
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("In tear down" );
		tearDownJavaRESTServer(dbName, fNames, restServerName);
		deleteRESTUser("app-user");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// create new connection for each test below
		  client = DatabaseClientFactory.newClient("localhost", restPort, "app-user", "password", Authentication.DIGEST);
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
	    String expectedCollections = "size:2|my-collection1|my-collection2|";
	    String actualCollections = getDocumentCollectionsString(collections);
	    assertEquals("Document collections difference", expectedCollections, actualCollections);
	    
	}

	@Test  
	public void testWriteMultipleTextDocWithDefaultMetadata() 
	  {
		DocumentMetadataHandle mh1,mh2;
		 String docId[] = {"/foo/test/myFoo1.txt","/foo/test/myFoo2.txt","/foo/test/myFoo3.txt"};
	    
	    TextDocumentManager docMgr = client.newTextDocumentManager();
	         
	    DocumentWriteSet writeset =docMgr.newWriteSet();
	 // put metadata
	    DocumentMetadataHandle mh = setMetadata();
	    
	    writeset.addDefault(mh);
	    writeset.add(docId[0], new StringHandle().with("This is so foo1"));
	    writeset.add(docId[1], new StringHandle().with("This is so foo2"));
	    writeset.add(docId[2], new StringHandle().with("This is so foo3"));
	    docMgr.write(writeset);
	    DocumentPage page = docMgr.read(docId);
	    
	    while(page.hasNext()){
	    	DocumentRecord rec = page.next();
	    	docMgr.readMetadata(rec.getUri(), mh);
	    	validateMetadata(mh);
	    }
	    validateMetadata(mh);
	  }
	@Test  
	public void testWriteMultipleXMLDocWithDefaultMetadata() throws Exception  
	  {  
	    String docId[] = {"/foo/test/Foo1.xml","/foo/test/Foo2.xml","/foo/test/Foo3.xml"};
	    XMLDocumentManager docMgr = client.newXMLDocumentManager();
	    DocumentWriteSet writeset =docMgr.newWriteSet();
	 // put metadata
	    DocumentMetadataHandle mh = setMetadata();
	    
	    writeset.addDefault(mh); 
	    writeset.add(docId[0], new DOMHandle(getDocumentContent("This is so foo1")));
	    writeset.add(docId[1], new DOMHandle().with(getDocumentContent("This is so foo2")));
	    writeset.add(docId[2], new DOMHandle().with(getDocumentContent("This is so foo3")));
	    
	    docMgr.write(writeset);
	    
	    DocumentPage page = docMgr.read(docId);
	    
	    while(page.hasNext()){
	    	DocumentRecord rec = page.next();
	    	docMgr.readMetadata(rec.getUri(), mh);
	    	validateMetadata(mh);
	    }
	    validateMetadata(mh);
	  }
	@Test 
	public void testWriteMultipleBinaryDocWithDefaultMetadata() throws Exception  
	  {
		 String docId[] = {"Pandakarlino.jpg","mlfavicon.png"};
		  
		 BinaryDocumentManager docMgr = client.newBinaryDocumentManager();
		 DocumentWriteSet writeset =docMgr.newWriteSet();
		 // put metadata
		 DocumentMetadataHandle mh = setMetadata();
		    
		 writeset.addDefault(mh);
		 File file1= null,file2=null;
		 file1 = new File("src/test/java/com/marklogic/javaclient/data/" + docId[0]);
		 FileHandle handle1 = new FileHandle(file1);
		 writeset.add("/1/"+docId[0],handle1.withFormat(Format.BINARY));
		 writeset.add("/2/"+docId[0],handle1.withFormat(Format.BINARY));
		 file2 = new File("src/test/java/com/marklogic/javaclient/data/" + docId[1]);
		 FileHandle handle2 = new FileHandle(file2);
		 writeset.add("/1/"+docId[1],handle2.withFormat(Format.BINARY));
		 writeset.add("/2/"+docId[1],handle2.withFormat(Format.BINARY));
		  
		 docMgr.write(writeset);
		 String uris[] = new String[102];
		 int j=0;
		 for(int i =1;i<3;i++){
		    uris[i]="/"+i+"/"+docId[j];
		    uris[i]="/"+i+"/"+docId[j+1];
		  }
		 DocumentPage page = docMgr.read(uris);
		    
		    while(page.hasNext()){
		    	DocumentRecord rec = page.next();
		    	docMgr.readMetadata(rec.getUri(), mh);
		    	System.out.println(rec.getUri());
		    	validateMetadata(mh);
		    }
		    validateMetadata(mh);
	    }

	@Test
	public void testWriteMultipleJSONDocsWithDefaultMetadata() throws Exception  
	  {
		 String docId[] = {"/a.json","/b.json","/c.json"};
         String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
         String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
         String json3 =  new String("{\"animal\":\"rat\", \"says\":\"keek\"}");
 		 Reader strReader = new StringReader(json1);
		 JSONDocumentManager docMgr = client.newJSONDocumentManager();
		 DocumentWriteSet writeset =docMgr.newWriteSet();
		 // put metadata
		 DocumentMetadataHandle mh = setMetadata();
		    
		 writeset.addDefault(mh);
		 writeset.add(docId[0],new ReaderHandle(strReader).withFormat(Format.JSON));
		 writeset.add(docId[1],new ReaderHandle(new StringReader(json2)));
		 writeset.add(docId[2],new ReaderHandle(new StringReader(json3)));
		  
		 docMgr.write(writeset);
		 
		 DocumentPage page = docMgr.read(docId);
		    
		    while(page.hasNext()){
		    	DocumentRecord rec = page.next();
		    	docMgr.readMetadata(rec.getUri(), mh);
		    	System.out.println(rec.getUri());
		    	validateMetadata(mh);
		    }
		    validateMetadata(mh);
	    }
	@Test 
	public void testWriteGenericDocMgrWithDefaultMetadata() throws Exception  
	  {
		 String docId[] = {"Pandakarlino.jpg","mlfavicon.png"};
		  
		GenericDocumentManager docMgr = client.newDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		 // put metadata
		 DocumentMetadataHandle mh = setMetadata();
		    
		 writeset.addDefault(mh);
		 File file1= null;
		 file1 = new File("src/test/java/com/marklogic/javaclient/data/" + docId[0]);
		 FileInputStream fis = new FileInputStream(file1);
		 InputStreamHandle handle1 = new InputStreamHandle(fis);
		 handle1.setFormat(Format.BINARY);
		 
		 writeset.add("/generic/Pandakarlino.jpg",handle1);
		 
	     JacksonHandle jh = new JacksonHandle();
	     ObjectMapper objM = new ObjectMapper();
	     JsonNode jn = objM.readTree(new String("{\"animal\":\"dog\", \"says\":\"woof\"}"));
	     jh.set(jn);
	     jh.setFormat(Format.JSON);
	     
	     writeset.add("/generic/dog.json",jh);
	     
	     String foo1 = "This is foo1 of byte Array";
	     byte[] ba = foo1.getBytes();
	     BytesHandle bh = new BytesHandle(ba);
	     bh.setFormat(Format.TEXT);
	     
	     writeset.add("/generic/foo1.txt",bh);
	     
	     DOMSource ds = new DOMSource(getDocumentContent("This is so foo1"));
	     SourceHandle sh = new SourceHandle();
	     sh.set(ds);
	     sh.setFormat(Format.XML);
	    
	     writeset.add("/generic/foo.xml",sh);
	     
		 docMgr.write(writeset);
	     
		 DocumentPage page = docMgr.read("/generic/Pandakarlino.jpg","/generic/dog.json","/generic/foo1.txt","/generic/foo.xml");
		    
		    while(page.hasNext()){
		    	DocumentRecord rec = page.next();
		    	docMgr.readMetadata(rec.getUri(), mh);
		    	System.out.println(rec.getUri());
		    	validateMetadata(mh);
		    }
		    validateMetadata(mh);
	    }

}
