package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;
import org.junit.Test;

public class PlanRowColTypesSeqImplTest extends Assert {

    @Test
    public void twoColumns() throws Exception {
        String expression = new PlanRowColTypesSeqImpl(
                new PlanRowColTypesImpl("myColumn", "string", null),
                new PlanRowColTypesImpl("myNumber", "integer", null)
        ).exportAst(new StringBuilder()).toString();

        ArrayNode array = (ArrayNode) new ObjectMapper().readTree(expression);
        System.out.println(array);
        assertEquals(2, array.size());
        assertEquals("myColumn", array.get(0).get("column").asText());
        assertEquals("string", array.get(0).get("type").asText());
        assertFalse(array.get(0).get("nullable").asBoolean());
        assertEquals("myNumber", array.get(1).get("column").asText());
        assertEquals("integer", array.get(1).get("type").asText());
        assertFalse(array.get(1).get("nullable").asBoolean());
    }
}
