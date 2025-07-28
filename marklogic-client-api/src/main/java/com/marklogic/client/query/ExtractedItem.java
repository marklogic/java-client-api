/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.query;

import com.marklogic.client.io.marker.StructureReadHandle;

/** Access the extracted XML or JSON node using any StructureReadHandle
 * or class registered by a ContentHandle. */
public interface ExtractedItem {
  /** Get the item using the specified handle.
   * @param handle a handle to populate
   * @param <T> the type of StructureReadHandle to return
   * @return the item represented by this instance
   */
  <T extends StructureReadHandle> T get(T handle);

  /** Get the item using the handle registered for the specified class.
   * The IO class must have been registered before creating the database client.
   * By default, the provided handles that implement
   * {@link com.marklogic.client.io.marker.ContentHandle ContentHandle} are registered.
   *
   * <a href="../../../../overview-summary.html#ShortcutMethods">Learn more about shortcut methods</a>
   *
   * @param as a Class type that has been registered by a handle
   * @param <T> the type of object that will be returned by the handle registered for it
   * @return the item represented by this instance
   */
  <T> T getAs(Class<T> as);
}

