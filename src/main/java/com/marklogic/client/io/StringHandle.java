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
 * A String Handle represents document content as a string for reading or writing.
 */
public class StringHandle
	extends BaseHandle<String, String>
	implements
		JSONReadHandle, JSONWriteHandle, 
		TextReadHandle, TextWriteHandle,
		XMLReadHandle, XMLWriteHandle,
		StructureReadHandle, StructureWriteHandle
{
	private String content;

	public StringHandle() {
		super();
	}
	public StringHandle(String content) {
		this();
		set(content);
	}

	public String get() {
		return content;
	}
	public void set(String content) {
		this.content = content;
	}
	public StringHandle with(String content) {
		set(content);
		return this;
	}

	public StringHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}
	public StringHandle withMimetype(String mimetype) {
		setMimetype(mimetype);
		return this;
	}

	@Override
	protected Class<String> receiveAs() {
		return String.class;
	}
	@Override
	protected void receiveContent(String content) {
		this.content = content;
	}
	@Override
	protected String sendContent() {
		if (content == null) {
			throw new IllegalStateException("No string to write");
		}

		return content;
	}
}
