package com.marklogic.client.impl;

import com.marklogic.client.type.PlanRowColTypes;
import com.marklogic.client.type.PlanRowColTypesSeq;

class PlanRowColTypesSeqImpl implements PlanRowColTypesSeq, BaseTypeImpl.BaseArgImpl {

    private String expression;

    public PlanRowColTypesSeqImpl(PlanRowColTypes... colTypes) {
        StringBuilder strb = new StringBuilder("[");
        for (int i = 0; i < colTypes.length; i++) {
            if (i > 0) {
                strb.append(",");
            }
            ((BaseTypeImpl.BaseArgImpl) colTypes[i]).exportAst(strb);
        }
        this.expression = strb.append("]").toString();
    }

    @Override
    public StringBuilder exportAst(StringBuilder strb) {
        return strb.append(this.expression);
    }
}
