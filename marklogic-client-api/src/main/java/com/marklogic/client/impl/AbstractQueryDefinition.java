/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.HashSet;

import com.marklogic.client.query.QueryDefinition;
import java.util.Set;

public abstract class AbstractQueryDefinition extends AbstractSearchQueryDefinition implements QueryDefinition {
  private Set<String> collections = new HashSet<>();
  private String          directory   = null;

  @Override
  public String[] getCollections() {
    return collections.toArray(new String[0]);
  }
  @Override
  public void setCollections(String... collections) {
    this.collections.clear();

    for (String collection : collections) {
      this.collections.add(collection);
    }
  }

  @Override
  public String getDirectory() {
    return directory;
  }
  @Override
  public void setDirectory(String directory) {
    this.directory = directory;
  }

}
