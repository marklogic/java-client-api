/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.document;

import com.marklogic.client.io.Format;

/**
 * A Content Descriptor identifies the properties of some content.
 */
public interface ContentDescriptor {
  /**
   * Indicates that the length of the content is not known.
   */
  long UNKNOWN_LENGTH = -1;

  /**
   * Returns the format of the content.
   * @return	the content format
   */
  Format getFormat();
  /**
   * Specifies the format of the content as binary, JSON, text, or XML.
   * @param format	the format of the content
   */
  void setFormat(Format format);

  /**
   * Returns the mimetype of the content.
   * @return	the content mimetype
   */
  String getMimetype();
  /**
   * Specifies the mimetype of the content such as application/json,
   * text/plain, or application/xml.
   * @param mimetype	the content mimetype
   */
  void setMimetype(String mimetype);

  /**
   * Returns the length of the content in bytes as returned by the server.
   * This value is usually not set when reading from a local source (such as a
   * file or an input stream).  The byte length won't be returned by the server
   * for very large documents for performance reasons.  The byte length can be
   * larger than the character length if the content contains multi-byte
   * characters.
   * @return	the content length in bytes
   */
  long getByteLength();
  /**
   * Specifies the length of the content in bytes or the UNKNOWN_LENGTH
   * constant if the length of the content is not known.
   * @param length	the content length in bytes
   */
  void setByteLength(long length);
}
