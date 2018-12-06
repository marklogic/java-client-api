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
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;

public class JobRunner {
   private String jobId;
   private String jobDirectory;
   private int batchSize = 100;
   private String[] roles;
   private int timeIntervalInSeconds = 10;
   private AtomicLong lastSuccessLogTime;
   private AtomicLong lastFailureLogTime;
   private Logger logger;
   
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

public Logger getLogger() {
	return logger;
}
public void setLogger(Logger logger) {
	this.logger = logger;
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

public void run(DatabaseClient client, InputStream csvRecords, InputStream jobControl) throws IOException {
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
         // TODO: cope with MissingNode on all path() calls
          ObjectNode jobDef        = (ObjectNode) jobControlObj.path("job");
          ObjectNode recordDef     = (ObjectNode) jobControlObj.path("record");
          
          if(jobDef.isMissingNode()) {
         	 throw new MarkLogicIOException("job Node cannot be empty.");
          }
          String id = jobDef.path("id").asText();
          if(id==null || id.length()==0) {
         	 throw new MarkLogicIOException("job id cannot be empty or Null");
          }
         this.jobId = id;

  		JsonNode metadataNode = jobDef.path("metadata");
		DocumentMetadataHandle documentMetadata = new DocumentMetadataHandle();
		documentMetadata.withCollections(getJobCollection(id));
		DocumentPermissions permissions = documentMetadata.getPermissions();
		for(String temp:getRoles()) {
			permissions.add(temp, Capability.READ, Capability.UPDATE);
		}

         WriteBatcher batcher = moveMgr.newWriteBatcher()
               .withBatchSize(getBatchSize());
			if (logger != null) {
				batcher = batcher.onBatchSuccess(batch -> {
					if (getBatchSize() == 1) {
						logger.info("Success for the batch-" + batch.getJobBatchNumber() + ". Total successes - 1");
						jobRunnerVariables.lastSuccessCount.set(jobRunnerVariables.totalSuccessCount.longValue() + 1);
						jobRunnerVariables.lastSuccessLogTime.set(System.currentTimeMillis());
					} else {
						long lastTime = jobRunnerVariables.lastSuccessLogTime.longValue();
						long currentTime = System.currentTimeMillis();
						long threshhold = lastTime + (getTimeIntervalInSeconds() * 1000);
						if (threshhold <= currentTime
								&& jobRunnerVariables.lastSuccessLogTime.compareAndSet(lastTime, currentTime)) {
							logger.info("Success for batch number - " + batch.getJobBatchNumber()
									+ ". Batch running since last log - "
									+ (jobRunnerVariables.totalSuccessCount.get()
											- jobRunnerVariables.lastSuccessCount.get() + 1)
									+ ". Total successes - " + jobRunnerVariables.totalSuccessCount + 1);
							jobRunnerVariables.lastSuccessCount
									.set(jobRunnerVariables.totalSuccessCount.longValue() + 1);
							jobRunnerVariables.lastSuccessLogTime.set(System.currentTimeMillis());
						}
					}
					jobRunnerVariables.totalSuccessCount.incrementAndGet();
				}).onBatchFailure((batch, throwable) -> {
					if (getBatchSize() == 1) {
						logger.info("Failure for the batch-" + batch.getJobBatchNumber() + ". Total failures - 1");
						jobRunnerVariables.lastFailureCount.set(jobRunnerVariables.totalSuccessCount.longValue() + 1);
						jobRunnerVariables.lastFailureLogTime.set(System.currentTimeMillis());
					} else {
						long lastTime = jobRunnerVariables.lastSuccessLogTime.longValue();
						long currentTime = System.currentTimeMillis();
						long threshhold = lastTime + (getTimeIntervalInSeconds() * 1000);
						if (threshhold <= currentTime
								&& jobRunnerVariables.lastFailureLogTime.compareAndSet(lastTime, currentTime)) {

							logger.info("Failure for batch number - " + batch.getJobBatchNumber()
									+ ". Batch running since last log - "
									+ (jobRunnerVariables.totalFailureCount.get()
											- jobRunnerVariables.lastFailureCount.get() + 1)
									+ ". Total failures - " + jobRunnerVariables.totalFailureCount + 1);
							jobRunnerVariables.lastFailureCount.set(jobRunnerVariables.totalSuccessCount.longValue() + 1);
							jobRunnerVariables.lastFailureLogTime.set(System.currentTimeMillis());
						}
					}
					jobRunnerVariables.totalFailureCount.incrementAndGet();
					logger.error("Exception Occured", throwable.getMessage());
				});

         }
         ObjectLoader loader = new ObjectLoader(batcher, recordDef, getJobDirectory(id), documentMetadata);
         CSVConverter converter = new CSVConverter();  

         Iterator<ObjectNode> itr = converter.convertObject(csvRecords);
         if(!itr.hasNext()) {
        	 throw new MarkLogicIOException("No header found.");
         }
         ObjectNode csvNode = itr.next();
         Iterator<String> headerValue = csvNode.fieldNames();
         ArrayNode headers = objectMapper.createArrayNode();
         while(headerValue.hasNext()) {
        	 headers.add(headerValue.next());
         }
         
   // write before job document in /jobStart and /jobs/ID collections or send before job payload to DHF endpoint  
         String beforeDocId = getBeforeJobDocumentUri(id);
         
         DocumentMetadataHandle beforeDocumentMetadata = new DocumentMetadataHandle();
         beforeDocumentMetadata.withCollections(getJobCollection(id), "/beforeJob");
         DocumentPermissions beforeDocPermissions = beforeDocumentMetadata.getPermissions();
         for(String temp:getRoles()) {
        	 beforeDocPermissions.add(temp, Capability.READ, Capability.UPDATE);
 		}
         
         String ingestionStartTime = LocalDateTime.now().toString();
         
         ObjectNode beforeDocRoot = objectMapper.createObjectNode();
         beforeDocRoot.put("jobId", id);
         beforeDocRoot.set("jobMetadata",  metadataNode);
         beforeDocRoot.put("ingestionStartTime",  ingestionStartTime);
         beforeDocRoot.set("headers",  headers);
         
         JacksonHandle jacksonHandle = new JacksonHandle(beforeDocRoot);
         
         logger.info("Writing the before job document to the database. BeforeDocId - "+beforeDocId);
         jsonMgr.write(beforeDocId, beforeDocumentMetadata, jacksonHandle);
         logger.info("Starting the job.");
         JobTicket ticket = moveMgr.startJob(batcher);
         loader.loadRecord(csvNode);
         
         loader.loadRecords(itr);
         batcher.flushAndWait();
         logger.info("Finishing the job");
         moveMgr.stopJob(ticket);
         
         String ingestionStopTime = LocalDateTime.now().toString();

// write after job document with job metadata in /jobEnd and /jobs/ID collections or send after job payload to DHF endpoint
         logger.info("Starting the after job document.");
         String afterDocId = getAfterJobDocumentUri(id);
         
         DocumentMetadataHandle afterDocumentMetadata = new DocumentMetadataHandle();
         afterDocumentMetadata.withCollections(getJobCollection(id), "/afterJob");
         DocumentPermissions afterDocPermissions = afterDocumentMetadata.getPermissions();
         for(String temp:getRoles()) {
        	 afterDocPermissions.add(temp, Capability.READ, Capability.UPDATE);
 		}
         
         ObjectNode afterDocRoot = objectMapper.createObjectNode();
         afterDocRoot.put("jobId", id);
         afterDocRoot.set("jobMetadata",  metadataNode);
         afterDocRoot.put("ingestionStartTime",  ingestionStartTime);
         afterDocRoot.put("ingestionStopTime",  ingestionStopTime);
         afterDocRoot.set("headers",  headers);
         afterDocRoot.put("numberOfRecords", loader.getCount());
         
         jacksonHandle = new JacksonHandle(afterDocRoot);
         logger.info("Writing the after job document to the database. AfterDocId - "+afterDocId);
         jsonMgr.write(afterDocId, afterDocumentMetadata, jacksonHandle);
         logger.info("Finished writing the after job document at - " + LocalDateTime.now().toString() + " Number of records written to the database - " + loader.getCount());

      } catch(Exception e){
    	  logger.error(e.getMessage());
      }finally {
    	  logger.info("Finishing the job. Time elapsed = " + (System.currentTimeMillis() - startTime)/1000.0f + " seconds.");
    	  moveMgr.release();
      }
   }

   public static String jobFileFor(String csvFile) {
      if (!csvFile.endsWith(".csv")) {
         throw new IllegalArgumentException("Invalid object key: "+csvFile);
      }
      return csvFile.substring(0, csvFile.length() - 4) + "-MARKLOGIC.json";
   }
   public static String getBeforeJobDocumentUri(String id) {
	   return ("/jobs/"+id+"/beforeJob.json");
   }
   public static String getAfterJobDocumentUri(String id) {
	   return ("/jobs/"+id+"/afterJob.json");
   }
   public static String getJobCollection(String id) {
	   return ("/jobs/"+ id);
   }
   public static String getJobDirectory(String id) {
      return "/jobs/"+id+"/";
   }
}
