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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.Format;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;

/**
 * A JacksonHandle represents JSON content as a Jackson JsonNode for reading or
 * writing.
 * 
 */
public class JacksonHandle extends BaseHandle<InputStream, OutputStreamSender>
		implements OutputStreamSender, JSONReadHandle, JSONWriteHandle,
		StructureReadHandle, StructureWriteHandle {
	private JsonNode content;
	private ObjectMapper mapper;

	public JacksonHandle() {
		super();
		super.setFormat(Format.JSON);
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
			new IllegalArgumentException(
					"JacksonHandle supports the JSON format only");
	}

	public JacksonHandle withFormat(Format format) {
		setFormat(format);
		return this;
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
			this.content = getMapper().readValue(content, JsonNode.class);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
		getMapper().writeValue(out, content);
	}

}
