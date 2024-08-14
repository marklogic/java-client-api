/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.query.SearchQueryDefinition;

public abstract class AbstractSearchQueryDefinition implements SearchQueryDefinition {

    protected String optionsUri = null;
    private ServerTransform transform   = null;

    @Override
    public String getOptionsName() {
        return optionsUri;
    }

    @Override
    public void setOptionsName(String uri) {
        this.optionsUri = uri;
    }
    @Override
    public ServerTransform getResponseTransform() {
        return transform;
    }

    @Override
    public void setResponseTransform(ServerTransform transform) {
        this.transform = transform;
    }

    public abstract boolean canSerializeQueryAsJSON();
}
