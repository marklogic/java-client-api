/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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

  private static final Logger logger = LoggerFactory.getLogger(DataMovementServices.class);

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

		JsonNode queryResult = result.get("query");

		String serializedCtsQuery = null;
		if (queryResult != null && queryResult.isObject() && queryResult.has("ctsquery")) {
			try {
				serializedCtsQuery = new ObjectMapper().writeValueAsString(queryResult);
				logger.debug("initialized query to: {}", serializedCtsQuery);
			} catch (JsonProcessingException e) {
				logger.warn("Unable to serialize query result while initializing QueryBatcher; cause: {}", e.getMessage());
			}
		}

		JsonNode filteredResult = result.get("filtered");
		Boolean filtered = null;
		if (filteredResult != null && filteredResult.isBoolean()) {
			filtered = filteredResult.asBoolean();
			logger.debug("initialized filtering to: {}", filtered);
		}

		JsonNode maxDocToUriBatchRatioNode = result.get("maxDocToUriBatchRatio");
		int maxDocToUriBatchRatio = -1;
		if (maxDocToUriBatchRatioNode != null && maxDocToUriBatchRatioNode.isInt()) {
			maxDocToUriBatchRatio = maxDocToUriBatchRatioNode.asInt();
			logger.debug("initialized maxDocToUriBatchRatio to : {}", maxDocToUriBatchRatio);
		}

		// Per GitHub bug 1872 and MLE-26460, the server may return -1 when there are fewer server threads than forests.
		// A value of -1 will cause later problems when constructing a LinkedBlockingQueue with a negative capacity.
		// So defaulting this to 1 to avoid later errors.
		if (maxDocToUriBatchRatio <= 0) {
			maxDocToUriBatchRatio = 1;
		}

		JsonNode defaultDocBatchSizeNode = result.get("defaultDocBatchSize");
		int defaultDocBatchSize = -1;
		if (defaultDocBatchSizeNode != null && defaultDocBatchSizeNode.isInt()) {
			defaultDocBatchSize = defaultDocBatchSizeNode.asInt();
			logger.debug("initialized defaultDocBatchSize to : {}", defaultDocBatchSize);
		}

		JsonNode maxUriBatchSizeNode = result.get("maxUriBatchSize");
		int maxUriBatchSize = -1;
		if (maxUriBatchSizeNode != null && maxUriBatchSizeNode.isInt()) {
			maxUriBatchSize = maxUriBatchSizeNode.asInt();
			logger.debug("initialized maxUriBatchSize to : {}", maxUriBatchSize);
		}

		ForestConfiguration forestConfig = makeForestConfig(result.has("forests") ? result.get("forests") : result);
		return new QueryConfig(serializedCtsQuery, forestConfig, filtered,
			maxDocToUriBatchRatio, defaultDocBatchSize, maxUriBatchSize);
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

}
