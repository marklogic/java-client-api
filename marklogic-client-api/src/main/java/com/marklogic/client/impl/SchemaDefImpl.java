/*
 * Copyright (c) 2022 MarkLogic Corporation
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
