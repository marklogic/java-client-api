/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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

  /** The request host name associated with the forest in the MarkLogic server.
   * The host name with which we have created the client - host name on the
   * request header.
   *
   * @return the host name of the request host in the MarkLogic server
   */
  String getRequestHost();

  /** Whether or not this forest is updateable.
   *
   * @return whether or not this forest is updateable
   */
  boolean isUpdateable();

  /**
   * Returns the host your application should talk to for this forest.  If
   * getAlternateHost() is not null, return it.  Otherwise, if
   * getOpenReplicaHost() is not null, return it. Otherwise, if
   * getRequestHost() is not null, return it. Otherwise, return getHost().
   *
   * @return the preferred host for this forest
   */
  public default String getPreferredHost() {
    if ( getAlternateHost() != null ) {
      return getAlternateHost();
    } else if ( getOpenReplicaHost() != null ) {
      return getOpenReplicaHost();
    } else if ( getRequestHost() != null ) {
      return getRequestHost();
    } else {
      return getHost();
    }
  }

  /**
   * <p>Enum containing the list of host types a forest can have.</p>
   *
   * <p>FOREST_HOST - host type indicating the host associated with
   * the forest in the MarkLogic server</p>
   *
   * <p>REQUEST_HOST - host type indicating the request host with which
   * we have created the client - host on the request header.</p>
   *
   * <p>ALTERNATE_HOST - host type indicating the alternate host of the forest -
   * the host of the e - node when this forest belongs to an d - node.</p>
   *
   * <p>OPEN_REPLICA_HOST - host type indicating the replica host associated
   * with the forest in the MarkLogic server</p>
   */
  enum HostType { FOREST_HOST, REQUEST_HOST, ALTERNATE_HOST, OPEN_REPLICA_HOST };

  /**
   * Returns the preferred host type for this forest.  If
   * getAlternateHost() is not null, return ALTERNATE_HOST. Otherwise, if
   * getOpenReplicaHost() is not null, return OPEN_REPLICA_HOST. Otherwise,
   * if getRequestHost() is  not null, return REQUEST_HOST.
   * Otherwise, return FOREST_HOST.
   *
   * @return the host type of the forest
   */
  public default HostType getPreferredHostType() {
    if ( getAlternateHost() != null ) {
      return HostType.ALTERNATE_HOST;
    } else if ( getOpenReplicaHost() != null ) {
      return HostType.OPEN_REPLICA_HOST;
    } else if ( getRequestHost() != null ) {
      return HostType.REQUEST_HOST;
    } else {
      return HostType.FOREST_HOST;
    }
  }
}
