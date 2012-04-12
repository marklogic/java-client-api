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
package com.marklogic.client.example.handle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import com.marklogic.client.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A XOM Handle represents XML content as a XOM document for reading or writing.
 */
public class XOMHandle
    implements OutputStreamSender,
    	XMLReadHandle<InputStream>, XMLWriteHandle<OutputStreamSender>,
    	StructureReadHandle<InputStream>, StructureWriteHandle<OutputStreamSender>
{
	private Document content;
	private Builder  builder;

	public XOMHandle() {
		super();
	}
	public XOMHandle(Document content) {
		this();
    	set(content);
	}

	public Builder getBuilder() {
		if (builder == null)
			builder = makeBuilder();
		return builder;
	}
	public void setBuilder(Builder builder) {
		this.builder = builder;
	}
	protected Builder makeBuilder() {
		return new Builder(false);
	}

	protected Serializer makeSerializer(OutputStream out) {
		return new Serializer(out);
	}

	public Document get() {
		return content;
	}
    public void set(Document content) {
    	this.content = content;
    }
    public XOMHandle with(Document content) {
    	set(content);
    	return this;
    }

    public Format getFormat() {
		return Format.XML;
	}
	public void setFormat(Format format) {
		if (format != Format.XML)
			new IllegalArgumentException("XOMHandle supports the XML format only");
	}
	public XOMHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	@Override
	public Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	@Override
	public void receiveContent(InputStream content) {
		if (content == null)
			return;

		try {
			this.content = getBuilder().build(content);
		} catch (ValidityException e) {
			throw new RuntimeException(e);
		} catch (ParsingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public OutputStreamSender sendContent() {
		if (content == null) {
			throw new IllegalStateException("No document to write");
		}

		return this;
	}
	@Override
	public void write(OutputStream out) throws IOException {
		makeSerializer(out).write(content);
	}

}
