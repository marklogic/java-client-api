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
package com.marklogic.client.datamovement;

import com.marklogic.client.datamovement.impl.ForestImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class FilteredForestConfiguration implements ForestConfiguration {
  ForestConfiguration wrappedForestConfig;
  Set<String> blackList = new HashSet<>();
  Set<String> whiteList = new HashSet<>();
  Map<String,String> renames = new HashMap<>();

  FilteredForestConfiguration(ForestConfiguration forestConfig) {
    this.wrappedForestConfig = forestConfig;
  }

  public Forest[] listForests() {
    Stream<Forest> stream = Stream.of(wrappedForestConfig.listForests());
    if ( blackList.size() > 0 ) {
      stream = stream.filter((forest) -> ! blackList.contains(forest.getHost()));
    } else if ( whiteList.size() > 0 ) {
      stream = stream.filter((forest) -> whiteList.contains(forest.getHost()));
    }
    // apply renames
    stream = stream.map((forest) -> {
      if ( renames.containsKey(forest.getHost()) ) {
        ((ForestImpl) forest).setHost(renames.get(forest.getHost()));
      }
      return forest;
    });
    return stream.toArray(Forest[]::new);
  }

  public FilteredForestConfiguration withBlackList(String... hostNames) {
    if ( whiteList.size() > 0 ) throw new IllegalStateException("whiteList already initialized");
    for ( String hostName : hostNames ) {
      blackList.add(hostName);
    }
    return this;
  }

  public FilteredForestConfiguration withWhiteList(String... hostNames) {
    if ( blackList.size() > 0 ) throw new IllegalStateException("blackList already initialized");
    for ( String hostName : hostNames ) {
      whiteList.add(hostName);
    }
    return this;
  }

  public FilteredForestConfiguration withRenamedHost(String hostName, String targetHostName) {
    renames.put(hostName, targetHostName);
    return this;
  }
}
