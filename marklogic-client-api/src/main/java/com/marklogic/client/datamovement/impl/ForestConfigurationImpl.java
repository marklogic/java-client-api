/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;

public class ForestConfigurationImpl implements ForestConfiguration {
  private Forest[] forests;

  public ForestConfigurationImpl(Forest[] forests) {
    this.forests = forests;
  }

  @Override
  public Forest[] listForests() {
    return forests;
  }
}
