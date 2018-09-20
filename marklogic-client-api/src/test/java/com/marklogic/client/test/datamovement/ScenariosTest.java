/*
 * Copyright 2015-2018 MarkLogic Corporation
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

import com.marklogic.client.datamovement.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.junit.AfterClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DOMHandle;

import com.marklogic.client.test.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScenariosTest {
  Logger logger = LoggerFactory.getLogger(ScenariosTest.class);
  DatabaseClient client = Common.connect();

  @AfterClass
  public static void afterClass() {
  }

  @Test
  /** 1.  Deborah is a developer working to bring data into MarkLogic. She’s
   * tasked with getting messages from her organization’s JBoss ESB, doing some
   * processing and validation and writing the results to MarkLogic. She’s
   * experienced with Java and enterprise software, but hasn’t worked with
   * MarkLogic before. She includes a reference to a MarkLogic library in her
   * own project’s Gradle dependencies. Gradle automatically gets the code and
   * its dependencies from the public Maven repository. In parallel, she
   * downloads example code and integration tutorials from
   * developer.marklogic.com. In an afternoon, she has a prototype of some
   * custom code that gets messages off the bus and writes them to MarkLogic as
   * XML in her development cluster.
   */
  public void scenario1() {
  }

  @Test
  /** 2. After some refactoring and testing, Deborah deploys her code to
   * dedicated acceptance and production clusters. In production, her code
   * needs to handle peak loads of 10M updates per day of documents roughly
   * 5KB each. Duane, her DBA has adequately sized the cluster, but Deborah
   * needs to be confident that she’s spreading the ingestion load such that
   * there are no hot spots and she can take advantage of the resources
   * available to increase throughput. She has instrumented her code, using
   * MarkLogic APIs, to get regularly updated measurements of number of
   * documents processed, number of bytes moved, warnings and errors, etc.
   */
  public void scenario2() throws Exception {
    OurJbossESBPlugin plugin = new OurJbossESBPlugin(client);
    plugin.process(new Message());
  }

  private class Message {
    public Map<String, Object> getBody() throws Exception {
      Map<String, Object> map = new HashMap<>();
      map.put("uri", "http://marklogic.com/my/test/uri");
      Document document =
        DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      Element element = document.createElement("test");
      document.appendChild(element);
      map.put("content", document);
      return map;
    }
  }

  private class OurJbossESBPlugin {

    private int BATCH_SIZE = 1;
    private DataMovementManager moveMgr;
    private JobTicket ticket;
    private WriteBatcher batcher;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public OurJbossESBPlugin(DatabaseClient client) {
      moveMgr = client.newDataMovementManager();
      batcher = moveMgr.newWriteBatcher()
        .withJobName("OurJbossESBPlugin")
        .withBatchSize(BATCH_SIZE)
        // every time a batch is full, write it to the database via mlcp
        // this is the default, only included here to make it obvious
        //.onBatchFull( new MlcpBatchFullListener() )
        // log a summary report after each successful batch
        .onBatchSuccess( batch ->  logger.info(getSummaryReport()) )
        .onBatchFailure( (batch, throwable) -> {
          List<String> uris = new ArrayList<>();
          for ( WriteEvent event : batch.getItems() ) {
            uris.add(event.getTargetUri());
          }
          logger.warn("FAILURE on batch:" + uris + "\n", throwable);
        });
      ticket = moveMgr.startJob(batcher);
    }

    public Message process(Message message) throws Exception {
      String uri = (String) message.getBody().get("uri");
      Document xmlBody = (Document) message.getBody().get("content");
      // do processing and validation
      batcher.add(uri, new DOMHandle(xmlBody));
      return message;
    }

    public String getSummaryReport() {
      JobReport report = moveMgr.getJobReport(ticket);
      return "batches: " + report.getSuccessBatchesCount() +
        ", docs: "       + report.getSuccessEventsCount() +
        ", failures: "   + report.getFailureEventsCount();
    }
  }
}
