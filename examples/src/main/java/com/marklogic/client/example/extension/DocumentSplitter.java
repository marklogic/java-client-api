/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.extension;

import org.w3c.dom.Element;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;
import com.marklogic.client.util.RequestParameters;

/**
 * DocumentSplitter provides an extension for splitting an input XML document
 * into multiple documents.  The root element for each split document must have
 * a "uri" attribute in the "http://marklogic.com/rest-api" namespace declaring
 * the URI for the document.
 */
public class DocumentSplitter extends ResourceManager {
  static final public String NAME = "docsplit";

  public DocumentSplitter(DatabaseClient client) {
    super();
    client.init(NAME, this);
  }

  public int split(XMLWriteHandle inputHandle) {
    if (inputHandle == null)
      throw new IllegalArgumentException("No input handle");

    DOMHandle errorHandle = new DOMHandle();
    getServices().post(
      new RequestParameters(), inputHandle, errorHandle
    );

    Element result = errorHandle.get().getDocumentElement();
    if (!"split-docs".equals(result.getLocalName()))
      throw new FailedRequestException(errorHandle.toString());

    return Integer.parseInt(result.getTextContent());
  }
}
