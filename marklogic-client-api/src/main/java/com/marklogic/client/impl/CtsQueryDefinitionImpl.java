/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
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

    @Override
    public boolean canSerializeQueryAsJSON() {
        return getOptionsName() == null;
    }
}
