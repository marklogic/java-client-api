/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.*;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.marker.ContentHandleFactory;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;



/*
 * This test is designed to to test all of bulk reads and write of JSON  with JacksonParserHandle Manager by passing set of uris
 * and also by descriptors.
 */

public class TestBulkReadWriteWithJacksonParserHandle extends AbstractFunctionalTest {

  private static final String DIRECTORY = "/";

  @BeforeEach
  public void testSetup() throws Exception {
    // create new connection for each test below
    client = getDatabaseClient("rest-admin", "x", getConnType());
  }

  @AfterEach
  public void testCleanUp() throws Exception {
    System.out.println("Running CleanUp script");
    // release client
    client.release();
  }

  public DocumentMetadataHandle setMetadata() {
    // create and initialize a handle on the meta-data
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    metadataHandle.getCollections().addAll("my-collection1",
        "my-collection2");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE,
        Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.getProperties().put("myString", "foo");
    metadataHandle.getProperties().put("myInteger", 10);
    metadataHandle.getProperties().put("myDecimal", 34.56678);
    metadataHandle.getProperties().put("myCalendar",
        Calendar.getInstance().get(Calendar.YEAR));
    metadataHandle.setQuality(23);
    return metadataHandle;
  }

  public void validateMetadata(DocumentMetadataHandle mh) {
    // get meta-data values
    DocumentProperties properties = mh.getProperties();
    DocumentPermissions permissions = mh.getPermissions();
    DocumentCollections collections = mh.getCollections();

    // Properties
    // String expectedProperties =
    // "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
    String actualProperties = getDocumentPropertiesString(properties);
    boolean result = actualProperties.contains("size:5|");
    assertTrue( result);

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println(actualPermissions);

    assertTrue(
        actualPermissions.contains("size:6"));
    // assertTrue(
    // "Document permissions difference in flexrep-eval permission",
    // actualPermissions.contains("flexrep-eval:[READ]"));
    assertTrue(
        actualPermissions.contains("rest-reader:[READ]"));
    assertTrue(
        actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions
            .contains("app-user:[READ, UPDATE]")));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println(collections);

