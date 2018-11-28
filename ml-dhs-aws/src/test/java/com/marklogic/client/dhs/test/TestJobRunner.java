package com.marklogic.client.dhs.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.dhs.JobRunner;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.semantics.Capability;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;

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

			String beforeDocId = JobRunner.getBeforeJobDocumentUri(jobDef.path("id").asText());
			assertNotNull(jsonMgr.exists(beforeDocId));

			DocumentMetadataHandle beforeDocumentMetadata = new DocumentMetadataHandle();
			JacksonHandle readHandle = new JacksonHandle();
			
			assertNotNull(jsonMgr.read(beforeDocId, beforeDocumentMetadata, readHandle)); 
			assertTrue(beforeDocumentMetadata.getCollections().contains(JobRunner.getJobCollection(jobDef.path("id").asText())));
			assertTrue(beforeDocumentMetadata.getCollections().contains("/beforeJob"));
			assertTrue(beforeDocumentMetadata.getPermissions().containsKey("admin"));
			assertTrue(beforeDocumentMetadata.getPermissions().get("admin").contains(Capability.READ));
			assertTrue(beforeDocumentMetadata.getPermissions().get("admin").contains(Capability.UPDATE));

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

			String afterDocId = JobRunner.getAfterJobDocumentUri(jobDef.path("id").asText());
			assertNotNull(jsonMgr.exists(afterDocId));
			DocumentMetadataHandle afterDocumentMetadata = new DocumentMetadataHandle();
			JacksonHandle readHandle = new JacksonHandle();
			
			assertNotNull(jsonMgr.read(afterDocId, afterDocumentMetadata, readHandle)); 
			assertTrue(afterDocumentMetadata.getCollections().contains(JobRunner.getJobCollection(jobDef.path("id").asText())));
			assertTrue(afterDocumentMetadata.getCollections().contains("/afterJob"));
			assertTrue(afterDocumentMetadata.getPermissions().containsKey("admin"));
			assertTrue(afterDocumentMetadata.getPermissions().get("admin").contains(Capability.READ));
			assertTrue(afterDocumentMetadata.getPermissions().get("admin").contains(Capability.UPDATE));

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testJsonDataStructure() throws Exception {
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

			StructuredQueryBuilder queryBldr = new StructuredQueryBuilder();
			// get only one result
			jsonMgr.setPageLength(1);
			
			// match documents that are in the job collection other than the before or after document
			DocumentPage docPage = jsonMgr.search(
					queryBldr.andNot(queryBldr.collection(JobRunner.getJobCollection(jobDef.path("id").asText())),
							queryBldr.document(JobRunner.getBeforeJobDocumentUri(jobDef.path("id").asText()),
									JobRunner.getAfterJobDocumentUri(jobDef.path("id").asText()))),
					1);
			long docCount = docPage.getTotalSize();

			FileReader fr = new FileReader(csvFile);
			LineNumberReader lnr = new LineNumberReader(fr);
			int linenumber = 0;
			while (lnr.readLine() != null) {
				linenumber++;
			}
			lnr.close();
			// compare with the number of CSV records excluding the CSV header
			assertEquals(docCount, (linenumber - 1));
			
			JacksonHandle firstDocHandle = docPage.nextContent(new JacksonHandle());
			JsonNode firstObject = firstDocHandle.get();

			// verify the record metadata and the instance keys by navigating the nodes using Jackson methods
			assertNotNull(firstObject.get("metadata"));
			assertNotNull(firstObject.get("instance"));

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
