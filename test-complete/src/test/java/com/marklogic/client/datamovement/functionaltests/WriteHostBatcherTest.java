package com.marklogic.client.datamovement.functionaltests;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.WriteEvent;
import com.marklogic.client.datamovement.functionaltests.util.DmsdkJavaClientREST;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.OutputStreamHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.StringHandle;

public class WriteHostBatcherTest extends  DmsdkJavaClientREST {
	
	private static String dbName = "WriteHostBatcher";
	private static DataMovementManager dmManager = null;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";
	
	private static DatabaseClient dbClient;
	private static String host = "localhost";
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";
	
	private static JacksonHandle jacksonHandle;
	private static StringHandle stringHandle;
	private static FileHandle fileHandle;
	private static DOMHandle domHandle;
	private static BytesHandle bytesHandle;
	private static InputStreamHandle isHandle;
	private static ReaderHandle readerHandle;
	private static OutputStreamHandle osHandle;
	private static DocumentMetadataHandle docMeta1;
	private static DocumentMetadataHandle docMeta2;
	private static ReaderHandle readerHandle1;
	private static OutputStreamHandle osHandle1;
	private static WriteBatcher ihbMT;
	private static JsonNode clusterInfo;
	private static String[] hostNames ;
	
	private static String stringTriple;
	private static File fileJson;
	private static Document docContent;

	private static FileInputStream inputStream;
	private static OutputStreamSender sender;
	private static OutputStreamSender sender1;
    private static BufferedReader docStream;
    private static BufferedReader docStream1;
    private static byte[] bytesJson;
	private static JsonNode jsonNode;
	private static JobTicket writeTicket;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		hostNames = getHosts();	    
		createDB(dbName);
		Thread.currentThread().sleep(500L);
		int count = 1;
		for ( String forestHost : hostNames ) {
			createForestonHost(dbName+"-"+count,dbName,forestHost);
		    count ++;
			Thread.currentThread().sleep(500L);
		}
			
		associateRESTServerWithDB(server,dbName);
		
		dbClient = DatabaseClientFactory.newClient(host, port, user, password, Authentication.DIGEST);
		dmManager = dbClient.newDataMovementManager();
		
		clusterInfo = ((DatabaseClientImpl) dbClient).getServices()
			      .getResource(null, "forestinfo", null, null, new JacksonHandle())
			      .get();
		
		//JacksonHandle
		jsonNode = new ObjectMapper().readTree("{\"k1\":\"v1\"}");
		jacksonHandle = new JacksonHandle();
		jacksonHandle.set(jsonNode);
		
