/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.io.marker;

import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryManager;

/**
 * A marker interface for handles capable of reading SPARQL results
 * (returned by {@link SPARQLQueryManager#executeSelect SPARQLQueryManager.executeSelect}).
 * Handles of this type should accept one of the {@link SPARQLMimeTypes}.
 */
public interface SPARQLResultsReadHandle extends AbstractReadHandle {
}
