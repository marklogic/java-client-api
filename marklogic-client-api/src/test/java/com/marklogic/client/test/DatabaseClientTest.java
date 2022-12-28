/*
 * Copyright (c) 2022 MarkLogic Corporation
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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClient.ConnectionResult;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.*;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.util.RequestLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseClientTest {
  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    Common.connectAdmin();
  }
  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testNewDocument() {
    GenericDocumentManager doc = Common.client.newDocumentManager();
    assertNotNull( doc);
  }

  @Test
  public void testNewBinaryDocument() {
    BinaryDocumentManager doc = Common.client.newBinaryDocumentManager();
    assertNotNull( doc);
  }

  @Test
  public void testNewJSONDocument() {
    JSONDocumentManager doc = Common.client.newJSONDocumentManager();
    assertNotNull( doc);
  }

  @Test
  public void testNewTextDocument() {
    TextDocumentManager doc = Common.client.newTextDocumentManager();
    assertNotNull( doc);
  }

  @Test
  public void testNewXMLDocument() {
    XMLDocumentManager doc = Common.client.newXMLDocumentManager();
    assertNotNull( doc);
  }

  @Test
  public void testNewLogger() {
    RequestLogger logger = Common.client.newLogger(System.out);
    assertNotNull( logger);
  }

  @Test
  public void testNewQueryManager() {
    QueryManager mgr = Common.client.newQueryManager();
    assertNotNull( mgr);
  }

  @Test
  public void testNewRuleManager() {
    RuleManager mgr = Common.client.newRuleManager();
    assertNotNull( mgr);
  }

  @Test
  public void testNewPojoRepository() {
    PojoRepository<City, Integer> mgr = Common.client.newPojoRepository(City.class, Integer.class);
    assertNotNull( mgr);
  }

  @Test
  public void testNewServerEvaluationCall() {
    ServerEvaluationCall mgr = Common.client.newServerEval();
    assertNotNull( mgr);
  }

  @Test
  public void testNewQueryOptionsManager() {
    QueryOptionsManager mgr = Common.adminClient.newServerConfigManager().newQueryOptionsManager();
    assertNotNull( mgr);
  }

  @Test
  public void testGetClientImplementationObject() {
    Object impl = Common.client.getClientImplementation();
    assertNotNull( impl);
    assertTrue( impl instanceof okhttp3.OkHttpClient);
  }

  @Test
  public void testCheckConnectionWithValidUser() {

	DatabaseClient marklogic = Common.makeNewClient(Common.HOST,
		        Common.PORT, Common.newSecurityContext(
				        Common.SERVER_ADMIN_USER, Common.SERVER_ADMIN_PASS));

    ConnectionResult connResult = marklogic.checkConnection();
    assertTrue(connResult.isConnected());
  }

  @Test
  public void testCheckConnectionWithInvalidUser() {
	DatabaseClient marklogic = Common.makeNewClient(Common.HOST,
		        Common.PORT, Common.newSecurityContext("invalid", "invalid"));

    ConnectionResult connResult = marklogic.checkConnection();
    assertFalse(connResult.isConnected());
    assertTrue(connResult.getStatusCode() == 401);
    assertTrue(connResult.getErrorMessage().equalsIgnoreCase("Unauthorized"));
  }

}
