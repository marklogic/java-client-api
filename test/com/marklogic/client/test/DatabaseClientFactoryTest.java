package com.marklogic.client.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

public class DatabaseClientFactoryTest {
	@Test
	public void testConnectStringIntStringStringDigest() {
		DatabaseClientFactory factory = DatabaseClientFactory.newFactory();
		DatabaseClient client = factory.connect(
			Common.HOST, Common.PORT, Common.USERNAME, Common.PASSWORD, Authentication.DIGEST
			);
		assertNotNull("No client from connect", client);
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
