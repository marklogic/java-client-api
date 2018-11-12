package com.marklogic.client.dhs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.UUID;

public class JobRunner {
   private String jobId;
   private String jobDirectory;

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
		String jobMetadataValue = metadataNode.asText();
		DocumentMetadataHandle documentMetadata = new DocumentMetadataHandle();
		documentMetadata.withCollections("/jobs/"+id);
         
// TODO: permissions

         WriteBatcher batcher = moveMgr.newWriteBatcher()
// TODO: sized to 500 in real thing?
               .withBatchSize(2)
// TODO: intermittent logging using logger
// https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_awslogs.html
               .onBatchSuccess(
                     batch -> {
                        System.out.println(
                              "success for batch: "+batch.getJobBatchNumber()+
                                    ", items: "+batch.getItems().length
                        );
                     })
               .onBatchFailure(
                     (batch, throwable) -> {
                        System.out.println(
                              "failure for batch: "+batch.getJobBatchNumber()+
                                    ", items: "+batch.getItems().length
                        );
                        throwable.printStackTrace(System.out);
                     });


         ObjectLoader loader = new ObjectLoader(batcher, recordDef, getJobDirectory(), documentMetadata);
         CSVConverter converter = new CSVConverter();  

         Iterator<ObjectNode> itr = converter.convertObject(csvRecords);
         if(!itr.hasNext()) {
        	 throw new MarkLogicIOException("No header found.");
         }
         ObjectNode csvNode = itr.next();
         
         String headerValue = csvNode.fieldNames().toString();
         
   // write before job document in /jobStart and /jobs/ID collections or send before job payload to DHF endpoint        
         String beforeDocId = "/jobs/"+id+"/beforeJob.json";
         
         DocumentMetadataHandle beforeDocumentMetadata = new DocumentMetadataHandle();
         beforeDocumentMetadata.withCollections("/jobs/"+beforeDocId, "/beforeJob");
         
         String ingestionStartTime = LocalDateTime.now().toString();
         
         ObjectNode beforeDocRoot = objectMapper.createObjectNode();
         beforeDocRoot.put("job id", id);
         beforeDocRoot.put("jobMetadataValue",  jobMetadataValue);
         beforeDocRoot.put("ingestionStartTime",  ingestionStartTime);
         beforeDocRoot.put("headerValue",  headerValue);
         
         JacksonHandle jacksonHandle = new JacksonHandle(beforeDocRoot);
         jsonMgr.write(beforeDocId, beforeDocumentMetadata, jacksonHandle);
         
         JobTicket ticket = moveMgr.startJob(batcher);
         loader.loadRecord(csvNode);
         
         loader.loadRecords(itr);
         batcher.flushAndWait();
         moveMgr.stopJob(ticket);
         
         String ingestionStopTime = LocalDateTime.now().toString();

// write after job document with job metadata in /jobEnd and /jobs/ID collections or send after job payload to DHF endpoint
         String afterDocId = "/jobs/"+id+"/afterJob.json";
         
         DocumentMetadataHandle afterDocumentMetadata = new DocumentMetadataHandle();
         afterDocumentMetadata.withCollections("/jobs/"+afterDocId, "/afterJob");
         
         ObjectNode afterDocRoot = objectMapper.createObjectNode();
         afterDocRoot.put("job id", id);
         afterDocRoot.put("jobMetadataValue",  jobMetadataValue);
         afterDocRoot.put("ingestionStartTime",  ingestionStartTime);
         afterDocRoot.put("ingestionStopTime",  ingestionStopTime);
         
         jacksonHandle = new JacksonHandle(afterDocRoot);
         jsonMgr.write(afterDocId, afterDocumentMetadata, jacksonHandle);
         
         JobTicket afterTicket = moveMgr.startJob(batcher);

         batcher.flushAndWait();
         moveMgr.stopJob(afterTicket);
         
      } finally {
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
