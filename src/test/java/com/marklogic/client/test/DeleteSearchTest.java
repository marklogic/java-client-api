/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class DeleteSearchTest {
	@SuppressWarnings("unused")
	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);
	
    @BeforeClass
    public static void beforeClass() throws ParserConfigurationException {
        Common.connectAdmin();

        String docId = "/delete/test/testWrite1.xml";

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
    public void testSearch() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        StringQueryDefinition qdef = queryMgr.newStringDefinition(null);
        qdef.setDirectory("/delete/test/");

        SearchHandle handle = new SearchHandle();
        handle = queryMgr.search(qdef, handle);

        assertNotNull(handle);
    }

    @Test
    public void testDelete() throws IOException {
        QueryManager queryMgr = Common.client.newQueryManager();
        DeleteQueryDefinition qdef = queryMgr.newDeleteDefinition();
        qdef.setDirectory("/delete/test/");

        queryMgr.delete(qdef);
    }
}
