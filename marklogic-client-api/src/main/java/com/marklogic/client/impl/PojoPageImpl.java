/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    JacksonDatabindHandle<T> handle = new JacksonDatabindHandle<>(entityClass);
    handle.getMapper().enableDefaultTyping(
      ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
    return docPage.nextContent(handle).get();
  }

  @Override
  public void close() {
    docPage.close();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
