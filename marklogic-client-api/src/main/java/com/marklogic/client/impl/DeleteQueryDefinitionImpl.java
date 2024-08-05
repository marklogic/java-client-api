/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.query.DeleteQueryDefinition;

public class DeleteQueryDefinitionImpl extends AbstractQueryDefinition implements DeleteQueryDefinition {
  public DeleteQueryDefinitionImpl() {
  }

  @Override
  public String getOptionsName() {
    return null;
  }

  @Override
  public void setOptionsName(String uri) {
    throw new UnsupportedOperationException("Options name has no meaning on a DeleteQueryDefinition");
  }
  @Override
  public ServerTransform getResponseTransform() {
    throw new UnsupportedOperationException("A server transform has no meaning on a DeleteQueryDefinition");
  }
  @Override
  public void setResponseTransform(ServerTransform transform) {
    throw new UnsupportedOperationException("A server transform has no meaning on a DeleteQueryDefinition");
  }

  @Override
  public boolean canSerializeQueryAsJSON() {
    return true;
  }
}
