/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.impl.FailedRequest;
import com.marklogic.client.impl.FailedRequestParser;

import java.io.InputStream;
import java.util.Map;

public class JSONErrorParser implements FailedRequestParser {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	@Override
	public FailedRequest parseFailedRequest(int httpStatus, InputStream content) {
		Map<String, Map<String, String>> errorData;
		try {
			errorData = objectMapper.readValue(content, Map.class);
		} catch (Exception ex) {
			return new FailedRequest(httpStatus, "Request failed; could not parse JSON in response body.");
		}

		if (!errorData.containsKey("errorResponse")) {
			return new FailedRequest(httpStatus, "Unexpected JSON in response body; did not find 'errorResponse' key; response: " + errorData);
		}

		FailedRequest failure = new FailedRequest();
		Map<String, String> errorBody = errorData.get("errorResponse");
		failure.setStatusCode(httpStatus);
		failure.setStatusString(errorBody.get("status"));
		failure.setMessageCode(errorBody.get("messageCode"));
		failure.setMessageString(errorBody.get("message"));
		failure.setStackTrace(errorBody.get("stackTrace"));
		return failure;
	}
}
