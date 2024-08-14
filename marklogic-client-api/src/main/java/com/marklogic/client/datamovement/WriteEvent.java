/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

/**
 * Each WriteBatch is composed of many WriteEvents, each of which represents all the information about a single document which was written to the server.
 */
public interface WriteEvent {
  /**
   * The uri written to the server.
   *
   * @return the uri
   */
  String getTargetUri();

  /**
   * The content written to the server.
   *
   * @return the content
   */
  AbstractWriteHandle getContent();

  /**
   * The metadata written to the server.
   *
   * @return the metadata
   */
  DocumentMetadataWriteHandle getMetadata();

  /**
   * Within the context of the job, the numric position of this document.
   *
   * @return the position in this job
   */
  long getJobRecordNumber();

  /**
   * Within the context of the batch, the numeric position of this document.
   *
   * @return the position in this batch
   */
  long getBatchRecordNumber();
}
