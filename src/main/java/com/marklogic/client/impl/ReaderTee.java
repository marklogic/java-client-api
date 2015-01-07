/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.CharBuffer;

public class ReaderTee extends Reader {
	private Reader             in;
	private OutputStreamWriter tee;
	private long               max  = 0;
	private long               sent = 0;

	public ReaderTee(Reader in, OutputStream tee, long max) {
		super();
		this.in  = in;
		this.tee = new OutputStreamWriter(tee);
		this.max = max;
	}

	@Override
	public int read() throws IOException {
		if (in == null)
			return -1;

		if (sent >= max)
			return in.read();

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
		if (sent == max)
			cleanupTee();

		return b;
	}
	@Override
	public int read(char[] cbuf) throws IOException {
		if (in == null)
			return -1;

		if (sent >= max)
			return in.read(cbuf);

		int resultLen = in.read(cbuf);
		if (resultLen < 1) {
			if (resultLen == -1)
				cleanupTee();
			return resultLen;
		}
		
		return readTee(cbuf, 0, resultLen);
	}
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (in == null)
			return -1;

		if (sent >= max)
			return in.read(cbuf, off, len);

		int resultLen = in.read(cbuf, off, len);
		if (resultLen < 1) {
			if (resultLen == -1)
				cleanupTee();
			return resultLen;
		}
		
		return readTee(cbuf, off, resultLen);
	}
	@Override
	public int read(CharBuffer target) throws IOException {
		if (in == null)
			return -1;

		if (sent >= max)
			return in.read(target);

		int resultLen = in.read(target);
		if (resultLen < 1) {
			if (resultLen == -1)
				cleanupTee();
			return resultLen;
		}

		char[] cbuf = new char[resultLen];
		target.get(cbuf);

		return readTee(cbuf, 0, resultLen);
	}
	private int readTee(char[] cbuf, int off, int resultLen) throws IOException {
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
		if (tee == null)
			return;

		tee.flush();
		tee = null;
	}
	@Override
	public void close() throws IOException {
		if (in == null)
			return;

		in.close();
		in = null;

		cleanupTee();
	}

	@Override
	public boolean ready() throws IOException {
		if (in == null)
			return true;

		return in.ready();
	}
	@Override
	public boolean markSupported() {
		if (in == null)
			return false;

		return in.markSupported();
	}
	@Override
	public void mark(int readAheadLimit) throws IOException {
		if (in == null)
			return;

		in.mark(readAheadLimit);
	}
	@Override
	public void reset() throws IOException {
		if (in == null)
			throw new IOException("Reader closed");

		in.reset();
	}
	@Override
	public long skip(long n) throws IOException {
		if (in == null)
			throw new IOException("Reader closed");

		return in.skip(n);
	}
}
