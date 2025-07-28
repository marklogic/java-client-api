/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.semantics;

/**
 * <p>The permission capabilities available for operations on graphs.  Used in
 * {@link GraphManager#permission}, {@link SPARQLQueryManager#permission}, and
 * {@link GraphPermissions#permission}.  See usage examples in javadocs for
 * {@link GraphManager} and {@link SPARQLQueryManager}.</p>
 *
 * <p>For details about RDF, SPARQL, and semantics in MarkLogic see
 * <a href="https://docs.marklogic.com/guide/semantics" target="_top">Semantics Developer's Guide</a>
 */
public enum Capability { READ, UPDATE, EXECUTE; }
