/*
 * Copyright (c) 2023 MarkLogic Corporation
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

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathNotExists;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAppServerConstraints extends AbstractFunctionalTest {

    @BeforeAll
    public static void beforeAll() throws Exception {
        System.out.println("In setup");
        client = getDatabaseClient("rest-admin", "x", getConnType());
    }

    @AfterEach
    public void testCleanUp() throws Exception {
        deleteDocuments(connectAsAdmin());
    }

    // Begin TestAppServicesAbsRangeConstraint
    @Test
    public void testWithVariousGrammarAndWordQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
            XpathException, KeyManagementException, NoSuchAlgorithmException
    {
        System.out.println("Running testWithVariousGrammarAndWordQuery");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "absRangeConstraintWithVariousGrammarAndWordQueryOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/abs-range-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("(pop:high OR pop:medium) AND price:medium AND intitle:served");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("Vannevar served", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("12.34", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
        assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][1]//*[local-name()='popularity'])", resultDoc);
        assertXpathEvaluatesTo("1", "string(//*[local-name()='facet-value']//@*[local-name()='count'])", resultDoc);
        assertXpathEvaluatesTo("High", "string(//*[local-name()='facet-value'])", resultDoc);

        // String expectedSearchReport =
        // "(cts:search(fn:collection(), cts:and-query((cts:or-query((cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&gt;=\", xs:int(\"5\"), (), 1), cts:and-query((cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&gt;=\", xs:int(\"3\"), (), 1), cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&lt;\", xs:int(\"5\"), (), 1)), ()))), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \"&gt;=\", 3.0, (), 1), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \"&lt;\", 14.0, (), 1), cts:element-word-query(fn:QName(\"\", \"title\"), \"served\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";

        // assertXpathEvaluatesTo(expectedSearchReport,
        // "string(//*[local-name()='report'])", resultDoc);
    }
    // End of TestAppServicesAbsRangeConstraint
    // Begin TestAppServicesCollectionConstraint
    @Test
    public void testWithNoFacet() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testWithNoFacet");

        String filename1 = "constraint1.xml";
        String filename2 = "constraint2.xml";
        String filename3 = "constraint3.xml";
        String filename4 = "constraint4.xml";
        String filename5 = "constraint5.xml";
        String queryOptionName = "collectionConstraintWithNoFacetOpt.xml";

        // create and initialize a handle on the metadata
        DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

        // set the metadata
        metadataHandle1.getCollections().addAll("http://test.com/set1");
        metadataHandle1.getCollections().addAll("http://test.com/set5");
        metadataHandle2.getCollections().addAll("http://test.com/set1");
        metadataHandle3.getCollections().addAll("http://test.com/set3");
        metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
        metadataHandle5.getCollections().addAll("http://test.com/set1");
        metadataHandle5.getCollections().addAll("http://test.com/set5");

        // write docs
        writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
        writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
        writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
        writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
        writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("coll:set3");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        System.out.println(convertXMLDocumentToString(resultDoc));
        // assertXpathEvaluatesTo("1",
        // "string(//*[local-name()='result'][last()]//@*[local-name()='index'])",
        // resultDoc);
        // assertXpathEvaluatesTo("For 1945",
        // "string(//*[local-name()='result'][1]//*[local-name()='title'])",
        // resultDoc);
        assertXpathNotExists("//search:facet-value[@count='1']", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:collection-query(\"http://test.com/set3\"), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }

    // @Test
    public void testWithWordConstraintAndGoogleGrammar() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
            XpathException
    {
        System.out.println("Running testWithWordConstraintAndGoogleGrammar");

        String filename1 = "constraint1.xml";
        String filename2 = "constraint2.xml";
        String filename3 = "constraint3.xml";
        String filename4 = "constraint4.xml";
        String filename5 = "constraint5.xml";
        String queryOptionName = "collectionConstraintWithWordConstraintAndGoogleGrammarOpt.xml";

        // create and initialize a handle on the metadata
        DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

        // set the metadata
        metadataHandle1.getCollections().addAll("http://test.com/set1");
        metadataHandle1.getCollections().addAll("http://test.com/set5");
        metadataHandle2.getCollections().addAll("http://test.com/set1");
        metadataHandle3.getCollections().addAll("http://test.com/set3");
        metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
        metadataHandle5.getCollections().addAll("http://test.com/set1");
        metadataHandle5.getCollections().addAll("http://test.com/set5");

        // write docs
        writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
        writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
        writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
        writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
        writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("(coll:set1 AND coll:set5) AND -intitle:memex");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query((cts:collection-query(\"http://test.com/set1\"), cts:collection-query(\"http://test.com/set5\"), cts:not-query(cts:element-word-query(fn:QName(\"\", \"title\"), \"memex\", (\"lang=en\"), 1), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }
    // End of TestAppServicesCollectionConstraint

    // Begin TestAppServicesConstraintCombination
    @Test
    public void testWithValueAndRange() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testWithValueAndRange");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "appservicesConstraintCombinationOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/appservices-combination-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("id:00*2 AND date:2006-02-02");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("0012", "string(//*[local-name()='result']//*[local-name()='id'])", resultDoc);

        // String expectedSearchReport =
        // "(cts:search(fn:collection(), cts:and-query((cts:element-value-query(fn:QName(\"\", \"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-range-query(fn:QName(\"http://purl.org/dc/elements/1.1/\", \"date\"), \"=\", xs:date(\"2006-02-02\"), (), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";

        // assertXpathEvaluatesTo(expectedSearchReport,
        // "string(//*[local-name()='report'])", resultDoc);
    }

    @Test
    public void testWithAll() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testWithAll");

        String filename1 = "constraint1.xml";
        String filename2 = "constraint2.xml";
        String filename3 = "constraint3.xml";
        String filename4 = "constraint4.xml";
        String filename5 = "constraint5.xml";
        String queryOptionName = "appservicesConstraintCombinationOpt.xml";

        // create and initialize a handle on the metadata
        DocumentMetadataHandle metadataHandle1 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle2 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle3 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle4 = new DocumentMetadataHandle();
        DocumentMetadataHandle metadataHandle5 = new DocumentMetadataHandle();

        // set the metadata
        metadataHandle1.getCollections().addAll("http://test.com/set1");
        metadataHandle1.getCollections().addAll("http://test.com/set5");
        metadataHandle2.getCollections().addAll("http://test.com/set1");
        metadataHandle3.getCollections().addAll("http://test.com/set3");
        metadataHandle4.getCollections().addAll("http://test.com/set3/set3-1");
        metadataHandle5.getCollections().addAll("http://test.com/set1");
        metadataHandle5.getCollections().addAll("http://test.com/set5");

        // write docs
        writeDocumentUsingInputStreamHandle(client, filename1, "/collection-constraint/", metadataHandle1, "XML");
        writeDocumentUsingInputStreamHandle(client, filename2, "/collection-constraint/", metadataHandle2, "XML");
        writeDocumentUsingInputStreamHandle(client, filename3, "/collection-constraint/", metadataHandle3, "XML");
        writeDocumentUsingInputStreamHandle(client, filename4, "/collection-constraint/", metadataHandle4, "XML");
        writeDocumentUsingInputStreamHandle(client, filename5, "/collection-constraint/", metadataHandle5, "XML");

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("(coll:set1 AND coll:set5) AND -intitle:memex AND (pop:high OR pop:medium) AND price:low AND id:**11 AND date:2005-01-01 AND (para:Bush AND -para:memex)");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);

        // String expectedSearchReport =
        // "(cts:search(fn:collection(), cts:and-query((cts:collection-query(\"http://test.com/set1\"), cts:collection-query(\"http://test.com/set5\"), cts:not-query(cts:element-word-query(fn:QName(\"\", \"title\"), \"memex\", (\"lang=en\"), 1), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";

        // assertXpathEvaluatesTo(expectedSearchReport,
        // "string(//*[local-name()='report'])", resultDoc);
    }
    // End of TestAppServicesConstraintCombination

    // Begin TestAppServicesFieldConstraint
    @Test
    public void testWithSnippetAndVariousGrammarAndWordQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
            XpathException
    {
        System.out.println("Running testWithSnippetAndVariousGrammarAndWordQuery");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "fieldConstraintWithSnippetAndVariousGrammarAndWordQueryOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/field-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("(para:Bush AND -para:memex) OR id:0026 AND memex");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("memex", "string(//*[local-name()='result'][1]//*[local-name()='match'][1]//*[local-name()='highlight'])", resultDoc);
        assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='match'][2]//*[local-name()='highlight'])", resultDoc);
        assertXpathEvaluatesTo("Memex", "string(//*[local-name()='result'][1]//*[local-name()='match'][3]//*[local-name()='highlight'])", resultDoc);
        assertXpathEvaluatesTo("Bush", "string(//*[local-name()='result'][2]//*[local-name()='match'][1]//*[local-name()='highlight'])", resultDoc);

        // String expectedSearchReport =
        // "(cts:search(fn:collection(), cts:and-query((cts:or-query((cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&gt;=\", xs:int(\"5\"), (), 1), cts:and-query((cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&gt;=\", xs:int(\"3\"), (), 1), cts:element-range-query(fn:QName(\"\", \"popularity\"), \"&lt;\", xs:int(\"5\"), (), 1)), ()))), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \"&gt;=\", 3.0, (), 1), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\", \"price\"), fn:QName(\"\", \"amt\"), \"&lt;\", 14.0, (), 1), cts:element-word-query(fn:QName(\"\", \"title\"), \"served\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\"), 1))[1 to 10]";

        // assertXpathEvaluatesTo(expectedSearchReport,
        // "string(//*[local-name()='report'])", resultDoc);
    }
    // End of TestAppServicesFieldConstraint

    // Begin TestAppServicesGeoAttrPairConstraint
    @Test
    public void testPointPositiveLangLat() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointPositiveLangLat");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-attr-pair:\"12,5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara 12,5 12,5 12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testPointPositiveLatNegativeLang() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointNegativeLangLat");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-attr-pair:\"12,-5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara 12,-5 12,-5 12 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
    }

	@Test
	public void testNegativePointInvalidValue() throws Exception {
		String queryOptionName = "geoConstraintOpt.xml";
		loadGeoData();

		setQueryOption(client, queryOptionName);
		QueryManager queryMgr = client.newQueryManager();

		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("geo-attr-pair:\"12,A\"");

		DOMHandle resultsHandle = new DOMHandle();

		String result = "";
		try {
			queryMgr.search(querydef, resultsHandle);
			Document resultDoc = resultsHandle.get();
			result = convertXMLDocumentToString(resultDoc).toString();
		} catch (Exception e) {
			result = e.getMessage();
		}

		String expectedResult = "<search:warning id=\"SEARCH-IGNOREDQTEXT\">[Invalid text, cannot parse geospatial point from '12,A'.]</search:warning>";
		assertTrue(result.contains(expectedResult), "Unexpected result: " + result);
	}

    @Test
    public void testCirclePositiveLatNegativeLang() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("testCirclePositiveLatNegativeLang");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-child:\"@70 12,-5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara 12,-5 12,-5 12 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("jack_kara 11,-5 11,-5 11 -5", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_jill 12,-4 12,-4 12 -4", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("bill_kara 13,-5 13,-5 13 -5", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_gale 12,-6 12,-6 12 -6", "string(//*[local-name()='result'][5]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testBoxPositiveLatNegativeLang() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("testBoxPositiveLatNegativeLang");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-child:\"[11,-5,12,-4]\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara 12,-5 12,-5 12 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("jack_kara 11,-5 11,-5 11 -5", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_jill 12,-4 12,-4 12 -4", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testCircleAndWord() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testCircleAndWord");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-child:\"@70 12,-5\" AND karl_kara");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("/geo-constraint/geo-constraint15.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
    }
    // End of TestAppServicesGeoAttrPairConstraint

    // Begin TestAppServicesGeoElementChildConstraint
    @Test
    public void testPointPositiveLangLat_ChildConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointPositiveLangLat_ChildConstraint");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-child:\"12,5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara 12,5 12,5 12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testPointNegativeLangLat() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointNegativeLangLat");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-child:\"-12,-5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara -12,-5 -12,-5 -12 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
    }

	@Test
	public void testNegativePointInvalidValue_ChildConstraint() throws Exception {
		String queryOptionName = "geoConstraintOpt.xml";
		loadGeoData();

		setQueryOption(client, queryOptionName);
		QueryManager queryMgr = client.newQueryManager();

		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("geo-elem-child:\"12,A\"");

		DOMHandle resultsHandle = new DOMHandle();
		String result = "";
		try {
			queryMgr.search(querydef, resultsHandle);
			Document resultDoc = resultsHandle.get();
			result = convertXMLDocumentToString(resultDoc).toString();
		} catch (Exception ex) {
			result = ex.getMessage();
		}

		String expectedResult = "<search:warning id=\"SEARCH-IGNOREDQTEXT\">[Invalid text, cannot parse geospatial point from '12,A'.]</search:warning>";
		assertTrue(result.contains(expectedResult), "Unexpected result: " + result);
	}

    @Test
    public void testCircleNegativeLangLat() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("testCircleNegativeLangLat");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-child:\"@70 -12,-5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("jack_kara -11,-5 -11,-5 -11 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_jill -12,-4 -12,-4 -12 -4", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara -12,-5 -12,-5 -12 -5", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("bill_kara -13,-5 -13,-5 -13 -5", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_gale -12,-6 -12,-6 -12 -6", "string(//*[local-name()='result'][5]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testBoxNegativeLangLat() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("testBoxNegativeLangLat");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-child:\"[-12,-5,-11,-4]\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("jack_kara -11,-5 -11,-5 -11 -5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_jill -12,-4 -12,-4 -12 -4", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara -12,-5 -12,-5 -12 -5", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("jack_jill -11,-4 -11,-4 -11 -4", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testBoxAndWord() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointAndWord");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-child:\"[-12,-5,-11,-4]\" AND karl_kara");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("/geo-constraint/geo-constraint2.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
    }
    // End of TestAppServicesGeoElementChildConstraint

    // Begin TestAppServicesGeoElementConstraint
    @Test
    public void testPointPositiveLangLat_GeoElementConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointPositiveLangLat_GeoElementConstraint");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        for (int i = 1; i <= 7; i++) {
            writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem:\"12,5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        System.out.println("Returned result of testPointPositiveLangLat :" + convertXMLDocumentToString(resultDoc) + " Ends here");
        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("12,5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testPointNegativeLangLat_GeoElementConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointNegativeLangLat_GeoElementConstraint");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        for (int i = 1; i <= 7; i++) {
            writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();
        queryMgr.setView(QueryManager.QueryView.ALL);
        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem:\"-12,-5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        System.out.println("testPointNegativeLangLat Result : " + convertXMLDocumentToString(resultDoc));
        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("geo-elem:\"-12,-5\"", "string(//*[local-name()='qtext'])", resultDoc);
    }

	@Test
	public void testNegativePointInvalidValue_GeoElementConstraint() throws Exception {
		String queryOptionName = "geoConstraintOpt.xml";

		for (int i = 1; i <= 7; i++) {
			writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
		}

		setQueryOption(client, queryOptionName);
		QueryManager queryMgr = client.newQueryManager();

		StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
		querydef.setCriteria("geo-elem:\"12,A\"");

		DOMHandle resultsHandle = new DOMHandle();
		String result = "";
		try {
			queryMgr.search(querydef, resultsHandle);
			Document resultDoc = resultsHandle.get();
			result = convertXMLDocumentToString(resultDoc).toString();
		} catch (Exception e) {
			result = e.getMessage();
		}

		String expectedResult = "<search:warning id=\"SEARCH-IGNOREDQTEXT\">[Invalid text, cannot parse geospatial point from '12,A'.]</search:warning>";
		assertTrue(result.contains(expectedResult), "Unexpected result: " + result);
	}

    @Test
    public void testCirclePositiveLangLat() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("testCirclePositiveLangLat");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        for (int i = 1; i <= 7; i++) {
            writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem:\"@70 12,5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("12,5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("13,5", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("12,6", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("11,5", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("12,4", "string(//*[local-name()='result'][5]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testBoxPositiveLangLat() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("testBoxPositiveLangLat");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        for (int i = 1; i <= 7; i++) {
            writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem:\"[11,4,12,5]\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("4", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("12,5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("11,4", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("11,5", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("12,4", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testPointAndWord() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointAndWord");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        for (int i = 1; i <= 9; i++) {
            writeDocumentUsingInputStreamHandle(client, "geo-constraint" + i + ".xml", "/geo-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem:\"150,-140\" AND john");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("/geo-constraint/geo-constraint8.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
    }

    // End of TestAppServicesGeoElementConstraint

    // Begin TestAppServicesGeoElemPairConstraint

    @Test
    public void testPointPositiveLangLat_GeoElemPairConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointPositiveLangLat_GeoElemPairConstraint");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-pair:\"12,5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara 12,5 12,5 12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testPointNegativeLatPositiveLang() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPointNegativeLatPositiveLang");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-pair:\"-12,5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara -12,5 -12,5 -12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testNegativePointInvalidValue_GeoElemPairConstraint() throws Exception
    {
        String queryOptionName = "geoConstraintOpt.xml";

        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-pair:\"-12,A\"");

        DOMHandle resultsHandle = new DOMHandle();
        String result = "";
        try {
            queryMgr.search(querydef, resultsHandle);
            Document resultDoc = resultsHandle.get();
            result = convertXMLDocumentToString(resultDoc).toString();
        } catch (Exception e) {
			result = e.getMessage();
        }

		String expectedResult = "<search:warning id=\"SEARCH-IGNOREDQTEXT\">[Invalid text, cannot parse geospatial point from '-12,A'.]</search:warning>";
        assertTrue(result.contains(expectedResult), "Unexpected result: " + result);
    }

    @Test
    public void testCircleNegativeLatPositiveLang() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("testCircleNegativeLatPositiveLang");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-pair:\"@70 -12,5\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("5", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("bill_kara -13,5 -13,5 -13 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_gale -12,6 -12,6 -12 6", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara -12,5 -12,5 -12 5", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("jack_kara -11,5 -11,5 -11 5", "string(//*[local-name()='result'][4]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_jill -12,4 -12,4 -12 4", "string(//*[local-name()='result'][5]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testBoxNegativeLatPositiveLang() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("testBoxNegativeLatPositiveLang");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-pair:\"[-12,4,-11,5]\"");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("karl_kara -12,5 -12,5 -12 5", "string(//*[local-name()='result'][1]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("jack_kara -11,5 -11,5 -11 5", "string(//*[local-name()='result'][2]//*[local-name()='match'])", resultDoc);
        assertXpathEvaluatesTo("karl_jill -12,4 -12,4 -12 4", "string(//*[local-name()='result'][3]//*[local-name()='match'])", resultDoc);
    }

    @Test
    public void testBoxAndWord_GeoElemPairConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testBoxAndWord_GeoElemPairConstraint");

        String queryOptionName = "geoConstraintOpt.xml";

        // write docs
        loadGeoData();

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("geo-elem-pair:\"[-12,4,-11,5]\" AND karl_kara");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("/geo-constraint/geo-constraint20.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
    }
    // End of TestAppServicesGeoElemPairConstraint

    // Begin TestAppServicesRangeConstraint
    @Test
    public void testWithWordSearch() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testWithWordSearch");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "rangeConstraintWithWordSearchOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("date:2006-02-02 OR policymaker");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("2008-04-04", "string(//*[local-name()='result'][1]//*[local-name()='date'])", resultDoc);
        assertXpathEvaluatesTo("2006-02-02", "string(//*[local-name()='result'][2]//*[local-name()='date'])", resultDoc);
        assertXpathEvaluatesTo("Vannevar served as a prominent policymaker and public intellectual.", "string(//*[local-name()='result'][1]//*[local-name()='p'])", resultDoc);
        assertXpathEvaluatesTo("The Bush article described a device called a Memex.", "string(//*[local-name()='result'][2]//*[local-name()='p'])", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-range-query(fn:QName(\"http://purl.org/dc/elements/1.1/\",\"date\"), \"=\", xs:date(\"2006-02-02\"), (), 1), cts:word-query(\"policymaker\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }

    /*
     * public void testNegativeWithoutIndexSettings() throws
     * KeyManagementException, NoSuchAlgorithmException, IOException,
     * ParserConfigurationException, SAXException, XpathException,
     * TransformerException {
     * System.out.println("Running testNegativeWithoutIndexSettings");
     *
     * String[] filenames = {"constraint1.xml", "constraint2.xml",
     * "constraint3.xml", "constraint4.xml", "constraint5.xml"}; String
     * queryOptionName = "rangeConstraintNegativeWithoutIndexSettingsOpt.xml";
     *
     * DatabaseClient client = getDatabaseClient("rest-admin", "x",
     * getConnType());
     *
     * // write docs for(String filename : filenames) {
     * writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/",
     * "XML"); }
     *
     * setQueryOption(client, queryOptionName);
     *
     * QueryManager queryMgr = client.newQueryManager();
     *
     * // create query def StringQueryDefinition querydef =
     * queryMgr.newStringDefinition(queryOptionName);
     * querydef.setCriteria("title:Bush");
     *
     * // create handle DOMHandle resultsHandle = new DOMHandle();
     *
     * String exception = "";
     *
     * // run search try { queryMgr.search(querydef, resultsHandle); } catch
     * (Exception e) { exception = e.toString(); }
     *
     * String expectedException =
     * "com.marklogic.client.FailedRequestException: Local message: search failed: Internal Server ErrorServer Message: XDMP-ELEMRIDXNOTFOUND"
     * ;
     *
     * //assertEquals( expectedException, exception); boolean
     * exceptionIsThrown = exception.contains(expectedException);
     * assertTrue( exceptionIsThrown);
     *
     * // release client
     * //client.release(); }
     */

    @Test
    public void testNegativeTypeMismatch() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testNegativeTypeMismatch");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "rangeConstraintNegativeTypeMismatchOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/range-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("date:2006-02-02");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();

        String exception = "";

        // run search
        try {
            queryMgr.search(querydef, resultsHandle);
        } catch (Exception e) {
            exception = e.toString();
        }

        String expectedException = "com.marklogic.client.FailedRequestException: Local message: search failed: Bad Request. Server Message: XDMP-ELEMRIDXNOTFOUND";

        // assertEquals( expectedException, exception);
        boolean exceptionIsThrown = exception.contains(expectedException);
        assertTrue( exceptionIsThrown);
    }
    // End of TestAppServicesRangeConstraint

    // Begin TestAppServicesRangePathIndexConstraint
    @Test
    public void testPathIndex() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
            TransformerException
    {
        System.out.println("Running testPathIndex");

        String[] filenames = { "pathindex1.xml", "pathindex2.xml" };
        String queryOptionName = "pathIndexConstraintOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("pindex:Aries");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("/path-index-constraint/pathindex1.xml", "string(//*[local-name()='result'][1]//@*[local-name()='uri'])", resultDoc);
        assertXpathEvaluatesTo("/path-index-constraint/pathindex2.xml", "string(//*[local-name()='result'][2]//@*[local-name()='uri'])", resultDoc);

        // ***********************************************
        // *** Running test path index with constraint ***
        // ***********************************************

        System.out.println("Running testPathIndexWithConstraint");

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
        }

        // create query options manager
        QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

        // create query options handle
        // QueryOptionsHandle handle = new QueryOptionsHandle();
        String xmlOptions = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +
                "<search:constraint name='lastname'>" +
                "<search:word>" +
                "<search:element name='ln' ns=''/>" +
                "</search:word>" +
                "</search:constraint>" +
                "<search:constraint name='pindex'>" +
                "<search:range collation='http://marklogic.com/collation/' type='xs:string'>" +
                "<search:path-index>/Employee/fn</search:path-index>" +
                "</search:range>" +
                "</search:constraint>" +
                "</search:options>";
        StringHandle handle = new StringHandle(xmlOptions);

        // write query options
        optionsMgr.writeOptions("PathIndexWithConstraint", handle);

        // create query manager
        queryMgr = client.newQueryManager();

        // create query def
        querydef = queryMgr.newStringDefinition("PathIndexWithConstraint");
        querydef.setCriteria("pindex:Aries AND lastname:Yuwono");

        StructuredQueryBuilder qb = queryMgr.newStructuredQueryBuilder("PathIndexWithConstraint");
        StructuredQueryDefinition queryPathIndex = qb.rangeConstraint("pindex", StructuredQueryBuilder.Operator.EQ, "Aries");
        StructuredQueryDefinition queryWord = qb.wordConstraint("lastname", "Yuwono");
        StructuredQueryDefinition queryFinal = qb.and(queryPathIndex, queryWord);

        // create handle
        resultsHandle = new DOMHandle();
        queryMgr.search(queryFinal, resultsHandle);

        resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("/path-index-constraint/pathindex1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);

        // ***********************************************
        // *** Running test path index on int ***
        // ***********************************************

        System.out.println("Running testPathIndexOnInt");

        String[] filenames2 = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };

        // write docs
        for (String filename : filenames2) {
            writeDocumentUsingInputStreamHandle(client, filename, "/path-index-constraint/", "XML");
        }

        // create query options manager
        optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

        // create query options handle
        String xmlOptions2 = "<search:options xmlns:search='http://marklogic.com/appservices/search'>" +

                "<search:constraint name='amount'>" +
                "<search:range type='xs:decimal'>" +
                "<search:path-index>//@amt</search:path-index>" +
                "</search:range>" +
                "</search:constraint>" +
                "<search:constraint name='pop'>" +
                "<search:range type='xs:int'>" +
                "<search:path-index>/root/popularity</search:path-index>" +
                "</search:range>" +
                "</search:constraint>" +
                "<search:transform-results apply='raw'/>" +
                "</search:options>";

        handle = new StringHandle(xmlOptions2);

        // write query options
        optionsMgr.writeOptions("PathIndexWithConstraint", handle);

        // create query manager
        queryMgr = client.newQueryManager();

        // create query builder
        qb = queryMgr.newStructuredQueryBuilder("PathIndexWithConstraint");
        StructuredQueryDefinition queryPathIndex1 = qb.rangeConstraint("pop", StructuredQueryBuilder.Operator.EQ, "5");
        StructuredQueryDefinition queryPathIndex2 = qb.rangeConstraint("amount", StructuredQueryBuilder.Operator.EQ, "0.1");
        queryFinal = qb.and(queryPathIndex1, queryPathIndex2);

        // create handle
        resultsHandle = new DOMHandle();
        queryMgr.search(queryFinal, resultsHandle);

        resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("/path-index-constraint/constraint1.xml", "string(//*[local-name()='result']//@*[local-name()='uri'])", resultDoc);
    }
    // End of TestAppServicesRangePathIndexConstraint

    // Begin TestAppServicesValueConstraint
    @Test
    public void testWildcard() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testWildcard");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "valueConstraintWildCardOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/value-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("id:00*2 OR id:0??6");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
        assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-value-query(fn:QName(\"\",\"id\"), \"00*2\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"\",\"id\"), \"0??6\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }

    @Test
    public void testGoogleStyleGrammar() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testGoogleStyleGrammar");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "valueConstraintGoogleGrammarOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/value-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("title:\"The memex\" OR (id:0113 AND date:2007-03-03)");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("For 1945", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("The memex", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("0113", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);
        assertXpathEvaluatesTo("0026", "string(//*[local-name()='result'][2]//*[local-name()='id'])", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-value-query(fn:QName(\"\",\"title\"), \"The memex\", (\"lang=en\"), 1), cts:and-query((cts:element-value-query(fn:QName(\"\",\"id\"), \"0113\", (\"lang=en\"), 1), cts:element-value-query(fn:QName(\"http://purl.org/dc/elements/1.1/\",\"date\"), \"2007-03-03\", (\"lang=en\"), 1)), ())), ()), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }

    @Test
    public void testWithoutIndexSettingsAndNS() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testWithoutIndexSettingsAndNS");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "valueConstraintWithoutIndexSettingsAndNSOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/value-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("id:0012");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("0012", "string(//*[local-name()='result'][1]//*[local-name()='id'])", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:element-value-query(fn:QName(\"\",\"id\"), \"0012\", (\"lang=en\"), 1), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }

    @Test
    public void testWithIndexSettingsAndNS() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testWithIndexSettingsAndNS");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "valueConstraintWithIndexSettingsAndNSOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/value-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("dt:2007-03-03");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("2007-03-03", "string(//*[local-name()='result'][1]//*[local-name()='date'])", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:element-value-query(fn:QName(\"http://purl.org/dc/elements/1.1/\",\"date\"), \"2007-03-03\", (\"lang=en\"), 1), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }

    @Test
    public void testSpaceSeparated() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testSpaceSeparated");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "valueConstraintSpaceSeparatedOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/value-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("title:\"Vannevar Bush\" price:0.1");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("1", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("Vannevar Bush", "string(//*[local-name()='result']//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("0.1", "string(//*[local-name()='result']//@*[local-name()='amt'])", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:and-query((cts:element-value-query(fn:QName(\"\",\"title\"), \"Vannevar Bush\", (\"lang=en\"), 1), cts:element-attribute-value-query(fn:QName(\"http://cloudbank.com\",\"price\"), fn:QName(\"\",\"amt\"), \"0.1\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }
    // End of TestAppServicesValueConstraint

    // Begin TestAppServicesWordConstraint
    @Test
    public void testWithElementAndAttributeIndex() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testWithElementAndAttributeIndex");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "wordConstraintWithElementAndAttributeIndexOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("intitle:1945 OR inprice:12");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("3", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("For 1945", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("Vannevar served", "string(//*[local-name()='result'][3]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("1.23", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
        assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);
        assertXpathEvaluatesTo("12.34", "string(//*[local-name()='result'][3]//@*[local-name()='amt'])", resultDoc);

        // String expectedSearchReport =
        // "(cts:search(fn:collection(), cts:or-query((cts:element-range-query(fn:QName(\"http://purl.org/dc/elements/1.1/\", \"date\"), \"=\", xs:date(\"2006-02-02\"), (), 1), cts:word-query(\"policymaker\", (\"lang=en\"), 1))), (\"score-logtfidf\"), 1))[1 to 10]";

        // assertXpathEvaluatesTo(expectedSearchReport,
        // "string(//*[local-name()='report'])", resultDoc);
    }

    @Test
    public void testWithNormalWordQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testWithNormalWordQuery");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "wordConstraintWithNormalWordQueryOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("Memex  OR inprice:.12");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("The memex", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
        assertXpathEvaluatesTo("123.45", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:word-query(\"Memex\", (\"lang=en\"), 1), cts:element-attribute-word-query(fn:QName(\"http://cloudbank.com\",\"price\"), fn:QName(\"\",\"amt\"), \".12\", (\"lang=en\"), 1)), ()), (\"score-logtfidf\",cts:score-order(\"descending\")), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }

    @Test
    public void testWithTermOptionCaseInsensitive() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException
    {
        System.out.println("Running testWithTermOptionCaseInsensitive");

        String[] filenames = { "constraint1.xml", "constraint2.xml", "constraint3.xml", "constraint4.xml", "constraint5.xml" };
        String queryOptionName = "wordConstraintWithTermOptionCaseInsensitiveOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentUsingInputStreamHandle(client, filename, "/word-constraint/", "XML");
        }

        setQueryOption(client, queryOptionName);
        QueryManager queryMgr = client.newQueryManager();

        // create query def
        StringQueryDefinition querydef = queryMgr.newStringDefinition(queryOptionName);
        querydef.setCriteria("intitle:for  OR price:0.12");

        // create handle
        DOMHandle resultsHandle = new DOMHandle();
        queryMgr.search(querydef, resultsHandle);

        // get the result
        Document resultDoc = resultsHandle.get();

        assertXpathEvaluatesTo("2", "string(//*[local-name()='result'][last()]//@*[local-name()='index'])", resultDoc);
        assertXpathEvaluatesTo("For 1945", "string(//*[local-name()='result'][1]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("The Bush article", "string(//*[local-name()='result'][2]//*[local-name()='title'])", resultDoc);
        assertXpathEvaluatesTo("1.23", "string(//*[local-name()='result'][1]//@*[local-name()='amt'])", resultDoc);
        assertXpathEvaluatesTo("0.12", "string(//*[local-name()='result'][2]//@*[local-name()='amt'])", resultDoc);

        String expectedSearchReport = "(cts:search(fn:collection(), cts:or-query((cts:element-word-query(fn:QName(\"\",\"title\"), \"for\", (\"case-insensitive\",\"lang=en\"), 1), cts:element-attribute-range-query(fn:QName(\"http://cloudbank.com\",\"price\"), fn:QName(\"\",\"amt\"), \"=\", 0.12, (), 1)), ()), (\"score-logtfidf\",\"faceted\",cts:score-order(\"descending\")), 1))[1 to 10]";

        assertXpathEvaluatesTo(expectedSearchReport, "string(//*[local-name()='report'])", resultDoc);
    }

    // End of TestAppServicesWordConstraint

    // Begin TestRangeConstraint
    @Test
    public void testElementRangeConstraint() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        System.out.println("Running testElementRangeConstraint");
        String filenames[] = { "bbq1.xml", "bbq2.xml", "bbq3.xml", "bbq4.xml", "bbq5.xml" };
        String queryOptionName = "rangeConstraintOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentReaderHandle(client, filename, "/range-constraint/", "XML");
        }

        // write the query options to the database
        setQueryOption(client, queryOptionName);

        // run the search
        SearchHandle resultsHandle = runSearch(client, queryOptionName, "rating:5");

        // search result
        String searchResult = returnSearchResult(resultsHandle);

        String expectedSearchResult = "|Matched 1 locations in /range-constraint/bbq4.xml|Matched 1 locations in /range-constraint/bbq3.xml";
        System.out.println(searchResult);

        assertEquals( expectedSearchResult, searchResult);
    }
    // End of TestRangeConstraint

    // Begin TestRangeConstraintRelativeBucket

    @Test
    public void testRangeConstraintRelativeBucket() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        System.out.println("Running testRangeConstraintRelativeBucket");
        String filenames[] = { "bbq1.xml", "bbq2.xml", "bbq3.xml", "bbq4.xml", "bbq5.xml" };
        String queryOptionName = "rangeRelativeBucketConstraintOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentReaderHandle(client, filename, "/range-constraint-rel-bucket/", "XML");
        }

        // write the query options to the database
        setQueryOption(client, queryOptionName);

        // run the search
        SearchHandle resultsHandle = runSearch(client, queryOptionName, "date:older");

        // search result
        String result = "Matched " + resultsHandle.getTotalResults();
        String expectedResult = "Matched 5";
        assertEquals( expectedResult, result);
    }
    // End of TestRangeConstraintRelativeBucket

    // Begin TestRangeConstraintAbsoluteBucket
    @Test
    public void testRangeConstraintAbsoluteBucket() throws KeyManagementException, NoSuchAlgorithmException, IOException
    {
        System.out.println("Running testRangeConstraintAbsoluteBucket");
        String filenames[] = { "bbq1.xml", "bbq2.xml", "bbq3.xml", "bbq4.xml", "bbq5.xml" };
        String queryOptionName = "rangeAbsoluteBucketConstraintOpt.xml";

        // write docs
        for (String filename : filenames) {
            writeDocumentReaderHandle(client, filename, "/range-constraint-abs-bucket/", "XML");
        }

        // write the query options to the database
        setQueryOption(client, queryOptionName);

        // run the search
        SearchHandle resultsHandle = runSearch(client, queryOptionName, "heat:moderate");

        // search result
        String searchResult = returnSearchResult(resultsHandle);

        String expectedSearchResult = "|Matched 1 locations in /range-constraint-abs-bucket/bbq1.xml|Matched 1 locations in /range-constraint-abs-bucket/bbq3.xml|Matched 1 locations in /range-constraint-abs-bucket/bbq5.xml";
        assertEquals( expectedSearchResult, searchResult);
    }
    // End of TestRangeConstraintAbsoluteBucket

}
