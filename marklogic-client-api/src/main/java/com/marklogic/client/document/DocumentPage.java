/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.Page;
import com.marklogic.client.io.marker.AbstractReadHandle;

import java.io.Closeable;

/** Allows iteration over documents in the page as {@link DocumentRecord} instances.
 * <pre>{@code
 *long start = 1;
 *DocumentPage page = documentManager.search(query, start);
 *try {
 *    for (DocumentRecord record : page) {
 *        String uri = record.getUri();
 *        // ... do something ...
 *    }
 *} finally {
 *    page.close();
 *}
 *}</pre>
 * <b>NOTICE!</b> When you finish with this instance
 * you must call close() to free the underlying resources.
 */
public interface DocumentPage extends Page<DocumentRecord>, Closeable {
  /** Convenience method combines the functionality of Page.next() and DocumentRecord.getContent().
   * @param contentHandle the handle top populate with the contents from the next document
   * @param <T> the type of AbstractReadHandle to return
   * @return the contents of the next document
   */
  <T extends AbstractReadHandle> T nextContent(T contentHandle);
  /** Frees the underlying resources, including the http connection. */
  @Override
  void close();
}
