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

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;

public class DocumentRecordImpl implements DocumentRecord {
	private static final Logger logger = LoggerFactory.getLogger(DocumentRecordImpl.class);
	private String uri;
	private Format format;
	private String mimetype;
	private InputStream metadata; 
	private InputStream content; 

	DocumentRecordImpl(String uri, Format format, String mimetype, InputStream metadata, InputStream content) {
		this.uri = uri;
		this.format = format;
		this.mimetype = mimetype;
		this.metadata = metadata;
		this.content = content;
	}

    @Override
    public String getUri() {
		return uri;
	}

    @Override
    public Format getFormat() {
		return format;
	}

    @Override
    public String getMimetype() {
		return mimetype;
	}

    @Override
    public <T extends DocumentMetadataReadHandle> T getMetadata(T metadataHandle) {
		HandleImplementation metadataBase = HandleAccessor.checkHandle(metadataHandle, "metadata");
		Format metadataFormat = metadataBase.getFormat();
		if (metadataFormat == null || (metadataFormat != Format.XML)) {
			if (logger.isWarnEnabled())
				logger.warn("Unsupported metadata format {}, using XML",metadataFormat.name());
			metadataBase.setFormat(Format.XML);
		}
		HandleAccessor.receiveContent(metadataHandle, metadata);
		return metadataHandle;
	}

    @Override
	public <T> T getMetadataAs(Class<T> clazz) {
		ContentHandle<T> readHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(clazz);
		if ( readHandle instanceof DocumentMetadataReadHandle ) {
			DocumentMetadataReadHandle metadataHandle = (DocumentMetadataReadHandle) readHandle;
			metadataHandle = getMetadata(metadataHandle);
			if ( metadataHandle == null ) return null;
			return readHandle.get();
		} else {
			throw new IllegalArgumentException("Class \"" + clazz.getName() + "\" uses handle " +
				readHandle.getClass() + " which is not a DocumentMetadataReadHandle");
		}
	}

    @Override
    public <T extends AbstractReadHandle> T getContent(T contentHandle) {
		HandleAccessor.checkHandle(contentHandle, "content");
		HandleAccessor.receiveContent(contentHandle, content);
		return contentHandle;
	}

    @Override
	public <T> T getContentAs(Class<T> clazz) {
		ContentHandle<T> readHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(clazz);
		readHandle = getContent(readHandle);
		if ( readHandle == null ) return null;
		return readHandle.get();
	}
}

