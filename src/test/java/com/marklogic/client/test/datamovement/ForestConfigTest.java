/*
 * Copyright 2015 MarkLogic Corporation
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
package com.marklogic.client.test.datamovement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.impl.DataMovementManagerImpl;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;

public class ForestConfigTest {
  private static DatabaseClient client = Common.connect();
  private DataMovementManager moveMgr = client.newDataMovementManager();

  @BeforeClass
  public static void beforeClass() {
  }

  @AfterClass
  public static void afterClass() {
    client.release();
  }

  @Test
  public void testArgs() throws Exception {
    int defaultPort = client.getPort();
    Class<?> defaultAuthContext = client.getSecurityContext().getClass();
    ForestConfiguration forestConfig = moveMgr.readForestConfig();
    Forest[] forests = forestConfig.listForests();
    String defaultDatabase = forests[0].getDatabaseName();
    assertEquals(3, forests.length);
    for ( Forest forest : forests ) {
      DatabaseClient forestClient = ((DataMovementManagerImpl) moveMgr).getForestClient(forest);
      // not all forests for a database are on the same host, so all we
      // can check is that the hostname is not null
      assertNotNull(forest.getHost());
      // not all hosts have the original REST server, but all hosts have the uber port
      assertEquals(defaultPort, forestClient.getPort());
      assertEquals(defaultDatabase, forest.getDatabaseName());
      assertEquals(defaultAuthContext, forestClient.getSecurityContext().getClass());
      assertEquals(true, forest.isUpdateable());
      assertEquals(false, forest.isDeleteOnly());
      if ( ! "java-unittest-1".equals(forest.getForestName()) &&
           ! "java-unittest-2".equals(forest.getForestName()) &&
           ! "java-unittest-3".equals(forest.getForestName()) ) {
        fail("Unexpected forestName \"" + forest.getForestName() + "\"");
      }
    }
  }
}
