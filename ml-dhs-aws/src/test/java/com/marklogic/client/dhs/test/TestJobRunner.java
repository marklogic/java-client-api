package com.marklogic.client.dhs.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.dhs.JobRunner;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
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
      String csvFile = "data"+File.separator+"OrderLines.csv";

      QueryManager queryMgr = client.newQueryManager();

      JobRunner jobRunner = new JobRunner();
      String jobDirectory = jobRunner.getJobDirectory();

      try (
            InputStream csvStream = openStream(csvFile);
            InputStream jobStream = openStream(JobRunner.jobFileFor(csvFile));
      ) {
         jobRunner.run(client, csvStream, jobStream);

// TODO: assert expected count of 1960 against documents in jobDirectory
      } finally {
         DeleteQueryDefinition deleteDef = queryMgr.newDeleteDefinition();
         deleteDef.setDirectory(jobDirectory);
         queryMgr.delete(deleteDef);
      }

   }
   
	@Test
	public void testBeforeJobDocument() throws Exception {
		String csvFile = "data"+File.separator+"OrderLines.csv";

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

			DocumentMetadataReadHandle beforeDocumentMetadata = new DocumentMetadataReadHandle() {
			};

			assertNotNull(jsonMgr.read(beforeDocId, beforeDocumentMetadata, null));

			// TODO: assert expected count of 1960 against documents in jobDirectory
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
   
	@Test
	public void testAfterJobDocument() throws Exception {
		String csvFile = "data"+File.separator+"OrderLines.csv";

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

			DocumentMetadataReadHandle afterDocumentMetadata = new DocumentMetadataReadHandle() {
			};

			assertNotNull(jsonMgr.read(afterDocId, afterDocumentMetadata, null));

			// TODO: assert expected count of 1960 against documents in jobDirectory
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
