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
package com.marklogic.client.bitemporal;


import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.Duration;

import com.marklogic.client.*;
import com.marklogic.client.bitemporal.TemporalDescriptor;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;

public interface TemporalDocumentManager<R extends AbstractReadHandle, W extends AbstractWriteHandle> {

  /**
   * Various protection levels to prevent updation/deletion and wiping of
   * temporal documents. You can request a more restrictive protection level than
   * the current protection level but not a lesser restrictive level. The descending
   * order of restriction levels are as follows: NOUPDATE, NODELETE and NOWIPE
   */
  public enum ProtectionLevel {
    /**
     * Protection level to prevent updating a temporal document. This
     * includes protection against temporal document wipe and delete as well.
     */
    NOUPDATE,
    /**
     * Protection level to prevent deletion of a temporal document. This
     * includes protection against temporal document wipe as well.
     */
    NODELETE,
    /**
     * Protection level to prevent wiping of a temporal document
     */
    NOWIPE;
    @Override
    public String toString() {
      switch(this) {
        case NODELETE:
          return "noDelete";
        case NOWIPE:
          return "noWipe";
        case NOUPDATE:
          return "noUpdate";
        default:
          throw new MarkLogicInternalException("Unknown enumeration");
      }
    }
  }

  /**
   *  Enables Last Stable Query Time (LSQT) on the named collection and
   *  advances the LSQT for the collection to the maximum system start time.
   *  When LSQT is enabled on the temporal collection, you can use the
   *  systemTime argument on many of the other TemporalDocumentManager methods.
   *
   *  The system time is returned in ISO 8601 format like all MarkLogic
   *  timestamps.  It can be parsed by {@link DatatypeConverter#parseDateTime
   *  DatatypeConverter.parseDateTime} but will lose precision since
   *  java.util.Calendar only supports millisecond precision.
   *
   *  Requires a user with the "rest-admin" privilege.
   *
   *  For details on how to use LSQT, see [Last Stable Query Time (LSQT) and
   *  Application-controlled System
   *  Time](http://docs.marklogic.com/guide/temporal/managing#id_75536) in the
   *  *Temporal Developer's Guide*.
   *
   *  @param temporalCollection the name of the temporal collection existing in
   *    the database into which this document should be written
   *  @return the temporal system time
   */
  public String advanceLsqt(String temporalCollection);

  /**
   *  Enables Last Stable Query Time (LSQT) on the named collection and
   *  advances the LSQT for the collection to the maximum system start time.
   *  When LSQT is enabled on the temporal collection, you can use the
   *  systemTime argument on many of the other TemporalDocumentManager methods.
   *
   *  The system time is returned in ISO 8601 format like all MarkLogic
   *  timestamps.  It can be parsed by {@link DatatypeConverter#parseDateTime
   *  DatatypeConverter.parseDateTime} but will lose precision since
   *  java.util.Calendar only supports millisecond precision.
   *
   *  Requires a user with the "rest-admin" privilege.
   *
   *  For details on how to use LSQT, see [Last Stable Query Time (LSQT) and
   *  Application-controlled System
   *  Time](http://docs.marklogic.com/guide/temporal/managing#id_75536) in the
   *  *Temporal Developer's Guide*.
   *
   *  @param temporalCollection the name of the temporal collection existing in
   *    the database into which this document should be written
   *  @param lag the milliseconds behind the maximum system start time to set LSQT
   *  @return the temporal system time
   */
  public String advanceLsqt(String temporalCollection, long lag);

