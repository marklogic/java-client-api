/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.type.PlanRowColTypes;

class PlanRowColTypesImpl implements PlanRowColTypes, BaseTypeImpl.BaseArgImpl {

    private String expression;

    public PlanRowColTypesImpl(String column, String type, Boolean nullable) {
        String template = "{\"column\":\"%s\", \"type\":\"%s\", \"nullable\":%s}";
        this.expression = String.format(template,
                column,
                type != null ? type : "none",
                nullable != null ? nullable : false
        );
    }

    @Override
    public StringBuilder exportAst(StringBuilder strb) {
        return strb.append(expression);
    }
}
