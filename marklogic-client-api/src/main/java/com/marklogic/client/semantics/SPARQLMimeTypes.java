/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.semantics;

import com.marklogic.client.io.marker.SPARQLResultsReadHandle;

/**
 * Some static constants to ease use of mime types appropriate for handles of
 * type {@link SPARQLResultsReadHandle} (used in
 * {@link SPARQLQueryManager#executeSelect SPARQLQueryManager.executeSelect}).
 * For more explanation, see {@link SPARQLQueryManager}.
 */
public final class SPARQLMimeTypes {

  public final static String SPARQL_XML    = "application/sparql-results+xml";
  public final static String SPARQL_JSON   = "application/sparql-results+json";
  public final static String SPARQL_CSV    = "text/csv";

}

