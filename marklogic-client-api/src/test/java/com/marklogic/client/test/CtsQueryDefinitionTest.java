/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.expression.CtsQueryBuilder;
import com.marklogic.client.io.*;
import com.marklogic.client.query.CtsQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.type.CtsQueryExpr;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.File;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CtsQueryDefinitionTest {

    @BeforeAll
    public static void beforeClass() {
        Common.connect();
        queryMgr = Common.client.newQueryManager();
    }

    @AfterAll
    public static void afterClass() {
        Common.client.newDocumentManager().delete("CtsQueryDefinitionTest.xml");
        Common.client.newDocumentManager().delete("CtsQueryDefinitionTest.json");
    }

    private static QueryManager queryMgr;

    @Test
    public void testctsQueryDefinitionSerialization() throws XpathException {

        String expected = "{\"search\":{\"ctsast\":{\"ns\":\"cts\", \"fn\":\"element-value-query\", \"args\":[{\"ns\":"
            + "\"xs\", \"fn\":\"QName\", \"args\":[\"id\"]}, {\"ns\":\"xs\", \"fn\":\"string\", \"args\":[\"0026\"]}]}}}";
        CtsQueryBuilder ctsQueryBuilder = queryMgr.newCtsSearchBuilder();
        CtsQueryExpr ctsQueryExpr = ctsQueryBuilder.cts.elementValueQuery("id", "0026");

        CtsQueryDefinition ctsQueryDefinition = ctsQueryBuilder.newCtsQueryDefinition(ctsQueryExpr);
        String serialize = ctsQueryDefinition.serialize();
        StringHandle handle = new StringHandle();
        ctsQueryBuilder.export(ctsQueryExpr, handle);
        String export = handle.get();

        assertEquals(expected, serialize);
        assertEquals(expected, export);
    }

    @Test
    public void testCtsQueryBuilderXML() throws XpathException {
        Common.client.newDocumentManager().write("CtsQueryDefinitionTest.xml",
            new FileHandle(new File("src/test/resources/constraint5.xml")).withFormat(Format.XML));

        CtsQueryBuilder ctsQueryBuilder = queryMgr.newCtsSearchBuilder();
        CtsQueryExpr ctsQueryExpr = ctsQueryBuilder.cts.elementValueQuery("id", "0026");
        CtsQueryDefinition ctsQueryDefinition = ctsQueryBuilder.newCtsQueryDefinition(ctsQueryExpr);

        SearchHandle searchHandle = new SearchHandle();
        queryMgr.search(ctsQueryDefinition, searchHandle);
        MatchDocumentSummary[] summaries = searchHandle.getMatchResults();
        for (MatchDocumentSummary summary : summaries ) {
            MatchLocation[] locations = summary.getMatchLocations();
            for (MatchLocation location : locations) {
                String result = location.getAllSnippetText();
                assertEquals("0026", result);
            }
        }
        long count = searchHandle.getTotalResults();
        assertEquals(count, 1);

        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(ctsQueryDefinition, resultsHandle);
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id' or local-name()='highlight'])", resultDoc);
    }

    @Test
    public void testCtsQueryBuilderXMLWithOptions() throws XpathException {
        Common.client.newDocumentManager().write("CtsQueryDefinitionTest.xml",
                new FileHandle(new File("src/test/resources/constraint5.xml")).withFormat(Format.XML));

        CtsQueryBuilder ctsQueryBuilder = queryMgr.newCtsSearchBuilder();
        CtsQueryExpr ctsQueryExpr = ctsQueryBuilder.cts.elementValueQuery("id", "0026");
        StringHandle optionHandle = new StringHandle();
        String options = "\"options\": {\n" +
                "  \"return-metrics\": false,\n" +
                "  \"return-qtext\": false,\n" +
                "  \"debug\": true\n" +
                "}";
        optionHandle.withFormat(Format.JSON).with(options);
        CtsQueryDefinition ctsQueryDefinition = ctsQueryBuilder.newCtsQueryDefinition(ctsQueryExpr, optionHandle);

        SearchHandle searchHandle = new SearchHandle();
        queryMgr.search(ctsQueryDefinition, searchHandle);
        MatchDocumentSummary[] summaries = searchHandle.getMatchResults();
        for (MatchDocumentSummary summary : summaries ) {
            MatchLocation[] locations = summary.getMatchLocations();
            for (MatchLocation location : locations) {
                String result = location.getAllSnippetText();
                assertEquals("0026", result);
            }
        }
        long count = searchHandle.getTotalResults();
        assertEquals(count, 1);

        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(ctsQueryDefinition, resultsHandle);
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id' or local-name()='highlight'])", resultDoc);
    }

    @Test
    public void testCtsQueryBuilderJSON() throws XpathException {

        Common.client.newDocumentManager().write("CtsQueryDefinitionTest.json",
                new FileHandle(new File("src/test/resources/ImportTest_content.json")).withFormat(Format.JSON));

        CtsQueryBuilder ctsQueryBuilder = queryMgr.newCtsSearchBuilder();
        CtsQueryExpr ctsQueryExpr = ctsQueryBuilder.cts.wordQuery("test1");
        StringHandle optionHandle = new StringHandle();
        String options = "\"options\": {\n" +
                "  \"return-metrics\": false,\n" +
                "  \"return-qtext\": false,\n" +
                "  \"debug\": true\n" +
                "}";
        optionHandle.withFormat(Format.JSON).with(options);
        CtsQueryDefinition ctsQueryDefinition = ctsQueryBuilder.newCtsQueryDefinition(ctsQueryExpr, optionHandle);

        SearchHandle searchHandle = new SearchHandle();
        queryMgr.search(ctsQueryDefinition, searchHandle);
        MatchDocumentSummary[] summaries = searchHandle.getMatchResults();
        for (MatchDocumentSummary summary : summaries ) {
            MatchLocation[] locations = summary.getMatchLocations();
            for (MatchLocation location : locations) {
                String result = location.getAllSnippetText();
                assertEquals("test1", result);
            }
        }
        long count = searchHandle.getTotalResults();
        assertEquals(count, 1);

        JacksonHandle results = queryMgr.search(ctsQueryDefinition, new JacksonHandle());
        JsonNode node = results.get().get("results").get(0).get("uri");
        assertEquals("CtsQueryDefinitionTest.json", node.asText());
    }
}
