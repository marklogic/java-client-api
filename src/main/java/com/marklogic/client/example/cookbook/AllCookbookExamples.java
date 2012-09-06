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
package com.marklogic.client.example.cookbook;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.marklogic.client.example.cookbook.Util.ExampleProperties;

/**
 * AllCookbookExamples executes all of the recipe examples in the cookbook.
 * Please set up a REST server and configure Example.properties
 * before running any example.
 */
public class AllCookbookExamples {
	public static void main(String[] args) throws IOException, JAXBException {
		ExampleProperties props = Util.loadProperties();

		// execute the examples
		ClientCreator.run(             props );
		DocumentWrite.run(             props );
		DocumentRead.run(              props );
		DocumentMetadataWrite.run(     props );
		DocumentMetadataRead.run(      props );
		DocumentDelete.run(            props );
		DocumentFormats.run(           props );
		DocumentOutputStream.run(      props );
		JAXBDocument.run(              props );
		KeyValueSearch.run(            props );
		QueryOptions.run(              props );
 		StringSearch.run(              props );
 		StructuredSearch.run(          props );
		MultiStatementTransaction.run( props );
 		DocumentReadTransform.run(     props );
 		DocumentWriteTransform.run(    props );
 		OptimisticLocking.run(         props );
 		ResourceExtension.run(         props );
		// SSLClientCreator is not included in this list because it requires a change
		//     to the REST server that invalidates all of the other examples.  See
		//     the comments in SSLClientCreator.
	}
}
