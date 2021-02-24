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

import com.marklogic.client.expression.*;
import com.marklogic.client.io.marker.JSONReadHandle;
import com.marklogic.client.io.marker.JSONWriteHandle;
import com.marklogic.client.query.CtsQueryDefinition;
import com.marklogic.client.type.CtsQueryExpr;

public class CtsQueryBuilderImpl extends CtsQueryBuilder {
     CtsQueryBuilderImpl() {
        super(CtsExprImpl.cts,
                FnExprImpl.fn,
                GeoExprImpl.geo,
                JsonExprImpl.json,
                MapExprImpl.map,
                MathExprImpl.math,
                RdfExprImpl.rdf,
                SemExprImpl.sem,
                SpellExprImpl.spell,
                SqlExprImpl.sql,
                XdmpExprImpl.xdmp,
                XsExprImpl.xs);
    }

    @Override
    public CtsQueryDefinition newCtsQueryDefinition(CtsQueryExpr query) {
        return new CtsQueryDefinitionImpl(query);
    }

    @Override
    public CtsQueryDefinition newCtsQueryDefinition(CtsQueryExpr query, JSONWriteHandle queryOptions) {
        return new CtsQueryDefinitionImpl(query, queryOptions);
    }

    @Override
    public <T extends JSONReadHandle> T export(CtsQueryExpr query, T handle) {
        Utilities.setHandleToString(handle, (new CtsQueryDefinitionImpl(query)).serialize());
        return handle;
    }
}
