/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface IoUtil {

	/**
	 * Tossing this commonly used logic here so that it can be reused. Can be removed when we drop Java 8 support, as
	 * Java 9+ has a "readAllBytes" method.
	 */
	static byte[] streamToBytes(InputStream stream) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] b = new byte[8192];
		int len = 0;
		while ((len = stream.read(b)) != -1) {
			buffer.write(b, 0, len);
		}
		buffer.flush();
		return buffer.toByteArray();
	}

	static void closeQuietly(Closeable closeable) {
		// Reinvented here as we don't yet have a dependency on a 3rd party library that provides this method, and it's
		// not worth bringing in a dependency just for this.
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				LoggerFactory.getLogger(IoUtil.class)
					.warn("Unexpected exception while closing stream: %s".formatted(e.getMessage()), e);
			}
		}
	}
}
