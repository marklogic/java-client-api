/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.Forest;

public class ForestImpl implements Forest {
  private String host;
  private String openReplicaHost;
  private String alternateHost;
  private String requestHost;
  private String databaseName;
  private String forestName;
  private String forestId;
  private boolean isUpdateable;
  private boolean isDeleteOnly;

  public ForestImpl(String host, String openReplicaHost, String requestHost, String alternateHost, String databaseName,
    String forestName, String forestId, boolean isUpdateable, boolean isDeleteOnly)
  {
    this.host = host;
    this.openReplicaHost = openReplicaHost;
    this.alternateHost = alternateHost;
    this.requestHost = requestHost;
    this.databaseName  = databaseName;
    this.forestName    = forestName;
    this.forestId      = forestId;
    this.isUpdateable  = isUpdateable;
    this.isDeleteOnly  = isDeleteOnly;
  }

  @Override
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @Override
  public String getOpenReplicaHost() {
    return openReplicaHost;
  }

  public void setOpenReplicaHost(String openReplicaHost) {
    this.openReplicaHost = openReplicaHost;
  }

  @Override
  public String getAlternateHost() {
    return alternateHost;
  }

  public void setAlternateHost(String alternateHost) {
    this.alternateHost = alternateHost;
  }

  @Override
  public String getRequestHost() {
    return requestHost;
  }

  public void setRequestHost(String requestHost) {
    this.requestHost = requestHost;
  }

  @Override
  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  @Override
  public String getForestName() {
    return forestName;
  }

  public void setForestName(String forestName) {
    this.forestName = forestName;
  }

  @Override
  public String getForestId() {
    return forestId;
  }

  public void setForestId(String forestId) {
    this.forestId = forestId;
  }

  @Override
  public boolean isUpdateable() {
    return isUpdateable;
  }

  public void setIsUpdateable(boolean isUpdateable) {
    this.isUpdateable = isUpdateable;
  }

  public boolean isDeleteOnly() {
    return isDeleteOnly;
  }

  public void setIsDeleteOnly(boolean isDeleteOnly) {
    this.isDeleteOnly = isDeleteOnly;
  }

  @Override
  public boolean equals(Object obj) {
    if ( obj == null ) return false;
    if ( ! (obj instanceof ForestImpl) ) return false;
    ForestImpl forestObj = (ForestImpl) obj;
    return getForestId().equals(forestObj.getForestId());
  }

  @Override
  public int hashCode() {
    return getForestId() != null ? getForestId().hashCode() : 0;
  }
}
