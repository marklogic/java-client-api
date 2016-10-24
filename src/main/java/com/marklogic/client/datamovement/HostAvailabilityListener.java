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

import com.marklogic.client.DatabaseClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.NoHttpResponseException;

import java.net.SocketException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HostAvailabilityListener implements QueryFailureListener, WriteFailureListener {
  private static Logger logger = LoggerFactory.getLogger(HostAvailabilityListener.class);
  private DataMovementManager moveMgr;
  private Batcher batcher;
  private Duration suspendTimeForHostUnavailable = Duration.ofMinutes(10);
  private int minHosts = 1;
  private ScheduledFuture<?> future;
  List<Class<?>> hostUnavailableExceptions = new ArrayList<>();
  {
    hostUnavailableExceptions.add(NoHttpResponseException.class);
    hostUnavailableExceptions.add(SocketException.class);
    hostUnavailableExceptions.add(SSLException.class);
    hostUnavailableExceptions.add(UnknownHostException.class);
  }

  /**
   * @param moveMgr the DataMovementManager (used to call readForestConfig to reset after black-listing an unavailable host)
   * @param batcher the WriteBatcher or QueryBatcher instance this will listen to (used to call withForestConfig to black-list an unavailable host)
   */
  public HostAvailabilityListener(DataMovementManager moveMgr, Batcher batcher) {
    if (moveMgr == null) throw new IllegalArgumentException("moveMgr must not be null");
    if (batcher == null) throw new IllegalArgumentException("batcher must not be null");
    this.moveMgr = moveMgr;
    this.batcher = batcher;
  }

  /** If a host becomes unavailable (NoHttpResponseException, SocketException, SSLException,
   * UnknownHostException), adds it to the blacklist
   *
   * @param duration the amount of time an unavailable host will be suspended
   *
   * @return this instance (for method chaining)
   */
  public HostAvailabilityListener withSuspendTimeForHostUnavailable(Duration
  duration) {
    if (duration == null) throw new IllegalArgumentException("duration must not be null");
    this.suspendTimeForHostUnavailable = duration;
    return this;
  }

  /** If less than minHosts are left, calls stopJob.
   *
   * @param numHosts the minimum number of hosts before this will call dataMovementMangaer.stopJob(batcher)
   *
   * @return this instance (for method chaining)
   */
  public HostAvailabilityListener withMinHosts(int numHosts) {
    this.minHosts = numHosts;
    return this;
  }

  /** Overwrites the list of exceptions for which a host will be blacklisted
   *
   * @param exceptionTypes the list of types of Throwable, any of which constitute a host that's unavailable
   *
   * @return this instance (for method chaining)
   */
  public HostAvailabilityListener withHostUnavailableExceptions(Class<Throwable>... exceptionTypes) {
    hostUnavailableExceptions = new ArrayList<>();
    for ( Class<Throwable> exception : exceptionTypes ) {
      hostUnavailableExceptions.add(exception);
    }
    return this;
  }

  /**
   * @return the list of types of Throwable, any of which constitute a host that's unavailable
   */
  public Throwable[] getHostUnavailableExceptions() {
    return hostUnavailableExceptions.toArray(new Throwable[hostUnavailableExceptions.size()]);
  }

  /**
   * @return the amount of time an unavailable host will be suspended
   */
  public Duration getSuspendTimeForHostUnavailable() {
    return suspendTimeForHostUnavailable;
  }

  /**
   * @return the minimum number of hosts before this will call dataMovementMangaer.stopJob(batcher)
   */
  public int getMinHosts() {
    return minHosts;
  }

  /**
   * This implements the BatchFailureListener interface
   *
   * @param hostClient the host-specific client
   * @param batch the batch of WriteEvents
   * @param throwable the exception
   */
  public void processFailure(DatabaseClient hostClient, WriteBatch batch, Throwable throwable) {
    boolean isHostUnavailableException = processException(throwable, hostClient.getHost());
    if ( isHostUnavailableException == true ) {
      // TODO: resubmit the batch
      //batch.getBatcher().retry(batch);
    }
  }

  /**
   * This implements the FailureListener interface
   *
   * @param client the host-specific client
   * @param throwable the exception with information about the status of the job
   */
  public void processFailure(DatabaseClient client, QueryHostException queryBatch) {
    boolean isHostUnavailableException = processException(queryBatch, client.getHost());
    if ( isHostUnavailableException == true ) {
      // TODO: resubmit the batch
      //batch.getBatcher().retry(queryBatch);
    }
  }

  private boolean processException(Throwable throwable, String host) {
    // we only do something if this throwable is on our list of exceptions
    // which we consider marking a host as unavilable
    boolean isHostUnavailableException = isHostUnavailableException(throwable, new HashSet<>());
    if ( isHostUnavailableException == true ) {
      ForestConfiguration existingForestConfig = batcher.getForestConfig();
      String[] preferredHosts = existingForestConfig.getPreferredHosts();
      if ( preferredHosts.length > minHosts ) {
        logger.error("ERROR: host unavailable \"" + host + "\", black-listing it for " +
          suspendTimeForHostUnavailable.toString(), throwable);
        FilteredForestConfiguration filteredForestConfig = new FilteredForestConfiguration(existingForestConfig);
        if ( batcher instanceof WriteBatcher ) {
          filteredForestConfig = filteredForestConfig.withBlackList(host);
        } else if ( batcher instanceof QueryBatcher ) {
          List<String> availableHosts = Stream.of(preferredHosts)
            .filter( (availableHost) -> ! availableHost.equals(host) )
            .collect(Collectors.toList());
          int randomPos = Math.abs(host.hashCode()) % availableHosts.size();
          String randomAvailableHost = availableHosts.get(randomPos);
          filteredForestConfig = filteredForestConfig.withRenamedHost(host, randomAvailableHost);
        }
        batcher.withForestConfig(filteredForestConfig);
        // cancel any previously scheduled re-sync
        if ( future != null ) future.cancel(false);
        // schedule a re-sync with the server forest config
        future = Executors.newScheduledThreadPool(1)
          .schedule( () -> {
              if ( batcher.isStopped() ) {
                logger.debug("Job \"{}\" is stopped, so cancelling re-sync with the server forest config",
                  batcher.getJobName());
              } else {
                ForestConfiguration updatedForestConfig = moveMgr.readForestConfig();
                logger.info("it's been {} since host {} failed, opening communication to all server hosts [{}]",
                  suspendTimeForHostUnavailable.toString(), host, Arrays.asList(updatedForestConfig.getPreferredHosts()));
                // set the forestConfig back to whatever the server says it is
                batcher.withForestConfig(updatedForestConfig);
              }
            }
            , suspendTimeForHostUnavailable.toMillis(), TimeUnit.MILLISECONDS);
      } else {
        // by black-listing this host we'd move below minHosts, so it's time to
        // stop this job
        logger.error("Encountered [" + throwable + "] on host \"" + host +
          "\" but black-listing it would drop job below minHosts (" + minHosts +
          "), so stopping job \"" + batcher.getJobName() + "\"", throwable);
        moveMgr.stopJob(batcher);
      }
    }
    return isHostUnavailableException;
  }

  private boolean isHostUnavailableException(Throwable throwable, Set<Throwable> path) {
    for ( Class<?> type : hostUnavailableExceptions ) {
      if ( type.isInstance(throwable) ) {
        return true;
      }
    }
    // we need to check our recursion path to avoid infinite recursion if a
    // getCause() pointed to itself or an ancestor
    if ( throwable.getCause() != null && ! path.contains(throwable.getCause()) ) {
      path.add(throwable.getCause());
      boolean isCauseHostUnavailableException = isHostUnavailableException(throwable.getCause(), path);
      if ( isCauseHostUnavailableException == true ) return true;
    }
    return false;
  }
}
