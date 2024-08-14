/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import com.marklogic.client.Transaction;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.*;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.SourceHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCtsQueryDefinition;
import org.junit.jupiter.api.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBulkWriteWithTransformations extends AbstractFunctionalTest {
  private static final int BATCH_SIZE = 100;
  private static final String DIRECTORY = "/bulkTransform/";

  private static String appServerHostname = null;

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    System.out.println("In setup");
    appServerHostname = getRestAppServerHostName();
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tear down");
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
  }

  @BeforeEach
  public void setUp() throws Exception {
    // create new connection for each test below
    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader");
	client = newClientAsUser("eval-user", "x");
  }

  @AfterEach
  public void tearDown() throws Exception {
    client.release();
  }

  @Test
  public void testBulkLoadWithXSLTClientSideTransform() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    String docId[] = { "/transform/emp.xml", "/transform/food1.xml", "/transform/food2.xml" };
    Source s[] = new Source[3];
    Scanner scanner=null, sc1 = null, sc2 = null;
    s[0] = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/employee.xml");
    s[1] = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/xml-original.xml");
    s[2] = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/xml-original-test.xml");
    // get the xslt
    Source xsl = new StreamSource("src/test/java/com/marklogic/client/functionaltest/data/employee-stylesheet.xsl");

    // create transformer
    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer(xsl);
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    DocumentWriteSet writeset = docMgr.newWriteSet();
    for (int i = 0; i < 3; i++) {
      SourceHandle handle = new SourceHandle();
      handle.set(s[i]);
      // set the transformer
      handle.setTransformer(transformer);
      writeset.add(docId[i], handle);
      // Close handle.
      handle.close();
    }
    docMgr.write(writeset);
    FileHandle dh = new FileHandle();

    try {
    docMgr.read(docId[0], dh);
    scanner = new Scanner(dh.get()).useDelimiter("\\Z");
    String readContent = scanner.next();
    assertTrue( readContent.contains("firstname"));
    docMgr.read(docId[1], dh);
    sc1 = new Scanner(dh.get()).useDelimiter("\\Z");
    readContent = sc1.next();
    assertTrue( readContent.contains("firstname"));
    docMgr.read(docId[2], dh);
    sc2 = new Scanner(dh.get()).useDelimiter("\\Z");
    readContent = sc2.next();
    assertTrue( readContent.contains("firstname"));
    }
    catch (Exception e) {
    	e.printStackTrace();
    }
    finally {
    	scanner.close();
    	sc1.close();
    	sc2.close();
    }

  }

  @Test
  public void testBulkLoadWithXQueryTransform() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    TransformExtensionsManager transMgr =
        client.newServerConfigManager().newTransformExtensionsManager();
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Adding attribute xquery Transform");
    metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");
    // get the transform file
    File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/add-attr-xquery-transform.xqy");
    FileHandle transformHandle = new FileHandle(transformFile);
    transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
    ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
    transform.put("name", "Lang");
    transform.put("value", "English");
    int count = 1;
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    Map<String, String> map = new HashMap<>();
    DocumentWriteSet writeset = docMgr.newWriteSet();
    for (int i = 0; i < 102; i++) {

      writeset.add(DIRECTORY + "foo" + i + ".xml", new DOMHandle(getDocumentContent("This is so foo" + i)));
      map.put(DIRECTORY + "foo" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is so foo" + i)));
      if (count % BATCH_SIZE == 0) {
        docMgr.write(writeset, transform);
        writeset = docMgr.newWriteSet();
      }
      count++;
    }
    if (count % BATCH_SIZE > 0) {
      docMgr.write(writeset, transform);
    }

    String uris[] = new String[102];
    for (int i = 0; i < 102; i++) {
      uris[i] = DIRECTORY + "foo" + i + ".xml";
    }
    count = 0;
    DocumentPage page = docMgr.read(uris);
    DOMHandle dh = new DOMHandle();
    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      rec.getContent(dh);
      assertTrue( dh.get().getElementsByTagName("foo").item(0).hasAttributes());
      count++;
    }
    assertEquals(102, count);
  }

  @Test
  public void testBulkSetReadTransform() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    String attribute = null;

    TransformExtensionsManager transMgr =
        client.newServerConfigManager().newTransformExtensionsManager();
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Adding attribute xquery Transform");
    metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");
    // get the transform file
    File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/add-attr-xquery-transform.xqy");
    FileHandle transformHandle = new FileHandle(transformFile);
    transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
    ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
    transform.put("name", "Land");
    transform.put("value", "USA");
    int count = 1;

    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    docMgr.setWriteTransform(transform);
    Map<String, String> map = new HashMap<>();
    DocumentWriteSet writeset = docMgr.newWriteSet();
    for (int i = 0; i < 10; i++) {
      writeset.add(DIRECTORY + "foo" + i + ".xml", new DOMHandle(getDocumentContent("This is so foo" + i)));
      map.put(DIRECTORY + "foo" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is so foo" + i)));
      if (count % BATCH_SIZE == 0) {
        docMgr.write(writeset);
        writeset = docMgr.newWriteSet();
      }
      count++;
    }
    if (count % BATCH_SIZE > 0) {
      docMgr.write(writeset);
    }

    String uris[] = new String[10];
    for (int i = 0; i < 10; i++) {
      uris[i] = DIRECTORY + "foo" + i + ".xml";
    }
    count = 0;

    XMLDocumentManager docMgrRd = client.newXMLDocumentManager();
    DocumentPage page = docMgrRd.read(uris);
    DOMHandle dh = new DOMHandle();
    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      rec.getContent(dh);
      assertTrue( dh.get().getElementsByTagName("foo").item(0).hasAttributes());
      count++;
    }

    assertEquals(10, count);
    DOMHandle readHandle = readDocumentUsingDOMHandle(client, uris[0], "XML");
    attribute = readHandle.get().getDocumentElement().getAttributes().getNamedItem("Land").toString();
    assertTrue( attribute.contains("Land=\"USA\""));

    // Do a read transform
    count = 0;
    ServerTransform transformRd = new ServerTransform("add-attr-xquery-transform");
    transformRd.put("name", "Place");
    transformRd.put("value", "England");

    Map<String, String> mapRd = new HashMap<>();
    DocumentWriteSet writesetRd = docMgrRd.newWriteSet();
    for (int i = 10; i < 20; i++) {
      writesetRd.add(DIRECTORY + "foo" + i + ".xml", new DOMHandle(getDocumentContent("This is so foo" + i)));
      mapRd.put(DIRECTORY + "foo" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is so foo" + i)));
      if (count % BATCH_SIZE == 0) {
        docMgrRd.write(writesetRd);
        writesetRd = docMgrRd.newWriteSet();
      }
      count++;
    }
    if (count % BATCH_SIZE > 0) {
      docMgrRd.write(writesetRd);
    }

    String urisRd[] = new String[10];
    for (int i = 0; i < 10; i++) {
      urisRd[i] = DIRECTORY + "foo" + (i + 10) + ".xml";
    }
    count = 0;
    docMgrRd.setReadTransform(transformRd);
    DocumentPage pageRd = docMgrRd.read(urisRd);
    DOMHandle dhRd = new DOMHandle();
    while (pageRd.hasNext()) {
      DocumentRecord recRd = pageRd.next();
      recRd.getContent(dhRd);
      assertTrue( dhRd.get().getElementsByTagName("foo").item(0).hasAttributes());
      attribute = dhRd.get().getDocumentElement().getAttributes().getNamedItem("Place").toString();
      assertTrue( attribute.contains("Place=\"England\""));
      count++;
    }

    assertEquals(10, count);

    // Test for multiple Read transforms on same URI.
    ServerTransform transformRd1 = new ServerTransform("add-attr-xquery-transform");
    transformRd1.put("name", "Country");
    transformRd1.put("value", "GB");

    XMLDocumentManager docMgrRd1 = client.newXMLDocumentManager();
    docMgrRd1.setReadTransform(transformRd);
    docMgrRd1.setReadTransform(transformRd1);
    DocumentPage pageRd1 = docMgrRd1.read(urisRd[0]);
    DOMHandle dhRd1 = new DOMHandle();
    DocumentRecord recRd1 = pageRd1.next();
    recRd1.getContent(dhRd1);
    attribute = dhRd1.get().getDocumentElement().getAttributes().getNamedItem("Country").toString();

    assertTrue( attribute.contains("Country=\"GB\""));

    // Test for Read and Write transforms on same URI.
    docMgrRd1.setReadTransform(null);
    DocumentWriteSet writesetMul = docMgrRd1.newWriteSet();
    docMgrRd1.setWriteTransform(transform);
    docMgrRd1.setReadTransform(transformRd1);
    writesetMul.add(DIRECTORY + "fooMultiple.xml", new DOMHandle(getDocumentContent("This is so foo Multiple")));
    docMgrRd1.write(writesetMul);

    DocumentPage pageMul = docMgrRd1.read(DIRECTORY + "fooMultiple.xml");
    DOMHandle dhRdMul = new DOMHandle();
    DocumentRecord recRdMul = pageMul.next();
    recRdMul.getContent(dhRdMul);
    attribute = dhRdMul.get().getDocumentElement().getAttributes().getNamedItem("Country").toString();
    assertTrue( attribute.contains("Country=\"GB\""));
    attribute = dhRdMul.get().getDocumentElement().getAttributes().getNamedItem("Land").toString();
    assertTrue( attribute.contains("Land=\"USA\""));

    // Negative test - Use null Transforms
    docMgrRd1.setReadTransform(null);
    docMgrRd1.setWriteTransform(null);
    DocumentWriteSet writeNull = docMgrRd1.newWriteSet();
    writeNull.add(DIRECTORY + "fooNullTransform.xml", new DOMHandle(getDocumentContent("This is so foo Multiple")));
    docMgrRd1.write(writeNull);

    DocumentPage pageNull = docMgrRd1.read(DIRECTORY + "fooNullTransform.xml");
    DOMHandle dhRdNull = new DOMHandle();
    DocumentRecord recRdNull = pageNull.next();
    recRdNull.getContent(dhRdNull);
    String content = dhRdNull.toString();

    assertTrue( content.contains("<foo>This is so foo Multiple</foo>"));

    // Git Issue 639 - Case 1: Test using documentManager.setReadTransform and passing in a SearchReadHandle
    // to verify it gets transformed .

    DocumentWriteSet writesetSearch = docMgrRd.newWriteSet();
    writesetSearch.add(DIRECTORY + "MarkLogic9.0" + ".xml", new DOMHandle(getDocumentContent("This is the best NoSQL product")));
    docMgrRd.write(writesetSearch);

    ServerTransform transformSrch = new ServerTransform("add-attr-xquery-transform");
    transformSrch.put("name", "Domicile");
    transformSrch.put("value", "USA");

    XMLDocumentManager docMgrSrch = client.newXMLDocumentManager();
    docMgrSrch.setReadTransform(transformSrch);
    QueryManager queryMgr = client.newQueryManager();
    // Search for word NoSQL and then verify if transform runs with search
    String wordQuery = "<cts:word-query xmlns:cts=\"http://marklogic.com/cts\">" +
            "<cts:text>NoSQL</cts:text></cts:word-query>";
    StringHandle handle = new StringHandle().with(wordQuery);
    RawCtsQueryDefinition querydef = queryMgr.newRawCtsQueryDefinition(handle);

    DocumentPage pageSrch = docMgrSrch.search(querydef, 1);

    DOMHandle dhSrch = new DOMHandle();
    DocumentRecord recSrch = pageSrch.next();
    recSrch.getContent(dhSrch);
    attribute = dhSrch.get().getDocumentElement().getAttributes().getNamedItem("Domicile").toString();

    assertTrue( attribute.contains("Domicile=\"USA\""));
    assertTrue( recSrch.getUri().trim().contains("/bulkTransform/MarkLogic9.0.xml"));

    // Test search() with SearchReadHandle
    // create result handle
    DOMHandle resultsDOMHandle = new DOMHandle();
    DocumentPage pageSrch1 = docMgrSrch.search(querydef, 1, resultsDOMHandle);

    //DOMHandle dhSrchHandle = new DOMHandle();
    DocumentRecord recSrchHandle = pageSrch1.next();
    recSrchHandle.getContent(resultsDOMHandle);
    attribute = resultsDOMHandle.get().getDocumentElement().getAttributes().getNamedItem("Domicile").toString();

    assertTrue( attribute.contains("Domicile=\"USA\""));
    assertTrue( recSrch.getUri().trim().contains("/bulkTransform/MarkLogic9.0.xml"));

    // Test with documentManager.setReadTransform and queryDefinition.setResponseTransform set to different transforms.
    // Case 1 : Transform applies to same location
    RawCtsQueryDefinition qdefWithTransform = queryMgr.newRawCtsQueryDefinition(handle);
    ServerTransform transformOnQdef = new ServerTransform("add-attr-xquery-transform");
    transformOnQdef.put("name", "Continent");
    transformOnQdef.put("value", "North America");

    qdefWithTransform.setResponseTransform(transformOnQdef);
    DocumentPage pageSrch2 = docMgrSrch.search(qdefWithTransform, 1);

    DOMHandle dhSrch2 = new DOMHandle();
    DocumentRecord recSrch2 = pageSrch2.next();
    recSrch2.getContent(dhSrch2);
    System.out.println("DOMHandle multiple Transforms " + dhSrch2.toString());
    attribute = dhSrch2.get().getDocumentElement().getAttributes().getNamedItem("Continent").toString();

    assertTrue( attribute.contains("Continent=\"North America\""));
    assertTrue( recSrch.getUri().trim().contains("/bulkTransform/MarkLogic9.0.xml"));

    // Case 2 : Apply different transforms. QueryDef has add element transformation
    // throws java.lang.IllegalStateException
    String strExptdMsg = "QueryDefinition transform and DocumentManager transform have different names (add-element-xquery-transform, add-attr-xquery-transform)";
    String actualMsg = null;
    try {
        TransformExtensionsManager transMgr2 = client.newServerConfigManager().newTransformExtensionsManager();
        ExtensionMetadata metadata2 = new ExtensionMetadata();
        metadata2.setTitle("Adding new element xquery Transform");
        metadata2.setDescription("This plugin transforms an XML document by adding new element to root node");
        metadata2.setProvider("MarkLogic");
        metadata2.setVersion("0.1");
        // get the transform file
        File transformFile2 = new File(
                "src/test/java/com/marklogic/client/functionaltest/transforms/add-element-xquery-transform.xqy");
        FileHandle transformHandle2 = new FileHandle(transformFile2);
        transMgr2.writeXQueryTransform("add-element-xquery-transform", transformHandle2, metadata2);
        ServerTransform transform2 = new ServerTransform("add-element-xquery-transform");
        transform2.put("name", "Planet");
        transform2.put("value", "Earth");

        RawCtsQueryDefinition qdefWithTransform2 = queryMgr.newRawCtsQueryDefinition(handle);
        qdefWithTransform2.setResponseTransform(transform2);
        DocumentPage pageAddElement = docMgrSrch.search(qdefWithTransform2, 1);

        DOMHandle domAddElement = new DOMHandle();
        DocumentRecord recAddElement = pageAddElement.next();
        recAddElement.getContent(domAddElement);
        System.out.println("DOMHandle multiple Transforms " + domAddElement.toString());
        attribute = dhSrch2.get().getDocumentElement().getAttributes().getNamedItem("Continent").toString();
    }
    catch(Exception ex /* throws java.lang.IllegalStateException */) {
        actualMsg = ex.getMessage();
    }
    assertTrue( actualMsg.contains(strExptdMsg));
  }

  /*
   * This test is similar to testBulkLoadWithXQueryTransform and is used to
   * validate Git Issue 396.
   *
   * Verify that a ServerTransform object is passed along when in transactions.
   */

  @Test
  public void testBulkXQYTransformWithTrans() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    TransformExtensionsManager transMgr =
        client.newServerConfigManager().newTransformExtensionsManager();
    Transaction tRollback = client.openTransaction();
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Adding attribute xquery Transform");
    metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");
    // get the transform file
    File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/add-attr-xquery-transform.xqy");
    FileHandle transformHandle = new FileHandle(transformFile);
    transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
    ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
    transform.put("name", "Lang");
    transform.put("value", "testBulkXQYTransformWithTrans");
    int count = 1;
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    Map<String, String> map = new HashMap<>();
    DocumentWriteSet writesetRollback = docMgr.newWriteSet();
    // Verify rollback with a smaller number of documents.
    for (int i = 0; i < 12; i++) {
      writesetRollback.add(DIRECTORY + "fooWithTrans" + i + ".xml", new DOMHandle(getDocumentContent("This is so foo" + i)));
      map.put(DIRECTORY + "fooWithTrans" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is so foo" + i)));
      if (count % 10 == 0) {
        docMgr.write(writesetRollback, transform, tRollback);
        writesetRollback = docMgr.newWriteSet();
      }
      count++;
    }
    if (count % 10 > 0) {
      docMgr.write(writesetRollback, transform, tRollback);
    }
    String uris[] = new String[102];
    for (int i = 0; i < 102; i++) {
      uris[i] = DIRECTORY + "fooWithTrans" + i + ".xml";
    }

    try {
        // Verify rollback on DocumentManager write method with transform.
        tRollback.rollback();
        DocumentPage pageRollback = docMgr.read(uris);
        assertEquals(pageRollback.size(), 0, "Document count is not zero. Transaction did not rollback");

        // Perform write with a commit.
        Transaction tCommit = client.openTransaction();
        DocumentWriteSet writeset = docMgr.newWriteSet();
        for (int i = 0; i < 102; i++) {
            writeset.add(DIRECTORY + "fooWithTrans" + i + ".xml", new DOMHandle(getDocumentContent("This is so foo" + i)));
            map.put(DIRECTORY + "fooWithTrans" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is so foo" + i)));
            if (count % BATCH_SIZE == 0) {
                docMgr.write(writeset, transform, tCommit);
                writeset = docMgr.newWriteSet();
            }
            count++;
        }
        if (count % BATCH_SIZE > 0) {
            docMgr.write(writeset, transform, tCommit);
        }
        tCommit.commit();
        count = 0;
        DocumentPage page = docMgr.read(uris);
        DOMHandle dh = new DOMHandle();
        // To verify that transformation did run on all docs.
        String verifyAttrValue = null;
        while (page.hasNext()) {
            DocumentRecord rec = page.next();
            rec.getContent(dh);
            assertTrue( dh.get().getElementsByTagName("foo").item(0).hasAttributes());
            verifyAttrValue = dh.get().getElementsByTagName("foo").item(0).getAttributes().getNamedItem("Lang").getNodeValue();
            assertTrue( verifyAttrValue.equalsIgnoreCase("testBulkXQYTransformWithTrans"));
            count++;
        }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw e;
    }
    assertEquals(102, count);
  }

//Refer to BT 52461. Having bulk write with transform (no meta-data on the transform itself) used to throw
 // SVC-FILSTAT: cts:tokenize("attachment; filename=&quot;/bulkTransform/foo0.xml&quot;", "http://marklogic.com/collation/")
 // -- File status error: GetFileAttributes
 @Test
 public void testBulkWriteNoMetadataWithXQueryTransform() throws KeyManagementException, NoSuchAlgorithmException, Exception {

   TransformExtensionsManager transMgr =
       client.newServerConfigManager().newTransformExtensionsManager();
   // get the transform file
   File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/add-attr-xquery-transform.xqy");
   FileHandle transformHandle = new FileHandle(transformFile);
   transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle);
   ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
   transform.put("name", "Lang");
   transform.put("value", "English");
   int count = 1;
   XMLDocumentManager docMgr = client.newXMLDocumentManager();
   Map<String, String> map = new HashMap<>();
   DocumentWriteSet writeset = docMgr.newWriteSet();
   for (int i = 0; i < 102; i++) {

     writeset.add(DIRECTORY + "foo" + i + ".xml", new DOMHandle(getDocumentContent("This is so foo" + i)));
     map.put(DIRECTORY + "foo" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is so foo" + i)));
     if (count % BATCH_SIZE == 0) {
       docMgr.write(writeset, transform);
       writeset = docMgr.newWriteSet();
     }
     count++;
   }
   if (count % BATCH_SIZE > 0) {
     docMgr.write(writeset, transform);
   }

   String uris[] = new String[102];
   for (int i = 0; i < 102; i++) {
     uris[i] = DIRECTORY + "foo" + i + ".xml";
   }
   count = 0;
   DocumentPage page = docMgr.read(uris);
   DOMHandle dh = new DOMHandle();
   while (page.hasNext()) {
     DocumentRecord rec = page.next();
     rec.getContent(dh);
     assertTrue( dh.get().getElementsByTagName("foo").item(0).hasAttributes());
     count++;
   }
   assertEquals(102, count);
 }

  @Test
  public void testBulkReadWithXQueryTransform() throws KeyManagementException, NoSuchAlgorithmException, Exception {

    TransformExtensionsManager transMgr =
        client.newServerConfigManager().newTransformExtensionsManager();
    ExtensionMetadata metadata = new ExtensionMetadata();
    metadata.setTitle("Adding attribute xquery Transform");
    metadata.setDescription("This plugin transforms an XML document by adding attribute to root node");
    metadata.setProvider("MarkLogic");
    metadata.setVersion("0.1");
    // get the transform file
    File transformFile = new File("src/test/java/com/marklogic/client/functionaltest/transforms/add-attr-xquery-transform.xqy");
    FileHandle transformHandle = new FileHandle(transformFile);
    transMgr.writeXQueryTransform("add-attr-xquery-transform", transformHandle, metadata);
    ServerTransform transform = new ServerTransform("add-attr-xquery-transform");
    transform.put("name", "Lang");
    transform.put("value", "English");
    int count = 1;
    XMLDocumentManager docMgr = client.newXMLDocumentManager();
    Map<String, String> map = new HashMap<>();
    DocumentWriteSet writeset = docMgr.newWriteSet();
    for (int i = 0; i < 102; i++) {
      writeset.add(DIRECTORY + "sec" + i + ".xml", new DOMHandle(getDocumentContent("This is to read" + i)));
      map.put(DIRECTORY + "sec" + i + ".xml", convertXMLDocumentToString(getDocumentContent("This is to read" + i)));
      if (count % BATCH_SIZE == 0) {
        docMgr.write(writeset);
        writeset = docMgr.newWriteSet();
      }
      count++;
    }
    if (count % BATCH_SIZE > 0) {
      docMgr.write(writeset);
    }

    String uris[] = new String[102];
    for (int i = 0; i < 102; i++) {
      uris[i] = DIRECTORY + "sec" + i + ".xml";
    }
    count = 0;
    DocumentPage page = docMgr.read(transform, uris);
    DOMHandle dh = new DOMHandle();
    while (page.hasNext()) {
      DocumentRecord rec = page.next();
      rec.getContent(dh);
      assertTrue( dh.get().getElementsByTagName("foo").item(0).hasAttributes());
      count++;
    }
    assertEquals(102, count);
  }
}
