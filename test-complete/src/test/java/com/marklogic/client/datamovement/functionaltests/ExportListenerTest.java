/*
 * Copyright 2014-2017 MarkLogic Corporation
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.functionaltests.util.DmsdkJavaClientREST;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class ExportListenerTest extends  DmsdkJavaClientREST {

	private static String dbName = "ExportListener";
	private static DataMovementManager dmManager = null;
	private static DatabaseClient dbClient;
	private static String host = "localhost";
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";
	private static JsonNode clusterInfo;

	private static final String query1 = "fn:count(fn:doc())";
	private static String[] hostNames ;

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
				.getResource(null, "internal/forestinfo", null, null, new JacksonHandle())
				.get();		
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
		String jsonDoc = "{" +
				"\"employees\": [" +
				"{ \"firstName\":\"John\" , \"lastName\":\"Doe\" }," +
				"{ \"firstName\":\"Ann\" , \"lastName\":\"Smith\" }," +
				"{ \"firstName\":\"Bob\" , \"lastName\":\"Foo\" }]" +
				"}";

		//Use WriteBatcher to write the files.				
		WriteBatcher wbatcher = dmManager.newWriteBatcher();

		wbatcher.withBatchSize(1000);
		StringHandle handle = new StringHandle();
		handle.set(jsonDoc);
		String uri = null;

		// Insert 10K documents
		for (int i = 0; i < 100; i++) {
			uri = "firstName" + i + ".json";
			wbatcher.add(uri, handle);
		}
		wbatcher.flushAndWait();
	}

	@After
	public void tearDown() throws Exception {
		clearDB(port);
	}

	/*
	 * This test verifies that DMSDK supports PointInTime query and export using ExportListener.
	 * The result returned is deterministic, since we have a delete operation running when
	 *  1) query batcher is setup with ConsistentSnapshot and
	 *  2) Listener is also setup with ConsistentSnapshot.
	 *  3) Second query batcher after delete listener should return 0 uris.
	 */
	@Test
	public void testPointInTimeQueryDeterministicSet() throws Exception {
		System.out.println("Running testPointInTimeQueryDeterministicSet");
		Map<String, String> props = new HashMap<String, String>();
		props.put("merge-timestamp","-6000000000");
		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");
		Thread.currentThread().sleep(10000L);

		List<String> docExporterList = new ArrayList<String>();
		List<String> batcherList2 = new ArrayList<String>();

		QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria("John AND Bob");

		StringQueryDefinition querydef2 = queryMgr.newStringDefinition();
		querydef2.setCriteria("John AND Bob");
		StringBuffer batchResults  = new StringBuffer();		

		try {
			// Listener IS setup with withConsistentSnapshot()
			ExportListener exportListener = new ExportListener();
			exportListener.withConsistentSnapshot()
			.onDocumentReady(doc->{
				String uriOfDoc = doc.getUri();
				System.out.println("URIs from Export " + uriOfDoc);
				// Make sure the docs are available. Not deleted from DB.
				docExporterList.add(uriOfDoc);
			}
					);

			QueryBatcher exportBatcher = dmManager.newQueryBatcher(querydef)
					.withConsistentSnapshot()
					.withBatchSize(10)
					.onUrisReady(exportListener)
					.onUrisReady(new DeleteListener())
					.onQueryFailure(exception -> {
						System.out.println("Exceptions thrown from exportBatcher callback onQueryFailure");
						exception.printStackTrace(); 
					});
			dmManager.startJob(exportBatcher);
			exportBatcher.awaitCompletion();

			ExportListener exportListener2 = new ExportListener();
			exportListener2.withConsistentSnapshot()
			.onDocumentReady(doc->{
				String uriOfDoc = doc.getUri();
				batcherList2.add(uriOfDoc);
			}
					);

			// Run a second batcher, no that DeleteListener has done its work.
			// Currently the DB snapshot is at a point, where we do not have any docs.			
			QueryBatcher batcher2 = dmManager.newQueryBatcher(querydef2)
					.withConsistentSnapshot()
					.withBatchSize(100)
					.onUrisReady(exportListener2)
					.onQueryFailure(exception -> {
						System.out.println("Exceptions thrown from batcher2 callback onQueryFailure");
						exception.printStackTrace(); 
					});

			dmManager.startJob(batcher2);
			batcher2.awaitCompletion();
		}
		catch(Exception ex) {
			System.out.println("Exceptions from testPointInTimeQueryDeterministicSet method is" + ex.getMessage());
		}
		finally {
		}

		System.out.println("Batch" + batchResults.toString());
		props.put("merge-timestamp","0");
		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");	
		assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
		System.out.println("List size from second QueryBatcher is " + batcherList2.size());
		System.out.println("List size from Export Listener is " + docExporterList.size());

		assertTrue("Docs deleted. Size incorrect", batcherList2.size() == 0);
		assertTrue("Docs deleted. Size incorrect", docExporterList.size() == 100);		
	}

	/*
	 * This test verifies that DMSDK supports PointInTime query and export using ExportListener.
	 * The result returned is deterministic, since we have a delete operation running when
	 *  1) query batcher is setup with ConsistentSnapshot and
	 *  2) Listener is setup with ConsistentSnapshot.
	 */
	@Test
	public void testWithSnapshots() throws Exception {
		System.out.println("Running testPointInTimeQueryDeterministicSet");
		Map<String, String> props = new HashMap<String, String>();
		props.put("merge-timestamp","-6000000000");
		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");
		Thread.currentThread().sleep(5000L);

		List<String> docExporterList = new ArrayList<String>();
		List<String> batcherList = new ArrayList<String>();

		QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria("John AND Bob");
		StringBuffer batchResults  = new StringBuffer();		

		try {			
			// Listener is setup with withConsistentSnapshot()
			ExportListener exportListener = new ExportListener();
			exportListener
			.withConsistentSnapshot()
			.onDocumentReady(doc->{
				String uriOfDoc = doc.getUri();
				// Make sure the docs are available. Not deleted from DB.
				docExporterList.add(uriOfDoc);
			}
					);

			QueryBatcher exportBatcher = dmManager.newQueryBatcher(querydef)
					.withConsistentSnapshot()
					.withBatchSize(10)
					.onUrisReady(exportListener)
					.onUrisReady(batch-> {						
						for (String u: batch.getItems()) {
							batchResults.append(u);
							batcherList.add(u);
						}
						batchResults.append("|");
						System.out.println("Batch Numer is " + batch.getJobBatchNumber());
						if (batch.getJobBatchNumber() == 1) {
							// Attempt to read firstName11 from database
							DocumentManager docMgr = dbClient.newJSONDocumentManager();
							JacksonHandle jacksonhandle = new JacksonHandle();
							docMgr.read("firstName11.json", jacksonhandle);
							JsonNode node = jacksonhandle.get();
							// 3 nodes available
							assertTrue("URI attempted for delete",node.path("employees").size() == 3);
							assertEquals("Doc content incorrect", "John", node.path("employees").get(0).path("firstName").asText());
							assertEquals("Doc content incorrect", "Ann", node.path("employees").get(1).path("firstName").asText());
							assertEquals("Doc content incorrect", "Bob", node.path("employees").get(2).path("firstName").asText());			
						}
						try {
							Thread.sleep(5000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					})
					.onQueryFailure(exception -> {
						System.out.println("Exceptions thrown from testPointInTimeQueryDeterministicSet callback onQueryFailure");
						exception.printStackTrace(); 
					});
			dmManager.startJob(exportBatcher);

			QueryBatcher deleteBatcher = dmManager.newQueryBatcher(querydef)
					.withConsistentSnapshot()
					.withBatchSize(100)
					.onUrisReady(new DeleteListener());

			dmManager.startJob(deleteBatcher);
			deleteBatcher.awaitCompletion();

			exportBatcher.awaitCompletion();
		}
		catch(Exception ex) {
			System.out.println("Exceptions from testPointInTimeQueryDeterministicSet method is" + ex.getMessage());
		}
		finally {
		}

		System.out.println("Batch" + batchResults.toString());
		props.put("merge-timestamp","0");
		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");	

		System.out.println("List size from QueryBatcher is " + batcherList.size());
		System.out.println("List size from Export Listener is " + docExporterList.size());

		assertTrue("Docs deleted. Size incorrect", batcherList.size() == 100);
		assertTrue("Docs deleted. Size incorrect", docExporterList.size() == 100);

		// Doc count should be zero after both batchers are done.
		assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	}

	/*
	 * This test verifies that DMSDK supports PointInTime query and export using ExportListener.
	 * The result returned is non-deterministic, since we have a delete operation running when
	 *  1) query batcher is setup with ConsistentSnapshot and
	 *  2) Listener is NOT setup with ConsistentSnapshot.
	 */
	@Test
	public void testNoSnapshotOnListener() throws Exception {
		System.out.println("Running testNoSnapshotOnListener");
		Map<String, String> props = new HashMap<String, String>();
		props.put("merge-timestamp","-6000000000");
		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");
		Thread.currentThread().sleep(5000L);

		List<String> docExporterList = new ArrayList<String>();
		List<String> batcherList = new ArrayList<String>();

		QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria("John AND Bob");
		StringBuffer batchResults  = new StringBuffer();		

		try {			
			// Listener is NOT setup with withConsistentSnapshot()
			ExportListener exportListener = new ExportListener();
			exportListener.onDocumentReady(doc->{
				String uriOfDoc = doc.getUri();
				docExporterList.add(uriOfDoc);
			}
					);

			QueryBatcher exportBatcher = dmManager.newQueryBatcher(querydef)
					.withConsistentSnapshot()
					.withBatchSize(10)
					.onUrisReady(exportListener)
					.onUrisReady(batch-> {						
						for (String u: batch.getItems()) {
							batchResults.append(u);
							batcherList.add(u);
						}
						batchResults.append("|");
						System.out.println("Batch Numer is " + batch.getJobBatchNumber());
						if (batch.getJobBatchNumber() == 1) {
							// Attempt to read firstName11 from database
							DocumentManager docMgr = dbClient.newJSONDocumentManager();
							JacksonHandle jacksonhandle = new JacksonHandle();
							docMgr.read("firstName11.json", jacksonhandle);
							JsonNode node = jacksonhandle.get();
							// 3 nodes available
							assertTrue("URI attempted for delete",node.path("employees").size() == 3);
							assertEquals("Doc content incorrect", "John", node.path("employees").get(0).path("firstName").asText());
							assertEquals("Doc content incorrect", "Ann", node.path("employees").get(1).path("firstName").asText());
							assertEquals("Doc content incorrect", "Bob", node.path("employees").get(2).path("firstName").asText());			
						}
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					})
					.onQueryFailure(exception -> {
						System.out.println("Exceptions thrown from exportBatcher callback onQueryFailure");
						exception.printStackTrace(); 
					});
			dmManager.startJob(exportBatcher);

			QueryBatcher deleteBatcher = dmManager.newQueryBatcher(querydef)
					.withConsistentSnapshot()
					.withBatchSize(100)
					.onUrisReady(new DeleteListener());

			dmManager.startJob(deleteBatcher);
			deleteBatcher.awaitCompletion();

			exportBatcher.awaitCompletion();
		}
		catch(Exception ex) {
			System.out.println("Exceptions from deleteBatcher method is" + ex.getMessage());
		}
		finally {
		}

		System.out.println("Batch" + batchResults.toString());
		props.put("merge-timestamp","0");
		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");	

		System.out.println("List size from QueryBatcher is " + batcherList.size());
		System.out.println("List size from Export Listener is " + docExporterList.size());

		assertTrue("Docs deleted. Size incorrect", batcherList.size() == 100);
		assertTrue("Docs deleted. Size incorrect", docExporterList.size() != 100);

		// Doc count should be zero after both batchers are done.
		assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	}

	/*
	 * This test verifies that DMSDK supports PointInTime query and export using ExportListener.
	 * The result returned is non-deterministic, since we have a delete operation running when query batcher is 
	 * setup with no ConsistentSnapshot.
	 * 1) Start query batcher 1 with a export listener.
	 * 2) Query batcher 1 waits for 5 seconds
	 * 3) Start a second query batcher with delete listener
	 * Results should be non deterministic.
	 */
	@Test
	public void testPointInTimeQueryNonDeterministicSet() throws Exception {
		System.out.println("Running testPointInTimeQueryNonDeterministicSet");
		Map<String, String> props = new HashMap<String, String>();
		props.put("merge-timestamp","-6000000000");
		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");
		Thread.currentThread().sleep(5000L);

		List<String> docExporterList = new ArrayList<String>();
		List<String> batcherList = new ArrayList<String>();

		QueryManager queryMgr = dbClient.newQueryManager();
		StringQueryDefinition querydef = queryMgr.newStringDefinition();
		querydef.setCriteria("John AND Bob");
		StringBuffer batchResults  = new StringBuffer();		

		try {			
			ExportListener exportListener = new ExportListener();			
			exportListener.onDocumentReady(doc->{
				String uriOfDoc = doc.getUri();
				// Make sure the docs are available. Not deleted from DB.
				docExporterList.add(uriOfDoc);
			}
					);

			QueryBatcher exportBatcher = dmManager.newQueryBatcher(querydef)
					.withBatchSize(10)
					.onUrisReady(exportListener)
					.onUrisReady(batch-> {						
						for (String u: batch.getItems()) {
							batchResults.append(u);
							batcherList.add(u);
						}
						batchResults.append("|");
						System.out.println("Batch Numer is " + batch.getJobBatchNumber());

						try {
							Thread.sleep(5000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					})
					.onQueryFailure(exception -> {
						System.out.println("Exceptions thrown from testPointInTimeQueryDeterministicSet callback onQueryFailure");
						exception.printStackTrace(); 
					});
			dmManager.startJob(exportBatcher);

			QueryBatcher deleteBatcher = dmManager.newQueryBatcher(querydef)
					.withConsistentSnapshot()
					.withBatchSize(100)
					.onUrisReady(new DeleteListener());

			dmManager.startJob(deleteBatcher);
			deleteBatcher.awaitCompletion();

			exportBatcher.awaitCompletion();
		}
		catch(Exception ex) {
			System.out.println("Exceptions from testPointInTimeQueryDeterministicSet method is" + ex.getMessage());
		}
		finally {
		}

		System.out.println("Batch" + batchResults.toString());
		props.put("merge-timestamp","0");
		changeProperty(props,"/manage/v2/databases/"+dbName+"/properties");		

		assertTrue("Docs deleted. Size incorrect", batcherList.size() != 100);
		assertTrue("Docs deleted. Size incorrect", docExporterList.size() != 100);

		// Doc count should be zero after both batchers are done.
		assertEquals(0, dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue());
	}
}