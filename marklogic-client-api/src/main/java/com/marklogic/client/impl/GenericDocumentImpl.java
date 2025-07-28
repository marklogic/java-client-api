/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.io.Format;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.marker.GenericReadHandle;
import com.marklogic.client.io.marker.GenericWriteHandle;

public class GenericDocumentImpl
  extends DocumentManagerImpl<GenericReadHandle, GenericWriteHandle>
  implements GenericDocumentManager
{
  GenericDocumentImpl(RESTServices services) {
    super(services, Format.UNKNOWN);
  }

}
