/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
