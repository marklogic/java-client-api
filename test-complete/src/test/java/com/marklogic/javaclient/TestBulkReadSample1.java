package com.marklogic.javaclient;

import java.io.File;

import java.util.HashMap;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.extra.jdom.*;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DOMHandle;

import com.marklogic.client.io.JacksonHandle;


import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.FailedRequestException;



import javax.xml.transform.dom.DOMSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Document;
import org.junit.*;

import static org.junit.Assert.*;

/*
 * This test is designed to to test simple bulk reads with different types of Managers and different content type like JSON,text,binary,XMl by passing set of uris
 * 
 *  TextDocumentManager
 *  XMLDocumentManager
 *  BinaryDocumentManager
 *  JSONDocumentManager
 *  GenericDocumentManager
 */



public class TestBulkReadSample1 extends BasicJavaClientREST  {

	private static final int BATCH_SIZE=100;
	private static final String DIRECTORY ="/bulkread/";
	private static String dbName = "TestBulkReadSampleDB";
	private static String [] fNames = {"TestBulkReadSampleDB-1"};
	private static String restServerName = "TestBulkReadSample-RESTServer";
	private static int restPort = 8011;
	private  DatabaseClient client ;
    @BeforeClass
	public static void setUp() throws Exception 
	{
	   System.out.println("In setup");
	   setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
       setupAppServicesConstraint(dbName);	  

	}
	
