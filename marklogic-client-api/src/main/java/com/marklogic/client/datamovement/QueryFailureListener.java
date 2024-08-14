/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement;

/**
 * A listener which can process an exception which occurred when attempting to
 * retrieve a batch of matches to a query.
 */
public interface QueryFailureListener extends FailureListener<QueryBatchException> {
  void processFailure(QueryBatchException failure);
}
