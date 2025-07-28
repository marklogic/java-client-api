/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

/**
 *
 */
package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.document.*;
import com.marklogic.client.io.*;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;




/*
 *         This test is test the DocumentWriteSet function public
 *         DocumentWriteSet add(String uri, DocumentMetadataWriteHandle
 *         metadataHandle); This test intention is to test the system default ,
 *         request wide, and document specific update to metadata will overwrite
 *         the existing metadata setup: create a usr1 with system level meta
 *         data as default client make a connection with usr1 to do bulk loading
 *         load first set of documents without default meta data and check
 *         documents get defaults from usr defaults load second set of documents
 *         where default metadata is set in the middle to see documents at the
 *         begining has old defaults and later have latest load third set of
 *         documents with defaultset and do a document specific metadata update
 *         and check that is updated.
 *
 */
public class TestBulkWriteMetadata2 extends AbstractFunctionalTest {

  /*
   * @throws java.lang.Exception
   */
  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    System.out.println("In Setup");
    createRESTUser("app-user", "password", "rest-writer", "rest-reader");
    createRESTUserWithPermissions("usr1", "password", getPermissionNode("flexrep-eval", Capability.READ), getCollectionNode("http://permission-collections/"), "rest-writer",
        "rest-reader");
    setMaintainLastModified("java-functest", true);
  }

  /*
   * @throws java.lang.Exception
   */
  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    setMaintainLastModified("java-functest", false);
    deleteRESTUser("app-user");
    deleteRESTUser("usr1");
  }

  /*
   * @throws java.lang.Exception
   */
  @BeforeEach
  public void setUp() throws Exception {
    // create new connection for each test below
    client = getDatabaseClient("usr1", "password", getConnType());
  }

  /*
   * @throws java.lang.Exception
   */
  @AfterEach
  public void tearDown() throws Exception {
    client.release();
  }

  public DocumentMetadataHandle setMetadata() {
    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
    metadataHandle.getCollections().addAll("my-collection1", "my-collection2");
    metadataHandle.getPermissions().add("app-user", Capability.UPDATE, Capability.READ);
    metadataHandle.getProperties().put("reviewed", true);
    metadataHandle.getProperties().put("myString", "foo");
    metadataHandle.getProperties().put("myInteger", 10);
    metadataHandle.getProperties().put("myDecimal", 34.56678);
    metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
    metadataHandle.setQuality(23);
    return metadataHandle;
  }

  public void validateMetadata(DocumentMetadataHandle mh) {
    // get metadata values
    DocumentProperties properties = mh.getProperties();
    DocumentPermissions permissions = mh.getPermissions();
    DocumentCollections collections = mh.getCollections();

    // Properties
    // String expectedProperties =
    // "size:5|reviewed:true|myInteger:10|myDecimal:34.56678|myCalendar:2014|myString:foo|";
    String actualProperties = getDocumentPropertiesString(properties);
    boolean result = actualProperties.contains("size:6|");
    assertTrue( result);

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println(actualPermissions);

    assertTrue( actualPermissions.contains("size:6"));
    assertTrue( actualPermissions.contains("flexrep-eval:[READ]"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println(collections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("my-collection1"));
    assertTrue( actualCollections.contains("my-collection2"));
  }

  public void validateDefaultMetadata(DocumentMetadataHandle mh) {
    // get metadata values
    DocumentProperties properties = mh.getProperties();
    DocumentPermissions permissions = mh.getPermissions();
    DocumentCollections collections = mh.getCollections();

    // Properties
    String actualProperties = getDocumentPropertiesString(properties);
    boolean result = actualProperties.contains("size:1|");
    System.out.println(actualProperties + result);
    assertTrue( result);

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);

    assertTrue( actualPermissions.contains("size:5"));
    assertTrue( actualPermissions.contains("flexrep-eval:[READ]"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));

    // Collections
    String expectedCollections = "size:1|http://permission-collections/|";
    String actualCollections = getDocumentCollectionsString(collections);

    assertEquals( expectedCollections, actualCollections);
  }

  @Test
  public void testWriteMultipleTextDocWithDefaultMetadata2()
  {
    DocumentMetadataHandle mh1, mh2;
    String docId[] = { "/foo/test/myFoo1.txt", "/foo/test/myFoo2.txt", "/foo/test/myFoo3.txt",
        "/foo/test/myFoo4.txt", "/foo/test/myFoo5.txt", "/foo/test/myFoo6.txt",
        "/foo/test/myFoo7.txt", "/foo/test/myFoo8.txt", "/foo/test/myFoo9.txt" };

    TextDocumentManager docMgr = client.newTextDocumentManager();

    DocumentWriteSet writeset = docMgr.newWriteSet();

    writeset.add(docId[0], new StringHandle().with("This is so foo1"));
    writeset.add(docId[1], new StringHandle().with("This is so foo2"));
    writeset.add(docId[2], new StringHandle().with("This is so foo3"));
    docMgr.write(writeset);
    // Default system property document set
    System.out.println("Assert if document set doesnt show default properties");
    DocumentPage page = docMgr.read(docId);
    mh1 = new DocumentMetadataHandle();
    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      docMgr.readMetadata(rec.getUri(), mh1);
      validateDefaultMetadata(mh1);
    }
    validateDefaultMetadata(mh1);

    // Adding document specific properties in document set2

    System.out.println("Assert if document 5 show default properties");

    writeset = docMgr.newWriteSet();
    // put metadata
    mh2 = setMetadata();

    writeset.add(docId[3], new StringHandle().with("This is so foo4"));
    writeset.add(docId[4], mh2, new StringHandle().with("This is so foo5"));
    writeset.add(docId[5], new StringHandle().with("This is so foo6"));
    docMgr.write(writeset);

    page = docMgr.read(docId[4]);
    mh2 = new DocumentMetadataHandle();
    DocumentRecord rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateMetadata(mh2);
    page = docMgr.read(docId[3]);
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateDefaultMetadata(mh2);
    page = docMgr.read(docId[5]);
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateDefaultMetadata(mh2);

    System.out.println("Assert if documents 8 and 9 show default properties and if 7 doesnt show default");

    writeset = docMgr.newWriteSet();
    // put metadata
    mh2 = setMetadata();

    writeset.add(docId[6], new StringHandle().with("This is so foo4"));
    writeset.addDefault(mh2);
    writeset.add(docId[7], new StringHandle().with("This is so foo5"));
    writeset.add(docId[8], new StringHandle().with("This is so foo6"));
    docMgr.write(writeset);

    page = docMgr.read(docId[6]);
    mh2 = new DocumentMetadataHandle();
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateDefaultMetadata(mh2);

    page = docMgr.read(docId[7]);
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateMetadata(mh2);

    page = docMgr.read(docId[8]);
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateMetadata(mh2);
  }

  @Test
  public void testWriteMultipleXMLDocWithDefaultMetadata2() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    // put metadata
    DocumentMetadataHandle mh1, mh2;
    String docId[] = { "/foo/test/Foo1.xml", "/foo/test/Foo2.xml", "/foo/test/Foo3.xml",
        "/foo/test/Foo4.xml", "/foo/test/Foo5.xml", "/foo/test/Foo6.xml",
        "/foo/test/Foo7.xml", "/foo/test/Foo8.xml", "/foo/test/Foo8.xml" };
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentWriteSet writeset = docMgr.newWriteSet();

    DocumentDescriptor docdisc = docMgr.newDescriptor("test1");

    writeset.add(docdisc, new DOMHandle(getDocumentContent("This is so foo1")));
    writeset.add(docId[1], new DOMHandle().with(getDocumentContent("This is so foo2")));
    writeset.add(docId[2], new DOMHandle().with(getDocumentContent("This is so foo3")));

    docMgr.write(writeset);

    DocumentPage page = docMgr.read(docId);
    DocumentRecord rec;
    mh1 = new DocumentMetadataHandle();
    while (page.hasNext()) {
      rec = page.next();
      docMgr.readMetadata(rec.getUri(), mh1);
      validateDefaultMetadata(mh1);
    }
    validateDefaultMetadata(mh1);

    // put metadata
    mh2 = setMetadata();
    writeset = docMgr.newWriteSet();

    writeset.add(docId[3], new DOMHandle(getDocumentContent("This is so foo4")));
    writeset.addDefault(mh2);
    writeset.add(docId[4], new DOMHandle().with(getDocumentContent("This is so foo5")));
    writeset.add(docId[5], new DOMHandle().with(getDocumentContent("This is so foo6")));
    docMgr.write(writeset);

    page = docMgr.read(docId[3]);
    mh2 = new DocumentMetadataHandle();
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateDefaultMetadata(mh2);

    page = docMgr.read(docId[4]);
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateMetadata(mh2);

    page = docMgr.read(docId[5]);
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateMetadata(mh2);
    // Overwriting the existing properties
    mh2 = setMetadata();
    writeset = docMgr.newWriteSet();
    writeset.add(docId[3], mh2, new DOMHandle(getDocumentContent("This is so foo4")));
    docMgr.write(writeset);

    page = docMgr.read(docId[3]);
    mh2 = new DocumentMetadataHandle();
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh2);
    validateMetadata(mh2);
  }

  @Test
  public void testWriteMultipleBinaryDocWithDefaultMetadata2() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    String docId[] = { "Pandakarlino.jpg", "mlfavicon.png" };

    BinaryDocumentManager docMgr = client.newBinaryDocumentManager();
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put metadata
    DocumentMetadataHandle mh = setMetadata();

    File file1 = null, file2 = null;
    file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + docId[0]);
    FileHandle handle1 = new FileHandle(file1);
    writeset.add("/1/" + docId[0], handle1.withFormat(Format.BINARY));

    writeset.add("/2/" + docId[0], new DocumentMetadataHandle().withQuality(5), handle1.withFormat(Format.BINARY));

    file2 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + docId[1]);
    FileHandle handle2 = new FileHandle(file2);
    writeset.add("/1/" + docId[1], new DocumentMetadataHandle().withCollections("collection1"), handle2.withFormat(Format.BINARY));

    writeset.addDefault(mh);

    writeset.add("/2/" + docId[1], handle2.withFormat(Format.BINARY));
    docMgr.write(writeset);

    DocumentPage page = docMgr.read("/1/Pandakarlino.jpg");
    DocumentRecord rec;
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh);
    // System.out.println(rec.getUri()+mh.getQuality());
    assertEquals( 0, mh.getQuality());
    validateDefaultMetadata(mh);
    page = docMgr.read("/2/Pandakarlino.jpg");
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh);
    assertEquals( 5, mh.getQuality());

    // Collections - Test corrected for bug fix from 32783
    /*
     * Default collections are applied only in the following cases:
     *
     * Creating a document with single write without specifying collections
     * Creating a document or updating document content with bulk write without
     * specifying collections Resetting collections by deleting all or
     * collection metadata
     */

    String expectedCollections = "size:1|http://permission-collections/|";
    String actualCollections = getDocumentCollectionsString(mh.getCollections());

    assertEquals( expectedCollections, actualCollections);

    page = docMgr.read("/1/mlfavicon.png");
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh);
    assertEquals( 0, mh.getQuality());
    // System.out.println(rec.getUri()+mh.getCollections().isEmpty());
    expectedCollections = "size:1|collection1|";
    actualCollections = getDocumentCollectionsString(mh.getCollections());

    assertEquals( expectedCollections, actualCollections);

    page = docMgr.read("/2/mlfavicon.png");
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh);
    validateMetadata(mh);
  }

  /*
   * This is test is made up for the github issue 25
   */
  @Test
  public void testWriteMultipleJSONDocsWithDefaultMetadata2() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    // Synthesize input content
    StringHandle doc1 = new StringHandle(
        "{\"number\": 1}").withFormat(Format.JSON);
    StringHandle doc2 = new StringHandle(
        "{\"number\": 2}").withFormat(Format.JSON);
    StringHandle doc3 = new StringHandle(
        "{\"number\": 3}").withFormat(Format.JSON);
    StringHandle doc4 = new StringHandle(
        "{\"number\": 4}").withFormat(Format.JSON);
    StringHandle doc5 = new StringHandle(
        "{\"number\": 5}").withFormat(Format.JSON);

    // Synthesize input metadata
    DocumentMetadataHandle defaultMetadata1 =
        new DocumentMetadataHandle().withQuality(1);
    DocumentMetadataHandle defaultMetadata2 =
        new DocumentMetadataHandle().withQuality(2);
    DocumentMetadataHandle docSpecificMetadata =
        new DocumentMetadataHandle().withCollections("mySpecificCollection");

    // Create and build up the batch
    JSONDocumentManager jdm = client.newJSONDocumentManager();
    DocumentWriteSet batch = jdm.newWriteSet();

    // use system default metadata
    batch.add("doc1.json", doc1); // system default metadata

    // using batch default metadata
    batch.addDefault(defaultMetadata1);
    batch.add("doc2.json", doc2); // batch default metadata
    batch.add("doc3.json", docSpecificMetadata, doc3);
    batch.add("doc4.json", doc4); // batch default metadata

    // replace batch default metadata with new metadata
    batch.addDefault(defaultMetadata2);
    batch.add("doc5.json", doc5); // batch default

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

    // Doc2 should use the first batch default metadata, with quality 1
    page = jdm.read("doc2.json");
    rec = page.next();
    jdm.readMetadata(rec.getUri(), mh);
    System.out.print(mh.getCollections().isEmpty());
    assertEquals( 1, mh.getQuality());
    String expectedCollections = "size:1|http://permission-collections/|";
    String actualCollections = getDocumentCollectionsString(mh.getCollections());

    assertEquals( expectedCollections, actualCollections);

    // Doc3 should have the system default document quality (0) because quality
    // was not included in the document-specific metadata. It should be in the
    // collection "mySpecificCollection", from the document-specific metadata.

    page = jdm.read("doc3.json");
    rec = page.next();
    jdm.readMetadata(rec.getUri(), mh);
    assertEquals( 0, mh.getQuality());
    assertEquals( "[mySpecificCollection]", mh.getCollections().toString());

    DocumentMetadataHandle doc3Metadata =
        jdm.readMetadata("doc3.json", new DocumentMetadataHandle());
    System.out.println("doc3 quality: Expected=0, Actual=" + doc3Metadata.getPermissions());
    expectedCollections = "size:1|mySpecificCollection|";
    actualCollections = getDocumentCollectionsString(mh.getCollections());

    assertEquals( expectedCollections, actualCollections);

    // Doc 4 should also use the 1st batch default metadata, with quality 1
    page = jdm.read("doc4.json");
    rec = page.next();
    jdm.readMetadata(rec.getUri(), mh);
    assertEquals( 1, mh.getQuality());
    expectedCollections = "size:1|http://permission-collections/|";
    actualCollections = getDocumentCollectionsString(mh.getCollections());

    assertEquals( expectedCollections, actualCollections);
    // Doc5 should use the 2nd batch default metadata, with quality 2
    page = jdm.read("doc5.json");
    rec = page.next();
    jdm.readMetadata(rec.getUri(), mh);
    assertEquals( 2, mh.getQuality());

  }

  @Test
  public void testWriteGenericDocMgrWithDefaultMetadata() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    String docId[] = { "Sega-4MB.jpg" };

    GenericDocumentManager docMgr = client.newDocumentManager();
    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put metadata
    DocumentMetadataHandle mh = setMetadata();

    writeset.addDefault(mh); // Adding default metadata to the entire batch
    File file1 = null;
    file1 = new File("src/test/java/com/marklogic/client/functionaltest/data/" + docId[0]);
    FileInputStream fis = new FileInputStream(file1);
    InputStreamHandle handle1 = new InputStreamHandle(fis);
    handle1.setFormat(Format.BINARY);
    // This document implicitly gets the default metadata that we added in the beginning
    writeset.add("/generic/Pandakarlino.jpg", handle1);
    JacksonHandle jh = new JacksonHandle();
    ObjectMapper objM = new ObjectMapper();
    JsonNode jn = objM.readTree(new String("{\"animal\":\"dog\", \"says\":\"woof\"}"));
    jh.set(jn);
    jh.setFormat(Format.JSON);
    // This document suppose to get the in scope metadata quality and should set collections to system default
    writeset.add("/generic/dog.json", new DocumentMetadataHandle().withQuality(10), jh);

    String foo1 = "This is foo1 of byte Array";
    byte[] ba = foo1.getBytes();
    BytesHandle bh = new BytesHandle(ba);
    bh.setFormat(Format.TEXT);

    writeset.add("/generic/foo1.txt", bh);

    Source ds = new DOMSource(getDocumentContent("This is so foo1"));
    SourceHandle sh = new SourceHandle();
    sh.set(ds);
    sh.setFormat(Format.XML);
    DocumentMetadataHandle mh2 = new DocumentMetadataHandle();
    // This document should over write the system default and default collection list and get document specific collection
    writeset.add("/generic/foo.xml", mh2.withCollections("genericCollection"), sh);

    docMgr.write(writeset);

    DocumentPage page = docMgr.read("/generic/Pandakarlino.jpg", "/generic/foo1.txt");

    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      docMgr.readMetadata(rec.getUri(), mh);
      System.out.println(rec.getUri());
      validateMetadata(mh);
    }
    validateMetadata(mh);
    page = docMgr.read("/generic/dog.json");
    DocumentRecord rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh);
    assertEquals( 10, mh.getQuality());

    String expectedCollections = "size:1|http://permission-collections/|";
    String actualCollections = getDocumentCollectionsString(mh.getCollections());

    assertEquals( expectedCollections, actualCollections);

    page = docMgr.read("/generic/foo.xml");
    rec = page.next();
    docMgr.readMetadata(rec.getUri(), mh);
    assertEquals( 0, mh.getQuality());
    assertEquals( "[genericCollection]", mh.getCollections().toString());
    sh.close();
  }
}
