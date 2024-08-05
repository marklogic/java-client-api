/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.*;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.util.RequestLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class StringSearchTest {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(StringSearchTest.class);

  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    Common.connectRestAdmin();
  }

  @AfterAll
  public static void afterClass() {
  }

  @Test
  void returnDocumentsWithMetadataValues() {
	  XMLDocumentManager mgr = Common.client.newXMLDocumentManager();
	  mgr.write("/metadata/test.xml",
		  new DocumentMetadataHandle().withMetadataValue("hello", "world"),
		  new StringHandle("<test>metadataabc</test>"));

	  mgr.setMetadataCategories(DocumentManager.Metadata.METADATAVALUES);

	  DocumentPage page = mgr.search(Common.client.newQueryManager().newStructuredQueryBuilder().term("metadataabc"), 1);
	  assertTrue(page.hasNext());

	  DocumentRecord record = page.next();
	  assertEquals("/metadata/test.xml", record.getUri());
	  DocumentMetadataHandle.DocumentMetadataValues values = record.getMetadata(new DocumentMetadataHandle()).getMetadataValues();
	  assertEquals("world", values.get("hello"));
  }

  @Test
  public void testStringSearch()
    throws FailedRequestException, ForbiddenUserException,
    ResourceNotFoundException, ResourceNotResendableException
  {
    String optionsName = writeOptions();

    QueryManager queryMgr = Common.client.newQueryManager();

    StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
    qdef.setCriteria("grandchild1 OR grandchild4");
    qdef.setDirectory("/sample/");

    SearchHandle results = queryMgr.search(qdef, new SearchHandle());
    assertNotNull(results);
    assertFalse(results.getMetrics().getTotalTime() == -1);

    FacetResult[] facets = results.getFacetResults();
    assertNotNull(facets);
    assertEquals( 1, facets.length);
    FacetValue[] facetVals = facets[0].getFacetValues();
    assertEquals( 6, facetVals.length);

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertNotNull(summaries);
    assertEquals( 2, summaries.length);
  }

  @Test
  public void testStringSearch2() throws IOException {
    QueryManager queryMgr = Common.client.newQueryManager();

    StringQueryDefinition qdef = queryMgr.newStringDefinition();
    qdef.setCriteria("10");
    qdef.setDirectory("/sample/");

    SearchHandle handle = new SearchHandle();
    handle = queryMgr.search(qdef, handle);
    assertNotNull(handle);

    MatchDocumentSummary[] summaries = handle.getMatchResults();
    assertNotNull(summaries);
    assertEquals( 2, summaries.length);

    for ( MatchDocumentSummary summary : summaries ) {
      MatchLocation[] locations = summary.getMatchLocations();
      assertEquals( 1, locations.length);
      for ( MatchLocation location : locations ) {
        assertNotNull(location.getAllSnippetText());
      }
    }
  }

  @Test
  public void testStringSearch4()
    throws IOException, FailedRequestException, ForbiddenUserException, ResourceNotFoundException,
    ResourceNotResendableException
  {
    String optionsName = writeOptions();

    QueryManager queryMgr = Common.client.newQueryManager();

    StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
    qdef.setCriteria("grandchild1 OR grandchild4");
    qdef.setDirectory("/sample/");

    queryMgr.setView(QueryView.FACETS);
    SearchHandle results = queryMgr.search(qdef, new SearchHandle());
    assertNotNull(results);

    FacetResult[] facets = results.getFacetResults();
    assertNotNull(facets);
    assertEquals( 1, facets.length);
    FacetValue[] facetVals = facets[0].getFacetValues();
    assertEquals( 6, facetVals.length);

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertTrue(summaries == null || summaries.length == 0);

    queryMgr.setView(QueryView.RESULTS);
    results = queryMgr.search(qdef, new SearchHandle());
    assertNotNull(results);

    facets = results.getFacetResults();
    assertTrue(facets == null || facets.length == 0);

    summaries = results.getMatchResults();
    assertNotNull(summaries);
    assertEquals( 2, summaries.length);
	  assertEquals("empty-snippet", results.getSnippetTransformType());
  }

  @Test
  public void testSearchHandle() throws Exception {
    String xml =
      "<product xmlns='http://example.com/products'>" + "<description xmlns='' xml:lang='en'>some description</description>" + "</product>";

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    RequestLogger logger = Common.client.newLogger(out);
    DocumentMetadataHandle meta = new DocumentMetadataHandle().withCollections("xml", "products");
    Common.client.newXMLDocumentManager().writeAs("test.xml", meta, xml);
    QueryManager queryMgr = Common.client.newQueryManager();
    queryMgr.startLogging(logger);
    RawCombinedQueryDefinition query = queryMgr.newRawCombinedQueryDefinition(new StringHandle(
      "<search xmlns='http://marklogic.com/appservices/search'>" + "<options>" + "<extract-document-data selected='all'>" + "<extract-path>//description[@xml:lang='en']</extract-path>" + "</extract-document-data>" + "<constraint name='myFacet'>" + "<range type='xs:string' facet='true'>" + "<element name='grandchild'/>" + "</range>" + "</constraint>" + "<extract-metadata>" + "<qname elem-name='description'/>" + "</extract-metadata>" + "<return-constraints>true</return-constraints>" + "<return-facets>true</return-facets>" + "<return-metrics>true</return-metrics>" + "<return-plan>true</return-plan>" + "<return-qtext>true</return-qtext>" + "<return-query>true</return-query>" + "<return-results>true</return-results>" + "<debug>true</debug>" + "</options>" + "<query>" + "<and-query>" + "<collection-query><uri>xml</uri></collection-query>" + "<collection-query><uri>products</uri></collection-query>" + "</and-query>" + "</query>" + "</search>"));
    queryMgr.setView(QueryView.ALL);
    SearchHandle results = queryMgr.search(query, new SearchHandle());
    assertTrue(results.getConstraintIterator(new StringHandle()).next().get().startsWith("<search:constraint"));
    assertTrue(results.getConstraintNames()[0].equals("myFacet"));
    assertTrue(results.getConstraint("myFacet", new StringHandle()).get().startsWith("<search:constraint"));
    assertEquals( "myFacet", results.getFacetNames()[0]);
    SearchMetrics metrics = results.getMetrics();
    assertTrue(metrics.getFacetResolutionTime() >= 0);
    assertTrue(metrics.getQueryResolutionTime() >= 0);
    assertTrue(metrics.getSnippetResolutionTime() >= 0);
    assertTrue(metrics.getExtractResolutionTime() >= 0);
    assertTrue(metrics.getTotalTime() >= 0);
    assertTrue(results.getPlan(new StringHandle()).get().startsWith("<search:plan"));
    assertEquals("plan", results.getPlan().getFirstChild().getLocalName());
    assertEquals("SEARCH-FLWOR", results.getReports()[0].getId());
    assertTrue(results.getQuery(new StringHandle()).get().startsWith("<search:query"));
    assertEquals("snippet", results.getSnippetTransformType());
    assertTrue(results.getWarnings().length == 0);
    assertTrue(out.toString().startsWith("searched"));
  }

  @Test
  public void testFailedSearch() throws IOException {
    QueryManager queryMgr = Common.client.newQueryManager();
    queryMgr.setView(QueryView.RESULTS);

    StringQueryDefinition qdef = queryMgr.newStringDefinition();
    qdef.setCriteria("criteriaThatShouldNotMatchAnyDocument");
    qdef.setDirectory("/sample/");

    SearchHandle results = queryMgr.search(qdef, new SearchHandle());
    assertNotNull(results);

    MatchDocumentSummary[] summaries = results.getMatchResults();
    assertTrue(summaries == null || summaries.length == 0);
  }

  @Test
  public void testJSON()
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException
  {
    String optionsName = writeOptions();

    QueryManager queryMgr = Common.client.newQueryManager();

    StringQueryDefinition qdef = queryMgr.newStringDefinition(optionsName);
    qdef.setCriteria("grandchild1 OR grandchild4");
    qdef.setDirectory("/sample/");

    queryMgr.setView(QueryView.FACETS);

    // create a handle for the search results
    StringHandle resultsHandle = new StringHandle().withFormat(Format.JSON);

    // run the search
    queryMgr.search(qdef, resultsHandle);

    assertEquals("{", resultsHandle.get().substring(0, 1)); // It's JSON, right?
  }

  @Test
  public void test_issue644() {
    QueryManager queryMgr = Common.client.newQueryManager();
    String queryText = "queryThatMatchesNothing";
    RawCombinedQueryDefinition query = queryMgr.newRawCombinedQueryDefinition(new StringHandle(
      "<search xmlns='http://marklogic.com/appservices/search'>" +
        "<options>" +
          "<extract-document-data selected='all'>" +
            "<extract-path>/*</extract-path>" +
          "</extract-document-data>" +
          "<return-metrics>true</return-metrics>" +
        "</options>" +
        "<query>" +
          "<term-query><text>" + queryText + "</text></term-query>" +
        "</query>" +
      "</search>"));
    queryMgr.search(query, new SearchHandle());
    // we didn't throw an Exception, which means this issue is resolved
  }

  private String writeOptions()
    throws FailedRequestException, ForbiddenUserException, ResourceNotFoundException, ResourceNotResendableException
  {
    String optionsName = "facets";

    // Get back facets...
    String options =
      "{\"options\": " +
        "{ \"constraint\": " +
            "[{\"name\":\"grandchild\", \"range\": " +
              "{   \"type\":\"xs:string\", \"collation\":\"http://marklogic.com/collation/\"," +
                "\"element\":{\"name\":\"grandchild\"}," +
                "\"facet\":true" +
              "}" +
            "}]," +
          "\"transform-results\": {\"apply\": \"empty-snippet\"}" +
        "}" +
      "}";

    QueryOptionsManager queryOptionsMgr =
      Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();

    queryOptionsMgr.writeOptions(optionsName, new StringHandle(options).withFormat(Format.JSON));

    return optionsName;
  }
}
