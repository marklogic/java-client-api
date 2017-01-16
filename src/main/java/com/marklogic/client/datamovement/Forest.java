/*
 * Copyright 2015-2017 MarkLogic Corporation
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

/**
 * Some details about a MarkLogic forest.
 */
public interface Forest {
  /** The id for the forest as provided by the MarkLogic server.
   *
   * @return the id for the forest in MarkLogic server
   */
  String getForestId();

  /** The name of the forest as provided by the MarkLogic server.
   *
   * @return the name of the forest in MarkLogic server
   */
  String getForestName();

  /** The database name associated with the forest in the MarkLogic server.
   *
   * @return the database name of the forest in the MarkLogic server
   */
  String getDatabaseName();

  /** The host name associated with the forest in the MarkLogic server.
   *
   * @return the host name associated with the forest in the MarkLogic server
   */
  String getHost();

  /** The replica host name associated with the forest in the MarkLogic server.
   * If this is not null this forest has failed over to a replica.
   *
   * @return the host name for the replica forest in the MarkLogic server
   */
  String getOpenReplicaHost();

  /** The alternate host name associated with the forest in the MarkLogic server.
   * If this is not null this forest cannot be accessed directly and should
   * instead be accessed through this alternate host.  One reason a forest
   * could not be accessed directly is if the forest is on a d-node (a
   * MarkLogic instance with no app servers).
   *
   * @return the host name of the alternat host in the MarkLogic server
   */
  String getAlternateHost();

  /** Whether or not this forest is updateable.
   *
   * @return whether or not this forest is updateable
   */
  boolean isUpdateable();

  /**
   * Returns the host your application should talk to for this forest.  If
   * getAlternateHost() is not null, return it.  Otherwise, if
   * getOpenReplicaHost() is not null, return it.  Otherwise, return getHost().
   *
   * @return the preferred host for this forest
   */
  public default String getPreferredHost() {
    if ( getAlternateHost() != null ) {
      return getAlternateHost();
    } else if ( getOpenReplicaHost() != null ) {
      return getOpenReplicaHost();
    } else {
      return getHost();
    }
  }
}
