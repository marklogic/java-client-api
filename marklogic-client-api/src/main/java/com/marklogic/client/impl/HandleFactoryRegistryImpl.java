/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputSourceHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.XMLEventReaderHandle;
import com.marklogic.client.io.XMLStreamReaderHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.ContentHandleFactory;

public class HandleFactoryRegistryImpl implements HandleFactoryRegistry {
  private Map<Class<?>,ContentHandleFactory> factories =
    new HashMap<>();

  public static HandleFactoryRegistry newDefault() {
    return registerDefaults(new HandleFactoryRegistryImpl());
  }
  public static HandleFactoryRegistry registerDefaults(HandleFactoryRegistry registry) {
    registry.register(BytesHandle.newFactory());
    registry.register(DOMHandle.newFactory());
    registry.register(FileHandle.newFactory());
    registry.register(InputSourceHandle.newFactory());
    registry.register(InputStreamHandle.newFactory());
    registry.register(JacksonHandle.newFactory());
    registry.register(JacksonParserHandle.newFactory());
    registry.register(ReaderHandle.newFactory());
    registry.register(SourceHandle.newFactory());
    registry.register(StringHandle.newFactory());
    registry.register(XMLEventReaderHandle.newFactory());
    registry.register(XMLStreamReaderHandle.newFactory());

    return registry;
  }

  public HandleFactoryRegistryImpl() {
    super();
  }

  @Override
  public void register(ContentHandleFactory factory) {
    if (factory == null)
      throw new IllegalArgumentException("no factory to register");
    register(factory, factory.getHandledClasses());
  }
  @Override
  public void register(ContentHandleFactory factory, Class<?>... types) {
    if (factory == null)
      throw new IllegalArgumentException("no factory to register");
    if (types == null || types.length == 0)
      throw new IllegalArgumentException("no types to register");
    for (Class<?> type: types) {
      factories.put(type, factory);
    }
  }
  @Override
  public boolean isRegistered(Class<?> type) {
    return (getRegisteredType(type) != null);
  }
  @Override
  public Set<Class<?>> listRegistered() {
    return factories.keySet();
  }
  @Override
  public <C> ContentHandle<C> makeHandle(Class<C> type) {
    if (type == null) {
      throw new IllegalArgumentException("Cannot make handle for null class");
    }

    Class<?> registeredType = getRegisteredType(type);
    if (registeredType == null) {
      throw new IllegalArgumentException("No factory for class "+type.getName());
    }

    ContentHandleFactory factory = factories.get(registeredType);
    if (type != registeredType) {
      factories.put(type, factory);
    }

    ContentHandle<C> handle = factory.newHandle(type);
    if (handle == null) {
      throw new IllegalArgumentException("Factory "+factory.getClass().getName()+
        " cannot make handle for class "+type.getName());
    }

    return handle;
  }
  @Override
  public void unregister(Class<?>... types) {
    if (types == null || types.length == 0)
      return;
    for (Class<?> type: types) {
      factories.remove(type);
    }
  }
  @Override
  public HandleFactoryRegistry copy() {
    HandleFactoryRegistryImpl copy = new HandleFactoryRegistryImpl();
    copy.factories.putAll(this.factories);
    return copy;
  }
  Class<?> getRegisteredType(Class<?> type) {
    if ( type == null ) throw new IllegalArgumentException("type must not be null");
    if (factories.containsKey(type)) {
      return type;
    }
    for ( Class<?> registeredClass : factories.keySet() ) {
      if ( registeredClass.isAssignableFrom(type) ) {
        return registeredClass;
      }
    }

    return null;
  }
}
