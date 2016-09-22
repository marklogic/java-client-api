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

public class DataMovementEventImpl<T extends DataMovementEventImpl<T>> {
  private String targetUri;
  private long bytesMoved;
  private long jobRecordNumber;
  private long batchRecordNumber;

  public DataMovementEventImpl() {}

  public String getTargetUri() {
    return targetUri;
  }

  @SuppressWarnings("unchecked")
  public T withTargetUri(String targetUri) {
    this.targetUri = targetUri;
    return (T) this;
  }

  public long getBytesMoved() {
    return bytesMoved;
  }

  @SuppressWarnings("unchecked")
  public T withBytesMoved(long bytesMoved) {
    this.bytesMoved = bytesMoved;
    return (T) this;
  }

  public long getJobRecordNumber() {
    return jobRecordNumber;
  }

  @SuppressWarnings("unchecked")
  public T withJobRecordNumber(long jobRecordNumber) {
    this.jobRecordNumber = jobRecordNumber;
    return (T)  this;
  }

  public long getBatchRecordNumber() {
    return batchRecordNumber;
  }

  @SuppressWarnings("unchecked")
  public T withBatchRecordNumber(long batchRecordNumber) {
    this.batchRecordNumber = batchRecordNumber;
    return (T) this;
  }
}
