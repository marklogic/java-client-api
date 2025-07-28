/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.bitemporal.TemporalDocumentManager;
import com.marklogic.client.io.marker.XMLReadHandle;
import com.marklogic.client.io.marker.XMLWriteHandle;

/**
 * A XML Document Manager supports database operations on XML documents.
 */
public interface XMLDocumentManager
extends DocumentManager<XMLReadHandle, XMLWriteHandle>, TemporalDocumentManager<XMLReadHandle, XMLWriteHandle>
{
  /**
   * The DocumentRepair enumeration specifies whether an XML document is repaired as much as possible or not at all.
   */
  public enum DocumentRepair {
    /**
     * Specifies that the server should try all methods for repairing
     * the document when an invalid document is written.
     */
    FULL,
    /**
     * Specifies that the server should not try to repair
     * the document when an invalid document is written.
     */
    NONE;
  }

  /**
   * Returns the repair policy for XML documents written by the manager.
   *
   * @return	the repair policy for written documents
   */
  DocumentRepair getDocumentRepair();
  /**
   * Specifies whether poorly formed XML documents written by the manager
   * should be repaired on the server.
   *
   * @param policy	the repair policy for written documents
   */
  void setDocumentRepair(DocumentRepair policy);

  /**
   * Creates a builder for specifying changes to the content and metadata
   * of an XML document.
   * @return	the patch builder
   */
  DocumentPatchBuilder newPatchBuilder();
}
