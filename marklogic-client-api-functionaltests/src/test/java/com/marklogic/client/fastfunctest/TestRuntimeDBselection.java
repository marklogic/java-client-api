/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.fastfunctest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestRuntimeDBselection extends AbstractFunctionalTest {

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {
    createUserRolesWithPrevilages("test-eval", "xdbc:eval", "xdbc:eval-in", "xdmp:eval-in", "any-uri", "xdbc:invoke");
    createRESTUser("eval-user", "x", "test-eval", "rest-admin", "rest-writer", "rest-reader");
  }

  @AfterAll
  public static void tearDownAfterClass() throws Exception {
    deleteRESTUser("eval-user");
    deleteUserRole("test-eval");
  }

  // Issue 184 exists
  @Test
  public void testRuntimeDBclientWithDifferentAuthType() {
    if (!IsSecurityEnabled()) {
		String originalServerAuthentication = getServerAuthentication(getRestServerName());
      try {
		  setAuthentication("basic", getRestServerName());
		client = newDatabaseClientBuilder().withBasicAuth("eval-user", "x").build();
        String insertJSON = "xdmp:document-insert(\"test2.json\",object-node {\"test\":\"hello\"})";
        client.newServerEval().xquery(insertJSON).eval();
        String query1 = "fn:count(fn:doc())";
        int response = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
        assertEquals( 1, response);
        String query2 = "declareUpdate();xdmp.documentDelete(\"test2.json\");";
        client.newServerEval().javascript(query2).eval();
        int response2 = client.newServerEval().xquery(query1).eval().next().getNumber().intValue();
        assertEquals( 0, response2);
        client.release();
      } finally {
		  setAuthentication(originalServerAuthentication, getRestServerName());
      }
    }
  }

  // issue 141 user with no privileges for eval
  /*
   * If ssl is enabled, then there throw an exception
   */
  @Test
  public void testRuntimeDBclientWithNoPrivUser() throws Exception {
    client = getDatabaseClient("rest-admin", "x", getConnType());
    String insertJSON = "xdmp:document-insert(\"test2.json\",object-node {\"test\":\"hello\"})";
    try {
      client.newServerEval().xquery(insertJSON).eval();
      fail("Exception should have been thrown");
    } catch (Exception e) {
      e.printStackTrace();
      assertTrue(e instanceof com.marklogic.client.FailedRequestException);
    }
      finally {
          client.release();
      }
  }
}
