package com.marklogic.client.impl;

import java.util.Iterator;

import com.marklogic.client.Page;
import com.marklogic.client.impl.BasicPage;
import com.marklogic.client.io.JacksonPojoHandle;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.pojo.PojoPage;

public class PojoPageImpl<T> extends BasicPage<T> implements PojoPage<T>, Iterator<T> {
    private Class<T> entityClass;
    private DocumentPage docPage;

    public PojoPageImpl(DocumentPage docPage, Class<T> entityClass) {
        super(entityClass);
        setStart( docPage.getStart() );
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
        return docPage.nextContent(new JacksonPojoHandle<T>(entityClass)).get();
    }
}
