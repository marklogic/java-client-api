/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is an implementation class used to read the list of named query options from
 * the server. It may be moved into the .impl package in a future release.
 */
public final class QueryOptionsListBuilder {
  /**
   * This is an implementation class that lists the named query options from
   * the server. It may be moved into the .impl package in a future release.
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(namespace = OptionsList.OPTIONS_LIST_NS, name = "query-options")
  public static final class OptionsList {
    public static final String OPTIONS_LIST_NS = "http://marklogic.com/rest-api";

    @XmlElement(namespace = OptionsList.OPTIONS_LIST_NS, name = "options")
    private List<Options> options;

    public OptionsList() {
      options = new ArrayList<>();
    }

    public HashMap<String, String> getOptionsMap() {
      HashMap<String,String> map = new HashMap<>();
      for (Options opt : options) {
        map.put(opt.getName(), opt.getUri());
      }
      return map;
    }
  }

  private static final class Options {
    @XmlElement(namespace = OptionsList.OPTIONS_LIST_NS, name = "name")
    String name;

    @XmlElement(namespace = OptionsList.OPTIONS_LIST_NS, name = "uri")
    String uri;

    public String getName() {
      return name;
    }

    public String getUri() {
      return uri;
    }
  }
}
