package com.marklogic.client.datamovement.functionaltests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

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
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;

public class ExportToWriterListenerTest extends com.marklogic.client.datamovement.functionaltests.util.DmsdkJavaClientREST {
	
	private static String dbName = "ExportToWriterListener";
	private static DataMovementManager dmManager = DataMovementManager.newInstance();
	private static final String TEST_DIR_PREFIX = "/WriteHostBatcher-testdata/";
	
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
	
	private static DocumentMetadataHandle meta1;
	private static DocumentMetadataHandle meta2;
	private static DocumentMetadataHandle meta3;
	
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
		ihb2.onBatchSuccess(
		        (client, batch) -> {
		        	
		        	
		        	}
		        )
		        .onBatchFailure(
		          (client, batch, throwable) -> {
		        	 throwable.printStackTrace();
		          });
		
		dmManager.startJob(ihb2);
		for (int j =0 ;j <10; j++){
			String uri ="/local/jsonA-"+ j;
			ihb2.add(uri, meta1, jacksonHandle);
		}
		for (int j =0 ;j <10; j++){
			String uri ="/local/jsonB-"+ j;
			ihb2.addAs(uri, meta2, fileHandle);
		}
		for (int j =0 ;j <10; j++){
			String uri ="/local/xml-"+ j;
			ihb2.addAs(uri, meta3, stringHandle);
		}
		
		ihb2.flushAndWait();
		Assert.assertTrue(dbClient.newServerEval().xquery(query1).eval().next().getNumber().intValue() == 30);
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
			
	}

	@Test
	public void testMassExportJSON() throws Exception {
		  // export to a csv with uri, collection, and contents columns
	    QueryDefinition query = new StructuredQueryBuilder().collection("ExportListener");
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
	          .onQueryFailure( (client, throwable) -> throwable.printStackTrace() );
	      dmManager.startJob( queryJob );

	      // wait for the export to finish
	      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
	      if ( finished == false ) {
	        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
	      }
	    }

	    try ( // validate that the docs were exported
	            FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
	      int lines = 0;
	      while ( reader.readLine() != null ) lines++;
	      assertEquals( "There should be 20 lines in the output file", 20, lines );
	    }
	  }
	
	@Test
	public void testMassExportXML() throws Exception {
		  // export to a csv with uri, collection, and contents columns
	    QueryDefinition query = new StructuredQueryBuilder().collection("XML");
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
	          .onQueryFailure( (client, throwable) -> throwable.printStackTrace() );
	      dmManager.startJob( queryJob );

	      // wait for the export to finish
	      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
	      if ( finished == false ) {
	        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
	      }
	    }

	    try ( // validate that the docs were exported
	            FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
	      int lines = 0;
	      while ( reader.readLine() != null ) lines++;
	      assertEquals( "There should be 20 lines in the output file", 20, lines );
	    }
	  }
	
	@Test
	public void testExportXMLJSON() throws Exception {
		  // export to a csv with uri, collection, and contents columns
	    QueryDefinition query = new StructuredQueryBuilder().document("/local/xml-1","/local/jsonA-1");
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
	          .onQueryFailure( (client, throwable) -> throwable.printStackTrace() );
	      dmManager.startJob( queryJob );

	      // wait for the export to finish
	      boolean finished = queryJob.awaitCompletion(3, TimeUnit.MINUTES);
	      if ( finished == false ) {
	        throw new IllegalStateException("ERROR: Job did not finish within three minutes");
	      }
	    }

	    try ( // validate that the docs were exported
	            FileReader fileReader = new FileReader(outputFile); BufferedReader reader = new BufferedReader(fileReader)) {
	      int lines = 0;
	      while ( reader.readLine() != null ) lines++;
	      assertEquals( "There should be 3 lines in the output file", 3, lines );
	    }
	  }
}	


