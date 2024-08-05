/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.SchemaDefExpr;

public class SchemaDefImpl implements SchemaDefExpr, BaseTypeImpl.BaseArgImpl {
    private String kind;
    private String mode;
    private String schemaUri;

    public SchemaDefImpl(String kind){
        this.kind = kind;
    }
    @Override
    public StringBuilder exportAst(StringBuilder strb) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        if(this.kind != null)
            node.put("kind", this.kind);
        if(this.mode != null)
            node.put("mode", this.mode);
        if(this.schemaUri != null)
            node.put("schemaUri", this.schemaUri);
        return strb.append(node.toString());
    }

    @Override
    public SchemaDefExpr withMode(String mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public SchemaDefExpr withSchemaUri(String schemaUri) {
        this.schemaUri = schemaUri;
        return this;
    }
}
