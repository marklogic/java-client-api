/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import jakarta.activation.DataSource;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A streaming DataSource implementation that wraps an InputStream without buffering it into memory.
 * This is a critical component for enabling true streaming of large documents from MarkLogic,
 * avoiding OutOfMemoryErrors when processing large result sets.
 * <p>
 * Unlike ByteArrayDataSource (which loads the entire stream into a byte array), this implementation
 * preserves the streaming nature of the underlying InputStream, allowing documents to be processed
 * incrementally as they are read from the network.
 * <p>
 * Note: This DataSource is read-only. The getOutputStream() method throws UnsupportedOperationException.
 */
public class InputStreamDataSource implements DataSource {

	private final InputStream inputStream;
	private final String contentType;

	/**
	 * Creates a new InputStreamDataSource.
	 *
	 * @param inputStream the InputStream to wrap (will not be buffered into memory)
	 * @param contentType the MIME type of the data
	 */
	public InputStreamDataSource(InputStream inputStream, String contentType) {
		this.inputStream = inputStream;
		this.contentType = contentType;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		throw new UnsupportedOperationException("InputStreamDataSource is read-only");
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getName() {
		return null;
	}
}
