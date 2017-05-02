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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.DeleteListener;
import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.functionaltest.Artifact;
import com.marklogic.client.functionaltest.Company;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentMetadataValues;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.functionaltest.BasicJavaClientREST;

public class ExportToWriterListenerTest extends BasicJavaClientREST {
	
	private static String dbName = "ExportToWriterListener";
	private static DataMovementManager dmManager = null;
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";
	
	private static DatabaseClient dbClient;
	private static String host = null;
	private static String user = "admin";
	private static int port = 8000;
	private static String password = "admin";
	private static String server = "App-Services";
	private static JsonNode clusterInfo;
	
	private static JacksonHandle jacksonHandle;
	private static StringHandle stringHandle;
	private static FileHandle fileHandle;
	
	private static DocumentMetadataHandle meta1;
	private static DocumentMetadataHandle meta2;
	private static DocumentMetadataHandle meta3;
	
	private static String stringTriple;
	private static File fileJson;
	private static JsonNode jsonNode;
	private static final String query1 = "fn:count(fn:doc())";
	private static String[] hostNames ;
	private static String dataConfigDirPath = null ;
	private static String outputFile = "/tmp/out.csv";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		loadGradleProperties();
		dataConfigDirPath = getDataConfigDirPath();
		host = getRestAppServerHostName();
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
		
		//JacksonHandle
		jsonNode = new ObjectMapper().readTree("{\"k1\":\"v1\"}");
		jacksonHandle = new JacksonHandle();
		jacksonHandle.set(jsonNode);
		
		meta1 = new DocumentMetadataHandle().withCollections("ExportListener").withQuality(1);
		meta2 = new DocumentMetadataHandle().withCollections("ExportListener").withQuality(2);
		meta3 = new DocumentMetadataHandle().withCollections("XML").withQuality(2);
		
		//StringHandle
		stringTriple = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><abc>xml</abc>";
		stringHandle = new StringHandle(stringTriple);
		stringHandle.setFormat(Format.XML);		
		
		// FileHandle
		fileJson = FileUtils.toFile(WriteHostBatcherTest.class.getResource(TEST_DIR_PREFIX+"dir.json"));
		fileHandle = new FileHandle(fileJson);
		fileHandle.setFormat(Format.JSON);
		
		Thread.currentThread().sleep(1000L);
		WriteBatcher ihb2 =  dmManager.newWriteBatcher();
		ihb2.withBatchSize(5).withThreadCount(2);
		
		ihb2.onBatchSuccess(batch -> {})
		    .onBatchFailure( (batch, throwable) -> {
		        	 throwable.printStackTrace();
		          });
		
