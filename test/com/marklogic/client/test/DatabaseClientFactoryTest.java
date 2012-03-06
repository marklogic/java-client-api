package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DatabaseClientFactoryTest {
	@Test
	public void testConnectStringIntStringStringDigest() {
		assertNotNull("Factory could not create client with digest connection", Common.client);
	}

/*
	@Test
	public void testConnectStringIntStringStringBasic() {
		fail("Not yet implemented");
	}
	@Test
	public void testConnectStringIntStringStringSSLContext() {
		fail("Not yet implemented");
	}
 */
}
