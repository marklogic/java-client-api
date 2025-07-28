/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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
