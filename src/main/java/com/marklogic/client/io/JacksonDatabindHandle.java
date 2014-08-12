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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.impl.JacksonBaseHandle;

/**
 * An adapter for using the Jackson Open Source library for JSON; represents 
 * JSON content as a Jackson JsonNode for reading or writing.  Enables reading and 
 * writing JSON documents, JSON structured search, and other JSON input and output.  
 */
public class JacksonDatabindHandle<T>
        extends JacksonBaseHandle<T>
        implements ContentHandle<T>
{
    private Class contentClass;
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
     * @param contentClass the class of your custom Pojo for databinding
     */
    public JacksonDatabindHandle(Class<T> contentClass) {
        super();
        this.contentClass = contentClass;
           setResendable(true);
    }
    /**
     * Provides a handle on JSON content as a Jackson tree.
     * @param content    the JSON root node of the tree.
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
    public JacksonDatabindHandle withFormat(Format format) {
        setFormat(format);
        return this;
    }

    /**
     * Returns the root node of the JSON tree.
     * @return    the JSON root node.
     */
    @Override
    public T get() {
        return content;
    }
    /**
     * Assigns your custom Pojo as the content.
     * @param content your custom Pojo
     */
    @Override
    public void set(T content) {
        this.content = content;
    }
    /**
     * Assigns a JSON tree as the content and returns the handle.
     * @param content    the JSON root node.
     * @return    the handle on the JSON tree.
     */
    public JacksonDatabindHandle<T> with(T content) {
        set(content);
        return this;
    }

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
            JacksonDatabindHandle handle = new JacksonDatabindHandle<C>(type);
            if ( mapper != null ) handle.setMapper(mapper);
            return handle;
        }
    }
}
