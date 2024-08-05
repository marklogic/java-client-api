/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * A ValuesListBuilder parses list of value results.
 *
 * The values list builder class is public to satisfy constraints of JAXB.
 * It is of no consequence to users of this API.
 */
public final class ValuesListBuilder {
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(namespace = ValuesList.VALUES_LIST_NS, name = "values-list")

  public static final class ValuesList {
    public static final String VALUES_LIST_NS = "http://marklogic.com/rest-api";

    @XmlElement(namespace = ValuesList.VALUES_LIST_NS, name = "values")
    private List<Values> values;

    public ValuesList() {
      values = new ArrayList<>();
    }

    public HashMap<String, String> getValuesMap() {
      HashMap<String,String> map = new HashMap<>();
      for (Values value : values) {
        map.put(value.getName(), value.getUri());
      }
      return map;
    }
  }

  private static final class Values {
    @XmlElement(namespace = ValuesList.VALUES_LIST_NS, name = "name")
    String name;

    @XmlElement(namespace = ValuesList.VALUES_LIST_NS, name = "uri")
    String uri;

    public String getName() {
      return name;
    }

    public String getUri() {
      return uri;
    }
  }
}
