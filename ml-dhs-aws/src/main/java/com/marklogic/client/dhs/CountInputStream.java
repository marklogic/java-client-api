package com.marklogic.client.dhs;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CountInputStream extends FilterInputStream {
	public CountInputStream(InputStream inputStream) throws FileNotFoundException {
		super(inputStream);
		this.byteCount = 0;
	}

	private int byteCount;

	public int getByteCount() {
		return byteCount;
	}

	private int addToByteCount(int readLength) {
		if (readLength > 0)
			byteCount += readLength;
		return readLength;
	}

	@Override
	public int read() throws IOException {
		int readLength = super.read();
		if (readLength > 0)
			byteCount += readLength;
		return readLength;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return addToByteCount(super.read(b));
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return addToByteCount(super.read(b, off, len));
	}
}