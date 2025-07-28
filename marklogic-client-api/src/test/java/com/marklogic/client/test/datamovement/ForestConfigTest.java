/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.test.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.Forest;
import com.marklogic.client.datamovement.ForestConfiguration;
import com.marklogic.client.datamovement.impl.DataMovementManagerImpl;
import com.marklogic.client.test.Common;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ForestConfigTest {
  private static DatabaseClient client = Common.connect();
  private DataMovementManager moveMgr = client.newDataMovementManager();

  @BeforeAll
  public static void beforeClass() {
  }

  @AfterAll
  public static void afterClass() {
  }

  @Test
  public void testArgs() throws Exception {
    if (moveMgr.getConnectionType() == DatabaseClient.ConnectionType.GATEWAY) return;

    int defaultPort = client.getPort();
    Class<?> defaultAuthContext = client.getSecurityContext().getClass();
    ForestConfiguration forestConfig = moveMgr.readForestConfig();
    Forest[] forests = forestConfig.listForests();
    String defaultDatabase = forests[0].getDatabaseName();
    // expect three forests per node
    assertTrue(forests.length % 3 == 0);
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
      if ( forest.getForestName() == null ||
           ! forest.getForestName().startsWith("java-unittest-") ) {
        fail("Unexpected forestName \"" + forest.getForestName() + "\"");
      }
    }
  }
}
