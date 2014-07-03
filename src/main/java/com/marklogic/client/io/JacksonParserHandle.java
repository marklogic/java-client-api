/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.OperationNotSupported;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.impl.JacksonBaseHandle;

/**
 * An adapter for using the streaming capabilities of the Jackson Open Source library.
 * Enables low-level reading and writing of JSON documents.
 */
// TODO: add link to jackson streaming documentation
public class JacksonParserHandle
    extends JacksonBaseHandle<JsonParser>
    implements ContentHandle<JsonParser>
{
    private ObjectMapper mapper;
    private JsonParser parser = null;
    private InputStream content = null;

	final static private int BUFFER_SIZE = 8192;

	/**
	 * Creates a factory to create a JacksonParserHandle instance for a JsonParser.
	 * @return	the factory
	 */
	static public ContentHandleFactory newFactory() {
		return new ContentHandleFactory() {
			@Override
			public Class<?>[] getHandledClasses() {
				return new Class<?>[]{ JsonParser.class };
			}
			@Override
			public boolean isHandled(Class<?> type) {
				return JsonParser.class.isAssignableFrom(type);
			}
			@Override
			public <C> ContentHandle<C> newHandle(Class<C> type) {
				@SuppressWarnings("unchecked")
				ContentHandle<C> handle = isHandled(type) ?
						(ContentHandle<C>) new JacksonParserHandle() : null;
				return handle;
			}
		};
	}

    public JacksonParserHandle() {
        super();
        setFormat(Format.JSON);
   		setResendable(false);
    }

    /**
     * Specifies the format of the content and returns the handle
     * as a fluent convenience.
     * @param format	the format of the content
     * @return	this handle
     */
    public JacksonParserHandle withFormat(Format format) {
        setFormat(format);
        return this;
    }

    /**
     * JsonParser allows streaming access to content as it arrives.
     */
    public JsonParser get() {
        if ( parser == null ) {
            if ( content == null ) {
                throw new IllegalStateException("Handle is not yet populated with content");
            }
            try {
				parser = getMapper().getFactory().createParser(content);
			} catch (JsonParseException e) {
                throw new MarkLogicIOException(e);
			} catch (IOException e) {
	            throw new MarkLogicIOException(e);
			}
        }
        return parser;
    }
    /**
     * Available for the edge case that content from a JsonParser must be written.
     */
    public void set(JsonParser parser) {
        this.parser = parser;
        if ( parser == null ) {
            content = null;
        } else if ( parser.getInputSource() instanceof InputStream ) {
            content = (InputStream) parser.getInputSource();
        }
    }

    @Override
    protected void receiveContent(InputStream content) {
        this.content = content;
        if (content == null) parser = null;
    }
    protected boolean hasContent() {
        return content != null || parser != null;
    }
	@Override
	public void write(OutputStream out) throws IOException {
        try {
            if ( parser != null && parser.nextToken() != null ) {
                JsonGenerator generator = getMapper().getFactory().createGenerator(out);
                generator.copyCurrentStructure(parser);
                generator.close();
            } else if (content != null) {
                byte[] b = new byte[BUFFER_SIZE];
                int len = 0;
                while ((len = content.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                content.close();
            }
        } catch (IOException e) {
            throw new MarkLogicIOException(e);
        }
    }
}
