package com.marklogic.client.dhs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.JacksonHandle;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

public class JobRunner {
	private String jobId;
	private int batchSize = 100;
	private String[] roles;
	private int timeIntervalInSeconds = 10;
	private AtomicLong lastSuccessLogTime;
	private AtomicLong lastFailureLogTime;

	public int getTimeIntervalInSeconds() {
		return timeIntervalInSeconds;
	}

	public void setTimeIntervalInSeconds(int timeIntervalInSeconds) {
		this.timeIntervalInSeconds = timeIntervalInSeconds;
	}

	public long getLastSuccessLogTime() {
		return lastSuccessLogTime.longValue();
	}

	public void setLastSuccessLogTime(long lastSuccessLogTime) {
		this.lastSuccessLogTime.set(lastSuccessLogTime);
	}

	public long getLastFailureLogTime() {
		return lastFailureLogTime.longValue();
	}

	public void setLastFailureLogTime(long lastFailureLogTime) {
		this.lastFailureLogTime.set(lastFailureLogTime);
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String... roles) {
		this.roles = roles;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public JobRunner() {
	}

	public String getJobId() {
		return jobId;
	}

	public void run(DatabaseClient client, InputStream csvRecords, InputStream jobControl, JobLogger jobLogger,
			long totalCsvBytes) throws IOException {
		DataMovementManager moveMgr = client.newDataMovementManager();
		JSONDocumentManager jsonMgr = client.newJSONDocumentManager();
		Long startTime = System.currentTimeMillis();

		final class JobRunnerVariables {
			AtomicLong lastSuccessCount = new AtomicLong();
			AtomicLong lastFailureCount = new AtomicLong();
			AtomicLong totalSuccessCount = new AtomicLong();
			AtomicLong totalFailureCount = new AtomicLong();
			AtomicLong lastSuccessLogTime = new AtomicLong();
			AtomicLong lastFailureLogTime = new AtomicLong();
		}

		final JobRunnerVariables jobRunnerVariables = new JobRunnerVariables();

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			ObjectNode jobControlObj = (ObjectNode) objectMapper.readTree(jobControl);
			ObjectNode jobDef = (ObjectNode) jobControlObj.path("job");
			ObjectNode recordDef = (ObjectNode) jobControlObj.path("record");
			if (jobDef.isMissingNode() || recordDef.isMissingNode()) {
				throw new MarkLogicIOException("job Node and/or record Node(s) cannot be empty.");
			}
			String id = jobDef.path("id").asText();
			if (id == null || id.length() == 0) {
				throw new MarkLogicIOException("job id cannot be empty or Null");
			}
			this.jobId = id;

			JsonNode metadataNode = jobDef.path("metadata");
			if(metadataNode.isMissingNode()) {
				throw new MarkLogicIOException("metadata node cannot be empty.");
			}
			DocumentMetadataHandle documentMetadata = new DocumentMetadataHandle();
			documentMetadata.withCollections(getJobCollection(id));
			DocumentPermissions permissions = documentMetadata.getPermissions();
			for (String temp : getRoles()) {
				permissions.add(temp, Capability.READ, Capability.UPDATE);
			}

			CSVConverter converter = new CSVConverter();
			Iterator<ObjectNode> itr = converter.convertObject(csvRecords);
			if (!itr.hasNext()) {
				throw new MarkLogicIOException("No header found.");
			}

			WriteBatcher batcher = moveMgr.newWriteBatcher().withBatchSize(getBatchSize());
			if (jobLogger != null) {
				batcher = batcher.onBatchSuccess(batch -> {

					long lastTime = jobRunnerVariables.lastSuccessLogTime.longValue();
					long currentTime = System.currentTimeMillis();
					long threshhold = lastTime + (getTimeIntervalInSeconds() * 1000);
					long currentTotalSuccessCount = jobRunnerVariables.totalSuccessCount.incrementAndGet();
					if (threshhold <= currentTime
							&& jobRunnerVariables.lastSuccessLogTime.compareAndSet(lastTime, currentTime)) {
						ObjectNode statusNode = new ObjectMapper().createObjectNode();
						statusNode.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());
						statusNode.put("Current status", "Batch Success Recorded.");
						statusNode.put("Estimated total bytes in the CSV file", totalCsvBytes);
						statusNode.put("Bytes read so far", converter.getByteCount());
						statusNode.put("Success Batch Number", batch.getJobBatchNumber());
						statusNode.put("Batch running since last log",
								currentTotalSuccessCount - jobRunnerVariables.lastSuccessCount.get());
						statusNode.put("Total successes", currentTotalSuccessCount);
						jobLogger.status(statusNode);
						jobRunnerVariables.lastSuccessCount.set(currentTotalSuccessCount);
					}
				}).onBatchFailure((batch, throwable) -> {
					long lastTime = jobRunnerVariables.lastFailureLogTime.longValue();
					long currentTime = System.currentTimeMillis();
					long threshhold = lastTime + (getTimeIntervalInSeconds() * 1000);
					long currentTotalFailureCount = jobRunnerVariables.totalFailureCount.incrementAndGet();
					if (threshhold <= currentTime
							&& jobRunnerVariables.lastFailureLogTime.compareAndSet(lastTime, currentTime)) {
						jobLogger.error("Failure for batch number - " + batch.getJobBatchNumber() + "\n"
								+ "Batch running since last log - "
								+ (currentTotalFailureCount - jobRunnerVariables.lastFailureCount.get() + "\n")
								+ ". Total failures - " + currentTotalFailureCount + "\n");
						jobRunnerVariables.lastFailureCount.set(currentTotalFailureCount);
					}
					jobLogger.error("Exception Occured", throwable);
				});

			}
			ObjectLoader loader = new ObjectLoader(batcher, recordDef, getJobDirectory(id), documentMetadata);

			ObjectNode csvNode = itr.next();
			Iterator<String> headerValue = csvNode.fieldNames();
			ArrayNode headers = objectMapper.createArrayNode();
			while (headerValue.hasNext()) {
				headers.add(headerValue.next());
			}

			// write before job document in /jobStart and /jobs/ID collections or send
			// before job payload to DHF endpoint
			String beforeDocId = getBeforeJobDocumentUri(id);

			DocumentMetadataHandle beforeDocumentMetadata = new DocumentMetadataHandle();
			beforeDocumentMetadata.withCollections(getJobCollection(id), "/beforeJob");
			DocumentPermissions beforeDocPermissions = beforeDocumentMetadata.getPermissions();
			for (String temp : getRoles()) {
				beforeDocPermissions.add(temp, Capability.READ, Capability.UPDATE);
			}

			String ingestionStartTime = LocalDateTime.now().toString();

			ObjectNode beforeDocRoot = objectMapper.createObjectNode();
			beforeDocRoot.put("jobId", id);
			beforeDocRoot.set("jobMetadata", metadataNode);
			beforeDocRoot.put("ingestionStartTime", ingestionStartTime);
			beforeDocRoot.set("headers", headers);

			JacksonHandle jacksonHandle = new JacksonHandle(beforeDocRoot);

			ObjectNode statusNode = new ObjectMapper().createObjectNode();
			statusNode.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());
			statusNode.put("Current status", "Writing the before job document to the database");
			statusNode.put("BeforeDocId", beforeDocId);
			jobLogger.status(statusNode);

