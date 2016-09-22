/*
 * Copyright 2015-2016 MarkLogic Corporation
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

import java.io.IOException;
import java.io.Writer;

/**
 * Facilitates writing uris to a file when necessary because setting [merge
 * timestamp][] and {@link QueryHostBatcher#withConsistentSnapshot
 * withConsistentSnapshot} is not an option, but you need to run DeleteListener
 * or ApplyTransformListener.
 *
 * Example writing uris to disk then running a delete:
 *
 *     FileWriter writer = new FileWriter("uriCache.txt");
 *     QueryHostBatcher getUris = dataMovementManager.newQueryHostBatcher(query)
 *       .withBatchSize(5000)
 *       .onUrisReady( new UrisToWriterListener(writer) )
 *       .onQueryFailure((client, exception) -&gt; exception.printStackTrace());
 *     JobTicket getUrisTicket = dataMovementManager.startJob(getUris);
 *     getUris.awaitCompletion();
 *     dataMovementManager.stopJob(getUrisTicket);
 *     writer.flush();
 *     writer.close();
 *
 *     // now we have the uris, let's step through them
 *     BufferedReader reader = new BufferedReader(new FileReader("uriCache.txt"));
 *     QueryHostBatcher performDelete = dataMovementManager.newQueryHostBatcher(reader.lines().iterator())
 *       .onUrisReady(new DeleteListener())
 *       .onQueryFailure((client, exception) -&gt; exception.printStackTrace());
 *     JobTicket ticket = dataMovementManager.startJob(performDelete);
 *     performDelete.awaitCompletion();
 *     dataMovementManager.stopJob(ticket);
 *
 * [merge timestamp]: https://docs.marklogic.com/guide/app-dev/point_in_time#id_32468
 */
public class UrisToWriterListener implements BatchListener<String> {
  private Writer writer;
  private String suffix = "\n";
  private String prefix;
  private OutputListener outputListener;

  public UrisToWriterListener(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void processEvent(DatabaseClient client, Batch<String> batch) {
    synchronized(writer) {
      for ( String uri : batch.getItems() ) {
        try {
          if (prefix != null) writer.write(prefix);
          if (outputListener != null) {
            writer.write(outputListener.generateOutput(uri));
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
    this.outputListener = listener;
    return this;
  }

  public static interface OutputListener {
    String generateOutput(String uri);
  }
}
