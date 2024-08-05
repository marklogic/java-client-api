/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Facilitates writing uris to a file when necessary because setting
 * <a href="https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468">merge timestamp</a>
 * and {@link QueryBatcher#withConsistentSnapshot withConsistentSnapshot} is
 * not an option, but you need to run DeleteListener or
 * ApplyTransformListener.</p>
 *
 * Example writing uris to disk then running a delete:
 *
 * <pre>{@code
 *     FileWriter writer = new FileWriter("uriCache.txt");
 *     QueryBatcher getUris = dataMovementManager.newQueryBatcher(query)
 *       .withBatchSize(5000)
 *       .onUrisReady( new UrisToWriterListener(writer) )
 *       .onQueryFailure(exception -> exception.printStackTrace());
 *     JobTicket getUrisTicket = dataMovementManager.startJob(getUris);
 *     getUris.awaitCompletion();
 *     dataMovementManager.stopJob(getUrisTicket);
 *     writer.flush();
 *     writer.close();
 *
 *     // now we have the uris, let's step through them
 *     BufferedReader reader = new BufferedReader(new FileReader("uriCache.txt"));
 *     QueryBatcher performDelete = dataMovementManager.newQueryBatcher(reader.lines().iterator())
 *       .onUrisReady(new DeleteListener())
 *       .onQueryFailure(exception -> exception.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(performDelete);
 *     performDelete.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
 *}</pre>
 *
 * <p>As with all the provided listeners, this listener will not meet the needs
 * of all applications but the
 * <a target="_blank" href="https://github.com/marklogic/java-client-api/blob/master/marklogic-client-api/src/main/java/com/marklogic/client/datamovement/UrisToWriterListener.java">source code</a>
 * for it should serve as helpful sample code so you can write your own custom
 * listeners.</p>
 *
  */
public class UrisToWriterListener implements QueryBatchListener {
  private static Logger logger = LoggerFactory.getLogger(UrisToWriterListener.class);
  private Writer writer;
  private String suffix = "\n";
  private String prefix;
  private List<OutputListener> outputListeners = new ArrayList<>();
  private List<BatchFailureListener<QueryBatch>> queryBatchFailureListeners = new ArrayList<>();

  public UrisToWriterListener(Writer writer) {
    this.writer = writer;
    logger.debug("new UrisToWriterListener - this should print once/job; " +
      "if you see this once/batch, fix your job configuration");
  }

  /**
   * This implementation of initializeListener adds this instance of
   * UrisToWriterListener to the two RetryListener's in this QueryBatcher so they
   * will retry any batches that fail during the uris request.
   */
  @Override
  public void initializeListener(QueryBatcher queryBatcher) {
    HostAvailabilityListener hostAvailabilityListener = HostAvailabilityListener.getInstance(queryBatcher);
    if ( hostAvailabilityListener != null ) {
      BatchFailureListener<QueryBatch> retryListener = hostAvailabilityListener.initializeRetryListener(this);
      if ( retryListener != null )  onFailure(retryListener);
    }
    NoResponseListener noResponseListener = NoResponseListener.getInstance(queryBatcher);
    if ( noResponseListener != null ) {
      BatchFailureListener<QueryBatch> noResponseRetryListener = noResponseListener.initializeRetryListener(this);
      if ( noResponseRetryListener != null )  onFailure(noResponseRetryListener);
    }
  }

  @Override
  public void processEvent(QueryBatch batch) {
    try {
      synchronized(writer) {
        for ( String uri : batch.getItems() ) {
          try {
            if (prefix != null) writer.write(prefix);
            if ( outputListeners.size() > 0 ) {
              for ( OutputListener listener : outputListeners ) {
                String output = null;
                try {
                  output = listener.generateOutput(uri);
                } catch (Throwable t) {
                  logger.error("Exception thrown by an onGenerateOutput listener", t);
                }
                if ( output != null ) {
                  writer.write( output );
                }
              }
            } else {
              writer.write(uri);
            }
            if (suffix != null) writer.write(suffix);
          } catch(IOException e) {
            throw new DataMovementException("Failed to write uri \"" + uri + "\"", e);
          }
        }
      }
    } catch (Throwable t) {
      for ( BatchFailureListener<QueryBatch> queryBatchFailureListener : queryBatchFailureListeners ) {
        try {
          queryBatchFailureListener.processFailure(batch, t);
        } catch (Throwable t2) {
          logger.error("Exception thrown by an onFailure listener", t2);
        }
      }
    }
  }

  public UrisToWriterListener withRecordSuffix(String suffix) {
    this.suffix = suffix;
    return this;
  }

  public UrisToWriterListener withRecordPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  public UrisToWriterListener onGenerateOutput(OutputListener listener) {
    outputListeners.add(listener);
    return this;
  }

  /**
   * When a batch fails or a callback throws an Exception, run this listener
   * code.  Multiple listeners can be registered with this method.
   *
   * @param listener the code to run when a failure occurs
   *
   * @return this instance for method chaining
   */
  public UrisToWriterListener onFailure(BatchFailureListener<QueryBatch> listener) {
    queryBatchFailureListeners.add(listener);
    return this;
  }

  public static interface OutputListener {
    String generateOutput(String uri);
  }
}
