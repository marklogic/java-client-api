/*
 * Copyright (c) 2019 MarkLogic Corporation
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
package com.marklogic.client.query;

/**
 * A QueryDefinition represents the common parts of most kinds of searches(except Cts query) that can be performed.
 */
public interface QueryDefinition extends SearchQueryDefinition {

  /**
   * Returns the array of collections to which the query is limited.
   * @return The array of collection URIs.
   */
  String[] getCollections();

  /**
   * Sets the list of collections to which the query should be limited.
   *
   * @param collections The list of collection URIs.
   */
  void setCollections(String... collections);

  /**
   * Returns the directory to which the query is limited.
   * @return The directory URI.
   */
  String getDirectory();

  /**
   * Sets the directory to which the query should be limited.
   * @param directory The directory URI.
   */
  void setDirectory(String directory);
}

