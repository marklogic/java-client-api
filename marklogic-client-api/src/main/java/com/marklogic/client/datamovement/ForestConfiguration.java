/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import com.marklogic.client.DatabaseClient;

import java.util.stream.Stream;

/**
 * A reflection of the forest configuration associated with the specified
 * database (or the default database for the specified port) in the MarkLogic
 * cluster.  This interface is purposely simple so it can be overriden for
 * advanced scenarios (see FilteredForestConfiguration for an example).
 */
public interface ForestConfiguration {
  /**
   * @return the Forest instances that should be used to talk to this database
   */
  Forest[] listForests();

  /**
   * A utility method to return the list of hosts a Batcher should use when talking
   * to this database.  The list is retrieved by calling getPreferredHost() on each
   * Forest.
   *
   * @return the list of hosts a Batcher should use
   */
  public default String[] getPreferredHosts() {
    return Stream.of(listForests()).map( (forest) -> forest.getPreferredHost()).distinct().toArray(String[]::new);
  }
}
