/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.document.XMLDocumentManager.DocumentRepair;
import com.marklogic.client.io.FileHandle;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;



public class TestXMLDocumentRepair extends AbstractFunctionalTest {

    @Test
    public void testXMLDocumentRepairFull() throws IOException {
        // acquire the content
        File file = new File("repairXMLFull.xml");
        file.delete();
        boolean success = file.createNewFile();
        if (success)
            System.out.println("New file created on " + file.getAbsolutePath());
        else
            System.out.println("Cannot create file");

        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        String xmlContent =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<repair>\n" +
                "<p>This is <b>bold and <i>italic</b> within the paragraph.</p>\n" +
                "<p>This is <b>bold and <i>italic</i></b></u> within the paragraph.</p>\n" +
                "<p>This is <b>bold and <i>italic</b></i> within the paragraph.</p>\n" +
                "</repair>";

        String repairedContent =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<repair>\n" +
                "<p>This is <b>bold and <i>italic</i></b> within the paragraph.</p>\n" +
                "<p>This is <b>bold and <i>italic</i></b> within the paragraph.</p>\n" +
                "<p>This is <b>bold and <i>italic</i></b> within the paragraph.</p>\n" +
                "</repair>";

        out.write(xmlContent);
        out.close();

        DatabaseClient client = connectAsRestWriter();

        // create doc id
        String docId = "/repair/xml/" + file.getName();

        // create document manager
        XMLDocumentManager docMgr = client.newXMLDocumentManager();

        // set document repair
        docMgr.setDocumentRepair(DocumentRepair.FULL);

        // create a handle on the content
        FileHandle handle = new FileHandle(file);
        handle.set(file);

        // write the document content
        docMgr.write(docId, handle);

        System.out.println("Write " + docId + " to database");

        // read the document
        docMgr.read(docId, handle);

        // access the document content
        File fileRead = handle.get();

        Scanner scanner = new Scanner(fileRead).useDelimiter("\\Z");
        String readContent = scanner.next();
        assertEquals( repairedContent, readContent);
        scanner.close();

        // release the client
        client.release();
    }
}
