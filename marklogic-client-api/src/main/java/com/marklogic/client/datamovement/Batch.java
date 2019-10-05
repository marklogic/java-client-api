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

import java.util.Calendar;

import com.marklogic.client.DatabaseClient;

/** A group of items (generally documents or uris) and context representing a
 * completed action in a datamovement job.
 */
public interface Batch<T> extends BatchEvent {
  /** The documents read by WriteBatcher or the uris retrieved by QueryBatcher.
   *
   * @return the items in this batch
   */
  T[] getItems();
}
