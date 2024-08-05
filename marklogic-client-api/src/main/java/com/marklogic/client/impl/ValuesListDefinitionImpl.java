/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.query.ValuesListDefinition;

public class ValuesListDefinitionImpl implements ValuesListDefinition {
  private String options = null;

  public ValuesListDefinitionImpl(String optionsName) {
    options = optionsName;
  }

  @Override
  public String getOptionsName() {
    return options;
  }

  @Override
  public void setOptionsName(String optname) {
    options = optname;
  }
}
