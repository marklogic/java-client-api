/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import java.util.HashMap;

/**
 * A ValuesListResults represents the results of a values query.
 */
public interface ValuesListResults {
  /**
   * Returns the map of value results.
   * @return The map.
   */
  HashMap<String, String> getValuesMap();
}


