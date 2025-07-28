/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.ValueQueryDefinition;
import com.marklogic.client.pojo.PojoQueryDefinition;
import com.marklogic.client.io.Format;

public interface CombinedQueryDefinition
  extends QueryDefinition, ValueQueryDefinition, PojoQueryDefinition
{
  /**
   * Returns the combined query definition as a serialized XML or JSON string.
   *
   * @return The serialized definition.
   */
  String serialize();

  Format getFormat();
}


