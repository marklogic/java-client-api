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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates writing uris to a file when necessary because setting [merge
 * timestamp][] and {@link QueryBatcher#withConsistentSnapshot
 * withConsistentSnapshot} is not an option, but you need to run DeleteListener
 * or ApplyTransformListener.
 *
 * Example writing uris to disk then running a delete:
 *
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
 *
 * [merge timestamp]: https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468
 */
public class UrisToWriterListener implements QueryBatchListener {
  private static Logger logger = LoggerFactory.getLogger(UrisToWriterListener.class);
  private Writer writer;
  private String suffix = "\n";
  private String prefix;
  private List<OutputListener> outputListeners = new ArrayList<>();

  public UrisToWriterListener(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void processEvent(QueryBatch batch) {
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

  public static interface OutputListener {
    String generateOutput(String uri);
  }
}
