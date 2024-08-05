/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import java.util.LinkedHashMap;
import com.marklogic.client.type.XsAnyAtomicTypeVal;

public class TypedRow extends LinkedHashMap<String, XsAnyAtomicTypeVal> {
  String uri;
  String rowNum;

  public TypedRow(String uri, String rowNum) {
    this.uri = uri;
    this.rowNum = rowNum;
  }

  public String getUri() {
    return uri;
  }

  public long getRowNum() {
    return new Long(rowNum).longValue();
  }

  public XsAnyAtomicTypeVal put(String name, XsAnyAtomicTypeVal val) {
    return super.put(name, val);
  }
}
