package com.marklogic.client.impl;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.TransformDef;
import com.marklogic.client.impl.BaseTypeImpl.BaseArgImpl;

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
            node.putPOJO("params", params);
        }
        return strb.append(node);
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
