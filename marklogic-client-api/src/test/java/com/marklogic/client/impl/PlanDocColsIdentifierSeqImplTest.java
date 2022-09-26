package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;
import org.junit.Test;

public class PlanDocColsIdentifierSeqImplTest extends Assert {

    @Test
    public void twoColumns() throws Exception {
        String expression = new PlanDocColsIdentifierSeqImpl(
                new PlanDocColsIdentifierImpl("", "", "myColumn", "string", null),
                new PlanDocColsIdentifierImpl(null, null, "myNumber", "integer", null)
        ).exportAst(new StringBuilder()).toString();

        ArrayNode array = (ArrayNode) new ObjectMapper().readTree(expression);
        assertEquals(2, array.size());
        assertEquals("myColumn", array.get(0).get("column").asText());
        assertEquals("string", array.get(0).get("type").asText());
        assertEquals("myNumber", array.get(1).get("column").asText());
        assertEquals("integer", array.get(1).get("type").asText());
    }
}
