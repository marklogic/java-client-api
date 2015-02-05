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
package com.marklogic.client.impl;

import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteOperation.OperationType;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

import java.util.LinkedHashSet;

public class DocumentWriteSetImpl extends LinkedHashSet<DocumentWriteOperation> implements DocumentWriteSet {
    public DocumentWriteSet addDefault(DocumentMetadataWriteHandle metadataHandle) {
		add(new DocumentWriteOperationImpl(OperationType.METADATA_DEFAULT,
			null, metadataHandle, null));
		return this;
	}

    public DocumentWriteSet disableDefault() {
		add(new DocumentWriteOperationImpl(OperationType.DISABLE_METADATA_DEFAULT,
			null, new StringHandle("{ }").withFormat(Format.JSON), null));
		return this;
	}

    public DocumentWriteSet add(String docId, AbstractWriteHandle contentHandle) {
		add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
			docId, null, contentHandle));
		return this;
	}

    public DocumentWriteSet add(String docId, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle) {
		add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
			docId, metadataHandle, contentHandle));
		return this;
	}

    public DocumentWriteSet add(DocumentDescriptor desc, AbstractWriteHandle contentHandle) {
		add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
			desc.getUri(), null, contentHandle));
		return this;
	}

    public DocumentWriteSet add(DocumentDescriptor desc, DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle) {
		add(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
			desc.getUri(), metadataHandle, contentHandle));
		return this;
	}
}
