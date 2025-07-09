/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import java.util.Calendar;

/**
 * A batch of documents written successfully.
 */
public interface WriteBatch extends Batch<WriteEvent> {
  /**
   * @return the WriteBatcher job which wrote this batch.
   */
  WriteBatcher getBatcher();

  /**
   * In the context of this job, the number of documents written so far.
   *
   * @return the number of writes by this job
   */
  long getJobWritesSoFar();
}
