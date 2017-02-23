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

import com.marklogic.client.datamovement.impl.ForestImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A utility class for wrapping a ForestConfiguration retrieved from {@link
 * DataMovementManager#readForestConfig}.  This class does not modify the
 * ForestConfiguration (of course, ForestConfiguration is immutable) but
 * instead provides a modified ForestConfiguration when listForests is called.
 * Passing this (or any ForestConfiguration) to {@link
 * Batcher#withForestConfig} will reset the forests seen and used by the
 * Batcher. This class is used by HostAvailabilityListener to black-list
 * unavailable hosts (such as in the case of a failover).
 *
 * Since this does frequent comparision of host names and they compare case
 * insensitive, this converts all host names to lower case.
 *
 * As with the provided listeners, this utility will not meet the needs of all
 * applications but the [source code][] for it should serve as helpful sample
 * code so you can write your own custom ForestConfiguration wrapper.
 *
 * [source code]: https://github.com/marklogic/java-client-api/blob/develop/src/main/java/com/marklogic/client/datamovement/FilteredForestConfiguration.java
 */
public class FilteredForestConfiguration implements ForestConfiguration {
  ForestConfiguration wrappedForestConfig;
  Set<String> blackList = new HashSet<>();
  Set<String> whiteList = new HashSet<>();
  Map<String,String> renames = new HashMap<>();

  /**
   * Initialize with a ForestConfiguration, usually from {@link DataMovementManager#readForestConfig}.
   *
   * @param forestConfig the ForestConfiguration to wrap
   */
  public FilteredForestConfiguration(ForestConfiguration forestConfig) {
    this.wrappedForestConfig = forestConfig;
  }

  /** Must be called after configuration methods (withBlackList, withWhiteList, withRenamedHost).
   */
  public Forest[] listForests() {
    // apply renames to all fields containing host names
    Forest[] renamedForests = Stream.of(wrappedForestConfig.listForests()).map( forest -> {
      String openReplicaHost = forest.getOpenReplicaHost();
      if ( openReplicaHost != null ) openReplicaHost = openReplicaHost.toLowerCase();
      if ( renames.containsKey(openReplicaHost) ) {
        openReplicaHost = renames.get(openReplicaHost);
      }
      String alternateHost = forest.getAlternateHost();
      if ( alternateHost != null ) alternateHost = alternateHost.toLowerCase();
      if ( renames.containsKey(alternateHost) ) {
        alternateHost = renames.get(alternateHost);
      }
      String host = forest.getHost();
      if ( host != null ) host = host.toLowerCase();
      if ( renames.containsKey(host) ) {
        host = renames.get(host);
      }
      return new ForestImpl(host, openReplicaHost, alternateHost, forest.getDatabaseName(),
        forest.getForestName(), forest.getForestId(), forest.isUpdateable(), false);
    }).toArray(Forest[]::new);

    String[] validHosts = Stream.of(renamedForests).flatMap( forest ->
      Stream.of(new String[] {forest.getHost(), forest.getOpenReplicaHost(), forest.getAlternateHost()})
        .filter( host -> host != null )
        .map   ( host -> host.toLowerCase() )
        .filter( host -> {
          if ( whiteList.size() > 0 && ! whiteList.contains(host) ) {
            // this host isn't on the white-list, so ignore it
            return false;
          }
          if ( blackList.contains(host) ) {
            // this host is on the black list, so ignore it
            return false;
          }
          // this host is valid, add it to the list
          return true;
        })
    ).distinct().toArray(String[]::new);

    Stream<Forest> replaced = Stream.of(renamedForests);
    if ( blackList.size() > 0 ) {
      replaced = replaced.map( forest -> {
        if ( blackList.contains(forest.getPreferredHost()) ) {
          if ( validHosts.length == 0 ) {
            throw new IllegalStateException("White list or black list rules are too restrictive:" +
              " no valid hosts are left");
          }
          // this forest has a preferred host which is black-listed, so let's
          // replace it with an acceptable (valid) host
          String openReplicaHost = forest.getOpenReplicaHost();
          if ( blackList.contains(openReplicaHost) ) {
            openReplicaHost = replaceHost(openReplicaHost, validHosts);
          }
          String alternateHost = forest.getAlternateHost();
          if ( blackList.contains(alternateHost) ) {
            alternateHost = replaceHost(alternateHost, validHosts);
          }
          String host = forest.getHost();
          if ( blackList.contains(host) ) {
            host = replaceHost(host, validHosts);
          }
          return new ForestImpl(host, openReplicaHost, alternateHost, forest.getDatabaseName(),
            forest.getForestName(), forest.getForestId(), forest.isUpdateable(), false);
        } else {
          return forest;
        }
      });
    }
    if ( whiteList.size() > 0 ) {
      replaced = replaced.map( forest -> {
        if ( ! whiteList.contains(forest.getPreferredHost()) ) {
          if ( validHosts.length == 0 ) {
            throw new IllegalStateException("White list or black list rules are too restrictive:" +
              " no valid hosts are left");
          }
          // this forest has a preferred host which is not white-listed, so let's
          // replace it with an acceptable (valid) host
          String openReplicaHost = forest.getOpenReplicaHost();
          if ( ! whiteList.contains(openReplicaHost) ) {
            openReplicaHost = replaceHost(openReplicaHost, validHosts);
          }
          String alternateHost = forest.getAlternateHost();
          if ( ! whiteList.contains(alternateHost) ) {
            alternateHost = replaceHost(alternateHost, validHosts);
          }
          String host = forest.getHost();
          if ( ! whiteList.contains(host) ) {
            host = replaceHost(host, validHosts);
          }
          return new ForestImpl(host, openReplicaHost, alternateHost, forest.getDatabaseName(),
            forest.getForestName(), forest.getForestId(), forest.isUpdateable(), false);
        } else {
          return forest;
        }
      });
    }
    return replaced.toArray(Forest[]::new);
  }

  private String replaceHost(String invalidHost, String[] validHosts) {
    // don't attempt to replace null hosts
    if ( invalidHost == null ) return null;
    int charSum = 0;
    for ( char c : invalidHost.toCharArray() ) charSum += c;
    int replacementHostPosition = charSum % validHosts.length;
    // return a consistent valid host to replace the given invalid host
    return validHosts[replacementHostPosition];
  }

  /**
   * Add hosts to the list of black-listed hosts (hosts which the Batcher will not see when it 
   * calls listForests).  Do not use at the same time as withWhiteList.
   *
   * @param hostNames the hosts to black-list
   * @return this instance (for method chaining)
   */
  public FilteredForestConfiguration withBlackList(String... hostNames) {
    if ( whiteList.size() > 0 ) throw new IllegalStateException("whiteList already initialized");
    for ( String hostName : hostNames ) {
      if ( hostName != null ) blackList.add(hostName.toLowerCase());
    }
    return this;
  }

  /**
   * Add hosts to a white-list.  Only hosts on the list will be returned when
   * the Batcher calls listForests.  Should not be used in combination with
   * black-list as that could be confusing to manage.
   *
   * @param hostNames the hosts to white-list
   * @return this instance (for method chaining)
   */
  public FilteredForestConfiguration withWhiteList(String... hostNames) {
    if ( blackList.size() > 0 ) throw new IllegalStateException("blackList already initialized");
    for ( String hostName : hostNames ) {
      if ( hostName != null ) whiteList.add(hostName.toLowerCase());
    }
    return this;
  }

  /**
   * Rename hosts to network-addressable names rather than the host names known
   * to the MarkLogic cluster.
   *
   * @param hostName the host to rename
   * @param targetHostName the network-addressable host name to use
   * @return this instance (for method chaining)
   */
  public FilteredForestConfiguration withRenamedHost(String hostName, String targetHostName) {
    if ( hostName == null )       throw new IllegalArgumentException("hostName must not be null");
    if ( targetHostName == null ) throw new IllegalArgumentException("targetHostName must not be null");
    renames.put(hostName.toLowerCase(), targetHostName.toLowerCase());
    return this;
  }
}
