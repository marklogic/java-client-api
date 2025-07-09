/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
