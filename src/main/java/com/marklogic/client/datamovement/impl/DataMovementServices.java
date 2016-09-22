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
package com.marklogic.client.datamovement.impl;

import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.JobTicket.JobType;
import com.marklogic.client.datamovement.QueryHostBatcher;
import com.marklogic.client.datamovement.WriteHostBatcher;
import java.util.List;

public class DataMovementServices {
  private DatabaseClient client;

  public DatabaseClient getClient() {
    return client;
  }

  public DataMovementServices setClient(DatabaseClient client) {
    this.client = client;
    return this;
  }

  public ForestConfigurationImpl readForestConfig() {
    List<ForestImpl> forests = new ArrayList<>();
    JsonNode results = ((DatabaseClientImpl) client).getServices()
      .getResource(null, "forestinfo", null, null, new JacksonHandle())
      .get();
    for ( JsonNode forestNode : results ) {
      String host = forestNode.get("host").asText();
      String database = forestNode.get("database").asText();
      String id = forestNode.get("id").asText();
      String name = forestNode.get("name").asText();
      boolean isUpdateable = "all".equals(forestNode.get("updatesAllowed").asText());
      boolean isDeleteOnly = false; // TODO: get this for real after we start using a REST endpoint
      long fragmentCount = 0;       // TODO: get this for real after we start using a REST endpoint
      forests.add(new ForestImpl(host, database, name, id, isUpdateable, isDeleteOnly, fragmentCount));
    }

    return new ForestConfigurationImpl(client, forests.toArray(new ForestImpl[forests.size()]));
  }

  public JobTicket startJob(WriteHostBatcher batcher) {
    // TODO: implement job tracking
    return new JobTicketImpl(generateJobId(), JobTicket.JobType.IMPORT_HOST_BATCHER)
        .withWriteHostBatcher((WriteHostBatcherImpl) batcher);
  }

  public JobTicket startJob(QueryHostBatcher batcher) {
    // TODO: implement job tracking
    ((QueryHostBatcherImpl) batcher).start();
    return new JobTicketImpl(generateJobId(), JobTicket.JobType.QUERY_HOST_BATCHER)
        .withQueryHostBatcher((QueryHostBatcherImpl) batcher);
  }

  public JobReport getJobReport(JobTicket ticket) {
    // TODO: implement
    return null;
  }

  public void stopJob(JobTicket ticket) {
    if ( ticket instanceof JobTicketImpl ) {
      JobTicketImpl ticketImpl = (JobTicketImpl) ticket;
      if ( ticketImpl.getJobType() == JobType.IMPORT_HOST_BATCHER ) {
        ticketImpl.getWriteHostBatcher().stop();
      } else if ( ticketImpl.getJobType() == JobType.QUERY_HOST_BATCHER ) {
        ticketImpl.getQueryHostBatcher().stop();
      }
    }
  }

  private String generateJobId() {
    return UUID.randomUUID().toString();
  }
}
