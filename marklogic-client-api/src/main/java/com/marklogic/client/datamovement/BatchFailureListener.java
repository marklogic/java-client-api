/*
 * Copyright 2015-2019 MarkLogic Corporation
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

/** A generic interface for listeners which process failures on batches.
 * Currently only WriteFailureListener implements since QueryBatcher has no
 * batch when the query fails.
 */
public interface BatchFailureListener<T extends Batch<?>> {
  /** The method called when a failure occurs.
   *
   * @param batch the batch of items that failed processing
   * @param throwable the exception that caused the failure
   */
  void processFailure(T batch, Throwable throwable);
}
