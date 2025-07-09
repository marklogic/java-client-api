/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.util;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement
public class Refers {
  public String              name  = "refers";
  public Referred            child = null;
  public Map<String,Integer> map   = null;
  public List<String>        list  = null;
}
