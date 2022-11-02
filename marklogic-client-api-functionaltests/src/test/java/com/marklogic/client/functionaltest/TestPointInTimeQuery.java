/*
 * Copyright (c) 2019 MarkLogic Corporation
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

package com.marklogic.client.functionaltest;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.PathLanguage;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPointInTimeQuery extends BasicJavaClientREST {
  private static String dbName = "TestPointInTimeQueryDB";
  private static String[] fNames = { "TestPointInTimeQuery-1" };
  private static int adminPort = 0;
  private static String appServerHostname = null;

  @BeforeClass
  public static void setUp() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames);
    setupAppServicesConstraint(dbName);

    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader");
    // set to - 60 seconds
    setDatabaseProperties(dbName, "merge-timestamp", "-600000000");
    appServerHostname = getRestAppServerHostName();
    adminPort = getAdminPort();
  }

  @After
  public void testCleanUp() throws Exception {
    clearDB();
    System.out.println("Running clear script");
  }

  /*
   * This test verifies if fragments are available for a document that is
   * inserted and then updated when merge time is set to 60 seconds and both
   * insert and update are within that time period.
   * 
   * Git Issue 457 needs to be completed in order for this test to be fleshed
   * completly.
   * 
   * Insert doc Verify fragment counts Update the document Verify read with
   * Point In Time Stamp Verify fragment counts. Update again. Verify read with
   * Point In Time Stamp again Verify fragment counts second time
   */
  @Test
  public void testAInsertAndUpdateJson() throws KeyManagementException, NoSuchAlgorithmException, IOException
  {
    System.out.println("Running testAInsertAndUpdateJson");

    String[] filenames = { "json-original.json" };

    DatabaseClient client = getDatabaseClient("eval-user", "x", getConnType());

    // write docs and save the timestamps in the array

    for (String filename : filenames) {
      writeDocumentUsingInputStreamHandle(client, filename, "/partial-update/", "JSON");
    }
    DocumentManager docMgrIns = client.newJSONDocumentManager();
    // create handle
    JacksonHandle jacksonHandle = new JacksonHandle();

    // Read the document with timestamp.
    docMgrIns.read("/partial-update/" + filenames[0], jacksonHandle);

    // Make sure we have the original document and document count is 1

    long insTimeStamp = jacksonHandle.getServerTimestamp();
    System.out.println("Point in Time Stamp after the initial insert " + insTimeStamp);
    DatabaseCounts dbCounts = getDBFragmentCount();
    System.out.println("Fragment counts after initial insert: " + dbCounts);
    assertEquals("Number of active Fragments after initial insert is incorrect", 1, dbCounts.activeFragments);
    assertEquals("Number of deleted Fragments after initial insert is incorrect", 0, dbCounts.deletedFragments);

    // Update the doc. Insert a fragment.
    ObjectMapper mapper = new ObjectMapper();

    String docId = "/partial-update/json-original.json";
    JSONDocumentManager docMgr = client.newJSONDocumentManager();
    DocumentPatchBuilder patchBldr = docMgr.newPatchBuilder();
    patchBldr.pathLanguage(PathLanguage.JSONPATH);

    ObjectNode fragmentNode = mapper.createObjectNode();
    ObjectNode fragmentNode1 = mapper.createObjectNode();
    ObjectNode fragmentNode2 = mapper.createObjectNode();

    fragmentNode.put("insertedKey", 9);
    fragmentNode1.put("original", true);
    fragmentNode2.put("modified", false);

    String fragment = mapper.writeValueAsString(fragmentNode);
    String fragment1 = mapper.writeValueAsString(fragmentNode1);
    String fragment2 = mapper.writeValueAsString(fragmentNode2);

    String jsonpath = new String("$.employees[2]");
    patchBldr.insertFragment(jsonpath, Position.AFTER, fragment);
    patchBldr.insertFragment("$.employees[2]", Position.AFTER, fragment1);

    DocumentPatchHandle patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);

    // Now read using a handle and time value in insTimeStamp. The updates
    // should not be present.
    StringHandle shReadInsTS = new StringHandle();
    shReadInsTS.setPointInTimeQueryTimestamp(insTimeStamp);
    String insTS = docMgr.read(docId, shReadInsTS).get();
    System.out.println(insTS);
    assertFalse("fragment is inserted", insTS.contains("{\"insertedKey\":9}"));
    assertFalse("Original fragment is inserted or incorrect", insTS.contains("{\"original\":true}"));

    // Now read using a handle without a Point In Time value set on the handle.
    // Should return the latest document.
    String content = docMgr.read(docId, new StringHandle()).get();

    System.out.println(content);

    assertTrue("fragment is not inserted", content.contains("{\"insertedKey\":9}"));
    assertTrue("Original fragment is not inserted or incorrect", content.contains("{\"original\":true}"));

    // Read the document with timestamp. Todo QUERY DB for document at a Point
    // In Time. Git Issue 457
    // Verify that the test can validate the first original docs only within the
    // -60 (time interval)

    // Sleep for some time. Read again with new point In time.
    // Verify that we get updated document.

    docMgrIns.read("/partial-update/" + filenames[0], jacksonHandle);

    long firstUpdTimeStamp = jacksonHandle.getPointInTimeQueryTimestamp();
    System.out.println("Point in Time Stamp after the first update " + firstUpdTimeStamp);

    // Verify the counts
    dbCounts = getDBFragmentCount();
    System.out.println("Fragment counts after first update: " + dbCounts);
    assertEquals("Number of active Fragments after initial insert is incorrect", 1, dbCounts.activeFragments);
    assertEquals("Number of deleted Fragments after initial insert is incorrect", 1, dbCounts.deletedFragments);

    // Insert / update the document again

    patchBldr.insertFragment("$.employees[0]", Position.AFTER, fragment2);

    patchHandle = patchBldr.build();
    docMgr.patch(docId, patchHandle);

    content = docMgr.read(docId, new StringHandle()).get();
    System.out.println(content);
    assertTrue("Modified fragment is not inserted or incorrect", content.contains("{\"modified\":false}"));

    // Read the document with timestamp. Todo QUERY DB for document at a Point
    // In Time. Git Issue 457
    // Verify that the test can validate the first original docs only within the
    // -60 (time interval)

    // Sleep for some time. Read again with new point In time.
    // Verify that we get updated document.

    docMgrIns.read("/partial-update/" + filenames[0], jacksonHandle);

    long secondUpdTimeStamp = jacksonHandle.getPointInTimeQueryTimestamp();
    System.out.println("Point in Time Stamp after the first update " + secondUpdTimeStamp);

    dbCounts = getDBFragmentCount();
    System.out.println("Fragment counts after second update: " + dbCounts);
    assertEquals("Number of active Fragments after initial insert is incorrect", 1, dbCounts.activeFragments);
    assertEquals("Number of deleted Fragments after initial insert is incorrect", 2, dbCounts.deletedFragments);

    client.release();
  }

  /*
   * This test verifies if fragments are available for a document that is
   * inserted and then deleted when merge time is set to 60 seconds and both
   * insert and delete are within that time period.
   * 
   * Git Issue 457 needs to be completed in order for this test to be fleshed
   * completly.
   * 
   * Insert doc Read the doc and Verify fragment counts Delete the document
   * Trying reading the doc without timestamp. Verify fragment counts Verify
   * read with Point In Time Stamp now. Should be available to read original doc
   * within timestamp period. Insert same doc again. Verify read with Point In
   * Time Stamp of deletion time. Should not be able ot get document. Verify
   * fragment counts second time.
   * 
   * Read again without timestamp. Should be able to read the document.
   */
  @Test
  public void testBInsertAndDeleteJson() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testBInsertAndDeleteJson");
    // TODO THE TEST.
    /*
     * From Pubs using XQuery 1) First, create a document:
     * xdmp:document-insert("/docs/test.xml", <a>hello</a>)) 2) When you query
     * the document, it returns the node you inserted: doc("/docs/test.xml") (:
     * returns the node <a>hello</a> :) 3) Delete the document:
     * xdmp:document-delete("/docs/test.xml") 4) Query the document again. It
     * returns the empty sequence because it was just deleted. 5) Run a
     * point-in-time query, specifying the current timestamp.
     * xdmp:eval("doc('/docs/test.xml')", (), <options xmlns="xdmp:eval">
     * <timestamp>{xdmp:request-timestamp()}</timestamp> </options>) (: returns
     * the empty sequence because the document has been deleted :) 6) Run the
     * point-in-time query at one less than the current timestamp, which is the
     * old timestamp in this case because only one change has happened to the
     * database. xdmp:eval("doc('/docs/test.xml')", (), <options
     * xmlns="xdmp:eval"> <timestamp>{xdmp:request-timestamp()-1}</timestamp>
     * </options>) (: returns the deleted version of the document :)
     */
  }

  /*
   * This test verifies if fragments are available for a document that is
   * inserted and then deleted when merge time is set to 60 seconds and both
   * insert and delete are within that time period.
   * 
   * 1) With Transaction commit and rollback. Verify within a transaction an
   * update and then do a rollback the steps below. 2) With Transaction commit
   * and rollback. Verify within a transaction an update and then do a commoi
   * the steps below.
   * 
   * Git Issue 457 needs to be completed in order for this test to be fleshed
   * completly.
   * 
   * Insert doc Read the doc and Verify fragment counts Delete the document
   * Trying reading the doc without timestamp. Verify fragment counts Verify
   * read with Point In Time Stamp now. Should be available to read original doc
   * within timestamp period. Insert same doc again. Verify read with Point In
   * Time Stamp of deletion time. Should not be able ot get document. Verify
   * fragment counts second time.
   * 
   * Read again without timestamp. Should be able to read the document.
   */
  @Test
  public void testCWithTransaction() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testCWithTransaction");
    // TODO THE TEST.
  }

  /*
   * This test verifies if fragments are available for a document that is
   * inserted and then deleted when merge time is set to 60 seconds and both
   * insert and delete are within that time period.
   * 
   * 1) With Server side Transformation.
   * 
   * 
   * Git Issue 457 needs to be completed in order for this test to be fleshed
   * completly.
   * 
   * Insert doc Read the doc and Verify fragment counts Delete the document
   * Trying reading the doc without timestamp. Verify fragment counts Verify
   * read with Point In Time Stamp now. Should be available to read original doc
   * within timestamp period. Insert same doc again. Verify read with Point In
   * Time Stamp of deletion time. Should not be able ot get document. Verify
   * fragment counts second time.
   * 
   * Read again without timestamp. Should be able to read the document.
   */
  @Test
  public void testDWithTransformation() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testDWithTransformation");
    // TODO THE TEST.
  }

  /*
   * This test verifies if fragments are available for a document that is
   * inserted and then deleted when merge time is set to 60 seconds and both
   * insert and delete are within that time period.
   * 
   * 1) Verify results on DocumentPage.search and other methods that take a
   * PointInTime parameter.
   * 
   * 
   * Git Issue 457 needs to be completed in order for this test to be fleshed
   * completly.
   * 
   * Insert doc Read the doc and Verify fragment counts Delete the document
   * Trying reading the doc without timestamp. Verify fragment counts Verify
   * read with Point In Time Stamp now. Should be available to read original doc
   * within timestamp period. Insert same doc again. Verify read with Point In
   * Time Stamp of deletion time. Should not be able ot get document. Verify
   * fragment counts second time.
   * 
   * Read again without timestamp. Should be able to read the document.
   */
  @Test
  public void testEDocumentPage() throws KeyManagementException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, XpathException,
      TransformerException
  {
    System.out.println("Running testEDocumentPage");
    // TODO THE TEST.
  }

  static class DatabaseCounts {
    public int activeFragments;
    public int deletedFragments;

    @Override
    public String toString() {
      return "active=" + activeFragments + "; deleted=" + deletedFragments;
    }
  }

  public DatabaseCounts getDBFragmentCount() {
    DefaultHttpClient client = null;
    try {
      client = new DefaultHttpClient();
      client.getCredentialsProvider().setCredentials(
          new AuthScope(appServerHostname, adminPort),
          new UsernamePasswordCredentials("admin", "admin"));
      String url = "http://" + appServerHostname + ":" + adminPort + "/manage/v2/databases/" + dbName + "?view=counts&format=xml";
      HttpGet getrequest = new HttpGet(url);
      HttpResponse resp = client.execute(getrequest);
      Document doc = new SAXBuilder().build(resp.getEntity().getContent());
      Namespace ns = Namespace.getNamespace("db", "http://marklogic.com/manage/databases");
      Element counts = doc.getRootElement().getChild("count-properties", ns);
      DatabaseCounts dbCounts = new DatabaseCounts();
      dbCounts.activeFragments = Integer.parseInt(counts.getChildText("active-fragments", ns));
      dbCounts.deletedFragments = Integer.parseInt(counts.getChildText("deleted-fragments", ns));
      return dbCounts;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      client.getConnectionManager().shutdown();
    }
  }

  @AfterClass
  public static void tearDown() throws Exception
  {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
  }
}
