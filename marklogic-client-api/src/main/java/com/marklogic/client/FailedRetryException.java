/*
 * Copyright 2012-2018 MarkLogic Corporation
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
package com.marklogic.client;

import com.marklogic.client.impl.FailedRequest;

/**
 * A FailedRetryException is used to capture and report when retry
 * of the request timed out or failed in some other way.
 */
@SuppressWarnings("serial")
public class FailedRetryException extends FailedRequestException {

  public FailedRetryException(String message) {
    super(message);
  }

  public FailedRetryException(String localMessage, Throwable cause) {
    super(localMessage, cause);
  }

  public FailedRetryException(String localMessage, FailedRequest failedRequest) {
    super(localMessage, failedRequest);
  }

}
