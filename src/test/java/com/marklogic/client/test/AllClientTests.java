/*
 * Copyright 2012 MarkLogic Corporation
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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/* NOTE:
 * Create a REST server that matches the parameters in Common.java before running the tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	DatabaseClientFactoryTest.class,
	DatabaseClientTest.class,
    ServerConfigurationManagerTest.class,
    RequestLoggerTest.class,

	GenericDocumentTest.class,
	BinaryDocumentTest.class,
	JSONDocumentTest.class,
	TextDocumentTest.class,
	XMLDocumentTest.class,
	DocumentMetadataHandleTest.class,
	ConditionalDocumentTest.class,

	StringSearchTest.class,
	KeyValueSearchTest.class,
    DOMSearchResultTest.class,
    StructuredQueryBuilderTest.class,
    StructuredSearchTest.class,
    SearchFacetTest.class,
    SuggestTest.class,

    QueryOptionsHandleTest.class,
    QueryOptionsManagerTest.class,
    QueryOptionsBuilderTest.class,
    
    ResourceExtensionsTest.class,
    ResourceServicesTest.class,
    TransformExtensionsTest.class,
    TransformTest.class,
    
    ValuesHandleTest.class,
    TuplesHandleTest.class,
    ValueConverterTest.class,
    AlertingTest.class,
    RawQueryDefinitionTest.class,

    // works when run independently but throws JUnit error in suite
//  JAXBHandle.class,

    NamespacesManagerTest.class
	})
	
public class AllClientTests {
}
