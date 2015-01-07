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
package com.marklogic.client.extra.gson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;

/**
 * A GSONHandle represents JSON content as a GSON JsonElement for reading or
 * writing.  You must install the GSON library to use this class.
 */
public class GSONHandle
		extends BaseHandle<InputStream, String>
		implements BufferableHandle, ContentHandle<JsonElement>,
			JSONReadHandle, JSONWriteHandle,
			StructureReadHandle, StructureWriteHandle
{
	private JsonElement content;
	private JsonParser  parser;

	/**
	 * Creates a factory to create a GSONHandle instance for a JsonElement node.
	 * @return	the factory
	 */
	static public ContentHandleFactory newFactory() {
		return new ContentHandleFactory() {
			@Override
			public Class<?>[] getHandledClasses() {
				return new Class<?>[]{ JsonElement.class };
			}
			@Override
			public boolean isHandled(Class<?> type) {
				return JsonElement.class.isAssignableFrom(type);
			}
			@Override
			public <C> ContentHandle<C> newHandle(Class<C> type) {
				@SuppressWarnings("unchecked")
				ContentHandle<C> handle = isHandled(type) ?
						(ContentHandle<C>) new GSONHandle() : null;
				return handle;
			}
		};
	}

	/**
	 * Zero-argument constructor.
	 */
	public GSONHandle() {
		super();
		super.setFormat(Format.JSON);
   		setResendable(true);
	}
	/**
	 * Provides a handle on JSON content as a tree.
	 * @param content	the JSON root element of the tree.
	 */
	public GSONHandle(JsonElement content) {
		this();
		set(content);
	}

	/**
	 * Returns the parser used to construct element objects from JSON.
	 * @return	the JSON parser.
	 */
	public JsonParser getParser() {
		if (parser == null)
			parser = new JsonParser();
		return parser;
	}

	/**
	 * Returns the root node of the JSON tree.
	 * @return	the JSON root element.
	 */
	@Override
	public JsonElement get() {
		return content;
	}
	/**
	 * Assigns a JSON tree as the content.
	 * @param content	the JSON root element.
	 */
	@Override
	public void set(JsonElement content) {
		this.content = content;
	}
	/**
	 * Assigns a JSON tree as the content and returns the handle.
	 * @param content	the JSON root element.
	 * @return	the handle on the JSON tree.
	 */
	public GSONHandle with(JsonElement content) {
		set(content);
		return this;
	}

	/**
	 * Restricts the format to JSON.
	 */
	@Override
	public void setFormat(Format format) {
		if (format != Format.JSON)
			throw new IllegalArgumentException(
					"GSONHandle supports the JSON format only");
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
		if (content == null) {
			return null;
		}

		return content.toString().getBytes(Charset.forName("UTF-8"));
	}

	/**
	 * Returns the JSON tree as a string.
	 */
	@Override
	public String toString() {
		if (content == null) {
			return null;
		}
		return content.toString();
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
			this.content = getParser().parse(
					new InputStreamReader(content, "UTF-8")
					);
		} catch (JsonIOException e) {
			throw new MarkLogicIOException(e);
		} catch (JsonSyntaxException e) {
			throw new MarkLogicIOException(e);
		} catch (UnsupportedEncodingException e) {
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
	protected String sendContent() {
		if (content == null) {
			throw new IllegalStateException("No document to write");
		}
		return content.toString();
	}
}
