package com.marklogic.client.example.cookbook.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.StructuredQueryBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RecordJobInformation {
  static int threadCount = 3;
  static int batchSize = 10;
  public static void main( String args[] ) throws IOException {
    Util.ExampleProperties props = Util.loadProperties();

    DatabaseClient client = DatabaseClientFactory.newClient(props.host, props.port,
        new DatabaseClientFactory.DigestAuthContext(props.adminUser, props.adminPassword));
    DataMovementManager dm = client.newDataMovementManager();

    // Create a WriteBatcher for which job information needs to be recorded
    WriteBatcher wb = dm.newWriteBatcher()
      .onBatchSuccess(batch -> {
          System.out.println("Batch written " + batch.getJobBatchNumber());
      })
      .onBatchFailure((batch, failure) ->
        System.out.println("Batch " + batch.getJobBatchNumber() + " failed " +
          failure.getMessage()))
      .withBatchSize(batchSize)
      .withThreadCount(threadCount);

    // Create a job properties map which should be stored with the job information
    Map<String, String> properties = new HashMap<String, String>();
    properties.put("key1", "value1");
    properties.put("key2", "value2");

    // Persist Job information for Write Batcher by creating an instance of JobInformationRecorder
    JobInformationRecorder writePersister = new JobInformationRecorder(wb)
        // Pass in a DatabaseClient to create an internal WriteBatcher to write job details into
        // the database.
        .withDatabaseClient(client)
        // This is optional and if this is called, one document would be created for each batch
        // which would have all the batch information and success/failure information
        // along with the list of all the URIs in the batch
        .withBatchInformation()
        // Add the properties map so that the JobInformationRecorder will persist them
        // along with the other job information like start and end time.
        .withProperty(properties);

    // Start the WriteBatcher Job and the JobInformationRecorder would automatically start persisting
    // job information. After each batch gets succeeded/failed, it would add a document for the batch
    // with all the batch information.
    dm.startJob(wb);
    for (int j = 1; j <= 109 ; j++) {
      String uri = "/threadpool/" + j + ".xml";
      if(j == 105) {
        // Added an invalid JSON so that the last batch would fail
        wb.add(uri, new StringHandle("{son:son}").withFormat(Format.JSON));
        continue;
      }
      wb.add(uri, new StringHandle("<xmlsample>test</xmlsample>"));
    }
    wb.flushAndWait();
    dm.stopJob(wb);

    // Create a WriteBatcher to be used to write job information and pass it to the JobInformationRecorder
    WriteBatcher jobPersisterWb = dm.newWriteBatcher()
        .withBatchSize(batchSize)
        .withThreadCount(threadCount);
    // Create a QueryBatcher and pass it to JobInformationRecorder to record all the information about the job

    QueryBatcher qb = dm.newQueryBatcher(new StructuredQueryBuilder().directory(true, "/threadpool/"))
        .withBatchSize(batchSize)
        .withThreadCount(threadCount)
        .onUrisReady(batch -> System.out.println(batch.getJobBatchNumber() + " --- " + Arrays.toString(batch.getItems())))
        .onQueryFailure(batch -> System.out.println("Batch " + batch.getJobBatchNumber() + " failed " + batch.getMessage()));

    // Register the QueryBatcher with the JobInformationRecorder
    JobInformationRecorder queryPersister = new JobInformationRecorder(qb)
        // Pass in a WriteBatcher instance to write the job details along with the DatamovementManager
        // associated with the WriteBatcher instance.
        .withWriteBatcher(dm, jobPersisterWb)
        .withBatchInformation()
        .withProperty("color", "yellow")
        // Sets the collection in which the job information documents would be written
        .withCollection("queryPersisterExample");

    dm.startJob(qb);
    qb.awaitCompletion();
    dm.stopJob(qb);
  }
}
