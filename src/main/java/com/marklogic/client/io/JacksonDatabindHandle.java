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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * JSON content for reading or writing as objects of the specified POJO class.
 * Enables reading and writing JSON directly to or from POJOs.
 */
public class JacksonDatabindHandle<T>
        extends JacksonBaseHandle<T>
        implements ContentHandle<T>,
            OutputStreamSender, BufferableHandle, 
            JSONReadHandle, JSONWriteHandle,
            TextReadHandle, TextWriteHandle,
            XMLReadHandle, XMLWriteHandle,
            StructureReadHandle, StructureWriteHandle
{
    private Class<?> contentClass;
    private T content;

    /**
     * Creates a factory to create a JacksonDatabindHandle instance for POJO instances
     * of the specified classes.
     * @param pojoClasses	the POJO classes for which this factory provides a handle
     * @return	the factory
     */
    static public ContentHandleFactory newFactory(Class<?>... pojoClasses) {
        if (pojoClasses == null || pojoClasses.length == 0) return null;
        return new JacksonDatabindHandleFactory(pojoClasses);
    }
    /**
     * Creates a factory to create a JacksonDatabindHandle instance for POJO instances
     * of the specified classes.
     * @param mapper	the Jackson ObjectMapper for marshaling the POJO classes
     * @param pojoClasses	the POJO classes for which this factory provides a handle
     * @return	the factory
     */
    static public ContentHandleFactory newFactory(ObjectMapper mapper, Class<?>... pojoClasses) {
        if (mapper == null || pojoClasses == null || pojoClasses.length == 0) return null;
        return new JacksonDatabindHandleFactory(mapper, pojoClasses);
    }

    /**
     * Specify the type of content this JacksonDatabindHandle will manage.
     * 
     * @param contentClass the class of your custom POJO for databinding
     */
    public JacksonDatabindHandle(Class<T> contentClass) {
        super();
        this.contentClass = contentClass;
           setResendable(true);
    }
    /**
     * Provides a handle on POJO content.
     * @param content    the POJO which should be serialized
     */
	public JacksonDatabindHandle(T content) {
        this((Class<T>) content.getClass());
        set(content);
    }

    /**
     * Specifies the format of the content and returns the handle
     * as a fluent convenience.
     * @param format	the format of the content
     * @return	this handle
     */
    public JacksonDatabindHandle<T> withFormat(Format format) {
        setFormat(format);
        return this;
    }

    /**
     * Returns the content.
     * @return the content you provided if you called {@link #JacksonDatabindHandle(Object)}
     *         or {@link #set(Object)} or if the content is being de-serialized, a pojo of
     *         the specified type populated with the data
     */
    @Override
    public T get() {
        return content;
    }
    /**
     * Assigns your custom POJO as the content.
     * @param content your custom POJO
     */
    @Override
    public void set(T content) {
        this.content = content;
    }
    /**
     * Assigns a your custom POJO as the content and returns the handle.
     * @param content    your custom POJO
     * @return    the handle
     */
    public JacksonDatabindHandle<T> with(T content) {
        set(content);
        return this;
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
        if (content == null)
            return;

        try {
            this.content = (T) getMapper().readValue(
                new InputStreamReader(content, "UTF-8"), contentClass);
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

    static private class JacksonDatabindHandleFactory implements ContentHandleFactory {
        private Class<?>[] contentClasses;
        private ObjectMapper mapper = null;
        private Set<Class<?>> classSet;

        private JacksonDatabindHandleFactory(Class<?>... contentClasses) {
            this(null, contentClasses);
        }

        private JacksonDatabindHandleFactory(ObjectMapper mapper, Class<?>... contentClasses) {
            super();
            this.contentClasses = contentClasses;
            this.mapper = mapper;
            this.classSet = new HashSet<Class<?>>(Arrays.asList(contentClasses));
        }

        @Override
        public Class<?>[] getHandledClasses() {
            return contentClasses;
        }
        @Override
        public boolean isHandled(Class<?> type) {
            return classSet.contains(type);
        }
        @Override
        public <C> ContentHandle<C> newHandle(Class<C> type) {
            if ( ! isHandled(type) ) return null;
            JacksonDatabindHandle<C> handle = new JacksonDatabindHandle<C>(type);
            if ( mapper != null ) handle.setMapper(mapper);
            return handle;
        }
    }
}
