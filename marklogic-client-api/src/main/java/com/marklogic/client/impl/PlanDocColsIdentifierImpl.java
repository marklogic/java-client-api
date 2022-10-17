package com.marklogic.client.impl;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.impl.BaseTypeImpl.BaseArgImpl;
import com.marklogic.client.type.PlanDocColsIdentifier;

public class PlanDocColsIdentifierImpl implements PlanDocColsIdentifier, BaseArgImpl {

    private String template;

    public PlanDocColsIdentifierImpl(Map<String, String> mapping) {
        ObjectNode descriptor = new ObjectMapper().createObjectNode();
        // Keys are not validated here as they will be caught by the server, and
        // restricting the set of keys could cause issues with future server releases
        mapping.keySet().forEach(key -> descriptor.put(key, mapping.get(key)));
        this.template = descriptor.toString();
    }

    @Override
    public StringBuilder exportAst(StringBuilder strb) {
        return strb.append(this.template);
    }
}