    assertTrue(
        actualCollections.contains("size:2"));
    assertTrue(
        actualCollections.contains("my-collection1"));
    assertTrue(
        actualCollections.contains("my-collection2"));
  }

  public void validateDefaultMetadata(DocumentMetadataHandle mh) {
    // get meta-data values
    DocumentProperties properties = mh.getProperties();
    DocumentPermissions permissions = mh.getPermissions();
    DocumentCollections collections = mh.getCollections();

    // Properties
    String actualProperties = getDocumentPropertiesString(properties);
    boolean result = actualProperties.contains("size:0|");
    System.out.println(actualProperties + result);
    assertTrue( result);

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);

    assertTrue( actualPermissions.contains("size:5"));
    // assertTrue(
    // actualPermissions.contains("flexrep-eval:[READ]"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));

    // Collections
    String expectedCollections = "size:0|";
    String actualCollections = getDocumentCollectionsString(collections);

    assertEquals( expectedCollections, actualCollections);
  }

  /*
   * This test verifies multiple JSON content can be written with no meta-data
   * in bulk write set. Use JacksonParserHandle as content handler. Verified by
   * reading individual documents.
   */
  @Test
  public void testWriteMultipleJSONDocs() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    String docId[] = { "/a.json", "/b.json", "/c.json" };
    String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
    String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
    String json3 = new String("{\"animal\":\"rat\", \"says\":\"keek\"}");

    JsonFactory f = new JsonFactory();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentWriteSet writeset = docMgr.newWriteSet();

    JacksonParserHandle jacksonParserHandle1 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle2 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle3 = new JacksonParserHandle();

    jacksonParserHandle1.set(f.createParser(json1));
    jacksonParserHandle2.set(f.createParser(json2));
    jacksonParserHandle3.set(f.createParser(json3));

    writeset.add(docId[0], jacksonParserHandle1);
    writeset.add(docId[1], jacksonParserHandle2);
    writeset.add(docId[2], jacksonParserHandle3);

    docMgr.write(writeset);

    // Using JacksonHandle to read back from database.
    JacksonHandle jacksonhandle = new JacksonHandle();
    docMgr.read(docId[0], jacksonhandle);
    JSONAssert.assertEquals(json1, jacksonhandle.toString(), true);

    docMgr.read(docId[1], jacksonhandle);
    JSONAssert.assertEquals(json2, jacksonhandle.toString(), true);

    docMgr.read(docId[2], jacksonhandle);
    JSONAssert.assertEquals(json3, jacksonhandle.toString(), true);
    // Close handles.
    jacksonParserHandle1.close();
    jacksonParserHandle2.close();
    jacksonParserHandle3.close();
  }

  /*
   * This test verifies multiple JSON content can be written using newFactory
   * method. Use JacksonParserHandle as content handler. Verified by reading
   * individual documents.
   */
  @Test
  public void testWriteMultipleJSONDocsFromFactory() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    String docId[] = { "/a.json", "/b.json", "/c.json" };
    String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
    String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
    String json3 = new String("{\"animal\":\"rat\", \"says\":\"keek\"}");

    JsonFactory f = new JsonFactory();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentWriteSet writeset = docMgr.newWriteSet();

    // Create a content Factory from JacksonDatabindHandle that will handle
    // writes.
    ContentHandleFactory ch = JacksonParserHandle.newFactory();

    // Instantiate a handle.
    JacksonParserHandle jacksonParserHandle1 = (JacksonParserHandle) ch.newHandle(JsonParser.class);
    JacksonParserHandle jacksonParserHandle2 = (JacksonParserHandle) ch.newHandle(JsonParser.class);
    JacksonParserHandle jacksonParserHandle3 = (JacksonParserHandle) ch.newHandle(JsonParser.class);

    jacksonParserHandle1.set(f.createParser(json1));
    jacksonParserHandle2.set(f.createParser(json2));
    jacksonParserHandle3.set(f.createParser(json3));

    writeset.add(docId[0], jacksonParserHandle1);
    writeset.add(docId[1], jacksonParserHandle2);
    writeset.add(docId[2], jacksonParserHandle3);

    docMgr.write(writeset);

    // Using JacksonHandle to read back from database.
    JacksonHandle jacksonhandle = new JacksonHandle();
    docMgr.read(docId[0], jacksonhandle);
    JSONAssert.assertEquals(json1, jacksonhandle.toString(), true);

    docMgr.read(docId[1], jacksonhandle);
    JSONAssert.assertEquals(json2, jacksonhandle.toString(), true);

    docMgr.read(docId[2], jacksonhandle);
    JSONAssert.assertEquals(json3, jacksonhandle.toString(), true);
    // Close handles.
    jacksonParserHandle1.close();
    jacksonParserHandle2.close();
    jacksonParserHandle3.close();
  }

  /*
   *
   * Use JacksonParserHandle to load JSON strings using bulk write set. Test
   * Bulk Read to see you can read the document specific meta-data.
   */

  @Test
  public void testWriteMultipleJSONDocsWithDefaultMetadata() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    String docId[] = { "/a.json", "/b.json", "/c.json" };
    String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
    String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
    String json3 = new String("{\"animal\":\"rat\", \"says\":\"keek\"}");

    JsonFactory f = new JsonFactory();

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);

    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();
    DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

    JacksonParserHandle jacksonParserHandle1 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle2 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle3 = new JacksonParserHandle();

    jacksonParserHandle1.set(f.createParser(json1));
    jacksonParserHandle2.set(f.createParser(json2));
    jacksonParserHandle3.set(f.createParser(json3));

    writeset.addDefault(mh);
    writeset.add(docId[0], jacksonParserHandle1);
    writeset.add(docId[1], jacksonParserHandle2);
    writeset.add(docId[2], jacksonParserHandle3);

    docMgr.write(writeset);

    DocumentPage page = docMgr.read(docId);

    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      docMgr.readMetadata(rec.getUri(), mhRead);
      System.out.println(rec.getUri());
      validateMetadata(mhRead);
    }
    validateMetadata(mhRead);
    // Close handles.
    jacksonParserHandle1.close();
    jacksonParserHandle2.close();
    jacksonParserHandle3.close();
  }

  /*
   *
   * Use JacksonParserHandle to load JSON strings using bulk write set. Test
   * Bulk Read to see you can read all the documents
   */
  @Test
  public void testWriteMultipleJSONDocsWithDefaultMetadata2()
      throws Exception {
    // Synthesize input content
    String doc1 = new String("{\"animal\": \"cat\", \"says\": \"meow\"}");
    String doc2 = new String("{\"animal\": \"dog\", \"says\": \"bark\"}");
    String doc3 = new String(
        "{\"animal\": \"eagle\", \"says\": \"squeak\"}");
    String doc4 = new String("{\"animal\": \"lion\", \"says\": \"roar\"}");
    String doc5 = new String("{\"animal\": \"man\", \"says\": \"hello\"}");

    // Synthesize input meta-data
    DocumentMetadataHandle defaultMetadata1 = new DocumentMetadataHandle()
        .withQuality(1);
    DocumentMetadataHandle defaultMetadata2 = new DocumentMetadataHandle()
        .withQuality(2);
    DocumentMetadataHandle docSpecificMetadata = new DocumentMetadataHandle()
        .withCollections("mySpecificCollection");

    // Create and build up the batch
    JSONDocumentManager jdm = client.newJSONDocumentManager();
    jdm.setMetadataCategories(Metadata.ALL);

    DocumentWriteSet batch = jdm.newWriteSet();

    JsonFactory f = new JsonFactory();

    JacksonParserHandle jacksonParserHandle1 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle2 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle3 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle4 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle5 = new JacksonParserHandle();

    jacksonParserHandle1.set(f.createParser(doc1));
    jacksonParserHandle2.set(f.createParser(doc2));
    jacksonParserHandle3.set(f.createParser(doc3));
    jacksonParserHandle4.set(f.createParser(doc4));
    jacksonParserHandle5.set(f.createParser(doc5));

    // use system default meta-data
    batch.add("doc1.json", jacksonParserHandle1);

    // using batch default meta-data
    batch.addDefault(defaultMetadata1);
    batch.add("doc2.json", jacksonParserHandle2); // batch default meta-data
    batch.add("doc3.json", docSpecificMetadata, jacksonParserHandle3);
    batch.add("doc4.json", jacksonParserHandle4); // batch default meta-data

    // replace batch default meta-data with new meta-data
    batch.addDefault(defaultMetadata2);
    batch.add("doc5.json", jacksonParserHandle5); // batch default

    // Execute the write operation
    jdm.write(batch);
    DocumentPage page;
    DocumentRecord rec;
    // Check the results
    // Doc1 should have the system default quality of 0
    page = jdm.read("doc1.json");
    DocumentMetadataHandle mh = new DocumentMetadataHandle();
    rec = page.next();
    jdm.readMetadata(rec.getUri(), mh);
    validateDefaultMetadata(mh);
    assertEquals( 0, mh.getQuality());

    // Doc2 should use the first batch default meta-data, with quality 1
    page = jdm.read("doc2.json");
    rec = page.next();
    jdm.readMetadata(rec.getUri(), mh);
    System.out.print(mh.getCollections().isEmpty());
    assertEquals( 1, mh.getQuality());
    assertTrue( mh.getCollections().isEmpty());

    // Doc3 should have the system default document quality (0) because
    // quality
    // was not included in the document-specific meta-data. It should be in
    // the
    // collection "mySpecificCollection", from the document-specific
    // meta-data.

    page = jdm.read("doc3.json");
    rec = page.next();
    jdm.readMetadata(rec.getUri(), mh);
    assertEquals( 0, mh.getQuality());
    assertEquals(
        "[mySpecificCollection]", mh.getCollections().toString());

    DocumentMetadataHandle doc3Metadata = jdm.readMetadata("doc3.json",
        new DocumentMetadataHandle());
    System.out.println("doc3 quality: Expected=0, Actual="
        + doc3Metadata.getPermissions());
    System.out.print("doc3 collections: Expected: myCollection, Actual=");
    for (String collection : doc3Metadata.getCollections()) {
      System.out.print(collection + " ");
    }
    System.out.println();

    // Doc 4 should also use the 1st batch default meta-data, with quality 1
    page = jdm.read("doc4.json");
    rec = page.next();
    jdm.readMetadata(rec.getUri(), mh);
    assertEquals( 1, mh.getQuality());
    assertTrue( mh.getCollections().isEmpty());
    // Doc5 should use the 2nd batch default meta-data, with quality 2
    page = jdm.read("doc5.json");
    rec = page.next();
    jdm.readMetadata(rec.getUri(), mh);
    assertEquals( 2, mh.getQuality());
    // Close handles.
    jacksonParserHandle1.close();
    jacksonParserHandle2.close();
    jacksonParserHandle3.close();
    jacksonParserHandle4.close();
    jacksonParserHandle5.close();
  }

  /*
   * Purpose : To read JSON files from file system and write into DB. Use
   * JacksonParserHandle to load JSON files using bulk write set. Test Bulk Read
   * to see you can read the document specific meta-data.
   */

  @Test
  public void testWriteMultiJSONFilesDefaultMetadata() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    String docId[] = { "/original.json", "/updated.json", "/constraint1.json" };
    String jsonFilename1 = "json-original.json";
    String jsonFilename2 = "json-updated.json";
    String jsonFilename3 = "constraint1.json";

    File jsonFile1 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + jsonFilename1);
    File jsonFile2 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + jsonFilename2);
    File jsonFile3 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + jsonFilename3);

    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();
    DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

    JsonFactory f = new JsonFactory();

    JacksonParserHandle jacksonParserHandle1 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle2 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle3 = new JacksonParserHandle();

    jacksonParserHandle1.set(f.createParser(jsonFile1));
    jacksonParserHandle2.set(f.createParser(jsonFile2));
    jacksonParserHandle3.set(f.createParser(jsonFile3));

    writeset.addDefault(mh);
    writeset.add(docId[0], jacksonParserHandle1);
    writeset.add(docId[1], jacksonParserHandle2);
    writeset.add(docId[2], jacksonParserHandle3);

    docMgr.write(writeset);

    DocumentPage page = docMgr.read(docId);

    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      docMgr.readMetadata(rec.getUri(), mhRead);
      System.out.println(rec.getUri());
      validateMetadata(mhRead);
    }
    validateMetadata(mhRead);
    mhRead = null;
    // Close handles.
    jacksonParserHandle1.close();
    jacksonParserHandle2.close();
    jacksonParserHandle3.close();
  }

  /*
   * Purpose: Bulk Search with JacksonParserHandle Use JacksonParserHandle to
   * retrieve JSON bulk search result set. Test Bulk Search with
   * JacksonParserHandle
   */
  @Test
  public void testBulkSearchQBEWithJSONResponseFormat() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException,
      TransformerException {
    int count;
    String docId[] = { "/a.json", "/b.json", "/c.json" };
    String json1 = new String("{\"animal\":\"dog\", \"says\":\"woof\"}");
    String json2 = new String("{\"animal\":\"cat\", \"says\":\"meow\"}");
    String json3 = new String("{\"animal\":\"rat\", \"says\":\"keek\"}");

    JsonFactory f = new JsonFactory();

    JSONDocumentManager docMgr1 = client.newJSONDocumentManager();
    docMgr1.setMetadataCategories(Metadata.ALL);
    DocumentWriteSet writeset = docMgr1.newWriteSet();

    JacksonParserHandle jacksonParserHandle1 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle2 = new JacksonParserHandle();
    JacksonParserHandle jacksonParserHandle3 = new JacksonParserHandle();

    jacksonParserHandle1.set(f.createParser(json1));
    jacksonParserHandle2.set(f.createParser(json2));
    jacksonParserHandle3.set(f.createParser(json3));

    writeset.add(docId[0], jacksonParserHandle1);
    writeset.add(docId[1], jacksonParserHandle2);
    writeset.add(docId[2], jacksonParserHandle3);
    // Write to database.
    docMgr1.write(writeset);

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

      // Add additional asserts once JacksonParserHandle is ready to handle bulk
      // Search set.

      // assertTrue(sh.get().get("start").asLong()
      // == page.getStart());
      assertEquals( page.size(), count);
      pageNo = pageNo + page.getPageSize();
    } while (!page.isLastPage() && page.hasContent());

    assertEquals( 1, page.getTotalPages());
    assertFalse( page.hasPreviousPage());
    assertEquals( 25, page.getPageSize());
    assertEquals( 1, page.getTotalSize());
    // Close handles.
    jacksonParserHandle1.close();
    jacksonParserHandle2.close();
    jacksonParserHandle3.close();
  }

  public void validateRecord(DocumentRecord record, Format type) {

    assertNotNull( record);
    assertNotNull( record.getUri());
    assertTrue(record.getUri().startsWith(DIRECTORY));
    assertEquals( type,
        record.getFormat());

  }
}
