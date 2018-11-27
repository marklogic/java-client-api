package com.marklogic.client.example.cookbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.example.cookbook.JobRunner;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

public class TestJobRunner {
	DatabaseClient client;
	
	@Before
	public void setUp() {
		// TODO: switch to 8011
	      this.client = DatabaseClientFactory.newClient(
	            "localhost", 8011, new DatabaseClientFactory.DigestAuthContext("admin", "admin")
	      );
	}
	
   @Test
   public void testRun() throws IOException {
      String csvFile = "OrderLines.csv";

      QueryManager queryMgr = client.newQueryManager();

      JobRunner jobRunner = new JobRunner();
      String jobDirectory = jobRunner.getJobDirectory();

      try (
            InputStream csvStream = openStream(csvFile);
            InputStream jobStream = openStream(JobRunner.jobFileFor(csvFile));
      ) {
         jobRunner.run(client, csvStream, jobStream);
// verify before and after documents are written
         // verify no.of documents written querying the collection
         // query the first document in collection about the structure and permissions
         
// TODO: assert expected count of 1960 against documents in jobDirectory
      } finally {
         DeleteQueryDefinition deleteDef = queryMgr.newDeleteDefinition();
         deleteDef.setDirectory(jobDirectory);
         queryMgr.delete(deleteDef);
      }

   }
   
	@Test
	public void testBeforeJobDocument() throws Exception {
		String csvFile = "OrderLines.csv";

		JobRunner jobRunner = new JobRunner();
		String[] roles = { "admin" };
		jobRunner.setRoles(roles);
		JSONDocumentManager jsonMgr = client.newJSONDocumentManager();

		try (InputStream csvStream = openStream(csvFile);
				InputStream jobStream = openStream(JobRunner.jobFileFor(csvFile));) {
			jobRunner.run(client, csvStream, jobStream);

			ObjectMapper objectMapper = new ObjectMapper();

			ObjectNode jobControlObj = (ObjectNode) objectMapper.readTree(jobStream);
			ObjectNode jobDef = (ObjectNode) jobControlObj.get("job");

			String beforeDocId = "/jobs/" + jobDef.path("id").asText() + "/beforeJob.json";

			DocumentMetadataHandle beforeDocumentMetadataRead = new DocumentMetadataHandle();
			JacksonHandle readHandle = new JacksonHandle();
			
/*
 * *  using the metadata handle to check that the expected
    collections and roles exist

*  providing a JacksonHandle in addition to the metadata
    handle on the call to read() and checking that the expected
    properties exist in the JSON documents
    */
			assertNotNull(jsonMgr.exists(beforeDocId));
			assertNotNull(jsonMgr.read(beforeDocId, beforeDocumentMetadataRead, readHandle)); //readHandle.get
			//assertNotNull(jsonMgr.read(beforeDocId, beforeDocumentMetadata, readHandle));
			//beforeDocumentMetadataRead

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
   
	@Test
	public void testAfterJobDocument() throws Exception {
		String csvFile = "OrderLines.csv";

		JobRunner jobRunner = new JobRunner();
		String[] roles = { "admin" };
		jobRunner.setRoles(roles);
		JSONDocumentManager jsonMgr = client.newJSONDocumentManager();

		try (InputStream csvStream = openStream(csvFile);
				InputStream jobStream = openStream(JobRunner.jobFileFor(csvFile));) {
			jobRunner.run(client, csvStream, jobStream);

			ObjectMapper objectMapper = new ObjectMapper();

			ObjectNode jobControlObj = (ObjectNode) objectMapper.readTree(jobStream);
			ObjectNode jobDef = (ObjectNode) jobControlObj.get("job");

			String afterDocId = "/jobs/" + jobDef.path("id").asText() + "/afterJob.json";
			JacksonHandle readHandle = new JacksonHandle();

			DocumentMetadataReadHandle afterDocumentMetadata = new DocumentMetadataReadHandle() {
			};

			assertNotNull(jsonMgr.read(afterDocId, afterDocumentMetadata, readHandle));

			// TODO: assert expected count of 1960 against documents in jobDirectory
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testJsonDataStructure() throws Exception {
		String csvFile = "OrderLines.csv";

		JobRunner jobRunner = new JobRunner();
		String[] roles = { "admin" };
		jobRunner.setRoles(roles);
		JSONDocumentManager jsonMgr = client.newJSONDocumentManager();

		try (InputStream csvStream = openStream(csvFile);
				InputStream jobStream = openStream(JobRunner.jobFileFor(csvFile));) {
			jobRunner.run(client, csvStream, jobStream);

			ObjectMapper objectMapper = new ObjectMapper();

			ObjectNode jobControlObj = (ObjectNode) objectMapper.readTree(jobStream);
			ObjectNode jobDef = (ObjectNode) jobControlObj.get("job");

			String beforeDocId = "/jobs/" + jobDef.path("id").asText() + "/beforeJob.json";

			DocumentMetadataReadHandle beforeDocumentMetadataRead = new DocumentMetadataReadHandle() {
			};
			JacksonHandle readHandle = new JacksonHandle();
			
			assertNotNull(jsonMgr.read(beforeDocId, beforeDocumentMetadataRead, readHandle).get().get("metadata"));
			assertNotNull(jsonMgr.read(beforeDocId, beforeDocumentMetadataRead, readHandle).get().get("instance"));

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@After
	public void closeSetUp() {
		client.release();
	}
	
   public InputStream openStream(String fileName) throws IOException {
      InputStream file = TestJobRunner.class.getClassLoader().getResourceAsStream(fileName);
      if (file == null) {
         throw new IOException("could not read file: "+fileName);
      }
      return file;
   }
}
