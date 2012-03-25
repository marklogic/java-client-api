package com.marklogic.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.marklogic.client.io.JAXBHandle;

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
	DocumentMetadataHandleTest.class,
    KeyValueSearchTest.class,
    StringSearchTest.class,
    StructuredQueryBuilderTest.class,
    StructuredSearchTest.class,
// works when run independently but throws JUnit error in suite
//  JAXBHandle.class,
    NamespacesManagerTest.class
	})
public class AllClientTests {
}
