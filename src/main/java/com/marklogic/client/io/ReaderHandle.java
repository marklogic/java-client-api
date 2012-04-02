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

import java.io.Reader;

import com.marklogic.client.Format;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A Reader Handle represents a resource as a reader for reading or writing.
 * 
 * When finished with the reader, close the reader to release the resources.
 */
public class ReaderHandle
	implements
		JSONReadHandle<Reader>, JSONWriteHandle<Reader>, 
		TextReadHandle<Reader>, TextWriteHandle<Reader>,
		XMLReadHandle<Reader>, XMLWriteHandle<Reader>,
		StructureReadHandle<Reader>, StructureWriteHandle<Reader>
{
    private Reader content;
	private Format format = Format.XML;

    public ReaderHandle() {
    }

    /**
	 * Returns a reader for a resource read from the database.
	 * 
     * When finished with the reader, close the reader to release
     * the response.
     * 
     * @return
     */
    public Reader get() {
    	return content;
    }
	public void set(Reader content) {
		this.content = content;
	}
	public ReaderHandle with(Reader content) {
		set(content);
		return this;
	}

	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public ReaderHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	public Class<Reader> receiveAs() {
		return Reader.class;
	}
	public void receiveContent(Reader content) {
		this.content = content;
	}
	public Reader sendContent() {
		if (content == null) {
			throw new IllegalStateException("No character stream to write");
		}

		return content;
	}
}
