/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;

/**
 * A Text Document Manager supports database operations on text documents.
 */
public interface TextDocumentManager extends DocumentManager<TextReadHandle, TextWriteHandle> {

}