  @Before  public void testSetup() throws Exception
  {
	  // create new connection for each test below
	  client = DatabaseClientFactory.newClient("localhost", restPort, "rest-admin", "x", Authentication.DIGEST);
  }
    @After
    public  void testCleanUp() throws Exception
    {
    	System.out.println("Running CleanUp script");	
    	// release client
    	client.release();

    	
    }
    /*
     * 
     * Use StringHandle to load 102 text documents using bulk write set.
     * Test Bulk Read to see you can read all the documents?
     */
	@Test  
	public void testReadMultipleTextDoc() 
	  {
		int count=1;
	    TextDocumentManager docMgr = client.newTextDocumentManager();
	    DocumentWriteSet writeset =docMgr.newWriteSet();
	    
	    for(int i =0;i<102;i++){
	      writeset.add(DIRECTORY+"foo"+i+".txt", new StringHandle().with("This is so foo"+i));
	      if(count%BATCH_SIZE == 0){
	    	  docMgr.write(writeset);
	    	  writeset = docMgr.newWriteSet();
	    	}
	      count++;
	    }
	    if(count%BATCH_SIZE > 0){
	    	  docMgr.write(writeset);
	    	    	}
	    String uris[] = new String[102];
	    for(int i =0;i<102;i++){
	    uris[i]=DIRECTORY+"foo"+i+".txt";
	    }
	    count=0;
	    DocumentPage page = docMgr.read(uris);
	    while(page.hasNext()){
	    	DocumentRecord rec = page.next();
	    	validateRecord(rec,Format.TEXT);
	    	count++;
	    }
	    assertEquals("document count", 102,count); 
	    
	  }
	/*
	* This test uses DOMHandle to do bulk write 102 xml documents, and does a bulk read from database.
    * Verified by reading individual documents
    */
@Test  
	public void testReadMultipleXMLDoc() throws Exception  
	  {
		int count=1;
	    XMLDocumentManager docMgr = client.newXMLDocumentManager();
	    HashMap<String,String> map= new HashMap<String,String>();
	    DocumentWriteSet writeset =docMgr.newWriteSet();
	    for(int i =0;i<102;i++){
	    	
		    writeset.add(DIRECTORY+"foo"+i+".xml", new DOMHandle(getDocumentContent("This is so foo"+i)));
		    map.put(DIRECTORY+"foo"+i+".xml", convertXMLDocumentToString(getDocumentContent("This is so foo"+i)));
		    if(count%BATCH_SIZE == 0){
		    	  docMgr.write(writeset);
		    	  writeset = docMgr.newWriteSet();
		    	}
		      count++;
	    }
	    if(count%BATCH_SIZE > 0){
	    	docMgr.write(writeset);
	 	 }
		 String uris[] = new String[102];
		 for(int i =0;i<102;i++){
		    uris[i]=DIRECTORY+"foo"+i+".xml";
		  }
		  count=0;
		  DocumentPage page = docMgr.read(uris);
		  DOMHandle dh = new DOMHandle();
		  while(page.hasNext()){
		    	DocumentRecord rec = page.next();
		    	validateRecord(rec,Format.XML);
		    	rec.getContent(dh);
		    	assertEquals("Comparing the content :",map.get(rec.getUri()),convertXMLDocumentToString(dh.get()));
		    	count++;
		  }
		  
		 assertEquals("document count", 102,count); 
	    
	  }
	/*
	 * This test uses FileHandle to bulkload 102 binary documents,test bulk read from database.
     * 
	 */
	@Test 
	public void testReadMultipleBinaryDoc() throws Exception  
	  {
		 String docId[] = {"Sega-4MB.jpg"};
		 int count=1;
		 BinaryDocumentManager docMgr = client.newBinaryDocumentManager();
		 DocumentWriteSet writeset =docMgr.newWriteSet();
		 File file1= null;
		 file1 = new File("src/test/java/com/marklogic/javaclient/data/" + docId[0]);
		 FileHandle h1 = new FileHandle(file1);
		 for(int i =0;i<102;i++){
			    writeset.add(DIRECTORY+"binary"+i+".jpg", h1);
			    if(count%BATCH_SIZE == 0){
			    	  docMgr.write(writeset);
			    	  writeset = docMgr.newWriteSet();
			    	}
			      count++;
		    }
		    if(count%BATCH_SIZE > 0){
		    	docMgr.write(writeset);
		 	 }
		    String uris[] = new String[102];
			 for(int i =0;i<102;i++){
			    uris[i]=DIRECTORY+"binary"+i+".jpg";
			  }
			  count=0;
			  FileHandle rh = new FileHandle();
			  DocumentPage page = docMgr.read(uris);
			  while(page.hasNext()){
			    	DocumentRecord rec = page.next();
			    	validateRecord(rec,Format.BINARY);
			    	rec.getContent(rh);
			    	assertEquals("Content length :",file1.length(),rh.get().length());
			    	count++;
			  }
			 assertEquals("document count", 102,count); 
			//Testing the multiple same uris will not read multiple records 
			for(int i =0;i<102;i++){
				    uris[i]=DIRECTORY+"binary"+12+".jpg";
				  }
				  count=0;
				  page = docMgr.read(uris);
				  while(page.hasNext()){
				    	DocumentRecord rec = page.next();
				    	validateRecord(rec,Format.BINARY);
				    	count++;
				  }
				 assertEquals("document count", 1,count); 
	    }

/*
	 * Load 102 JSON documents using JacksonHandle, do a bulk read. 
     * Verify by reading individual documents
	 * This test has a bug logged in github with tracking Issue#33
 */
	@Test
	public void testWriteMultipleJSONDocs() throws Exception  
	  {
		 int count=1;	 
 		 JSONDocumentManager docMgr = client.newJSONDocumentManager();
		 DocumentWriteSet writeset =docMgr.newWriteSet();
		 
		 HashMap<String,String> map= new HashMap<String,String>();
		 
		 for(int i =0;i<102;i++){
		 JsonNode jn = new ObjectMapper().readTree("{\"animal"+i+"\":\"dog"+i+"\", \"says\":\"woof\"}");
		 JacksonHandle jh = new JacksonHandle();
		 jh.set(jn);
		 writeset.add(DIRECTORY+"dog"+i+".json",jh);
		 map.put(DIRECTORY+"dog"+i+".json", jn.toString());
		 if(count%BATCH_SIZE == 0){
	    	  docMgr.write(writeset);
	    	  writeset = docMgr.newWriteSet();
	    	}
	      count++;
//	      System.out.println(jn.toString());
		 }
		 if(count%BATCH_SIZE > 0){
		 docMgr.write(writeset);
		 }
		 String uris[] = new String[102];
		 for(int i =0;i<102;i++){
			 uris[i]=DIRECTORY+"dog"+i+".json";
		 }
		 count=0;
		 DocumentPage page = docMgr.read(uris);
		 DocumentRecord rec;
		 JacksonHandle jh = new JacksonHandle();
		 while(page.hasNext()){
	    	rec = page.next();
	    	validateRecord(rec,Format.JSON);
	    	rec.getContent(jh);
	    	assertEquals("Comparing the content :",map.get(rec.getUri()),jh.get().toString());
	    	count++;
	  }
//		 validateRecord(rec,Format.JSON);
	 assertEquals("document count", 102,count); 
   

	    }
/*
 * This test uses GenericManager to load all different document types
 * This test has a bug logged in github with tracking Issue#33
 * 
 */
@Test 
	public void testWriteGenericDocMgr() throws Exception  
	  {
		
		GenericDocumentManager docMgr = client.newDocumentManager();
		 int countXML=0,countJson=0,countJpg=0,countTEXT=0;
		 String uris[] = new String[102];
		 for(int i =0;i<99;){
		    uris[i]=DIRECTORY+"foo"+i+".xml";
		    i++;
		    uris[i]=DIRECTORY+"foo"+i+".txt";
		    i++;
		    uris[i]=DIRECTORY+"binary"+i+".jpg";
		    i++;
		    uris[i]=DIRECTORY+"dog"+i+".json";
		    i++;
		  }
//		 for(String uri:uris){System.out.println(uri);}
		  DocumentPage page = docMgr.read(uris);
		  if(!page.hasNext()){
			testReadMultipleTextDoc();
			testReadMultipleXMLDoc();
			testReadMultipleBinaryDoc();
			testWriteMultipleJSONDocs();
			page = docMgr.read(uris);
			}
		  while(page.hasNext()){
		    	DocumentRecord rec = page.next();
		     	switch(rec.getFormat())
		    	{
		    	case XML: countXML++; break;
		    	case TEXT: countTEXT++;break;
		    	case JSON: countJson++; break;
		    	case BINARY: countJpg++; break;
		    	default :
		    		break;
		    	}
		    	validateRecord(rec,rec.getFormat());
		     }
		  System.out.println("xml :"+countXML+"TXT :"+countTEXT+" json :"+countJpg+" "+countJson);
		 assertEquals("xml document count", 25,countXML);
		 assertEquals("text document count", 25,countTEXT);
		 assertEquals("binary document count", 25,countJpg);
		 assertEquals("Json document count", 25,countJson);
	    }
	@AfterClass
	public static void tearDown() throws Exception
	{
	System.out.println("In tear down" );
	tearDownJavaRESTServer(dbName, fNames, restServerName);
	}

  public void validateRecord(DocumentRecord record,Format type) {
	       
	        assertNotNull("DocumentRecord should never be null", record);
	        assertNotNull("Document uri should never be null", record.getUri());
	        assertTrue("Document uri should start with " + DIRECTORY, record.getUri().startsWith(DIRECTORY));
	        assertEquals("All records are expected to be in same format", type, record.getFormat());
	        
	       }
}