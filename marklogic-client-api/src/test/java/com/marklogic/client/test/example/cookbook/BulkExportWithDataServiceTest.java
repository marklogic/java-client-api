/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test.example.cookbook;

import com.marklogic.client.example.cookbook.datamovement.BulkExportWithDataService;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.Test;

public class BulkExportWithDataServiceTest {

    @Test
    public void testMain() throws Exception {
        new BulkExportWithDataService(
            Common.connect(),
            Common.newEvalClient("java-unittest-modules")
        ).run();
    }

}
