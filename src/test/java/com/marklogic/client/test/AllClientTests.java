package com.marklogic.client.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/* NOTE:
 * Create a REST server that matches the parameters in Common.java before running the tests
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	DatabaseClientFactoryTest.class,
	DatabaseClientTest.class,
	GenericDocumentTest.class,
	BinaryDocumentTest.class,
	JSONDocumentTest.class,
	TextDocumentTest.class,
	XMLDocumentTest.class,
	MetadataHandleTest.class
	})
public class AllClientTests {
}
