package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

public class PlanRowColTypesImplTest extends Assert {

    @Test
    public void columnOnly() {
        ObjectNode node = toJson(new PlanRowColTypesImpl("myColumn", null, null));
        assertEquals("myColumn", node.get("column").asText());
        assertEquals("none", node.get("type").asText());
        assertEquals(false, node.get("nullable").asBoolean());
    }

    @Test
    public void columnAndType() {
        ObjectNode node = toJson(new PlanRowColTypesImpl("myColumn", "string", null));
        assertEquals("myColumn", node.get("column").asText());
        assertEquals("string", node.get("type").asText());
        assertEquals(false, node.get("nullable").asBoolean());
    }

    @Test
    public void allFields() {
        ObjectNode node = toJson(new PlanRowColTypesImpl("someNumber", "integer", false));
        assertEquals("someNumber", node.get("column").asText());
        assertEquals("integer", node.get("type").asText());
        assertEquals(false, node.get("nullable").asBoolean());
    }

    private ObjectNode toJson(PlanRowColTypesImpl identifier) {
        try {
            return (ObjectNode) new ObjectMapper().readTree(identifier.exportAst(new StringBuilder()).toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
