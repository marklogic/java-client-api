package com.marklogic.client.impl;

import com.marklogic.client.type.PlanDocColsIdentifier;

class PlanDocColsIdentifierImpl implements PlanDocColsIdentifier, BaseTypeImpl.BaseArgImpl {

    private String expression;

    public PlanDocColsIdentifierImpl(String schema, String view, String column, String type, Boolean nullable) {
        String template = "{\"schema\":\"%s\", \"view\":\"%s\", \"column\":\"%s\", \"type\":\"%s\", \"nullable\":%s}";
        this.expression = String.format(template,
                schema != null ? schema : "",
                view != null ? view : "",
                column, type,
                nullable != null ? nullable : true
        );
    }

    @Override
    public StringBuilder exportAst(StringBuilder strb) {
        return strb.append(expression);
    }
}