		dmManager.startJob(ihb2);
		for (int j =0 ;j <10; j++) {
			String uri ="/local/jsonA-"+ j;
			ihb2.add(uri, meta1, jacksonHandle);
		}
		for (int j =0 ;j <10; j++) {
			String uri ="/local/jsonB-"+ j;
			ihb2.addAs(uri, meta2, fileHandle);
		}
		for (int j =0 ;j <10; j++) {
			String uri ="/local/xml-"+ j;
			ihb2.addAs(uri, meta3, stringHandle);
		}
		
		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 30);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		associateRESTServerWithDB(server,"Documents");
		for (int i =0 ; i < clusterInfo.size(); i++) {
			System.out.println(dbName+"-"+(i+1));
			detachForest(dbName, dbName+"-"+(i+1));
			deleteForest(dbName+"-"+(i+1));
		}		
		deleteDB(dbName);
		
		//Delete the output file
		File file = new File(outputFile);
		file.deleteOnExit();
	}	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMassExportJSON() throws Exception {
		  // export to a csv with uri, collection, and contents columns
		StructuredQueryDefinition query = new StructuredQueryBuilder().collection("ExportListener");
	    try (FileWriter writer = new FileWriter(outputFile)) {
	      ExportToWriterListener exportListener = new ExportToWriterListener(writer)
	        .withRecordSuffix("\n")
	       .withMetadataCategory(DocumentManager.Metadata.COLLECTIONS)
	        .withMetadataCategory(DocumentManager.Metadata.QUALITY)
	        .onGenerateOutput(
	          record -> {
	            String uri = record.getUri();
	            DocumentMetadataHandle handle = record.getMetadata(new DocumentMetadataHandle());
	            String collection = handle.getCollections().iterator().next();
	            int quality = handle.getQuality();
	            String contents = record.getContentAs(String.class);
	            return uri + "," + collection + "," + quality+ ","+ contents;
	          }
	        );

	      QueryBatcher queryJob =
	    		  dmManager.newQueryBatcher(query)
	          .withThreadCount(5)
	          .withBatchSize(10)
	          .onUrisReady(exportListener)
	          .onQueryFailure(throwable -> throwable.printStackTrace() );
	      dmManager.startJob( queryJob );

	      // wait for the export to finish
	      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
	      if ( finished == false ) {
	        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
	      }
	    }

	    try (// validate that the docs were exported
	            FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
	      int lines = 0;
	      while ( reader.readLine() != null ) lines++;
	      assertEquals( "There should be 20 lines in the output file", 20, lines );
	    }
	  }
	
	@Test
	public void testMassExportXML() throws Exception {
		  // export to a csv with uri, collection, and contents columns
		StructuredQueryDefinition query = new StructuredQueryBuilder().collection("XML");
	    try (FileWriter writer = new FileWriter(outputFile)) {
	      ExportToWriterListener exportListener = new ExportToWriterListener(writer)
	        .withRecordSuffix("\n")
	       .withMetadataCategory(DocumentManager.Metadata.COLLECTIONS)
	        .withMetadataCategory(DocumentManager.Metadata.QUALITY)
	        .onGenerateOutput(
	          record -> {
	            String uri = record.getUri();
	            DocumentMetadataHandle handle = record.getMetadata(new DocumentMetadataHandle());
	            String collection = handle.getCollections().iterator().next();
	            int quality = handle.getQuality();
	            String contents = record.getContentAs(String.class);
	            return uri + "," + collection + "," + quality+ ","+ contents;
	          }
	        );

	      QueryBatcher queryJob =
	    		  dmManager.newQueryBatcher(query)
	          .withThreadCount(1)
	          .withBatchSize(1)
	          .onUrisReady(exportListener)
	          .onQueryFailure(throwable -> throwable.printStackTrace() );
	      dmManager.startJob( queryJob );

	      // wait for the export to finish
	      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
	      if ( finished == false ) {
	        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
	      }
	    }

	    try (// validate that the docs were exported
	            FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
	      int lines = 0;
	      while ( reader.readLine() != null ) lines++;
	      assertEquals( "There should be 20 lines in the output file", 20, lines );
	    }
	  }
	
	@Test
	public void testExportXMLJSON() throws Exception {
		  // export to a csv with uri, collection, and contents columns
		StructuredQueryDefinition query = new StructuredQueryBuilder().document("/local/xml-1","/local/jsonA-1");
	    try (FileWriter writer = new FileWriter(outputFile)) {
	      ExportToWriterListener exportListener = new ExportToWriterListener(writer)
	        .withRecordSuffix("\n")
	       .withMetadataCategory(DocumentManager.Metadata.COLLECTIONS)
	        .withMetadataCategory(DocumentManager.Metadata.QUALITY)
	        .onGenerateOutput(
	          record -> {
	            String uri = record.getUri();
	            DocumentMetadataHandle handle = record.getMetadata(new DocumentMetadataHandle());
	            String collection = handle.getCollections().iterator().next();
	            int quality = handle.getQuality();
	            String contents = record.getContentAs(String.class);
	            return uri + "," + collection + "," + quality+ ","+ contents;
	          }
	        );

	      QueryBatcher queryJob =
	    		  dmManager.newQueryBatcher(query)
	          .withThreadCount(2)
	          .withBatchSize(2)
	          .onUrisReady(exportListener)
	          .onQueryFailure(throwable -> throwable.printStackTrace());
	      dmManager.startJob( queryJob );

	      // wait for the export to finish
	      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
	      if ( finished == false ) {
	        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
	      }
	    }

	    try (// validate that the docs were exported
	            FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
	      int lines = 0;
	      String str = null;
	      while ((str = reader.readLine()) != null) { lines++; System.out.println("Line read from file with URIS is" + str);}
	      assertEquals( "There should be 3 lines in the output file", 3, lines );
	    }
	  }
	
	/*
	 * Test contents of exported file as-is. Output file should not have meta-data
	 * since there is no call to onGenerateOutput listener
	 */
	@Test
	public void testExportWithNoDocumentRecord() throws Exception {
		System.out.println("Running testExportWithNoDocumentRecord");		  
		StructuredQueryDefinition query = new StructuredQueryBuilder().document("/local/jsonB-1","/local/jsonA-1");
	    try (FileWriter writer = new FileWriter(outputFile)) {
	      ExportToWriterListener exportListener = new ExportToWriterListener(writer)
	        .withRecordSuffix("\n");

	      QueryBatcher queryJob = dmManager.newQueryBatcher(query)
	          .withThreadCount(2)
	          .withBatchSize(2)
	          .onUrisReady(exportListener)
	          .onQueryFailure(throwable -> throwable.printStackTrace());
	      dmManager.startJob( queryJob );

	      // wait for the export to finish
	      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
	      if ( finished == false ) {
	        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
	      }
	    }

	    try ( // validate that the docs were exported
	            FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
	      int expLines = 0;
	      String line = null;
	      while ((line = reader.readLine()) != null) { 
	    	  expLines++;
	    	  System.out.println("Line read from file with URIS is" + line);
	    	  assertTrue("Export to Write - Incorrect contents", line.contains("{\"k1\":\"v1\"}")?true:
	    		  line.contains("{\"a\":{\"b1\":{\"c\":\"jsonValue1\"}, \"b2\":[\"b2 val1\", \"b2 val2\"]}}")?true:false);
	    	  assertTrue("Export to Write - Incorrect contents", !line.contains("/local/jsonA-1")||line.contains("/local/jsonB-1"));
	      }
	      assertEquals( "There should be 2 lines in the output file", 2, expLines );
	    }
	  }
	
	/*
	 * Test contents of exported file with meta-data. Output file should have meta-data
	 * since there is a call to onGenerateOutput listener
	 */
	@Test
	public void testExportWithOnGenerateOutput() throws Exception {
		System.out.println("Running testExportWithOnGenerateOutput");		 
		//Use WriteBatcher to write files.	
		String[] filenames = {"constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml"};
		String dataFileDir = dataConfigDirPath + "/data/";
		DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
		
		metadataHandle.getMetadataValues().put("key1", "value1");
		metadataHandle.getMetadataValues().put("key2", "value2");
		metadataHandle.getMetadataValues().put("key3", "value3");
		
		metadataHandle.getCollections().add("QAKEYS");
		WriteBatcher wbatcher =  dmManager.newWriteBatcher();
		wbatcher.withBatchSize(5).withThreadCount(2);

		wbatcher.withBatchSize(2);
		InputStreamHandle contentHandle1 = new InputStreamHandle();
		contentHandle1.set(new FileInputStream(dataFileDir + filenames[0]));
		InputStreamHandle contentHandle2 = new InputStreamHandle();
		contentHandle2.set(new FileInputStream(dataFileDir + filenames[1]));
		InputStreamHandle contentHandle3 = new InputStreamHandle();
		contentHandle3.set(new FileInputStream(dataFileDir + filenames[2]));
		InputStreamHandle contentHandle4 = new InputStreamHandle();
		contentHandle4.set(new FileInputStream(dataFileDir + filenames[3]));
		InputStreamHandle contentHandle5 = new InputStreamHandle();
		contentHandle5.set(new FileInputStream(dataFileDir + filenames[4]));

		wbatcher.add(filenames[0], metadataHandle, contentHandle1);
		wbatcher.add(filenames[1], metadataHandle, contentHandle2);
		wbatcher.add(filenames[2], metadataHandle, contentHandle3);
		wbatcher.add(filenames[3], metadataHandle, contentHandle4);
		wbatcher.add(filenames[4], metadataHandle, contentHandle5);

		// Flush
		wbatcher.flushAndWait();
		
		StructuredQueryDefinition query = new StructuredQueryBuilder().document(filenames[0], filenames[1]);
	    try (FileWriter writer = new FileWriter(outputFile)) {
	    	
	      ExportToWriterListener exportListener = new ExportToWriterListener(writer)
	        .withRecordSuffix("\n")
	        .withMetadataCategory(DocumentManager.Metadata.ALL)
	        .onGenerateOutput(
	        		record -> {
	        			DocumentMetadataHandle readMetaData =  new DocumentMetadataHandle();
	        			String uri = record.getUri();
	        			String format = record.getFormat().toString();
	        			String mimetype = record.getMimetype();	        			
	        			
	        			readMetaData =  record.getMetadata(readMetaData);
	        			
	        			String collection = readMetaData.getCollections().toString();
	        			DocumentMetadataValues metadataValues = readMetaData.getMetadataValues();
	        			String keyValue1 = metadataValues.get("key1");
	        			
	        			return uri + "|" + format + "|" + mimetype + "|"+ collection + "|" + keyValue1;
	        		});

	      QueryBatcher queryJob = dmManager.newQueryBatcher(query)
	          .withThreadCount(2)
	          .withBatchSize(2)
	          .onUrisReady(exportListener)
	          .onQueryFailure(throwable -> throwable.printStackTrace());
	      dmManager.startJob( queryJob );

	      // wait for the export to finish
	      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
	      if ( finished == false ) {
	        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
	      }
	    }

	    try (// validate that the docs were exported
	            FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
	      int expLines = 0;
	      String line = null;
	      while ((line = reader.readLine()) != null) { 
	    	  expLines++;
	    	  System.out.println("Line read from file with URIS is " + line);
	    	  assertTrue("Export to Write - Incorrect collection contents", line.contains("QAKEYS")?true:false);
	    	  assertTrue("Export to Write - Incorrect meta data values", line.contains("value1"));
	      }
	      assertEquals( "There should be 2 lines in the output file", 2, expLines );
	    }
	  }
		
	/*
	 * Test that ExportListener can export POJO documents returned from QueryBatcher
	 */
	@Test
	public void testPOJOExport() throws IOException, ParserConfigurationException, SAXException, InterruptedException
	{	
		System.out.println("Running testPOJOExport");
		StringBuilder batchResults = new StringBuilder();
		StringBuilder batchFailResults =  new StringBuilder();
		
		PojoRepository<Artifact,Long> products = dbClient.newPojoRepository(Artifact.class, Long.class);
		try {
			// Populate POJOs in database and get expected URIs into the map for the assert.
			for(int i=1;i<11;i++) {
				if(i%2 == 0) {
					products.write(this.getArtifact(i),"even","numbers");
				}
				else {
					products.write(this.getArtifact(i),"odd","numbers");
				}
			}
			
			StructuredQueryDefinition query = new StructuredQueryBuilder().document("com.marklogic.client.functionaltest.Artifact/1.json",
					                                                                "com.marklogic.client.functionaltest.Artifact/2.json");
		    try (FileWriter writer = new FileWriter(outputFile)) {
		      ExportToWriterListener exportListener = new ExportToWriterListener(writer)
		        .withRecordSuffix("\n");

		      QueryBatcher queryJob = dmManager.newQueryBatcher(query)
		          .withThreadCount(2)
		          .withBatchSize(2)
		          .onUrisReady(exportListener)
		          .onQueryFailure(throwable -> throwable.printStackTrace());
		      dmManager.startJob( queryJob );

		      // wait for the export to finish
		      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
		      if ( finished == false ) {
		        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
		      }
		      writer.close();
		    }

		    try (// validate that the docs were exported
		            FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
		      int expLines = 0;
		      String line = null;
		      while ((line = reader.readLine()) != null) { 
		    	  expLines++;
		    	  System.out.println("Line read from file with URIS is" + line);
		    	  // Verify that parts of the objects are avaialble in the file.
		    	  assertTrue("Export to Write - Incorrect contents", line.contains("\"name\":\"Cogs 1\"")?true:
		    		  line.contains("\"name\":\"Cogs 2\"")?true:false);
		    	  assertTrue("Export to Write - Incorrect contents", line.contains("\"name\":\"Acme 2, Inc.\"")?true:
		    		  line.contains("\"name\":\"Widgets 1, Inc.\"")?true:false);  
		      }
		      assertEquals( "There should be 2 lines in the output file", 2, expLines );
		      fileReader.close();
		    }
		}
		catch (Exception ex) {
			System.out.println("Exception from method testPOJOExport " + ex.getMessage());
		}		
		finally {
			QueryManager queryMgr = dbClient.newQueryManager();
			StringQueryDefinition qd = queryMgr.newStringDefinition();
			qd.setCriteria("Cogs");
			
			// Run delete listener to clear DB of POJOs.
			QueryBatcher deleteBatcher = dmManager.newQueryBatcher(qd)
					.withBatchSize(5)
					.withConsistentSnapshot()
					.onUrisReady(new DeleteListener())
					.onUrisReady(batch-> {
						for (String str : batch.getItems()) {
							batchResults.append(str)
									.append('|');
						}     
					})
					.onQueryFailure(throwable-> {        	
						System.out.println("Exceptions thrown from callback onQueryFailure");        	
						throwable.printStackTrace();
						batchFailResults.append("Test has Exceptions");          	
					});
			dmManager.startJob(deleteBatcher);
			deleteBatcher.awaitCompletion();
			System.out.println("Done with method testPOJOExport ");
		}
	}
	
	/*
	 * Trigger writer closure to generate batch failure
	 */
	@Test
	public void testOnBatchFailure() throws Exception {
		System.out.println("Running testOnBatchFailure");
		StructuredQueryDefinition query = new StructuredQueryBuilder().document("/local/xml-1","/local/jsonA-1");
		StringBuilder onBatchFailureStr = new StringBuilder();
		try (FileWriter writer = new FileWriter(outputFile)) {

			QueryBatcher queryJob =
					dmManager.newQueryBatcher(query)
					.withThreadCount(2)
					.withBatchSize(2)
					.onUrisReady(new ExportToWriterListener(writer)
					.withRecordSuffix("\n")
					.withMetadataCategory(DocumentManager.Metadata.COLLECTIONS)
					.withMetadataCategory(DocumentManager.Metadata.QUALITY)
					.onGenerateOutput(
							record -> {
								String uri = record.getUri();
								DocumentMetadataHandle handle = record.getMetadata(new DocumentMetadataHandle());
								String collection = handle.getCollections().iterator().next();
								int quality = handle.getQuality();
								String contents = record.getContentAs(String.class);
								try {
									Thread.sleep(5000);
								} catch (Exception e) {
									e.printStackTrace();
								}
								return uri + "," + collection + "," + quality+ ","+ contents;
							}
							)
							.onBatchFailure((batch, throwable)->{
								onBatchFailureStr.append("From onBatchFailure QA Exception");
								System.out.println("From onBatchFailure QA Exception");
							}))
							.onQueryFailure(throwable -> throwable.printStackTrace());
			dmManager.startJob( queryJob );
			// Close writer to trigger onBatchFailure on Listener.
			writer.close();

			// wait for the export to finish
			boolean finished = queryJob.awaitCompletion();
			if ( finished == false ) {
				throw new IllegalStateException("ERROR: Job did not finish within three minutes");
			}
		}
		assertTrue("On Batch Failure call has issues", onBatchFailureStr.toString().contains("From onBatchFailure QA Exception"));
	}
	
	public Artifact getArtifact(int counter) {

		Artifact cogs = new Artifact();
		cogs.setId(counter);
		if (counter % 5 == 0) {
			cogs.setName("Cogs special");
			if (counter % 2 ==0) {
				Company acme = new Company();
				acme.setName("Acme special, Inc.");
				acme.setWebsite("http://www.acme special.com");
				acme.setLatitude(41.998+counter);
				acme.setLongitude(-87.966+counter);
				cogs.setManufacturer(acme);

			} else {
				Company widgets = new Company();
				widgets.setName("Widgets counter Inc.");
				widgets.setWebsite("http://www.widgets counter.com");
				widgets.setLatitude(41.998+counter);
				widgets.setLongitude(-87.966+counter);
				cogs.setManufacturer(widgets);
			}
		} else {
			cogs.setName("Cogs "+counter);
			if (counter % 2 ==0) {
				Company acme = new Company();
				acme.setName("Acme "+counter+", Inc.");
				acme.setWebsite("http://www.acme"+counter+".com");
				acme.setLatitude(41.998+counter);
				acme.setLongitude(-87.966+counter);
				cogs.setManufacturer(acme);

			} else {
				Company widgets = new Company();
				widgets.setName("Widgets "+counter+", Inc.");
				widgets.setWebsite("http://www.widgets"+counter+".com");
				widgets.setLatitude(41.998+counter);
				widgets.setLongitude(-87.966+counter);
				cogs.setManufacturer(widgets);
			}
		}
		cogs.setInventory(1000+counter);
		return cogs;
	}
}
