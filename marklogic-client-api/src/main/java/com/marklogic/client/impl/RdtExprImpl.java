/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.expression.RdtExpr;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanExprCol;

import java.util.Map;

public class RdtExprImpl implements RdtExpr {
    final static RdtExprImpl rdt = new RdtExprImpl();

    @Override
    public PlanExprCol maskDeterministic(PlanColumn column) {
        return maskDeterministic(column, null);
    }
    @Override
    public PlanExprCol maskDeterministic(PlanColumn column, Map<String,?> options) {
        return redactImpl("maskDeterministic", "mask-deterministic", column, options);
    }
    @Override
    public PlanExprCol maskRandom(PlanColumn column) {
        return maskRandom(column, null);
    }
    @Override
    public PlanExprCol maskRandom(PlanColumn column, Map<String,?> options) {
        return redactImpl("maskRandom", "mask-random", column, options);
    }
    @Override
    public PlanExprCol redactDatetime(PlanColumn column, Map<String,?> options) {
        if (column == null) {
            throw new IllegalArgumentException("must provide options for redactDatetime()");
        }
        return redactImpl("redactDatetime", "redact-datetime", column, options);
    }
    @Override
    public PlanExprCol redactEmail(PlanColumn column) {
        return redactEmail(column, null);
    }
    @Override
    public PlanExprCol redactEmail(PlanColumn column, Map<String,?> options) {
        return redactImpl("redactEmail", "redact-email", column, options);
    }
    @Override
    public PlanExprCol redactIpv4(PlanColumn column) {
        return redactIpv4(column, null);
    }
    @Override
    public PlanExprCol redactIpv4(PlanColumn column, Map<String,?> options) {
        return redactImpl("redactIpv4", "redact-ipv4", column, options);
    }
    @Override
    public PlanExprCol redactNumber(PlanColumn column) {
        return redactNumber(column, null);
    }
    @Override
    public PlanExprCol redactNumber(PlanColumn column, Map<String,?> options) {
        return redactImpl("redactNumber", "redact-number", column, options);
    }
    @Override
    public PlanExprCol redactRegex(PlanColumn column, Map<String,?> options) {
        if (column == null) {
            throw new IllegalArgumentException("must provide options for redactRegex()");
        }
        return redactImpl("redactRegex", "redact-regex", column, options);
    }
    @Override
    public PlanExprCol redactUsPhone(PlanColumn column) {
        return redactUsPhone(column, null);
    }
    @Override
    public PlanExprCol redactUsPhone(PlanColumn column, Map<String,?> options) {
        return redactImpl("redactUsPhone", "redact-us-phone", column, options);
    }
    @Override
    public PlanExprCol redactUsSsn(PlanColumn column) {
        return redactUsSsn(column, null);
    }
    @Override
    public PlanExprCol redactUsSsn(PlanColumn column, Map<String,?> options) {
        return redactImpl("redactUsSsn", "redact-us-ssn", column, options);
    }
    private PlanExprCol redactImpl(String clientFn, String enodeFn, PlanColumn column, Map<String,?> options) {
        if (column == null) {
            throw new IllegalArgumentException("must provide column to redact for "+clientFn+"()");
        }
        return new RedactCallImpl(enodeFn, new Object[]{column, (options == null) ? null : new BaseTypeImpl.BaseMapImpl(options)});
    }
    static class RedactCallImpl extends PlanBuilderSubImpl.ExprColCallImpl {
        RedactCallImpl(String fnName, Object[] fnArgs) {
            super("ordt", fnName, fnArgs);
        }
    }
}
