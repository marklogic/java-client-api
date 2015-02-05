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
package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import com.marklogic.client.impl.JacksonBaseHandle;

/**
 * An adapter for using the Jackson Open Source library for JSON; represents 
 * JSON content as a Jackson JsonNode for reading or writing.  Enables reading and 
 * writing JSON documents, JSON structured search, and other JSON input and output.  
 */
public class JacksonHandle
		extends JacksonBaseHandle<JsonNode>
		implements ContentHandle<JsonNode>,
            OutputStreamSender, BufferableHandle, 
            JSONReadHandle, JSONWriteHandle,
            TextReadHandle, TextWriteHandle,
            XMLReadHandle, XMLWriteHandle,
            StructureReadHandle, StructureWriteHandle
{
	private JsonNode content;

	/**
	 * Creates a factory to create a JacksonHandle instance for a JSON node.
	 * @return	the factory
	 */
	static public ContentHandleFactory newFactory() {
		return new ContentHandleFactory() {
			@Override
			public Class<?>[] getHandledClasses() {
				return new Class<?>[]{ JsonNode.class };
			}
			@Override
			public boolean isHandled(Class<?> type) {
				return JsonNode.class.isAssignableFrom(type);
			}
			@Override
			public <C> ContentHandle<C> newHandle(Class<C> type) {
				@SuppressWarnings("unchecked")
				ContentHandle<C> handle = isHandled(type) ?
						(ContentHandle<C>) new JacksonHandle() : null;
				return handle;
			}
		};
	}

	/**
	 * Zero-argument constructor.
	 */
	public JacksonHandle() {
		super();
   		setResendable(true);
	}
	/**
	 * Provides a handle on JSON content as a Jackson tree.
	 * @param content	the JSON root node of the tree.
	 */
	public JacksonHandle(JsonNode content) {
		this();
		set(content);
	}

	/**
	 * Specifies the format of the content and returns the handle
	 * as a fluent convenience.
	 * @param format	the format of the content
	 * @return	this handle
	 */
	public JacksonHandle withFormat(Format format) {
		setFormat(format);
		return this;
	}

	/**
	 * Returns the root node of the JSON tree.
	 * @return	the JSON root node.
	 */
	@Override
	public JsonNode get() {
		return content;
	}
	/**
	 * Assigns a JSON tree as the content.
	 * @param content	the JSON root node.
	 */
	@Override
	public void set(JsonNode content) {
		this.content = content;
	}
	/**
	 * Assigns a JSON tree as the content and returns the handle.
	 * @param content	the JSON root node.
	 * @return	the handle on the JSON tree.
	 */
	public JacksonHandle with(JsonNode content) {
		set(content);
		return this;
	}

	@Override
	protected void receiveContent(InputStream content) {
		if (content == null)
			return;

		try {
			set( getMapper().readValue(
					new InputStreamReader(content, "UTF-8"), JsonNode.class
			));
		} catch (JsonParseException e) {
			throw new MarkLogicIOException(e);
		} catch (JsonMappingException e) {
			throw new MarkLogicIOException(e);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		} finally {
			try {
				content.close();
			} catch (IOException e) {
				// ignore.
			}
		}

	}
	@Override
	protected boolean hasContent() {
		return content != null;
	}
	@Override
	public void write(OutputStream out) throws IOException {
		getMapper().writeValue(new OutputStreamWriter(out, "UTF-8"), get());
	}
}
