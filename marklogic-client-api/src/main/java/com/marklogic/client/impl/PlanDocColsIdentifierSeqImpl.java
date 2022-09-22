package com.marklogic.client.impl;

import com.marklogic.client.type.PlanDocColsIdentifier;
import com.marklogic.client.type.PlanDocColsIdentifierSeq;

class PlanDocColsIdentifierSeqImpl implements PlanDocColsIdentifierSeq, BaseTypeImpl.BaseArgImpl {

    private String expression;

    public PlanDocColsIdentifierSeqImpl(PlanDocColsIdentifier... colTypes) {
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
