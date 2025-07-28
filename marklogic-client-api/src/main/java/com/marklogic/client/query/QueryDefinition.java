/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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

