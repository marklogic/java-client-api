/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.*;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.StringQueryDefinition;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;



public class TestBulkSearchWithStringQueryDef extends AbstractFunctionalTest {
  private static final int BATCH_SIZE = 100;
  private static final String DIRECTORY = "/bulkSearch/";

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    createRESTUserWithPermissions("usr1", "password", getPermissionNode("flexrep-eval", Capability.READ), getCollectionNode("http://permission-collections/"), "rest-writer",
        "rest-reader");
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    deleteRESTUser("usr1");
  }

  @BeforeEach
  public void setUp() throws Exception {
    // create new connection for each test below
    client = getDatabaseClient("usr1", "password", getConnType());
  }

  @AfterEach
  public void tearDown() throws Exception {
    client.release();
  }

  public void loadJSONDocuments() throws KeyManagementException, NoSuchAlgorithmException, JsonProcessingException, IOException {
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
      // System.out.println(jn.toString());
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
    // System.out.println(record.getMimetype());

  }

  public void loadTxtDocuments() {
    int count = 1;
    TextDocumentManager docMgr = client.newTextDocumentManager();
    DocumentWriteSet writeset = docMgr.newWriteSet();
    for (int i = 0; i < 101; i++) {
      writeset.add(DIRECTORY + "Textfoo" + i + ".txt", new StringHandle().with("bar can be foo" + i));
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

  public void loadXMLDocuments() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException {
    int count = 1;
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentWriteSet writeset = docMgr.newWriteSet();
    for (int i = 0; i < 102; i++) {

      writeset.add(DIRECTORY + "foo" + i + ".xml", new DOMHandle(getDocumentContent("This is so foo with a bar " + i)));

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
  public void testBulkSearchSQDwithDifferentPageSizes() {
    int count;
    loadTxtDocuments();
    // Creating a txt document manager for bulk search
    TextDocumentManager docMgr = client.newTextDocumentManager();
    // using QueryManger for query definition and set the search criteria
    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("bar");
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
    assertEquals(101, page.getTotalSize());
    assertEquals(101, page.getTotalPages());
    // till the issue #78 get fixed
    System.out.println("Is this First page :" + page.isFirstPage() + page.getPageNumber());// this
                                                                                           // is
                                                                                           // bug
    System.out.println("Is this Last page :" + page.isLastPage());
    System.out.println("Is this First page has content:" + page.hasContent());
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
        validateRecord(rec, Format.TEXT);
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
    // assertTrue(pageNo == page.getTotalPages());
    assertTrue( page.hasPreviousPage());
    assertEquals( 1, page.getPageSize());
    assertEquals( 101, page.getTotalSize());
    page = docMgr.search(qd, 102);
    assertFalse( page.hasContent());
  }

  // This test is trying to set the setResponse to JSON on DocumentManager and
  // use search handle which only work with XML
	@Test
  public void testBulkSearchSQDwithWrongResponseFormat() {
    loadTxtDocuments();
    TextDocumentManager docMgr = client.newTextDocumentManager();
    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("bar");
    docMgr.setNonDocumentFormat(Format.JSON);
    SearchHandle results = new SearchHandle();
    assertThrows(UnsupportedOperationException.class, () -> docMgr.search(qd, 1, results));
  }

  // Testing issue 192
  @Test
  public void testBulkSearchSQDwithNoResults() {
    loadTxtDocuments();
    TextDocumentManager docMgr = client.newTextDocumentManager();
    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("zzzz");
    SearchHandle results = new SearchHandle();
    DocumentPage page = docMgr.search(qd, 1, results);
    assertFalse( page.hasNext());

  }

  // This test has set response to JSON and pass StringHandle with format as
  // JSON, expectint it to work, logged an issue 82
  @Test
  public void testBulkSearchSQDwithResponseFormatandStringHandle() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    loadTxtDocuments();
    loadJSONDocuments();
    TextDocumentManager docMgr = client.newTextDocumentManager();

    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("bar");

    docMgr.setNonDocumentFormat(Format.JSON);
    docMgr.setSearchView(QueryView.METADATA);
    docMgr.setMetadataCategories(Metadata.PERMISSIONS);

    StringHandle results = new StringHandle().withFormat(Format.JSON);
    DocumentPage page = docMgr.search(qd, 1, results);
    DocumentMetadataHandle mh = new DocumentMetadataHandle();
    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      validateRecord(rec, Format.TEXT);
      docMgr.readMetadata(rec.getUri(), mh);
      assertTrue( mh.getPermissions().containsKey("flexrep-eval"));
      assertTrue( mh.getCollections().isEmpty());
    }
    assertFalse( results.get().isEmpty());

  }

  // This test is testing SearchView options and search handle
  @Test
  public void testBulkSearchSQDwithJSONResponseFormat() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    loadTxtDocuments();
    loadJSONDocuments();
    TextDocumentManager docMgr = client.newTextDocumentManager();

    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("woof");
    docMgr.setNonDocumentFormat(Format.JSON);

    docMgr.setSearchView(QueryView.FACETS);
    JacksonHandle jh = new JacksonHandle();
    DocumentPage page = docMgr.search(qd, 1, jh);

    // System.out.println(jh.get().toString());
    assertTrue( jh.get().has("facets"));
    assertFalse( jh.get().has("results"));
    // Issue 84 is tracking this
    assertFalse( jh.get().has("metrics"));

    docMgr.setSearchView(QueryView.RESULTS);
    page = docMgr.search(qd, 1, jh);

    assertFalse( jh.get().has("facets"));
    assertTrue( jh.get().has("results"));
    assertFalse( jh.get().has("metrics"));
    // Issue 84 is tracking this

    docMgr.setSearchView(QueryView.METADATA);
    page = docMgr.search(qd, 1, jh);

    assertFalse( jh.get().has("facets"));
    assertFalse( jh.get().has("results"));
    assertTrue( jh.get().has("metrics"));

    docMgr.setSearchView(QueryView.ALL);
    page = docMgr.search(qd, 1, jh);

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
    TextDocumentManager docMgr = client.newTextDocumentManager();
    DOMHandle results = new DOMHandle();
    QueryManager queryMgr = client.newQueryManager();
    StringQueryDefinition qd = queryMgr.newStringDefinition();
    qd.setCriteria("thought");
    Transaction t = client.openTransaction();
    try {
      loadTxtDocuments();
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

    DocumentPage page = docMgr.search(qd, 1, results);
    System.out.println(this.convertXMLDocumentToString(results.get()));

    assertEquals( results.get().getElementsByTagNameNS("*", "response").item(0).getAttributes().getNamedItem("total").getNodeValue(),
        "0");
  }
}
