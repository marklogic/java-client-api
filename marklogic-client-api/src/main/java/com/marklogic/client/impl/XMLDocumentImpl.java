/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.io.Format;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.util.RequestParameters;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

class XMLDocumentImpl
  extends DocumentManagerImpl<XMLReadHandle, XMLWriteHandle>
  implements XMLDocumentManager
{
  private DocumentRepair repair;

  XMLDocumentImpl(RESTServices services) {
    super(services, Format.XML);
  }

  @Override
  public DocumentRepair getDocumentRepair() {
    return repair;
  }
  @Override
  public void setDocumentRepair(DocumentRepair policy) {
    repair = policy;
  }

  @Override
  public DocumentPatchBuilder newPatchBuilder() {
    return new DocumentPatchBuilderImpl(Format.XML);
  }

  @Override
  protected RequestParameters getWriteParams() {
    if (repair == null)
      return null;

    RequestParameters params = new RequestParameters();
    if (repair == DocumentRepair.FULL)
      params.put("repair", "full");
    else if (repair == DocumentRepair.NONE)
      params.put("repair", "none");
    else
      throw new MarkLogicInternalException("Internal error - unknown repair policy: "+repair.name());

    return params;
  }
}
