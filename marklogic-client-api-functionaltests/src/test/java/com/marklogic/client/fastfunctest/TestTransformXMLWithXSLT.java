/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.SourceHandle;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



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
        // assertEquals( transformedContent,
        // readContent);
        assertTrue( readContent.contains("firstname"));
        scanner.close();
        handle.close();
    }
}
