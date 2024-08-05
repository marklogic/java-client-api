/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.BaseHandle;

import java.lang.reflect.Array;

/**
 * A Content Handle provides get / set access to a representation
 * of content such a byte[] array, input stream, file, reader, string,
 * and so on.
 */
public interface ContentHandle<C>
  extends AbstractReadHandle, AbstractWriteHandle {
  /**
   * Returns the content.
   * @return	the content
   */
  C get();
  /**
   * Assigns the content.
   * @param content	the content
   */
  void set(C content);
  /**
   * Returns the class of the handled content, which may be a base
   * class of the actual class of a content object.
   *
   * Note that implementations should override the default method,
   * which returns null if the handle doesn't have any content.
   *
   * @return  the class for the handled content
   */
  default Class<C> getContentClass() {
    C value = get();
    if (value != null)
      return (Class<C>) value.getClass();
    return null;
  }
  /**
   * Constructs a new handle for the same content representation,
   * initializing the new handle with the same format and mime type.
   * @return  the new handle
   */
  default ContentHandle<C> newHandle() {
    ContentHandle<C> newHandle = null;

    try {
      Class<C> contentClass = getContentClass();
      if (contentClass != null && DatabaseClientFactory.getHandleRegistry().isRegistered(contentClass)) {
        newHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(contentClass);
      }

      if (newHandle == null) {
        newHandle = getClass().newInstance();
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "Error constructing ContentHandle with DatabaseClientFactory.HandleFactoryRegistry or zero-argument constructor",
          e
      );
    }
    if (newHandle == null) {
      throw new RuntimeException(
          "Could not construct ContentHandle with DatabaseClientFactory.HandleFactoryRegistry or zero-argument constructor"
      );
    }

    if (!(this instanceof BaseHandle)) {
      throw new IllegalArgumentException("ContentHandle must also be BaseHandle");
    }
    BaseHandle<?,?> thisBaseHandle = (BaseHandle<?,?>) this;
    BaseHandle<?,?> newBaseHandle = (BaseHandle<?,?>) newHandle;
    newBaseHandle.setFormat(thisBaseHandle.getFormat());
    newBaseHandle.setMimetype(thisBaseHandle.getMimetype());

    return newHandle;
  }

  /**
   * Constructs a new handle for the same content representation,
   * initializing the new handle with the same format and mime type
   * and new content.
   * @param content  the new content to initialize the new handle
   * @return  the new handle
   */
  default ContentHandle<C> newHandle(C content) {
    ContentHandle<C> handle = newHandle();
    handle.set(content);
    return handle;
  }

  /**
   * Constructs an array for the handled content representation
   * @param length the size of the array (zero or more)
   * @return the constructed array
   */
  @SuppressWarnings("unchecked")
  default C[] newArray(int length) {
    if (length < 0) throw new IllegalArgumentException("array length less than zero: "+length);
    return (C[]) Array.newInstance(getContentClass(), length);
  }
}
