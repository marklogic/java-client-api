package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.TransformDef;
import org.junit.Assert;
import org.junit.Test;

public class TransformDefImplTest extends Assert {

    @Test
    public void paramsWithPlanColumn() throws Exception {
        PlanBuilderSubImpl.ColumnCallImpl column = new PlanBuilderSubImpl.ColumnCallImpl("op", "col",
            new Object[]{new XsValueImpl().string("uri")});

        TransformDef transform = new TransformDefImpl("/my/transform.mjs")
            .withKind("mjs")
            .withParam("myColumnParam", column)
            .withParam("myRegularParam", "hi");

        String json = ((TransformDefImpl) transform).exportAst(new StringBuilder()).toString();
        ObjectNode node = (ObjectNode) new ObjectMapper().readTree(json);

        assertEquals("/my/transform.mjs", node.get("path").asText());
        assertEquals("mjs", node.get("kind").asText());
        assertEquals("hi", node.get("params").get("myRegularParam").asText());

        assertEquals("op", node.get("params").get("myColumnParam").get("ns").asText());
        assertEquals("col", node.get("params").get("myColumnParam").get("fn").asText());
        JsonNode arg = node.get("params").get("myColumnParam").get("args").get(0);
        assertEquals("xs", arg.get("ns").asText());
        assertEquals("string", arg.get("fn").asText());
        assertEquals("uri", arg.get("args").get(0).asText());
    }
}
