/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

/**
 * This tests meta-data changes for text documents.
 */
package com.marklogic.client.fastfunctest;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.*;
import com.marklogic.client.functionaltest.Product;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentProperties;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;




/**
 *
 * This test is designed to update meta-data bulk writes and reads with one type
 * of Manager and one content type text.
 *
 * TextDocumentManager
 *
 */
public class TestBulkReadWriteMetaDataChange extends AbstractFunctionalTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    createRESTUser("app-user", "password", "rest-writer", "rest-reader");
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    deleteRESTUser("app-user");
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  public void setUp() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    // create new connection for each test below
    client = getDatabaseClient("app-user", "password", getConnType());
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterEach
  public void tearDown() throws Exception {
    System.out.println("Running clear script");
    // release client
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

  public DocumentMetadataHandle setUpdatedMetadataProperties() {
    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();

    metadataHandle.getProperties().put("reviewed", false);
    metadataHandle.getProperties().put("myString", "bar");
    metadataHandle.getProperties().put("myInteger", 20);
    metadataHandle.getProperties().put("myDecimal", 3459.012678);
    metadataHandle.getProperties().put("myCalendar", Calendar.getInstance().get(Calendar.YEAR));
    metadataHandle.setQuality(12);
    return metadataHandle;
  }

  public DocumentMetadataHandle setUpdatedMetadataCollections() {
    // create and initialize a handle on the metadata
    DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle().withCollections("my-collection3", "my-collection4");
    // metadataHandle.getCollections().addAll("my-collection1","my-collection2");
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
    String actualProperties = getDocumentPropertiesString(properties);
    System.out.println("Returned properties: " + actualProperties);
    StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

    assertTrue( actualProperties.contains("size:5"));
    assertTrue( actualProperties.contains("reviewed:true"));
    assertTrue( actualProperties.contains("myInteger:10"));
    assertTrue( actualProperties.contains("myDecimal:34.56678"));
    assertTrue( actualProperties.contains(calProperty.toString()));
    assertTrue( actualProperties.contains("myString:foo"));

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println("Returned permissions: " + actualPermissions);

    assertTrue( actualPermissions.contains("size:5"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println("Returned collections: " + actualCollections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("my-collection1"));
    assertTrue( actualCollections.contains("my-collection2"));
  }

  public void validateUpdatedMetadataProperties(DocumentMetadataHandle mh) {
    // get metadata values
    DocumentProperties properties = mh.getProperties();
    DocumentPermissions permissions = mh.getPermissions();
    DocumentCollections collections = mh.getCollections();

    // Properties
    String actualProperties = getDocumentPropertiesString(properties);
    System.out.println("Returned properties after Meta-data only update: " + actualProperties);
    StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

    assertTrue( actualProperties.contains("size:5"));
    assertTrue( actualProperties.contains("reviewed:true"));
    assertTrue( actualProperties.contains("myInteger:10"));
    assertTrue( actualProperties.contains("myDecimal:34.56678"));
    assertTrue( actualProperties.contains(calProperty.toString()));
    assertTrue( actualProperties.contains("myString:foo"));

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println("Returned permissions: " + actualPermissions);

    assertTrue( actualPermissions.contains("size:5"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println("Returned collections: " + actualCollections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("my-collection1"));
    assertTrue( actualCollections.contains("my-collection2"));
  }

  public void validateUpdatedMetadataCollections(DocumentMetadataHandle mh) {
    // get metadata values
    DocumentProperties properties = mh.getProperties();
    DocumentPermissions permissions = mh.getPermissions();
    DocumentCollections collections = mh.getCollections();

    // Properties
    String actualProperties = getDocumentPropertiesString(properties);
    System.out.println("Returned properties: " + actualProperties);
    StringBuffer calProperty = new StringBuffer("myCalendar:").append(Calendar.getInstance().get(Calendar.YEAR));

    assertTrue( actualProperties.contains("size:5"));
    assertTrue( actualProperties.contains("reviewed:true"));
    assertTrue( actualProperties.contains("myInteger:10"));
    assertTrue( actualProperties.contains("myDecimal:34.56678"));
    assertTrue( actualProperties.contains(calProperty.toString()));
    assertTrue( actualProperties.contains("myString:foo"));

    // Permissions
    String actualPermissions = getDocumentPermissionsString(permissions);
    System.out.println("Returned permissions: " + actualPermissions);

    assertTrue( actualPermissions.contains("size:5"));
    assertTrue( actualPermissions.contains("rest-reader:[READ]"));
    assertTrue( actualPermissions.contains("rest-writer:[UPDATE]"));
    assertTrue(
        (actualPermissions.contains("app-user:[UPDATE, READ]") || actualPermissions.contains("app-user:[READ, UPDATE]")));
    assertTrue( actualPermissions.contains("harmonized-updater:[UPDATE]"));
    assertTrue( actualPermissions.contains("harmonized-reader:[READ]"));

    // Collections
    String actualCollections = getDocumentCollectionsString(collections);
    System.out.println("Returned collections: " + actualCollections);

    assertTrue( actualCollections.contains("size:2"));
    assertTrue( actualCollections.contains("my-collection3"));
    assertTrue( actualCollections.contains("my-collection4"));
  }

  /*
   * This test verifies that properties do not change when new meta data is used
   * in a bulk write set. Verified by reading individual documents. User does
   * not have permission to update the meta-data.
   */

  @Test
  public void testWriteMultipleTextDocWithChangedMetadataProperties() {
    String docId[] = { "/foo/test/myFoo1.txt", "/foo/test/myFoo2.txt", "/foo/test/myFoo3.txt" };

    TextDocumentManager docMgr = client.newTextDocumentManager();

    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();
    DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

    writeset.addDefault(mh);
    writeset.add(docId[0], new StringHandle().with("This is so foo1"));
    writeset.add(docId[1], new StringHandle().with("This is so foo2"));
    writeset.add(docId[2], new StringHandle().with("This is so foo3"));
    docMgr.write(writeset);
    StringHandle sh = docMgr.read(docId[0], new StringHandle());
    System.out.println(sh.get());
    DocumentPage page = docMgr.read(docId);
    // Issue #294 DocumentPage.size() should return correct size
    assertTrue(page.size() == 3);

    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      System.out.println(rec.getUri());
      docMgr.readMetadata(rec.getUri(), mhRead);
      validateMetadata(mhRead);
    }
    validateMetadata(mhRead);
    mhRead = null;

    // Add new meta-data
    DocumentMetadataHandle mhUpdated = setUpdatedMetadataProperties();
    writeset.addDefault(mhUpdated);

    docMgr.write(writeset);
    DocumentMetadataHandle mhUpd = new DocumentMetadataHandle();

    for (String docURI : docId) {
      docMgr.readMetadata(docURI, mhUpd);
      validateUpdatedMetadataProperties(mhUpd);
    }
    validateUpdatedMetadataProperties(mhUpd);
    mhUpd = null;
  }

  @Test
  public void testWriteMultipleJacksonPoJoDocsWithMetadata() throws KeyManagementException, NoSuchAlgorithmException, Exception
  {
    String docId[] = { "/jack/iphone.json", "/jack/ipad.json", "/jack/ipod.json" };
    Product product1 = new Product();
    product1.setName("iPhone");
    product1.setIndustry("Hardware");
    product1.setDescription("Very cool Iphone");
    Product product2 = new Product();
    product2.setName("iPad");
    product2.setIndustry("Hardware");
    product2.setDescription("Very cool Ipad");
    Product product3 = new Product();
    product3.setName("iPod");
    product3.setIndustry("Hardware");
    product3.setDescription("Very cool Ipod");

    DocumentMetadataHandle mh = setMetadata();
    DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

    JacksonHandle writeHandle = new JacksonHandle();
    JsonNode writeDocument = writeHandle.getMapper().convertValue(product1, JsonNode.class);
    writeHandle.set(writeDocument);
    JsonNode writeDocument2 = writeHandle.getMapper().convertValue(product2, JsonNode.class);
    JsonNode writeDocument3 = writeHandle.getMapper().convertValue(product3, JsonNode.class);
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentWriteSet writeset = docMgr.newWriteSet();

    writeset.addDefault(mh);
    writeset.add(docId[0], writeHandle);
    writeset.add(docId[1], new JacksonHandle().with(writeDocument2));
    DocumentMetadataHandle mhUpdated = setUpdatedMetadataCollections();
    writeset.add(docId[2], mhUpdated, new JacksonHandle().with(writeDocument3));
    docMgr.write(writeset);

    JacksonHandle jh = new JacksonHandle();
    docMgr.read(docId[0], jh);
    String exp = "{\"name\":\"iPhone\",\"industry\":\"Hardware\",\"description\":\"Very cool Iphone\"}";
    JSONAssert.assertEquals(exp, jh.get().toString(), false);
    docMgr.readMetadata(docId[0], mhRead);
    validateMetadata(mhRead);

    docMgr.read(docId[1], jh);
    exp = "{\"name\":\"iPad\",\"industry\":\"Hardware\",\"description\":\"Very cool Ipad\"}";
    JSONAssert.assertEquals(exp, jh.get().toString(), false);
    docMgr.readMetadata(docId[1], mhRead);
    validateMetadata(mhRead);

    docMgr.read(docId[2], jh);
    exp = "{\"name\":\"iPod\",\"industry\":\"Hardware\",\"description\":\"Very cool Ipod\"}";
    JSONAssert.assertEquals(exp, jh.get().toString(), false);
    docMgr.readMetadata(docId[2], mhRead);
    this.validateUpdatedMetadataCollections(mhRead);
  }

  /*
   * Purpose: To validate: DocumentManager::read(Transaction, uri....) This test
   * verifies document meta-data reads from an open database transaction in the
   * representation provided by the handle to call readMetadata. Verified by
   * reading meta-data of individual document records from Document Page. read
   * method performs the bulk read
   */
  @Test
  public void testBulkReadUsingMultipleUri() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    String docId[] = { "/foo/test/transactionURIFoo1.txt", "/foo/test/transactionURIFoo2.txt", "/foo/test/transactionURIFoo3.txt" };
    Transaction transaction = client.openTransaction();
    try {
      TextDocumentManager docMgr = client.newTextDocumentManager();
      docMgr.setMetadataCategories(Metadata.ALL);
      DocumentWriteSet writeset = docMgr.newWriteSet();
      // put meta-data
      DocumentMetadataHandle mh = setMetadata();
      DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

      writeset.addDefault(mh);
      writeset.add(docId[0], new StringHandle().with("This is so transactionURIFoo 1"));
      writeset.add(docId[1], new StringHandle().with("This is so transactionURIFoo 2"));
      writeset.add(docId[2], new StringHandle().with("This is so transactionURIFoo 3"));
      docMgr.write(writeset, transaction);
      transaction.commit();
      transaction = client.openTransaction();

      DocumentPage page = docMgr.read(transaction, docId[0], docId[1], docId[2]);
      // Issue #294 DocumentPage.size() should return correct size
      assertTrue(page.size() == 3);

      while (page.hasNext()) {
        DocumentRecord rec = page.next();
        mhRead = rec.getMetadata(mhRead);
        validateMetadata(mhRead);
      }
      validateMetadata(mhRead);
      mhRead = null;
    } catch (Exception exp) {
      System.out.println(exp.getMessage());
      throw exp;
    } finally {
      transaction.rollback();
    }
  }

  /*
   * * Purpose: To validate: DocumentManager::readMetadata(uri, MetdataHandle,
   * Transaction) This test verifies document meta-data reads from an open
   * database transaction in the representation provided by the handle to call
   * readMetadata. Verified by reading meta-data for individual documents.
   */

  @Test
  public void testReadUsingMultipleUriAndMetadataHandleInTransaction() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    String docId[] = { "/foo/test/multipleURIFoo1.txt", "/foo/test/multipleURIFoo2.txt", "/foo/test/multipleURIFoo3.txt" };
    Transaction transaction = client.openTransaction();
    try {
      TextDocumentManager docMgr = client.newTextDocumentManager();
      docMgr.setMetadataCategories(Metadata.ALL);

      DocumentWriteSet writeset = docMgr.newWriteSet();
      // put meta-data
      DocumentMetadataHandle mh = setMetadata();

      writeset.addDefault(mh);
      writeset.add(docId[0], new StringHandle().with("This is so multipleURI foo 1"));
      writeset.add(docId[1], new StringHandle().with("This is so multipleURI foo 2"));
      writeset.add(docId[2], new StringHandle().with("This is so multipleURI foo 3"));
      docMgr.write(writeset, transaction);
      transaction.commit();
      transaction = client.openTransaction();

      DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

      for (String docStrId : docId) {
        docMgr.readMetadata(docStrId, mhRead, transaction);
        validateMetadata(mhRead);
      }
      validateMetadata(mhRead);
      mhRead = null;
    } catch (Exception exp) {
      System.out.println(exp.getMessage());
      throw exp;
    } finally {
      transaction.rollback();
    }
  }

  /*
   * * Purpose: To validate DocumentManager readMetadata(String... uris) without
   * Transaction This test verifies document meta-data reads from an open
   * database in the representation provided by the handle to call readMetadata.
   * Verified by reading meta-data for individual documents.
   */

  @Test
  public void testBulkReadMetadataUsingMultipleUriNoTransaction() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    String docId[] = { "/foo/test/URIFoo1.txt", "/foo/test/URIFoo2.txt", "/foo/test/URIFoo3.txt" };
    DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

    TextDocumentManager docMgr = client.newTextDocumentManager();
    docMgr.setMetadataCategories(Metadata.ALL);

    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();

    writeset.addDefault(mh);
    writeset.add(docId[0], new StringHandle().with("This is so URI foo 1"));
    writeset.add(docId[1], new StringHandle().with("This is so URI foo 2"));
    writeset.add(docId[2], new StringHandle().with("This is so URI foo 3"));
    docMgr.write(writeset);

    DocumentPage page = docMgr.readMetadata(docId[0], docId[1], docId[2]);
    // Issue #294 DocumentPage.size() should return correct size
    assertTrue(page.size() == 3);

    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      rec.getMetadata(mhRead);
      validateMetadata(mhRead);
    }
    validateMetadata(mhRead);
    mhRead = null;
  }

  /*
   * This test verifies that collections do change when new meta data is used in
   * a bulk write set. Verified by reading individual documents
   */
  @Test
  public void testWriteMultipleTextDocWithChangedMetadataCollections() {
    String docId[] = { "/foo/test/myFoo4.txt", "/foo/test/myFoo5.txt", "/foo/test/myFoo6.txt" };
    TextDocumentManager docMgr = client.newTextDocumentManager();

    DocumentWriteSet writeset = docMgr.newWriteSet();
    // put meta-data
    DocumentMetadataHandle mh = setMetadata();

    writeset.addDefault(mh);
    writeset.add(docId[0], new StringHandle().with("This is so foo4"));
    writeset.add(docId[1], new StringHandle().with("This is so foo5"));
    writeset.add(docId[2], new StringHandle().with("This is so foo6"));
    docMgr.write(writeset);

    // Add new meta-data
    DocumentMetadataHandle mhUpdated = setUpdatedMetadataCollections();

    docMgr.writeMetadata(docId[0], mhUpdated);
    docMgr.writeMetadata(docId[1], mhUpdated);
    docMgr.writeMetadata(docId[2], mhUpdated);

    DocumentPage page = docMgr.read(docId);
    // Issue #294 DocumentPage.size() should return correct size
    assertTrue(page.size() == 3);

    DocumentMetadataHandle metadataHandleRead = new DocumentMetadataHandle();

    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      docMgr.readMetadata(rec.getUri(), metadataHandleRead);
      validateUpdatedMetadataCollections(metadataHandleRead);
    }
    validateUpdatedMetadataCollections(metadataHandleRead);
  }

  // Test reset of metadata collections in a transaction.
  @Test
  public void testDefaultMetadataOnDocManager() throws InterruptedException {
    System.out.println("Running testDefaultMetadataOnDocManager");
    Transaction t1 = null, tReset = null;
    try {
      String docId[] = {"/foo/test/myFoo1.txt", "/foo/test/myFoo2.txt", "/foo/test/myFoo3.txt",
      };
      TextDocumentManager docMgr = client.newTextDocumentManager();

      DocumentWriteSet writeset = docMgr.newWriteSet();
      // put meta-data
      DocumentMetadataHandle mh = new DocumentMetadataHandle();
      DocumentMetadataHandle mhRead = new DocumentMetadataHandle();

      mh.getCollections().addAll("groupCollections");
      DocumentMetadataHandle docSpecificMetadata =
              new DocumentMetadataHandle().withCollections("specificDocCollection");
      writeset.addDefault(mh);
      writeset.add(docId[0], docSpecificMetadata, new StringHandle().with("This is so foo1"));
      writeset.add(docId[1], new StringHandle().with("This is so foo2"));
      writeset.add(docId[2], new StringHandle().with("This is so foo3"));
      docMgr.write(writeset);

      StringHandle sh = docMgr.read(docId[0], new StringHandle());
      assertEquals( "This is so foo1", sh.get());
      docMgr.readMetadata(docId[0], mhRead);

      DocumentCollections collections = mhRead.getCollections();
      String actualCollections = getDocumentCollectionsString(collections);
      System.out.println(actualCollections);

      String expectedCollections1 = "size:1|specificDocCollection|";
      assertEquals( expectedCollections1, actualCollections);

      // Verify that docId[1] does not have specificDoccollection
      mhRead = new DocumentMetadataHandle();
      docMgr.readMetadata(docId[1], mhRead);
      collections = mhRead.getCollections();
      actualCollections = getDocumentCollectionsString(collections);
      System.out.println(actualCollections);

      String expectedCollections2 = "size:1|groupCollections|";
      assertEquals( expectedCollections2, actualCollections);
      // Reset metadata
      docMgr.writeDefaultMetadata(docId);
      Thread.sleep(5000);
      mhRead = new DocumentMetadataHandle();
      // Read meta data for docId[0] to verify that it reset.
      docMgr.readMetadata(docId[0], mhRead);
      collections = mhRead.getCollections();
      actualCollections = getDocumentCollectionsString(collections);
      System.out.println(actualCollections);
      String expectedCollections3 = "size:0|";
      assertEquals( expectedCollections3, actualCollections);

      // Call in a writeDefaultMetadata transaction
      String docIdTx[] = {"/foo/test/myFoo4.txt", "/foo/test/myFoo5.txt"};
      t1 = client.openTransaction();

      DocumentMetadataHandle docTransSpecificMetadata =
              new DocumentMetadataHandle().withCollections("TransSpecificDocCollection");
      TextDocumentManager docMgrTx = client.newTextDocumentManager();

      DocumentWriteSet writesetTx = docMgrTx.newWriteSet();
      writesetTx.add(docIdTx[0], docTransSpecificMetadata, new StringHandle().with("This is so foo4"));
      writesetTx.add(docIdTx[1], docTransSpecificMetadata, new StringHandle().with("This is so foo5"));
      docMgrTx.write(writesetTx, t1);
      t1.commit();
      Thread.sleep(2000);
      t1 = null;

      sh = docMgrTx.read(docIdTx[0], new StringHandle());
      assertEquals( "This is so foo4", sh.get());
      docMgrTx.readMetadata(docIdTx[0], mhRead);

      collections = mhRead.getCollections();
      actualCollections = getDocumentCollectionsString(collections);
      System.out.println(actualCollections);

      String expectedCollections4 = "size:1|TransSpecificDocCollection|";
      assertEquals( expectedCollections4, actualCollections);

      tReset = client.openTransaction();
      docMgrTx.writeDefaultMetadata(tReset, docIdTx);
      // Read again without committing

      docMgrTx.readMetadata(docIdTx[0], mhRead);

      collections = mhRead.getCollections();
      actualCollections = getDocumentCollectionsString(collections);
      System.out.println(actualCollections);

      expectedCollections4 = "size:1|TransSpecificDocCollection|";
      assertEquals( expectedCollections4, actualCollections);

      // Now commit transaction and verify if collections reset.
      tReset.commit();

      Thread.sleep(2000);
      tReset = null;
      mhRead = new DocumentMetadataHandle();
      // Read meta data for docId[1] to verify that it reset.
      docMgrTx.readMetadata(docIdTx[1], mhRead);
      collections = mhRead.getCollections();
      actualCollections = getDocumentCollectionsString(collections);
      System.out.println(actualCollections);
      String expectedCollections5 = "size:0|";
      assertEquals( expectedCollections5, actualCollections);

      // Negative case - Call with empty args
      StringBuilder negStr = new StringBuilder();
      try {
        docMgrTx.writeDefaultMetadata();
      }
      catch(Exception e) {
        System.out.println(e.getMessage());
        negStr.append(e.getMessage());
      }
      finally {
        assertTrue( negStr.toString().contains("Resetting document metadata with empty identifier list"));
      }
    }
    catch (Exception ex) {
      System.out.println("Exceptions thrown" + ex.getStackTrace());
      fail("Test failed due to exceptions");
    }
    finally {
        if (t1 != null) {
          t1.rollback();
        }
        if (tReset != null) {
          tReset.rollback();
        }
      }
    }
  }
