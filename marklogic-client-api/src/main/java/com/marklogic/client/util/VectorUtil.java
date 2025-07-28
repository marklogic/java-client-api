/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

/**
 * Supports encoding and decoding vectors using the same approach as the vec:base64-encode and vec:base64-decode
 * functions supported by the MarkLogic server.
 *
 * @since 7.2.0
 */
public interface VectorUtil {

	/**
	 * @param vector
	 * @return a base64-encoded string representing the vector and using the same approach as the vec:base64-encode
	 * function supported by the MarkLogic server.
	 */
	static String base64Encode(float... vector) {
		final int dimensions = vector.length;
		ByteBuffer buffer = ByteBuffer.allocate(8 + 4 * dimensions);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(0); // version
		buffer.putInt(dimensions);
		for (float v : vector) {
			buffer.putFloat(v);
		}
		return Base64.getEncoder().encodeToString(buffer.array());
	}

	/**
	 * @param encodedVector
	 * @return a vector represented by the base64-encoded string and using the same approach as the vec:base64-decode
	 * function supported by the MarkLogic server.
	 */
	static float[] base64Decode(String encodedVector) {
		ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(encodedVector));
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		final int version = buffer.getInt();
		if (version != 0) {
			throw new IllegalArgumentException("Unsupported vector version: " + version);
		}

		final int dimensions = buffer.getInt();
		float[] vector = new float[dimensions];
		for (int i = 0; i < dimensions; i++) {
			vector[i] = buffer.getFloat();
		}
		return vector;
	}
}
