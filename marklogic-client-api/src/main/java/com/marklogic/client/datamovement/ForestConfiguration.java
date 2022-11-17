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

import com.marklogic.client.DatabaseClient;

import java.util.stream.Stream;

/**
 * A reflection of the forest configuration associated with the specified
 * database (or the default database for the specified port) in the MarkLogic
 * cluster.  This interface is purposely simple so it can be overriden for
 * advanced scenarios (see FilteredForestConfiguration for an example).
 */
public interface ForestConfiguration {
  /**
   * @return the Forest instances that should be used to talk to this database
   */
  Forest[] listForests();

  /**
   * A utility method to return the list of hosts a Batcher should use when talking
   * to this database.  The list is retrieved by calling getPreferredHost() on each
   * Forest.
   *
   * @return the list of hosts a Batcher should use
   */
  public default String[] getPreferredHosts() {
    return Stream.of(listForests()).map( (forest) -> forest.getPreferredHost()).distinct().toArray(String[]::new);
  }
}
