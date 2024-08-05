/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
