/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.client.test.Common;
import com.marklogic.client.test.junit5.RequiresML12;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RequiresML12.class)
class VectorUtilTest {

	private final float[] VECTOR = new float[]{3.14f, 1.59f, 2.65f};
	private final double ACCEPTABLE_DELTA = 0.0001;

	@Test
	void encodeAndDecodeWithJavaClient() {
		String encoded = VectorUtil.base64Encode(VECTOR);
		assertEquals("AAAAAAMAAADD9UhAH4XLP5qZKUA=", encoded);

		float[] decoded = VectorUtil.base64Decode(encoded);
		assertEquals(VECTOR.length, decoded.length);
		for (int i = 0; i < VECTOR.length; i++) {
			assertEquals(VECTOR[i], decoded[i], ACCEPTABLE_DELTA);
		}
	}

	@Test
	void encodeAndDecodeWithServer() {
		String encoded = VectorUtil.base64Encode(VECTOR);
		assertEquals("AAAAAAMAAADD9UhAH4XLP5qZKUA=", encoded);

		ArrayNode decoded = (ArrayNode) Common.newEvalClient().newServerEval()
			.xquery(String.format("vec:base64-decode('%s')", encoded))
			.evalAs(JsonNode.class);

		assertEquals(3, decoded.size());
		assertEquals(3.14f, decoded.get(0).asDouble(), ACCEPTABLE_DELTA);
		assertEquals(1.59f, decoded.get(1).asDouble(), ACCEPTABLE_DELTA);
		assertEquals(2.65f, decoded.get(2).asDouble(), ACCEPTABLE_DELTA);
	}

	@Test
	void encodeWithServerAndDecodeWithJavaClient() {
		String encoded = Common.newEvalClient().newServerEval()
			.xquery("vec:base64-encode(vec:vector((3.14, 1.59, 2.65)))")
			.evalAs(String.class);
		assertEquals("AAAAAAMAAADD9UhAH4XLP5qZKUA=", encoded);

		float[] decoded = VectorUtil.base64Decode(encoded);
		assertEquals(VECTOR.length, decoded.length);
		for (int i = 0; i < VECTOR.length; i++) {
			assertEquals(VECTOR[i], decoded[i], ACCEPTABLE_DELTA);
		}
	}
}
