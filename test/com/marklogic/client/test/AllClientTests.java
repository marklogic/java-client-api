package com.marklogic.client.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	DatabaseClientFactoryTest.class,
	DatabaseClientTest.class,
	BinaryDocumentTest.class,
	TextDocumentTest.class,
	XMLDocumentTest.class
	})
public class AllClientTests {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}
}
