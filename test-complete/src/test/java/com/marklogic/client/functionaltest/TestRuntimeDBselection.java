/*
 * Copyright 2014-2017 MarkLogic Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.FailedRequestException;

public class TestRuntimeDBselection extends BasicJavaClientREST {
  private static String dbName = "TestRuntimeDB";
  private static String[] fNames = { "TestRuntimeDB-1" };
  private static String appServerHostname = null;

  private DatabaseClient client;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("In setup");
    configureRESTServer(dbName, fNames, false);
    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader");
    appServerHostname = getRestAppServerHostName();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("In tear down");
    cleanupRESTServer(dbName, fNames);
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
  }

  // Ignoring the test due to enhanced security and need for a authentication
  // digest.
  @Ignore
  public void testRuntimeDBclientWithDefaultUser() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    if (!IsSecurityEnabled()) {
      associateRESTServerWithDefaultUser(getRestServerName(), "eval-user", "application-level");
      int restPort = getRestServerPort();
      client = DatabaseClientFactory.newClient(appServerHostname, restPort, dbName);
      String insertJSON = "xdmp:document-insert(\"test2.json\",object-node {\"test\":\"hello\"})";
      client.newServerEval().xquery(insertJSON).eval();
      String query1 = "fn:count(fn:doc())";
      int response = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
      assertEquals("count of documents ", 1, response);
      String query2 = "declareUpdate();xdmp.documentDelete(\"test2.json\");";
      client.newServerEval().javascript(query2).eval();
      int response2 = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
      assertEquals("count of documents ", 0, response2);
      client.release();
      associateRESTServerWithDefaultUser(getRestServerName(), "nobody", "digest");
    }
  }

  // Issue 184 exists
  @Test
  public void testRuntimeDBclientWithDifferentAuthType() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    if (!IsSecurityEnabled()) {
      associateRESTServerWithDefaultUser(getRestServerName(), "nobody", "basic");
      int restPort = getRestServerPort();

      client = getDatabaseClientOnDatabase(appServerHostname, restPort, dbName, "eval-user", "x", Authentication.BASIC);
      String insertJSON = "xdmp:document-insert(\"test2.json\",object-node {\"test\":\"hello\"})";
      client.newServerEval().xquery(insertJSON).eval();
      String query1 = "fn:count(fn:doc())";
      int response = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
      assertEquals("count of documents ", 1, response);
      String query2 = "declareUpdate();xdmp.documentDelete(\"test2.json\");";
      client.newServerEval().javascript(query2).eval();
      int response2 = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
      assertEquals("count of documents ", 0, response2);
      client.release();
      associateRESTServerWithDefaultUser(getRestServerName(), "nobody", "digest");
    }
  }

  // issue 141 user with no privileges for eval
  /*
   * If ssl is enabled, then there throw an exception
   */
  @Test
  public void testRuntimeDBclientWithNoPrivUser() throws KeyManagementException, NoSuchAlgorithmException, Exception {
    client = getDatabaseClient("rest-admin", "x", Authentication.DIGEST);
    String insertJSON = "xdmp:document-insert(\"test2.json\",object-node {\"test\":\"hello\"})";
    try {
      client.newServerEval().xquery(insertJSON).eval();
      fail("Exception should have been thrown");
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(e instanceof com.marklogic.client.FailedRequestException);
      Assert.assertTrue(e.getMessage().contains("SEC-PRIV"));
    }
    client.release();
  }
}
