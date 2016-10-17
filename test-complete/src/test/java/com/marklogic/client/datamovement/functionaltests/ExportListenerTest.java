/*
 * Copyright 2014-2016 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marklogic.client.datamovement.functionaltests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryHostBatcher;
import com.marklogic.client.datamovement.WriteHostBatcher;
import com.marklogic.client.datamovement.functionaltests.util.DmsdkJavaClientREST;

public class ExportListenerTest extends  DmsdkJavaClientREST {
	
	private static String dbName = "ExportListener";
	private static DataMovementManager dmManager = DataMovementManager.newInstance();
	private static final String TEST_DIR_PREFIX = "src/test/resources/QueryHostBatcher-testdata/";
	
	private static DatabaseClient dbClient;
	private static String host = "localhost";
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";
	private static JsonNode clusterInfo;
	
	private static JacksonHandle jacksonHandle;
	private static StringHandle stringHandle;
	private static FileHandle fileHandle;
	
	private static DocumentMetadataHandle meta;
	
	private static String stringTriple;
	private static File fileJson;
	private static JsonNode jsonNode;
	private static final String query1 = "fn:count(fn:doc())";
	private static String[] hostNames ;
	private String outputFile = "/tmp/out.csv";
	
	
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
		dmManager.withClient(dbClient);
		
		clusterInfo = ((DatabaseClientImpl) dbClient).getServices()
			      .getResource(null, "forestinfo", null, null, new JacksonHandle())
			      .get();
		
		//JacksonHandle
		jsonNode = new ObjectMapper().readTree("{\"k1\":\"v1\"}");
		jacksonHandle = new JacksonHandle();
		jacksonHandle.set(jsonNode);
		
		meta = new DocumentMetadataHandle().withCollections("DeleteListener");
		
		//StringHandle
		stringTriple = "<abc>xml</abc>";
		stringHandle = new StringHandle(stringTriple);
		stringHandle.setFormat(Format.XML);
		
		// FileHandle
		fileJson = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"dir.json"));
		fileHandle = new FileHandle(fileJson);
		fileHandle.setFormat(Format.JSON);
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
		Thread.currentThread().sleep(1000L);
		WriteHostBatcher ihb2 =  dmManager.newWriteHostBatcher();
		ihb2.withBatchSize(27).withThreadCount(10);
		ihb2.onBatchSuccess(
		        (client, batch) -> {	        	
		        	}
		        )
		        .onBatchFailure(
		          (client, batch, throwable) -> {
		        	 throwable.printStackTrace();
		          });
		
		dmManager.startJob(ihb2);
		for (int j =0 ;j < 2000; j++){
			String uri ="/local/json-"+ j;
			ihb2.add(uri, meta, jacksonHandle);
		}
	
		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 2000);
	}
	
	@After
	public void tearDown() throws Exception {
		clearDB(port);
	}
	/*
	 * This test verifies that DMSDK supports PointInTime query and export using ExportListener.
	 * Issue seen: We are yet to completly set/get the server timestamp for the query. Needs REST support.
	 * As of now we have export working with Snapshot and QHB returns a document as of its run-time.
	 * 
	 * ToDo: Update this test using a patch builder to have fragments and when we have support for 
	 * timestamp, use the timestamp in the qery def' query to go back in the time and retrieve that
	 * specific document.
	 * 
	 *  Git Issue: 
	 */
	@Test
	public void readPointInTimeQuery() throws Exception{
		Map<String, String> props = new HashMap<String, String>();
 		props.put("merge-timestamp","-6000000000");
 		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");
 		Thread.currentThread().sleep(5000L);
 		
 		// Write the json document into database using Java Client API.
 		//Cannot use DMSDK API to simulate updates for the document.
 		// Use PatchBuilder.
 		
 		String[] filenames = {"json-original.json"};
 		for(String filename : filenames) {
 			writeDocuments(dbClient, filename, "/partial-update/", "JSON");
		}
		DocumentManager docMgrIns = dbClient.newJSONDocumentManager();
		// create handle
		JacksonHandle jacksonHandle = new JacksonHandle();
		
		// Read the document with timestamp.
		docMgrIns.read( "/partial-update/"+filenames[0], jacksonHandle);
		
		long insTimeStamp = jacksonHandle.getServerTimestamp();
		System.out.println("Point in Time Stamp after the initial insert " + insTimeStamp);
		
		QueryManager queryMgr = dbClient.newQueryManager();

		// create query def for export listener with point in time query.
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		StringBuilder expListenResult = new StringBuilder();
		StringBuilder expListenHandle = new StringBuilder();
		querydef.setCriteria("(John AND Bob");
		 try (FileWriter writer = new FileWriter(outputFile)) {
			 ExportListener exportListener = new ExportListener();
			 exportListener.withConsistentSnapshot()
			               .onDocumentReady(doc->{ 
			            	   StringHandle handle = new StringHandle();
			                   doc.getContent(handle); 
			                   expListenResult.append(handle.get());
			                   expListenHandle.append(handle.getServerTimestamp());
			                   });
			               
		 QueryHostBatcher exportBatcher = dmManager.newQueryHostBatcher(querydef)
			    .withConsistentSnapshot()
			    .onUrisReady(exportListener)
		        .onQueryFailure((client, exception) -> {
		        	System.out.println("Exceptions thrown from callback onQueryFailure");
		        	exception.printStackTrace(); 
		        });
		 JobTicket ticket = dmManager.startJob(exportBatcher); 
		 exportBatcher.awaitCompletion();
		 dmManager.stopJob(ticket);
		 System.out.println("Original Document contents " + expListenResult.toString());
		 System.out.println("First query time stamp is " + expListenHandle.toString());
			    
		 }

		props.put("merge-timestamp","0");
 		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");
	//   	if ( failures2.length() > 0 ) fail(failures2.toString());
	    assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	}
	
	public void writeDocuments(DatabaseClient client, String filename, String uri, String type) throws FileNotFoundException
	{
		// create doc manager
		DocumentManager docMgr = null;
		docMgr = documentManagerSelector(client, docMgr, type);	

		// create handle
		InputStreamHandle contentHandle = new InputStreamHandle();

		// get the file
		InputStream inputStream = new FileInputStream(TEST_DIR_PREFIX + filename);
		
		// set uri
		String docId = uri + filename;

		contentHandle.set(inputStream);
			
		// write doc
		docMgr.write(docId, contentHandle);
		
		System.out.println("Write " + docId + " to database");
	}
	
}