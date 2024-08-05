/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import com.marklogic.client.impl.HandleImplementation;

/**
 * BaseHandle is the base class for content representations
 * such as byte arrays, strings, input streams, character readers,
 * files, POJO (Plain Old Java Object) structures and so on.
 * Content representations are used for query options, search results, values results,
 * document metadata, and documents in binary, JSON, text, and XML formats.
 * Read handles receive content from the server and must implement the receiveAs() and
 * receiveContent() methods.
 * Write handles send content to the server, must implement the sendContent() method,
 * and should initialize the setResendable() accessor.
 * A handle can support both read and write operations.
 *
 * @param <R>	a read handle or OperationNotSupported in the com.marklogic.client.io.marker package
 * @param <W>	a write handle or OperationNotSupported in the com.marklogic.client.io.marker package
 */
public abstract class BaseHandle<R,W>
  extends HandleImplementation<R,W>
{
  private Format format = Format.UNKNOWN;
  private String mimetype;
  private long length = UNKNOWN_LENGTH;

  /**
   * Zero-argument constructor.
   */
  public BaseHandle() {
    super();
  }

  @Override
  public Format getFormat() {
    return format;
  }
  @Override
  public void setFormat(Format format) {
    this.format = format;
  }

  @Override
  public String getMimetype() {
    if (mimetype == null && format != null)
      return format.getDefaultMimetype();

    return mimetype;
  }
  @Override
  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  @Override
  public long getServerTimestamp() {
    return super.getServerTimestamp();
  }
  public void setServerTimestamp(long serverTimestamp) {
    super.setPointInTimeQueryTimestamp(serverTimestamp);
  }

  @Override
  public long getByteLength() {
    return length;
  }
  @Override
  public void setByteLength(long length) {
    this.length = length;
  }
}
