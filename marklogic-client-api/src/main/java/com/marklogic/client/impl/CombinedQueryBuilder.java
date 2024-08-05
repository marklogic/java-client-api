/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.io.marker.QueryOptionsWriteHandle;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;

public interface CombinedQueryBuilder {
  CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery, String qtext);
  CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
                                  QueryOptionsWriteHandle options);
  CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
                                  QueryOptionsWriteHandle options, String qtext);
  CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
                                  QueryOptionsWriteHandle options, String qtext, String sparql);
  CombinedQueryDefinition combine(RawStructuredQueryDefinition structuredQuery, String qtext);
  CombinedQueryDefinition combine(RawStructuredQueryDefinition structuredQuery,
                                  QueryOptionsWriteHandle options);
  CombinedQueryDefinition combine(RawStructuredQueryDefinition structuredQuery,
                                  QueryOptionsWriteHandle options, String qtext);
  CombinedQueryDefinition combine(RawStructuredQueryDefinition structuredQuery,
                                  QueryOptionsWriteHandle options, String qtext, String sparql);
  CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery, String qtext);
  CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
                                  QueryOptionsWriteHandle options);
  CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
                                  QueryOptionsWriteHandle options, String qtext);
  CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
                                  QueryOptionsWriteHandle options, String qtext, String sparql);
}

