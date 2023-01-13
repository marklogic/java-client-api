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

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.QueryOptionsListHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QueryOptionsListHandleTest {
  @SuppressWarnings("unused")
  private static final Logger logger = (Logger) LoggerFactory
    .getLogger(QueryOptionsListHandleTest.class);

  @BeforeAll
  public static void beforeClass() {
    Common.connect();
    Common.connectRestAdmin();
  }

  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void tesQueryOptionsListHandle() throws IOException, ParserConfigurationException, SAXException {
    File f = new File("src/test/resources/optionslist.xml");
    MyQueryOptionsListHandle v;
    try (FileInputStream is = new FileInputStream(f)) {
      v = new MyQueryOptionsListHandle();
      v.parseTestData(is);
    }
    Map<String,String> map = v.getValuesMap();
    assertEquals( map.size(), 2);
    assertEquals( map.get("photos"), "/v1/config/query/photos");
  }

  // This only works if you've loaded the 5min guide @Test
  public void serverOptionsList() throws IOException, ParserConfigurationException, SAXException {
    QueryManager queryMgr = Common.client.newQueryManager();

    QueryOptionsListHandle results = queryMgr.optionsList(new QueryOptionsListHandle());
    assertNotNull(results);
    Map<String,String> map = results.getValuesMap();
    assertEquals( map.size(), 2);
    assertEquals( map.get("photos"), "/v1/config/query/photos");
  }

  @Test
  public void serverOptionsListRaw()
    throws IOException, ParserConfigurationException, SAXException, ForbiddenUserException, FailedRequestException
  {
    QueryManager queryMgr = Common.client.newQueryManager();
    QueryOptionsManager queryOptionsMgr = Common.restAdminClient.newServerConfigManager().newQueryOptionsManager();

    StringHandle results = queryMgr.optionsList(new StringHandle());
    assertNotNull(results);
    String resultsString = results.get();
    assertNotNull(resultsString);

    StringHandle results2 = queryOptionsMgr.optionsList(new StringHandle());
    assertNotNull(results2);
    assertEquals(resultsString, results2.get());
    assertNotNull(resultsString);

  }


  static public class MyQueryOptionsListHandle extends QueryOptionsListHandle {
    public void parseTestData(InputStream stream) {
      receiveContent(stream);
    }
  }

  @Test
  public void testInitialization() {
    new QueryOptionsListHandle().getValuesMap();

  }
}
