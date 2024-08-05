/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.SPARQLResultsReadHandle;
import com.marklogic.client.io.marker.TextWriteHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;

public class SPARQLQueryManagerImpl extends AbstractLoggingManager implements SPARQLQueryManager {
  private RESTServices services;
  private long pageLength = -1;

  public SPARQLQueryManagerImpl(RESTServices services) {
    super();
    this.services = services;
  }

  @Override
  public SPARQLQueryDefinition newQueryDefinition() {
    return new SPARQLQueryDefinitionImpl();
  }

  @Override
  public SPARQLQueryDefinition newQueryDefinition(String sparql) {
    return new SPARQLQueryDefinitionImpl(sparql);
  }

  @Override
  public SPARQLQueryDefinition newQueryDefinition(TextWriteHandle sparql) {
    return new SPARQLQueryDefinitionImpl(sparql);
  }

  @Override
  public <T extends SPARQLResultsReadHandle> T executeSelect(
    SPARQLQueryDefinition qdef, T handle) {
    return executeQueryImpl(qdef, handle, null, false);
  }

  @Override
  public <T extends SPARQLResultsReadHandle> T executeSelect(
    SPARQLQueryDefinition qdef, T handle, Transaction tx) {
    return executeQueryImpl(qdef, handle, tx, false);
  }

  @Override
  public <T extends SPARQLResultsReadHandle> T executeSelect(
    SPARQLQueryDefinition qdef, T handle, long start) {
    return executeSelect(qdef, handle, start, null);
  }

  @Override
  public <T extends SPARQLResultsReadHandle> T executeSelect(
    SPARQLQueryDefinition qdef, T handle, long start, Transaction tx) {
    if ( start < 1 ) throw new IllegalArgumentException("start must be 1 or greater");
    return executeQueryImpl(qdef, handle, start, tx, false);
  }

  @Override
  public long getPageLength() {
    return pageLength;
  }

  @Override
  public void setPageLength(long pageLength) {
    if ( pageLength < 0 ) throw new IllegalArgumentException("pageLength must be 0 or greater");
    this.pageLength = pageLength;
  }

  @Override
  public void clearPageLength() {
    pageLength = -1;
  }

  private <T extends AbstractReadHandle> T executeQueryImpl(
    SPARQLQueryDefinition qdef, T handle, Transaction tx, boolean isUpdate) {
    return executeQueryImpl(qdef, handle, -1, tx, isUpdate);
  }

  private <T extends AbstractReadHandle> T executeQueryImpl(
    SPARQLQueryDefinition qdef, T handle, long start, Transaction tx, boolean isUpdate) {
    if ( qdef == null )   throw new IllegalArgumentException("qdef cannot be null");
    if ( handle == null ) throw new IllegalArgumentException("handle cannot be null");
    return services.executeSparql(requestLogger, qdef, handle, start, getPageLength(), tx, isUpdate);
  }

  @Override
  public <T extends TriplesReadHandle> T executeConstruct(
    SPARQLQueryDefinition qdef, T triplesReadHandle) {
    setRdfXmlOrJsonMimetype(triplesReadHandle);
    return executeQueryImpl(qdef, triplesReadHandle, null, false);
  }

  @Override
  public <T extends TriplesReadHandle> T executeConstruct(
    SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx) {
    setRdfXmlOrJsonMimetype(triplesReadHandle);
    return executeQueryImpl(qdef, triplesReadHandle, tx, false);
  }

  private void setRdfXmlOrJsonMimetype(TriplesReadHandle handle) {
    HandleImplementation baseHandle = HandleAccessor.as(handle);
    if ( baseHandle.getFormat() == Format.JSON ) {
      if ( Format.JSON.getDefaultMimetype().equals(baseHandle.getMimetype()) ) {
        baseHandle.setMimetype(RDFMimeTypes.RDFJSON);
      }
    } else if ( baseHandle.getFormat() == Format.XML ) {
      if ( Format.XML.getDefaultMimetype().equals(baseHandle.getMimetype())) {
        baseHandle.setMimetype(RDFMimeTypes.RDFXML);
      }
    }
  }

  @Override
  public <T extends TriplesReadHandle> T executeDescribe(
    SPARQLQueryDefinition qdef, T triplesReadHandle) {
    setRdfXmlOrJsonMimetype(triplesReadHandle);
    return executeQueryImpl(qdef, triplesReadHandle, null, false);
  }

  @Override
  public <T extends TriplesReadHandle> T executeDescribe(
    SPARQLQueryDefinition qdef, T triplesReadHandle, Transaction tx) {
    setRdfXmlOrJsonMimetype(triplesReadHandle);
    return executeQueryImpl(qdef, triplesReadHandle, tx, false);
  }

  @Override
  public Boolean executeAsk(SPARQLQueryDefinition qdef) {
    JsonNode result = executeQueryImpl(qdef, new JacksonHandle(), null, false).get();
    return result.get("boolean").asBoolean();
  }

  @Override
  public Boolean executeAsk(SPARQLQueryDefinition qdef, Transaction tx) {
    JsonNode result = executeQueryImpl(qdef, new JacksonHandle(), tx, false).get();
    return result.get("boolean").asBoolean();
  }

  @Override
  public void executeUpdate(SPARQLQueryDefinition qdef) {
    executeQueryImpl(qdef, (TriplesReadHandle) new StringHandle().withFormat(Format.JSON), null, true);
  }

  @Override
  public void executeUpdate(SPARQLQueryDefinition qdef, Transaction tx) {
    executeQueryImpl(qdef, (TriplesReadHandle) new StringHandle().withFormat(Format.JSON), tx, true);
  }

  @Override
  public GraphPermissions permission(String role, Capability... capabilities) {
    return new GraphPermissionsImpl().permission(role, capabilities);
  }
}
