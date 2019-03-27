/*
 * Copyright 2012-2019 MarkLogic Corporation
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ExtensionLibraryDescriptor;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

public class ExtensionLibrariesTest {

  @Test
  public void testXQueryModuleCRUD()
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException, FailedRequestException {

    Common.connectAdmin();
    // get a manager
    ExtensionLibrariesManager libsMgr = Common.adminClient
      .newServerConfigManager().newExtensionLibrariesManager();

    // write XQuery file to the modules database
    libsMgr.write("/ext/my/path/to/my/module.xqy", new FileHandle(
      new File("src/test/resources/module.xqy")).withFormat(Format.TEXT));

    // read it back
    String xqueryModuleAsString = libsMgr.read(
      "/ext/my/path/to/my/module.xqy", new StringHandle()).get();

    assertTrue("module read and read back", xqueryModuleAsString.startsWith("xquery version \"1.0-ml\";"));

    // rewrite XQuery file to the modules database with permissions
    ExtensionLibraryDescriptor moduleDescriptor = new ExtensionLibraryDescriptor();
    moduleDescriptor.setPath("/ext/my/path/to/my/module.xqy");
    moduleDescriptor.addPermission("manage-user",  "execute");

    libsMgr.write(moduleDescriptor, new FileHandle(
      new File("src/test/resources/module.xqy")).withFormat(Format.TEXT));

    // get the list of descriptors
    ExtensionLibraryDescriptor[] descriptors = libsMgr.list("/ext/my/path/to/my/");
    assertEquals("number of modules installed", 1, descriptors.length);

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
    assertEquals("number of modules installed", 0, descriptors.length);

  }

  @Test
  public void testXQueryModuleCRUDXmlFileNegative() {
    Common.connectAdmin();

    // get a manager
    ExtensionLibrariesManager libsMgr = Common.adminClient.newServerConfigManager().newExtensionLibrariesManager();

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
