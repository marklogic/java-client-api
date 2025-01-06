/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.impl.FailedRequest;
import com.marklogic.client.io.JSONErrorParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONErrorParserTest {

	private final JSONErrorParser parser = new JSONErrorParser();
	private final ObjectMapper mapper = new ObjectMapper();
	private ObjectNode response;

	@BeforeEach
	void setup() {
		response = mapper.createObjectNode();
	}

	@Test
	void happyPath() {
		response.putObject("errorResponse")
			.put("status", "My status")
			.put("messageCode", "MY_CODE")
			.put("message", "My message")
			.put("stackTrace", "My stacktrace");

		FailedRequest failure = parser.parseFailedRequest(400, new ByteArrayInputStream(response.toPrettyString().getBytes()));
		assertEquals(400, failure.getStatusCode());
		assertEquals("My status", failure.getStatus());
		assertEquals("MY_CODE", failure.getMessageCode());
		assertEquals("My message", failure.getMessage());
		assertEquals("My stacktrace", failure.getStackTrace());
	}

	@Test
	void noErrorResponse() {
		response.putObject("some_other_data");

		FailedRequest failure = parser.parseFailedRequest(500, new ByteArrayInputStream(response.toPrettyString().getBytes()));
		assertEquals(500, failure.getStatusCode());
		assertEquals(
			"Unexpected JSON in response body; did not find 'errorResponse' key; response: {some_other_data={}}",
			failure.getMessage(),
			"In the event that the user mistakenly sends a request to a non-REST-API server but still receives a JSON " +
				"response body, the error should identify the issue and include the JSON response body to help the " +
				"user realize that they likely sent the request to a non-REST-API server."
		);
	}
}
