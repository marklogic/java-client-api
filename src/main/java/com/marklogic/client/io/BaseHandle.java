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
package com.marklogic.client.io;

import com.marklogic.client.impl.HandleImplementation;

/**
 * BaseHandle is the base class for content representations
 * such as byte arrays, strings, input streams, character readers,
 * files, POJO (Plain Old Java Object) structures and so on.
 * Content representations are used for query options, search results, values results,
 * document metadata, and documents in binary, JSON, text, and XML formats.
 * Read handles receive content from the server and must implement the receiveAs() and
 * receiveContent() methods.
 * Write handles send content to the server and must implement the sendContent() method.
 * A handle can support both read and write operations.
 *
 * @param <R>
 * @param <W>
 */
public abstract class BaseHandle<R,W>
    extends HandleImplementation<R,W>
{
	private Format format = Format.UNKNOWN;
	private String mimetype;
	private long length = UNKNOWN_LENGTH;

	public BaseHandle() {
		super();
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
	public String getMimetype() {
		if (mimetype == null && format != null)
			return format.getDefaultMimetype();

		return mimetype;
	}
	@Override
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	@Override
	public long getByteLength() {
		return length;
	}
	@Override
	public void setByteLength(long length) {
		this.length = length;
	}
}
