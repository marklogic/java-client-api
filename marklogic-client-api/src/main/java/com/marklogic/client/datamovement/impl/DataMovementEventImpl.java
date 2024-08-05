/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

public class DataMovementEventImpl {
  private long bytesMoved;
  private long jobRecordNumber;
  private long batchRecordNumber;

  public DataMovementEventImpl() {}

  public long getBytesMoved() {
    return bytesMoved;
  }

  public DataMovementEventImpl withBytesMoved(long bytesMoved) {
    this.bytesMoved = bytesMoved;
    return this;
  }

  public long getJobRecordNumber() {
    return jobRecordNumber;
  }

  public DataMovementEventImpl withJobRecordNumber(long jobRecordNumber) {
    this.jobRecordNumber = jobRecordNumber;
    return  this;
  }

  public long getBatchRecordNumber() {
    return batchRecordNumber;
  }

  public DataMovementEventImpl withBatchRecordNumber(long batchRecordNumber) {
    this.batchRecordNumber = batchRecordNumber;
    return this;
  }
}
