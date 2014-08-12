package com.marklogic.javaclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.custommonkey.xmlunit.exceptions.XpathException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.FailedRequestException;





import javax.xml.transform.dom.DOMSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.*;

import static org.junit.Assert.*;

/*
 * This test is designed to to test simple bulk writes with different types of Managers and different content type like JSON,text,binary,XMl
 * 
 *  TextDocumentManager
 *  XMLDocumentManager
 *  BinaryDocumentManager
 *  JSONDocumentManager
 *  GenericDocumentManager
 */



public class TestBulkWriteSample1 extends BasicJavaClientREST  {

	
	private static String dbName = "TestBulkWriteSampleDB";
	private static String [] fNames = {"TestBulkWriteSampleDB-1"};
	private static String restServerName = "REST-Java-Client-API-Server";
	private static int restPort = 8011;
	private  DatabaseClient client ;
    @BeforeClass
	public static void setUp() throws Exception 
	{
	   System.out.println("In setup");
	   setupJavaRESTServer(dbName, fNames[0], restServerName,restPort);
	 
	   //To enable client side http logging
	   
//	   System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
//	   System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");   
//       setupAppServicesConstraint(dbName);	  

	}
	
  @Before  public void testSetup() throws Exception
  {
	  // create new connection for each test below
	  client = DatabaseClientFactory.newClient("localhost", restPort, "rest-admin", "x", Authentication.DIGEST);
  }
    @After
    public  void testCleanUp() throws Exception
    {
    	System.out.println("Running clear script");	
    	// release client
    	client.release();

    	
    }
    /*
     * This is cloned in github with tracking bug #27685 
     * https://github.com/marklogic/java-client-api/issues/23
     * 
     * This test uses StringHandle to load 3 text documents, writes to database using bulk write set.
     * Verified by reading individual documents
     */
	@Test  
	public void testWriteMultipleTextDoc() 
	  {
		
		 String docId[] = {"/foo/test/myFoo1.txt","/foo/test/myFoo2.txt","/foo/test/myFoo3.txt"};
	    
	    TextDocumentManager docMgr = client.newTextDocumentManager();
	    DocumentWriteSet writeset =docMgr.newWriteSet();
	    
	    writeset.add(docId[0], new StringHandle().with("This is so foo1"));
	    writeset.add(docId[1], new StringHandle().with("This is so foo2"));
	    writeset.add(docId[2], new StringHandle().with("This is so foo3"));
	    
	    docMgr.write(writeset);
	    
	    assertEquals("Text document write difference", "This is so foo1", docMgr.read(docId[0], new StringHandle()).get());
	    assertEquals("Text document write difference", "This is so foo2", docMgr.read(docId[1], new StringHandle()).get());
	    assertEquals("Text document write difference", "This is so foo3", docMgr.read(docId[2], new StringHandle()).get());
	  }
	/*
	* This test uses DOMHandle to load 3 xml documents, writes to database using bulk write set.
    * Verified by reading individual documents
    */
	@Test  
	public void testWriteMultipleXMLDoc() throws Exception  
	  {
		
	   
	    String docId[] = {"/foo/test/Foo1.xml","/foo/test/Foo2.xml","/foo/test/Foo3.xml"};
	    XMLDocumentManager docMgr = client.newXMLDocumentManager();
	    DocumentWriteSet writeset =docMgr.newWriteSet();
	       
	    writeset.add(docId[0], new DOMHandle(getDocumentContent("This is so foo1")));
	    writeset.add(docId[1], new DOMHandle().with(getDocumentContent("This is so foo2")));
	    writeset.add(docId[2], new DOMHandle().with(getDocumentContent("This is so foo3")));
	    
	    docMgr.write(writeset);
	    
	    DOMHandle dh = new DOMHandle();
	    docMgr.read(docId[0], dh);  
	    
	    
	    assertEquals("xml document write difference", "This is so foo1",dh.get().getChildNodes().item(0).getTextContent());
	    docMgr.read(docId[1], dh);
	    assertEquals("xml document write difference", "This is so foo2", dh.get().getChildNodes().item(0).getTextContent());
	    docMgr.read(docId[2], dh);
	    assertEquals("xml document write difference", "This is so foo3", dh.get().getChildNodes().item(0).getTextContent());
	  }
	/*
	 * This test uses FileHandle to load 3 binary documents with same URI, writes to database using bulk write set.
     * Expecting an exception.
	 */
	@Test (expected = FailedRequestException.class) 
	public void testWriteMultipleSameBinaryDoc() throws Exception  
	  {
		 String docId[] = {"Pandakarlino.jpg","mlfavicon.png"};
		  
		 BinaryDocumentManager docMgr = client.newBinaryDocumentManager();
		 DocumentWriteSet writeset =docMgr.newWriteSet();
		 File file1= null;
		 file1 = new File("src/test/java/com/marklogic/javaclient/data/"  + docId[0]);
		 FileHandle handle1 = new FileHandle(file1);
		 writeset.add("/1/"+docId[0],handle1.withFormat(Format.BINARY));
		 writeset.add("/1/"+docId[0],handle1.withFormat(Format.BINARY));
		 		  
		 docMgr.write(writeset);
		 		
	    }
	/*
	 * This test uses FileHandle to load 3 binary documents, writes to database using bulk write set.
     * Verified by reading individual documents
	 */
	@Test 
	public void testWriteMultipleBinaryDoc() throws Exception  
	  {
		 String docId[] = {"Pandakarlino.jpg","mlfavicon.png"};
		  
		 BinaryDocumentManager docMgr = client.newBinaryDocumentManager();
		 DocumentWriteSet writeset =docMgr.newWriteSet();
		 File file1= null,file2=null;
		 file1 = new File("src/test/java/com/marklogic/javaclient/data/" + docId[0]);
		 FileHandle handle1 = new FileHandle(file1);
		 writeset.add("/1/"+docId[0],handle1.withFormat(Format.BINARY));
		 writeset.add("/2/"+docId[0],handle1.withFormat(Format.BINARY));
		 file2 = new File("src/test/java/com/marklogic/javaclient/data/"  + docId[1]);
		 FileHandle handle2 = new FileHandle(file2);
		 writeset.add("/1/"+docId[1],handle2.withFormat(Format.BINARY));
		 writeset.add("/2/"+docId[1],handle2.withFormat(Format.BINARY));
		  
		 docMgr.write(writeset);
		 long fsize1 = file1.length(),fsize2 = file2.length();
		 
		 
		 FileHandle readHandle1 = new FileHandle();
		 docMgr.read("/1/"+docId[0],readHandle1);
		 
		 FileHandle readHandle2 = new FileHandle();
		 docMgr.read("/1/"+docId[1],readHandle2);
		 System.out.println(file1.getName()+":"+fsize1+" "+readHandle1.get().getName()+":"+readHandle1.get().length());
		 System.out.println(file2.getName()+":"+fsize2+" "+readHandle2.get().getName()+":"+readHandle2.get().length());
		 assertEquals("Size of the  File 1"+docId[0],fsize1,readHandle1.get().length());
		 assertEquals("Size of the  File 1"+docId[1],fsize2,readHandle2.get().length());
	    }

/*
	 * This test uses ReaderHandle to load 3 JSON documents, writes to database using bulk write set.
     * Verified by reading individual documents
	
 */
	@Test
	public void testWriteMultipleJSONDocs() throws Exception  
	  {
		 String docId[] = {"/a.json","/b.json","/c.json"};
         String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
         String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
         String json3 =  new String("{\"animal\":\"rat\", \"says\":\"keek\"}");
 		 Reader strReader = new StringReader(json1);
		 JSONDocumentManager docMgr = client.newJSONDocumentManager();
		 DocumentWriteSet writeset =docMgr.newWriteSet();
		 writeset.add(docId[0],new ReaderHandle(strReader).withFormat(Format.JSON));
		 writeset.add(docId[1],new ReaderHandle(new StringReader(json2)));
		 writeset.add(docId[2],new ReaderHandle(new StringReader(json3)));
		  
		 docMgr.write(writeset);
		 
		 ReaderHandle r1 = new ReaderHandle();
		 docMgr.read(docId[0],r1);
		 BufferedReader bfr =  new BufferedReader(r1.get());
		 assertEquals(json1,bfr.readLine());
		 docMgr.read(docId[1],r1);
		 assertEquals("Json File Content"+docId[1],json2,new BufferedReader(r1.get()).readLine());
		 docMgr.read(docId[2],r1);
		 assertEquals("Json File Content"+docId[2],json3,new BufferedReader(r1.get()).readLine());
		 bfr.close();
	    }
	@Test
	public void testWriteMultipleJAXBDocs() throws Exception  
	  {
		String docId[] ={"/jaxb/iphone.xml","/jaxb/ipad.xml","/jaxb/ipod.xml"};
		Product product1 = new Product();
		product1.setName("iPhone");
		product1.setIndustry("Hardware");
		product1.setDescription("Very cool Iphone");
		Product product2 = new Product();
		product2.setName("iPad");
		product2.setIndustry("Hardware");
		product2.setDescription("Very cool Ipad");
		Product product3 = new Product();
		product3.setName("iPod");
		product3.setIndustry("Hardware");
		product3.setDescription("Very cool Ipod");
		JAXBContext context = JAXBContext.newInstance(Product.class);
		 XMLDocumentManager docMgr = client.newXMLDocumentManager();
		 DocumentWriteSet writeset =docMgr.newWriteSet();
//		 JAXBHandle contentHandle = new JAXBHandle(context);
//		 contentHandle.set(product1);
		 writeset.add(docId[0],new JAXBHandle(context).with(product1));
		 writeset.add(docId[1],new JAXBHandle(context).with(product2));
		 writeset.add(docId[2],new JAXBHandle(context).with(product3));
		  
		 docMgr.write(writeset);
		 
		  DOMHandle dh = new DOMHandle();
		  docMgr.read(docId[0], dh);
		 		  
		  assertEquals("xml document write difference", "Very cool Iphone",dh.get().getChildNodes().item(0).getChildNodes().item(1).getTextContent());
		    docMgr.read(docId[1], dh);
		    assertEquals("xml document write difference", "Very cool Ipad", dh.get().getChildNodes().item(0).getChildNodes().item(1).getTextContent());
		    docMgr.read(docId[2], dh);
		    assertEquals("xml document write difference", "Very cool Ipod", dh.get().getChildNodes().item(0).getChildNodes().item(1).getTextContent());
		   
	    }
/*
 * This test uses GenericManager to load all different document types
 * This test has a bug logged in github with tracking Issue#33
 * 
 */
	@Test 
	public void testWriteGenericDocMgr() throws Exception  
	  {
		 String docId[] = {"Pandakarlino.jpg","mlfavicon.png"};
		  
		GenericDocumentManager docMgr = client.newDocumentManager();
		DocumentWriteSet writeset =docMgr.newWriteSet();
		 
		 File file1= null;
		 file1 = new File("src/test/java/com/marklogic/javaclient/data/"  + docId[0]);
		 FileInputStream fis = new FileInputStream(file1);
		 InputStreamHandle handle1 = new InputStreamHandle(fis);
		 handle1.setFormat(Format.BINARY);
		 
		 writeset.add("/generic/"+docId[0],handle1);
		 
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
	     
	     FileHandle rh = new FileHandle();
		 
	     docMgr.read("/generic/"+docId[0],rh);
		 assertEquals("Size of the  File /generic/"+docId[0],file1.length(),rh.get().length());		 
		 System.out.println(rh.get().getName()+":"+rh.get().length()+"\n");
		 
		 docMgr.read("/generic/foo.xml",rh);
	     BufferedReader br=new BufferedReader(new FileReader(rh.get()));
	     br.readLine();
		 assertEquals("xml document write difference", "<foo>This is so foo1</foo>", br.readLine() );
		 docMgr.read("/generic/foo1.txt",rh);
		 br.close();
	     br=new BufferedReader(new FileReader(rh.get()));
	     assertEquals("txt document write difference", foo1, br.readLine() );
	     br.close();
	     docMgr.read("/generic/dog.json",rh);
	     br=new BufferedReader(new FileReader(rh.get()));
	     assertEquals("Json document write difference", "{\"animal\":\"dog\", \"says\":\"woof\"}", br.readLine() );
	     br.close();
		 fis.close();
	    }
	@AfterClass
	public static void tearDown() throws Exception
	{
	System.out.println("In tear down" );
	tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}