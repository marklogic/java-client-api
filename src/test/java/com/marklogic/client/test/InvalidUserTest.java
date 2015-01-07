/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.StringHandle;

public class InvalidUserTest {
    @Test
    public void testInvalidUserAuth() {

        // create the client
        DatabaseClient client = DatabaseClientFactory.newClient(
          "localhost", 8012, "MyFooUser", "x", Authentication.DIGEST);


        String expectedException = "com.marklogic.client.FailedRequestException: " +
          "Local message: write failed: Unauthorized. Server Message: Unauthorized";
        String exception = "";

        String docId = "/example/text.txt";
        TextDocumentManager docMgr = client.newTextDocumentManager();
        try {
            // make use of the client connection so we get an auth error
            StringHandle handle = new StringHandle();
            handle.set("A simple text document");
            docMgr.write(docId, handle);
            // the next line will only run if write doesn't throw an exception
            docMgr.delete(docId);
        }
        catch (Exception e) {
            exception = e.toString();
        } finally {
            client.release();
        }
        assertEquals(expectedException, exception);

    }
}
