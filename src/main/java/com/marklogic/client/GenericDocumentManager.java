package com.marklogic.client;

import com.marklogic.client.docio.GenericReadHandle;
import com.marklogic.client.docio.GenericWriteHandle;

/**
 * A Generic Document Manager supports database operations on documents with an unknown format.
 */
public interface GenericDocumentManager extends AbstractDocumentManager<GenericReadHandle, GenericWriteHandle> {
}
