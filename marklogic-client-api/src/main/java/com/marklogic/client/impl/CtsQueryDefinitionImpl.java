/*
 * Copyright (c) 2020 MarkLogic Corporation
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

import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.query.CtsQueryDefinition;
import com.marklogic.client.type.CtsQueryExpr;

public class CtsQueryDefinitionImpl extends AbstractSearchQueryDefinition implements CtsQueryDefinition {
    private CtsQueryExpr query;
    private JSONWriteHandle queryOptions;

    public CtsQueryDefinitionImpl(CtsQueryExpr query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null.");
        }
        this.query = query;
    }

    public CtsQueryDefinitionImpl(CtsQueryExpr query, JSONWriteHandle queryOptions) {
        this(query);
        this.queryOptions = queryOptions;
    }

    @Override
    public String serialize() {
        StringBuilder str = new StringBuilder();
        str.append("{\"search\":");
        str.append("{\"ctsast\":");
        ((CtsExprImpl.QueryCallImpl) query).exportAst(str);
        if (queryOptions != null) {
            str.append(", ");
            str.append(queryOptions.toString());
        }
        str.append("}}");
        return str.toString();
    }
}
