/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>An extension of ExportListener which facilitates writing all documents to a
 * single Writer output stream.  The Writer could be a FileWriter, for example,
 * to write output to a CSV file.  The Writer could pipe to a socket, for
 * example, to send the output directly to another server endpoint.</p>
 *
 * <p>By default only document contents are retrieved.  If you would also like
 * metadata, make sure to call {@link #withMetadataCategory withMetadataCategory}
 * to configure which categories of metadata you desire.</p>
 *
 * <p>As with all the provided listeners, this listener will not meet the needs
 * of all applications but the
 * <a target="_blank" href="https://github.com/marklogic/java-client-api/blob/master/marklogic-client-api/src/main/java/com/marklogic/client/datamovement/ExportToWriterListener.java">source code</a>
 * for it should serve as helpful sample code so you can write your own custom
 * listeners.</p>
 */
public class ExportToWriterListener extends ExportListener {
  private static Logger logger = LoggerFactory.getLogger(ExportToWriterListener.class);
  private Writer writer;
  private String suffix;
  private String prefix;
  private List<OutputListener> outputListeners = new ArrayList<>();

  public ExportToWriterListener(Writer writer) {
    this.writer = writer;
    logger.debug("new ExportToWriterListener - this should print once/job; " +
      "if you see this once/batch, fix your job configuration");
  }

  /**
   * This implementation of initializeListener adds this instance of
   * ExportToWriterListener to the two RetryListener's in this QueryBatcher so they
   * will retry any batches that fail during the read request.
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
    try ( DocumentPage docs = getDocs(batch) ) {
      synchronized(writer) {
        for ( DocumentRecord doc : docs ) {
          Format format = doc.getFormat();
          if ( Format.BINARY.equals(format) ) {
            throw new IllegalStateException("Document " + doc.getUri() +
              " is binary and cannot be written.  Change your query to not select any binary documents.");
          } else {
            try {
              if ( prefix != null ) writer.write( prefix );
              if ( outputListeners.size() > 0 ) {
                for ( OutputListener listener : outputListeners ) {
                  String output = null;
                  try {
                    output = listener.generateOutput(doc);
                  } catch (Throwable t) {
                    logger.error("Exception thrown by an onGenerateOutput listener", t);
                  }
                  if ( output != null ) {
                    writer.write( output );
                  }
                }
              } else {
                writer.write( doc.getContent(new StringHandle()).get() );
              }
              if ( suffix != null ) writer.write( suffix );
            } catch (IOException e) {
              throw new DataMovementException("Failed to write document \"" + doc.getUri() + "\"", e);
            }
          }
        }
      }
    } catch (Throwable t) {
      for ( BatchFailureListener<QueryBatch> queryBatchFailureListener : getBatchFailureListeners() ) {
        try {
          queryBatchFailureListener.processFailure(batch, t);
        } catch (Throwable t2) {
          logger.error("Exception thrown by an onFailure listener", t2);
        }
      }
    }
  }

  /**
   * Sets the string suffix to append to the writer after each record.
   *
   * @param suffix the string suffix
   * @return this instance (for method chaining)
   */
  public ExportToWriterListener withRecordSuffix(String suffix) {
    this.suffix = suffix;
    return this;
  }

  /**
   * Sets the string prefix to send to the writer before each record.
   *
   * @param prefix the string prefix
   * @return this instance (for method chaining)
   */
  public ExportToWriterListener withRecordPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  /**
   * Registers a custom listener to override the default behavior for each
   * document which sends the document contents to the writer.  This listener
   * can choose what string to send to the writer for each document.
   *
   * @param listener the custom listener (or lambda expression)
   * @return this instance (for method chaining)
   */
  public ExportToWriterListener onGenerateOutput(OutputListener listener) {
    outputListeners.add(listener);
    return this;
  }

  /**
   * The listener interface required by onGenerateOutput.
   */
  public static interface OutputListener {
    /**
     * Given the DocumentRecord, generate the desired String output to send to the writer.
     *
     * @param record the document retrieved from the server
     * @return the String output to send to the writer
     */
    public String generateOutput(DocumentRecord record);
  }

  // override the following just to narrow the return type
  @Override
  public ExportToWriterListener withTransform(ServerTransform transform) {
    super.withTransform(transform);
    return this;
  }

  /* TODO: test to see if QueryView is really necessary
  @Override
  public ExportToWriterListener withSearchView(QueryManager.QueryView view) {
    super.withSearchView(view);
    return this;
  }
  */

  /**
   * Adds a metadata category to retrieve with each document.  The metadata
   * will be available via {@link DocumentRecord#getMetadata
   * DocumentRecord.getMetadata} in each DocumentRecord sent to the
   * OutputListener registered with onGenerateOutput.  To specify the format
   * for the metdata, call {@link #withNonDocumentFormat
   * withNonDocumentFormat}.
   *
   * @param category the metadata category to retrieve
   * @return this instance (for method chaining)
   */
  @Override
  public ExportToWriterListener withMetadataCategory(DocumentManager.Metadata category) {
    super.withMetadataCategory(category);
    return this;
  }

  /**
   * The format for the metadata retrieved with each document.  The metadata will
   * be available in each DocumentRecord sent to the OutputListener registered
   * with onGenerateOutput.
   *
   * @param nonDocumentFormat the format for the metadata
   * @return this instance (for method chaining)
   */
  @Override
  public ExportToWriterListener withNonDocumentFormat(Format nonDocumentFormat) {
    super.withNonDocumentFormat(nonDocumentFormat);
    return this;
  }

}
