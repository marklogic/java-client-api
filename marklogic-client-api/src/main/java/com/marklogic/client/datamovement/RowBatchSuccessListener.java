package com.marklogic.client.datamovement;

import com.marklogic.client.io.marker.StructureReadHandle;

public interface RowBatchSuccessListener<T extends StructureReadHandle> extends BatchListener<RowBatchResponseEvent<T>> {
}
