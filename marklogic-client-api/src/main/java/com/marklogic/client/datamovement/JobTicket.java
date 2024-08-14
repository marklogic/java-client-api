/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/**
 * JobTicket is used to uniquely identify a job. This is given as input
 * when we want to generate a JobReport. The JobReport given the JobTicket
 * would generate the report for that particular job
 *
 */
public interface JobTicket {
  /**
   * @return the UUID which uniquely identifies the Job
   */
  String getJobId();
  /**
   * @return the type of job which the ticket is identifying. It might be a
   * {@link WriteBatcher} or {@link QueryBatcher}
   */
  JobType getJobType();

  /**
   * @return the Batcher instance associated with this JobTicket. This is useful
   *         to start/stop the job or update the forest configuration.
   */
  Batcher getBatcher();

  enum JobType { WRITE_BATCHER, QUERY_BATCHER, ROW_BATCHER; };
}
