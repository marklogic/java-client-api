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

package com.marklogic.client.converted;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.fastfunctest.AbstractFunctionalTest;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.SourceHandle;
import org.junit.Test;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class TestTransformXMLWithXSLT extends AbstractFunctionalTest {

    @Test
    public void testWriteXMLWithXSLTransform() throws TransformerException, FileNotFoundException {
        DatabaseClient client = connectAsRestWriter();

        // get the doc
        Source source = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/employee.xml");

        // get the xslt
        Source xsl = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/employee-stylesheet.xsl");

        // create transformer
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xsl);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // create a doc manager
        XMLDocumentManager docMgr = client.newXMLDocumentManager();

        // create an identifier for the document
        String docId = "/example/trans/transform.xml";

        // create a handle on the content
        SourceHandle handle = new SourceHandle();
        handle.set(source);

        // set the transformer
        handle.setTransformer(transformer);

        // write the document content
        docMgr.write(docId, handle);

        System.out.println("Write " + docId + " to database");

        // create a handle on the content
        FileHandle readHandle = new FileHandle();

        // read the document
        docMgr.read(docId, readHandle);

        // access the document content
        File fileRead = readHandle.get();

        Scanner scanner = new Scanner(fileRead).useDelimiter("\\Z");
        String readContent = scanner.next();
        // String transformedContent = readContent.replaceAll("^name$",
        // "firstname");
        // assertEquals("XML document write difference", transformedContent,
        // readContent);
        assertTrue("check document from DB has name element changed", readContent.contains("firstname"));
        scanner.close();
        handle.close();
    }
}
