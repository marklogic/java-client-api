/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.ByteArrayOutputStream;
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
}
