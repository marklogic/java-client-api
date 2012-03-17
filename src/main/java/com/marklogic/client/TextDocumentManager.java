package com.marklogic.client;

import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;

/**
 * A Text Document Manager supports database operations on text documents.
 */
public interface TextDocumentManager extends AbstractDocumentManager<TextReadHandle, TextWriteHandle> {

}
