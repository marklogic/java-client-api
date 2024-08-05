/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.TransformDef;
import com.marklogic.client.impl.BaseTypeImpl.BaseArgImpl;
import com.marklogic.client.type.PlanColumn;

import java.util.HashMap;
import java.util.Map;

public class TransformDefImpl implements TransformDef, BaseArgImpl {

    private String path;
    private String kind = "mjs";
    private Map<String, Object> params;

    public TransformDefImpl(String path) {
        this.path = path;
    }

    @Override
    public StringBuilder exportAst(StringBuilder strb) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("path", path);
        node.put("kind", kind);
        if (params != null) {
            node.putPOJO("params", prepareParamsDuringExport());
        }
        return strb.append(node);
    }

    private Map<String, Object> prepareParamsDuringExport() {
        Map<String, Object> preparedParams = new HashMap<>();
        ObjectMapper mapper = null;
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof PlanColumn) {
                String json = ((BaseArgImpl) value).exportAst(new StringBuilder()).toString();
                if (mapper == null) {
                    mapper = new ObjectMapper();
                }
                try {
                    preparedParams.put(key, mapper.readTree(json));
                } catch (JsonProcessingException e) {
                    // This exception is not expected, as it would require that a bug exist in the implementation
                    // for exporting a PlanColumn
                    throw new RuntimeException("Unable to prepare parameters while exporting transform definition: " + e.getMessage(), e);
                }
            } else {
                preparedParams.put(key, value);
            }
        }
        ;
        return preparedParams;
    }

    @Override
    public TransformDef withKind(String kind) {
        this.kind = kind;
        return this;
    }

    @Override
    public TransformDef withParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    @Override
    public TransformDef withParam(String name, Object value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(name, value);
        return this;
    }
}
