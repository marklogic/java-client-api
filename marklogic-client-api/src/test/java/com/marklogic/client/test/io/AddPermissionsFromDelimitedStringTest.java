package com.marklogic.client.test.io;

import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddPermissionsFromDelimitedStringTest {

	private DocumentMetadataHandle.DocumentPermissions permissions;

	@BeforeEach
	void beforeEach() {
		permissions = new DocumentMetadataHandle().getPermissions();
	}

	@Test
	void simpleCase() {
		permissions.addFromDelimitedString("rest-admin,read,rest-admin,update,rest-extension-user,execute,app-user,node-update");

		assertEquals(3, permissions.size());
		assertEquals(2, permissions.get("rest-admin").size());
		assertEquals(1, permissions.get("rest-extension-user").size());
		assertEquals(DocumentMetadataHandle.Capability.EXECUTE, permissions.get("rest-extension-user").iterator().next());
		assertEquals(1, permissions.get("app-user").size());
		assertEquals(DocumentMetadataHandle.Capability.NODE_UPDATE, permissions.get("app-user").iterator().next());
	}

	@Test
	void roleAlreadyExists() {
		permissions.addFromDelimitedString("rest-reader,read");
		assertEquals(1, permissions.size());
		assertEquals(1, permissions.get("rest-reader").size());

		permissions.addFromDelimitedString("rest-reader,update");
		assertEquals(1, permissions.size());
		assertEquals(2, permissions.get("rest-reader").size());

		permissions.addFromDelimitedString("rest-reader,read");
		assertEquals(1, permissions.size());
		assertEquals(2, permissions.get("rest-reader").size(), "A duplicate permission should be ignored since the " +
			"set of capabilities is a Set.");
	}

	@Test
	void badInput() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
			() -> permissions.addFromDelimitedString("rest-admin,read,rest-admin"));
		assertTrue(ex.getMessage().startsWith("Unable to parse permissions string"));
	}

	@Test
	void invalidCapability() {
		IllegalArgumentException ex = assertThrows(
			IllegalArgumentException.class,
			() -> permissions.addFromDelimitedString("app-user,not-valid"));
		assertEquals("Unable to parse permissions string: app-user,not-valid; cause: No enum constant com.marklogic.client.io.DocumentMetadataHandle.Capability.NOT_VALID",
			ex.getMessage());
	}

}
