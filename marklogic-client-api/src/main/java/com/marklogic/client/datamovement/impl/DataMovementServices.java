/*
 * Copyright 2015-2019 MarkLogic Corporation
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
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.datamovement.Batcher;
import com.marklogic.client.datamovement.DataMovementException;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.JobTicket.JobType;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.JobReport;

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
      .getResource(null, "internal/forestinfo", null, null, new JacksonHandle())
      .get();
    for ( JsonNode forestNode : results ) {
      String id = forestNode.get("id").asText();
      String name = forestNode.get("name").asText();
      String database = forestNode.get("database").asText();
      String host = forestNode.get("host").asText();
      String openReplicaHost = null;
      if ( forestNode.get("openReplicaHost") != null ) openReplicaHost = forestNode.get("openReplicaHost").asText();
      String requestHost = null;
      if ( forestNode.get("requestHost") != null ) requestHost = forestNode.get("requestHost").asText();
      String alternateHost = null;
      if ( forestNode.get("alternateHost") != null ) alternateHost = forestNode.get("alternateHost").asText();
      // Since we added the forestinfo end point to populate both alternateHost and requestHost
      // in case we have a requestHost so that we don't break the existing API code, we will make the
      // alternateHost as null if both alternateHost and requestHost is set.
      if ( requestHost != null && alternateHost != null )
        alternateHost = null;
      boolean isUpdateable = "all".equals(forestNode.get("updatesAllowed").asText());
      boolean isDeleteOnly = false; // TODO: get this for real after we start using a REST endpoint
      forests.add(
            new ForestImpl(host, openReplicaHost, requestHost, alternateHost, database, name, id, isUpdateable, isDeleteOnly)
      );
    }

    return new ForestConfigurationImpl(forests.toArray(new ForestImpl[forests.size()]));
  }

  public JobTicket startJob(WriteBatcher batcher, ConcurrentHashMap<String, JobTicket> activeJobs) {
    return startJobImpl((WriteBatcherImpl) batcher, JobType.WRITE_BATCHER, activeJobs)
            .withWriteBatcher((WriteBatcherImpl) batcher);
  }
  public JobTicket startJob(QueryBatcher batcher, ConcurrentHashMap<String, JobTicket> activeJobs) {
    return startJobImpl((QueryBatcherImpl) batcher, JobType.QUERY_BATCHER, activeJobs)
        .withQueryBatcher((QueryBatcherImpl) batcher);
  }

  private JobTicketImpl startJobImpl(
          BatcherImpl batcher, JobTicket.JobType jobType, ConcurrentHashMap<String, JobTicket> activeJobs
  ) {
    String jobId = batcher.getJobId();
    if (batcher.getJobId() == null) {
      jobId = generateJobId();
      batcher.setJobId(jobId);
    }
    if (!batcher.isStarted() && activeJobs.containsKey(jobId)) {
      throw new DataMovementException(
              "Cannot start the batcher because the given job Id already exists in the active jobs", null);
    }
    JobTicketImpl jobTicket = new JobTicketImpl(jobId, jobType);
    batcher.start(jobTicket);
    activeJobs.put(jobId, jobTicket);
    return jobTicket;
  }

  public JobReport getJobReport(JobTicket ticket) {
    if ( ticket instanceof JobTicketImpl ) {
      return JobReportImpl.about((JobTicketImpl) ticket);
    }
    return null;
  }

  public void stopJob(JobTicket ticket, ConcurrentHashMap<String, JobTicket> activeJobs) {
    if ( ticket instanceof JobTicketImpl ) {
      JobTicketImpl ticketImpl = (JobTicketImpl) ticket;
      ticketImpl.getBatcher().stop();
      activeJobs.remove(ticket.getJobId());
    }
  }

  public void stopJob(Batcher batcher, ConcurrentHashMap<String, JobTicket> activeJobs) {
    if (batcher instanceof BatcherImpl) {
      ((BatcherImpl) batcher).stop();
    }
    if (batcher.getJobId() != null) activeJobs.remove(batcher.getJobId());
  }

  private String generateJobId() {
    return UUID.randomUUID().toString();
  }
}
