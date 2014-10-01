package com.marklogic.client.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.impl.FailedRequest;
import com.marklogic.client.impl.FailedRequestParser;

/**
 * This class is provided as a convenience method for parsing MarkLogic errors that
 * are serialized as JSON.  In order to use this class, your project must provide
 * the Jackson data binding library for JSON.
 */
public class JSONErrorParser implements FailedRequestParser {

	@SuppressWarnings("unchecked")
	public FailedRequest parseFailedRequest(int httpStatus, InputStream content) {
		FailedRequest failure = new FailedRequest();
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		Map<String, Map<String, String>> errorData;
		try {
			errorData = mapper.readValue(content, Map.class);
			Map<String, String> errorBody = errorData.get("errorResponse");
			failure.setStatusCode(httpStatus);
			failure.setStatusString(errorBody.get("status"));
			failure.setMessageCode(errorBody.get("messageCode"));
			failure.setMessageString(errorBody.get("message"));
		} catch (JsonParseException e1) {
			failure.setStatusCode(httpStatus);
			failure.setMessageString("Request failed. Error body not received from server");
		} catch (JsonMappingException e1) {
			failure.setStatusCode(httpStatus);
			failure.setMessageString("Request failed. Error body not received from server");
		} catch (IOException e1) {
			failure.setStatusCode(httpStatus);
			failure.setMessageString("Request failed. Error body not received from server");
		}
		return failure;
	}
}
