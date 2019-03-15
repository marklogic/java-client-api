package com.marklogic.client.dataservices;

import com.marklogic.client.datamovement.Batch;

public interface CallManyEvent<R> extends CallEvent, Batch<R> {
    R[] getItems();
}
