/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.document.*;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

public class TestBulkSearchEWithQBE extends AbstractFunctionalTest {
  private static final int BATCH_SIZE = 100;
  private static final String DIRECTORY = "/bulkSearch/";

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    createRESTUserWithPermissions("usr1", "password", getPermissionNode("flexrep-eval", Capability.READ),
        getCollectionNode("http://permission-collections/"), "rest-writer", "rest-reader");
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    deleteRESTUser("usr1");
  }

  @BeforeEach
  public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    // create new connection for each test below
    client = getDatabaseClient("usr1", "password", getConnType());
    loadTxtDocuments();
    loadXMLDocuments();
    loadJSONDocuments();
  }

  @AfterEach
  public void tearDown() throws Exception {
    System.out.println("Running clear script");
    // release client
    client.release();
  }

  public void validateRecord(DocumentRecord record, Format type) {

    assertNotNull( record);
    assertNotNull( record.getUri());
    assertTrue(record.getUri().startsWith(DIRECTORY));
    assertEquals(type, record.getFormat());
    // System.out.println(record.getMimetype());

  }

  public void loadXMLDocuments() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException {
    int count = 1;
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentWriteSet writeset = docMgr.newWriteSet();
    for (int i = 0; i < 102; i++) {
      Document doc = this.getDocumentContent("This is so foo with a bar " + i);
      Element childElement = doc.createElement("author");
      childElement.appendChild(doc.createTextNode("rhiea"));
      doc.getElementsByTagName("foo").item(0).appendChild(childElement);
      writeset.add(DIRECTORY + "foo" + i + ".xml", new DOMHandle(doc));

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

  public void loadJSONDocuments() throws KeyManagementException, NoSuchAlgorithmException, JsonProcessingException, IOException {
    int count = 1;
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentWriteSet writeset = docMgr.newWriteSet();

    Map<String, String> map = new HashMap<>();

    for (int i = 0; i < 102; i++) {
      JsonNode jn = new ObjectMapper().readTree("{\"animal\":\"dog " + i + "\", \"says\":\"woof\"}");
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

  @Test
  public void testBulkSearchQBEWithXMLResponseFormat() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      TransformerException, XpathException {
    int count;
    // Creating a xml document manager for bulk search
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    // using QBE for query definition and set the search criteria

    QueryManager queryMgr = client.newQueryManager();
    String queryAsString =
        "<q:qbe xmlns:q=\"http://marklogic.com/appservices/querybyexample\"><q:query><foo><q:word>foo</q:word></foo></q:query></q:qbe>";
    RawQueryByExampleDefinition qd = queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString));

    // set document manager level settings for search response
    docMgr.setPageLength(25);
    docMgr.setSearchView(QueryView.RESULTS);
    docMgr.setNonDocumentFormat(Format.XML);

    // Search for documents where content has bar and get first result record,
    // get search handle on it,Use DOMHandle to read results
    DOMHandle dh = new DOMHandle();
    DocumentPage page;

    long pageNo = 1;
    do {
      count = 0;
      page = docMgr.search(qd, pageNo, dh);
      if (pageNo > 1) {
        assertFalse(page.isFirstPage());
        assertTrue(page.hasPreviousPage());
      }
      while (page.hasNext()) {
        DocumentRecord rec = page.next();
        rec.getFormat();
        validateRecord(rec, Format.XML);

        count++;
      }

      Document resultDoc = dh.get();
      assertXpathEvaluatesTo("xml", "string(//*[local-name()='result'][last()]//@*[local-name()='format'])", resultDoc);
      assertEquals( page.size(), count);
      pageNo = pageNo + page.getPageSize();
    } while (!page.isLastPage() && page.hasContent());
    assertEquals( 5, page.getTotalPages());
    assertTrue(page.hasPreviousPage());
    assertEquals( 25, page.getPageSize());
    assertEquals( 102, page.getTotalSize());

  }

  @Test
  public void testBulkSearchQBEWithJSONResponseFormat() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      TransformerException {
    int count;

    // Creating a xml document manager for bulk search
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    // using QBE for query definition and set the search criteria

    QueryManager queryMgr = client.newQueryManager();
    String queryAsString = "{\"$query\": { \"says\": {\"$word\":\"woof\",\"$exact\": false}}}";
    RawQueryByExampleDefinition qd = queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));

    // set document manager level settings for search response
    docMgr.setPageLength(25);
    docMgr.setSearchView(QueryView.RESULTS);
    docMgr.setNonDocumentFormat(Format.JSON);

    // Search for documents where content has bar and get first result record,
    // get search handle on it,Use DOMHandle to read results
    JacksonHandle sh = new JacksonHandle();
    DocumentPage page;

    long pageNo = 1;
    do {
      count = 0;
      page = docMgr.search(qd, pageNo, sh);
      if (pageNo > 1) {
        assertFalse( page.isFirstPage());
        assertTrue(page.hasPreviousPage());
      }
      while (page.hasNext()) {
        DocumentRecord rec = page.next();
        rec.getFormat();
        validateRecord(rec, Format.JSON);
        System.out.println(rec.getContent(new StringHandle()).get().toString());
        count++;
      }
      assertTrue( sh.get().get("start").asLong() == page.getStart());
      assertEquals( page.size(), count);
      pageNo = pageNo + page.getPageSize();
    } while (!page.isLastPage() && page.hasContent());

    assertEquals( 5, page.getTotalPages());
    assertTrue(page.hasPreviousPage());
    assertEquals( 25, page.getPageSize());
    assertEquals( 102, page.getTotalSize());

  }

  @Test
  public void testBulkSearchQBECombinedQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      TransformerException, XpathException {
    int count;

    // Creating a xml document manager for bulk search
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    // using QBE for query definition and set the search criteria

    QueryManager queryMgr = client.newQueryManager();

    String queryAsString = "<search:search " +
        "xmlns:search='http://marklogic.com/appservices/search'>" +
        "<search:query>" +
        "<search:term-query>" +
        "<search:text>bar</search:text>" +
        "</search:term-query>" +
        "<search:value-constraint-query>" +
        "<search:constraint-name>authorName</search:constraint-name>" +
        "<search:text>rhiea</search:text>" +
        "</search:value-constraint-query>" +
        "</search:query>" +
        "<search:options>" +
        "<search:constraint name='authorName'>" +
        "<search:value>" +
        "<search:element name='author' ns=''/>" +
        "</search:value>" +
        "</search:constraint>" +
        "</search:options>" +
        "</search:search>";
    RawCombinedQueryDefinition qd = queryMgr.newRawCombinedQueryDefinition(new StringHandle(queryAsString).withFormat(Format.XML));

    // set document manager level settings for search response
    docMgr.setPageLength(25);
    docMgr.setSearchView(QueryView.RESULTS);

    // Search for documents where content has bar and get first result record,
    // get search handle on it,Use DOMHandle to read results
    DOMHandle dh = new DOMHandle();
    DocumentPage page;

    long pageNo = 1;
    do {
      count = 0;
      page = docMgr.search(qd, pageNo, dh);
      if (pageNo > 1) {
        assertFalse( page.isFirstPage());
        assertTrue( page.hasPreviousPage());
      }
      while (page.hasNext()) {
        DocumentRecord rec = page.next();
        validateRecord(rec, Format.XML);
        count++;
      }
      Document resultDoc = dh.get();
      assertXpathEvaluatesTo("xml", "string(//*[local-name()='result'][last()]//@*[local-name()='format'])", resultDoc);
      assertEquals( page.size(), count);
      pageNo = pageNo + page.getPageSize();
    } while (!page.isLastPage() && page.hasContent());

    assertEquals( 5, page.getTotalPages());
    assertTrue( page.hasPreviousPage());
    assertEquals( 25, page.getPageSize());
    assertEquals( 102, page.getTotalSize());

  }

  @Test
  public void testBulkSearchQBEWithJSONCombinedQuery() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      TransformerException {
    int count;

    // Creating a xml document manager for bulk search
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    // using QBE for query definition and set the search criteria

    QueryManager queryMgr = client.newQueryManager();
    String queryAsString = "{\"search\":{\"query\":{\"value-constraint-query\":{\"constraint-name\":\"animal\", \"text\":\"woof\"}}, \"options\":{\"constraint\":{\"name\":\"animal\", \"value\":{\"json-property\":\"says\"}}}}}";

    RawCombinedQueryDefinition qd = queryMgr.newRawCombinedQueryDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));
    // set document manager level settings for search response
    docMgr.setPageLength(25);
    docMgr.setSearchView(QueryView.RESULTS);
    docMgr.setNonDocumentFormat(Format.JSON);

    // Search for documents where content has woof and get first result record,
    JacksonHandle sh = new JacksonHandle();
    queryMgr.search(qd, sh);
    System.out.println(sh.get().toString());
    DocumentPage page;

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
        validateRecord(rec, Format.JSON);
        count++;
      }
      assertTrue( sh.get().get("start").asLong() == page.getStart());
      assertEquals( page.size(), count);
      pageNo = pageNo + page.getPageSize();
    } while (!page.isLastPage() && page.hasContent());
    System.out.println(sh.get().toString());
    assertEquals( 5, page.getTotalPages());
    assertTrue( page.hasPreviousPage());
    assertEquals( 25, page.getPageSize());
    assertEquals( 102, page.getTotalSize());

  }

  /*
   * This test method verifies if JacksonParserHandle class supports
   * SearchReadHandle. Verifies Git Issue 116. The test functionality is same as
   * testBulkSearchQBEWithJSONResponseFormat.
   */

  @Test
  public void testBulkSearchQBEResponseInParserHandle() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      TransformerException {
    int count;

    // Creating a xml document manager for bulk search
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    // using QBE for query definition and set the search criteria

    QueryManager queryMgr = client.newQueryManager();
    String queryAsString = "{\"$query\": { \"says\": {\"$word\":\"woof\",\"$exact\": false}}}";
    RawQueryByExampleDefinition qd = queryMgr.newRawQueryByExampleDefinition(new StringHandle(queryAsString).withFormat(Format.JSON));

    // set document manager level settings for search response
    docMgr.setPageLength(25);
    docMgr.setSearchView(QueryView.RESULTS);
    docMgr.setNonDocumentFormat(Format.JSON);

    // Search for documents where content has bar and get first result record,
    // get JacksonParserHandle on it,Use DOMHandle to read results
    JacksonParserHandle sh = new JacksonParserHandle();
    DocumentPage page;

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
        validateRecord(rec, Format.JSON);
        System.out.println(rec.getContent(new StringHandle()).get().toString());
        count++;
      }

      assertEquals( page.size(), count);
      pageNo = pageNo + page.getPageSize();
    } while (!page.isLastPage() && page.hasContent());

    ObjectMapper mapper = new ObjectMapper();
    JsonParser jsonParser = sh.get();

    JsonNode jnode = null;
    jnode = mapper.readValue(jsonParser, JsonNode.class);

    assertTrue( jnode.get("start").asLong() == page.getStart());

    assertEquals( 5, page.getTotalPages());
    assertTrue(page.hasPreviousPage());
    assertEquals( 25, page.getPageSize());
    assertEquals( 102, page.getTotalSize());
    sh.close();
  }
}
