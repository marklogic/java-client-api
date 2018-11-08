package com.marklogic.client.dhs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.io.DocumentMetadataHandle;

import java.io.IOException;
import java.io.InputStream;
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

      try {
         DocumentMetadataHandle documentMetadata = new DocumentMetadataHandle();
         documentMetadata.withCollections("/jobs/"+jobId);
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

         ObjectMapper objectMapper = new ObjectMapper();
         ObjectNode jobControlObj = (ObjectNode) objectMapper.readTree(jobControl);
         ObjectNode jobDef        = (ObjectNode) jobControlObj.get("job");
         ObjectNode recordDef     = (ObjectNode) jobControlObj.get("record");

         ObjectLoader loader = new ObjectLoader(batcher, recordDef, getJobDirectory(), documentMetadata);
         CSVConverter converter = new CSVConverter();

// TODO: write before job document in /jobStart and /jobs/ID collections or send before job payload to DHF endpoint

         JobTicket ticket = moveMgr.startJob(batcher);
         loader.loadRecords(converter.convertObject(csvRecords));
         batcher.flushAndWait();
         moveMgr.stopJob(ticket);

// TODO: write after job document with job metadata in /jobEnd and /jobs/ID collections or send after job payload to DHF endpoint
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
