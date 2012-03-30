package com.marklogic.client.impl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamTee extends FilterOutputStream {
	private OutputStream tee;
	private long         max  = 0;
	private long         sent = 0;
	private byte[]       buf  = new byte[1024];
	private int          next = 0;
	private boolean      did  = false;

	public OutputStreamTee(OutputStream out, OutputStream tee, long max) {
		super(out);
		this.tee = tee;
		this.max = max;
	}

	@Override
	public void write(int b) throws IOException {
		if (did) {
			super.write(b);
			return;
		}

		buf[next] = (byte) b;
		next++;
		if (next > buf.length) {
			flushBuffer();
		}

		did = true;
		super.write(b);
		did = false;
	}
	@Override
	public void write(byte[] b) throws IOException {
		if (did) {
			super.write(b);
			return;
		}

		write(b, 0, b.length);
	}
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (did) {
			super.write(b, off, len);
			return;
		}

		if (next > 0) {
			flushBuffer();
		}

		did = true;
		super.write(b, off, len);
		did = false;

		if (max == Long.MAX_VALUE) {
			tee.write(b, off, len);
			return;
		}

		int teeLen = ((sent + len) <= max) ? len : (int) (max - sent);
		sent += teeLen;
		tee.write(b, off, teeLen);

		if (sent >= max)
			cleanupTee();
	}
	private void flushBuffer() throws IOException {
		if (next == 0)
			return;

		int len = Math.min(next, buf.length);
		next = 0;
		write(buf, 0, len);
	}

	@Override
	public void flush() throws IOException {
		if (tee != null) {
			if (next > 0) {
				flushBuffer();
			}
			tee.flush();
		}

		did = true;
		super.flush();
		did = false;
	}
	@Override
	public void close() throws IOException {
		if (tee != null) {
			cleanupTee();
		}

		did = true;
		super.close();
		did = false;
	}
	private void cleanupTee() throws IOException {
		flush();
		tee = null;
	}
}
