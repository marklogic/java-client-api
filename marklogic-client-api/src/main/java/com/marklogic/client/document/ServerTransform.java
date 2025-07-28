/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.marklogic.client.util.RequestParameters;

/**
 * ServerTransform specifies the invocation of a transform on the server
 * including both the name of the transform and the parameters passed
 * to the transform.
 */
public class ServerTransform extends RequestParameters {
  private String name;

  /**
   * Specifies invocation of the named transform on the server.
   * @param name	the transform installed on the server
   */
  public ServerTransform(String name) {
    super();
    if ( name == null ) throw new IllegalArgumentException("Transform name cannot be null");
    this.name = name;
  }

  /**
   * Gets the name of the invoked transform.
   * @return	the name of the transform installed on the server
   */
  public String getName() {
    return name;
  }

  /**
   * Appends a value to the list for a parameter and returns this instance for
   * method chaining.
   * @param name	the parameter
   * @param value	the value to add to the list
   * @return this instance (for method chaining)
   */
  public ServerTransform addParameter(String name, String value) {
    add(name, value);
    return this;
  }

  /**
   * Merges the transform and its parameters with other parameters
   * of the request.
   *
   * Ordinarily, and application does not need to call this method.
   * @param currentParams	the other parameters
   * @return	the union of the other parameters and the transform parameters
   */
  public Map<String,List<String>> merge(Map<String,List<String>> currentParams) {
    Map<String,List<String>> params = (currentParams != null) ?
      currentParams : new RequestParameters();

    params.put("transform", Arrays.asList(getName()));

    for (Map.Entry<String, List<String>> entry: entrySet()) {
      params.put("trans:"+entry.getKey(), entry.getValue());
    }

    return params;
  }
}
