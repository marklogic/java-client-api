/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.io.Format;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.marker.TextReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;

class TextDocumentImpl
  extends DocumentManagerImpl<TextReadHandle, TextWriteHandle>
  implements TextDocumentManager
{
  TextDocumentImpl(RESTServices services) {
    super(services, Format.TEXT);
  }
}