  /**
   * Just like {@link DocumentManager#create(DocumentUriTemplate, DocumentMetadataWriteHandle,
   * AbstractWriteHandle, ServerTransform, Transaction) create} but create document
   * in a temporalCollection, which will enforce all the rules of
   * <a href="http://docs.marklogic.com/guide/temporal/managing">
   * bitemporal data management</a>.
   * @param template	the template for constructing the document uri
   * @param metadataHandle	a handle for writing the metadata of the document
   * @param contentHandle	an IO representation of the document content
   * @param transform	a server transform to modify the document content
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database into
   *    which this document should be written
   * @return the TemporalDescriptor including the database uri that identifies the created document,
   *    as well as the temporal system time when the document was created
   */
  public TemporalDescriptor create(DocumentUriTemplate template,
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
   * <a href="http://docs.marklogic.com/guide/temporal/managing">
   * bitemporal data management</a>.
   * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
   * @param metadataHandle	a handle for writing the metadata of the document
   * @param contentHandle	an IO representation of the document content
   * @param transform	a server transform to modify the document content
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database into
   *    which this document should be written
   * @return the TemporalDescriptor with the temporal system time when the document was written
   */
  public TemporalDescriptor write(DocumentDescriptor desc,
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
   * <a href="http://docs.marklogic.com/guide/temporal/managing">
   * bitemporal data management</a>.
   * @param docId	the URI identifier for the document
   * @param metadataHandle	a handle for writing the metadata of the document
   * @param contentHandle	an IO representation of the document content
   * @param transform	a server transform to modify the document content
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database into
   *    which this document should be written
   * @return the TemporalDescriptor with the temporal system time when the document was written
   */
  public TemporalDescriptor write(String docId,
                                  DocumentMetadataWriteHandle metadataHandle,
                                  W contentHandle,
                                  ServerTransform transform,
                                  Transaction transaction,
                                  String temporalCollection)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  /**
   * Just like {@link DocumentManager#delete(DocumentDescriptor, Transaction) delete} but delete
   * document in a temporalCollection, which will enforce all the rules of
   * <a href="http://docs.marklogic.com/guide/temporal/managing">
   * bitemporal data management</a>.
   * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database in
   *    which this document should be marked as deleted
   * @return the TemporalDescriptor with the temporal system time when the document was deleted
   */
  public TemporalDescriptor delete(DocumentDescriptor desc,
                                   Transaction transaction,
                                   String temporalCollection)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  /**
   * Just like {@link DocumentManager#delete(String, Transaction) delete} but delete
   * document in a temporalCollection, which will enforce all the rules of
   * <a href="http://docs.marklogic.com/guide/temporal/managing">
   * bitemporal data management</a>.
   * @param docId	the URI identifier for the document
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database in
   *    which this document should be marked as deleted
   * @return the TemporalDescriptor with the temporal system time when the document was deleted
   */
  public TemporalDescriptor delete(String docId,
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
   * @return the database uri that identifies the created document,
   *    as well as the temporal system time when the document was created
   */
  public TemporalDescriptor create(DocumentUriTemplate template,
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
   * @return the TemporalDescriptor with the temporal system time when the document was written
   */
  public TemporalDescriptor write(DocumentDescriptor desc,
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
   * @return the TemporalDescriptor with the temporal system time when the document was written
   */
  public TemporalDescriptor write(String docId,
                                  DocumentMetadataWriteHandle metadataHandle,
                                  W contentHandle,
                                  ServerTransform transform,
                                  Transaction transaction,
                                  String temporalCollection,
                                  java.util.Calendar systemTime)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  /**
   * Just like {@link DocumentManager#write(DocumentWriteSet, ServerTransform, Transaction)
   *  write} but create document in a temporalCollection, which will enforce all the rules of
   * <a href="http://docs.marklogic.com/guide/temporal/managing">
   * bitemporal data management</a>.
   * @param writeSet	the set of documents and metadata to write
   * @param transform	a server transform to modify the contents of each document
   * @param transaction	an open transaction under which the documents will be written
   * @param temporalCollection	the name of the temporal collection existing in the database into
   *    which this document should be written
   * @see <a href="http://docs.marklogic.com/guide/rest-dev/bulk">REST API -&gt; Reading
   *      and Writing Multiple Documents</a>
   */
  // TODO: do we return something for the temporal system time? is it per-document?
  public void write(DocumentWriteSet writeSet,
                    ServerTransform transform,
                    Transaction transaction,
                    String temporalCollection);

  /**
   * Just like {@link #write(DocumentDescriptor, DocumentMetadataWriteHandle,
   * AbstractWriteHandle, ServerTransform, Transaction, String) write} but creates a new
   * version of the document in the logical temporal collection URI passed as argument
   * and names the new version of the document as the URI in the Document descriptor
   * @param desc    a descriptor for the version URI identifier, format, and mimetype of the document
   * @param temporalDocumentURI the logical temporal document collection URI of the document
   * @param metadataHandle  a handle for writing the metadata of the document
   * @param contentHandle   an IO representation of the document content
   * @param transform   a server transform to modify the document content
   * @param transaction an open transaction under which the document may have been created or deleted
   * @param temporalCollection  the name of the temporal collection existing in the database into
   *    which this document should be written
   * @return the TemporalDescriptor with the temporal system time when the document was written
   */
  public TemporalDescriptor write(DocumentDescriptor desc, String temporalDocumentURI,
                                  DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform,
                                  Transaction transaction, String temporalCollection);

  /**
   * Just like {@link #write(String, DocumentMetadataWriteHandle, AbstractWriteHandle,
   * ServerTransform, Transaction, String) write} but creates a new
   * version of the document in the logical temporal collection URI passed as argument
   * and names the new version of the document as the docId passed.
   * @param uri   the version URI identifier for the document
   * @param temporalDocumentURI the logical temporal document collection URI of the document
   * @param metadataHandle  a handle for writing the metadata of the document
   * @param contentHandle   an IO representation of the document content
   * @param transform   a server transform to modify the document content
   * @param transaction an open transaction under which the document may have been created or deleted
   * @param temporalCollection  the name of the temporal collection existing in the database into
   *    which this document should be written
   * @return the TemporalDescriptor with the temporal system time when the document was written
   */
  public TemporalDescriptor write(String uri, String temporalDocumentURI, DocumentMetadataWriteHandle metadataHandle,
                                  W contentHandle, ServerTransform transform, Transaction transaction, String temporalCollection);

  /**
   * Just like {@link #write(String, String, DocumentMetadataWriteHandle,
   * AbstractWriteHandle, ServerTransform, Transaction, String) write} but writes document
   * at a specific system time
   * @param uri the version URI identifier for the document
   * @param temporalDocumentURI the logical temporal document collection URI of the document
   * @param metadataHandle    a handle for writing the metadata of the document
   * @param contentHandle an IO representation of the document content
   * @param transform a server transform to modify the document content
   * @param transaction   an open transaction under which the document may have been created or deleted
   * @param temporalCollection    the name of the temporal collection existing in the database into
   *    which this document should be written
   * @param systemTime    the application-specified system time with which this document will be marked
   * @return the TemporalDescriptor with the temporal system time when the document was written
   */
  public TemporalDescriptor write(String uri, String temporalDocumentURI, DocumentMetadataWriteHandle metadataHandle,
                                  W contentHandle, ServerTransform transform, Transaction transaction, String temporalCollection,
                                  Calendar systemTime);

  /**
   * Just like {@link #write(DocumentDescriptor, String, DocumentMetadataWriteHandle,
   * AbstractWriteHandle, ServerTransform, Transaction, String) write} but writes document
   * at a specific system time
   * @param desc  a descriptor for the version URI identifier, format, and mimetype of the document
   * @param temporalDocumentURI the logical temporal document collection URI of the document
   * @param metadataHandle    a handle for writing the metadata of the document
   * @param contentHandle an IO representation of the document content
   * @param transform a server transform to modify the document content
   * @param transaction   an open transaction under which the document may have been created or deleted
   * @param temporalCollection    the name of the temporal collection existing in the database into
   *    which this document should be written
   * @param systemTime    the application-specified system time with which this document will be marked
   * @return the TemporalDescriptor with the temporal system time when the document was written
   */
  public TemporalDescriptor write(DocumentDescriptor desc, String temporalDocumentURI,
                                  DocumentMetadataWriteHandle metadataHandle, W contentHandle, ServerTransform transform,
                                  Transaction transaction, String temporalCollection, Calendar systemTime);

  /**
   * Just like {@link #delete(DocumentDescriptor, Transaction, String) delete} but delete
   * document at a specified system time
   * @param desc	a descriptor for the URI identifier, format, and mimetype of the document
   * @param transaction	an open transaction under which the document may have been created or deleted
   * @param temporalCollection	the name of the temporal collection existing in the database in
   *    which this document should be marked as deleted
   * @param systemTime	the application-specified system time with which this document will be marked
   * @return the TemporalDescriptor with the temporal system time when the document was deleted
   */
  public TemporalDescriptor delete(DocumentDescriptor desc,
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
   * @return the TemporalDescriptor with the temporal system time when the document was deleted
   */
  public TemporalDescriptor delete(String docId,
                                   Transaction transaction,
                                   String temporalCollection,
                                   java.util.Calendar systemTime)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  /**
   * Removes all the versions of the temporal document specified by
   * the temporal document logical URI in the temporalCollection
   * @param temporalDocumentURI the logical temporal document URI of the document to be wiped
   * @param transaction an open transaction
   * @param temporalCollection the name of the temporal collection existing in the database
   *    from which this temporal document should be wiped
   */
  public void wipe(String temporalDocumentURI, Transaction transaction, String temporalCollection);

  /**
   * Removes all the versions of the temporal document specified by
   * the temporal document logical URI in the temporalCollection
   * @param temporalDocumentURI the logical temporal document URI of the document to be wiped
   * @param temporalCollection the name of the temporal collection existing in the database
   *    from which this temporal document should be wiped
   */
  public void wipe(String temporalDocumentURI, String temporalCollection);

  /**
   * Protects the temporal document from document update,
   * wipe or delete till the expiryTime provided
   * @param temporalDocumentURI the logical temporal document URI of the document to be protected
   * @param temporalCollection the name of the temporal collection which contains the temporal document
   * @param level the Protection level acquired - NODELETE, NOWIPE or NOUPDATE. Default value is NODELETE
   * @param expiryTime the exact date time when the document protection expires
   */
  public void protect(String temporalDocumentURI, String temporalCollection, ProtectionLevel level, Calendar expiryTime);

  /**
   * Protects the temporal document from document update, wipe or delete till the expiryTime
   * provided and saves the serialized copy of the current version in the path given
   * @param temporalDocumentURI the logical temporal document URI of the document to be protected
   * @param temporalCollection the name of the temporal collection which contains the temporal document
   * @param level the Protection level acquired - NODELETE, NOWIPE or NOUPDATE. Default value is NODELETE
   * @param expiryTime the exact date time when the document protection expires
   * @param archivePath File path to save a serialized copy of the current version of the document
   */
  public void protect(String temporalDocumentURI, String temporalCollection, ProtectionLevel level, Calendar expiryTime, String archivePath);

  /**
   * Protects the temporal document from document update,
   * wipe or delete for the specified duration.
   * @param temporalDocumentURI the logical temporal document URI of the document to be protected
   * @param temporalCollection the name of the temporal collection which contains the temporal document
   * @param level the Protection level acquired - NODELETE, NOWIPE, NOUPDATE. Default value is NODELETE
   * @param duration the duration during which the document is protected
   */
  public void protect(String temporalDocumentURI, String temporalCollection, ProtectionLevel level, Duration duration);

  /**
   * Protects the temporal document from document update, wipe or delete till the expiryTime
   * provided and saves the serialized copy of the current version in the path given
   * @param temporalDocumentURI the logical temporal document URI of the document to be protected
   * @param temporalCollection the name of the temporal collection which contains the temporal document
   * @param level the Protection level acquired - NODELETE, NOWIPE, NOUPDATE. Default value is NODELETE
   * @param duration the duration during which the document is protected
   * @param archivePath File path to save a serialized copy of the current version of the document
   */
  public void protect(String temporalDocumentURI, String temporalCollection, ProtectionLevel level, Duration duration, String archivePath);

  /**
   * Protects the temporal document from document update,
   * wipe or delete for the specified duration.
   * @param temporalDocumentURI the logical temporal document URI of the document to be protected
   * @param temporalCollection the name of the temporal collection which contains the temporal document
   * @param level the Protection level acquired - NODELETE, NOWIPE, NOUPDATE. Default value is NODELETE
   * @param duration the duration during which the document is protected
   * @param transaction an open transaction
   */
  public void protect(String temporalDocumentURI, String temporalCollection, ProtectionLevel level, Duration duration,
                      Transaction transaction);

  /**
   * Protects the temporal document from document update,
   * wipe or delete till the expiryTime provided
   * @param temporalDocumentURI the logical temporal document URI of the document to be protected
   * @param temporalCollection the name of the temporal collection which contains the temporal document
   * @param level the Protection level acquired - NODELETE, NOWIPE or NOUPDATE. Default value is NODELETE
   * @param expiryTime the exact date time when the document protection expires
   * @param transaction an open transaction
   */
  public void protect(String temporalDocumentURI, String temporalCollection, ProtectionLevel level, Calendar expiryTime,
                      Transaction transaction);

  /**
   * Protects the temporal document from document update, wipe or delete till the expiryTime
   * provided and saves the serialized copy of the current version in the path given
   * @param temporalDocumentURI the logical temporal document URI of the document to be protected
   * @param temporalCollection the name of the temporal collection which contains the temporal document
   * @param level the Protection level acquired - NODELETE, NOWIPE, NOUPDATE. Default value is NODELETE
   * @param duration the duration during which the document is protected
   * @param archivePath File path to save a serialized copy of the current version of the document
   * @param transaction an open transaction
   */
  public void protect(String temporalDocumentURI, String temporalCollection, ProtectionLevel level, Duration duration, String archivePath,
                      Transaction transaction);

  /**
   * Protects the temporal document from document update, wipe or delete till the expiryTime
   * provided and saves the serialized copy of the current version in the path given
   * @param temporalDocumentURI the logical temporal document URI of the document to be protected
   * @param temporalCollection the name of the temporal collection which contains the temporal document
   * @param level the Protection level acquired - NODELETE, NOWIPE or NOUPDATE. Default value is NODELETE
   * @param expiryTime the exact date time when the document protection expires
   * @param archivePath File path to save a serialized copy of the current version of the document
   * @param transaction an open transaction
   */
  public void protect(String temporalDocumentURI, String temporalCollection, ProtectionLevel level, Calendar expiryTime, String archivePath,
                      Transaction transaction);

  /**
   * Modifies the content of a temporal JSON or XML document in the temporal collection.
   *
   * @param uri the URI identifier for the document
   * @param temporalCollection the name of the temporal collection which contains
   *        the temporal document to be patched.
   * @param patch a handle definition of what to patch in the document
   */
  void patch(String uri, String temporalCollection, DocumentPatchHandle patch);

  /**
   * Modifies the content of a temporal JSON or XML document in the temporal collection.
   *
   * @param uri the URI identifier for the document
   * @param temporalCollection the name of the temporal collection which contains
   *        the temporal document to be patched.
   * @param patch a handle definition of what to patch in the document
   * @param transaction an open transaction
   */
  void patch(String uri, String temporalCollection, DocumentPatchHandle patch, Transaction transaction);

  /**
   * Patches a temporal document by taking the content from the sourceDocumentURI
   * present in the temporalDocumentURI collection with the patch handle and
   * creates a new version identified by the uri in the temporalCollection.
   *
   * @param uri the new version URI identifier for the document
   * @param temporalDocumentURI the logical temporal document URI
   * @param temporalCollection the name of the temporal collection which contains
   *        the temporal document to be patched.
   * @param sourceDocumentURI the URI of the document to be used as the source of the patch
   * @param patch a handle definition of what to patch in the document
   */
  void patch(String uri, String temporalDocumentURI, String temporalCollection, String sourceDocumentURI,
             DocumentPatchHandle patch);

  /**
   * Patches a temporal document by taking the content from the sourceDocumentURI
   * present in the temporalDocumentURI collection with the patch handle and
   * creates a new version identified by the uri in the temporalCollection.
   *
   * @param uri the new version URI identifier for the document
   * @param temporalDocumentURI the logical temporal document URI
   * @param temporalCollection the name of the temporal collection which contains
   *        the temporal document to be patched.
   * @param sourceDocumentURI the URI of the document to be used as the source of the patch
   * @param patch a handle definition of what to patch in the document
   * @param transaction an open transaction
   */
  void patch(String uri, String temporalDocumentURI, String temporalCollection, String sourceDocumentURI,
             DocumentPatchHandle patch, Transaction transaction);
}
