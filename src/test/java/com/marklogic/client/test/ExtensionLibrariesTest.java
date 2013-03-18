package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ExtensionLibraryDescriptor;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

public class ExtensionLibrariesTest {

	@Test
	public void testXQueryModuleCRUD() {

		Common.connectAdmin();

		// get a manager
		ExtensionLibrariesManager libsMgr = Common.client
				.newServerConfigManager().newExtensionLibrariesManager();

		// write XQuery file to the modules database
		libsMgr.write("/ext/my/path/to/my/module.xqy", new FileHandle(
				new File("src/test/resources/module.xqy")).withFormat(Format.TEXT));

		// read it back
		String xqueryModuleAsString = libsMgr.read(
				"/ext/my/path/to/my/module.xqy", new StringHandle()).get();

		assertTrue("module read and read back", xqueryModuleAsString.startsWith("xquery version \"1.0-ml\";"));
		
		// get the list of descriptors
		ExtensionLibraryDescriptor[] descriptors = libsMgr.list();
		assertEquals("number of modules installed", descriptors.length, 1);
		
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
		
		descriptors = libsMgr.list();
		assertEquals("number of modules installed", descriptors.length, 0);
		
	}
}
