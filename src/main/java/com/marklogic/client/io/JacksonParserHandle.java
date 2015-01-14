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
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.impl.JacksonBaseHandle;
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

/**
 * An adapter for using the streaming capabilities of the Jackson Open Source library.
 * Enables low-level efficient reading and writing of JSON documents.
 * @see <a href="http://wiki.fasterxml.com/JacksonStreamingApi">Jackson Streaming API</a>
 */
public class JacksonParserHandle
    extends JacksonBaseHandle<JsonParser>
    implements ContentHandle<JsonParser>,
            OutputStreamSender, BufferableHandle, 
            JSONReadHandle, JSONWriteHandle,
            TextReadHandle, TextWriteHandle,
            XMLReadHandle, XMLWriteHandle,
            StructureReadHandle, StructureWriteHandle
{
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
     * @return the JsonParser over the content (usually received from the server)
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

    /**
     * Provides access to the ObjectMapper used internally so you can configure
     * it to fit your JSON.
     * @return the ObjectMapper instance
     */
    @Override
    public ObjectMapper getMapper() { return super.getMapper(); }
    /**
     * Enables clients to specify their own ObjectMapper instance, including databinding mappers
     * for formats other than JSON.
     * For <a href="https://github.com/FasterXML/jackson-dataformat-csv">example</a>:<pre>{@code
     *ObjectMapper mapper = new CsvMapper();
     *mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
     *mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
     *handle.setMapper(mapper);
     * }</pre>
     *
     * Use at your own risk!  Note that you most likely want to set to false the two options we
     * demonstrate above (JsonGenerator.Feature.AUTO_CLOSE_TARGET and JsonParser.Feature.AUTO_CLOSE_SOURCE)
     * as we do so your mapper will not close streams which we may need to reuse if we have to
     * resend a network request.
     **/
    @Override
    public void setMapper(ObjectMapper mapper) { super.setMapper(mapper); }

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
