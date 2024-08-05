/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An Output Stream Sender sends content to the database
 * by writing to the provided OutputStream.
 *
 * When writing JSON, text, or XML content, you should use an OutputStream only
 * only to write bytes for characters encoded in UTF-8.  If the bytes provide
 * characters with a different encoding, convert the bytes using
 * the java.nio.charset.CharsetDecoder class.
 */
public interface OutputStreamSender {
  /**
   * Implements a callback to write content to the provided output stream
   * for sending to the database server.
   * @param out	the output stream receiving the content
   * @throws IOException if io problems arise
   */
  void write(OutputStream out) throws IOException;
}
