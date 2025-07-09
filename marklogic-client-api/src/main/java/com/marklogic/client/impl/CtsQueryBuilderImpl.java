/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
