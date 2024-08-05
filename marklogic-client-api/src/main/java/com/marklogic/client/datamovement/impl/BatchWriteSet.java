/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import java.util.function.Consumer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.datamovement.WriteBatch;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.WriteEvent;

public class BatchWriteSet {
  private WriteBatcher batcher;
  private DocumentWriteSet writeSet;
  private long batchNumber;
  private long itemsSoFar;
  private DatabaseClient client;
  private ServerTransform transform;
  private String temporalCollection;
  private Runnable onSuccess;
  private Consumer<Throwable> onFailure;
  private Runnable onBeforeWrite;

  public BatchWriteSet(WriteBatcher batcher, DocumentWriteSet writeSet, DatabaseClient client,
    ServerTransform transform, String temporalCollection)
  {
    this.batcher = batcher;
    this.writeSet = writeSet;
    this.client = client;
    this.transform = transform;
    this.temporalCollection = temporalCollection;
  }

  public DocumentWriteSet getWriteSet() {
    return writeSet;
  }

  public void setWriteSet(DocumentWriteSet writeSet) {
    this.writeSet = writeSet;
  }

  public long getBatchNumber() {
    return batchNumber;
  }

  public void setBatchNumber(long batchNumber) {
    this.batchNumber = batchNumber;
  }

  public void setItemsSoFar(long itemsSoFar) {
    this.itemsSoFar = itemsSoFar;
  }

  public DatabaseClient getClient() {
    return client;
  }

  public void setClient(DatabaseClient client) {
    this.client = client;
  }

  public ServerTransform getTransform() {
    return transform;
  }

  public void setTransform(ServerTransform transform) {
    this.transform = transform;
  }

  public String getTemporalCollection() {
    return temporalCollection;
  }

  public void setTemporalCollection(String temporalCollection) {
    this.temporalCollection = temporalCollection;
  }

  public Runnable getOnSuccess() {
    return onSuccess;
  }

  public void onSuccess(Runnable onSuccess) {
    this.onSuccess = onSuccess;
  }

  public Consumer<Throwable> getOnFailure() {
    return onFailure;
  }

  public void onFailure(Consumer<Throwable>  onFailure) {
    this.onFailure = onFailure;
  }

  public Runnable getOnBeforeWrite() {
    return onBeforeWrite;
  }

  public void onBeforeWrite(Runnable onBeforeWrite) {
    this.onBeforeWrite = onBeforeWrite;
  }

  public WriteBatch getBatchOfWriteEvents() {
    WriteBatchImpl batch = new WriteBatchImpl()
      .withBatcher(batcher)
      .withClient(client)
      .withJobBatchNumber(batchNumber)
      .withJobWritesSoFar(itemsSoFar)
      .withJobTicket(batcher.getJobTicket());
    WriteEvent[] writeEvents = getWriteSet().stream()
      .map(writeOperation ->
        new WriteEventImpl()
          .withTargetUri(writeOperation.getUri())
          .withContent(writeOperation.getContent())
          .withMetadata(writeOperation.getMetadata())
      )
      .toArray(WriteEventImpl[]::new);
    batch.withItems(writeEvents);
    return batch;
  }
}
