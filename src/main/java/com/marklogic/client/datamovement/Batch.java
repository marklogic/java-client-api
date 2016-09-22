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

import java.util.Calendar;

public interface Batch<T> {
  T[] getItems();
  Calendar getTimestamp();
  long getServerTimestamp();
  long getJobBatchNumber();
  // TODO: should this be getJobItemsSoFar to apply to both QHB and WHB?
  long getJobResultsSoFar();
  // TODO: are these next two helpful for WHB?
  long getForestBatchNumber();
  long getForestResultsSoFar();
  // note that this is only populated by QHB
  Forest getForest();
  // TOOD: implement
  long getBytesMoved();
  // TOOD: implement
  JobTicket getJobTicket();
}
