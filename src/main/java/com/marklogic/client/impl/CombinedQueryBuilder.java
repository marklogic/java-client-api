/*
 * Copyright 2012-2016 MarkLogic Corporation
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
    CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery, String qtext);
    CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
        QueryOptionsWriteHandle options);
    CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext);
    CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext, String sparql);
    CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery, String qtext);
    CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
        QueryOptionsWriteHandle options);
    CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext);
    CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext, String sparql);
}

