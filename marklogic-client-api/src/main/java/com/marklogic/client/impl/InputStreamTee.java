/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamTee extends InputStream {
  private InputStream  in;
  private OutputStream tee;
  private long         max  = 0;
  private long         sent = 0;

  public InputStreamTee(InputStream in, OutputStream tee, long max) {
    super();
    this.in  = in;
    this.tee = tee;
    this.max = max;
  }

  @Override
  public int read() throws IOException {
    if (in == null) return -1;

    if (sent >= max) return in.read();

    int b = in.read();
    if (b == -1) {
      cleanupTee();
      return b;
    }

    if (max == Long.MAX_VALUE) {
      tee.write(b);
      return b;
    }

    tee.write(b);

    sent++;
    if (sent == max) cleanupTee();

    return b;
  }
  @Override
  public int read(byte[] b) throws IOException {
    if (in == null) return -1;

    if (sent >= max) return in.read(b);

    return readTee(b, 0, in.read(b));
  }
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    if (in == null) return -1;

    if (sent >= max) return in.read(b, off, len);

    return readTee(b, 0, in.read(b, off, len));
  }
  private int readTee(byte[] b, int off, int resultLen) throws IOException {
    if (resultLen < 1) {
      if (resultLen == -1) cleanupTee();
      return resultLen;
    }

    if (max == Long.MAX_VALUE) {
      tee.write(b, off, resultLen);
      return resultLen;
    }

    int teeLen = ((sent + resultLen) <= max) ? resultLen : (int) (max - sent);
    sent += teeLen;
    tee.write(b, off, teeLen);

    if (sent >= max) cleanupTee();

    return resultLen;
  }
  private void cleanupTee() throws IOException {
    if (tee == null) return;

    tee.flush();
    tee = null;
  }
  @Override
  public void close() throws IOException {
    if (in == null) return;

    in.close();
    in = null;

    cleanupTee();
  }

  @Override
  public int available() throws IOException {
    if (in == null) return 0;

    return in.available();
  }
  @Override
  public boolean markSupported() {
    if (in == null) return false;

    return in.markSupported();
  }
  @Override
  public synchronized void mark(int readlimit) {
    if (in == null) return;

    in.mark(readlimit);
  }
  @Override
  public synchronized void reset() throws IOException {
    if (in == null) throw new IOException("Input Stream closed");

    in.reset();
  }
  @Override
  public long skip(long n) throws IOException {
    if (in == null) throw new IOException("Input Stream closed");

    return in.skip(n);
  }
}