		//StringHandle
		stringTriple = "<abc>xml</abc>";
		stringHandle = new StringHandle(stringTriple);
		stringHandle.setFormat(Format.XML);
		
		
		// FileHandle
		fileJson = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"dir.json"));
		fileHandle = new FileHandle(fileJson);
		fileHandle.setFormat(Format.JSON);
			    
	    //DomHandle
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		docContent = docBuilder.parse(FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"xml-original-test.xml")));			 
	   
	  	domHandle = new DOMHandle();
		domHandle.set(docContent);
    
	    docMeta1= new DocumentMetadataHandle()
		 .withCollections("Sample Collection 1").withProperty("docMeta-1", "true").withQuality(1);
		docMeta1.setFormat(Format.XML);
		 
		docMeta2 = new DocumentMetadataHandle()
		 .withCollections("Sample Collection 2").withProperty("docMeta-2", "true").withQuality(0);
		docMeta2.setFormat(Format.XML);		
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		associateRESTServerWithDB(server,"Documents");
		for (int i =0 ; i < clusterInfo.size(); i++){
			System.out.println(dbName+"-"+(i+1));
			detachForest(dbName, dbName+"-"+(i+1));
			deleteForest(dbName+"-"+(i+1));
		}
		
		deleteDB(dbName);
	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
		
		Map<String, String> props = new HashMap<>();
 		props.put("group-id","Default");
 		props.put("view","status");
		
 		JsonNode output = getState(props, "/manage/v2/servers/"+server).path("server-status").path("status-properties");
 		props.clear();
 		String s = output.findValue("enabled").get("value").asText();
 		System.out.println("S is "+s);
		if(s.trim().equals("false")){
			props.put("server-name",server);
			props.put("group-name", "Default");
			props.put("enabled", "true");
   			changeProperty(props,"/manage/v2/servers/"+server+"/properties");
		}
			
		clearDB(port);
	}
	
	private void replenishStream() throws Exception{
		
		// InputStreamHandle
		isHandle = new InputStreamHandle();
		inputStream = new FileInputStream(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"myJSONFile.json").getPath());
		isHandle.withFormat(Format.JSON);
		isHandle.set(inputStream);
		
		// OutputStreamHandle
		sender = new OutputStreamSender() {
            // the callback receives the output stream
			public void write(OutputStream out) throws IOException {
        		// acquire the content
				InputStream docStreamwrongjson = new FileInputStream(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "WrongFormat.json").getPath());
				
        		// copy content to the output stream
        		byte[] buf = new byte[1024];
        		int byteCount = 0;
        		while ((byteCount=docStreamwrongjson.read(buf)) != -1) {
        			out.write(buf, 0, byteCount);
        		}
        		
            }
        };
        
        // create the handle
        osHandle = new OutputStreamHandle(sender);
	    osHandle.withFormat(Format.JSON);
	
		sender1 = new OutputStreamSender() {
            // the callback receives the output stream
			@Override
			public void write(OutputStream out) throws IOException {
				// copy content to the output stream
				try ( // acquire the content
						InputStream docStreamwrongjson1 = new FileInputStream(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "product-apple.json").getPath())) {
					// copy content to the output stream
					byte[] buf = new byte[1024];
					int byteCount = 0;
					while ((byteCount=docStreamwrongjson1.read(buf)) != -1) {
						out.write(buf, 0, byteCount);
					}
				}
            }
        };
        
        // create the handle
        osHandle1 = new OutputStreamHandle(sender1);
	    osHandle1.withFormat(Format.JSON);
	    	    
		// ReaderHandle
		docStream = new BufferedReader(new FileReader(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "WrongFormat.xml").getPath()));
	    readerHandle = new ReaderHandle();
	    readerHandle.withFormat(Format.XML);
	    readerHandle.set(docStream);
		    
	    docStream1 = new BufferedReader(new FileReader(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX + "employee.xml").getPath()));
	    readerHandle1 = new ReaderHandle();
	    readerHandle1.withFormat(Format.XML);
	    readerHandle1.set(docStream1);
	    
		//BytesHandle
		File file = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"dir.json"));		 
		
		try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			byte[] buf = new byte[1024];
			for (int readNum; (readNum = fis.read(buf)) != -1;)
			{
				bos.write(buf, 0, readNum);
			}	bytesJson = bos.toByteArray();
		}
	        	    
	    bytesHandle = new BytesHandle();
	    bytesHandle.setFormat(Format.JSON);
	    bytesHandle.set(bytesJson);
	}
	
	// ISSUE 45
	@Test
	public void testAdd() throws Exception{
	    final StringBuffer successBatch = new StringBuffer();
	    final StringBuffer failureBatch = new StringBuffer();
	    final String query1 = "fn:count(fn:doc())";

    	// Test 1 few failures with add (batchSize =1)
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	replenishStream();
		WriteBatcher ihb1 =  dmManager.newWriteBatcher();
		ihb1.withBatchSize(1);
		ihb1.onBatchSuccess(
		        batch -> {
		        	for(WriteEvent w: batch.getItems()){
		        		successBatch.append(w.getTargetUri()+":");
		        	}
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  throwable.printStackTrace();
		        	  for(WriteEvent w: batch.getItems()){
		        		  System.out.println("Failed URI's are"+ w.getTargetUri());
		        		  failureBatch.append(w.getTargetUri()+":");
			        	}

		           
		          });
		dmManager.startJob(ihb1);
		ihb1.add("/doc/jackson", jacksonHandle).add("/doc/reader_wrongxml", readerHandle).add("/doc/string", docMeta1, stringHandle).add("/doc/file", docMeta2, fileHandle).add("/doc/is", isHandle)
		.add("/doc/os_wrongjson", docMeta2, osHandle).add("/doc/bytes", docMeta1, bytesHandle).add("/doc/dom", domHandle);
				
		ihb1.flushAndWait();
			   	
		System.out.println("Success URI's: "+successBatch.toString());
		System.out.println("Failure URI's: "+failureBatch.toString());
		
    	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/os_wrongjson"));
    	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/reader_wrongxml"));

    	DocumentMetadataHandle  mHandle = readMetadataFromDocument(dbClient, "/doc/string", "XML");
    	Assert.assertEquals("Sample Collection 1",mHandle.getCollections().iterator().next());
     	Assert.assertTrue(mHandle.getCollections().size()==1);
     	System.out.println("Quality of /doc/string is "+ mHandle.getQuality());
     	Assert.assertEquals(1,mHandle.getQuality());
     	
    	DocumentMetadataHandle  mHandle1 = readMetadataFromDocument(dbClient, "/doc/file", "XML");
    	Assert.assertEquals(0,mHandle1.getQuality());
    	Assert.assertEquals("Sample Collection 2",mHandle1.getCollections().iterator().next());
     	Assert.assertTrue(mHandle1.getCollections().size()==1);
     	
     	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==6);
     	
     	successBatch.delete(0,successBatch.length());
    	failureBatch.delete(0,failureBatch.length());
    	clearDB(port);
    	    	
    	//ISSUE # 38
	    // Test 2 All failure with add (batchSize =8)
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
	    replenishStream();
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(8);
		ihb2.onBatchSuccess(
		        batch -> {
		        	for(WriteEvent w: batch.getItems()){
		        		successBatch.append(w.getTargetUri()+":");
		        	}
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  for(WriteEvent w: batch.getItems()){
		        		  failureBatch.append(w.getTargetUri()+":");
			        	}

		           
		          });
		dmManager.startJob(ihb2);
		ihb2.add("/doc/jackson", jacksonHandle).add("/doc/reader_wrongxml", readerHandle).add("/doc/string", docMeta1, stringHandle).add("/doc/file", docMeta2, fileHandle).add("/doc/is", isHandle)
		.add("/doc/os_wrongjson", docMeta2, osHandle).add("/doc/bytes", docMeta1, bytesHandle).add("/doc/dom", domHandle);
		
		
		ihb2.flushAndWait();
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
     	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/reader_wrongxml"));
    	
    	successBatch.delete(0,successBatch.length());
    	failureBatch.delete(0,failureBatch.length());
    	clearDB(port);
   
	 // Test 3 All success with add (batchSize =8)
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	replenishStream();
		WriteBatcher ihb3 =  dmManager.newWriteBatcher();
		ihb3.withBatchSize(8);
		ihb3.onBatchSuccess(
		        batch -> {
		        	for(WriteEvent w: batch.getItems()){
		        		successBatch.append(w.getTargetUri()+":");
		        		 
		        	}
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  for(WriteEvent w: batch.getItems()){
		        		 
		        		  failureBatch.append(w.getTargetUri()+":");
			        	}
		          });
		dmManager.startJob(ihb3);
		
		ihb3.add("/doc/jackson", docMeta2, jacksonHandle).add("/doc/reader_xml",docMeta1, readerHandle1).add("/doc/string", stringHandle).add("/doc/file",  fileHandle).add("/doc/is", docMeta2,isHandle)
		.add("/doc/os_json",  osHandle1).add("/doc/bytes",  bytesHandle).add("/doc/dom", docMeta1, domHandle);
				
		ihb3.flushAndWait();
		
		System.out.println("Size is "+dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==8);
    	
    	DocumentMetadataHandle  mHandle2 = readMetadataFromDocument(dbClient, "/doc/reader_xml", "XML");
    	Assert.assertEquals(1,mHandle2.getQuality());
    	Assert.assertEquals("Sample Collection 1",mHandle2.getCollections().iterator().next());
     	Assert.assertTrue(mHandle2.getCollections().size()==1);
    	
    	DocumentMetadataHandle  mHandle3 = readMetadataFromDocument(dbClient, "/doc/jackson", "XML");
    	Assert.assertEquals(0,mHandle3.getQuality());
    	Assert.assertEquals("Sample Collection 2",mHandle3.getCollections().iterator().next());
     	Assert.assertTrue(mHandle3.getCollections().size()==1);

     	Assert.assertTrue(uriExists(successBatch.toString(),"/doc/os_json"));
    	Assert.assertFalse(uriExists(successBatch.toString(),"/doc/reader_wrongxml"));
         	
     	successBatch.delete(0,successBatch.length());
    	failureBatch.delete(0,failureBatch.length());
    	clearDB(port);
    	
		// Test 4 All failures in 2 batches
    	Thread.currentThread().sleep(1500L);
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	replenishStream();
		WriteBatcher ihb4 =  dmManager.newWriteBatcher();
		ihb4.withBatchSize(4);
		ihb4.onBatchSuccess(
		        batch -> {
		        	for(WriteEvent w: batch.getItems()){
		        		successBatch.append(w.getTargetUri()+":");
		        		 
		        	}
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  for(WriteEvent w: batch.getItems()){
		        		 
		        		  failureBatch.append(w.getTargetUri()+":");
			        	}		           
		          });
		dmManager.startJob(ihb4);
		
		ihb4.add("/doc/jackson", docMeta2, jacksonHandle).add("/doc/reader_wrongxml",docMeta1, readerHandle).add("/doc/string", stringHandle).add("/doc/file",  fileHandle);
		ihb4.flushAndWait();
		
		ihb4.add("/doc/is", docMeta2,isHandle).add("/doc/os_wrongjson",  osHandle).add("/doc/bytes",  bytesHandle).add("/doc/dom", docMeta1, domHandle);
		ihb4.flushAndWait();
			
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/reader_wrongxml"));
    	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/os_wrongjson"));    	
	}
	
	//ISSUE 60
	@Test
	public void testAddAs() throws Exception{
	    final StringBuffer successBatch = new StringBuffer();
	    final StringBuffer failureBatch = new StringBuffer();
	    final String query1 = "fn:count(fn:doc())";
	     	  
	 // Test 1 All success with addAs (batchSize =8)
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	replenishStream();
		WriteBatcher ihb3 =  dmManager.newWriteBatcher();
		ihb3.withBatchSize(8);
		ihb3.onBatchSuccess(
		        batch -> {
		        	for(WriteEvent w: batch.getItems()){
		        		successBatch.append(w.getTargetUri()+":");
		        		 
		        	}
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  for(WriteEvent w: batch.getItems()){
		        		 
		        		  failureBatch.append(w.getTargetUri()+":");
			        	}		           
		          });
		dmManager.startJob(ihb3);
		
		ihb3.addAs("/doc/jackson", docMeta2, jsonNode).addAs("/doc/reader_xml",docMeta1, docStream1).addAs("/doc/string", stringTriple).addAs("/doc/dom", docMeta1, docContent);
				
		ihb3.flushAndWait();
		System.out.println("Size is "+dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==4);
    	
    	DocumentMetadataHandle  mHandle2 = readMetadataFromDocument(dbClient, "/doc/reader_xml", "XML");
    	Assert.assertEquals(1,mHandle2.getQuality());
    	Assert.assertEquals("Sample Collection 1",mHandle2.getCollections().iterator().next());
     	Assert.assertTrue(mHandle2.getCollections().size()==1);
    	
    	DocumentMetadataHandle  mHandle3 = readMetadataFromDocument(dbClient, "/doc/jackson", "XML");
    	Assert.assertEquals(0,mHandle3.getQuality());
    	Assert.assertEquals("Sample Collection 2",mHandle3.getCollections().iterator().next());
     	Assert.assertTrue(mHandle3.getCollections().size()==1);

     	Assert.assertTrue(uriExists(successBatch.toString(),"/doc/string"));
    	       	
     	successBatch.delete(0,successBatch.length());
    	failureBatch.delete(0,failureBatch.length());
    	clearDB(port);    	
	}
	
    //ISSUE 60
	@Test
	public void testAddandAddAs() throws Exception{
	    final StringBuffer successBatch = new StringBuffer();
	    final StringBuffer failureBatch = new StringBuffer();
	    final String query1 = "fn:count(fn:doc())";

    	// Test 1 few failures with addAs and add(batchSize =1)
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	replenishStream();
		WriteBatcher ihb1 =  dmManager.newWriteBatcher();
		ihb1.withBatchSize(1);
		ihb1.onBatchSuccess(
		        batch -> {
		        	for(WriteEvent w: batch.getItems()){
		        		successBatch.append(w.getTargetUri()+":");
		        	}
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  for(WriteEvent w: batch.getItems()){
		        		  failureBatch.append(w.getTargetUri()+":");
			        	}		           
		          });
		dmManager.startJob(ihb1);
		ihb1.addAs("/doc/jackson", jsonNode).add("/doc/reader_wrongxml", readerHandle).addAs("/doc/string", docMeta1, stringTriple).add("/doc/file", docMeta2, fileHandle).add("/doc/is", isHandle)
		.add("/doc/os_wrongjson", docMeta2, osHandle).add("/doc/bytes", docMeta1, bytesHandle).addAs("/doc/dom", domHandle);
		
		ihb1.flushAndWait();
		
    	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/os_wrongjson"));
    	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/reader_wrongxml"));

    	DocumentMetadataHandle  mHandle = readMetadataFromDocument(dbClient, "/doc/string", "XML");
    	Assert.assertEquals(1,mHandle.getQuality());
    	Assert.assertEquals("Sample Collection 1",mHandle.getCollections().iterator().next());
     	Assert.assertTrue(mHandle.getCollections().size()==1);
    	
    	DocumentMetadataHandle  mHandle1 = readMetadataFromDocument(dbClient, "/doc/file", "XML");
    	Assert.assertEquals(0,mHandle1.getQuality());
    	Assert.assertEquals("Sample Collection 2",mHandle1.getCollections().iterator().next());
     	Assert.assertTrue(mHandle1.getCollections().size()==1);
     	
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==6);
    	
     	successBatch.delete(0,successBatch.length());
    	failureBatch.delete(0,failureBatch.length());
    	clearDB(port);
    	  	
    	//ISSUE # 38
	    // Test 2 All failure with addAs and add(batchSize =8)
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
	    replenishStream();
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(8);
		ihb2.onBatchSuccess(
		        batch -> {
		        	for(WriteEvent w: batch.getItems()){
		        		successBatch.append(w.getTargetUri()+":");
		        	}
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  for(WriteEvent w: batch.getItems()){
		        		  failureBatch.append(w.getTargetUri()+":");
			        	}		           
		          });
		dmManager.startJob(ihb2);
		ihb2.add("/doc/jackson", jacksonHandle).add("/doc/reader_wrongxml", readerHandle).add("/doc/string", docMeta1, stringHandle).addAs("/doc/file", docMeta2, fileJson).add("/doc/is", isHandle)
		.add("/doc/os_wrongjson", docMeta2, osHandle).addAs("/doc/bytes", docMeta1, bytesJson).addAs("/doc/dom", domHandle);
				
		ihb2.flushAndWait();
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
     	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/reader_wrongxml"));
    	
    	successBatch.delete(0,successBatch.length());
    	failureBatch.delete(0,failureBatch.length());
    	clearDB(port);
   
	 // Test 3 All success with addAs and add(batchSize =8)
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	replenishStream();
		WriteBatcher ihb3 =  dmManager.newWriteBatcher();
		ihb3.withBatchSize(8);
		ihb3.onBatchSuccess(
		        batch -> {
		        	for(WriteEvent w: batch.getItems()){
		        		successBatch.append(w.getTargetUri()+":");
		        		 
		        	}
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  for(WriteEvent w: batch.getItems()){
		        		 
		        		  failureBatch.append(w.getTargetUri()+":");
			        	}
		          });
		dmManager.startJob(ihb3);
		
		ihb3.addAs("/doc/jackson", docMeta2, jsonNode).add("/doc/reader_xml",docMeta1, osHandle1).addAs("/doc/string", stringTriple).add("/doc/file",  fileHandle).add("/doc/is", docMeta2,isHandle)
		.add("/doc/os_json",  osHandle1).add("/doc/bytes",  bytesHandle).addAs("/doc/dom", docMeta1, docContent);
				
		ihb3.flushAndWait();
		System.out.println("Size is "+dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==8);
    	
    	DocumentMetadataHandle  mHandle2 = readMetadataFromDocument(dbClient, "/doc/reader_xml", "XML");
    	Assert.assertEquals(1,mHandle2.getQuality());
    	Assert.assertEquals("Sample Collection 1",mHandle2.getCollections().iterator().next());
     	Assert.assertTrue(mHandle2.getCollections().size()==1);
    	
    	DocumentMetadataHandle  mHandle3 = readMetadataFromDocument(dbClient, "/doc/jackson", "XML");
    	Assert.assertEquals(0,mHandle3.getQuality());
    	Assert.assertEquals("Sample Collection 2",mHandle3.getCollections().iterator().next());
     	Assert.assertTrue(mHandle3.getCollections().size()==1);

     	Assert.assertTrue(uriExists(successBatch.toString(),"/doc/os_json"));
    	Assert.assertFalse(uriExists(successBatch.toString(),"/doc/reader_wrongxml"));
         	
     	successBatch.delete(0,successBatch.length());
    	failureBatch.delete(0,failureBatch.length());
    	clearDB(port);
    	
		// Test 4 All failures in 2 batches
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	replenishStream();
		WriteBatcher ihb4 =  dmManager.newWriteBatcher();
		ihb4.withBatchSize(4);
		ihb4.onBatchSuccess(
		        batch -> {
		        	for(WriteEvent w: batch.getItems()){
		        		successBatch.append(w.getTargetUri()+":");
		        		 
		        	}
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  for(WriteEvent w: batch.getItems()){
		        		 
		        		  failureBatch.append(w.getTargetUri()+":");
			        	}
		          });
		dmManager.startJob(ihb4);
		
		ihb4.addAs("/doc/jackson", docMeta2, jsonNode).add("/doc/reader_wrongxml",docMeta1, readerHandle).add("/doc/string", stringHandle).addAs("/doc/file",  fileJson);
		ihb4.flushAndWait();
		
		ihb4.add("/doc/is", docMeta2,isHandle).add("/doc/os_wrongjson",  osHandle).add("/doc/bytes",  bytesHandle).addAs("/doc/dom", docMeta1, docContent);
		ihb4.flushAndWait();
				
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/reader_wrongxml"));
    	Assert.assertTrue(uriExists(failureBatch.toString(),"/doc/os_wrongjson"));  	
	}
	
	private boolean uriExists(String s, String in){
		return s.contains(in);
	}
	
	//Immutability of WriteBatcher- ISSUE # 26 ea 3
	@Test
	public void testHostBatcherImmutability() throws Exception{
		
		WriteBatcher ihb = dmManager.newWriteBatcher();
		ihb.withJobName(null);
		ihb.withBatchSize(2);
		dmManager.startJob(ihb);
		ihb.withBatchSize(7);

		ihb.withJobName("Job 1");
		ihb.add("/local/triple", stringHandle);
		try{
			ihb.withJobName("Job 2");
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof IllegalStateException);
		}
		
		try{
			ihb.withBatchSize(1);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof IllegalStateException);
		}
				
		ihb.flushAndWait();		
	}
	
	//ISSUE # 38
	@Test
	public void testNumberofBatches() throws Exception{
	   
	    final MutableInt numberOfSuccessFulBatches = new MutableInt(0);
	    final MutableBoolean state = new MutableBoolean(true);
	    
		WriteBatcher ihb1 =  dmManager.newWriteBatcher();
		ihb1.withBatchSize(5);
		ihb1.onBatchSuccess(
		        batch -> {
		        	numberOfSuccessFulBatches.add(1);
		        	        
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	state.isFalse();
		           
		          });
		dmManager.startJob(ihb1);
	
		for (int i =0 ;i < 101; i++){
			String uri ="/local/json-"+ i;
			ihb1.add(uri, jacksonHandle);
		}
	
		ihb1.flushAndWait();
		Assert.assertTrue(state.booleanValue());
		System.out.println(numberOfSuccessFulBatches.intValue());
		Assert.assertTrue(numberOfSuccessFulBatches.intValue()==21);
		
	}
	
	// ISSUE # 39, 40
	@Test
	public void testClientObject() throws Exception {
		
	    final StringBuffer successHost = new StringBuffer();
	    final StringBuffer successUser = new StringBuffer();
	    final StringBuffer successPassword = new StringBuffer();
	    final StringBuffer successPort = new StringBuffer();
	    	    
	    final StringBuffer failureHost = new StringBuffer();
	    final StringBuffer failureUser = new StringBuffer();
	    final StringBuffer failurePassword = new StringBuffer();
	    final StringBuffer failurePort = new StringBuffer();
	    
		WriteBatcher ihb1 =  dmManager.newWriteBatcher();
		ihb1.withBatchSize(1);
		ihb1.onBatchSuccess(
		        batch -> {
		        	successHost.append(batch.getClient().getHost()+":");  
		        	successPort.append(batch.getClient().getPort()+":");  
		        	  
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	failureHost.append(batch.getClient().getHost()+":");  
					failurePort.append(batch.getClient().getPort()+":");  	           
		          });
		dmManager.startJob(ihb1);
	
		for (int i =0 ; i < 10; i++){
			String uri ="/local/json-"+ i;
			ihb1.add(uri, stringHandle);
		}
		
		for (int i =0 ; i < 5; i++){
			ihb1.add("", stringHandle);
		}
		ihb1.flushAndWait();

		System.out.println(successUser.toString());
		System.out.println(count(successUser.toString(),user));
		Assert.assertTrue(count(successPort.toString(),String.valueOf(port))==10);
		Assert.assertTrue(count(successHost.toString(),String.valueOf(host))!=10);
				
		Assert.assertTrue(count(failurePort.toString(),String.valueOf(port))==5);
		Assert.assertTrue(count(failureHost.toString(),String.valueOf(host))!=5);
	}
		
	//not implemented ea3
	@Ignore
	public void testBatchObject() throws Exception{
	   	  
	    final StringBuffer successBatchNum = new StringBuffer();
	    final StringBuffer successBytesMoved = new StringBuffer();
	    final StringBuffer successForestName = new StringBuffer();
	    final StringBuffer successJobID = new StringBuffer();
	    final StringBuffer successTime = new StringBuffer();
	    
	    final StringBuffer failureBatchNum = new StringBuffer();
	    final StringBuffer failureBytesMoved = new StringBuffer();
	    final StringBuffer failureForestName = new StringBuffer();
	    final StringBuffer failureJobID = new StringBuffer();
	    final StringBuffer failureTime = new StringBuffer();
	    	    
		WriteBatcher ihb1 =  dmManager.newWriteBatcher();
		ihb1.withBatchSize(10);
		ihb1.onBatchSuccess(
		        batch -> {
		        	System.out.println("Success");
		        	System.out.println(batch.getJobBatchNumber());
		        	//System.out.println(batch  getJobResultsSoFar());
		        	System.out.println(batch.getBytesMoved());
		        	//System.out.println(batch.getForest()== null);
		        	System.out.println(batch.getJobTicket() == null);
		        	System.out.println(batch.getTimestamp()==null);
		        	//System.out.println(batch.getForest().getForestName()==null);
		        	//successBatchNum.append(batch.getForestBatchNumber());
		        	successBatchNum.append(batch.getJobBatchNumber());
		        	successBytesMoved.append(batch.getBytesMoved());
		        	//successForestName.append(batch.getForest().getForestName());
		        	successJobID.append(batch.getJobTicket().getJobId());
		        	successTime.append(batch.getTimestamp().getTime().getTime());
		         	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	System.out.println("Failure");
		        	//failureBatchNum.append(batch.getForestBatchNumber());
		        	failureBatchNum.append(batch.getJobBatchNumber());
  		        	failureBytesMoved.append(batch.getBytesMoved());
  		        	//failureForestName.append(batch.getForest().getForestName());
  		        	failureJobID.append(batch.getJobTicket().getJobId());
  		        	failureTime.append(batch.getTimestamp().getTime().getTime());
		      	    
		          });
		JobTicket jt= dmManager.startJob(ihb1);
	
		for (int i =0 ;i < 10; i++){
			String uri ="/local/json-"+ i;
			ihb1.add(uri, stringHandle);
		}
		
		for (int i =0 ;i < 5; i++){
			ihb1.add("", stringHandle);
		}
		ihb1.flushAndWait();
		dmManager.stopJob(jt);
		
		System.out.println(successBatchNum.toString());
		System.out.println(successBytesMoved.toString());
		System.out.println(successForestName.toString());
		System.out.println(successJobID.toString());
		System.out.println(successTime.toString());
		
		System.out.println(failureBatchNum.toString());
		System.out.println(failureBytesMoved.toString());
		System.out.println(failureForestName.toString());
		System.out.println(failureJobID.toString());
		System.out.println(failureTime.toString());
	}
	
	private int count(String s, String in){
		int i = 0;
		Pattern p = Pattern.compile(in);
		Matcher m = p.matcher( s );
		while (m.find()) {
		    i++;
		}
		return i;
	}
	
	//ISSUE # 28- expected to fail in ea2
	@Test
	public void testWithInvalidValues() throws Exception{
		
		final String query1 = "fn:count(fn:doc())";
		WriteBatcher ihb1 = dmManager.newWriteBatcher();
		
		try{
			ihb1.withBatchSize(-20);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}		
				
		WriteBatcher ihb2 = dmManager.newWriteBatcher();
		try{
			ihb2.withBatchSize(0);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
				
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);
		
		WriteBatcher ihb3 = dmManager.newWriteBatcher();
		try{
			ihb3.withThreadCount(-4);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		
		try{
			ihb3.withThreadCount(0);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch (Exception e){
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 0);		
	}
		
	@Test
	public void testInsertoReadOnlyForest() throws Exception{
		Map <String, String> properties = new HashMap<>();
		properties.put("updates-allowed", "read-only");
		for (int i =0 ; i < clusterInfo.size(); i++)
		 	changeProperty(properties,"/manage/v2/forests/"+dbName+"-"+(i+1)+"/properties");
		final String query1 = "fn:count(fn:doc())";
	 	
       	final MutableInt successCount = new MutableInt(0);
       	
       	final MutableBoolean failState = new MutableBoolean(false);
       	final MutableInt failCount = new MutableInt(0);
       	
		for (int i =0 ; i < clusterInfo.size(); i++)
		 	changeProperty(properties,"/manage/v2/forests/"+dbName+"-"+(i+1)+"/properties");
		 	
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(25);
		ihb2.onBatchSuccess(
		        batch -> {
		        	
		        	successCount.add(batch.getItems().length);
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  failState.setTrue();
		        	  failCount.add(batch.getItems().length);
		          });
		
		dmManager.startJob(ihb2);
		for (int j =0 ;j < 20; j++){
			String uri ="/local/json-"+ j;
			ihb2.addAs(uri, stringHandle);
		}
	
		ihb2.flushAndWait();
		
		properties.put("updates-allowed", "all");
		for (int i =0 ; i < clusterInfo.size(); i++)
		 	changeProperty(properties,"/manage/v2/forests/"+dbName+"-"+(i+1)+"/properties");
      
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
		
		Assert.assertTrue(failState.booleanValue());
		
		Assert.assertTrue(successCount.intValue() == 0);
		Assert.assertTrue(failCount.intValue() == 20);
	}
	
	// Git Issue # 41
	@Ignore
	public void testDuplicates() throws Exception{
		Map <String, String> properties = new HashMap<>();
		properties.put("updates-allowed", "read-only");
		
		final String query1 = "fn:count(fn:doc())";
	 	
       	final MutableBoolean successState = new MutableBoolean(false);
       	final MutableBoolean failState = new MutableBoolean(false);
       	
       	final MutableInt successCount = new MutableInt(0);
       	final MutableInt failureCount = new MutableInt(0);
       	      	
		WriteBatcher ihb1 =  dmManager.newWriteBatcher();
		ihb1.withBatchSize(5);
	
		dmManager.startJob(ihb1);
		
		for (int i =0 ;i < 20; i++){
			String uri ="/local/json-"+ i;
			ihb1.add(uri, stringHandle);
		}
	
		ihb1.flushAndWait();
		
	 	Number response = dbClient.newServerEval().xquery(query1).eval().next().getNumber();
    	Assert.assertTrue(response.intValue()==20);
    	
		for (int i =0 ; i < clusterInfo.size() -1; i++)
		 	changeProperty(properties,"/manage/v2/forests/"+dbName+"-"+(i+1)+"/properties");
       	   	
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		dmManager.startJob(ihb2);
		ihb2.withBatchSize(1);
		ihb2.onBatchSuccess(
		        batch -> {
		        	successCount.add(batch.getItems().length);
		        	successState.setTrue();
		        	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  failureCount.add(batch.getItems().length);
		        	  failState.setTrue();
		        	  
		          });
		
		for (int j =0 ;j < 21; j++){
			String uri ="/local/json-"+ j;
			ihb2.add(uri, stringHandle);
		}

		ihb2.flushAndWait();
		
		properties.put("updates-allowed", "all");
		for (int i =0 ; i < clusterInfo.size(); i++)
		 	changeProperty(properties,"/manage/v2/forests/"+dbName+"-"+(i+1)+"/properties");
    	
    	System.out.println("Success count: "+successCount);
      	System.out.println("Failure count: "+failureCount);
      	
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==21);
	
    	Assert.assertTrue(successState.booleanValue());
		Assert.assertTrue(failState.booleanValue());
	}
	
	@Test
	public void testInsertoDisabledDB() throws Exception{
		Map <String, String> properties = new HashMap<>();
		properties.put("enabled", "false");
		final String query1 = "fn:count(fn:doc())";
	 	
       	final MutableInt successCount = new MutableInt(0);
       	
       	final MutableBoolean failState = new MutableBoolean(false);
       	final MutableInt failCount = new MutableInt(0);
       	
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(30);
		dmManager.startJob(ihb2);
		
		for (int j =0 ;j < 20; j++){
			String uri ="/local/json-"+ j;
			ihb2.add(uri, stringHandle);
		}
		
		ihb2.onBatchSuccess(
		        batch -> {
		        	
		        	successCount.add(batch.getItems().length);
		        	
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  failState.setTrue();
		        	  failCount.add(batch.getItems().length);
		          });

		changeProperty(properties,"/manage/v2/databases/"+dbName+"/properties");
		
		ihb2.flushAndWait();
		
		properties.put("enabled", "true");
		changeProperty(properties,"/manage/v2/databases/"+dbName+"/properties");
		
    	System.out.println("Fail : "+failCount.intValue());
    	System.out.println("Success : "+successCount.intValue());
    	System.out.println("Count : "+ dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    	
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
		Assert.assertTrue(failState.booleanValue());
		
		Assert.assertTrue(failCount.intValue() == 20);	
	}
		
	@Test
	public void testServerXQueryTransformSuccess() throws Exception
    {      
		   final String query1 = "fn:count(fn:doc())";
		   Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
		   final MutableInt successCount = new MutableInt(0);
	       	
	       final MutableBoolean failState = new MutableBoolean(false);
	       final MutableInt failCount = new MutableInt(0);
           TransformExtensionsManager transMgr = 
                        dbClient.newServerConfigManager().newTransformExtensionsManager();
           ExtensionMetadata metadata = new ExtensionMetadata();
           metadata.setTitle("Adding attribute xquery Transform");
           metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
           metadata.setProvider("MarkLogic");
           metadata.setVersion("0.1");
           // get the transform file from add-attr-xquery-transform.xqy
           File transformFile = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"add-attr-xquery-transform.xqy"));
           FileHandle transformHandle = new FileHandle(transformFile);
           transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
           
           ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
           transform.put("name", "Lang");
           transform.put("value", "English");
           
           String xmlStr1 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so foo</foo>";
           String xmlStr2 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so bar</foo>";
                  
           //Use WriteBatcher to write the same files.                      
           WriteBatcher ihb1 =  dmManager.newWriteBatcher();
	   	   ihb1.withBatchSize(2);
	   	   ihb1.withTransform(transform);
	   	   ihb1.onBatchSuccess(
	   			   batch -> {
		        	
	   				   successCount.add(batch.getItems().length);
		        	
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  throwable.printStackTrace();
		        	  failState.setTrue();
		        	  failCount.add(batch.getItems().length);
		          });
	   	   dmManager.startJob(ihb1);
           StringHandle handleFoo = new StringHandle();
           handleFoo.set(xmlStr1);
           handleFoo.setFormat(Format.XML);
           
           StringHandle handleBar = new StringHandle();
           handleBar.set(xmlStr2);
           handleBar.setFormat(Format.XML);
           
           String uri1 = null;
           String uri2 = null;
         
           for (int i = 0; i < 4; i++) {
                  uri1 = "foo" + i + ".xml";
                  uri2 = "bar" + i + ".xml";
                  ihb1.addAs(uri1, handleFoo).addAs(uri2, handleBar);
           }
           // Flush
           ihb1.flushAndWait();
   		   Assert.assertFalse(failState.booleanValue());
   		   Assert.assertTrue(successCount.intValue()==8);
           Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==8);
    }
	
	@Test
	public void testServerXQueryTransformFailure() throws Exception
    {      
		   final String query1 = "fn:count(fn:doc())";             
		   final MutableInt successCount = new MutableInt(0);
	       	
	       final MutableBoolean failState = new MutableBoolean(false);
	       final MutableInt failCount = new MutableInt(0);
           TransformExtensionsManager transMgr = 
                        dbClient.newServerConfigManager().newTransformExtensionsManager();
           ExtensionMetadata metadata = new ExtensionMetadata();
           metadata.setTitle("Adding attribute xquery Transform");
           metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
           metadata.setProvider("MarkLogic");
           metadata.setVersion("0.1");
           // get the transform file from add-attr-xquery-transform.xqy
           File transformFile = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"add-attr-xquery-transform.xqy"));
           FileHandle transformHandle = new FileHandle(transformFile);
           transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
           
           ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
           transform.put("name", "Lang");
           transform.put("value", "English");
           
           String xmlStr1 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so foo</foo>";
           String xmlStr2 = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so bar</foo";
                  
           //Use WriteBatcher to write the same files.                      
           WriteBatcher ihb1 =  dmManager.newWriteBatcher();
	   	   ihb1.withBatchSize(1);
	   	   ihb1.withTransform(transform);
	   	   ihb1.onBatchSuccess(
	   			   batch -> {
		        	
	   				   successCount.add(batch.getItems().length);
		        	
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  failState.setTrue();
		        	  failCount.add(batch.getItems().length);
		          });
	   	   
           StringHandle handleFoo = new StringHandle();
           handleFoo.set(xmlStr1);
           handleFoo.setFormat(Format.XML);
           
           StringHandle handleBar = new StringHandle();
           handleBar.set(xmlStr2);
           handleBar.setFormat(Format.XML);
           
           String uri1 = null;
           String uri2 = null;
           
           for (int i = 0; i < 4; i++) {
                  uri1 = "foo" + i + ".xml";
                  uri2 = "bar" + i + ".xml";
                  ihb1.add(uri1, handleFoo).add(uri2, handleBar);;
           }
           // Flush
           ihb1.flushAndWait();
           Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==4);
   		   Assert.assertTrue(failState.booleanValue());
   		   Assert.assertTrue(successCount.intValue()==4);
   		   Assert.assertTrue(failCount.intValue()==4);
   		   
   		   clearDB(port);
   		   failCount.setValue(0);
   		   successCount.setValue(0);
   		   failState.setFalse();
   		   
   		   // with non-existent transform
           
   		   ServerTransform transform1 = new ServerTransform("abcd");
   		   WriteBatcher ihb2 =  dmManager.newWriteBatcher();
           ihb2.withBatchSize(1);
	   	   ihb2.withTransform(transform1);
	   	   ihb2.onBatchSuccess(
	   			   batch -> {
		        	
	   				   successCount.add(batch.getItems().length);
		        	
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  failState.setTrue();
		        	  failCount.add(batch.getItems().length);
		          });
           for (int i = 0; i < 4; i++) {
               uri1 = "foo" + i + ".xml";
               ihb2.add(uri1, handleFoo);
        }
        // Flush
        ihb2.flushAndWait();
        Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
	    Assert.assertTrue(failState.booleanValue());
	    Assert.assertTrue(successCount.intValue()==0);
	    Assert.assertTrue(failCount.intValue()==4);
    }
	
	// Multiple threads writing to same WHB object with unique uri's
	@Test
	public void testAddMultiThreadedSuccess() throws Exception{
		
		final String query1 = "fn:count(fn:doc())";
		ihbMT =  dmManager.newWriteBatcher();
       	ihbMT.withBatchSize(100);
       	ihbMT.onBatchSuccess(
		        batch -> {
		        	System.out.println("Batch size "+batch.getItems().length);
		        	for(WriteEvent w:batch.getItems()){
		        		//System.out.println("Success "+w.getTargetUri());
		        	}
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  throwable.printStackTrace();
		        		for(WriteEvent w:batch.getItems()){
		        			System.out.println("Failure "+w.getTargetUri());
		        		}		       
		});
		dmManager.startJob(ihbMT);

       	class MyRunnable implements Runnable {
       	  
       	  @Override
       	  public void run() {
         		
           		for (int j =0 ;j < 100; j++){
    				String uri ="/local/json-"+ j+"-"+Thread.currentThread().getId();
    				System.out.println("Thread name: "+Thread.currentThread().getName()+"  URI:"+ uri);
    				ihbMT.add(uri, fileHandle);
    				
    				
    			}
           		ihbMT.flushAndWait();
       	  }  
           		
       	} 
       	Thread t1,t2,t3;
       	t1 = new Thread(new MyRunnable());
       	t2 = new Thread(new MyRunnable());
       	t3 = new Thread(new MyRunnable());
       	t1.start();
       	t2.start();
       	t3.start();
       	
       	t1.join();
       	t2.join();
       	t3.join();
       	    	
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==300);
	}
	
	//ISSUE 48
	@Test
	public void testAddMultiThreadedFailureEventCount() throws Exception{
		
		final MutableInt eventCount = new MutableInt(0);
		ihbMT =  dmManager.newWriteBatcher();
       	ihbMT.withBatchSize(105);
       	ihbMT.onBatchSuccess(
		        batch -> {
		        	synchronized(eventCount){
		        		 eventCount.add(batch.getItems().length);
		        	}
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  	synchronized(eventCount){
			        		 eventCount.add(batch.getItems().length);
			        	}
		      
		});
		dmManager.startJob(ihbMT);

       	class MyRunnable implements Runnable {
       	  
       	  @Override
       	  public void run() {
         		
           		for (int j =0 ;j < 100; j++){
    				String uri ="/local/json-"+ j;
    				//System.out.println("Thread name: "+Thread.currentThread().getName()+"  URI:"+ uri);
    				ihbMT.add(uri, fileHandle);
    			}
           		ihbMT.flushAndWait();
       	  }  
           		
       	} 
       	Thread t1,t2,t3;
       	t1 = new Thread(new MyRunnable());
       	t2 = new Thread(new MyRunnable());
       	t3 = new Thread(new MyRunnable());
       	t1.start();
      
       	t2.start();
   
       	t3.start();
       	
       	t1.join();
       	t2.join();
       	t3.join();
       	System.out.println(eventCount.intValue());
       	Assert.assertTrue(eventCount.intValue()==300);
		
	}
	
	//ISSUE 85
	// Multiple threads writing to same WHB object with unique uri's and with thread count =10 and txsize =3
	@Test
	public void testAddMultiThreadedwithTransactionsizeSuccess() throws Exception{
		
		final String query1 = "fn:count(fn:doc())";
		final MutableInt count = new MutableInt(0);
		
		ihbMT =  dmManager.newWriteBatcher();
       	ihbMT.withBatchSize(99);
       	ihbMT.withThreadCount(10);
       //	ihbMT.withTransactionSize(3);
       	
       	ihbMT.onBatchSuccess(
		        batch -> {
		        	System.out.println("Batch size "+batch.getItems().length);
		        	for(WriteEvent w:batch.getItems()){
		        		//System.out.println("Success "+w.getTargetUri());
		        	}
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  throwable.printStackTrace();
		        		for(WriteEvent w:batch.getItems()){
		        			System.out.println("Failure "+w.getTargetUri());
		        		}		       
		});
		dmManager.startJob(ihbMT);

       	class MyRunnable implements Runnable {
       	  
       	  @Override
       	  public void run() {
         		
           		for (int j =0 ;j < 5000; j++){
    				String uri ="/local/json-"+ j+"-"+Thread.currentThread().getId();
    				System.out.println("Thread name: "+Thread.currentThread().getName()+"  URI:"+ uri);
    				ihbMT.add(uri, fileHandle);   				
    			}
           		ihbMT.flushAndWait();
       	  }  
           		
       	} 
       	
       	class CountRunnable implements Runnable {
	       	  
	       	  @Override
	       	  public void run() {
	       		  try {
					Thread.currentThread().sleep(15000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       		  Set threads = Thread.getAllStackTraces().keySet();
	       		  Iterator<Thread> iter = threads.iterator();
	       		  while(iter.hasNext()){
	       			  Thread t =  iter.next();
	       			  if(t.getName().contains("pool-1-thread-"))
	       				  System.out.println(t.getName());
	       					  count.add(1);
	       			  
	       		  }
	       		  
	       	  }  
	           		
       	} 
	    Thread countT;
	    countT = new Thread(new CountRunnable());
	    
	    
       	Thread t1,t2,t3;
       	t1 = new Thread(new MyRunnable());
       	t2 = new Thread(new MyRunnable());
       	t3 = new Thread(new MyRunnable());
       	
       	countT.start();
       	t1.start();
       	t2.start();
       	t3.start();
       	
       	countT.join();
       	
       	t1.join();
       	t2.join();
       	t3.join();
       	// Verify more than 1 thread is spawned
       	Assert.assertTrue(count.intValue() > 1);
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==15000);
		clearDB(port);
	}
		
	
	// Multiple threads writing to same WHB object with unique uri's and with thread count =10 and txsize =3 but with small number of docs
	// Failing intermittently with rewriteWHB branch
	@Ignore
	public void testAddMultiThreadedLessDocsSuccess() throws Exception{
		
		final String query1 = "fn:count(fn:doc())";
		final MutableInt count = new MutableInt(0);
		
		ihbMT =  dmManager.newWriteBatcher();
       	ihbMT.withBatchSize(99);
       	ihbMT.withThreadCount(10);
       	//ihbMT.withTransactionSize(3);
       	
       	ihbMT.onBatchSuccess(
		        batch -> {
		        	System.out.println("Batch size "+batch.getItems().length);
		        	for(WriteEvent w:batch.getItems()){
		        		//System.out.println("Success "+w.getTargetUri());
		        	}		        	
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  throwable.printStackTrace();
		        		for(WriteEvent w:batch.getItems()){
		        			System.out.println("Failure "+w.getTargetUri());
		        		}
		});
		dmManager.startJob(ihbMT);

       	class MyRunnable implements Runnable {
       	  
       	  @Override
       	  public void run() {
         		
           		for (int j =0 ;j < 15; j++){
    				String uri ="/local/json-"+ j+"-"+Thread.currentThread().getId();
    				System.out.println("Thread name: "+Thread.currentThread().getName()+"  URI:"+ uri);
    				ihbMT.add(uri, fileHandle);  				
    			}
           		ihbMT.flushAndWait();
       	  }  
           		
       	} 
       	
       	class CountRunnable implements Runnable {
	       	  
	       	  @Override
	       	  public void run() {
	       		  try {
					Thread.currentThread().sleep(15000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       		  Set threads = Thread.getAllStackTraces().keySet();
	       		  Iterator<Thread> iter = threads.iterator();
	       		  while(iter.hasNext()){
	       			  Thread t =  iter.next();
	       			  if(t.getName().contains("pool-1-thread-"))
	       				  System.out.println(t.getName());
	       					  count.add(1);
	       		  }
	       		  
	       	  }  
	           		
       	} 
	    Thread countT;
	    countT = new Thread(new CountRunnable());
	    
	    
       	Thread t1,t2,t3;
       	t1 = new Thread(new MyRunnable());
       	t2 = new Thread(new MyRunnable());
       	t3 = new Thread(new MyRunnable());
       	
       	countT.start();
       	t1.start();
       	t2.start();
       	t3.start();
       	
       	countT.join();
       	
       	t1.join();
       	t2.join();
       	t3.join();
       	
       	//Assert.assertTrue(count.intValue()==10);
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==45);
		clearDB(port);
	}
	
	// Multiple threads writing to same WHB object with duplicate uri's and with thread count =10 and txsize =3
	// currently causing deadlock and at the completion of the test, clearDB() causes forests to go to middle closing state when run against reWriteHB branch
	// Git Issue # 62
	@Ignore
	public void testAddMultiThreadedwithThreadCountFailure() throws Exception{
		
		final MutableInt count = new MutableInt(0);
		final MutableInt eventCount = new MutableInt(0);
		
		ihbMT =  dmManager.newWriteBatcher();
       	ihbMT.withBatchSize(99);
       	ihbMT.withThreadCount(10);
      // 	ihbMT.withTransactionSize(3);
       	
       	ihbMT.onBatchSuccess(
		        batch -> {
		        	synchronized(eventCount){
		        		 eventCount.add(batch.getItems().length);
		        	}
		        	
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  throwable.printStackTrace();
		        	  synchronized(eventCount){
			        		 eventCount.add(batch.getItems().length);
			        	}
		       
		          });
		dmManager.startJob(ihbMT);

       	class MyRunnable implements Runnable {
       	  
       	  @Override
       	  public void run() {
         		
           		for (int j =0 ;j < 5000; j++){
    				String uri ="/local/json-"+ j;
    				ihbMT.add(uri, fileHandle);  				
    			}
           		ihbMT.flushAndWait();
       	  }  
           		
       	} 
       	
       	class CountRunnable implements Runnable {
	       	  
	       	  @Override
	       	  public void run() {
	       		  try {
					Thread.currentThread().sleep(15000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       		  Set threads = Thread.getAllStackTraces().keySet();
	       		  Iterator<Thread> iter = threads.iterator();
	       		  while(iter.hasNext()){
	       			  Thread t =  iter.next();
	       			  if(t.getName().contains("pool-1-thread-"))
	       					  count.add(1);
	       			  
	       		  }
	       		  
	       	  }  
	           		
       	} 
	    Thread countT;
	    countT = new Thread(new CountRunnable());
	    	    
       	Thread t1,t2,t3;
       	t1 = new Thread(new MyRunnable());
       	t2 = new Thread(new MyRunnable());
       	t3 = new Thread(new MyRunnable());
       	
       	countT.start();
       	t1.start();
       	t2.start();
       	t3.start();
       	
       	countT.join();
       	
       	t1.join();
       	t2.join();
       	t3.join();
       	
       	Assert.assertTrue(count.intValue()==15000);
       	Assert.assertTrue(eventCount.intValue()==10);
		
	}
	
	//ISSUE # 58
	@Ignore
	public void testTransactionSize() throws Exception{
		try{
			final String query1 = "fn:count(fn:doc())";
		 	
	       	final MutableInt successCount = new MutableInt(0);
	       	
	       	final MutableBoolean failState = new MutableBoolean(false);
	       	final MutableInt failCount = new MutableInt(0);
	           	
			WriteBatcher ihb2 =  dmManager.newWriteBatcher();
			ihb2.withBatchSize(480);
		//	ihb2.withTransactionSize(5);
			dmManager.startJob(ihb2);
							
			ihb2.onBatchSuccess(
			        batch -> {
			        	
			        	successCount.add(batch.getItems().length);
			        	 System.out.println("Success Batch size "+batch.getItems().length);
				        	for(WriteEvent w:batch.getItems()){
				        		System.out.println("Success "+w.getTargetUri());
				        	}
			        	
			        	}
			        )
			        .onBatchFailure(
			          (batch, throwable) -> {
			        	  throwable.printStackTrace();
			        	  System.out.println("Failure Batch size "+batch.getItems().length);
				        	for(WriteEvent w:batch.getItems()){
				        		System.out.println("Failure "+w.getTargetUri());
				        	}
			        	  failState.setTrue();
			        	  failCount.add(batch.getItems().length);
			          });
			for (int j =0 ;j < 500; j++){
				String uri ="/local/ABC-"+ j;
				ihb2.add(uri, stringHandle);
			}
		
		    ihb2.flushAndWait();
		    
	    	System.out.println("Fail : "+failCount.intValue());
	    	System.out.println("Success : "+successCount.intValue());
	    	System.out.println("Count : "+ dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==500);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Adding 25000 docs with thread count = 20 
	@Test
	public void testThreadSize() throws Exception{
		try{
			final String query1 = "fn:count(fn:doc())";
		 	
	       	final MutableInt successCount = new MutableInt(0);
	       	
	       	final MutableBoolean failState = new MutableBoolean(false);
	       	final MutableInt failCount = new MutableInt(0);
	       	final AtomicBoolean count = new AtomicBoolean(false);
	       	
	           	
			WriteBatcher ihb2 =  dmManager.newWriteBatcher();
			ihb2.withBatchSize(200);
		//	ihb2.withTransactionSize(2);
			ihb2.withThreadCount(20);
			dmManager.startJob(ihb2);
						
			ihb2.onBatchSuccess(
			        batch -> {
			        	
			        	successCount.add(batch.getItems().length);
			          }
			        )
			        .onBatchFailure(
			          (batch, throwable) -> {
			        	  throwable.printStackTrace();
			        	  System.out.println("Failure Batch size "+batch.getItems().length);
				        	for(WriteEvent w:batch.getItems()){
				        		System.out.println("Failure "+w.getTargetUri());
				        	}
			        	  failState.setTrue();
			        	  failCount.add(batch.getItems().length);
			          });
			
	       	class MyRunnable implements Runnable {
	       	  
	       	  @Override
	       	  public void run() {
	       		  try {
	       			//Sleep for 15 seconds so that the threads are spawned
					Thread.currentThread().sleep(10000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       		  Set<Thread> threads = Thread.getAllStackTraces().keySet();
	       		  Iterator<Thread> iter = threads.iterator();
	       		  Map<String,Integer> threadMap = new HashMap<>(); 
	       		  while(iter.hasNext()){
	       			  Thread t =  iter.next();
	       			  String threadName = t.getName();
	       			  if(threadName.contains("pool")){
	       				  int i = threadName.indexOf('-', 1 + threadName.indexOf('-'));
	       				  String poolname = threadName.substring(0, i);
	       				  System.out.println("poolname: "+poolname);
	       				  if(! threadMap.containsKey(poolname)){
	       					  threadMap.put(poolname, 1);
	       				  }
	       				  else{
	       					threadMap.put(poolname, new Integer(threadMap.get(poolname)+1));
	       				  }
	       			 }
	       		}
	       		Iterator<Entry<String, Integer>>  it = threadMap.entrySet().iterator();
	       	    while (it.hasNext()) {
	       	        Map.Entry<String,Integer> pair = (Map.Entry)it.next();
	       	        System.out.println("Thread pool: "+ pair.getKey() + " = " + pair.getValue()+" Threads");
	       	        if(pair.getValue()==20){
	       	        	count.set(true);
	       	        }
	       	        it.remove(); 
	       	    }  
	       		  
	       	  }  
	           		
	       	} 
	       	Thread t1;
	       	t1 = new Thread(new MyRunnable());
	       	t1.start();
	       	
			for (int j =0 ;j < 25000; j++){
				String uri ="/local/ABC-"+ j;
				ihb2.add(uri, stringHandle);
			}
		
		    ihb2.flushAndWait();
		    t1.join();
		    
	    	System.out.println("Fail : "+failCount.intValue());
	    	System.out.println("Success : "+successCount.intValue());
	    	System.out.println("Count : "+ dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	    	// Confirms that more than one thread is spawned
	    	Assert.assertTrue(count.get());
	    	// Confirms that the number of docs inserted = 50000
	    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==25000);
	    	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNPECallBack() throws Exception{
		
		final String query1 = "fn:count(fn:doc())";
	 	
       	final MutableInt successCount = new MutableInt(0);
       	
       	final MutableBoolean failState = new MutableBoolean(false);
       	final MutableInt failCount = new MutableInt(0);
          	
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(5);
		dmManager.startJob(ihb2);
		
		ihb2.onBatchSuccess(
		        batch -> {
		        	String s= null;
		        	s.length();
		        	System.out.println("Success host : "+batch.getClient().getHost());
		        	System.out.println(batch.getItems().length);
		        	successCount.add(batch.getItems().length);
		        	
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  failState.setTrue();
		        	  failCount.add(batch.getItems().length);
		          });


		for (int j =0 ;j < 30; j++){
			String uri ="/local/json-"+ j;
			ihb2.add(uri, stringHandle);
		}
							
    	System.out.println("Fail : "+failCount.intValue());
    	System.out.println("Success : "+successCount.intValue());
    	System.out.println("Count : "+ dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	}
	
	@Test
	public void testEmptyFlush() throws Exception{
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(5);
			
		ihb2.onBatchSuccess(
		        batch -> {
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		          
		          }
		);
		try{
			ihb2.flushAndWait();
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue(e instanceof IllegalStateException);
		}
	}
	
	@Test
	public void testAddMultiThreadedStopJob() throws Exception{
		
		final String query1 = "fn:count(fn:doc())";
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() ==0);
		ihbMT =  dmManager.newWriteBatcher();
       	ihbMT.withBatchSize(11);
       	ihbMT.onBatchSuccess(
		        batch -> {
		        	System.out.println("Batch size "+batch.getItems().length);
		        	for(WriteEvent w:batch.getItems()){
		        		System.out.println("Success "+w.getTargetUri());
		        	}
		        })
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  throwable.printStackTrace();
		        		for(WriteEvent w:batch.getItems()){
		        			System.out.println("Failure "+w.getTargetUri());
		        		}		       
		});
		writeTicket = dmManager.startJob(ihbMT);
				   		
       	class MyRunnable implements Runnable {
       	  
       	  @Override
       	  public void run() {
         		
           		for (int j =0 ;j < 100; j++){
    				String uri ="/local/multi-"+ j+"-"+Thread.currentThread().getId();
    				System.out.println("Thread name: "+Thread.currentThread().getName()+"  URI:"+ uri);
    				ihbMT.add(uri, fileHandle);
    				if(j ==80){
    					ihbMT.flushAndWait();
    					dmManager.stopJob(writeTicket);
    				}		
           		}           		
       	  }  
           		
       	} 
       	Thread t1,t2;
       	t1 = new Thread(new MyRunnable());
       	t2 = new Thread(new MyRunnable());
      
       	t1.start();
       	t2.start();
             	
       	t1.join();
       	t2.join();
               	
       	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() >=80);
       	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() <=160);
	}
	
	@Test
	public void testAddMultiStartJob() throws Exception{
		
		final String query1 = "fn:count(fn:doc())";
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() ==0);
		ihbMT =  dmManager.newWriteBatcher();
       	ihbMT.withBatchSize(11);
       	ihbMT.onBatchSuccess(
		        batch -> {
		        	System.out.println("Batch size "+batch.getItems().length);
		        	for(WriteEvent w:batch.getItems()){
		        		System.out.println("Success "+w.getTargetUri());
		        	}
		        })
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  throwable.printStackTrace();
		        		for(WriteEvent w:batch.getItems()){
		        			System.out.println("Failure "+w.getTargetUri());
		        		}		       
		});
		
        
		   		
       	class MyRunnable implements Runnable {
       	  
       	  @Override
       	  public void run() {
       		  writeTicket = dmManager.startJob(ihbMT);
  		
           	  for (int j =0 ;j < 100; j++){
    				String uri ="/local/multi-"+ j+"-"+Thread.currentThread().getId();
    				System.out.println("Thread name: "+Thread.currentThread().getName()+"  URI:"+ uri);
    				ihbMT.add(uri, fileHandle);
    				if(j ==80){
    					dmManager.startJob(ihbMT);
    					ihbMT.flushAndWait();
    				}
    			}
           		ihbMT.flushAndWait();
		 }  
           		
       	} 
       	Thread t1,t2;
       	t1 = new Thread(new MyRunnable());
       	t2 = new Thread(new MyRunnable());
      
       	t1.start();
       	t2.start();
       	
       	t1.join();
        t2.join();
                    	
       	Assert.assertEquals(200,dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
       	dmManager.stopJob(writeTicket);
	}

	@Test
	public void testInserttoDisabledAppServer() throws Exception{
		
		final String query1 = "fn:count(fn:doc())";
	 	Map<String,String> properties = new HashMap<>();
      	
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(3000);
		
		ihb2.onBatchSuccess(
		        batch -> {
		         	System.out.println("Success Batch size "+batch.getItems().length);
		        	for(WriteEvent w:batch.getItems()){
		        		System.out.println("Success "+w.getTargetUri());
		        	}
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  	throwable.printStackTrace();
		        	 	System.out.println("Failure Batch size "+batch.getItems().length);
			        	for(WriteEvent w:batch.getItems()){
			        		System.out.println("Failure "+w.getTargetUri());
			        	}
		          });
		
		dmManager.startJob(ihb2);
		for (int j =0 ;j < 200; j++){
			String uri ="/local/json-"+ j;
			ihb2.add(uri, stringHandle);
		}

		properties.put("server-name",server);
		properties.put("group-name", "Default");
		properties.put("enabled", "false");
		changeProperty(properties,"/manage/v2/servers/"+server+"/properties");
		Thread.currentThread().sleep(1000L);
		ihb2.flushAndWait();
		
		properties.put("enabled", "true");
		changeProperty(properties,"/manage/v2/servers/"+server+"/properties");
		
		System.out.println("testInserttoDisabledAppServer: Size is "+ dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
    	Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue()==0);
    	
	}
	
	// ea3
	@Ignore
	public void testDisableAppServerDuringInsert() throws Exception{
		
		Thread t1 = new Thread(new StopServerRunnable());
     	t1.setName("Status Check");
     	  	
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(5);
				
		ihb2.onBatchSuccess(
		        batch -> {
		         	System.out.println("Success Batch size "+batch.getItems().length);
		        	for(WriteEvent w:batch.getItems()){
		        		System.out.println("Success "+w.getTargetUri());
		        	}		        	
		        	}
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  	throwable.printStackTrace();
		        	 	System.out.println("Failure Batch size "+batch.getItems().length);
			        	for(WriteEvent w:batch.getItems()){
			        		System.out.println("Failure "+w.getTargetUri());
			        	}
		          });
		
		dmManager.startJob(ihb2);
		t1.start();
		
		for (int j =0 ;j < 2000; j++){
			String uri ="/local/json-"+ j;
			ihb2.add(uri, fileHandle);
		}
				
		ihb2.flushAndWait();
		t1.join();   	
	}
	class StopServerRunnable implements Runnable {
	  final String query1 = "fn:count(fn:doc())";
	  Map<String,String> properties = new HashMap<>();
	
   	  @Override
   	  public void run() {
   		  properties.put("server-name",server);
		  properties.put("group-name", "Default");
		  properties.put("enabled", "false");
   		  boolean state = true;
   		  while (state){
   			 int count =dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue();
   			 System.out.println("Count is "+count);
   			 if(count >= 100){
   				
     			changeProperty(properties,"/manage/v2/servers/"+server+"/properties");
     			state=false;
   			 }
   				
   		  }
   	  }  
       		
  } 
	// EA 3 - We need a better way of getting to know that DB is disabled and how to assert on counts.
	@Ignore
	public void testDisableDBDuringInsert() throws Exception{
		
	    Thread t1 = new Thread(new DisabledDBRunnable());
		MutableBoolean failCheck = new MutableBoolean(false);
		MutableInt successCount = new MutableInt(0);
		MutableInt failureCount = new MutableInt(0);
		
     	t1.setName("Status Check");
     	Map<String,String> properties = new HashMap<>();  	
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(5);
				
		ihb2.onBatchSuccess(
		        batch -> {
		        	successCount.add(batch.getItems().length);		        	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  failCheck.setTrue();
		        	  failureCount.add(batch.getItems().length);
		        	  throwable.printStackTrace();
		          });
		dmManager.startJob(ihb2);
		t1.start();
		
		for (int j =0 ;j < 1000; j++){
			String uri ="/local/json-"+ j;
			ihb2.add(uri, fileHandle);
		}
				
		ihb2.flushAndWait();
		t1.join();
		properties.put("enabled", "true");
		changeProperty(properties,"/manage/v2/databases/"+dbName+"/properties");
		Assert.assertTrue(failCheck.booleanValue());
		Assert.assertTrue(successCount.intValue() >= 100);
		Assert.assertTrue(successCount.intValue() < 1000);
		Assert.assertTrue(failureCount.intValue() <= 900);
	}
	
	class DisabledDBRunnable implements Runnable {
	  final String query1 = "fn:count(fn:doc())";
	  Map<String,String> properties = new HashMap<>();
	
   	  @Override
   	  public void run() {
   	
		  properties.put("enabled", "false");			
   		  boolean state = true;
   		  while (state){
   			 int count =dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue();
   			 System.out.println("Count is "+count);
   			 if(count >= 100){
   				changeProperty(properties,"/manage/v2/databases/"+dbName+"/properties");
     			
     			state=false;
   			 }
   				
   		  }
   	  }  
       		
  } 
	
	// ea 3
	@Ignore
	public void testOfflineForestStopServerDuringInsert() throws Exception{
		
		Thread t1 = new Thread(new OffLineForestStopServerRunnable());
		MutableBoolean failCheck = new MutableBoolean(false);
		MutableInt successCount = new MutableInt(0);
		MutableInt failureCount = new MutableInt(0);
		
     	t1.setName("Status Check");
     	Map<String,String> properties = new HashMap<>(); 
     	
     	properties.put("forest-name","WriteHostBatcher-1");
     	properties.put("availability","offline");
     	changeProperty(properties,"/manage/v2/forests/WriteHostBatcher-1/properties");
     	properties.clear();
     	
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(5);
				
		ihb2.onBatchSuccess(
		        batch -> {
		        	successCount.add(batch.getItems().length);
		        	
		        	System.out.println("Success host: "+batch.getClient().getHost());
		        	System.out.println("Success Batch size "+batch.getItems().length);
		        	for(WriteEvent w:batch.getItems()){
		        		System.out.println("Success "+w.getTargetUri());
		        	}		        	
		          }
		        )
		        .onBatchFailure(
		          (batch, throwable) -> {
		        	  failCheck.setTrue();
		        	  failureCount.add(batch.getItems().length);
		        	  throwable.printStackTrace();
		        	  System.out.println("Failure host: "+batch.getClient().getHost());
		        	  System.out.println("Failure Batch size "+batch.getItems().length);
			        	for(WriteEvent w:batch.getItems()){
			        		System.out.println("Failure "+w.getTargetUri());
			        	}	           
		          });
		dmManager.startJob(ihb2);
		t1.start();
		
		for (int j =0 ;j < 10000; j++){
			String uri ="/local/json-"+ j;
			ihb2.add(uri, fileHandle);
		}
				
		ihb2.flushAndWait();
		t1.join();
						
     	properties.put("forest-name","WriteHostBatcher-1");
     	properties.put("availability","online");
     	changeProperty(properties,"/manage/v2/forests/WriteHostBatcher-1/properties");
     	
		Assert.assertTrue(failCheck.booleanValue());
		Assert.assertTrue(successCount.intValue() >= 100);
		Assert.assertTrue(failureCount.intValue() <= 900);
    	
	}
	
	class OffLineForestStopServerRunnable implements Runnable {
	  final String query1 = "fn:count(fn:doc())";
	  Map<String,String> properties = new HashMap<>();
	
   	  @Override
   	  public void run() {
  		properties.put("host-name", hostNames[0]);
  		properties.put("group", "default");
  		properties.put("state", "shutdown");
			
   		  boolean state = true;
   		  while (state){
   			 int count =dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue();
   			 System.out.println("Count is "+count);
   			 if(count >= 100){
   				changeProperty(properties,"/manage/v2/hosts/"+hostNames[0]+"/properties");
     			
     			state=false;
   			 }
   				
   		  }
   		try {
			Thread.currentThread().sleep(35000L);
			properties.clear();
			properties.put("host-name", hostNames[0]);
			properties.put("group", "default");
			properties.put("state", "restart");
			changeProperty(properties,"/manage/v2/hosts/"+hostNames[0]+"/properties");
			
			Thread.currentThread().sleep(5000L);
	     	System.out.println(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	     	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	  }  
       		
  } 
	
}
