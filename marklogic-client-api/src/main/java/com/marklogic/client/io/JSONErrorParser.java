/*
 * Copyright 2012-2019 MarkLogic Corporation
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
  @Override
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
      failure.setStackTrace(errorBody.get("stackTrace"));
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
