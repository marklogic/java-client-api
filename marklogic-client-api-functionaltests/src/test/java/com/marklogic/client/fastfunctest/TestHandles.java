/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.*;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestHandles extends AbstractFunctionalTest {

    @BeforeAll
    public static void setUp() throws Exception {
        createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
        createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader");
    }

    @AfterEach
    public void testCleanUp() throws Exception {
        deleteDocuments(connectAsAdmin());
    }

    @AfterAll
    public static void tearDown() throws Exception {
        deleteRESTUser("eval-user");
        deleteUserRole("test-eval");
    }

    // Begin TestBytesHandle
    @Test
    public void testXmlCRUD_BytesHandle() throws IOException, SAXException, ParserConfigurationException {

        String filename = "xml-original-test.xml";
        String uri = "/write-xml-domhandle/";
        System.out.println("Running testXmlCRUD_BytesHandle");
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setNormalizeWhitespace(true);

        DatabaseClient client = newClientAsUser("eval-user", "x");

        // write docs
        writeDocumentUsingBytesHandle(client, filename, uri, null, "XML");
        // read docs
        BytesHandle contentHandle = readDocumentUsingBytesHandle(client, uri + filename, "XML");

        // get the contents
        byte[] readDoc1 = (byte[]) contentHandle.get();
        String readDoc2 = new String(readDoc1);
        Document readDoc = convertStringToXMLDocument(readDoc2);

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);
        assertXMLEqual("Write XML difference", expectedDoc, readDoc);

        // Update the Doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentUsingByteHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        BytesHandle updateHandle = readDocumentUsingBytesHandle(client, uri + filename, "XML");
        byte[] readDocUpdateInBytes = updateHandle.get();
        String readDocUpdateInString = new String(readDocUpdateInBytes);

        // convert actual string to xml doc
        Document readDocUpdate = convertStringToXMLDocument(readDocUpdateInString);

        // get xml document for expected result
        Document expectedDocUpdate = expectedXMLDocument(updateFilename);
        assertXMLEqual("Write XML Difference", expectedDocUpdate, readDocUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingBytesHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-xml-domhandle/xml-original-test.xml";
        assertEquals( expectedException, exception);

        // assertFalse( isDocumentExist(client, uri +
        // filename, "XML"));

        // release client
        client.release();
    }

    @Test
    public void testTextCRUD_BytesHandle() throws IOException, ParserConfigurationException, SAXException {
        String filename = "text-original.txt";
        String uri = "/write-text-Byteshandle/";
        System.out.println("Runing test TextCRUD_BytesHandle");

        DatabaseClient client = newClientAsUser("eval-user", "x");

        // write docs
        writeDocumentUsingBytesHandle(client, filename, uri, "Text");
        // read docs
        BytesHandle contentHandle = readDocumentUsingBytesHandle(client, uri + filename, "Text");

        // get the contents
        byte[] fileRead = (byte[]) contentHandle.get();
        // String readContent = contentHandle.get().toString();
        String readContent = new String(fileRead);
        String expectedContent = "hello world, welcome to java API";
        assertEquals( expectedContent.trim(), readContent.trim());

        // UPDATE the doc
        // acquire the content for update
        // String updateFilename = "text-updated.txt";
        String updateFilename = "text-updated.txt";
        updateDocumentUsingByteHandle(client, updateFilename, uri + filename, "Text");

        // read the document
        BytesHandle updateHandle = readDocumentUsingBytesHandle(client, uri + filename, "Text");

        // get the contents
        byte[] fileReadUpdate = updateHandle.get();
        String readContentUpdate = new String(fileReadUpdate);
        String expectedContentUpdate = "hello world, welcome to java API after new updates";

        assertEquals( expectedContentUpdate.trim(), readContentUpdate.toString().trim());

        // delete the document
        deleteDocument(client, uri + filename, "Text");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "Text"));

        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "Text");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-text-Byteshandle/text-original.txt";
        assertEquals( expectedException, exception);

        // release client
        client.release();
    }

    @Test
    public void testJsonCRUD_BytesHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
        String filename = "json-original.json";
        String uri = "/write-json-Byteshandle/";
        System.out.println("Running testJsonCRUD_BytesHandle");

        ObjectMapper mapper = new ObjectMapper();

        DatabaseClient client = newClientAsUser("eval-user", "x");

        // write docs
        writeDocumentUsingBytesHandle(client, filename, uri, "JSON");
        // read docs
        BytesHandle contentHandle = readDocumentUsingBytesHandle(client, uri + filename, "JSON");

        // get the contents
        byte[] fileRead = contentHandle.get();
        JsonNode readContent = mapper.readTree(fileRead);

        // get expected contents
        JsonNode expectedContent = expectedJSONDocument(filename);

        assertTrue( readContent.equals(expectedContent));

        // update the doc
        // acquire the content for update
        String updateFilename = "json-updated.json";
        updateDocumentUsingByteHandle(client, updateFilename, uri + filename, "JSON");

        // read the document
        BytesHandle updateHandle = readDocumentUsingBytesHandle(client, uri + filename, "JSON");

        // get the contents
        byte[] fileReadUpdate = updateHandle.get();
        JsonNode readContentUpdate = mapper.readTree(fileReadUpdate);

        // get expected contents
        JsonNode expectedContentUpdate = expectedJSONDocument(updateFilename);
        assertTrue( readContentUpdate.equals(expectedContentUpdate));

        // delete the document
        deleteDocument(client, uri + filename, "JSON");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "JSON"));

        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-json-Byteshandle/json-original.json";
        assertEquals( expectedException, exception);

        // release client
        client.release();
    }

    @Test
    public void testBinaryCRUD_BytesHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
        String filename = "Pandakarlino.jpg";
        String uri = "/write-bin-Bytehandle/";
        System.out.println("Running testBinaryCRUD_BytesHandle");

        DatabaseClient client = newClientAsUser("eval-user", "x");

        // write docs
        writeDocumentUsingBytesHandle(client, filename, uri, "Binary");
        // read docs
        BytesHandle contentHandle = readDocumentUsingBytesHandle(client, uri + filename, "Binary");

        // get the contents
        byte[] fileRead = contentHandle.get();

        // get the binary size
        long size = getBinarySizeFromByte(fileRead);
        long expectedSize = 34543;

        assertEquals( expectedSize, size);

        // update the doc
        // acquire the content for update
        String updateFilename = "mlfavicon.png";
        updateDocumentUsingByteHandle(client, updateFilename, uri + filename, "Binary");

        // read the document
        BytesHandle updateHandle = readDocumentUsingBytesHandle(client, uri + filename, "Binary");

        // get the contents
        byte[] fileReadUpdate = updateHandle.get();

        // get the binary size
        long sizeUpdate = getBinarySizeFromByte(fileReadUpdate);
        // long expectedSizeUpdate = 3290;
        long expectedSizeUpdate = 3322;
        assertEquals( expectedSizeUpdate, sizeUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "Binary");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "Binary"));

        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");
        } catch (Exception e) {
            exception = e.toString();
        }
        String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-bin-Bytehandle/Pandakarlino.jpg";
        assertEquals( expectedException, exception);

        // release client
        client.release();
    }
    // End od TestBytesHandle

    // Begin TestDOMHandle
    @Test
    public void testXmlCRUD_DOMHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
    {
        String filename = "xml-original-test.xml";
        String uri = "/write-xml-domhandle/";

        System.out.println("Running testXmlCRUD_DOMHandle");

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setNormalizeWhitespace(true);

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingDOMHandle(client, filename, uri, "XML");
        // read docs
        DOMHandle contentHandle = readDocumentUsingDOMHandle(client, uri + filename, "XML");

        Document readDoc = contentHandle.get();

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);

        assertEquals( readDoc.getFirstChild().getNodeName().trim(), "food");
        assertEquals( readDoc.getFirstChild().getAttributes().item(0).getNodeValue().trim(), "en");
        assertEquals( readDoc.getChildNodes().item(0).getTextContent().trim(), "noodle");

        assertEquals( expectedDoc.getFirstChild().getNodeName().trim(), "food");
        assertEquals( expectedDoc.getFirstChild().getAttributes().item(0).getNodeValue().trim(), "en");
        assertEquals( expectedDoc.getChildNodes().item(0).getTextContent().trim(), "noodle");

        // update the doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentUsingDOMHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        DOMHandle updateHandle = readDocumentUsingDOMHandle(client, uri + filename, "XML");

        Document readDocUpdate = updateHandle.get();

        assertEquals( readDocUpdate.getFirstChild().getNodeName().trim(), "food");
        assertEquals( readDocUpdate.getFirstChild().getAttributes().item(0).getNodeValue().trim(), "en");
        assertEquals( readDocUpdate.getChildNodes().item(0).getTextContent().trim(), "fried noodle");

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }
    // End of TestDOMHandle

    // Begin TestFileHandle
    @Test
    public void testXmlCRUD_FileHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
    {
        String filename = "xml-original-test.xml";
        String uri = "/write-xml-filehandle/";

        System.out.println("Running testXmlCRUD_FileHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingFileHandle(client, filename, uri, "XML");
        // read docs
        FileHandle contentHandle = readDocumentUsingFileHandle(client, uri + filename, "XML");

        // get the contents
        File fileRead = contentHandle.get();

        String readContent = convertFileToString(fileRead);

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);

        // convert actual string to xml doc
        Document readDoc = convertStringToXMLDocument(readContent);

        assertXMLEqual("Write XML difference", expectedDoc, readDoc);

        // update the doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentUsingFileHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        FileHandle updateHandle = readDocumentUsingFileHandle(client, uri + filename, "XML");

        // get the contents
        File fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertFileToString(fileReadUpdate);

        // get xml document for expected result
        Document expectedDocUpdate = expectedXMLDocument(updateFilename);

        // convert actual string to xml doc
        Document readDocUpdate = convertStringToXMLDocument(readContentUpdate);

        assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingFileHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }
        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }

    @Test
    public void testTextCRUD_FileHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "text-original.txt";
        String uri = "/write-text-filehandle/";

        System.out.println("Running testTextCRUD_FileHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingFileHandle(client, filename, uri, "Text");
        // read docs
        FileHandle contentHandle = readDocumentUsingFileHandle(client, uri + filename, "Text");

        // get the contents
        File fileRead = contentHandle.get();

        String readContent = convertFileToString(fileRead);

        String expectedContent = "hello world, welcome to java API";

        assertEquals( expectedContent.trim(), readContent.trim());

        // update the doc
        // acquire the content for update
        String updateFilename = "text-updated.txt";
        updateDocumentUsingFileHandle(client, updateFilename, uri + filename, "Text");

        // read the document
        FileHandle updateHandle = readDocumentUsingFileHandle(client, uri + filename, "Text");

        // get the contents
        File fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertFileToString(fileReadUpdate);

        String expectedContentUpdate = "hello world, welcome to java API after new updates";

        assertEquals( expectedContentUpdate.trim(), readContentUpdate.toString().trim());

        // delete the document
        deleteDocument(client, uri + filename, "Text");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "Text"));

        String exception = "";
        try {
            readDocumentUsingFileHandle(client, uri + filename, "Text");
        } catch (Exception e) {
            exception = e.toString();
        }
        //
        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }

    @Test
    public void testJsonCRUD_FileHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "json-original.json";
        String uri = "/write-json-filehandle/";

        System.out.println("Running testJsonCRUD_FileHandle");

        ObjectMapper mapper = new ObjectMapper();

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingFileHandle(client, filename, uri, "JSON");
        // read docs
        FileHandle contentHandle = readDocumentUsingFileHandle(client, uri + filename, "JSON");

        // get the contents
        File fileRead = contentHandle.get();

        JsonNode readContent = mapper.readTree(fileRead);

        // get expected contents
        JsonNode expectedContent = expectedJSONDocument(filename);

        assertTrue( readContent.equals(expectedContent));

        // update the doc
        // acquire the content for update
        String updateFilename = "json-updated.json";
        updateDocumentUsingFileHandle(client, updateFilename, uri + filename, "JSON");

        // read the document
        FileHandle updateHandle = readDocumentUsingFileHandle(client, uri + filename, "JSON");

        // get the contents
        File fileReadUpdate = updateHandle.get();

        JsonNode readContentUpdate = mapper.readTree(fileReadUpdate);

        // get expected contents
        JsonNode expectedContentUpdate = expectedJSONDocument(updateFilename);

        assertTrue( readContentUpdate.equals(expectedContentUpdate));

        // delete the document
        deleteDocument(client, uri + filename, "JSON");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "JSON"));

        String exception = "";
        try {
            readDocumentUsingFileHandle(client, uri + filename, "JSON");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }

    @Test
    public void testBinaryCRUD_FileHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "Pandakarlino.jpg";
        String uri = "/write-bin-filehandle/";

        System.out.println("Running testBinaryCRUD_FileHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingFileHandle(client, filename, uri, "Binary");

        // read docs
        FileHandle contentHandle = readDocumentUsingFileHandle(client, uri + filename, "Binary");

        // get the contents
        File fileRead = contentHandle.get();

        // get the binary size
        long size = fileRead.length();
        long expectedSize = 34543;

        assertEquals( expectedSize, size);

        // update the doc
        // acquire the content for update
        String updateFilename = "mlfavicon.png";
        updateDocumentUsingFileHandle(client, updateFilename, uri + filename, "Binary");

        // read the document
        FileHandle updateHandle = readDocumentUsingFileHandle(client, uri + filename, "Binary");

        // get the contents
        File fileReadUpdate = updateHandle.get();

        // get the binary size
        long sizeUpdate = fileReadUpdate.length();
        long expectedSizeUpdate = 3322;

        assertEquals( expectedSizeUpdate, sizeUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "Binary");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "Binary"));

        String exception = "";
        try {
            readDocumentUsingFileHandle(client, uri + filename, "Binary");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }
    // End of TestFileHandle

    // Begin TestInputSourceHandle
    @Test
    public void testXmlCRUD_InputSourceHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException, TransformerException
    {
        String filename = "xml-original-test.xml";
        String uri = "/write-xml-inputsourcehandle/";

        System.out.println("Running testXmlCRUD_InputSourceHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");
        // read docs
        InputSourceHandle contentHandle = readDocumentUsingInputSourceHandle(client, uri + filename, "XML");

        // get the contents
        InputSource fileRead = contentHandle.get();

        String readContent = convertInputSourceToString(fileRead);
        System.out.println(readContent);

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);

        // convert actual string to xml doc
        Document readDoc = convertStringToXMLDocument(readContent);

        assertXMLEqual("Write XML difference", expectedDoc, readDoc);

        // update the doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        InputSourceHandle updateHandle = readDocumentUsingInputSourceHandle(client, uri + filename, "XML");

        // get the contents
        InputSource fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertInputSourceToString(fileReadUpdate);

        // get xml document for expected result
        Document expectedDocUpdate = expectedXMLDocument(updateFilename);

        // convert actual string to xml doc
        Document readDocUpdate = convertStringToXMLDocument(readContentUpdate);

        assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        contentHandle.close();
        updateHandle.close();
        client.release();
    }
    // End of TestInputSourceHandle

    // Begin TestInputStreamHandle
    @Test
    public void testXmlCRUD_InputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
    {
        String filename = "xml-original-test.xml";
        String uri = "/write-xml-inputstreamhandle/";

        System.out.println("Running testXmlCRUD_InputStreamHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");
        // read docs
        InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "XML");

        // get the contents
        InputStream fileRead = contentHandle.get();

        String readContent = convertInputStreamToString(fileRead);

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);

        // convert actual string to xml doc
        Document readDoc = convertStringToXMLDocument(readContent);

        assertXMLEqual("Write XML difference", expectedDoc, readDoc);

        // update the doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "XML");

        // get the contents
        InputStream fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertInputStreamToString(fileReadUpdate);

        // get xml document for expected result
        Document expectedDocUpdate = expectedXMLDocument(updateFilename);

        // convert actual string to xml doc
        Document readDocUpdate = convertStringToXMLDocument(readContentUpdate);

        assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }

    @Test
    public void testTextCRUD_InputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "text-original.txt";
        String uri = "/write-text-inputstreamhandle/";

        System.out.println("Running testTextCRUD_InputStreamHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingInputStreamHandle(client, filename, uri, "Text");
        // read docs
        InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Text");

        // get the contents
        InputStream fileRead = contentHandle.get();

        String readContent = convertInputStreamToString(fileRead);

        String expectedContent = "hello world, welcome to java API";

        assertEquals( expectedContent.trim(), readContent.trim());

        // update the doc
        // acquire the content for update
        String updateFilename = "text-updated.txt";
        updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "Text");

        // read the document
        InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Text");

        // get the contents
        InputStream fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertInputStreamToString(fileReadUpdate);

        String expectedContentUpdate = "hello world, welcome to java API after new updates";

        assertEquals( expectedContentUpdate.trim(), readContentUpdate.toString().trim());

        // delete the document
        deleteDocument(client, uri + filename, "Text");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "Text"));

        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "Text");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }

    @Test
    public void testJsonCRUD_InputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "json-original.json";
        String uri = "/write-json-inputstreamhandle/";

        System.out.println("Running testJsonCRUD_InputStreamHandle");

        ObjectMapper mapper = new ObjectMapper();

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingInputStreamHandle(client, filename, uri, "JSON");
        // read docs
        InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");

        // get the contents
        InputStream fileRead = contentHandle.get();
        JsonNode readContent = mapper.readTree(fileRead);

        // get expected contents
        JsonNode expectedContent = expectedJSONDocument(filename);

        assertTrue( readContent.equals(expectedContent));

        // update the doc
        // acquire the content for update
        String updateFilename = "json-updated.json";
        updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "JSON");

        // read the document
        InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");

        // get the contents
        InputStream fileReadUpdate = updateHandle.get();

        JsonNode readContentUpdate = mapper.readTree(fileReadUpdate);

        // get expected contents
        JsonNode expectedContentUpdate = expectedJSONDocument(updateFilename);

        assertTrue( readContentUpdate.equals(expectedContentUpdate));

        // delete the document
        deleteDocument(client, uri + filename, "JSON");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "JSON"));

        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }

    @Test
    public void testBinaryCRUD_InputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "Pandakarlino.jpg";
        String uri = "/write-bin-inputstreamhandle/";

        System.out.println("Running testBinaryCRUD_InputStreamHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingInputStreamHandle(client, filename, uri, "Binary");
        // read docs
        InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");

        // get the contents
        InputStream fileRead = contentHandle.get();

        // get the binary size
        int size = getBinarySize(fileRead);
        int expectedSize = 34543;

        assertEquals( expectedSize, size);

        // update the doc
        // acquire the content for update
        String updateFilename = "mlfavicon.png";
        updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "Binary");

        // read the document
        InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");

        // get the contents
        InputStream fileReadUpdate = updateHandle.get();

        // get the binary size
        int sizeUpdate = getBinarySize(fileReadUpdate);
        int expectedSizeUpdate = 3322;

        assertEquals( expectedSizeUpdate, sizeUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "Binary");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "Binary"));

        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }
    // End of TestInputStreamHandle

    // Begin TestOutputStreamHandle
    @Test
    public void testXmlCRUD_OutputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
    {
        String filename = "xml-original-test.xml";
        String uri = "/write-xml-outputstreamhandle/";

        System.out.println("Running testXmlCRUD_OutputStreamHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingOutputStreamHandle(client, filename, uri, "XML");
        // read docs
        InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "XML");
        // get the contents
        InputStream fileRead = contentHandle.get();
        String readContent = convertInputStreamToString(fileRead);

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);

        // convert actual string to xml doc
        Document readDoc = convertStringToXMLDocument(readContent);

        assertXMLEqual("Write XML difference", expectedDoc, readDoc);

        // update the doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentUsingOutputStreamHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "XML");

        // get the contents
        InputStream fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertInputStreamToString(fileReadUpdate);

        // get xml document for expected result
        Document expectedDocUpdate = expectedXMLDocument(updateFilename);

        // convert actual string to xml doc
        Document readDocUpdate = convertStringToXMLDocument(readContentUpdate);

        assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }

    @Test
    public void testTextCRUD_OutputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "text-original.txt";
        String uri = "/write-text-outputstreamhandle/";

        System.out.println("Running testTextCRUD_OutputStreamHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingOutputStreamHandle(client, filename, uri, "Text");
        // read docs
        InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Text");

        // get the contents
        InputStream fileRead = contentHandle.get();

        String readContent = convertInputStreamToString(fileRead);

        String expectedContent = "hello world, welcome to java API";

        assertEquals( expectedContent.trim(), readContent.trim());

        // update the doc
        // acquire the content for update
        String updateFilename = "text-updated.txt";
        updateDocumentUsingOutputStreamHandle(client, updateFilename, uri + filename, "Text");

        // read the document
        InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Text");

        // get the contents
        InputStream fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertInputStreamToString(fileReadUpdate);

        String expectedContentUpdate = "hello world, welcome to java API after new updates";

        assertEquals( expectedContentUpdate.trim(), readContentUpdate.toString().trim());

        // delete the document
        deleteDocument(client, uri + filename, "Text");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "Text");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }

    @Test
    public void testJsonCRUD_OutputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "json-original.json";
        String uri = "/write-json-outputstreamhandle/";

        System.out.println("Running testJsonCRUD_OutputStreamHandle");

        ObjectMapper mapper = new ObjectMapper();

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingOutputStreamHandle(client, filename, uri, "JSON");
        // read docs
        InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");

        // get the contents
        InputStream fileRead = contentHandle.get();
        JsonNode readContent = mapper.readTree(fileRead);

        // get expected contents
        JsonNode expectedContent = expectedJSONDocument(filename);

        assertTrue( readContent.equals(expectedContent));

        // update the doc
        // acquire the content for update
        String updateFilename = "json-updated.json";
        updateDocumentUsingOutputStreamHandle(client, updateFilename, uri + filename, "JSON");

        // read the document
        InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");

        // get the contents
        InputStream fileReadUpdate = updateHandle.get();

        JsonNode readContentUpdate = mapper.readTree(fileReadUpdate);

        // get expected contents
        JsonNode expectedContentUpdate = expectedJSONDocument(updateFilename);

        assertTrue( readContentUpdate.equals(expectedContentUpdate));

        // delete the document
        deleteDocument(client, uri + filename, "JSON");

        // read the deleted document
        // assertFalse( isDocumentExist(client, uri +
        // filename, "JSON"));

        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "JSON");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }

    @Test
    public void testBinaryCRUD_OutputStreamHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "Pandakarlino.jpg";
        String uri = "/write-bin-outputstreamhandle/";

        System.out.println("Running testBinaryCRUD_OutputStreamHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingOutputStreamHandle(client, filename, uri, "Binary");
        // read docs
        InputStreamHandle contentHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");

        // get the contents
        InputStream fileRead = contentHandle.get();

        // get the binary size
        int size = getBinarySize(fileRead);
        int expectedSize = 34543;

        assertEquals( expectedSize, size);

        // update the doc
        // acquire the content for update
        String updateFilename = "mlfavicon.png";
        updateDocumentUsingOutputStreamHandle(client, updateFilename, uri + filename, "Binary");

        // read the document
        InputStreamHandle updateHandle = readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");

        // get the contents
        InputStream fileReadUpdate = updateHandle.get();

        // get the binary size
        int sizeUpdate = getBinarySize(fileReadUpdate);
        int expectedSizeUpdate = 3322;

        assertEquals( expectedSizeUpdate, sizeUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "Binary");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "Binary");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        client.release();
    }
    // End of TestOutputStreamHandle

    // Begin TestReaderHandle
    @Test
    public void testXmlCRUD_ReaderHandle() throws KeyManagementException, NoSuchAlgorithmException, FileNotFoundException, IOException, SAXException, ParserConfigurationException
    {
        System.out.println("Running testXmlCRUD_ReaderHandle");

        String filename = "xml-original-test.xml";
        String uri = "/write-xml-readerhandle/";

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setNormalizeWhitespace(true);

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write the doc
        writeDocumentReaderHandle(client, filename, uri, "XML");
        // read the document
        ReaderHandle readHandle = readDocumentReaderHandle(client, uri + filename, "XML");

        // access the document content
        Reader fileRead = readHandle.get();

        String readContent = convertReaderToString(fileRead);

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);

        // convert actual string to xml doc
        Document readDoc = convertStringToXMLDocument(readContent);

        assertXMLEqual("Write XML difference", expectedDoc, readDoc);

        // update the doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentReaderHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        ReaderHandle updateHandle = readDocumentReaderHandle(client, uri + filename, "XML");

        // access the document content
        Reader fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertReaderToString(fileReadUpdate);

        // get xml document for expected result
        Document expectedDocUpdate = expectedXMLDocument(updateFilename);

        // convert actual string to xml doc
        Document readDocUpdate = convertStringToXMLDocument(readContentUpdate);

        assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        // read the deleted document
        String exception = "";
        try {
            readDocumentReaderHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release the client
        client.release();
    }

    @Test
    public void testTextCRUD_ReaderHandle() throws KeyManagementException, NoSuchAlgorithmException, FileNotFoundException, IOException
    {
        System.out.println("Running testTextCRUD_ReaderHandle");

        String filename = "text-original.txt";
        String uri = "/write-text-readerhandle/";

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write the doc
        writeDocumentReaderHandle(client, filename, uri, "Text");
        // read the document
        ReaderHandle readHandle = readDocumentReaderHandle(client, uri + filename, "Text");

        // access the document content
        Reader fileRead = readHandle.get();

        String expectedContent = "hello world, welcome to java API";

        String readContent = convertReaderToString(fileRead);

        assertEquals( expectedContent, readContent);

        // update the doc
        // acquire the content for update
        String updateFilename = "text-updated.txt";
        updateDocumentReaderHandle(client, updateFilename, uri + filename, "Text");

        // read the document
        ReaderHandle updateHandle = readDocumentReaderHandle(client, uri + filename, "Text");

        // access the document content
        Reader fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertReaderToString(fileReadUpdate);

        String expectedContentUpdate = "hello world, welcome to java API after new updates";

        assertEquals( expectedContentUpdate, readContentUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "Text");

        // read the deleted document
        String exception = "";
        try {
            readDocumentReaderHandle(client, uri + filename, "Text");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release the client
        client.release();
    }

    @Test
    public void testJsonCRUD_ReaderHandle() throws KeyManagementException, NoSuchAlgorithmException, FileNotFoundException, IOException
    {
        System.out.println("Running testJsonCRUD_ReaderHandle");

        String filename = "json-original.json";
        String uri = "/write-json-readerhandle/";

        ObjectMapper mapper = new ObjectMapper();

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write the doc
        writeDocumentReaderHandle(client, filename, uri, "JSON");
        // read the document
        ReaderHandle readHandle = readDocumentReaderHandle(client, uri + filename, "JSON");

        // access the document content
        Reader fileRead = readHandle.get();
        JsonNode readContent = mapper.readTree(fileRead);

        // get expected contents
        JsonNode expectedContent = expectedJSONDocument(filename);

        assertTrue( readContent.equals(expectedContent));

        // update the doc
        // acquire the content for update
        String updateFilename = "json-updated.json";
        updateDocumentReaderHandle(client, updateFilename, uri + filename, "JSON");

        // read the document
        ReaderHandle updateHandle = readDocumentReaderHandle(client, uri + filename, "JSON");

        // access the document content
        Reader fileReadUpdate = updateHandle.get();
        JsonNode readContentUpdate = mapper.readTree(fileReadUpdate);

        // get expected contents
        JsonNode expectedContentUpdate = expectedJSONDocument(updateFilename);

        assertTrue( readContentUpdate.equals(expectedContentUpdate));

        // delete the document
        deleteDocument(client, uri + filename, "JSON");

        // read the deleted document
        String exception = "";
        try {
            readDocumentReaderHandle(client, uri + filename, "JSON");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release the client
        client.release();
    }
    // End of TestReaderHandle

    // Begin TestSourceHandle
    @Test
    public void testXmlCRUD_SourceHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException, TransformerException
    {
        String filename = "xml-original-test.xml";
        String uri = "/write-xml-sourcehandle/";

        System.out.println("Running testXmlCRUD_SourceHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingInputStreamHandle(client, filename, uri, "XML");
        // read docs
        SourceHandle contentHandle = readDocumentUsingSourceHandle(client, uri + filename, "XML");

        // get the contents
        Source fileRead = contentHandle.get();

        String readContent = convertSourceToString(fileRead);

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);

        // convert actual string to xml doc
        Document readDoc = convertStringToXMLDocument(readContent);

        assertXMLEqual("Write XML difference", expectedDoc, readDoc);

        // update the doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentUsingInputStreamHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        SourceHandle updateHandle = readDocumentUsingSourceHandle(client, uri + filename, "XML");

        // get the contents
        Source fileReadUpdate = updateHandle.get();

        String readContentUpdate = convertSourceToString(fileReadUpdate);

        // get xml document for expected result
        Document expectedDocUpdate = expectedXMLDocument(updateFilename);

        // convert actual string to xml doc
        Document readDocUpdate = convertStringToXMLDocument(readContentUpdate);

        assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingInputStreamHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release client
        contentHandle.close();
        updateHandle.close();
        client.release();
    }
    // End of TestSourceHandle

    // Begin TestStringHandle
    @Test
    public void testXmlCRUD_StringHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
    {
        String filename = "xml-original-test.xml";
        String uri = "/write-xml-string/";

        System.out.println("Running testXmlCRUD_StringHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingStringHandle(client, filename, uri, "XML");
        // read docs
        StringHandle contentHandle = readDocumentUsingStringHandle(client, uri + filename, "XML");

        // get the contents
        // File fileRead = contentHandle.get();

        String readContent = contentHandle.get();

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);

        // convert actual string to xml doc
        Document readDoc = convertStringToXMLDocument(readContent);

        assertXMLEqual("Write XML difference", expectedDoc, readDoc);

        // update the doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentUsingStringHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        StringHandle updateHandle = readDocumentUsingStringHandle(client, uri + filename, "XML");

        // get the contents
        String readContentUpdate = updateHandle.get();

        // get xml document for expected result
        Document expectedDocUpdate = expectedXMLDocument(updateFilename);

        // convert actual string to xml doc
        Document readDocUpdate = convertStringToXMLDocument(readContentUpdate);

        assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        // read the deleted document
        String exception = "";
        try {
            readDocumentUsingFileHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-xml-string/xml-original-test.xml";
        assertEquals( expectedException, exception);

        // release client
        client.release();
    }

    @Test
    public void testTextCRUD_StringHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "text-original.txt";
        String uri = "/write-text-stringhandle/";

        System.out.println("Running testTextCRUD_StringHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingStringHandle(client, filename, uri, "Text");
        // read docs
        StringHandle contentHandle = readDocumentUsingStringHandle(client, uri + filename, "Text");

        // get the contents
        // File fileRead = contentHandle.get();

        String readContent = contentHandle.get();

        String expectedContent = "hello world, welcome to java API";

        assertEquals( expectedContent.trim(), readContent.trim());

        // update the doc
        // acquire the content for update
        String updateFilename = "text-updated.txt";
        updateDocumentUsingStringHandle(client, updateFilename, uri + filename, "Text");

        // read the document
        StringHandle updateHandle = readDocumentUsingStringHandle(client, uri + filename, "Text");

        // get the contents
        // File fileReadUpdate = updateHandle.get();

        String readContentUpdate = updateHandle.get();

        String expectedContentUpdate = "hello world, welcome to java API after new updates";

        assertEquals( expectedContentUpdate.trim(), readContentUpdate.toString().trim());

        // delete the document
        deleteDocument(client, uri + filename, "Text");

        String exception = "";
        try {
            readDocumentUsingFileHandle(client, uri + filename, "Text");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-text-stringhandle/text-original.txt";
        assertEquals( expectedException, exception);

        // release client
        client.release();
    }

    @Test
    public void testJsonCRUD_StringHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        String filename = "json-original.json";
        String uri = "/write-json-stringhandle/";

        System.out.println("Running testJsonCRUD_StringHandle");

        ObjectMapper mapper = new ObjectMapper();

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write docs
        writeDocumentUsingStringHandle(client, filename, uri, "JSON");
        // read docs
        StringHandle contentHandle = readDocumentUsingStringHandle(client, uri + filename, "JSON");

        // get the contents
        JsonNode readContent = mapper.readValue(contentHandle.get(), JsonNode.class);

        // get expected contents
        JsonNode expectedContent = expectedJSONDocument(filename);

        assertTrue( readContent.equals(expectedContent));

        // update the doc
        // acquire the content for update
        String updateFilename = "json-updated.json";
        updateDocumentUsingFileHandle(client, updateFilename, uri + filename, "JSON");

        // read the document
        FileHandle updateHandle = readDocumentUsingFileHandle(client, uri + filename, "JSON");

        // get the contents
        File fileReadUpdate = updateHandle.get();

        JsonNode readContentUpdate = mapper.readTree(fileReadUpdate);

        // get expected contents
        JsonNode expectedContentUpdate = expectedJSONDocument(updateFilename);

        assertTrue( readContentUpdate.equals(expectedContentUpdate));

        // delete the document
        deleteDocument(client, uri + filename, "JSON");

        String exception = "";
        try {
            readDocumentUsingFileHandle(client, uri + filename, "JSON");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "com.marklogic.client.ResourceNotFoundException: Local message: Could not read non-existent document. Server Message: RESTAPI-NODOCUMENT: (err:FOER0000) Resource or document does not exist:  category: content message: /write-json-stringhandle/json-original.json";
        assertEquals( expectedException, exception);

        // release client
        client.release();
    }

    @Test
    public void testBug22356_StringHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException
    {
        System.out.println("Running testBug22356_StringHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // read docs
        StringHandle contentHandle = null;
        try {
            // get the contents
            contentHandle.get();
        } catch (NullPointerException e) {
            System.out.println("Null pointer Exception is expected noy an Empty Value");
            e.toString();
        }

        // release client
        client.release();
    }
    // End of TestStringHandle

    // Begin TestXMLEventReaderHandle
    @Test
    public void testXmlCRUD_XMLEventReaderHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException, TransformerException,
            XMLStreamException
    {
        String filename = "xml-original-test.xml";
        String uri = "/write-xml-XMLEventReaderHandle/";

        System.out.println("Running testXmlCRUD_XMLEventReaderHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write the doc
        writeDocumentReaderHandle(client, filename, uri, "XML");
        // read the document
        XMLEventReaderHandle readHandle = readDocumentUsingXMLEventReaderHandle(client, uri + filename, "XML");

        // access the document content
        String readContentCrop = readHandle.toString();
        System.out.println(readContentCrop);

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);

        String expectedContent = convertXMLDocumentToString(expectedDoc);
        System.out.println(expectedContent);
        // convert actual string to xml doc
        Document readDoc = convertStringToXMLDocument(readContentCrop);

        assertXMLEqual("Write XML difference", expectedDoc, readDoc);

        // update the doc
        // acquire the content for update
        String updateFilename = "xml-updated-test.xml";
        updateDocumentReaderHandle(client, updateFilename, uri + filename, "XML");

        // read the document
        XMLEventReaderHandle updateHandle = readDocumentUsingXMLEventReaderHandle(client, uri + filename, "XML");

        // access the document content
        String readContentUpdateCrop = updateHandle.toString();
        // get xml document for expected result
        Document expectedDocUpdate = expectedXMLDocument(updateFilename);

        // convert actual string to xml doc
        Document readDocUpdate = convertStringToXMLDocument(readContentUpdateCrop);

        assertXMLEqual("Write XML difference", expectedDocUpdate, readDocUpdate);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        String exception = "";
        try {
            readDocumentReaderHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release the client
        client.release();
    }
    // End of TestXMLEventReaderHandle

    // Begin TestXMLStreamReaderHandle
    @Test
    public void testXmlCRUD_XMLStreamReaderHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, SAXException, ParserConfigurationException, TransformerException,
            XMLStreamException
    {
        String filename = "xml-original-test.xml";
        String uri = "/write-xml-XMLStreamReaderHandle/";

        System.out.println("Running testXmlCRUD_XMLStreamReaderHandle");

        // connect the client
        DatabaseClient client = getDatabaseClient("rest-writer", "x", getConnType());

        // write the doc
        writeDocumentReaderHandle(client, filename, uri, "XML");
        // read the document
        XMLStreamReaderHandle readHandle = readDocumentUsingXMLStreamReaderHandle(client, uri + filename, "XML");

        // access the document content
        XMLStreamReader fileRead = readHandle.get();
        String readContent = convertXMLStreamReaderToString(fileRead);

        // get xml document for expected result
        Document expectedDoc = expectedXMLDocument(filename);
        String expectedContent = convertXMLDocumentToString(expectedDoc);
        expectedContent = "null" + expectedContent.substring(expectedContent.indexOf("<name>") + 6, expectedContent.indexOf("</name>"));
        assertEquals( expectedContent, readContent);

        // delete the document
        deleteDocument(client, uri + filename, "XML");

        String exception = "";
        try {
            readDocumentReaderHandle(client, uri + filename, "XML");
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "Could not read non-existent document";
        boolean documentIsDeleted = exception.contains(expectedException);
        assertTrue( documentIsDeleted);

        // release the client
        client.release();
    }
    // End of TestXMLStreamReaderHandle
}
