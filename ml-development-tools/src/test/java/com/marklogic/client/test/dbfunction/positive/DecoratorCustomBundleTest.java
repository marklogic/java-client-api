/*
 * Copyright 2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.test.dbfunction.positive;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.test.dbfunction.DBFunctionTestUtil;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DecoratorCustomBundleTest {
    final static private String SOURCE_SERVICE =
            "src/test/ml-modules/root/dbfunctiondef/positive/decoratorCustom/service.json";

    // execute the same implementation via direct interface and
    DecoratorCustomBundle  testDirectInterface = DecoratorCustomBundle.on(DBFunctionTestUtil.db);
    // via a customized implementation of the same interface
    DecoratorBaseBundle testCustomOverride  = DecoratorBaseBundle.on(
        DBFunctionTestUtil.db, new FileHandle(new File(SOURCE_SERVICE))
        );

    @Test
    public void testDocify() {
        try {
            JsonNode[] dbNodes = new JsonNode[]{
                testDirectInterface.docify("value0"),
                testCustomOverride.docify("value1")
            };

            for (int i=0; i < dbNodes.length; i++) {
                String interfaceType = (i == 0) ? "default" : "custom";

                JsonNode dbNode = dbNodes[i];
                assertNotNull("failed to read "+interfaceType+" docified value from database", dbNode );

// System.out.println(dbNode.toString());

                for (String key: new String[]{"value", "type"}) {
                    String value = (key == "value") ? "value"+i : "string";
                    JsonNode dbValue = dbNode.get(key);
                    assertNotNull("no "+key+" key in "+interfaceType+" docified response", dbValue);
                    assertTrue(interfaceType+" docified "+key+" is not textual", dbValue.isTextual());
                    assertEquals("unexpected "+interfaceType+" docified "+key, value, dbValue.asText());
                }
            }
        } catch(Exception e) {
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }
    }
}
