/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.datamovement.impl;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.impl.DatabaseClientImpl;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.datamovement.JobTicket.JobType;
import com.marklogic.client.query.SearchQueryDefinition;
import com.marklogic.client.util.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DataMovementServices {
  private static Logger logger = LoggerFactory.getLogger(DataMovementServices.class);

  private DatabaseClient client;

  public DatabaseClient getClient() {
    return client;
  }

  public DataMovementServices setClient(DatabaseClient client) {
    this.client = client;
    return this;
  }

  QueryConfig initConfig(String method, SearchQueryDefinition qdef) {
    logger.debug("initializing forest configuration with query");
    if (qdef == null) throw new IllegalArgumentException("null query definition");

    JsonNode result = ((DatabaseClientImpl) this.client).getServices()
      .forestInfo(null, method, new RequestParameters(), qdef, new JacksonHandle())
      .get();
    // System.out.println(result.toPrettyString());

    QueryConfig queryConfig = new QueryConfig();

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode queryResult = result.get("query");
      if (queryResult != null && queryResult.isObject() && queryResult.has("ctsquery")) {
        queryConfig.serializedCtsQuery = mapper.writeValueAsString(queryResult);
        logger.debug("initialized query to: {}", queryConfig.serializedCtsQuery);
      }
      JsonNode filteredResult = result.get("filtered");
      if (filteredResult != null && filteredResult.isBoolean()) {
        queryConfig.filtered = filteredResult.asBoolean();
        logger.debug("initialized filtering to: {}", queryConfig.filtered.toString());
      }
      JsonNode maxDocToUriBatchRatio = result.get("maxDocToUriBatchRatio");
      if (maxDocToUriBatchRatio != null && maxDocToUriBatchRatio.isInt()) {
        queryConfig.maxDocToUriBatchRatio = maxDocToUriBatchRatio.asInt();
        logger.debug("initialized maxDocToUriBatchRatio to : {}", queryConfig.maxDocToUriBatchRatio);
      } else {
        queryConfig.maxDocToUriBatchRatio = -1;
      }
      JsonNode defaultDocBatchSize = result.get("defaultDocBatchSize");
      if (defaultDocBatchSize != null && defaultDocBatchSize.isInt()) {
        queryConfig.defaultDocBatchSize = defaultDocBatchSize.asInt();
        logger.debug("initialized defaultDocBatchSize to : {}", queryConfig.defaultDocBatchSize);
      } else {
        queryConfig.defaultDocBatchSize = -1;
      }
      JsonNode maxUriBatchSize = result.get("maxUriBatchSize");
      if (maxUriBatchSize != null && maxUriBatchSize.isInt()) {
        queryConfig.maxUriBatchSize = maxUriBatchSize.asInt();
        logger.debug("initialized maxUriBatchSize to : {}", queryConfig.maxUriBatchSize);
      } else {
        queryConfig.maxUriBatchSize = -1;
      }

    } catch (JsonProcessingException e) {
      logger.error("failed to initialize query", e);
    }

    queryConfig.forestConfig = makeForestConfig(result.has("forests") ? result.get("forests") : result);

    return queryConfig;
  }

  ForestConfigurationImpl readForestConfig() {
    logger.debug("initializing forest configuration");

    JsonNode forestNodes = ((DatabaseClientImpl) this.client).getServices()
      .getResource(null, "internal/forestinfo", null, null, new JacksonHandle())
      .get();
    return makeForestConfig(forestNodes);
  }
  private ForestConfigurationImpl makeForestConfig(JsonNode forestNodes) {
    List<ForestImpl> forests = new ArrayList<>();
    for (JsonNode forestNode : forestNodes) {
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
  public JobTicket startJob(RowBatcher<?> batcher, ConcurrentHashMap<String, JobTicket> activeJobs) {
    return startJobImpl((RowBatcherImpl<?>) batcher, JobType.ROW_BATCHER, activeJobs)
            .withRowBatcher((RowBatcherImpl<?>) batcher);
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

  static class QueryConfig {
    String serializedCtsQuery;
    ForestConfiguration forestConfig;
    Boolean filtered;
    int maxDocToUriBatchRatio;
    int defaultDocBatchSize;
    int maxUriBatchSize;
  }
}
