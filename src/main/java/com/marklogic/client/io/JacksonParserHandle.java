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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.BufferableHandle;
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
    extends BaseHandle<InputStream, OperationNotSupported>
    implements BufferableHandle, GenericReadHandle,
        JSONReadHandle, TextReadHandle, XMLReadHandle, StructureReadHandle
{
    private ObjectMapper mapper;
    private JsonParser parser = null;
    private InputStream content = null;

	final static private int BUFFER_SIZE = 8192;

    public JacksonParserHandle() {
        super();
        setFormat(Format.JSON);
   		setResendable(true);
    }

    /**
     * Returns the mapper used to generate the JsonParser.
     * @return    the JSON mapper.
     */
    public ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
            mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        }
        return mapper;
    }

	/**
	 * Enables clients to use any mapper, including databinding mappers for formats other than JSON.
	 * Use at your own risk!  Note that you may want to configure your mapper as we do to not close
	 * streams which we may need to reuse if we have to resend a network request:
	 * <code>
     *      mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
     *      mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
	 * </code>
	 **/
	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
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
    public JsonParser getParser() {
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

    @Override
    protected Class<InputStream> receiveAs() {
        return InputStream.class;
    }

    @Override
    protected void receiveContent(InputStream content) {
        if (content == null) return;
        this.content = content;
    }
    protected boolean hasContent() {
        return content != null;
    }
    @Override
    public void fromBuffer(byte[] buffer) {
        if (buffer == null || buffer.length == 0) {
            content = null;
        } else {
            receiveContent(new ByteArrayInputStream(buffer));
        }
    }
    @Override
    public byte[] toBuffer() {
        try {
            if (content == null) return null;

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] b = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = content.read(b)) != -1) {
                buffer.write(b, 0, len);
            }
            content.close();

            b = buffer.toByteArray();
            fromBuffer(b);

            return b;
        } catch (IOException e) {
            throw new MarkLogicIOException(e);
        }
    }
}

