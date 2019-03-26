/*
 * Copyright 2014-2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

