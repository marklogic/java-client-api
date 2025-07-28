package com.marklogic.client.impl;

import com.marklogic.client.impl.BaseTypeImpl.BaseArgImpl;
import com.marklogic.client.type.PlanColumn;
import com.marklogic.client.type.PlanDocColsIdentifier;

import java.util.Map;

public class PlanDocColsIdentifierImpl implements PlanDocColsIdentifier, BaseArgImpl {

    private String template;

    public PlanDocColsIdentifierImpl(Map<String, PlanColumn> mapping) {
        StringBuilder sb = new StringBuilder("{");
        // Keys are not validated here as they will be caught by the server, and
        // restricting the set of keys could cause issues with future server releases
        boolean firstOne = true;
        for (String key : mapping.keySet()) {
            if (!firstOne) {
                sb.append(", ");
            }
            sb.append(String.format("\"%s\": ", key));
            ((BaseArgImpl) mapping.get(key)).exportAst(sb);
            firstOne = false;
        }
        this.template = sb.append("}").toString();
    }

    @Override
    public StringBuilder exportAst(StringBuilder strb) {
        return strb.append(this.template);
    }
}
