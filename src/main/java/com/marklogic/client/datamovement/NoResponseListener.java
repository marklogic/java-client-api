package com.marklogic.client.datamovement;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

/**
 * NoResponseListener is a default listener like HostAvailabilityListener that
 * is automatically registered with the QueryBatcher and WriteBatcher instances.
 * This listener is used to handle empty responses from the server. For some
 * requests, we might not have any response from the server for a request when
 * the server goes down or is unavailable. To handle such scenarios, we register
 * this listener and this would automatically take care of retrying the batches.
 * <br>
 * <br>
 * This is different from HostAvailabilityListener in the fact that this
 * listener's RetryListener inherited from HostAvailabilityListener might not be
 * desirable when using an ApplyTransformListener. If we get empty responses
 * when we try to apply a transform to the batch of URIs retrieved from the
 * server, we are not sure what happened in the server - if the transform has
 * been applied or it has not been applied. Retrying in those scenarios would
 * apply the transform twice if the transform has been already applied and this
 * is not desirable.
 *
 */
public class NoResponseListener extends HostAvailabilityListener {

  public NoResponseListener(DataMovementManager moveMgr) {
    super(moveMgr);
  }

  // Overriding isHostUnavailableException() to check for the
  // appropriate exception for no response from the server
  protected boolean isHostUnavailableException(Throwable throwable, Set<Throwable> path) {
    // Check if the exception is an IOException and check if the
    // message indicates an end of stream on a Connection.
    if ( IOException.class.isInstance(throwable) && throwable.getMessage().contains("unexpected end of stream on") ) {
      return true;
    }
    // we need to check our recursion path to avoid infinite recursion if a
    // getCause() pointed to itself or an ancestor
    if ( throwable.getCause() != null && !path.contains(throwable.getCause()) ) {
      path.add(throwable.getCause());
      boolean isCauseHostUnavailableException = isHostUnavailableException(throwable.getCause(), path);
      if ( isCauseHostUnavailableException == true )
        return true;
    }
    return false;
  }

  /**
   * Returns the NoResponseListener instance registered with the Batcher.
   *
   * @param batcher the Batcher instance for which the registered
   *          NoResponseListener is returned
   * @return the NoResponseListener instance with the batcher or null if there
   *         is no NoResponseListener registered
   * @throws IllegalStateException if the passed Batcher is neither a
   *           QueryBatcher nor a WriteBatcher
   */
  public static NoResponseListener getInstance(Batcher batcher) {
    if ( batcher instanceof WriteBatcher ) {
      WriteFailureListener[] writeFailureListeners = ((WriteBatcher) batcher).getBatchFailureListeners();
      for (WriteFailureListener writeFailureListener : writeFailureListeners) {
        if ( writeFailureListener instanceof NoResponseListener ) {
          return (NoResponseListener) writeFailureListener;
        }
      }
    } else if ( batcher instanceof QueryBatcher ) {
      QueryFailureListener[] queryFailureListeners = ((QueryBatcher) batcher).getQueryFailureListeners();
      for (QueryFailureListener queryFailureListener : queryFailureListeners) {
        if ( queryFailureListener instanceof NoResponseListener ) {
          return (NoResponseListener) queryFailureListener;
        }
      }
    } else {
      throw new IllegalStateException(
          "The Batcher should be either a QueryBatcher instance or a WriteBatcher instance");
    }
    return null;
  }
}
