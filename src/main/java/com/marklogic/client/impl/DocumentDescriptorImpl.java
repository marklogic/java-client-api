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
import com.marklogic.client.io.Format;

public class DocumentDescriptorImpl implements DocumentDescriptor {
	private String  uri;
	private Format  format;
	private String  mimetype;
	private long    byteLength = UNKNOWN_LENGTH;
	private long    version    = UNKNOWN_VERSION;
	private boolean isInternal = false;

	public DocumentDescriptorImpl(boolean isInternal) {
		super();
		setInternal(isInternal);
	}
	public DocumentDescriptorImpl(String uri, boolean isInternal) {
		this(isInternal);
		setUri(uri);
	}

	@Override
	public String getUri() {
		return uri;
	}
	@Override
	public void setUri(String uri) {
		this.uri = uri;
		if (byteLength != UNKNOWN_LENGTH)
			byteLength = UNKNOWN_LENGTH;
	}

	@Override
	public Format getFormat() {
		return format;
	}
	@Override
	public void setFormat(Format format) {
		this.format = format;
	}
	@Override
	public DocumentDescriptor withFormat(Format format) {
		setFormat(format);
		return this;
	}

	@Override
	public String getMimetype() {
		return mimetype;
	}
	@Override
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	@Override
	public long getByteLength() {
    	return byteLength;
    }
	@Override
	public void setByteLength(long length) {
    	byteLength = length;
    }

	@Override
	public void setVersion(long version) {
		this.version = version;
	}
	@Override
	public long getVersion() {
		return version;
	}

	protected void setInternal(boolean isInternal) {
		this.isInternal = isInternal;
	}
	protected boolean isInternal() {
		return isInternal;
	}
}
