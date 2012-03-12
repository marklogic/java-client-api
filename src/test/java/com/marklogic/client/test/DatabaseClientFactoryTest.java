package com.marklogic.client.test;

import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseClientFactoryTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

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
