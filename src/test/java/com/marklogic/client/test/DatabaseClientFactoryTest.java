/*
 * Copyright 2012-2014 MarkLogic Corporation
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
