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
