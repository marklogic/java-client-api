/*
 * Copyright (c) 2022 MarkLogic Corporation
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
