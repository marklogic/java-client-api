/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.io.Format;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;

public class JSONDocumentImpl
  extends DocumentManagerImpl<JSONReadHandle, JSONWriteHandle>
  implements JSONDocumentManager
{
  JSONDocumentImpl(RESTServices services) {
    super(services,Format.JSON);
  }

  @Override
  public DocumentPatchBuilder newPatchBuilder() {
    return new DocumentPatchBuilderImpl(Format.JSON);
  }

  @Override
  protected RequestParameters getWriteParams() {
    RequestParameters params = new RequestParameters();

    return params;
  }
}
