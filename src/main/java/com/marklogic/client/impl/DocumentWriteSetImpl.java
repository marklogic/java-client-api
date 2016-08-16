/*
 * Copyright 2012-2016 MarkLogic Corporation
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

import java.util.LinkedHashSet;

public class DocumentWriteSetImpl extends LinkedHashSet<DocumentWriteOperation> implements DocumentWriteSet {
    @Override
    public DocumentWriteSet addDefault(DocumentMetadataWriteHandle metadataHandle) {
		add(new DocumentWriteOperationImpl(OperationType.METADATA_DEFAULT,
			null, metadataHandle, null));
		return this;
	}

    @Override
    public DocumentWriteSet disableDefault() {
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
}
