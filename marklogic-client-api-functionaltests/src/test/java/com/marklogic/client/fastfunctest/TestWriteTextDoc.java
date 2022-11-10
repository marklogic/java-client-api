/*
 * Copyright (c) 2019 MarkLogic Corporation
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

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestWriteTextDoc extends AbstractFunctionalTest {

    @Test
    public void testWriteTextDoc() {
        DatabaseClient client = connectAsAdmin();

        String docId = "/foo/test/myFoo.txt";
        TextDocumentManager docMgr = client.newTextDocumentManager();
        docMgr.write(docId, new StringHandle().with("This is so foo"));
        assertEquals("Text document write difference", "This is so foo", docMgr.read(docId, new StringHandle()).get());
    }
}
