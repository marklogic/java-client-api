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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.io.Format;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;

/**
 * A JacksonHandle represents JSON content as a Jackson JsonNode for reading or
 * writing.
 * 
 */
public class JacksonHandle
		extends BaseHandle<InputStream, OutputStreamSender>
		implements OutputStreamSender, BufferableHandle,
			JSONReadHandle, JSONWriteHandle,
			StructureReadHandle, StructureWriteHandle
{
	private JsonNode content;
	private ObjectMapper mapper;

	public JacksonHandle() {
		super();
		super.setFormat(Format.JSON);
   		setResendable(true);
	}

	public JacksonHandle(JsonNode content) {
		this();
		set(content);
	}

	public ObjectMapper getMapper() {
		if (mapper == null)
			mapper = new ObjectMapper();
		return mapper;
	}

	public JsonNode get() {
		return content;
	}

	public void set(JsonNode content) {
		this.content = content;
	}

	public JacksonHandle with(JsonNode content) {
		set(content);
		return this;
	}

	public void setFormat(Format format) {
		if (format != Format.JSON)
			throw new IllegalArgumentException(
					"JacksonHandle supports the JSON format only");
	}

	@Override
	public void fromBuffer(byte[] buffer) {
		if (buffer == null || buffer.length == 0)
			content = null;
		else
			receiveContent(new ByteArrayInputStream(buffer));
	}
	@Override
	public byte[] toBuffer() {
		try {
			if (content == null)
				return null;

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			write(buffer);

			return buffer.toByteArray();
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
	}
	@Override
	public String toString() {
		try {
			return new String(toBuffer(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new MarkLogicIOException(e);
		}
	}

	@Override
	protected Class<InputStream> receiveAs() {
		return InputStream.class;
	}
	@Override
	protected void receiveContent(InputStream content) {
		if (content == null)
			return;

		try {
			this.content = getMapper().readValue(
					new InputStreamReader(content, "UTF-8"), JsonNode.class
					);
		} catch (JsonParseException e) {
			throw new MarkLogicIOException(e);
		} catch (JsonMappingException e) {
			throw new MarkLogicIOException(e);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}

	}
	@Override
	protected OutputStreamSender sendContent() {
		if (content == null) {
			throw new IllegalStateException("No document to write");
		}
		return this;
	}
	@Override
	public void write(OutputStream out) throws IOException {
		getMapper().writeValue(new OutputStreamWriter(out, "UTF-8"), content);
	}
}
