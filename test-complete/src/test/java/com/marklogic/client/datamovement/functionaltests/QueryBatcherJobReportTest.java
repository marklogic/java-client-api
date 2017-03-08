package com.marklogic.client.datamovement.functionaltests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.ApplyTransformListener;
import com.marklogic.client.datamovement.ApplyTransformListener.ApplyResult;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.functionaltests.util.DmsdkJavaClientREST;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;

public class QueryBatcherJobReportTest extends  DmsdkJavaClientREST {

	private static String dbName = "QueryBatcherJobReport";
	private static DataMovementManager dmManager = null;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";

	private static DatabaseClient dbClient;
	private static String host = "localhost";
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";
	private static JsonNode clusterInfo;


	private static StringHandle stringHandle;
	private static FileHandle fileHandle;

	private static DocumentMetadataHandle meta1;
	private static DocumentMetadataHandle meta2;
	

	private static String stringTriple;
	private static File fileJson;

	private static final String query1 = "fn:count(fn:doc())";
	private static String[] hostNames ;

	private static JobTicket queryTicket;

	/**
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		dbClient = DatabaseClientFactory.newClient(host, port, user, password, Authentication.DIGEST);
		dmManager = dbClient.newDataMovementManager();
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

				
	

		clusterInfo = ((DatabaseClientImpl) dbClient).getServices()
				.getResource(null, "internal/forestinfo", null, null, new JacksonHandle())
				.get();


		// FileHandle
		fileJson = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"dir.json"));
		fileHandle = new FileHandle(fileJson);
		fileHandle.setFormat(Format.JSON);
		meta1 = new DocumentMetadataHandle().withCollections("JsonTransform");

		//StringHandle
		stringTriple = "<?xml  version=\"1.0\" encoding=\"UTF-8\"?><foo>This is so foo</foo>";
		stringHandle = new StringHandle(stringTriple);
		stringHandle.setFormat(Format.XML);
		meta2 = new DocumentMetadataHandle().withCollections("XmlTransform");		

	
		
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(27).withThreadCount(10);
		ihb2.onBatchSuccess(
				batch -> {


				}
				)
		.onBatchFailure(
				(batch, throwable) -> {
					throwable.printStackTrace();
				});

		dmManager.startJob(ihb2);
		for (int j =0 ;j < 2000; j++){
			String uri ="/local/json-"+ j;
			ihb2.add(uri, meta1, fileHandle);
		}

		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 2000);

		for (int j =0 ;j < 2000; j++){
			String uri ="/local/string-"+ j;
			ihb2.add(uri, meta2, stringHandle);
		}

		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 4000);
		
		//Xquery transformation
		TransformExtensionsManager transMgr = 
				dbClient.newServerConfigManager().newTransformExtensionsManager();
		ExtensionMetadata metadata = new ExtensionMetadata();
		metadata.setTitle("Adding attribute xquery Transform");
		metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
		metadata.setProvider("MarkLogic");
		metadata.setVersion("0.1");
		// get the transform file
		File transformFile = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"add-attr-xquery-transform.xqy"));
		FileHandle transformHandle = new FileHandle(transformFile);
		transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);

		//JS Transformation
		File transformFile1 = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"javascript_transform.sjs"));
		FileHandle transformHandle1 = new FileHandle(transformFile1);
		transMgr.writeJavascriptTransform("jsTransform", transformHandle1);

		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		clearDB(port);
		associateRESTServerWithDB(server,"Documents");
		for (int i =0 ; i < clusterInfo.size(); i++){
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

	}

	@Test
	public void jobReport() throws Exception{

		
				
		AtomicInteger batchCount = new AtomicInteger(0);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicLong count = new AtomicLong(0);
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform")).withBatchSize(20).withThreadCount(20);			
		batcher.onUrisReady(batch->{
			if(dmManager.getJobReport(queryTicket).getSuccessBatchesCount() == batchCount.incrementAndGet()){
				count.incrementAndGet();
			}
		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());
		queryTicket = dmManager.startJob( batcher );
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);
		
		batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform")).withBatchSize(20).withThreadCount(20);
		batcher.onUrisReady(batch->{
			if(dmManager.getJobReport(queryTicket).getSuccessEventsCount() == successCount.addAndGet(batch.getItems().length)){
					count.incrementAndGet();
			}
			
		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());
		queryTicket = dmManager.startJob( batcher );
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);
		
		batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform")).withBatchSize(20).withThreadCount(20);
		batcher.onUrisReady(batch->{
			if(Math.abs(dmManager.getJobReport(queryTicket).getReportTimestamp().getTime().getTime()-Calendar.getInstance().getTime().getTime()) < 10000){
				count.incrementAndGet();
			}
			
		
		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());
		
		queryTicket = dmManager.startJob( batcher );
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);
		
		Assert.assertEquals(0, dmManager.getJobReport(queryTicket).getFailureEventsCount());
		Assert.assertEquals(dmManager.getJobReport(queryTicket).getSuccessBatchesCount(), batchCount.get());
		Assert.assertEquals(dmManager.getJobReport(queryTicket).getSuccessEventsCount(), successCount.get());
		Assert.assertEquals(dmManager.getJobReport(queryTicket).getSuccessBatchesCount() *3, count.get());
	
		
	}


	@Test
	public void testNullQdef() throws IOException, InterruptedException
	{	
			
		JsonNode node = null;
		
		JacksonHandle jacksonHandle = null;
		
		WriteBatcher wbatcher = dmManager.newWriteBatcher().withBatchSize(32).withThreadCount(20);
		
		try{
			wbatcher.addAs("/nulldoc",node);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch(IllegalArgumentException e){
			Assert.assertTrue(e.getMessage().equals("content must not be null"));
		}
		
		try{
			wbatcher.add("/nulldoc",jacksonHandle);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch(IllegalArgumentException e){
			Assert.assertTrue(e.getMessage().equals("contentHandle must not be null"));
		}
		
		QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();

		querydef = null;

		try{
			QueryBatcher batcher = dmManager.newQueryBatcher(querydef).withBatchSize(32).withThreadCount(20);
			Assert.assertFalse("Exception was not thrown, when it should have been", 1<2);
		}
		catch(IllegalArgumentException e){
			Assert.assertTrue(e.getMessage().equals("query must not be null"));
		}
	}
	
	@Test
	public void queryFailures() throws Exception{

		
		Thread t1 = new Thread(new DisabledDBRunnable());
     	t1.setName("Status Check -1");
     	
     	Thread t2 = new Thread(new DisabledDBRunnable());
     	t2.setName("Status Check -2");


    	QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
    	querydef.setCriteria("John AND Bob");	
		AtomicInteger batches = new AtomicInteger(0);
		
		String jsonDoc = "{" +
				"\"employees\": [" +
				"{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
				"{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
				"{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
				"}";
		WriteBatcher wbatcher = dmManager.newWriteBatcher();
		wbatcher.withBatchSize(6000);
		wbatcher.onBatchFailure((batch, throwable)->throwable.printStackTrace());
		StringHandle handle = new StringHandle();
		handle.set(jsonDoc);
		String uri = null;
	
		// Insert 10 K documents
		for (int i = 0; i < 6000; i++) {
			uri = "/firstName" + i + ".json";
			wbatcher.add(uri, handle);
		}
				
		wbatcher.flushAndWait();
			
		QueryBatcher batcher = dmManager.newQueryBatcher(querydef).withBatchSize(10).withThreadCount(3);
		batcher.onUrisReady(batch->{
			batches.incrementAndGet();
		});
		batcher.onQueryFailure((throwable)->{
			try {
				Thread.currentThread().sleep(7000L);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			throwable.getBatcher().retry(throwable);
			String s = null;
			s.length();	
			
		});
		
		queryTicket = dmManager.startJob(batcher);
		t1.start();
		
		t1.join();
		Thread.currentThread().sleep(2000L);
    	t2.start();
		
		t2.join();	
		batcher.awaitCompletion();
			
		Assert.assertEquals(6000, dmManager.getJobReport(queryTicket).getSuccessEventsCount());
		Assert.assertEquals(batches.intValue(), dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
		Assert.assertEquals(2* hostNames.length, dmManager.getJobReport(queryTicket).getFailureEventsCount());
		Assert.assertEquals(2* hostNames.length, dmManager.getJobReport(queryTicket).getFailureBatchesCount());
	}
	class DisabledDBRunnable implements Runnable {
	  Map<String,String> properties = new HashMap<>();
	
   	  @Override
   	  public void run() {
   		  properties.put("enabled", "false");
   		  boolean state = true;
   		  while (state){
   			 System.out.println(dmManager.getJobReport(queryTicket).getSuccessEventsCount());
   			 if(dmManager.getJobReport(queryTicket).getSuccessEventsCount() >= 0){
   				
   				changeProperty(properties,"/manage/v2/databases/"+dbName+"/properties");
     			System.out.println("DB disabled");
     			state=false;
   			 }
   				
   		  }
   		try {
			Thread.currentThread().sleep(5000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	
		properties.put("enabled", "true");
		changeProperty(properties,"/manage/v2/databases/"+dbName+"/properties");
   	  }  
       		
   }
	
	@Test
	public void jobReportStopJob() throws Exception{

		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform")).withBatchSize(20).withThreadCount(20);
		
		AtomicInteger batchCount = new AtomicInteger(0);
		AtomicInteger successCount = new AtomicInteger(0);
		
		Set uris = Collections.synchronizedSet(new HashSet());
		
		batcher.onUrisReady(batch->{
			uris.addAll(Arrays.asList(batch.getItems()));
			batchCount.incrementAndGet();
			if(dmManager.getJobReport(queryTicket).getSuccessEventsCount() > 40){
				dmManager.stopJob(queryTicket);
			}
				
		});
		batcher.onQueryFailure(throwable -> throwable.printStackTrace());
				
		queryTicket = dmManager.startJob( batcher );
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
	
		queryTicket = dmManager.startJob( batcher );
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		
		System.out.println("Success event: "+dmManager.getJobReport(queryTicket).getSuccessEventsCount());
		System.out.println("Success batch: "+dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
		System.out.println("Failure event: "+dmManager.getJobReport(queryTicket).getFailureEventsCount());
		System.out.println("Failure batch: "+dmManager.getJobReport(queryTicket).getFailureBatchesCount());
		
		Assert.assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() > 40);
		Assert.assertTrue(dmManager.getJobReport(queryTicket).getSuccessEventsCount() < 1000);
		Assert.assertTrue(dmManager.getJobReport(queryTicket).getFailureEventsCount() == 0);
		Assert.assertTrue(dmManager.getJobReport(queryTicket).getFailureBatchesCount() == 0);
				
		Assert.assertTrue(batchCount.get() == dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
				
	}

	@Test
	public void jsMasstransformReplace() throws Exception{

		ServerTransform transform = new ServerTransform("jsTransform");
		transform.put("newValue", "new Value");

		ApplyTransformListener listener = new ApplyTransformListener()
				.withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE);
		
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("JsonTransform")).withBatchSize(20).withThreadCount(20);
		AtomicBoolean success = new AtomicBoolean(false);
		
		AtomicInteger batchCount = new AtomicInteger(0);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger count = new AtomicInteger(0);

		batcher = batcher.onUrisReady((batch)->{
				successCount.addAndGet(batch.getItems().length);
				batchCount.incrementAndGet();
				if(dmManager.getJobReport(queryTicket).getSuccessEventsCount() ==successCount.get())
				{
					success.set(true);
					count.incrementAndGet();
				}
	
	   	}).onUrisReady(listener).onQueryFailure((throwable)-> throwable.printStackTrace());
		

		queryTicket = dmManager.startJob( batcher );
		batcher.awaitCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
		dmManager.stopJob(queryTicket);


		String uris[] = new String[2000];
		for(int i =0;i<2000;i++){
			uris[i] = "/local/json-"+ i;
		}
		int doccount=0;
		DocumentPage page = dbClient.newDocumentManager().read(uris);
		JacksonHandle dh = new JacksonHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			assertEquals("Attribute value should be new Value","new Value",dh.get().get("c").asText());
			doccount++;
		}
	
		assertEquals("document count", 2000,doccount); 
		Assert.assertTrue(success.get());
		Assert.assertEquals(batchCount.get(), dmManager.getJobReport(queryTicket).getSuccessBatchesCount());
		Assert.assertEquals(batchCount.get(), count.get());
		Assert.assertEquals(2000, dmManager.getJobReport(queryTicket).getSuccessEventsCount());
		
	}

	
	//ISSUE # 106
	@Test
	public void stopTransformJobTest() throws Exception{

		ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
		transform.put("name", "Lang");
		transform.put("value", "French");
		List<String> skippedBatch = new ArrayList<>();
		List<String> successBatch = new ArrayList<>();
		List<String> failedBatch = new ArrayList<>();

		ApplyTransformListener listener = new ApplyTransformListener()
				.withTransform(transform)
				.withApplyResult(ApplyResult.REPLACE)
				.onSuccess(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					successBatch.addAll(batchList);
					System.out.println("stopTransformJobTest: Success: "+batch.getItems()[0]);

				})
				.onSkipped(batch -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					skippedBatch.addAll(batchList);
					System.out.println("stopTransformJobTest : Skipped: "+batch.getItems()[0]);
				})
				.onBatchFailure((batch,throwable) -> {
					List<String> batchList = Arrays.asList(batch.getItems());
					failedBatch.addAll(batchList);
					throwable.printStackTrace();
					System.out.println("stopTransformJobTest: Failed: "+batch.getItems()[0]);

				});
		
		
		QueryBatcher batcher = dmManager.newQueryBatcher(new StructuredQueryBuilder().collection("XmlTransform"))
				.withBatchSize(1)
				.withThreadCount(1);
		
		AtomicLong successCount = new AtomicLong(0L);
		

		batcher = batcher.onUrisReady((batch)->{
			System.out.println(dmManager.getJobReport(queryTicket).getSuccessEventsCount());
			System.out.println("Count: "+dmManager.getJobReport(queryTicket).getSuccessEventsCount() );
			successCount.set(dmManager.getJobReport(queryTicket).getSuccessEventsCount());
	     	
		}).onUrisReady(listener);
		queryTicket = dmManager.startJob( batcher );
		Thread.currentThread().sleep(4000L);
		dmManager.stopJob(queryTicket);

		String uris[] = new String[2000];
		for(int i =0;i<2000;i++){
			uris[i] = "/local/string-"+ i;
		}
		int doccount=0;
		DocumentPage page = dbClient.newDocumentManager().read(uris);
		DOMHandle dh = new DOMHandle();
		while(page.hasNext()){
			DocumentRecord rec = page.next();
			rec.getContent(dh);
			if(dh.get().getElementsByTagName("foo").item(0).getAttributes().item(0) == null){
				doccount++;
				System.out.println("stopTransformJobTest: skipped in server"+rec.getUri());
			}
				
		}
		System.out.println("stopTransformJobTest: Success: "+successBatch.size());
		System.out.println("stopTransformJobTest: Skipped: "+skippedBatch.size());
		System.out.println("stopTransformJobTest: Failed: "+failedBatch.size());
		System.out.println("stopTransformJobTest : count "+doccount);
		Assert.assertEquals(2000-doccount,successBatch.size());
		Assert.assertEquals(2000-doccount, successCount.get());

	}
}