			jsonMgr.write(beforeDocId, beforeDocumentMetadata, jacksonHandle);
			
			statusNode = new ObjectMapper().createObjectNode();
			statusNode.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());
			statusNode.put("Current status", "Before job document written successfully.");
			jobLogger.status(statusNode);

			statusNode = new ObjectMapper().createObjectNode();
			statusNode.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());
			statusNode.put("Current status", "Starting the job.");
			jobLogger.status(statusNode);

			JobTicket ticket = moveMgr.startJob(batcher);
			loader.loadRecord(csvNode);

			loader.loadRecords(itr);
			batcher.flushAndWait();

			statusNode = new ObjectMapper().createObjectNode();
			statusNode.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());
			statusNode.put("Current status", "Finishing the job.");
			jobLogger.status(statusNode);

			moveMgr.stopJob(ticket);

			String ingestionStopTime = LocalDateTime.now().toString();

			// write after job document with job metadata in /jobEnd and /jobs/ID
			// collections or send after job payload to DHF endpoint
			statusNode = new ObjectMapper().createObjectNode();
			statusNode.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());
			statusNode.put("Current status", "Starting the after job document.");
			jobLogger.status(statusNode);

			String afterDocId = getAfterJobDocumentUri(id);

			DocumentMetadataHandle afterDocumentMetadata = new DocumentMetadataHandle();
			afterDocumentMetadata.withCollections(getJobCollection(id), "/afterJob");
			DocumentPermissions afterDocPermissions = afterDocumentMetadata.getPermissions();
			for (String temp : getRoles()) {
				afterDocPermissions.add(temp, Capability.READ, Capability.UPDATE);
			}

			ObjectNode afterDocRoot = objectMapper.createObjectNode();
			afterDocRoot.put("jobId", id);
			afterDocRoot.set("jobMetadata", metadataNode);
			afterDocRoot.put("ingestionStartTime", ingestionStartTime);
			afterDocRoot.put("ingestionStopTime", ingestionStopTime);
			afterDocRoot.set("headers", headers);
			afterDocRoot.put("numberOfRecords", loader.getCount());

			jacksonHandle = new JacksonHandle(afterDocRoot);

			statusNode = new ObjectMapper().createObjectNode();
			statusNode.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());
			statusNode.put("Current status", "Writing the after job document to the database.");
			statusNode.put("AfterDocId", afterDocId);
			jobLogger.status(statusNode);
			jsonMgr.write(afterDocId, afterDocumentMetadata, jacksonHandle);

			statusNode = new ObjectMapper().createObjectNode();
			statusNode.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());
			statusNode.put("Current status", "After job document written successfully.");
			statusNode.put(" Number of records written to the database - ", loader.getCount());
			jobLogger.status(statusNode);

		} catch (Exception e) {
			jobLogger.error(e.getMessage());
		} finally {
			ObjectNode statusNode = new ObjectMapper().createObjectNode();
			statusNode.put("Timestamp", new Timestamp(System.currentTimeMillis()).toString());
			statusNode.put("Current status", "Finishing the job.");
			statusNode.put("Time elapsed in seconds", (System.currentTimeMillis() - startTime) / 1000.0f);
			jobLogger.status(statusNode);

			moveMgr.release();
		}
	}

	public static String jobFileFor(String csvFile) {
		if (!csvFile.endsWith(".csv")) {
			throw new IllegalArgumentException("Invalid object key: " + csvFile);
		}
		return csvFile.substring(0, csvFile.length() - 4) + "-MARKLOGIC.json";
	}

	public static String getBeforeJobDocumentUri(String id) {
		return ("/jobs/" + id + "/beforeJob.json");
	}

	public static String getAfterJobDocumentUri(String id) {
		return ("/jobs/" + id + "/afterJob.json");
	}

	public static String getJobCollection(String id) {
		return ("/jobs/" + id);
	}

	public static String getJobDirectory(String id) {
		return "/jobs/" + id + "/";
	}

}