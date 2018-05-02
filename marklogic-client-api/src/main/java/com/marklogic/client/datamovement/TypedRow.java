/*
 * Copyright 2018 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
