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
import com.marklogic.client.io.JacksonHandle;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobRunner {
   private String jobId;
   private String jobDirectory;
   private int batchSize = 100;
   private Set<String> roles;
   private int countSinceLastLog = 0;
   private int totalSuccessCount = 0;
   private int totalFailureCount = 0;
   private int timeIntervalInSeconds = 10;
   public int getTimeIntervalInSeconds() {
	return timeIntervalInSeconds;
}
public void setTimeIntervalInSeconds(int timeIntervalInSeconds) {
	this.timeIntervalInSeconds = timeIntervalInSeconds;
}

private long lastSuccessLogTime;
   public long getLastSuccessLogTime() {
	return lastSuccessLogTime;
}
public void setLastSuccessLogTime(long lastSuccessLogTime) {
	this.lastSuccessLogTime = lastSuccessLogTime;
}
public long getLastFailureLogTime() {
	return lastFailureLogTime;
}
public void setLastFailureLogTime(long lastFailureLogTime) {
	this.lastFailureLogTime = lastFailureLogTime;
}

private long lastFailureLogTime;


private static final Logger logger = (Logger) LoggerFactory
		    .getLogger(JobRunner.class);
public Set<String> getRoles() {
	return roles;
}
public void setRoles(Set<String> roles) {
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
		documentMetadata.withCollections("/jobs/"+id);
		logger.info("Adding read and update permissions for the roles");
		for(String temp:getRoles()) {
			documentMetadata.withPermission(temp, Capability.READ, Capability.UPDATE);
		}
         
// TODO: permissions

         WriteBatcher batcher = moveMgr.newWriteBatcher()
// TODO: sized to 500 in real thing?
               .withBatchSize(getBatchSize())
// TODO: intermittent logging using logger
// https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_awslogs.html
               .onBatchSuccess(
                     batch -> {
                    	 if(getBatchSize()==1) {
                    		 System.out.println("Number of batch = 1. Batch succeeded");
                    	 } else {
                    		 if(totalSuccessCount == 0){
                    			 logger.info("Success for the batch-" + batch.getJobBatchNumber());
                    			 setLastSuccessLogTime(System.currentTimeMillis());
                    			 totalSuccessCount++;
                    			 countSinceLastLog = 1;
                    		 } else if(((System.currentTimeMillis() - getLastSuccessLogTime())/1000) >= getTimeIntervalInSeconds()) {
                    			 setLastSuccessLogTime(System.currentTimeMillis());
                    			 countSinceLastLog = 1;
                    			 totalSuccessCount++;
                    			 logger.info("Success for batch number - " + batch.getJobBatchNumber() + ". Batch running since last log - " + countSinceLastLog + ". Total successes - " + totalSuccessCount);
                    		 } else {
                    			 totalSuccessCount++;
                    			 countSinceLastLog++;
                    		 }
                    	 }
                     })
               .onBatchFailure(
                     (batch, throwable) -> {
                    	 if(getBatchSize()==1) {
                    		 System.out.println("Number of batch = 1. Batch failed");
                    	 } else {
                    		 if(totalFailureCount == 0){
                    			 logger.info("Failure for the batch-" + batch.getJobBatchNumber());
                    			 setLastFailureLogTime(System.currentTimeMillis());
                    			 totalFailureCount++;
                    			 countSinceLastLog = 1;
                    		 } else if(((System.currentTimeMillis() - getLastFailureLogTime())/1000) >= getTimeIntervalInSeconds()) {
                    			 countSinceLastLog = 1;
                    			 totalFailureCount++;
                    			 logger.info("Failure for batch number - " + batch.getJobBatchNumber() + ". Batch running since last log - " + countSinceLastLog + ". Total failures - " + totalFailureCount);
                    		 } else {
                    			 totalFailureCount++;
                    			 countSinceLastLog++;
                    		 }
                    	 }
                     });


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
         logger.info("Starting the before job document.");
         String beforeDocId = "/jobs/"+id+"/beforeJob.json";
         
         DocumentMetadataHandle beforeDocumentMetadata = new DocumentMetadataHandle();
         beforeDocumentMetadata.withCollections("/jobs/"+beforeDocId, "/beforeJob");
         logger.info("Adding read and update permissions to before job document for the roles");
         for(String temp:getRoles()) {
        	 beforeDocumentMetadata.withPermission(temp, Capability.READ, Capability.UPDATE);
 		}
         
         String ingestionStartTime = LocalDateTime.now().toString();
         
         ObjectNode beforeDocRoot = objectMapper.createObjectNode();
         beforeDocRoot.put("jobId", id);
         beforeDocRoot.set("jobMetadata",  metadataNode);
         beforeDocRoot.put("ingestionStartTime",  ingestionStartTime);
         beforeDocRoot.set("headers",  headers);
         
         JacksonHandle jacksonHandle = new JacksonHandle(beforeDocRoot);
         
         logger.info("Writing the before job document to the database.");
         jsonMgr.write(beforeDocId, beforeDocumentMetadata, jacksonHandle);
         
         JobTicket ticket = moveMgr.startJob(batcher);
         loader.loadRecord(csvNode);
         
         loader.loadRecords(itr);
         batcher.flushAndWait();
         moveMgr.stopJob(ticket);
         
         String ingestionStopTime = LocalDateTime.now().toString();

// write after job document with job metadata in /jobEnd and /jobs/ID collections or send after job payload to DHF endpoint
         logger.info("Starting the after job document.");
         String afterDocId = "/jobs/"+id+"/afterJob.json";
         
         DocumentMetadataHandle afterDocumentMetadata = new DocumentMetadataHandle();
         afterDocumentMetadata.withCollections("/jobs/"+afterDocId, "/afterJob");
         logger.info("Adding read and update permissions to after job document for the roles");
         for(String temp:getRoles()) {
        	 afterDocumentMetadata.withPermission(temp, Capability.READ, Capability.UPDATE);
 		}
         
         ObjectNode afterDocRoot = objectMapper.createObjectNode();
         afterDocRoot.put("jobId", id);
         afterDocRoot.set("jobMetadata",  metadataNode);
         afterDocRoot.put("ingestionStartTime",  ingestionStartTime);
         afterDocRoot.put("ingestionStopTime",  ingestionStopTime);
         afterDocRoot.set("headers",  headers);
         afterDocRoot.put("numberOfRecords", loader.getCount());
         
         jacksonHandle = new JacksonHandle(afterDocRoot);
         logger.info("Writing the after job document to the database.");
         jsonMgr.write(afterDocId, afterDocumentMetadata, jacksonHandle);
         
      } finally {
    	  logger.info("Finishing the job. Time elapsed = " + (System.currentTimeMillis() - startTime)/1000 + "seconds.");
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
}
