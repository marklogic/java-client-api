/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.io.Format;

/**
 * A DocumentUriTemplate specifies how the server should construct
 * a name for a document.
 */
public interface DocumentUriTemplate extends ContentDescriptor {
  /**
   * Returns the directory that should prefix the document uri.
   * @return	the directory.
   */
  String getDirectory();
  /**
   * Specifies the directory that should prefix the document uri.
   * @param directory	the directory.
   */
  void setDirectory(String directory);
  /**
   * Specifies the directory that should prefix the document uri
   * and returns the template object.
   * @param directory	the directory.
   * @return	the template object.
   */
  DocumentUriTemplate withDirectory(String directory);
  /**
   * Returns the extension that should suffix the document uri.
   * @return	the extension.
   */
  String getExtension();
  /**
   * Specifies the extension that should suffix the document uri.
   * The extension should not start with the period separator.
   * @param extension	the extension.
   */
  void setExtension(String extension);
  /**
   * Specifies the format of the document
   * and returns the template object.
   * @param format	the format.
   * @return	the template object.
   */
  DocumentUriTemplate withFormat(Format format);
}
