/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.Transaction;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.*;
import com.marklogic.client.expression.CtsQueryBuilder;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.*;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.type.CtsQueryExpr;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;


public class TestBulkSearchWithStrucQueryDef extends AbstractFunctionalTest {

    private static final int BATCH_SIZE = 100;
    private static final String DIRECTORY = "/bulkSearch/";
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        createRESTUserWithPermissions("usr1", "password", getPermissionNode("flexrep-eval", Capability.READ), getCollectionNode("http://permission-collections/"), "rest-writer",
                "rest-reader");
        client = getDatabaseClient("usr1", "password", getConnType());
        loadJSONDocuments();
        loadXMLDocuments();
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        deleteRESTUser("usr1");
    }

    public static void loadJSONDocuments() throws IOException {
        int count = 1;
        JSONDocumentManager docMgr = client.newJSONDocumentManager();
        DocumentWriteSet writeset = docMgr.newWriteSet();

        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < 102; i++) {
            JsonNode jn = new ObjectMapper().readTree("{\"animal\":\"dog" + i + "\", \"says\":\"woof\"}");
            JacksonHandle jh = new JacksonHandle();
            jh.set(jn);
            writeset.add(DIRECTORY + "dog" + i + ".json", jh);
            map.put(DIRECTORY + "dog" + i + ".json", jn.toString());
            if (count % BATCH_SIZE == 0) {
                docMgr.write(writeset);
                writeset = docMgr.newWriteSet();
            }
            count++;
        }
        if (count % BATCH_SIZE > 0) {
            docMgr.write(writeset);
        }
    }

    public void validateRecord(DocumentRecord record, Format type) {
        assertNotNull( record);
        assertNotNull( record.getUri());
        assertTrue(record.getUri().startsWith(DIRECTORY));
        assertEquals( type, record.getFormat());
    }

    public static Document getDocContent(String xmltype) throws IOException, ParserConfigurationException, SAXException
    {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document content = docBuilder.newDocument();
        Element rootElement = content.createElement("foo");
        rootElement.appendChild(content.createTextNode(xmltype));
        content.appendChild(rootElement);

        // content.createTextNode(xmltype);
        return content;
    }

    public static void loadXMLDocuments() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException {
        int count = 1;
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        DocumentWriteSet writeset = docMgr.newWriteSet();
        for (int i = 0; i < 102; i++) {

            writeset.add(DIRECTORY + "foo" + i + ".xml", new DOMHandle(getDocContent("This is so foo with a bar " + i)));

            if (count % BATCH_SIZE == 0) {
                docMgr.write(writeset);
                writeset = docMgr.newWriteSet();
            }
            count++;
        }
        if (count % BATCH_SIZE > 0) {
            docMgr.write(writeset);
        }
    }

    @Test
    public void testBulkSearchSQDwithDifferentPageSizes() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
    TransformerException {

        int count;
        // Creating a txt document manager for bulk search
        TextDocumentManager docMgr = client.newTextDocumentManager();
        // using QueryManger for query definition and set the search criteria
        StructuredQueryBuilder qb = new StructuredQueryBuilder();
        StructuredQueryDefinition qd = qb.and(qb.term("foo", "bar"));

        // set document manager level settings for search response
        System.out.println("Default Page length setting on docMgr :" + docMgr.getPageLength());
        docMgr.setPageLength(1);
        docMgr.setSearchView(QueryView.RESULTS);
        docMgr.setNonDocumentFormat(Format.XML);
        assertEquals( "XML", docMgr.getNonDocumentFormat().toString());
        assertEquals( "RESULTS", docMgr.getSearchView().toString());
        assertEquals( 1, docMgr.getPageLength());
        // Search for documents where content has bar and get first result record,
        // get search handle on it
        SearchHandle sh = new SearchHandle();
        DocumentPage page = docMgr.search(qd, 0);
        // test for page methods
        assertEquals( 1, page.size());
        assertEquals( 1, page.getStart());
        assertEquals( 102, page.getTotalSize());
        assertEquals( 102, page.getTotalPages());
        // till the issue #78 get fixed
        assertTrue( page.isFirstPage());// this is bug
        assertFalse( page.isLastPage());
        assertTrue( page.hasContent());
        // Need the Issue #75 to be fixed
        assertFalse( page.hasPreviousPage());
        //
        long pageNo = 1;
        do {
            count = 0;
            page = docMgr.search(qd, pageNo, sh);
            if (pageNo > 1) {
                assertFalse( page.isFirstPage());
                assertTrue( page.hasPreviousPage());
            }
            while (page.hasNext()) {
                DocumentRecord rec = page.next();
                rec.getFormat();
                validateRecord(rec, Format.XML);
                // System.out.println(rec.getUri());
                count++;
            }
            MatchDocumentSummary[] mds = sh.getMatchResults();
            assertEquals( 1, mds.length);
            // since we set the query view to get only results, facet count supposed
            // be 0
            assertEquals( 0, sh.getFacetNames().length);

            assertEquals( page.size(), count);
            pageNo = pageNo + page.getPageSize();
        } while (!page.isLastPage());

        assertTrue( page.hasPreviousPage());
        assertEquals( 1, page.getPageSize());
        assertEquals( 102, page.getTotalSize());
        page = docMgr.search(qd, 103);
        assertFalse( page.hasContent());
    }

    // This test has set response to JSON and pass StringHandle with format as
    // JSON, expectint it to work, logged an issue 82
    @Test
    public void testBulkSearchSQDwithResponseFormatandStringHandle() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        QueryManager queryMgr = client.newQueryManager();
        StructuredQueryBuilder qb = new StructuredQueryBuilder();
        StructuredQueryDefinition qd = qb.and(qb.term("dog1", "dog11"));
        queryMgr.search(qd, new SearchHandle());

        docMgr.setNonDocumentFormat(Format.JSON);
        docMgr.setSearchView(QueryView.METADATA);
        docMgr.setMetadataCategories(Metadata.PERMISSIONS);

        StringHandle results = new StringHandle().withFormat(Format.JSON);
        DocumentPage page = docMgr.search(qd, 1, results);
        DocumentMetadataHandle mh = new DocumentMetadataHandle();
        while (page.hasNext()) {
            DocumentRecord rec = page.next();
            validateRecord(rec, Format.JSON);
            docMgr.readMetadata(rec.getUri(), mh);
            assertTrue( mh.getPermissions().containsKey("flexrep-eval"));
            assertTrue( mh.getCollections().isEmpty());
        }
        assertFalse( results.get().isEmpty());
    }

    // This test is testing SearchView options and search handle
    @Test
    public void testBulkSearchSQDwithJSONResponseFormat() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        QueryManager queryMgr = client.newQueryManager();
        StructuredQueryBuilder qb = new StructuredQueryBuilder();
        StructuredQueryDefinition qd = qb.and(qb.term("woof"));
        docMgr.setNonDocumentFormat(Format.JSON);

        docMgr.setSearchView(QueryView.FACETS);
        JacksonHandle jh = new JacksonHandle();
        docMgr.search(qd, 1, jh);

        assertTrue( jh.get().has("facets"));
        assertFalse( jh.get().has("results"));// Issue 84 is tracking this
        assertFalse( jh.get().has("metrics"));

        docMgr.setSearchView(QueryView.RESULTS);
        docMgr.search(qd, 1, jh);

        assertFalse( jh.get().has("facets"));
        assertTrue( jh.get().has("results"));
        assertFalse( jh.get().has("metrics"));// Issue 84 is tracking this

        docMgr.setSearchView(QueryView.METADATA);
        docMgr.search(qd, 1, jh);

        assertFalse( jh.get().has("facets"));
        assertFalse( jh.get().has("results"));
        assertTrue( jh.get().has("metrics"));

        docMgr.setSearchView(QueryView.ALL);
        docMgr.search(qd, 1, jh);

        assertTrue( jh.get().has("facets"));
        assertTrue( jh.get().has("results"));
        assertTrue( jh.get().has("metrics"));

        queryMgr.setView(QueryView.FACETS);
        queryMgr.search(qd, jh);
        System.out.println(jh.get().toString());
    }

    // This test is to verify the transactions, verifies the search works with
    // transaction before commit, after rollback and after commit
    @Test
    public void testBulkSearchSQDwithTransactionsandDOMHandle() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        DOMHandle results = new DOMHandle();
        StructuredQueryBuilder qb = new StructuredQueryBuilder();
        StructuredQueryDefinition qd = qb.and(qb.term("much", "thought"));
        Transaction t = client.openTransaction();
        try {
            int count = 1;
            XMLDocumentManager xmldocMgr = client.newXMLDocumentManager();
            DocumentWriteSet writeset = xmldocMgr.newWriteSet();
            for (int i = 0; i < 102; i++) {
                writeset.add(DIRECTORY + "boo" + i + ".xml", new DOMHandle(getDocumentContent("This is so too much thought " + i)));
                if (count % BATCH_SIZE == 0) {
                    xmldocMgr.write(writeset, t);
                    writeset = xmldocMgr.newWriteSet();
                }
                count++;
            }
            if (count % BATCH_SIZE > 0) {
                xmldocMgr.write(writeset, t);
            }
            count = 0;
            docMgr.setSearchView(QueryView.RESULTS);

            DocumentPage page = docMgr.search(qd, 1, results, t);
            while (page.hasNext()) {
                DocumentRecord rec = page.next();

                validateRecord(rec, Format.XML);
                count++;
            }
            assertTrue( page.hasContent());
            assertEquals( "102",
                    results.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total").getNodeValue());
            // System.out.println(results.get().getElementsByTagNameNS("*",
            // "response").item(0).getAttributes().getNamedItem("total").getNodeValue());

        } catch (Exception e) {
            throw e;
        } finally {
            t.rollback();
        }
        docMgr.search(qd, 1, results);
        System.out.println(convertXMLDocumentToString(results.get()));
        assertEquals( results.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total").getNodeValue(),
                "0");
    }

    // This test is to verify RAW XML structured Query
    @Test
    public void testBulkSearchRawXMLStrucQD() throws Exception {
        GenericDocumentManager docMgr = client.newDocumentManager();
        QueryManager queryMgr = client.newQueryManager();
        String rawXMLQuery =
                "<search:query " +
                        "xmlns:search='http://marklogic.com/appservices/search'>" +
                        " <search:or-query><search:term-query>" +
                        "<search:text>bar</search:text>" +
                        "</search:term-query>" +
                        "<search:term-query>" +
                        "<search:text>woof</search:text>" +
                        "</search:term-query> </search:or-query>" +
                        "</search:query>";
        StringHandle rh = new StringHandle(rawXMLQuery);
        RawStructuredQueryDefinition qd =
                queryMgr.newRawStructuredQueryDefinition(rh);
        DOMHandle dh = new DOMHandle();
        DocumentPage page = docMgr.search(qd, 1, dh);
        DocumentMetadataHandle mh = new DocumentMetadataHandle();

        int count = 1;
        while (count < 4)
        {
            page = docMgr.search(qd, count, dh);

            while (page.hasNext()) {
                DocumentRecord rec = page.next();
                if (rec.getMimetype().contains("xml")) {
                    validateRecord(rec, Format.XML);
                }
                else {
                    validateRecord(rec, Format.JSON);
                }
                docMgr.readMetadata(rec.getUri(), mh);
                assertTrue( mh.getPermissions().containsKey("flexrep-eval"));
                assertFalse( mh.getCollections().isEmpty());

            }
            System.out.println(this.convertXMLDocumentToString(dh.get()));
            assertEquals( "204", dh.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total")
                    .getNodeValue());
            count++;
        }
    }

    // This test is to verify extract-document-data & extract-path with Default
    // selected option query
    @Test
    public void testExtractDocumentData() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
        String tail = "</search:search>";
        String qtext4 = "<search:qtext>71 OR dog14</search:qtext>";
        QueryManager queryMgr = client.newQueryManager();
        String options =
                "<search:options>" +
                        "<search:extract-document-data>" +
                        "<search:extract-path>//foo</search:extract-path>" +
                        "<search:extract-path>//says</search:extract-path>" +
                        "</search:extract-document-data>" +
                        "</search:options>";
        // test XML response with extracted XML and JSON matches
        String combinedSearch = head + qtext4 + options + tail;
        RawCombinedQueryDefinition rawCombinedQueryDefinition =
                queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));
        SearchHandle results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertNotNull(summaries);
        assertEquals(2, summaries.length);
        for (MatchDocumentSummary summary : summaries) {
            ExtractedResult extracted = summary.getExtracted();
            if (Format.XML == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                assertEquals(1, extracted.size());
                Document item1 = extracted.next().getAs(Document.class);
                assertEquals( "This is so foo with a bar 71", item1.getFirstChild().getTextContent());
                continue;
            } else if (Format.JSON == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                assertEquals(1, extracted.size());
                for (ExtractedItem item : extracted) {
                    String stringJsonItem = item.getAs(String.class);
                    JsonNode nodeJsonItem = item.getAs(JsonNode.class);
                    if (nodeJsonItem.has("says")) {
                        assertEquals("{\"says\":\"woof\"}", stringJsonItem);
                        continue;
                    }
                    fail("unexpected extracted item:" + stringJsonItem);
                }
                continue;
            }
            fail("unexpected search result:" + summary.getUri());
        }
    }

    // This test is to verify extract-document-data & extract-path with
    // selected=exclude option query
    @Test
    public void testExtractDocumentData2() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
        String tail = "</search:search>";
        String qtext4 = "<search:qtext>71 OR dog14</search:qtext>";
        QueryManager queryMgr = client.newQueryManager();
        String options =
                "<search:options>" +
                        "<search:extract-document-data selected=\"exclude\">" +
                        "<search:extract-path>//foo</search:extract-path>" +
                        "<search:extract-path>//says</search:extract-path>" +
                        "</search:extract-document-data>" +
                        "</search:options>";
        // test XML response with extracted XML and JSON matches
        String combinedSearch = head + qtext4 + options + tail;
        RawCombinedQueryDefinition rawCombinedQueryDefinition =
                queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));
        SearchHandle results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertNotNull(summaries);
        assertEquals(2, summaries.length);
        for (MatchDocumentSummary summary : summaries) {
            ExtractedResult extracted = summary.getExtracted();
            if (Format.XML == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                System.out.println("EXTRACTED Size ==" + extracted.size());
                // TODO:: Bug 33921 also add test to include-with-ancestors
                assertEquals(0, extracted.size());
                // Document item1 = extracted.next().getAs(Document.class);
                // assertEquals(
                // item1.getFirstChild().getTextContent());
                continue;
            } else if (Format.JSON == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                assertEquals(1, extracted.size());
                for (ExtractedItem item : extracted) {
                    String stringJsonItem = item.getAs(String.class);
                    JsonNode nodeJsonItem = item.getAs(JsonNode.class);
                    if (nodeJsonItem.has("animal")) {
                        assertEquals("{\"animal\":\"dog14\"}", stringJsonItem);
                        continue;
                    }
                    fail("unexpected extracted item:" + stringJsonItem);
                }
                continue;
            }
            fail("unexpected search result:" + summary.getUri());
        }
    }

    // This test is to verify extract-document-data & extract-path with
    // selected=include-with-ancestors option query
    @Test
    public void testExtractDocumentData3() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
        String tail = "</search:search>";
        String qtext4 = "<search:qtext>71 OR dog14</search:qtext>";
        QueryManager queryMgr = client.newQueryManager();
        String options =
                "<search:options>" +
                        "<search:extract-document-data selected=\"include-with-ancestors\">" +
                        "<search:extract-path>//foo</search:extract-path>" +
                        "<search:extract-path>//says</search:extract-path>" +
                        "</search:extract-document-data>" +
                        "</search:options>";
        // test XML response with extracted XML and JSON matches
        String combinedSearch = head + qtext4 + options + tail;
        RawCombinedQueryDefinition rawCombinedQueryDefinition =
                queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));
        SearchHandle results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertNotNull(summaries);
        assertEquals(2, summaries.length);
        for (MatchDocumentSummary summary : summaries) {
            ExtractedResult extracted = summary.getExtracted();
            if (Format.XML == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                assertEquals(1, extracted.size());
                Document item1 = extracted.next().getAs(Document.class);
                assertEquals( "This is so foo with a bar 71", item1.getFirstChild().getTextContent());
                continue;
            } else if (Format.JSON == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                assertEquals(1, extracted.size());
                for (ExtractedItem item : extracted) {
                    String stringJsonItem = item.getAs(String.class);
                    JsonNode nodeJsonItem = item.getAs(JsonNode.class);
                    if (nodeJsonItem.has("says")) {
                        assertEquals("{\"says\":\"woof\"}", stringJsonItem);
                        continue;
                    }
                    fail("unexpected extracted item:" + stringJsonItem);
                }
                continue;
            }
            fail("unexpected search result:" + summary.getUri());
        }
    }

    // This test is to verify extract-document-data & extract-path with
    // selected=include option query
    @Test
    public void testExtractDocumentData4() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
        String tail = "</search:search>";
        String qtext4 = "<search:qtext>71 OR dog14</search:qtext>";
        QueryManager queryMgr = client.newQueryManager();
        String options =
                "<search:options>" +
                        "<search:extract-document-data selected=\"include\">" +
                        "<search:extract-path>//foo</search:extract-path>" +
                        "<search:extract-path>//says</search:extract-path>" +
                        "</search:extract-document-data>" +
                        "</search:options>";
        // test XML response with extracted XML and JSON matches
        String combinedSearch = head + qtext4 + options + tail;
        RawCombinedQueryDefinition rawCombinedQueryDefinition =
                queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));
        SearchHandle results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertNotNull(summaries);
        assertEquals(2, summaries.length);
        for (MatchDocumentSummary summary : summaries) {
            ExtractedResult extracted = summary.getExtracted();
            if (Format.XML == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                assertEquals(1, extracted.size());
                Document item1 = extracted.next().getAs(Document.class);
                assertEquals( "This is so foo with a bar 71", item1.getFirstChild().getTextContent());
                continue;
            } else if (Format.JSON == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                assertEquals(1, extracted.size());
                for (ExtractedItem item : extracted) {
                    String stringJsonItem = item.getAs(String.class);
                    JsonNode nodeJsonItem = item.getAs(JsonNode.class);
                    if (nodeJsonItem.has("says")) {
                        assertEquals("{\"says\":\"woof\"}", stringJsonItem);
                        continue;
                    }
                    fail("unexpected extracted item:" + stringJsonItem);
                }
                continue;
            }
            fail("unexpected search result:" + summary.getUri());
        }
    }

    // This test is to verify extract-document-data & extract-path with
    // selected=all option query
    @Test
    public void testExtractDocumentData5() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
        String tail = "</search:search>";
        String qtext4 = "<search:qtext>71 OR dog14</search:qtext>";
        QueryManager queryMgr = client.newQueryManager();
        String options =
                "<search:options>" +
                        "<search:extract-document-data selected=\"all\">" +
                        "<search:extract-path>//foo</search:extract-path>" +
                        "<search:extract-path>//says</search:extract-path>" +
                        "</search:extract-document-data>" +
                        "</search:options>";
        // test XML response with extracted XML and JSON matches
        String combinedSearch = head + qtext4 + options + tail;
        RawCombinedQueryDefinition rawCombinedQueryDefinition =
                queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));
        SearchHandle results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertNotNull(summaries);
        assertEquals(2, summaries.length);
        for (MatchDocumentSummary summary : summaries) {
            ExtractedResult extracted = summary.getExtracted();
            if (Format.XML == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                assertEquals(1, extracted.size());
                Document item1 = extracted.next().getAs(Document.class);
                assertEquals( "This is so foo with a bar 71", item1.getFirstChild().getTextContent());
                continue;
            } else if (Format.JSON == summary.getFormat()) {
                // we don't test for kind because it isn't sent in this case
                assertEquals(1, extracted.size());
                for (ExtractedItem item : extracted) {
                    String stringJsonItem = item.getAs(String.class);
                    JsonNode nodeJsonItem = item.getAs(JsonNode.class);
                    if (nodeJsonItem.has("says")) {
                        assertEquals("{\"animal\":\"dog14\", \"says\":\"woof\"}", stringJsonItem);
                        continue;
                    }
                    fail("unexpected extracted item:" + stringJsonItem);
                }
                continue;
            }
            fail("unexpected search result:" + summary.getUri());
        }
    }

    // This test is to verify RAW JSON structured query
    @Test
    public void testBulkSearchRawJSONStrucQD() {
        GenericDocumentManager docMgr = client.newDocumentManager();
        QueryManager queryMgr = client.newQueryManager();
        JacksonHandle jh = new JacksonHandle();
        ObjectMapper mapper = new ObjectMapper();
        // constructing JSON representation of Raw JSON Structured Query

        ObjectNode txtNode = mapper.createObjectNode();
        txtNode.putArray("text").add("woof");
        ObjectNode termQNode = mapper.createObjectNode();
        termQNode.set("term-query", txtNode);
        ObjectNode queriesArrayNode = mapper.createObjectNode();
        queriesArrayNode.putArray("queries").add(termQNode);

        ObjectNode txtNode2 = mapper.createObjectNode();
        txtNode2.putArray("text").add("bar");
        ObjectNode termQNode2 = mapper.createObjectNode();
        termQNode2.set("term-query", txtNode2);
        queriesArrayNode.withArray("queries").add(termQNode2);

        ObjectNode orQueryNode = mapper.createObjectNode();
        orQueryNode.set("or-query", queriesArrayNode);

        ObjectNode queryArrayNode = mapper.createObjectNode();
        queryArrayNode.putArray("queries").add(orQueryNode);
        ObjectNode mainNode = mapper.createObjectNode();
        mainNode.set("query", queryArrayNode);
        jh.set(mainNode);
        RawStructuredQueryDefinition qd = queryMgr.newRawStructuredQueryDefinition(jh);
        System.out.println(jh.get().toString());
        docMgr.setNonDocumentFormat(Format.JSON);
        JacksonHandle results = new JacksonHandle();
        DocumentPage page = docMgr.search(qd, 1, results);
        DocumentMetadataHandle mh = new DocumentMetadataHandle();

        int count = 1;
        while (count < 4)
        {
            page = docMgr.search(qd, count, results);

            while (page.hasNext()) {
                DocumentRecord rec = page.next();
                if (rec.getMimetype().contains("xml")) {
                    validateRecord(rec, Format.XML);
                }
                else {
                    validateRecord(rec, Format.JSON);
                }
                docMgr.readMetadata(rec.getUri(), mh);
                assertTrue( mh.getPermissions().containsKey("flexrep-eval"));
                assertFalse( mh.getCollections().isEmpty());
            }
            count++;
        }
        System.out.println(results.get().toString());
        assertEquals( "204", results.get().get("total").asText());
    }

    /*
     * Searching for boolean and string in XML element using value query. Purpose:
     * To validate QueryBuilder's new value methods (in 8.0) in XML document using
     * an element.
     *
     * Load a file that has a boolean value in a XML attribute and use query def
     * to search on that boolean value
     *
     * Methods used : value(StructuredQueryBuilder.TextIndex index, boolean)
     * value(StructuredQueryBuilder.TextIndex index, String)
     */
    @Test
    public void testQueryBuilderValueWithBooleanAndString() throws KeyManagementException, NoSuchAlgorithmException, XpathException, SAXException, IOException {

        String docId[] = { "play-persons.xml" };

        TextDocumentManager docMgr = client.newTextDocumentManager();
        QueryManager queryMgr = client.newQueryManager();
        DocumentWriteSet writeset = docMgr.newWriteSet();

        // Put meta-data
        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        metadataHandle.getCollections().addAll("my-collection1", "my-collection2");
        metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
        metadataHandle.getProperties().put("reviewed", true);
        metadataHandle.getProperties().put("myString", "foo");
        metadataHandle.getProperties().put("myInteger", 10);
        metadataHandle.getProperties().put("myDecimal", 34.56678);
        metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
        metadataHandle.setQuality(23);

        writeset.addDefault(metadataHandle);

        // Create a new document using StringHandle
        StringBuffer strBuf = new StringBuffer();

        strBuf.append("<PLAY>");
        strBuf.append("<TITLE>All's Well That Ends Well</TITLE>");
        strBuf.append("<PERSONAE>");
        strBuf.append("<TITLE>Dramatis Personae</TITLE>");

        strBuf.append("<PGROUP>");
        strBuf.append("<subgroup>true</subgroup>");

        strBuf.append("<PERSONA>KING OF FRANCE</PERSONA>");
        strBuf.append("<PERSONA>DUKE OF FLORENCE</PERSONA>");
        strBuf.append("<PERSONA>BERTRAM, Count of Rousillon.</PERSONA>");
        strBuf.append("<PERSONA>LAFEU, an old lord.</PERSONA>");
        strBuf.append("</PGROUP>");

        strBuf.append("<PGROUP>");
        strBuf.append("<subgroup>false</subgroup>");

        strBuf.append("<PERSONA>PAROLLES, a follower of Bertram.</PERSONA>");
        strBuf.append("<PERSONA>A Page. </PERSONA>");
        strBuf.append("</PGROUP>");

        strBuf.append("<PGROUP>");
        strBuf.append("<subgroup>false</subgroup>");
        strBuf.append("<PERSONA>COUNTESS OF ROUSILLON, mother to Bertram. </PERSONA>");
        strBuf.append("<PERSONA>HELENA, a gentlewoman protected by the Countess.</PERSONA>");
        strBuf.append("<PERSONA>An old Widow of Florence. </PERSONA>");
        strBuf.append("<PERSONA>DIANA, daughter to the Widow.</PERSONA>");
        strBuf.append("</PGROUP>");

        strBuf.append("<PGROUP>");
        strBuf.append("<subgroup>false</subgroup>");
        strBuf.append("<PERSONA>VIOLENTA</PERSONA>");
        strBuf.append("<PERSONA>MARIANA</PERSONA>");
        strBuf.append("<GRPDESCR>neighbours and friends to the Widow.</GRPDESCR>");
        strBuf.append("</PGROUP>");

        strBuf.append("<PERSONA>Lords, Officers, Soldiers, &amp;c., French and Florentine.</PERSONA>");
        strBuf.append("</PERSONAE>");
        strBuf.append("</PLAY>");

        writeset.add("/1/" + docId[0], new StringHandle().with(strBuf.toString()));
        docMgr.write(writeset);

        docMgr.write(writeset);

        // Search for the range with attribute value true in
        // rangeRelativeBucketConstraintOpt.xml document.
        StructuredQueryBuilder qb = new StructuredQueryBuilder();

        // Build an object that represents StructuredQueryBuilder.ElementAttribute
        // for use in values method
        // that is of type StructuredQueryBuilder.TextIndex

        QueryDefinition qd = qb.value(qb.element("subgroup"), false);

        // Create handle for the result
        StringHandle resultsHandle = new StringHandle().withFormat(Format.XML);
        queryMgr.search(qd, resultsHandle);

        // Get the result
        String resultDoc = resultsHandle.get();

        System.out.println(resultDoc);
        // Verify that search response has found 1 element attribute
        assertXpathEvaluatesTo("fn:doc(\"/1/play-persons.xml\")", "string(//*[local-name()='response']//*[local-name()='result']//@*[local-name()='path'])", resultDoc);
        assertXpathEvaluatesTo("3", "count(//*[local-name()='response']//*[local-name()='match'])", resultDoc);

        // Search for the following royal (XML ELEMENT) in all-well.xml document.
        StructuredQueryBuilder qbStr = new StructuredQueryBuilder();
        QueryDefinition qdStr = qbStr.value(qbStr.element("PERSONA"), "KING OF FRANCE", "DUKE OF FLORENCE", "BERTRAM, Count of Rousillon.", "LAFEU, an old lord.");

        // Create handle for the result
        StringHandle resultsHandleStr = new StringHandle().withFormat(Format.XML);
        queryMgr.search(qdStr, resultsHandleStr);

        // Get the result
        String resultDocStr = resultsHandleStr.get();

        System.out.println(resultDocStr);
        // Verify that search response has found 4 PERSONA elements under
        // /PLAY/PERSONAE
        assertXpathEvaluatesTo("fn:doc(\"/1/play-persons.xml\")", "string(//*[local-name()='response']//*[local-name()='result']//@*[local-name()='path'])", resultDocStr);
        assertXpathEvaluatesTo("4", "count(//*[local-name()='response']//*[local-name()='match'])", resultDocStr);
    }

    /*
     * This test is to verify extract-document-data & extract-path with
     * selected=include option query category, pageLength/limit, frequency,
     * direction, aggregatePath, and aggregate.
     */
    @Test
    public void testPageLenOptionsWithBulkSearch() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        DatabaseClient clientTmp = getDatabaseClient("rest-admin", "x", getConnType());
        String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
        String tail = "</search:search>";
        String qtext4 = "<search:qtext>71 OR dog14 OR dog15 OR dog21 OR dog22</search:qtext>";
        String queryOptionName = "bulkPageOptions";

        QueryManager queryMgr = clientTmp.newQueryManager();
        // create query options manager
        QueryOptionsManager optionsMgr = clientTmp.newServerConfigManager().newQueryOptionsManager();
        // This options is dynamic
        String optionsWithPageLen = "<search:options>" +
                "<search:page-length>3</search:page-length>" +
                "<search:extract-document-data selected=\"exclude\">" +
                "<search:extract-path>//foo</search:extract-path>" +
                "<search:extract-path>//says</search:extract-path>" +
                "</search:extract-document-data>" +
                "</search:options>";

        String pageLengthOpt = "<options xmlns=\"http://marklogic.com/appservices/search\">" +
                "<page-length>4</page-length></options>";
        StringHandle handle = new StringHandle(pageLengthOpt);

        // write query options
        optionsMgr.writeOptions(queryOptionName, handle);

        // test XML response with extracted XML and JSON matches
        String combinedSearch = head + qtext4 + optionsWithPageLen + tail;
        RawCombinedQueryDefinition rawCombinedQueryDefinition =
                queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));
        SearchHandle results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertNotNull(summaries);
        int nSummariesLen = summaries.length;
        int nPageLen = results.getPageLength();
        System.out.println("Page Length from search results is " + nPageLen);
        System.out.println("Summaries Length is " + nSummariesLen);

        // 1 - Verify that page length from client takes precedence. Both client and
        // persisted options have page length
        assertEquals( 3, nPageLen);
        assertEquals( 3, nSummariesLen);

        String optionsWithoutPageLen = "<search:options>" +
                "<search:extract-document-data selected=\"exclude\">" +
                "<search:extract-path>//foo</search:extract-path>" +
                "<search:extract-path>//says</search:extract-path>" +
                "</search:extract-document-data>" +
                "</search:options>";
        // test XML response with extracted XML and JSON matches
        String combinedSearchNoPage = head + qtext4 + optionsWithoutPageLen + tail;
        RawCombinedQueryDefinition rawCombinedQueryDefinitionNoPage =
                queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearchNoPage).withMimetype("application/xml"));
        SearchHandle resultsNoPage = queryMgr.search(rawCombinedQueryDefinitionNoPage, new SearchHandle());
        MatchDocumentSummary[] summariesNoPage = resultsNoPage.getMatchResults();
        assertNotNull(summariesNoPage);
        int nSummariesLenNP = summariesNoPage.length;
        int nPageLenNP = resultsNoPage.getPageLength();
        System.out.println("Page Length from search results is " + nPageLenNP);
        System.out.println("Summaries Length is " + nSummariesLenNP);
        // 2 - Verify that without page length from client precedence is for Server
        // options.
        assertTrue( nPageLenNP == 10);

        // 3 - Search with options - Persisted options take precedence
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("woof");
        SearchHandle searchHandle = new SearchHandle();
        queryMgr.search(querydef, searchHandle);

        MatchDocumentSummary[] docSummaries = searchHandle.getMatchResults();
        assertNotNull(docSummaries);
        int nSummariesLenOpt = docSummaries.length;

        System.out.println("Summaries Length is " + nSummariesLenOpt);
        // Verify that page length from client options takes precedence.
        assertTrue( nSummariesLenOpt == 4);

        // 4 - Search WITHOUT options - Persisted options take precedence
        StringQueryDefinition querydefNoOpts = queryMgr.newStringDefinition();
        querydefNoOpts.setCriteria("woof");
        SearchHandle searchHandleNoOpts = new SearchHandle();
        queryMgr.search(querydefNoOpts, searchHandleNoOpts);

        MatchDocumentSummary[] docSummariesNoOpts = searchHandleNoOpts.getMatchResults();
        assertNotNull(docSummariesNoOpts);
        int nSummariesLenNoOpts = docSummaries.length;

        System.out.println("Summaries Length is " + nSummariesLenNoOpts);
        // Verify that page length from client options takes precedence.
        assertTrue( nSummariesLenNoOpts == 4);
    }

    /*
     * This test is to verify extract-document-data & extract-path with
     * selected=include option query category, pageLength/limit, frequency,
     * direction, aggregatePath, and aggregate.
     */
    @Test
    public void testDirectionOptionsWithBulkSearch() throws Exception {

        DatabaseClient clientTmp = getDatabaseClient("rest-admin", "x", getConnType());
        String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
        String tail = "</search:search>";
        String qtext4 = "<search:qtext>dog10 OR dog14 OR dog15 OR dog21 OR dog22</search:qtext>";
        String queryOptionName = "bulkSortOptions";

        QueryManager queryMgr = clientTmp.newQueryManager();
        // create query options manager
        QueryOptionsManager optionsMgr = clientTmp.newServerConfigManager().newQueryOptionsManager();
        // This options is dynamic. Descending sort order.
        String optionsWithSortDesc = "<search:options>" +
                "<search:page-length>20</search:page-length>" +
                "<search:extract-document-data selected=\"exclude\">" +
                "<search:extract-path>//foo</search:extract-path>" +
                "<search:extract-path>//says</search:extract-path>" +
                "</search:extract-document-data>" +
                "<search:sort-order direction='descending'>" +
                "<search:json-property>animal</search:json-property>" +
                "</search:sort-order>" +
                "</search:options>";
        // Ascending sort order
        String serverAscOpts = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
                "<search:return-metrics>false</search:return-metrics>" +
                "<search:return-qtext>false</search:return-qtext>" +
                "<search:sort-order direction='ascending'>" +
                "<search:json-property>animal</search:json-property>" +
                "</search:sort-order>" +
                "<search:transform-results apply='raw'/>" +
                "</search:options>";
        StringHandle handle = new StringHandle(serverAscOpts);

        // write query options
        optionsMgr.writeOptions(queryOptionName, handle);

        // 1 - Verify that client overides server options - descending
        String combinedSearch = head + qtext4 + optionsWithSortDesc + tail;
        RawCombinedQueryDefinition rawCombinedQueryDefinition =
                queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

        SearchHandle results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertNotNull(summaries);
        int nSummariesLen = summaries.length;
        System.out.println("Summaries Length is " + nSummariesLen);
        assertEquals( 5, nSummariesLen);

        // Make sure we have proper descending sort.
        LinkedHashMap<String, String> descHashMap = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> exptdHashMap = new LinkedHashMap<String, String>();

        exptdHashMap.put("dog22", "Content");
        exptdHashMap.put("dog21", "Content");
        exptdHashMap.put("dog15", "Content");
        exptdHashMap.put("dog14", "Content");
        exptdHashMap.put("dog10", "Content");

        for (MatchDocumentSummary summary : summaries) {
            ExtractedResult extracted = summary.getExtracted();
            if (summary.getFormat() == Format.JSON) {
                for (ExtractedItem item : extracted) {
                    String stringJsonItem = item.getAs(String.class);
                    JsonNode nodeJsonItem = item.getAs(JsonNode.class);
                    if (nodeJsonItem.has("animal")) {
                        descHashMap.put(nodeJsonItem.path("animal").asText(), "Content");
                        continue;
                    }
                    fail("unexpected extracted item:" + stringJsonItem);
                }
                continue;
            }
            else
                fail("unexpected search result:" + summary.getUri());
        }

        assertTrue( descHashMap.equals(exptdHashMap));

        // 2 - Verify that without sort from client, precedence is for Server
        // options
        String optionsWithoutSortDesc = "<search:options>" +
                "<search:page-length>20</search:page-length>" +
                "<search:extract-document-data selected=\"exclude\">" +
                "<search:extract-path>//foo</search:extract-path>" +
                "<search:extract-path>//says</search:extract-path>" +
                "</search:extract-document-data>" +
                "</search:options>";
        String combinedSearchNoSort = head + qtext4 + optionsWithoutSortDesc + tail;
        RawCombinedQueryDefinition rawCombinedQueryDefinitionNoSort =
                queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearchNoSort).withMimetype("application/xml"));

        SearchHandle resultsNoSort = queryMgr.search(rawCombinedQueryDefinitionNoSort, new SearchHandle());
        MatchDocumentSummary[] summariesNoSort = resultsNoSort.getMatchResults();
        assertNotNull(summariesNoSort);
        int nSummariesNoSortLen = summariesNoSort.length;
        System.out.println("Summaries Length is " + nSummariesNoSortLen);
        assertEquals( 5, nSummariesNoSortLen);

        // Make sure we have proper ascending sort.
        LinkedHashMap<String, String> ascHashMap = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> exptdAscHashMap = new LinkedHashMap<String, String>();

        exptdAscHashMap.put("dog10", "Content");
        exptdAscHashMap.put("dog14", "Content");
        exptdAscHashMap.put("dog15", "Content");
        exptdAscHashMap.put("dog21", "Content");
        exptdAscHashMap.put("dog22", "Content");

        for (MatchDocumentSummary summary : summariesNoSort) {
            ExtractedResult extracted = summary.getExtracted();
            if (summary.getFormat() == Format.JSON) {
                for (ExtractedItem item : extracted) {
                    String stringJsonItem = item.getAs(String.class);
                    JsonNode nodeJsonItem = item.getAs(JsonNode.class);
                    if (nodeJsonItem.has("animal")) {
                        ascHashMap.put(nodeJsonItem.path("animal").asText(), "Content");
                        continue;
                    }
                    fail("unexpected extracted item:" + stringJsonItem);
                }
                continue;
            }
            else
                fail("unexpected search result:" + summary.getUri());
        }
        // 2 - Verify server options is used - ascending
        assertTrue( ascHashMap.equals(exptdAscHashMap));

        // 3 - Search with options - Persisted options take precedence
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("dog10 OR dog14 OR dog15 OR dog21 OR dog22");
        SearchHandle searchHandle = new SearchHandle();
        queryMgr.search(querydef, searchHandle);

        LinkedHashMap<String, String> WithOptionsAscHashMap = new LinkedHashMap<String, String>();

        MatchDocumentSummary[] docSummaries = searchHandle.getMatchResults();
        assertNotNull(docSummaries);
        for (int i = 0; i < docSummaries.length; i++) {
            String str = docSummaries[i].getPath().split(DIRECTORY)[1].split("\\.")[0];
            if (str != null) {
                WithOptionsAscHashMap.put(str, "Content");
            }
            else {
                fail("unexpected search result:");
            }
        }

        // 3 - Verify server options is used - ascending
        assertTrue( WithOptionsAscHashMap.equals(exptdAscHashMap));

        // 4 - Search without options - Persisted options take precedence
        StringQueryDefinition querydefNoOpts = queryMgr.newStringDefinition(queryOptionName);
        querydefNoOpts.setCriteria("dog10 OR dog14 OR dog15 OR dog21 OR dog22");
        SearchHandle searchHandleNoOpts = new SearchHandle();
        queryMgr.search(querydefNoOpts, searchHandleNoOpts);

        LinkedHashMap<String, String> NoOptsAscHashMap = new LinkedHashMap<String, String>();

        MatchDocumentSummary[] docSummariesNoOpts = searchHandleNoOpts.getMatchResults();
        assertNotNull(docSummariesNoOpts);
        for (int i = 0; i < docSummariesNoOpts.length; i++) {
            String str = docSummaries[i].getPath().split(DIRECTORY)[1].split("\\.")[0];
            if (str != null) {
                NoOptsAscHashMap.put(str, "Content");
            }
            else {
                fail("unexpected search result:");
            }
        }
        // 4 - Verify server options is used - ascending
        assertTrue( NoOptsAscHashMap.equals(exptdAscHashMap));
    }

    @Test
    public void testStartPageOnQueryManager() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        Transaction t = null;
        try {
            DatabaseClient clientTmp = getDatabaseClient("rest-admin", "x", getConnType());
            String head = "<search:search xmlns:search=\"http://marklogic.com/appservices/search\">";
            String tail = "</search:search>";
            String qtext4 = "<search:qtext>dog10 OR dog11 OR dog12 OR dog13 OR dog14 OR dog15 OR dog16 OR dog17 OR dog18 OR dog19 OR dog20 OR dog21 OR dog22 OR dog23</search:qtext>";

            QueryManager queryMgr = clientTmp.newQueryManager();
            queryMgr.setPageLength(2);
            // create query options manager
            // This options is dynamic. Descending sort order.
            String optionsWithSortDesc = "<search:options>" +
                    "<search:page-length>2</search:page-length>" +
                    "<search:extract-document-data selected=\"exclude\">" +
                    "<search:extract-path>//foo</search:extract-path>" +
                    "<search:extract-path>//says</search:extract-path>" +
                    "</search:extract-document-data>" +
                    "<search:sort-order direction='descending'>" +
                    "<search:json-property>animal</search:json-property>" +
                    "</search:sort-order>" +
                    "</search:options>";

            // 1 - Verify that client overides server options - descending
            String combinedSearch = head + qtext4 + optionsWithSortDesc + tail;
            RawCombinedQueryDefinition rawCombinedQueryDefinition =
                    queryMgr.newRawCombinedQueryDefinition(new StringHandle(combinedSearch).withMimetype("application/xml"));

            SearchHandle results = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle());
            MatchDocumentSummary[] summaries1 = results.getMatchResults();
            assertNotNull(summaries1);
            int nSummariesLen = summaries1.length;
            System.out.println("Summaries Length is " + nSummariesLen);

            assertEquals( 2, nSummariesLen);
            long nTotal = results.getTotalResults();
            System.out.println("Total # of results are " + nTotal);
            assertEquals( 14, nTotal);

            // Make sure we have proper descending sort.
            LinkedHashMap<String, String> descHashMap = new LinkedHashMap<String, String>();
            LinkedHashMap<String, String> exptdHashMap = new LinkedHashMap<String, String>();

            exptdHashMap.put("dog23", "Content");
            exptdHashMap.put("dog22", "Content");
            for (MatchDocumentSummary summary : summaries1) {
                ExtractedResult extracted = summary.getExtracted();
                if (summary.getFormat() == Format.JSON) {
                    for (ExtractedItem item : extracted) {
                        String stringJsonItem = item.getAs(String.class);
                        JsonNode nodeJsonItem = item.getAs(JsonNode.class);
                        if (nodeJsonItem.has("animal")) {
                            descHashMap.put(nodeJsonItem.path("animal").asText(), "Content");
                            continue;
                        }
                        fail("unexpected extracted item:" + stringJsonItem);
                    }
                    continue;
                }
                else
                    fail("unexpected search result:" + summary.getUri());
            }
            assertTrue( descHashMap.equals(exptdHashMap));

            t = clientTmp.openTransaction("QM");;

            SearchHandle results2 = queryMgr.search(rawCombinedQueryDefinition, new SearchHandle(), 2, t);
            MatchDocumentSummary[] summaries2 = results2.getMatchResults();
            assertNotNull(summaries2);
            nSummariesLen = summaries2.length;
            System.out.println("Summaries Length is " + nSummariesLen);

            assertEquals( 2, nSummariesLen);

            nTotal = results2.getTotalResults();
            System.out.println("Total # of results are " + nTotal);
            assertEquals( 14, nTotal);

            // Make sure we have proper descending sort.
            LinkedHashMap<String, String> descHashMap2 = new LinkedHashMap<String, String>();
            LinkedHashMap<String, String> exptdHashMap2 = new LinkedHashMap<String, String>();

            exptdHashMap2.put("dog22", "Content");
            exptdHashMap2.put("dog21", "Content");

            for (MatchDocumentSummary summary : summaries2) {
                ExtractedResult extracted = summary.getExtracted();
                if (summary.getFormat() == Format.JSON) {
                    for (ExtractedItem item : extracted) {
                        String stringJsonItem = item.getAs(String.class);
                        JsonNode nodeJsonItem = item.getAs(JsonNode.class);
                        if (nodeJsonItem.has("animal")) {
                            descHashMap2.put(nodeJsonItem.path("animal").asText(), "Content");
                            continue;
                        }
                        fail("unexpected extracted item:" + stringJsonItem);
                    }
                    continue;
                }
                else
                    fail("unexpected search result:" + summary.getUri());
            }
            assertTrue( descHashMap2.equals(exptdHashMap2));
        }
        catch(Exception ex) {
            System.out.println("Exception from method " + ex.getMessage());
        }
        finally {
            if(t!=null) {
                t.rollback();
            }
        }
    }

    /*This test is testing SearchView options and search handle. Similar to testBulkSearchSQDwithJSONResponseFormat
    Use CtsQueryBuilder and SearchQueryDefinition
     */
    @Test
    public void testBulkSearchWithCtsQueryBuilder() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        QueryManager queryMgr = client.newQueryManager();

        CtsQueryBuilder ctsQueryBuilder = queryMgr.newCtsSearchBuilder();
        CtsQueryExpr ctsQueryExpr = ctsQueryBuilder.cts.andQuery(ctsQueryBuilder.cts.wordQuery("woof"));
        CtsQueryDefinition qd = ctsQueryBuilder.newCtsQueryDefinition(ctsQueryExpr);

        docMgr.setNonDocumentFormat(Format.JSON);

        docMgr.setSearchView(QueryView.FACETS);
        JacksonHandle jh = new JacksonHandle();
        docMgr.search(qd, 1, jh);

        // System.out.println(jh.get().toString());
        assertTrue( jh.get().has("facets"));
        assertFalse( jh.get().has("results"));// Issue 84 is tracking this
        assertFalse( jh.get().has("metrics"));

        docMgr.setSearchView(QueryView.RESULTS);
        docMgr.search(qd, 1, jh);

        assertFalse( jh.get().has("facets"));
        assertTrue( jh.get().has("results"));
        assertFalse( jh.get().has("metrics"));// Issue 84 is tracking this

        docMgr.setSearchView(QueryView.METADATA);
        docMgr.search(qd, 1, jh);

        assertFalse( jh.get().has("facets"));
        assertFalse( jh.get().has("results"));
        assertTrue( jh.get().has("metrics"));

        docMgr.setSearchView(QueryView.ALL);
        docMgr.search(qd, 1, jh);

        assertTrue( jh.get().has("facets"));
        assertTrue( jh.get().has("results"));
        assertTrue( jh.get().has("metrics"));

        queryMgr.setView(QueryView.FACETS);
        queryMgr.search(qd, jh);
        System.out.println(jh.get().toString());
    }

    /* This test is to verify extract-document-data & extract-path with Default
       selected option query with CtsQueryBuilder and QueryManager.search
       Similar to test method testExtractDocumentData()
    */
    @Test
    public void testExtractDocumentDataWithCtsQueryBuilder() throws KeyManagementException, NoSuchAlgorithmException, Exception {

        QueryManager queryMgr = client.newQueryManager();
        String options =
                "<search:options>" +
                        "<search:extract-document-data>" +
                        "<search:extract-path>//foo</search:extract-path>" +
                        "<search:extract-path>//says</search:extract-path>" +
                        "</search:extract-document-data>" +
                        "</search:options>";
        // test XML response with extracted JSON matches
        CtsQueryBuilder ctsQueryBuilder = queryMgr.newCtsSearchBuilder();

        CtsQueryExpr orQuery1 = ctsQueryBuilder.cts.wordQuery("YYY71");
        CtsQueryExpr orQuery2 = ctsQueryBuilder.cts.wordQuery("dog14");
        CtsQueryExpr orQuery = ctsQueryBuilder.cts.orQuery(orQuery1, orQuery2);

        CtsQueryDefinition qd = ctsQueryBuilder.newCtsQueryDefinition(orQuery);

        SearchHandle results = queryMgr.search(qd, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        assertNotNull(summaries);
        assertEquals(1, summaries.length);
        assertEquals("/bulkSearch/dog14.json", summaries[0].getUri());
        String text = summaries[0].getFirstSnippet(new StringHandle()).toString();
        assertTrue(text.contains("dog14"));
    }

}
