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
package com.marklogic.client.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.BaseHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.io.marker.StructureReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

public abstract class JacksonBaseHandle<T>
        extends BaseHandle<InputStream, OutputStreamSender>
        implements OutputStreamSender, BufferableHandle, JSONReadHandle, JSONWriteHandle,
            TextReadHandle, TextWriteHandle,
            XMLReadHandle, XMLWriteHandle,
            StructureReadHandle, StructureWriteHandle
{
    private ObjectMapper mapper;

    protected JacksonBaseHandle() {
        super();
        super.setFormat(Format.JSON);
    }

    /**
     * Returns the mapper used to construct node objects from JSON.
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

    public abstract void set(T content);
    @Override
    public void fromBuffer(byte[] buffer) {
        if (buffer == null || buffer.length == 0)
            set(null);
        else
            receiveContent(new ByteArrayInputStream(buffer));
    }
    @Override
    public byte[] toBuffer() {
        try {
            if ( ! hasContent() )
                return null;

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            write(buffer);

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new MarkLogicIOException(e);
        }
    }

    /**
     * Returns the JSON as a string.
     */
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
    protected OutputStreamSender sendContent() {
        if ( ! hasContent() ) {
            throw new IllegalStateException("No document to write");
        }
        return this;
    }

    protected abstract boolean hasContent();

    @Override
    public abstract void write(OutputStream out) throws IOException;
}

