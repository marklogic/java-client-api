/*
 * Copyright 2012 MarkLogic Corporation
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

import com.marklogic.client.DocumentIdentifier;

public class DocumentIdentifierImpl implements DocumentIdentifier {
	private String uri;
	private long   byteLength = UNKNOWN_LENGTH;
	private String mimetype;

	public DocumentIdentifierImpl(String uri) {
		super();
		setUri(uri);
	}

	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
		if (byteLength != UNKNOWN_LENGTH)
			byteLength = UNKNOWN_LENGTH;
	}
	public DocumentIdentifier withUri(String uri) {
		setUri(uri);
		return this;
	}

	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public DocumentIdentifier withMimetype(String mimetype) {
		setMimetype(mimetype);
		return this;
	}

	public long getByteLength() {
    	return byteLength;
    }
	public void setByteLength(long length) {
    	byteLength = length;
    }
}
