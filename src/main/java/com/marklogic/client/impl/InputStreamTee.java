package com.marklogic.client.impl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamTee extends FilterInputStream {
	private OutputStream tee;
	private long         max  = 0;
	private long         sent = 0;

	public InputStreamTee(InputStream in, OutputStream tee, long max) {
		super(in);
		this.tee = tee;
		this.max = max;
	}

	@Override
	public int read() throws IOException {
		if (sent >= max)
			return super.read();

		int b = super.read();
		if (b == -1) {
			cleanupTee();
			return b;
		}

		if (max == Long.MAX_VALUE) {
			tee.write(b);
			return b;
		}

		sent++;
		tee.write(b);

		if (sent >= max)
			cleanupTee();

		return b;
	}
	@Override
	public int read(byte[] b) throws IOException {
		if (sent >= max)
			return super.read(b);

		return read(b, 0, b.length);
	}
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (sent >= max)
			return super.read(b, off, len);

		int resultLen = super.read(b, off, len);
		if (resultLen < 1) {
			if (resultLen == -1)
				cleanupTee();
			return resultLen;
		}

		if (max == Long.MAX_VALUE) {
			tee.write(b, off, resultLen);
			return resultLen;
		}

		int teeLen = ((sent + resultLen) <= max) ? resultLen : (int) (max - sent);
		sent += teeLen;
		tee.write(b, off, teeLen);

		if (sent >= max)
			cleanupTee();

		return resultLen;
	}
	private void cleanupTee() throws IOException {
		if (tee != null) {
			tee.flush();
			tee = null;
		}
	}
}
