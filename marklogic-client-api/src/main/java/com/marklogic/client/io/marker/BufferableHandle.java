/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

/**
 * A Bufferable Handle can read content from a byte[] buffer or write content
 * as a byte[] buffer without changing the state of an external resource.
 * Handles that modify an external resource, such as FileHandle, cannot
 * implement BufferableHandle.
 *
 * For JSON, text, or XML content, the byte array must be encoded in UTF-8.
 * Where possible, the handle uses the byte array directly as a backing store.
 * Otherwise, the handle makes a copy into its own store from the byte array.
 */
public interface BufferableHandle extends AbstractWriteHandle, AbstractReadHandle {
  /**
   * Sets the content of the handle by copying from a byte array buffer
   * encoded in UTF-8.
   * @param buffer	the byte array
   */
  void fromBuffer(byte[] buffer);
  /**
   * Copies the content of the handle to a byte array buffer
   * encoded in UTF-8.
   * @return	the byte array
   */
  byte[] toBuffer();
}
