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
import java.util.UUID;
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

// TODO: pass in environmental information from AWS for job and record metadata
   public JobRunner() {
      this(null);
   }
   public JobRunner(String jobId) {
      if (jobId == null) {
         jobId = UUID.randomUUID().toString();
      }
      this.jobId = jobId;
      this.jobDirectory = "/jobs/"+jobId+"/";
   }

   public String getJobId() {
      return jobId;
   }
   public String getJobDirectory() {
      return jobDirectory;
   }

public void run(DatabaseClient client, InputStream csvRecords, InputStream jobControl) throws IOException {
      DataMovementManager moveMgr = client.newDataMovementManager();
      JSONDocumentManager jsonMgr = client.newJSONDocumentManager();
      Long startTime = System.currentTimeMillis();
      
      final class JobRunnerVariables{
    	  AtomicLong lastSuccessCount;
    	  AtomicLong lastFailureCount;
    	  AtomicLong totalSuccessCount;
    	  AtomicLong totalFailureCount;

			synchronized boolean trackLastSuccessCount() {
				if (((System.currentTimeMillis() - getLastSuccessLogTime()) / 1000) >= getTimeIntervalInSeconds()) {
					setLastSuccessLogTime(System.currentTimeMillis());
					lastSuccessCount.set(totalSuccessCount.longValue() + 1);
					return true;
				}
				return false;
			}
    	  synchronized boolean trackLastFailureCount(){
    		  if(((System.currentTimeMillis() - getLastFailureLogTime())/1000) >= getTimeIntervalInSeconds()) {
    			  setLastFailureLogTime(System.currentTimeMillis());
    			  lastFailureCount.set(totalFailureCount.longValue() + 1);
    			  return true;
    		  }
    		  return false;
    	  }
      }
      final JobRunnerVariables jobRunnerVariables = new JobRunnerVariables();
      
      try {
          ObjectMapper objectMapper = new ObjectMapper();
          
          ObjectNode jobControlObj = (ObjectNode) objectMapper.readTree(jobControl);
          ObjectNode jobDef        = (ObjectNode) jobControlObj.get("job");
          ObjectNode recordDef     = (ObjectNode) jobControlObj.get("record");
          
          if(jobDef.isMissingNode()) {
         	 throw new MarkLogicIOException("job Node cannot be empty.");
          }
          String id = jobDef.path("id").asText();
          if(id==null || id.length()==0) {
         	 throw new MarkLogicIOException("job id cannot be empty or Null");
          }
  		JsonNode metadataNode = jobDef.path("metadata");
		DocumentMetadataHandle documentMetadata = new DocumentMetadataHandle();
		documentMetadata.withCollections(getJobCollection(id));
		DocumentPermissions permissions = documentMetadata.getPermissions();
		for(String temp:getRoles()) {
			permissions.add(temp, Capability.READ, Capability.UPDATE);
		}
         
// TODO: permissions

         WriteBatcher batcher = moveMgr.newWriteBatcher()
// TODO: sized to 500 in real thing?
               .withBatchSize(getBatchSize());
// TODO: intermittent logging using logger
// https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_awslogs.html
         if(logger!=null) {
        	 batcher = batcher.onBatchSuccess(
                     batch -> {
                    	 if(getBatchSize()==1) {
                    		 System.out.println("Success for the batch-" + batch.getJobBatchNumber() + ". Total successes - 1");
                    	 } else {
                    		 if(jobRunnerVariables.totalSuccessCount.equals(0)){
                    			 logger.info("Success for batch number - " + batch.getJobBatchNumber());
                    			 setLastSuccessLogTime(System.currentTimeMillis());
                    			 jobRunnerVariables.lastSuccessCount.set(1);;
                    		 } else if(jobRunnerVariables.trackLastSuccessCount()) {
                    			 logger.info("Success for batch number - " + batch.getJobBatchNumber() + ". Batch running since last log - " + (jobRunnerVariables.totalSuccessCount.get() - jobRunnerVariables.lastSuccessCount.get() + 1) + ". Total successes - " + jobRunnerVariables.totalSuccessCount + 1);
                    		 } 
                    		 jobRunnerVariables.totalSuccessCount.incrementAndGet();
                    	 }
                     })
               .onBatchFailure(
                     (batch, throwable) -> {
                    	 if(getBatchSize()==1) {
                    		 System.out.println("Failure for the batch-" + batch.getJobBatchNumber() + ". Total failures - 1");
                    	 } else {
                    		 if(jobRunnerVariables.totalFailureCount.equals(0)){
                    			 logger.info("Failure for the batch-" + batch.getJobBatchNumber());
                    			 setLastFailureLogTime(System.currentTimeMillis());
                    			 jobRunnerVariables.lastFailureCount.set(1);;
                    		 } else if(jobRunnerVariables.trackLastFailureCount()) {
                    			 logger.info("Failure for batch number - " + batch.getJobBatchNumber() + ". Batch running since last log - " + (jobRunnerVariables.totalFailureCount.get() - jobRunnerVariables.lastFailureCount.get() + 1) + ". Total failures - " + jobRunnerVariables.totalFailureCount + 1);
                    		 }
                    		 jobRunnerVariables.totalFailureCount.incrementAndGet();
                    	 }
                    	 System.out.println(throwable.getStackTrace());
                     });

         }
         ObjectLoader loader = new ObjectLoader(batcher, recordDef, getJobDirectory(), documentMetadata);
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

// TODO: archaic - was part of the staging directory concept
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
   public static String getJobCollection(String docUri) {
	   return ("/jobs/"+ docUri);
   }
}
