/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
