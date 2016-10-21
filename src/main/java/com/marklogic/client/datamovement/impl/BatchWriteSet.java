/*
 * Copyright 2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.datamovement.impl;

import java.util.function.Consumer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.datamovement.WriteBatch;
import com.marklogic.client.datamovement.WriteEvent;
import com.marklogic.client.datamovement.impl.WriteHostBatcherImpl.TransactionInfo;

public class BatchWriteSet {
  private DocumentWriteSet writeSet;
  private long batchNumber;
  private long itemsSoFar;
  private DatabaseClient client;
  private TransactionInfo transactionInfo;
  private ServerTransform transform;
  private String temporalCollection;
  private Runnable onSuccess;
  private Consumer<Throwable> onFailure;
  private Runnable onBeforeWrite;

  public BatchWriteSet(DocumentWriteSet writeSet, DatabaseClient client,
    ServerTransform transform, String temporalCollection)
  {
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

  public TransactionInfo getTransactionInfo() {
    return transactionInfo;
  }

  public void setTransactionInfo(TransactionInfo transactionInfo) {
    this.transactionInfo = transactionInfo;
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
      .withJobBatchNumber(batchNumber)
      .withJobWritesSoFar(itemsSoFar);
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
