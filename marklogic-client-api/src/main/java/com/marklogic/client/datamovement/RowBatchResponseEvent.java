package com.marklogic.client.datamovement;

import com.marklogic.client.io.marker.StructureReadHandle;

public interface RowBatchResponseEvent<T extends StructureReadHandle> extends RowBatchRequestEvent {
    T getRowsDoc(T handle);
}
