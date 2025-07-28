/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.extensions.ResourceServices;

// exposes protected method
public abstract class ResourceManagerImplementation {
  private ResourceServices services;
  protected ResourceManagerImplementation() {
    super();
  }
  final void init(ResourceServices services) {
    this.services = services;
  }
  protected ResourceServices getServices() {
    return services;
  }
}
