/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.InputStream;

/**
 * Defines a class that knows how to construct and return a FailedRequest object.
 *
 */
public interface FailedRequestParser {

  FailedRequest parseFailedRequest(int httpStatus, InputStream is);

}
