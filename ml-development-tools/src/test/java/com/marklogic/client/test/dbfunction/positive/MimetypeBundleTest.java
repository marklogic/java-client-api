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

import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.test.dbfunction.DBFunctionTestUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MimetypeBundleTest {
    // endpoint needs elevated privilege to write document
    MimetypeBundle testObj = MimetypeBundle.on(DBFunctionTestUtil.adminDb);

    @Test
    public void testApiReader() {
        String sourceAPI = "src/test/ml-modules/root/dbfunctiondef/positive/mimetype/apiReader.api";
        try {
            String clientAPI = NodeConverter.ReaderToString(new FileReader(new File(sourceAPI)))
                    .replaceAll("\\s+", "")
                    .replaceAll(",", ", ");

            Reader dbReader = testObj.apiReader("/dbf/test/mimetype/","apiReader");
            assertNotNull("failed to read API declaration from database", dbReader);

            String dbAPI = NodeConverter.ReaderToString(dbReader);
            assertEquals("API declaration differs", clientAPI, dbAPI);
        } catch(Exception e) {
            fail(e.getClass().getSimpleName()+": "+e.getMessage());
        }

    }
}
