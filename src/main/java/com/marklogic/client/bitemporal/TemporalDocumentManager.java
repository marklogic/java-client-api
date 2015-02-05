/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.bitemporal;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.util.RequestParameters;

public interface TemporalDocumentManager<R extends AbstractReadHandle, W extends AbstractWriteHandle> {
  /**
   * Just like {@link DocumentManager#create(DocumentUriTemplate, DocumentMetadataWriteHandle,
   * AbstractWriteHandle, ServerTransform, Transaction) create} but create document
   * in a temporalCollection, which will enforce all the rules of
   * <a href="http://docs.marklogic.com/8.0/guide/concepts/data-management#id_98803">
   * bitemporal data management</a>.
   * @param template	the template for constructing the document uri
   * @param metadataHandle	a handle for writing the metadata of the document
   * @param contentHandle	an IO representation of the document content
   * @param transform	a server transform to modify the document content
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database into
   *    which this document should be written
   * @return the database uri that identifies the created document
   */
  public DocumentDescriptor create(DocumentUriTemplate template,
      DocumentMetadataWriteHandle metadataHandle,
      W contentHandle,
      ServerTransform transform,
      Transaction transaction,
      String temporalCollection)
  throws ForbiddenUserException, FailedRequestException;

  /**
   * Just like {@link DocumentManager#write(DocumentDescriptor, DocumentMetadataWriteHandle,
   * AbstractWriteHandle, ServerTransform, Transaction) write} but write document
   * in a temporalCollection, which will enforce all the rules of
   * <a href="http://docs.marklogic.com/8.0/guide/concepts/data-management#id_98803">
   * bitemporal data management</a>.
   * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
   * @param metadataHandle	a handle for writing the metadata of the document
   * @param contentHandle	an IO representation of the document content
   * @param transform	a server transform to modify the document content
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database into
   *    which this document should be written
   */
  public void write(DocumentDescriptor desc,
      DocumentMetadataWriteHandle metadataHandle,
      W contentHandle,
      ServerTransform transform,
      Transaction transaction,
      String temporalCollection)
  throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  /**
   * Just like {@link DocumentManager#write(String, DocumentMetadataWriteHandle,
   * AbstractWriteHandle, ServerTransform, Transaction) write} but write document
   * in a temporalCollection, which will enforce all the rules of
   * <a href="http://docs.marklogic.com/8.0/guide/concepts/data-management#id_98803">
   * bitemporal data management</a>.
   * @param docId	the URI identifier for the document
   * @param metadataHandle	a handle for writing the metadata of the document
   * @param contentHandle	an IO representation of the document content
   * @param transform	a server transform to modify the document content
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database into
   *    which this document should be written
   */
  public void write(String docId,
      DocumentMetadataWriteHandle metadataHandle,
      W contentHandle,
      ServerTransform transform,
      Transaction transaction,
      String temporalCollection)
  throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  /**
   * Just like {@link DocumentManager#delete(DocumentDescriptor, Transaction) delete} but delete
   * document in a temporalCollection, which will enforce all the rules of
   * <a href="http://docs.marklogic.com/8.0/guide/concepts/data-management#id_98803">
   * bitemporal data management</a>.
   * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database in
   *    which this document should be marked as deleted
   */
  public void delete(DocumentDescriptor desc,
      Transaction transaction,
      String temporalCollection)
  throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  /**
   * Just like {@link DocumentManager#delete(String, Transaction) delete} but delete
   * document in a temporalCollection, which will enforce all the rules of
   * <a href="http://docs.marklogic.com/8.0/guide/concepts/data-management#id_98803">
   * bitemporal data management</a>.
   * @param docId	the URI identifier for the document
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database in
   *    which this document should be marked as deleted
   */
  public void delete(String docId,
      Transaction transaction,
      String temporalCollection)
  throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

    // The following methods take a system time which is an advanced concept in bitemporal feature. 
    /**
     * Just like {@link #create(DocumentUriTemplate, DocumentMetadataWriteHandle,
     * AbstractWriteHandle, ServerTransform, Transaction, String) create} but create document
     * at a specific system time
     * @param template	the template for constructing the document uri
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	an IO representation of the document content
     * @param transform	a server transform to modify the document content
     * @param transaction	an open transaction under which the document may have been created or deleted
     * @param temporalCollection	the name of the temporal collection existing in the database into
     *    which this document should be written
     * @param systemTime	the application-specified system time with which this document will be marked
     * @return the database uri that identifies the created document
     */
    public DocumentDescriptor create(DocumentUriTemplate template,
        DocumentMetadataWriteHandle metadataHandle,
        W contentHandle,
        ServerTransform transform,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
    throws ForbiddenUserException, FailedRequestException;

    /**
     * Just like {@link #write(DocumentDescriptor, DocumentMetadataWriteHandle,
     * AbstractWriteHandle, ServerTransform, Transaction, String) write} but write document
     * at a specific system time
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	an IO representation of the document content
     * @param transform	a server transform to modify the document content
     * @param transaction	an open transaction under which the document may have been created or deleted
     * @param temporalCollection	the name of the temporal collection existing in the database into
     *    which this document should be written
     * @param systemTime	the application-specified system time with which this document will be marked
     */
    public void write(DocumentDescriptor desc,
        DocumentMetadataWriteHandle metadataHandle,
        W contentHandle,
        ServerTransform transform,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

    /**
     * Just like {@link #write(String, DocumentMetadataWriteHandle,
     * AbstractWriteHandle, ServerTransform, Transaction, String) write} but write document
     * at a specific system time
     * @param docId	the URI identifier for the document
     * @param metadataHandle	a handle for writing the metadata of the document
     * @param contentHandle	an IO representation of the document content
     * @param transform	a server transform to modify the document content
     * @param transaction	an open transaction under which the document may have been created or deleted
     * @param temporalCollection	the name of the temporal collection existing in the database into
     *    which this document should be written
     * @param systemTime	the application-specified system time with which this document will be marked
     */
    public void write(String docId,
        DocumentMetadataWriteHandle metadataHandle,
        W contentHandle,
        ServerTransform transform,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

    /**
     * Just like {@link #delete(DocumentDescriptor, Transaction, String) delete} but delete
     * document at a specified system time
     * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
     * @param transaction	an open transaction under which the document may have been created or deleted
     * @param temporalCollection	the name of the temporal collection existing in the database in
     *    which this document should be marked as deleted
     * @param systemTime	the application-specified system time with which this document will be marked
     */
    public void delete(DocumentDescriptor desc,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

    /**
     * Just like {@link #delete(String, Transaction, String) delete} but delete
     * document at a specified system time
     * @param docId	the URI identifier for the document
     * @param transaction	an open transaction under which the document may have been created or deleted
     * @param temporalCollection	the name of the temporal collection existing in the database in
     *    which this document should be marked as deleted
     * @param systemTime	the application-specified system time with which this document will be marked
     */
    public void delete(String docId,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
}
