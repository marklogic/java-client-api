/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class SemanticsPermissionsTest {
  private static GraphManager gmgr;
  private static String graphUri = "SemanticsPermissionsTest";
  private static DatabaseClient readPrivilegedClient = Common.newClientBuilder().withUsername("read-privileged").build();
  private static DatabaseClient writePrivilegedClient = Common.newClientBuilder().withUsername("write-privileged").build();

  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    gmgr = Common.client.newGraphManager();
    String triple = "<s> <p> <o>.";
    GraphPermissions perms = gmgr.permission("java-test-read-privileged", Capability.READ)
      .permission("java-test-write-privileged", Capability.UPDATE);
    gmgr.write(graphUri, new StringHandle(triple).withMimetype(RDFMimeTypes.NTRIPLES), perms);
  }

  @AfterAll
  public static void afterClass() {
    GraphManager gmgr = Common.client.newGraphManager();
    gmgr.delete(graphUri);
    readPrivilegedClient.release();
    writePrivilegedClient.release();
  }

  @Test
  public void A_testReadPemission() throws Exception {
    // a negative test to ensure a user without read privilege can't read
    try {
      GraphManager writePrivilegedGmgr = writePrivilegedClient.newGraphManager();
      writePrivilegedGmgr.readAs(graphUri, String.class);
      fail("User write-privileged should see ForbiddenUserException when trying to read");
    } catch (ResourceNotFoundException e) {
      // passed negative test
    }
  }

  @Test
  public void B_testWritePermission() {
    // a negative test to ensure a user without update privilege can't write
    try {
      GraphManager readPrivilegedGmgr = readPrivilegedClient.newGraphManager();
      readPrivilegedGmgr.writeAs(graphUri, new StringHandle("").withMimetype(RDFMimeTypes.NTRIPLES));
      fail("User read-privileged should see ForbiddenUserException when trying to write");
    } catch (ForbiddenUserException e) {
      // passed negative test
    }
  }

  @Test
  public void C_testGetPermissions() {
    GraphManager readPrivilegedGmgr = readPrivilegedClient.newGraphManager();
    GraphPermissions permissions = readPrivilegedGmgr.getPermissions(graphUri);
    assertEquals(6, permissions.size());
    assertNotNull(permissions.get("java-test-read-privileged"));
    assertNotNull(permissions.get("java-test-write-privileged"));
    assertEquals(1, permissions.get("java-test-read-privileged").size());
    assertEquals(1, permissions.get("java-test-write-privileged").size());
    assertEquals(Capability.READ, permissions.get("java-test-read-privileged").iterator().next());
    assertEquals(Capability.UPDATE, permissions.get("java-test-write-privileged").iterator().next());
  }

  @Test
  public void D_testWritePermissions() {
    GraphPermissions perms = gmgr.newGraphPermissions();
    perms = perms.permission("java-test-read-privileged", Capability.EXECUTE);
    gmgr.writePermissions(graphUri, perms);
    GraphPermissions permissions = gmgr.getPermissions(graphUri);
    assertEquals(5, permissions.size());
    assertNotNull(permissions.get("java-test-read-privileged"));
    assertEquals(1, permissions.get("java-test-read-privileged").size());
    for ( Capability capability : permissions.get("java-test-read-privileged") ) {
      assertEquals(Capability.EXECUTE, capability);
    }
  }

  @Test
  public void E_testMergePermissions() {
    GraphPermissions perms = gmgr.permission("java-test-read-privileged", Capability.READ);
    gmgr.mergePermissions(graphUri, perms);
    GraphPermissions permissions = gmgr.getPermissions(graphUri);
    assertEquals(5, permissions.size());
    assertNotNull(permissions.get("java-test-read-privileged"));
    assertEquals(2, permissions.get("java-test-read-privileged").size());
    for ( Capability capability : permissions.get("java-test-read-privileged") ) {
      if ( capability == null ) fail("capability should not be null");
      if ( capability != Capability.READ && capability != Capability.EXECUTE ) {
        fail("capabilities should be read or execute, not [" + capability + "]");
      }
    }
  }

  @Test
  public void F_testDeletePermissions() {
    gmgr.deletePermissions(graphUri);
    GraphPermissions permissions = gmgr.getPermissions(graphUri);
    assertEquals(4, permissions.size());
    assertNull(permissions.get("java-test-read-privileged"));
  }

  @Test
  public void G_testSPARQLInsertPermissions() {
    String localGraphUri = graphUri + ".SPARQLPermissions";
    String sparql = "INSERT DATA { GRAPH <" + localGraphUri + "> { <s2> <p2> <o2> } }";
    SPARQLQueryManager sparqlMgr = Common.client.newSPARQLQueryManager();
    SPARQLQueryDefinition qdef = sparqlMgr.newQueryDefinition(sparql)
      .withUpdatePermission("java-test-write-privileged", Capability.READ)
      .withUpdatePermission("java-test-write-privileged", Capability.UPDATE);
    sparqlMgr.executeUpdate(qdef);
    GraphPermissions getPermissions = gmgr.getPermissions(localGraphUri);
    assertEquals(5, getPermissions.size());
    assertNotNull(getPermissions.get("java-test-write-privileged"));
    assertEquals(2, getPermissions.get("java-test-write-privileged").size());
    for ( Capability capability : getPermissions.get("java-test-write-privileged") ) {
      if ( capability == null ) fail("capability should not be null");
      if ( capability != Capability.READ && capability != Capability.UPDATE ) {
        fail("capabilities should be read or update, not [" + capability + "]");
      }
    }
    gmgr.delete(localGraphUri);
  }
}
