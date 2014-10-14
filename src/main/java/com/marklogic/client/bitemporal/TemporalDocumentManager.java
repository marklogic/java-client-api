/*
 * Copyright 2012-2014 MarkLogic Corporation
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
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

public interface TemporalDocumentManager<R extends AbstractReadHandle, W extends AbstractWriteHandle> {
    public DocumentDescriptor create(DocumentUriTemplate template,
        DocumentMetadataWriteHandle metadataHandle,
        W contentHandle,
        ServerTransform transform,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
	throws ForbiddenUserException, FailedRequestException;
 
    public void write(DocumentDescriptor desc,
        DocumentMetadataWriteHandle metadataHandle,
        W contentHandle,
        ServerTransform transform,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
 
    public void write(String docId,
        DocumentMetadataWriteHandle metadataHandle,
        W contentHandle,
        ServerTransform transform,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
 
	/*
    public <T extends R> T read(DocumentDescriptor desc,
        DocumentMetadataReadHandle metadataHandle,
        T  contentHandle,
        ServerTransform transform,
        Transaction transaction,
        String temporalCollection)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
 
    public <T extends R> T read(String docId,
        DocumentMetadataReadHandle metadataHandle,
        T contentHandle,
        ServerTransform transform,
        Transaction transaction,
        String temporalCollection)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
 
    public DocumentPage read(ServerTransform transform,
        Transaction transaction,
        String temporalCollection,
        String[] uris)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	*/
 
    public void delete(DocumentDescriptor desc,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
 
    public void delete(String docId,
        Transaction transaction,
        String temporalCollection,
        java.util.Calendar systemTime)
	throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
}
