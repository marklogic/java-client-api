/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamTee extends OutputStream {
  private OutputStream out;
  private OutputStream tee;
  private long         max  = 0;
  private long         sent = 0;

  public OutputStreamTee(OutputStream out, OutputStream tee, long max) {
    super();
    this.out = out;
    this.tee = tee;
    this.max = max;
  }

  @Override
  public void write(int b) throws IOException {
    if (out == null)
      throw new IOException("Output Stream closed");

    out.write(b);

    if (max == Long.MAX_VALUE) {
      tee.write(b);
      return;
    } else if (sent >= max) {
      return;
    }

    tee.write(b);

    sent++;
    if (sent == max)
      cleanupTee();
  }
  @Override
  public void write(byte[] b) throws IOException {
    if (out == null)
      throw new IOException("Output Stream closed");

    out.write(b);

    writeTee(b, 0, b.length);
  }
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (out == null)
      throw new IOException("Output Stream closed");

    out.write(b, off, len);

    writeTee(b, off, len);
  }
  private void writeTee(byte[] b, int off, int len) throws IOException {
    if (max == Long.MAX_VALUE) {
      tee.write(b, off, len);
      return;
    } else if (sent >= max) {
      return;
    }

    int teeLen = ((sent + len) <= max) ? len : (int) (max - sent);
    sent += teeLen;
    tee.write(b, off, teeLen);

    if (sent >= max)
      cleanupTee();
  }

  @Override
  public void flush() throws IOException {
    if (out == null)
      return;

    out.flush();

    if (tee != null) {
      tee.flush();
    }
  }
  @Override
  public void close() throws IOException {
    if (out == null)
      return;

    out.close();
    out = null;

    cleanupTee();
  }
  private void cleanupTee() throws IOException {
    if (tee == null)
      return;

    tee.flush();
    tee = null;
  }
}
