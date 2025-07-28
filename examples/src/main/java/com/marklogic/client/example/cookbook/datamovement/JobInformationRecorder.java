package com.marklogic.client.example.cookbook.datamovement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JobInformationRecorder {
  private DatabaseClient client;
  private DataMovementManager dataMovementManager;
  private WriteBatcher writeBatcher;
  private String collection;
  private Batcher sourceBatcher;
  private ObjectMapper mapper;
  private String uriPrefix;
  private Runnable jobStartTracker;
  private Runnable jobFinishTracker;
  private JSONDocumentManager documentManager;
  private Map<String, String> properties;
  private String successCollection = "success";
  private String failureCollection = "failure";
  DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

  /**
   * JobInformationRecorder is used to record the events of a QueryBatcher or WriteBatcher
   * This, by default, records start time, end time along with any properties set to the
   * object via {@link JobInformationRecorder#withProperty(String, String)} or you can pass
   * in a Map of properties via {@link JobInformationRecorder#withProperty(Map)}.
   *
   * JobInformationRecorder takes a Batcher (WriteBatcher/QueryBatcher) as an argument for
   * which job information needs to be recorded. You need to either pass in a WriteBatcher to write
   * the job information using {@link JobInformationRecorder#withWriteBatcher(DataMovementManager, WriteBatcher)}
   * or set a DatabaseClient using {@link JobInformationRecorder#withDatabaseClient(DatabaseClient)}
   * in order to create a default WriteBatcher.
   *
   * If batch information needs to be persisted along with the other job information, you need to
   * create the object with {@link JobInformationRecorder#withBatchInformation()} and this signals
   * to record batch information as well.
   *
   * All the job information documents would be written to a collection which can be set using
   * {@link JobInformationRecorder#withCollection(String)}. For example, if we set the collection to be
   * 'example', all the documents would be written to 'example' collection and the main file containing
   * job related information would be in /example/example.json and the job information documents would
   * be in /example/{batchnumber}.json where {batchnumber} is the batch's unique number. If nothing is
   * set, it would use the jobId of the Batcher as the default collection.
   *
   * For example:
   *
   * <pre>{@code
   *     DataMovementManager moveMgr = client.newDataMovementManager();
   *     QueryBatcher queryBatcher = moveMgr.newQueryBatcher()
   *     JobInformationRecorder writePersister = new JobInformationRecorder(queryBatcher)
   *          .withBatchInformation()
   *          .withProperty("key1", "value1")
   *          .withProperty("key2", "value2")
   *          .withDatabaseClient(client)
   *          .withCollection("example");
   *
   * }</pre>
   *
   * writePersister would create a default WriteBatcher to log all the information of the queryBatcher
   * job in "example" collection along with the job's start time, end time and properties mentioned above.
   * Since withBatchInformation() is called, it would record each batch information as well.
   *
   * @param batcher The WriteBatcher/QueryBatcher for which we need to record the job information
   */

  JobInformationRecorder(Batcher batcher) {
    this.sourceBatcher = batcher;
    properties = new HashMap<>();
    this.mapper = new ObjectMapper();

    jobStartTracker = () -> {
      while (!sourceBatcher.isStarted()) ;
      if(dataMovementManager == null) {
        if(client == null) {
          throw new IllegalStateException("No valid DataMovementManager found. Need to set a valid WriteBatcher instance " +
              "using withWriteBatcher() method or set a valid DatabaseClient instance using withDatabaseClient() method " +
              "which would be used to create a default WriteBatcher using the client's DatamovementManager");
        }
        dataMovementManager = client.newDataMovementManager();
        writeBatcher = dataMovementManager.newWriteBatcher();
      }
      documentManager = client.newJSONDocumentManager();
      dataMovementManager.startJob(writeBatcher);
      if (collection == null) collection = batcher.getJobId();
      uriPrefix = "/" + collection + "/";
      ObjectNode sourceNode = mapper.createObjectNode();
      sourceNode.put("Job Start Time", dateFormat.format(sourceBatcher.getJobStartTime().getTime()));
      DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
      metadataHandle.getCollections().add(collection);
      documentManager.write(uriPrefix + collection + ".json", metadataHandle, new JacksonHandle(sourceNode));
    };

    jobFinishTracker = () -> {
      while (!sourceBatcher.isStopped()) ;
      finishPersisting();
    };
    Thread jobStartTrackerThread = new Thread(jobStartTracker);
    jobStartTrackerThread.start();
    Thread jobFinishTrackerThread = new Thread(jobFinishTracker);
    jobFinishTrackerThread.start();
  }

  /**
   * Sets the WriteBatcher object which should be used for writing the job information. This
   * overrides the internal WriteBatcher object created by default.
   *
   * @param dataMovementManager The manager associated with the write batcher
   * @param writeBatcher The WriteBatcher object with which we should write the job information.
   * @return this object for chaining
   */
  public JobInformationRecorder withWriteBatcher(DataMovementManager dataMovementManager, WriteBatcher writeBatcher) {
    if(sourceBatcher.isStarted()) throw new IllegalStateException("Configuration cannot be changed after startJob has been called");
    this.writeBatcher = writeBatcher;
    this.dataMovementManager = dataMovementManager;
    this.client = writeBatcher.getPrimaryClient();
    this.dataMovementManager.startJob(this.writeBatcher);
    return this;
  }

  /**
   * Sets the DatabaseClient from which the internal WriteBatcher object is created if an external
   * WriteBatcher object is not passed.
   *
   * @param client The DatabaseClient object for creating internal WriteBatcher
   * @return this object for chaining
   */
  public JobInformationRecorder withDatabaseClient(DatabaseClient client) {
    this.client = client;
    return this;
  }
  /**
   * Sets the collection in which the job information documents would be written. This
   * overrides the collection set by default - which is the jobId.
   *
   * @param collection The collection in which the job information documents should be written
   * @return this object for chaining
   */
  public JobInformationRecorder withCollection(String collection) {
    if(sourceBatcher.isStarted()) throw new IllegalStateException("Configuration cannot be changed after startJob has been called");
    this.collection = collection;
    return this;
  }

  private void finishPersisting() {
    JacksonHandle handle = new JacksonHandle();
    documentManager.read(uriPrefix + collection + ".json", handle);
    ObjectNode document = (ObjectNode) handle.get();
    if (properties.size() > 0) {
      ObjectNode mapNode = mapper.createObjectNode();
      document.set("properties", mapNode);
      for (Map.Entry<String, String> entry : properties.entrySet()) {
        mapNode.put(entry.getKey(), entry.getValue());
      }
    }
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    document.put("Job End Time", dateFormat.format(sourceBatcher.getJobEndTime().getTime()));
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    metadataHandle.getCollections().add(collection);
    documentManager.write(uriPrefix + collection + ".json", metadataHandle, new JacksonHandle(document));
    writeBatcher.flushAndWait();
    dataMovementManager.stopJob(writeBatcher);
  }

  /**
   * Indicates the JobInformationRecorder to persist batch information. For each batch, the success/failure
   * information along with a few metadata of the batch would be written to the database.
   *
   * @return this object for chaining
   */
  public JobInformationRecorder withBatchInformation() {
    if(sourceBatcher.isStarted()) throw new IllegalStateException("Configuration cannot be changed after startJob has been called");
    if(sourceBatcher instanceof QueryBatcher) {
      ( (QueryBatcher) sourceBatcher ).onUrisReady(new addQueryBatchSuccessInformationListener());
      ( (QueryBatcher) sourceBatcher ).onQueryFailure(new addQueryBatchFailureInformationListener());
    } else if(sourceBatcher instanceof WriteBatcher) {
      ( (WriteBatcher) sourceBatcher ).onBatchSuccess(new addWriteBatchSuccessInformationListener());
      ( (WriteBatcher) sourceBatcher ).onBatchFailure(new addWriteBatchFailureInformationListener());
    }
    return this;
  }

  /**
   * Sets the key value pair to the job information. This would be persisted along with the other job
   * information like start time, end time etc
   *
   * @param key  key of the key value pair
   * @param value  value of the key value pair
   * @return this object for chaining
   */
  public JobInformationRecorder withProperty(String key, String value) {
    if(sourceBatcher.isStarted()) throw new IllegalStateException("Configuration cannot be changed after startJob has been called");
    properties.put(key, value);
    return this;
  }

  /**
   * Sets the map of key value pairs to the job information. This would be persisted along with
   * the other job information like start time, end time etc. This would clear any key value property
   * set before and initialize the properties with this map.
   *
   * @param properties map of Key value pairs
   * @return this object for chaining
   */
  public JobInformationRecorder withProperty(Map<String, String> properties) {
    if(sourceBatcher.isStarted()) throw new IllegalStateException("Configuration cannot be changed after startJob has been called");
    this.properties.clear();
    this.properties.putAll(properties);
    return this;
  }

  public class addQueryBatchSuccessInformationListener implements QueryBatchListener {
    @Override
    public void processEvent(QueryBatch batch) {
      String uri = uriPrefix + "query/" + batch.getJobBatchNumber() + ".json";
      ObjectNode sourceNode = mapper.createObjectNode();
      DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      sourceNode.put("Batch Success/Failure Information", "Success");
      sourceNode.put("Query Batch number", batch.getJobBatchNumber());
      sourceNode.put("Query batch results so far", batch.getJobResultsSoFar());
      sourceNode.put("Forest", batch.getForest().getForestName());
      sourceNode.put("Forest Batch", batch.getForestBatchNumber());
      sourceNode.put("Forest results so far", batch.getForestResultsSoFar());
      sourceNode.put("Time at which query was run", dateFormat.format(new Date(batch.getServerTimestamp())));
      ArrayNode itemArray = mapper.createArrayNode();
      sourceNode.set("Batch Items", itemArray);
      for(String event : batch.getItems()) {
        itemArray.add(event);
      }
      DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
      metadataHandle.getCollections().add(collection);
      metadataHandle.getCollections().add(successCollection);
      writeBatcher.add(uri, metadataHandle, new JacksonHandle(sourceNode));
    }
  }

  public class addQueryBatchFailureInformationListener implements QueryFailureListener {
    @Override
    public void processFailure(QueryBatchException failure) {
      String uri = uriPrefix + "query/" + failure.getJobBatchNumber() + ".json";
      ObjectNode sourceNode = mapper.createObjectNode();
      sourceNode.put("Batch Success/Failure Information", "Failure");
      sourceNode.put("Failure Message", failure.getMessage());
      sourceNode.put("Query Batch number", failure.getJobBatchNumber());
      sourceNode.put("Query batch results so far", failure.getJobResultsSoFar());
      sourceNode.put("Forest", failure.getForest().getForestName());
      sourceNode.put("Forest Batch", failure.getForestBatchNumber());
      sourceNode.put("Forest results so far", failure.getForestResultsSoFar());
      DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
      metadataHandle.getCollections().add(collection);
      metadataHandle.getCollections().add(failureCollection);
      writeBatcher.add(uri, metadataHandle, new JacksonHandle(sourceNode));
    }
  }

  public class addWriteBatchSuccessInformationListener implements WriteBatchListener {
    @Override
    public void processEvent(WriteBatch batch) {
      String uri = uriPrefix + "write/" +batch.getJobBatchNumber() + ".json";
      DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      ObjectNode sourceNode = mapper.createObjectNode();
      sourceNode.put("Batch Success/Failure Information", "Success");
      sourceNode.put("Write Batch number", batch.getJobBatchNumber());
      sourceNode.put("Job Writes so far", batch.getJobWritesSoFar());
      sourceNode.put("Time at which the write batch completed", dateFormat.format(batch.getTimestamp().getTime()));
      ArrayNode itemArray = mapper.createArrayNode();
      sourceNode.set("Batch Items", itemArray);
      for(WriteEvent event : batch.getItems()) {
        itemArray.add(event.getTargetUri());
      }
      DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
      metadataHandle.getCollections().add(collection);
      metadataHandle.getCollections().add(successCollection);
      writeBatcher.add(uri, metadataHandle, new JacksonHandle(sourceNode));
    }
  }

  public class addWriteBatchFailureInformationListener implements WriteFailureListener {
    @Override
    public void processFailure(WriteBatch batch, Throwable failure) {
      String uri = uriPrefix + "write/" + batch.getJobBatchNumber() + ".json";
      ObjectNode sourceNode = mapper.createObjectNode();
      sourceNode.put("Batch Success/Failure Information", "Failure");
      sourceNode.put("Failure Message", failure.getMessage());
      sourceNode.put("Write Batch number", batch.getJobBatchNumber());
      sourceNode.put("Job Writes so far", batch.getJobWritesSoFar());
      ArrayNode itemArray = mapper.createArrayNode();
      sourceNode.set("Batch Items", itemArray);
      for(WriteEvent event : batch.getItems()) {
        itemArray.add(event.getTargetUri());
      }
      DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
      metadataHandle.getCollections().add(collection);
      metadataHandle.getCollections().add(failureCollection);
      writeBatcher.add(uri, metadataHandle, new JacksonHandle(sourceNode));
    }
  }
}