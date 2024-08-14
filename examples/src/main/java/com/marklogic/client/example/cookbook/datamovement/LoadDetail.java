/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
