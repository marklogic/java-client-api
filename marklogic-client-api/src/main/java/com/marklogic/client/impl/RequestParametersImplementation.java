/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.AbstractMultivaluedMap;
import javax.ws.rs.core.MultivaluedMap;

public abstract class RequestParametersImplementation {
  private MultivaluedMap<String, String> map =
    new AbstractMultivaluedMap<String,String>(new ConcurrentHashMap<>()) {};

  protected RequestParametersImplementation() {
    super();
  }

  protected Map<String,List<String>> getMap() {
    return map;
  }
}
