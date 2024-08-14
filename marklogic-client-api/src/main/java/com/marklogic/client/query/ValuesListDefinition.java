/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

/**
 * A ValuesListDefinition is the base type for a values or tuples list.
 */
public interface ValuesListDefinition {
  /**
   * Returns the name of the options node associated with this query.
   * @return The name of the options node.
   */
  String getOptionsName();

  /**
   * Set the name of the options node to be used for this query.
   * @param optname The name of the options node.
   */
  void setOptionsName(String optname);
}

