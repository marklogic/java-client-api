/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

/**
 * A Structure Write Handle represents a query serialized as a structured
 * query, combined query, or query by example.
 */
public interface StructureWriteHandle
  extends DocumentPatchHandle, DocumentMetadataWriteHandle, QueryOptionsWriteHandle, RuleWriteHandle
{
}
