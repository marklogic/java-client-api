/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.util.RequestLogger;

class RequestLoggerImpl implements RequestLogger {
  static final private Logger logger = LoggerFactory.getLogger(RequestLoggerImpl.class);

  private PrintStream out;
  private boolean     enabled    = true;
  private long        contentMax = NO_CONTENT;

  RequestLoggerImpl(OutputStream out) {
    if (out == null)
      this.out = System.out;
    else if (out instanceof PrintStream)
      this.out = (PrintStream) out;
    else
      this.out = new PrintStream(out);
  }

  @Override
  public long getContentMax() {
    return contentMax;
  }
  @Override
  public void setContentMax(long max) {
    this.contentMax = max;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
  @Override
  public void setEnabled(boolean enabled) {
    if (out == null && enabled == true)
      throw new IllegalStateException("Cannot enable closed request logger");
    this.enabled = enabled;
  }

  @Override
  public PrintStream getPrintStream() {
    return (enabled) ? out : null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T copyContent(T content) {
    if (content == null)
      return content;

    long max = getContentMax();
    if (max < 1)
      return content;

    PrintStream out = getPrintStream();
    if (out == null)
      return content;

    if (content instanceof byte[]) {
      byte[] b = (byte[]) content;
      out.write(b, 0, (int) Math.min(b.length, max));
      return content;
    }

    if (content instanceof File) {
      out.println("info: cannot copy content from "+
        ((File) content).getAbsolutePath());
      return content;
    }

    if (content instanceof InputStream) {
      return (T) new InputStreamTee((InputStream) content, out, max);
    }

    if (content instanceof Reader) {
      return (T) new ReaderTee((Reader) content, out, max);
    }

    if (content instanceof String) {
      String s = (String) content;
      int len = s.length();
      if (len <= max)
        out.print(s);
      else
        out.print(s.substring(0, (int) Math.min(len, max)));
      return content;
    }

    if (logger.isWarnEnabled())
      logger.warn("Unknown {} class for content", content.getClass().getName());
    return content;
  }

  @Override
  public void flush() {
    if (out == null) return;
    out.flush();
  }
  @Override
  public void close() {
    if (out == null) return;
    out.close();
    out = null;
    enabled = false;
  }

	// Moved here in the 8.2 release to make it obvious it's only used by RequestLoggerImpl. It is only used by the
	// copyContent method in RequestLoggerImpl. So while Polaris rightfully complains about the thread safety issues of
	// this class, it is only used in a single thread context and so the thread safety issues are not a problem.
	private static class InputStreamTee extends InputStream {
		private InputStream in;
		private OutputStream tee;
		private long max = 0;
		private long sent = 0;

		public InputStreamTee(InputStream in, OutputStream tee, long max) {
			super();
			this.in = in;
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
		public synchronized void mark(int readLimit) {
			if (in == null) return;
			in.mark(readLimit);
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
}
