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

  enum JobType { WRITE_BATCHER, QUERY_BATCHER };
}
