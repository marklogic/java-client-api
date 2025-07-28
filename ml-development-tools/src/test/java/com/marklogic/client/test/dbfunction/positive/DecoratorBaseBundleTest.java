/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.dbfunction.positive;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.test.dbfunction.DBFunctionTestUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import static org.junit.Assert.*;

public class DecoratorBaseBundleTest {
    DecoratorBaseBundle testObj = DecoratorBaseBundle.on(DBFunctionTestUtil.db);

    @Test
    public void testDocify() {
        try {
            JsonNode dbNode = testObj.docify("value1");
            assertNotNull("failed to read docified value from database", dbNode );

            JsonNode dbValue = dbNode.get("value");
            assertNotNull("no value key in docified value", dbValue);
            assertTrue("docified value is not textual", dbValue.isTextual());
            assertEquals("unexpected docified value", "value1", dbValue.asText());
        } catch(Exception e) {
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
}
