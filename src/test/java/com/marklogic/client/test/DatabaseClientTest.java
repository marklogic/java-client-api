/*
 * Copyright 2012-2015 MarkLogic Corporation
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
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.util.RequestLogger;

public class DatabaseClientTest {
	@BeforeClass
	public static void beforeClass() {
		Common.connect();
	}
	@AfterClass
	public static void afterClass() {
		Common.release();
	}

	@Test
	public void testNewDocument() {
		GenericDocumentManager doc = Common.client.newDocumentManager();
		assertNotNull("Client could not create generic document", doc);
	}

	@Test
	public void testNewBinaryDocument() {
		BinaryDocumentManager doc = Common.client.newBinaryDocumentManager();
		assertNotNull("Client could not create binary document", doc);
	}

	@Test
	public void testNewJSONDocument() {
		JSONDocumentManager doc = Common.client.newJSONDocumentManager();
		assertNotNull("Client could not create JSON document", doc);
	}

	@Test
	public void testNewTextDocument() {
		TextDocumentManager doc = Common.client.newTextDocumentManager();
		assertNotNull("Client could not create text document", doc);
	}

	@Test
	public void testNewXMLDocument() {
		XMLDocumentManager doc = Common.client.newXMLDocumentManager();
		assertNotNull("Client could not create XML document", doc);
	}

	@Test
	public void testNewLogger() {
		RequestLogger logger = Common.client.newLogger(System.out);
		assertNotNull("Client could not create request logger", logger);
	}

	@Test
	public void testNewQueryManager() {
		QueryManager mgr = Common.client.newQueryManager();
		assertNotNull("Client could not create query manager", mgr);
	}

	@Test
	public void testNewRuleManager() {
		RuleManager mgr = Common.client.newRuleManager();
		assertNotNull("Client could not create rule manager", mgr);
	}

	@Test
	public void testNewPojoRepository() {
		PojoRepository<City, Integer> mgr = Common.client.newPojoRepository(City.class, Integer.class);
		assertNotNull("Client could not create pojo repository", mgr);
	}

	@Test
	public void testNewServerEvaluationCall() {
		ServerEvaluationCall mgr = Common.client.newServerEval();
		assertNotNull("Client could not create ServerEvaluationCall", mgr);
	}

	@Test
	public void testNewQueryOptionsManager() {
		QueryOptionsManager mgr = Common.client.newServerConfigManager().newQueryOptionsManager();
		assertNotNull("Client could not create query options manager", mgr);
	}

	@Test
	public void testGetClientImplementationObject() {
		Object impl = Common.client.getClientImplementation();
		assertNotNull("Client could not get client implementation", impl);
		assertTrue("", impl instanceof org.apache.http.client.HttpClient);
	}
}
