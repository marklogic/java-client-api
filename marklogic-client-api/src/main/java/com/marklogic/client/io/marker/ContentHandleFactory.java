/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

/**
 * A ContentHandleFactory creates instances of a ContentHandle that provides
 * an adapter for an IO representation.
 * @see ContentHandle
 */
public interface ContentHandleFactory {
  /**
   * Returns classes that the handle always supports;
   * @return	the classes if any
   */
  Class<?>[] getHandledClasses();
  /**
   * Returns true if the factory can create a handle for instances
   * of the IO class.
   * @param type	the class of the IO representation
   * @return	whether the factory can create a handle
   */
  boolean isHandled(Class<?> type);
  /**
   * Instantiates a handle for an IO class recognized by the factory.
   * @param type	the class of the IO representation
   * @param <C> the registered type which will be handled by the returned
   *            ContentHandle
   * @return	the handle or null if the class is unrecognized
   */
  <C> ContentHandle<C> newHandle(Class<C> type);
}
