/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.query.StringQueryDefinition;

public class StringQueryDefinitionImpl extends AbstractQueryDefinition implements StringQueryDefinition {
  protected String criteria = null;

  public StringQueryDefinitionImpl(String uri) {
    optionsUri = uri;
  }

  @Override
  public String getCriteria() {
    return criteria;
  }

  @Override
  public void setCriteria(String criteria) {
    if (criteria.length() == 0) {
      throw new IllegalArgumentException("Criteria cannot be an empty string.");
    }
    this.criteria = criteria;
  }

  @Override
  public StringQueryDefinition withCriteria(String criteria) {
    setCriteria(criteria);
    return this;
  }

  @Override
  public boolean canSerializeQueryAsJSON() {
    return getOptionsName() == null;
  }
}
