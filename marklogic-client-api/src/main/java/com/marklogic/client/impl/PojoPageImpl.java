/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
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
    handle.getMapper().activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
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
