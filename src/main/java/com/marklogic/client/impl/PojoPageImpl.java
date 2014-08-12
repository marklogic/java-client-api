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

import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.Page;
import com.marklogic.client.impl.BasicPage;
import com.marklogic.client.io.JacksonDatabindHandle;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.pojo.PojoPage;

public class PojoPageImpl<T> extends BasicPage<T> implements PojoPage<T>, Iterator<T> {
    private Class<T> entityClass;
    private DocumentPage docPage;

    public PojoPageImpl(DocumentPage docPage, Class<T> entityClass) {
        super(entityClass);
        setStart( docPage.getStart() );
        setSize( docPage.size() );
        setPageSize( docPage.getPageSize() );
        setTotalSize( docPage.getTotalSize() );

        this.docPage = docPage;
        this.entityClass = entityClass;
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return docPage.hasNext();
    }

    @Override
    public T next() {
        JacksonDatabindHandle<T> handle = new JacksonDatabindHandle<T>(entityClass);
        handle.getMapper().enableDefaultTyping(
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
        return docPage.nextContent(handle).get();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
