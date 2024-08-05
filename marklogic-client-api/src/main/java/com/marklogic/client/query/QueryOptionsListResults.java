/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import java.util.HashMap;

/**
 * This interface supports access to the list of named query options provided by the server.
 */
public interface QueryOptionsListResults {
  /**
   * Returns a HashMap of the named query options from the server.
   *
   * The keys are the names of the query options, the values are the corresponding URIs on the server.
   *
   * @return The map of names to URIs.
   */
  HashMap<String, String> getValuesMap();
}


