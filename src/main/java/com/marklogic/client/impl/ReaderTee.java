package com.marklogic.client.impl;

import java.io.FilterReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class ReaderTee extends FilterReader {
	private OutputStreamWriter tee;
	private long               max  = 0;
	private long               sent = 0;

	public ReaderTee(Reader in, OutputStream tee, long max) {
		super(in);
		this.tee = new OutputStreamWriter(tee);
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
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (sent >= max)
			return super.read(cbuf, off, len);

		int resultLen = super.read(cbuf, off, len);
		if (resultLen < 1) {
			if (resultLen == -1)
				cleanupTee();
			return resultLen;
		}

		if (max == Long.MAX_VALUE) {
			tee.write(cbuf, off, resultLen);
			return resultLen;
		}

		int teeLen = ((sent + resultLen) <= max) ? resultLen : (int) (max - sent);
		sent += teeLen;
		tee.write(cbuf, off, teeLen);

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
