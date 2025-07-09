/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
