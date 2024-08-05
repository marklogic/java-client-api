/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;



public class TestWriteTextDoc extends AbstractFunctionalTest {

    @Test
    public void testWriteTextDoc() {
        DatabaseClient client = connectAsAdmin();

        String docId = "/foo/test/myFoo.txt";
        TextDocumentManager docMgr = client.newTextDocumentManager();
        docMgr.write(docId, new StringHandle().with("This is so foo"));
        assertEquals( "This is so foo", docMgr.read(docId, new StringHandle()).get());
    }
}
