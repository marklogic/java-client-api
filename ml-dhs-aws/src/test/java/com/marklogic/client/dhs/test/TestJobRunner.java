package com.marklogic.client.dhs.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.dhs.JobLogger;
import com.marklogic.client.dhs.JobRunner;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestJobRunner {
	static final private String csvFile = "data" + File.separator + "OrderLines.csv";
	static final private String role = "rest-tracer";

	DatabaseClient client;

	public class TestJobLogger implements JobLogger {
		int countSuccesses = 0;
		int countFailures = 0;

		@Override
		public void status(ObjectNode obj) {
			countSuccesses++;
			assertNotNull(obj);
			assertNotNull(obj.get("Timestamp"));
			assertNotNull(obj.get("Current status"));
			if (!obj.path("Current status").isMissingNode()
					&& obj.get("Current status").textValue().equals("Batch Success Recorded.")) {
				assertNotNull(obj.get("Estimated total bytes in the CSV file"));
				assertNotNull(obj.get("Bytes read so far"));
				assertNotNull(obj.get("Success Batch Number"));
				assertNotNull(obj.get("Batch running since last log"));
				assertNotNull(obj.get("Total successes"));
			}
		}

		@Override
		public void error(String msg) {
			countFailures++;
		}

		@Override
		public void error(String msg, Throwable err) {
			countFailures++;
		}

	}

	@Before
	public void setUp() {
		this.client = DatabaseClientFactory.newClient("localhost", 8012,
				new DatabaseClientFactory.DigestAuthContext("rest-admin", "x"));
	}

	@Test
	public void testRun() throws IOException {
		String jobFile = JobRunner.jobFileFor(csvFile);

		String jobCollection = null;
		TestJobLogger testJobLogger = new TestJobLogger();

		JobRunner jobRunner = new JobRunner();
		jobRunner.setRoles(role);

		try (InputStream csvStream = openStream(csvFile);
				InputStream jobStream = openStream(jobFile);
				InputStream jobStream2 = openStream(jobFile)) {
			File csvFilehandle = new File(TestJobRunner.class.getClassLoader().getResource(csvFile).toURI());
			long totalBytes = csvFilehandle.length();
			jobRunner.run(client, csvStream, jobStream, testJobLogger, totalBytes);

			assertTrue(testJobLogger.countSuccesses > 0);
			assertTrue(testJobLogger.countFailures == 0);

			jobCollection = JobRunner.getJobCollection(jobRunner.getJobId());

			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode jobControlObj = (ObjectNode) objectMapper.readTree(jobStream2);

			testBeforeJobDocument(jobControlObj);
			testAfterJobDocument(jobControlObj);
			testJsonDataStructure(jobControlObj);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("test exception");
		} finally {
			if (jobCollection != null) {
				QueryManager queryMgr = client.newQueryManager();
				DeleteQueryDefinition deleteDef = queryMgr.newDeleteDefinition();
				deleteDef.setCollections(jobCollection);
				// comment to inspect the database after running the test
				queryMgr.delete(deleteDef);
			}
		}
	}

	public void testBeforeJobDocument(ObjectNode jobControlObj) {
		JSONDocumentManager jsonMgr = client.newJSONDocumentManager();

		ObjectNode jobDef = (ObjectNode) jobControlObj.get("job");

		String beforeDocId = JobRunner.getBeforeJobDocumentUri(jobDef.path("id").asText());
		assertNotNull(jsonMgr.exists(beforeDocId));

		DocumentMetadataHandle beforeDocumentMetadata = new DocumentMetadataHandle();
		JacksonHandle readHandle = new JacksonHandle();

		assertNotNull(jsonMgr.read(beforeDocId, beforeDocumentMetadata, readHandle));
		assertTrue(beforeDocumentMetadata.getCollections()
				.contains(JobRunner.getJobCollection(jobDef.path("id").asText())));
		assertTrue(beforeDocumentMetadata.getCollections().contains("/beforeJob"));
		assertTrue(beforeDocumentMetadata.getPermissions().containsKey(role));
		assertTrue(beforeDocumentMetadata.getPermissions().get(role).contains(DocumentMetadataHandle.Capability.READ));
		assertTrue(
				beforeDocumentMetadata.getPermissions().get(role).contains(DocumentMetadataHandle.Capability.UPDATE));

		JsonNode readHandleNode = readHandle.get();
		assertNotNull(readHandleNode);
		assertNotNull(readHandleNode.fields());
	}

	public void testAfterJobDocument(ObjectNode jobControlObj) {
		JSONDocumentManager jsonMgr = client.newJSONDocumentManager();
		ObjectNode jobDef = (ObjectNode) jobControlObj.get("job");

		String afterDocId = JobRunner.getAfterJobDocumentUri(jobDef.path("id").asText());
		assertNotNull(jsonMgr.exists(afterDocId));
		DocumentMetadataHandle afterDocumentMetadata = new DocumentMetadataHandle();
		JacksonHandle readHandle = new JacksonHandle();

		assertNotNull(jsonMgr.read(afterDocId, afterDocumentMetadata, readHandle));
		assertTrue(afterDocumentMetadata.getCollections()
				.contains(JobRunner.getJobCollection(jobDef.path("id").asText())));
		assertTrue(afterDocumentMetadata.getCollections().contains("/afterJob"));
		assertTrue(afterDocumentMetadata.getPermissions().containsKey(role));
		assertTrue(afterDocumentMetadata.getPermissions().get(role).contains(DocumentMetadataHandle.Capability.READ));
		assertTrue(afterDocumentMetadata.getPermissions().get(role).contains(DocumentMetadataHandle.Capability.UPDATE));

		JsonNode readHandleNode = readHandle.get();
		assertNotNull(readHandleNode);
		assertNotNull(readHandleNode.fields());
	}

	public void testJsonDataStructure(ObjectNode jobControlObj) {
		JSONDocumentManager jsonMgr = client.newJSONDocumentManager();
		jsonMgr.setMetadataCategories(DocumentManager.Metadata.COLLECTIONS, DocumentManager.Metadata.PERMISSIONS);

		ObjectNode jobDef = (ObjectNode) jobControlObj.get("job");

		int linenumber = 0;
		try (LineNumberReader lnr = new LineNumberReader(new InputStreamReader(openStream(csvFile)))) {
			while (lnr.readLine() != null) {
				linenumber++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("test exception");
		}

		DocumentPage docPage = null;
		try {
			StructuredQueryBuilder queryBldr = new StructuredQueryBuilder();
			// get only one result
			jsonMgr.setPageLength(1);

			// match documents that are in the job collection other than the before or after
			// document
			docPage = jsonMgr.search(
					queryBldr.andNot(queryBldr.collection(JobRunner.getJobCollection(jobDef.path("id").asText())),
							queryBldr.document(JobRunner.getBeforeJobDocumentUri(jobDef.path("id").asText()),
									JobRunner.getAfterJobDocumentUri(jobDef.path("id").asText()))),
					1);
			long docCount = docPage.getTotalSize();

			// compare with the number of CSV records excluding the CSV header
			assertEquals(docCount, (linenumber - 1));

			DocumentRecord docRecord = docPage.next();

			DocumentMetadataHandle firstMetadata = docRecord.getMetadata(new DocumentMetadataHandle());
			assertTrue(firstMetadata.getCollections().contains(JobRunner.getJobCollection(jobDef.path("id").asText())));
			assertTrue(firstMetadata.getPermissions().containsKey(role));
			assertTrue(firstMetadata.getPermissions().get(role).contains(DocumentMetadataHandle.Capability.READ));
			assertTrue(firstMetadata.getPermissions().get(role).contains(DocumentMetadataHandle.Capability.UPDATE));

			JacksonHandle firstDocHandle = docRecord.getContent(new JacksonHandle());
			JsonNode firstObject = firstDocHandle.get();

			// verify the record metadata and the instance keys by navigating the nodes
			// using Jackson methods
			JsonNode metadataNode = firstObject.get("metadata");
			assertNotNull(metadataNode);
			assertNotNull(metadataNode.fields());

			JsonNode instanceNode = firstObject.get("instance");
			assertNotNull(instanceNode);
			assertNotNull(instanceNode.fields());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("test exception");
		} finally {
			if (docPage != null) {
				docPage.close();
			}
		}
	}

	@After
	public void closeSetUp() {
		client.release();
	}

	public InputStream openStream(String fileName) throws IOException {
		InputStream file = TestJobRunner.class.getClassLoader().getResourceAsStream(fileName);
		if (file == null) {
			throw new IOException("could not read file: " + fileName);
		}
		return file;
	}
}
