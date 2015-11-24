/*
 * Copyright 2012-2015 MarkLogic Corporation
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
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SemanticsPermissionsTest {
    private static GraphManager gmgr;
    private static String graphUri = "SemanticsPermissionsTest";
    private static DatabaseClient readPrivilegedClient;
    private static DatabaseClient writePrivilegedClient;

    @BeforeClass
    public static void beforeClass() {
        Common.connect();
        gmgr = Common.client.newGraphManager();
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "debug");
        readPrivilegedClient = DatabaseClientFactory.newClient(
                Common.HOST, Common.PORT, "read-privileged", "x", Authentication.DIGEST);
        writePrivilegedClient = DatabaseClientFactory.newClient(
                Common.HOST, Common.PORT, "write-privileged", "x", Authentication.DIGEST);
        String triple = "<s> <p> <o>.";
        GraphPermissions perms = gmgr.permission("read-privileged", Capability.READ)
            .permission("write-privileged", Capability.UPDATE);
        gmgr.write(graphUri, new StringHandle(triple).withMimetype(RDFMimeTypes.NTRIPLES), perms);
    }

    @AfterClass
    public static void afterClass() {
        GraphManager gmgr = Common.client.newGraphManager();
        gmgr.delete(graphUri);
        Common.release();
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
    public void B_testWritePermission() throws Exception {
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
    public void C_testGetPermissions() throws Exception {
        GraphManager readPrivilegedGmgr = readPrivilegedClient.newGraphManager();
        GraphPermissions permissions = readPrivilegedGmgr.getPermissions(graphUri);
        assertEquals(4, permissions.size());
        assertNotNull(permissions.get("read-privileged"));
        assertNotNull(permissions.get("write-privileged"));
        assertEquals(1, permissions.get("read-privileged").size());
        assertEquals(1, permissions.get("write-privileged").size());
        assertEquals(Capability.READ, permissions.get("read-privileged").iterator().next());
        assertEquals(Capability.UPDATE, permissions.get("write-privileged").iterator().next());
    }

    @Test
    public void D_testWritePermissions() throws Exception {
        GraphPermissions perms = gmgr.newGraphPermissions();
        perms = perms.permission("read-privileged", Capability.EXECUTE);
        gmgr.writePermissions(graphUri, perms);
        GraphPermissions permissions = gmgr.getPermissions(graphUri);
        assertEquals(3, permissions.size());
        assertNotNull(permissions.get("read-privileged"));
        assertEquals(1, permissions.get("read-privileged").size());
        for ( Capability capability : permissions.get("read-privileged") ) {
            assertEquals(Capability.EXECUTE, capability);
        }
    }

    @Test
    public void E_testMergePermissions() throws Exception {
        GraphPermissions perms = gmgr.permission("read-privileged", Capability.READ);
        gmgr.mergePermissions(graphUri, perms);
        GraphPermissions permissions = gmgr.getPermissions(graphUri);
        assertEquals(3, permissions.size());
        assertNotNull(permissions.get("read-privileged"));
        assertEquals(2, permissions.get("read-privileged").size());
        for ( Capability capability : permissions.get("read-privileged") ) {
            if ( capability == null ) fail("capability should not be null");
            if ( capability != Capability.READ && capability != Capability.EXECUTE ) {
                fail("capabilities should be read or execute, not [" + capability + "]");
            }
        }
    }

    @Test
    public void F_testDeletePermissions() throws Exception {
        gmgr.deletePermissions(graphUri);
        GraphPermissions permissions = gmgr.getPermissions(graphUri);
        assertEquals(2, permissions.size());
        assertNull(permissions.get("read-privileged"));
    }

    @Test
    public void G_testSPARQLInsertPermissions() throws Exception {
        String localGraphUri = graphUri + ".SPARQLPermissions";
        String sparql = "INSERT DATA { GRAPH <" + localGraphUri + "> { <s2> <p2> <o2> } }";
        SPARQLQueryManager sparqlMgr = Common.client.newSPARQLQueryManager();
        SPARQLQueryDefinition qdef = sparqlMgr.newQueryDefinition(sparql)
            .withUpdatePermission("write-privileged", Capability.READ)
            .withUpdatePermission("write-privileged", Capability.UPDATE);
        sparqlMgr.executeUpdate(qdef);
        GraphPermissions getPermissions = gmgr.getPermissions(localGraphUri);
        assertEquals(3, getPermissions.size());
        assertNotNull(getPermissions.get("write-privileged"));
        assertEquals(2, getPermissions.get("write-privileged").size());
        for ( Capability capability : getPermissions.get("write-privileged") ) {
            if ( capability == null ) fail("capability should not be null");
            if ( capability != Capability.READ && capability != Capability.UPDATE ) {
                fail("capabilities should be read or update, not [" + capability + "]");
            }
        }
        gmgr.delete(localGraphUri);
    }
}
