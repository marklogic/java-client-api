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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.datamovement.impl.assignment.AssignmentManager;
import com.marklogic.client.datamovement.impl.assignment.AssignmentPolicy;
import com.marklogic.client.datamovement.impl.assignment.ForestInfo;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.datamovement.Batcher;
import com.marklogic.client.datamovement.DataMovementException;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.JobTicket.JobType;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.JobReport;
import com.marklogic.client.util.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataMovementServices {
  private static Logger logger = LoggerFactory.getLogger(DataMovementServices.class);
  private enum Condition { TRUE, FALSE, UNKNOWN; }
  private final static Condition MAY_BATCH_PER_FOREST =
          Condition.valueOf(System.getProperty("MAY_BATCH_PER_FOREST", "UNKNOWN"));

  private DatabaseClient client;

  public DatabaseClient getClient() {
    return client;
  }
  public DataMovementServices setClient(DatabaseClient client) {
    this.client = client;
    return this;
  }

  public ForestConfigurationImpl readForestConfig(DataMovementManagerImpl moveMgr) {
    long serverVersion = moveMgr.getServerVersion();
    boolean hasPolicy = (
        MAY_BATCH_PER_FOREST == Condition.TRUE ||
        (MAY_BATCH_PER_FOREST == Condition.UNKNOWN &&
            (Long.compareUnsigned(serverVersion, Long.parseUnsignedLong("10000300")) >= 0 ||
            (Long.compareUnsigned(serverVersion, Long.parseUnsignedLong("9001100")) >= 0 &&
                Long.compareUnsigned(serverVersion, Long.parseUnsignedLong("10000000")) < 0))));
    RequestParameters params = hasPolicy ? new RequestParameters().with("view", "policy") : null;
    JsonNode results = null;
    logger.info("Getting forest configuration");
    try {
      results = ((DatabaseClientImpl) client).getServices()
              .getResource(null, "internal/forestinfo", null, params, new JacksonHandle())
              .get();
    } catch(Exception e) {
      logger.warn("Failed to read forest policy configuration", e);
      hasPolicy = false;
      results = ((DatabaseClientImpl) client).getServices()
              .getResource(null, "internal/forestinfo", null, null, new JacksonHandle())
              .get();
    }
    ForestConfigurationImpl forestConfig = new ForestConfigurationImpl();
    AssignmentPolicy.Kind assignPolicy = null;
    boolean fastloadAllowed = false;
    String database = null;
    Map<String, ForestInfo> forestMap = null;
    JsonNode forestNodes = null;
System.out.println("PERFTEST: MAY_BATCH_PER_FOREST="+MAY_BATCH_PER_FOREST+", hasPolicy="+hasPolicy);
    if (hasPolicy) {
      String assignPolicyName = results.get("assignmentPolicy").asText();
      assignPolicy = AssignmentPolicy.Kind.forName(assignPolicyName);
      switch(assignPolicy) {
        case BUCKET:
        case LEGACY:
        case SEGMENT:
          fastloadAllowed = true;
          break;
        case STATISTICAL:
          fastloadAllowed = results.get("fastloadAllowed").asBoolean();
          if (!fastloadAllowed) {
            logger.warn("Cannot batch by forest during rebalancing for statistical assignment policy");
          }
          break;
        // unsupported because configuration is required
        case QUERY:
        case RANGE:
          fastloadAllowed = false;
          logger.warn("Batch by forest not supported for query or range assignment policies");
          break;
        default:
          throw new MarkLogicInternalException("cannot initialize unknown policy: "+assignPolicyName);
      }
System.out.println("PERFTEST: assignPolicy="+assignPolicy+", fastloadAllowed="+fastloadAllowed);
      database = results.get("database").asText();
      forestMap = new HashMap<>();
      forestNodes = results.get("forests");
    } else {
      forestNodes = results;
    }
    List<ForestImpl> forests = new ArrayList<>();
    for (JsonNode forestNode : forestNodes) {
      String id = forestNode.get("id").asText();
      String name = forestNode.get("name").asText();
      if (database == null) {
        database = forestNode.get("database").asText();
      }
      String host = forestNode.get("host").asText();
      String openReplicaHost = getNodeAsText(forestNode,"openReplicaHost");
      String requestHost = getNodeAsText(forestNode,"requestHost");
      // Since we added the forestinfo end point to populate both alternateHost and requestHost
      // in case we have a requestHost so that we don't break the existing API code, we will make the
      // alternateHost as null if both alternateHost and requestHost is set.
      String alternateHost = (requestHost != null) ? null : getNodeAsText(forestNode,"alternateHost");
      boolean isUpdateable = "all".equals(forestNode.get("updatesAllowed").asText());
      boolean isDeleteOnly = false; // TODO: get this for real after we start using a REST endpoint
      ForestImpl forest = new ForestImpl(host, openReplicaHost, requestHost, alternateHost, database, name, id, isUpdateable, isDeleteOnly);
      if (hasPolicy) {
        JsonNode fragmentCount = forestNode.get("fragmentCount");
        if (fragmentCount != null) forest.setFragmentCount(fragmentCount.asLong());
      }
      if (forestMap != null) forestMap.put(id, forest);
      forests.add(forest);
    }
    if (fastloadAllowed) {
      logger.info("Batching by forest for assignment policy: "+assignPolicy.name());
      AssignmentManager assignMgr = AssignmentManager.getInstance();
      if (!assignMgr.isInitialized()) {
        assignMgr.setEffectiveVersion(serverVersion);
        assignMgr.initialize(assignPolicy, forestMap, BatcherImpl.DEFAULT_BATCH_SIZE);
      }
    }
    return forestConfig.withForests(forests.toArray(new ForestImpl[forests.size()]));
  }

  static private String getNodeAsText(JsonNode parent, String name) {
    JsonNode child = parent.get(name);
    if (child == null) return null;
    return child.asText();
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
