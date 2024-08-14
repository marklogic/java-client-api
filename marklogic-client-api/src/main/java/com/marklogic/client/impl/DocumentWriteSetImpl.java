/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteOperation.OperationType;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

import java.util.*;

public class DocumentWriteSetImpl implements Set<DocumentWriteOperation>,DocumentWriteSet {

  private List<DocumentWriteOperation> operations;
  Boolean canSort;

  DocumentWriteSetImpl(){
    operations = new ArrayList<>();
    canSort = true;
  }
  @Override
  public DocumentWriteSet addDefault(DocumentMetadataWriteHandle metadataHandle) {
    if(canSort && operations.size() > 0)
      canSort = false;
    add(new DocumentWriteOperationImpl(OperationType.METADATA_DEFAULT,
      null, metadataHandle, null));
    return this;
  }

  @Override
  public DocumentWriteSet disableDefault() {
    if(canSort && operations.size() > 0)
      canSort = false;
    add(new DocumentWriteOperationImpl(OperationType.DISABLE_METADATA_DEFAULT,
      null, new StringHandle("{ }").withFormat(Format.JSON), null));
    return this;
  }

  @Override
  public DocumentWriteSet add(String docId, AbstractWriteHandle contentHandle) {
    add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
      docId, null, contentHandle));
    return this;
  }

  @Override
  public DocumentWriteSet addAs(String docId, Object content) {
    return addAs(docId, null, content);
  }

  @Override
  public DocumentWriteSet add(String docId, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle) {
    add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
      docId, metadataHandle, contentHandle));
    return this;
  }

  @Override
  public DocumentWriteSet addAs(String docId, DocumentMetadataWriteHandle metadataHandle, Object content) {
    if (content == null) throw new IllegalArgumentException("content must not be null");

    Class<?> as = content.getClass();
    ContentHandle<?> handle = DatabaseClientFactory.getHandleRegistry().makeHandle(as);
    Utilities.setHandleContent(handle, content);

    return add(docId, metadataHandle, handle);
  }

  @Override
  public DocumentWriteSet add(DocumentDescriptor desc, AbstractWriteHandle contentHandle) {
    add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
      desc.getUri(), null, contentHandle));
    return this;
  }

  @Override
  public DocumentWriteSet add(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle) {
    add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
      desc.getUri(), metadataHandle, contentHandle));
    return this;
  }

  @Override
  public DocumentWriteSet add(String docId, AbstractWriteHandle contentHandle, String temporalDocumentURI) {
    add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE, docId, null, contentHandle, temporalDocumentURI));
    return this;
  }

  @Override
  public DocumentWriteSet addAs(String docId, Object content, String temporalDocumentURI) {
    return addAs(docId, null, content, temporalDocumentURI);
  }

  @Override
  public DocumentWriteSet add(String docId, DocumentMetadataWriteHandle metadataHandle,
                              AbstractWriteHandle contentHandle, String temporalDocumentURI) {
    add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE, docId, metadataHandle, contentHandle,
      temporalDocumentURI));
    return this;
  }

  @Override
  public DocumentWriteSet addAs(String docId, DocumentMetadataWriteHandle metadataHandle, Object content,
                                String temporalDocumentURI) {
    if (content == null)
      throw new IllegalArgumentException("content must not be null");

    Class<?> as = content.getClass();
    ContentHandle<?> handle = DatabaseClientFactory.getHandleRegistry().makeHandle(as);
    Utilities.setHandleContent(handle, content);

    return add(docId, metadataHandle, handle, temporalDocumentURI);
  }

  @Override
  public DocumentWriteSet add(DocumentDescriptor desc, AbstractWriteHandle contentHandle, String temporalDocumentURI) {
    add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE, desc.getUri(), null, contentHandle,
      temporalDocumentURI));
    return this;
  }

  @Override
  public DocumentWriteSet add(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle,
                              AbstractWriteHandle contentHandle, String temporalDocumentURI) {
    add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE, desc.getUri(), metadataHandle, contentHandle,
      temporalDocumentURI));
    return this;
  }

  @Override
  public int size() {
    return operations.size();
  }

  @Override
  public boolean isEmpty() {
    return operations.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return operations.contains(o);
  }

  @Override
  public Iterator<DocumentWriteOperation> iterator() {
    if(canSort)
      Collections.sort(operations);
    return operations.iterator();
  }

  @Override
  public Object[] toArray() {
    return operations.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return operations.toArray(a);
  }

  @Override
  public boolean add(DocumentWriteOperation documentWriteOperation) {
    return operations.add(documentWriteOperation);
  }

  @Override
  public boolean remove(Object o) {
    return operations.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return operations.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends DocumentWriteOperation> c) {
    return operations.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return operations.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return operations.removeAll(c);
  }

  @Override
  public void clear() {
    operations.clear();
  }

}
