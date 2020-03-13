package com.marklogic.client.datamovement;

public interface RowBatchRequestEvent extends BatchEvent {
    long getLowerBound();
    long getUpperBound();
}
