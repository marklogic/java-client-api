/*
 * Copyright 2015 MarkLogic Corporation
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
package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.Forest;

public class ForestImpl implements Forest {
  private String host;
  private String openReplicaHost;
  private String alternateHost;
  private String databaseName;
  private String forestName;
  private String forestId;
  private boolean isUpdateable;
  private boolean isDeleteOnly;

  public ForestImpl(String host, String openReplicaHost, String alternateHost, String databaseName,
    String forestName, String forestId, boolean isUpdateable, boolean isDeleteOnly)
  {
    this.host = host;
    this.openReplicaHost = openReplicaHost;
    this.alternateHost = alternateHost;
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
