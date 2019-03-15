package com.marklogic.client.datamovement;

public interface CallManyEvent<R> extends CallEvent, Batch<R> {
    R[] getItems();
}
