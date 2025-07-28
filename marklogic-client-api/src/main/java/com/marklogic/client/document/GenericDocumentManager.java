/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.bitemporal.TemporalDocumentManager;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;

/**
 * A Generic Document Manager supports database operations on documents with an unknown format.
 *
 * As of the 6.6.0 release, this now extends {@code TemporalDocumentManager}, which has long been possible since the
 * one implementation of this interface - {@code GenericDocumentImpl} - already implements the methods in the temporal
 * interface.
 */
public interface GenericDocumentManager extends DocumentManager<GenericReadHandle, GenericWriteHandle>,
	TemporalDocumentManager<GenericReadHandle, GenericWriteHandle> {
}
