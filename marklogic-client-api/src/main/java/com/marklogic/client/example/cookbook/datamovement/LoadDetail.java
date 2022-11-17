/*
 * Copyright (c) 2022 MarkLogic Corporation
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
package com.marklogic.client.example.cookbook.datamovement;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadDetail {
  private int hashCode;
  private String jobName;

  public LoadDetail() {}

  public LoadDetail(String jobName, int hashCode) {
    this.jobName = jobName;
    this.hashCode = hashCode;
  }

  public int getHashCode() {
    return hashCode;
  }

  public void setHashCode(int hashCode) {
    this.hashCode = hashCode;
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public static String makeUri(String originalUri) {
    if ( originalUri == null ) throw new IllegalArgumentException("originalUri cannot be null");
    if ( originalUri.startsWith("/") ) {
      return "/loadDetails" + originalUri + ".ld.json";
    } else {
      return "/loadDetails/" + originalUri + ".ld.json";
    }
  }

  public static String[] makeUris(String[] originalUris) {
    return Stream.of(originalUris).map(uri->makeUri(uri)).toArray(String[]::new);
  }
}
