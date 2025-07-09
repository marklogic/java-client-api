/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

/**
 * A Structure Read Handle can represent a data structure read from the
 * database.
 *
 */
public interface StructureReadHandle extends DocumentMetadataReadHandle,
  QueryOptionsReadHandle, QueryOptionsListReadHandle, SearchReadHandle,
  ValuesListReadHandle, ValuesReadHandle, TuplesReadHandle,
  RuleListReadHandle, RuleReadHandle {
}
