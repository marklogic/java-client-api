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

public class ExportToWriterListener extends ExportListener {
  private Writer writer;
  private String suffix;
  private String prefix;
  private List<OutputListener> outputListeners = new ArrayList<>();

  public ExportToWriterListener(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void processEvent(QueryBatch batch) {
    DocumentPage docs = getDocs(batch);
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
                writer.write( listener.generateOutput(doc) );
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
  }

  public ExportToWriterListener withRecordSuffix(String suffix) {
    this.suffix = suffix;
    return this;
  }

  public ExportToWriterListener withRecordPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  public ExportToWriterListener onGenerateOutput(OutputListener listener) {
    outputListeners.add(listener);
    return this;
  }

  public static interface OutputListener {
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

  @Override
  public ExportToWriterListener withMetadataCategory(DocumentManager.Metadata category) {
    super.withMetadataCategory(category);
    return this;
  }

  @Override
  public ExportToWriterListener withNonDocumentFormat(Format nonDocumentFormat) {
    super.withNonDocumentFormat(nonDocumentFormat);
    return this;
  }

}
