/*
 * Copyright 2012-2019 MarkLogic Corporation
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
package com.marklogic.client.impl;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteOperation.OperationType;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

public class DocumentWriteOperationImpl implements DocumentWriteOperation,Comparable<DocumentWriteOperation> {
  private OperationType operationType;
  private String uri;
  private String temporalDocumentURI;
  private DocumentMetadataWriteHandle metadata;
  private AbstractWriteHandle content;

  public DocumentWriteOperationImpl(OperationType type, String uri,
                                    DocumentMetadataWriteHandle metadata, AbstractWriteHandle content)
  {
    this(type, uri, metadata, content, null);
  }

  public DocumentWriteOperationImpl(OperationType type, String uri,
                                    DocumentMetadataWriteHandle metadata, AbstractWriteHandle content, String temporalDocumentURI) {
    if(type == OperationType.DOCUMENT_WRITE && uri == null) {
      throw new IllegalArgumentException("Uri cannot be null when Operation Type is DOCUMENT_WRITE.");
    }
    if(type != OperationType.DOCUMENT_WRITE && uri != null) {
      throw new IllegalArgumentException("Operation Type should be DOCUMENT_WRITE when uri is not null");
    }

    this.operationType = type;
    this.uri = uri;
    this.metadata = metadata;
    this.content = content;
    this.temporalDocumentURI = temporalDocumentURI;
  }

  @Override
  public OperationType getOperationType() {
    return operationType;
  }

  @Override
  public String getUri() {
    return uri;
  }

  @Override
  public String getTemporalDocumentURI() {
    return temporalDocumentURI;
  }

  @Override
  public DocumentMetadataWriteHandle getMetadata() {
    return metadata;
  }

  @Override
  public AbstractWriteHandle getContent() {
    return content;
  }

  @Override
  public int compareTo(DocumentWriteOperation o) {
    if(o == null)
      throw new NullPointerException("DocumentWriteOperation cannot be null");

    if(this.getUri() != null && o.getUri() != null)
      return getUri().compareTo(o.getUri());

    if(this.getUri() == null && o.getUri() != null)
      return -1;

    if(this.getUri() != null && o.getUri()==null)
      return 1;

    if(this.getUri() == null && o.getUri() == null)
    {
      if(this.hashCode() > o.hashCode())
        return 1;
      else if (this.hashCode() < o.hashCode())
        return -1;
      return 0;
    }
    return 0;
  }

  @Override
  public int hashCode() {
    if(this.getUri()!=null)
      return this.getUri().hashCode();
    return super.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if(!(o instanceof DocumentWriteOperation))
      return false;
    if(this.getUri() == null)
      return super.equals(o);
    return this.getUri().equals(((DocumentWriteOperation) o).getUri());
  }
}
