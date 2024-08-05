/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ExtensionLibraryDescriptor;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ExtensionLibrariesTest {

  @Test
  public void testXQueryModuleCRUD()
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {

    Common.connectRestAdmin();
    // get a manager
    ExtensionLibrariesManager libsMgr = Common.restAdminClient
      .newServerConfigManager().newExtensionLibrariesManager();

    // write XQuery file to the modules database
    libsMgr.write("/ext/my/path/to/my/module.xqy", new FileHandle(
      new File("src/test/resources/module.xqy")).withFormat(Format.TEXT));

    // read it back
    String xqueryModuleAsString = libsMgr.read(
      "/ext/my/path/to/my/module.xqy", new StringHandle()).get();

    assertTrue( xqueryModuleAsString.startsWith("xquery version \"1.0-ml\";"));

    // rewrite XQuery file to the modules database with permissions
    ExtensionLibraryDescriptor moduleDescriptor = new ExtensionLibraryDescriptor();
    moduleDescriptor.setPath("/ext/my/path/to/my/module.xqy");
    moduleDescriptor.addPermission("manage-user",  "execute");

    libsMgr.write(moduleDescriptor, new FileHandle(
      new File("src/test/resources/module.xqy")).withFormat(Format.TEXT));

    // get the list of descriptors
    ExtensionLibraryDescriptor[] descriptors = libsMgr.list("/ext/my/path/to/my/");
    assertEquals( 1, descriptors.length);

    for (ExtensionLibraryDescriptor descriptor : descriptors) {
      assertEquals(descriptor.getPath(), "/ext/my/path/to/my/module.xqy");
    }

    // delete it
    libsMgr.delete("/ext/my/path/to/my/module.xqy");

    try {
      // read deleted module
      xqueryModuleAsString = libsMgr.read(
        "/ext/my/path/to/my/module.xqy", new StringHandle()).get();
    } catch (ResourceNotFoundException e) {
      // pass;
    }

    descriptors = libsMgr.list("/ext/my/path/to/my/");
    assertEquals( 0, descriptors.length);

  }

  @Test
  public void testXQueryModuleCRUDXmlFileNegative() {
    Common.connectRestAdmin();

    // get a manager
    ExtensionLibrariesManager libsMgr = Common.restAdminClient.newServerConfigManager().newExtensionLibrariesManager();

    String libraryPath = "/foo/my/path/to/my/module.xqy";
    FileHandle f = new FileHandle(new File("test-complete/src/test/java/com/marklogic/client/functionaltest/data/all_well.xml")).withFormat(Format.XML);

    // write XQuery file to the modules database
    try{
      libsMgr.write(libraryPath, f);
      fail("Call to write with an invalid path should have failed");
    }catch(Exception e){
      assertEquals("libraryPath (the modules database path under which you install an asset) must begin with /ext/", e.getMessage());
    }

    // read it
    try{
      libsMgr.read(libraryPath, new StringHandle());
      fail("Call to read with an invalid path should have failed");
    }catch(Exception e){
      assertEquals("libraryPath (the modules database path under which you install an asset) must begin with /ext/", e.getMessage());
    }

    // delete it
    try{
      libsMgr.delete(libraryPath);
      fail("Call to delete with an invalid path should have failed");
    }catch(Exception e){
      assertEquals("libraryPath (the modules database path under which you install an asset) must begin with /ext/", e.getMessage());
    }
  }
}
