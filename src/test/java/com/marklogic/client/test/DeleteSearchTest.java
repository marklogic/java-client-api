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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteSearchTest {
    private static final String directory = "/delete/test/";
    private static final String filename = "testWrite1.xml";
    private static final String docId = directory + filename;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Common.connectAdmin();
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
        writeDoc();
    }

    public static void writeDoc() throws Exception {
        Document domDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = domDocument.createElement("root");
        root.setAttribute("xml:lang", "en");
        root.setAttribute("foo", "bar");
        root.appendChild(domDocument.createElement("child"));
        root.appendChild(domDocument.createTextNode("mixed"));
        domDocument.appendChild(root);

        @SuppressWarnings("unused")
		String domString = ((DOMImplementationLS) DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .getDOMImplementation()).createLSSerializer().writeToString(domDocument);

        XMLDocumentManager docMgr = Common.client.newXMLDocumentManager();
        docMgr.write(docId, new DOMHandle().with(domDocument));
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void test_A_Delete() throws IOException {
        GenericDocumentManager docMgr = Common.client.newDocumentManager();
        DocumentDescriptor desc = docMgr.exists(docId);
        assertNotNull("Should find document before delete", desc);
        assertEquals(desc.getUri(), docId);

        QueryManager queryMgr = Common.client.newQueryManager();
        DeleteQueryDefinition qdef = queryMgr.newDeleteDefinition();
        qdef.setDirectory(directory);

        queryMgr.delete(qdef);

        desc = docMgr.exists(docId);
        assertNull("Should not find document after delete", desc);
    }

    @Test
    public void test_B_RuntimeDb() throws Exception {
        Common.release();
        // connect to a runtime db
        Common.client = DatabaseClientFactory.newClient(
            Common.HOST, Common.PORT, "Documents", Common.EVAL_USERNAME, Common.EVAL_PASSWORD, Authentication.DIGEST);
        writeDoc();
        test_A_Delete();
    }
}
