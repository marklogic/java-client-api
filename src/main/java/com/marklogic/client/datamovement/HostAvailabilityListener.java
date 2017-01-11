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

/** HostAvailabilityListener is automatically registered with all QueryBatcher
 * and WriteBatcher instances to monitor for failover scenarios.  When
 * HostAvailabilityListener detects that a host is unavailable (matches one of
 * {@link #getHostUnavailableExceptions()}), it black-lists the host for a
 * period of time equal to {@link #getSuspendTimeForHostUnavailable()}.  After
 * that time, it calls {@link DataMovementManager#readForestConfig()} then
 * passes that updated ForestConfiguration to batcher.withForestConfig() so the
 * batcher will fall back to using the hosts the server says are available.
 * Directly after black-listing (and before updating the ForestConfiguration)
 * this calls batcher.retry with the failed WriteBatch or QueryBatchException
 * so the batch can succeed if possible.  The main objective here is to
 * gracefully handle a failover scenario by temporarily routing requests to
 * hosts other than the failed host(s), retry-ing requests that failed, and
 * eventually reverting to utilizing the full cluster that's available.
 * Nevertheless, there will definitely be failure scenarios not addressed by
 * HostAvailabilityListener and therefore we recommend that production
 * installations of Data Movement SDK use HostAvailabilityListener as an
 * example and install their own failure-handling listeners complete with
 * retry and updates to the batcher's ForestConfiguration as appropriate.
 *
 * If you would like to change the default settings, you can change them
 * on the pre-registered HostAvailabilityListener which you can access via
 * {@link WriteBatcher#getBatchFailureListeners()} or {@link
 * QueryBatcher#getQueryFailureListeners()}.
 *
 */
public class HostAvailabilityListener implements QueryFailureListener, WriteFailureListener {
  private static Logger logger = LoggerFactory.getLogger(HostAvailabilityListener.class);
  private DataMovementManager moveMgr;
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
   */
  public HostAvailabilityListener(DataMovementManager moveMgr) {
    if (moveMgr == null) throw new IllegalArgumentException("moveMgr must not be null");
    this.moveMgr = moveMgr;
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
    if (numHosts <= 0) throw new IllegalArgumentException("numHosts must be > 0");
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
   * This implements the WriteFailureListener interface
   *
   * @param batch the batch of WriteEvents
   * @param throwable the exception
   */
  public void processFailure(WriteBatch batch, Throwable throwable) {
    boolean isHostUnavailableException = processException(batch.getBatcher(), throwable, batch.getClient().getHost());
    if ( isHostUnavailableException == true ) {
      try {
        logger.warn("Retrying failed batch: {}, results so far: {}, uris: {}",
          batch.getJobBatchNumber(), batch.getJobWritesSoFar(),
          Stream.of(batch.getItems()).map(event->event.getTargetUri()).collect(Collectors.toList()));
        batch.getBatcher().retry(batch);
      } catch (RuntimeException e) {
        logger.error("Exception during retry", e);
        processFailure(batch, e);
      }
    }
  }

  /**
   * This implements the QueryFailureListener interface
   *
   * @param queryBatch the exception with information about the failed query attempt
   */
  public void processFailure(QueryBatchException queryBatch) {
    boolean isHostUnavailableException = processException(queryBatch.getBatcher(), queryBatch, queryBatch.getClient().getHost());
    if ( isHostUnavailableException == true ) {
      try {
        logger.warn("Retrying failed batch: {}, results so far: {}, forest: {}, forestBatch: {}, forest results so far: {}",
          queryBatch.getJobBatchNumber(), queryBatch.getJobResultsSoFar(), queryBatch.getForest().getForestName(),
          queryBatch.getForestBatchNumber(), queryBatch.getForestResultsSoFar());
        queryBatch.getBatcher().retry(queryBatch);
      } catch (RuntimeException e) {
        logger.error("Exception during retry", e);
        processFailure(new QueryBatchException(queryBatch, e));
      }
    }
  }

  private synchronized boolean processException(Batcher batcher, Throwable throwable, String host) {
    // we only do something if this throwable is on our list of exceptions
    // which we consider marking a host as unavilable
    boolean isHostUnavailableException = isHostUnavailableException(throwable, new HashSet<>());
    boolean shouldWeRetry = isHostUnavailableException;
    if ( isHostUnavailableException == true ) {
      ForestConfiguration existingForestConfig = batcher.getForestConfig();
      String[] preferredHosts = existingForestConfig.getPreferredHosts();
      if ( ! Arrays.asList(preferredHosts).contains(host) ) {
        // skip all the logic below because the host in question here is already
        // missing from the list of hosts for this batcher
        return shouldWeRetry;
      }
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
        shouldWeRetry = false;
        logger.error("Encountered [" + throwable + "] on host \"" + host +
          "\" but black-listing it would drop job below minHosts (" + minHosts +
          "), so stopping job \"" + batcher.getJobName() + "\"", throwable);
        moveMgr.stopJob(batcher);
      }
    }
    return shouldWeRetry;
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
