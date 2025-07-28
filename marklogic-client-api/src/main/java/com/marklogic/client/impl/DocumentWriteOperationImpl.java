/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

public class DocumentWriteOperationImpl implements DocumentWriteOperation {
  private OperationType operationType;
  private String uri;
  private String temporalDocumentURI;
  private DocumentMetadataWriteHandle metadata;
  private AbstractWriteHandle content;

  /**
   * Defaults the type of operation to {@code OperationType.DOCUMENT_WRITE}.
   *
   * @param uri
   * @param metadata
   * @param content
   */
  public DocumentWriteOperationImpl(String uri, DocumentMetadataWriteHandle metadata, AbstractWriteHandle content)
  {
    this(OperationType.DOCUMENT_WRITE, uri, metadata, content, null);
  }

  public DocumentWriteOperationImpl(OperationType type, String uri,
                                    DocumentMetadataWriteHandle metadata, AbstractWriteHandle content)
  {
    this(type, uri, metadata, content, null);
  }

  /**
   * Defaults the type of operation to {@code OperationType.DOCUMENT_WRITE}.
   *
   * @param uri
   * @param metadata
   * @param content
   * @param temporalDocumentURI
   */
  public DocumentWriteOperationImpl(String uri, DocumentMetadataWriteHandle metadata, AbstractWriteHandle content, String temporalDocumentURI) {
    this(OperationType.DOCUMENT_WRITE, uri, metadata, content, temporalDocumentURI);
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

  public DocumentWriteOperationImpl(String uri, AbstractWriteHandle content){
    this(OperationType.DOCUMENT_WRITE, uri, null, content, null);
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
