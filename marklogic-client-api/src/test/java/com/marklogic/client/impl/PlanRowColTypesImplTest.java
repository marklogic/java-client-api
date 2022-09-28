package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

public class PlanRowColTypesImplTest extends Assert {

    @Test
    public void columnAndType() {
        ObjectNode node = toJson(new PlanRowColTypesImpl(null, null, "myColumn", "string", null));
        assertEquals("myColumn", node.get("column").asText());
        assertEquals("string", node.get("type").asText());
        assertEquals("", node.get("schema").asText());
        assertEquals("", node.get("view").asText());
        assertEquals("If not specified, nullable should default to true to err on the side of being less restrictive",
                true, node.get("nullable").asBoolean());
    }

    @Test
    public void allFields() {
        ObjectNode node = toJson(new PlanRowColTypesImpl("mySchema", "myView", "someNumber", "integer", false));
        assertEquals("someNumber", node.get("column").asText());
        assertEquals("integer", node.get("type").asText());
        assertEquals("mySchema", node.get("schema").asText());
        assertEquals("myView", node.get("view").asText());
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
