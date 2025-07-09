/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.bitemporal.TemporalDocumentManager;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

/**
 * A JSON Document Manager supports database operations on JSON documents.
 */
public interface JSONDocumentManager
extends DocumentManager<JSONReadHandle, JSONWriteHandle>, TemporalDocumentManager<JSONReadHandle, JSONWriteHandle>
{
  /**
   * Creates a builder for specifying changes to the content and metadata
   * of a JSON document.
   * @return	the patch builder
   */
  DocumentPatchBuilder newPatchBuilder();
}
