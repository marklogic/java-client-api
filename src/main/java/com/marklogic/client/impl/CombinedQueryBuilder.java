/*
 * Copyright 2012-2015 MarkLogic Corporation
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
    public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery, String qtext);
    public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
        QueryOptionsWriteHandle options);
    public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
        QueryOptionsWriteHandle options, String qtext);
    public CombinedQueryDefinition combine(StructuredQueryDefinition structuredQuery,
        QueryOptionsWriteHandle options, String qtext, String sparql);
    public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery, String qtext);
    public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
        QueryOptionsWriteHandle options);
    public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext);
    public CombinedQueryDefinition combine(RawStructuredQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext, String sparql);
    public CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery, String qtext);
    public CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
        QueryOptionsWriteHandle options);
    public CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext);
    public CombinedQueryDefinition combine(RawCombinedQueryDefinition rawQuery,
        QueryOptionsWriteHandle options, String qtext, String sparql);
}

