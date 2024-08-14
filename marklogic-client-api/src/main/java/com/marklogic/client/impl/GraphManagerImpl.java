/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
import com.marklogic.client.io.marker.QuadsWriteHandle;
import com.marklogic.client.io.marker.TriplesReadHandle;
import com.marklogic.client.io.marker.TriplesWriteHandle;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.GraphPermissions;
import java.util.Map;

public class GraphManagerImpl<R extends TriplesReadHandle, W extends TriplesWriteHandle>
  extends AbstractLoggingManager
  implements GraphManager
{
  private RESTServices services;
  private HandleFactoryRegistry handleRegistry;
  private String defaultMimetype;

  public GraphManagerImpl(RESTServices services, HandleFactoryRegistry handleRegistry) {
    super();
    this.services = services;
    this.handleRegistry = handleRegistry;
  }

  @Override
  public Iterator<String> listGraphUris() {
    final String uriString = services.getGraphUris(requestLogger, new StringHandle()).get();
    String[] uris = uriString.split("\n");
    return Arrays.asList(uris).iterator();
  }

  @Override
  public <T extends TriplesReadHandle> T read(String uri, T handle) {
    return read(uri, handle, null);
  }

  @Override
  public <T extends TriplesReadHandle> T read(String uri, T handle,
                                              Transaction transaction) {
    @SuppressWarnings("rawtypes")
    HandleImplementation baseHandle = HandleAccessor.as(handle);
    String mimetype = baseHandle.getMimetype();
    if ( mimetype == null ) baseHandle.setMimetype(defaultMimetype);
    services.readGraph(requestLogger, uri, handle, transaction);
    baseHandle.setMimetype(mimetype);
    return handle;
  }

  @Override
  public <T> T readAs(String uri, Class<T> as) {
    return readAs(uri, as, null);
  }

  @Override
  public <T> T readAs(String uri, Class<T> as,
                      Transaction transaction) {
    ContentHandle<T> triplesHandle = getTriplesReadHandle(as);
    if (null == read(uri, (TriplesReadHandle) triplesHandle, transaction)) {
      return null;
    }

    return triplesHandle.get();
  }

  @Override
  public GraphPermissions getPermissions(String uri) {
    return getPermissions(uri, null);
  }

  @Override
  public GraphPermissions getPermissions(String uri, Transaction transaction) {
    JsonNode json = services.getPermissions(requestLogger, uri, new JacksonHandle(), transaction).get();
    GraphPermissions perms = new GraphPermissionsImpl();
    for ( JsonNode permission : json.path("permissions") ) {
      String role = permission.path("role-name").asText();
      Set<Capability> capabilities = new HashSet<>();
      for ( JsonNode capability : permission.path("capabilities") ) {
        String value = capability.asText();
        if ( value != null ) {
          capabilities.add(Capability.valueOf(value.toUpperCase()));
        }
      }
      perms.put(role, capabilities);
    }
    return perms;
  }

  @Override
  public void deletePermissions(String uri) {
    deletePermissions(uri, null);
  }

  @Override
  public void deletePermissions(String uri, Transaction transaction) {
    services.deletePermissions(requestLogger, uri, transaction);
  }

  private <T> ContentHandle<T> getTriplesReadHandle(Class<T> as) {
    ContentHandle<T> handle = handleRegistry.makeHandle(as);

    if ( ! (handle instanceof TriplesReadHandle) ) {
      throw new IllegalArgumentException("The Class for arg \"as\" " +
        "is registered by " + handle.getClass() + " which is not a " +
        "TriplesReadHandle so it cannot be used by GraphManager");
    }
    return handle;
  }

  private JacksonHandle generatePermissions(GraphPermissions permissions) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode payload = mapper.createObjectNode();
    ArrayNode permissionsNode = mapper.createArrayNode();
    payload.set("permissions", permissionsNode);
    for ( Map.Entry<String,Set<Capability>> entry : permissions.entrySet() ) {
      ObjectNode permissionNode = mapper.createObjectNode();
      permissionNode.put("role-name", entry.getKey());
      ArrayNode capabilitiesNode = mapper.createArrayNode();
      for ( Capability capability : entry.getValue() ) {
        capabilitiesNode.add(capability.toString().toLowerCase());
      }
      permissionNode.set("capabilities", capabilitiesNode);
      permissionsNode.add(permissionNode);
    }

    return new JacksonHandle(payload);
  }

  @Override
  public void writePermissions(String uri, GraphPermissions permissions) {
    writePermissions(uri, permissions, null);
  }

  @Override
  public void writePermissions(String uri, GraphPermissions permissions, Transaction transaction) {
    services.writePermissions(requestLogger, uri,
      generatePermissions(permissions), transaction);
  }

  @Override
  public void mergePermissions(String uri, GraphPermissions permissions) {
    mergePermissions(uri, permissions, null);
  }

  @Override
  public void mergePermissions(String uri, GraphPermissions permissions, Transaction transaction) {
    services.mergePermissions(requestLogger, uri,
      generatePermissions(permissions), transaction);
  }

  @Override
  public void merge(String uri, TriplesWriteHandle handle) {
    merge(uri, handle, null, null);
  }

  @Override
  public void merge(String uri, TriplesWriteHandle handle,
                    Transaction transaction) {
    merge(uri, handle, null, transaction);
  }

  @Override
  public void merge(String uri, TriplesWriteHandle handle,
                    GraphPermissions permissions) {
    merge(uri, handle, permissions, null);
  }

  @Override
  public void merge(String uri, TriplesWriteHandle handle,
                    GraphPermissions permissions, Transaction transaction) {
    @SuppressWarnings("rawtypes")
    HandleImplementation baseHandle = HandleAccessor.as(handle);
    String mimetype = validateGraphsMimetype(baseHandle);
    services.mergeGraph(requestLogger, uri, handle, permissions, transaction);
    baseHandle.setMimetype(mimetype);
  }

  @Override
  public void mergeAs(String uri, Object graphData) {
    mergeAs(uri, graphData, null, null);
  }

  @Override
  public void mergeAs(String uri, Object graphData, Transaction transaction) {
    mergeAs(uri, graphData, null, transaction);
  }

  @Override
  public void mergeAs(String uri, Object graphData,
                      GraphPermissions permissions) {
    mergeAs(uri, graphData, permissions, null);
  }

  @Override
  public void mergeAs(String uri, Object graphData,
                      GraphPermissions permissions, Transaction transaction) {
    merge(uri, populateTriplesHandle(graphData), permissions, transaction);
  }

  @Override
  public void write(String uri, TriplesWriteHandle handle) {
    write(uri, handle, null, null);
  }

  @Override
  public void write(String uri, TriplesWriteHandle handle,
                    Transaction transaction) {
    write(uri, handle, null, transaction);
  }

  @Override
  public void write(String uri, TriplesWriteHandle handle,
                    GraphPermissions permissions) {
    write(uri, handle, permissions, null);
  }

  @Override
  public void write(String uri, TriplesWriteHandle handle,
                    GraphPermissions permissions, Transaction transaction) {
    @SuppressWarnings("rawtypes")
    HandleImplementation baseHandle = HandleAccessor.as(handle);
    String mimetype = validateGraphsMimetype(baseHandle);
    services.writeGraph(requestLogger, uri, handle, permissions, transaction);
    baseHandle.setMimetype(mimetype);
  }

  @Override
  public void writeAs(String uri, Object graphData) {
    writeAs(uri, graphData, null, null);
  }

  @Override
  public void writeAs(String uri, Object graphData, Transaction transaction) {
    writeAs(uri, graphData, null, transaction);
  }

  @Override
  public void writeAs(String uri, Object graphData,
                      GraphPermissions permissions) {
    writeAs(uri, graphData, permissions, null);
  }

  @Override
  public void writeAs(String uri, Object graphData,
                      GraphPermissions permissions, Transaction transaction) {
    write(uri, populateTriplesHandle(graphData), permissions, transaction);
  }

  private AbstractWriteHandle populateHandle(Object graphData) {
    if (graphData == null) {
      throw new IllegalArgumentException("no graphData to write");
    }

    Class<?> as = graphData.getClass();

    if (AbstractWriteHandle.class.isAssignableFrom(as)) {
      return (AbstractWriteHandle) graphData;
    } else {
      ContentHandle<?> handle = handleRegistry.makeHandle(as);
      if ( ! (handle instanceof TriplesReadHandle) ) {
        throw new IllegalArgumentException("Arg \"graphData\" " +
          "is handled by " + handle.getClass() + " which is not a " +
          "TriplesReadHandle so it cannot write using GraphManager");
      }
      Utilities.setHandleContent(handle, graphData);
      return handle;
    }
  }
  private QuadsWriteHandle populateQuadsHandle(Object graphData) {
    return (QuadsWriteHandle) populateHandle(graphData);
  }
  private TriplesWriteHandle populateTriplesHandle(Object graphData) {
    return (TriplesWriteHandle) populateHandle(graphData);
  }

  @Override
  public void delete(String uri) {
    services.deleteGraph(requestLogger, uri, null);
  }

  @Override
  public void delete(String uri, Transaction transaction) {
    services.deleteGraph(requestLogger, uri, transaction);
  }

  @Override
  public <T extends TriplesReadHandle> T things(T handle, String... iris) {
    if ( iris == null ) throw new IllegalArgumentException("iris cannot be null");
    return services.getThings(requestLogger, iris, handle);
  }

  @Override
  public <T> T thingsAs(Class<T> as, String... iris) {
    ContentHandle<T> triplesHandle = getTriplesReadHandle(as);
    if (null == things((TriplesReadHandle) triplesHandle, iris) ) {
      return null;
    }

    return triplesHandle.get();
  }

  @Override
  public void mergeGraphs(QuadsWriteHandle handle) {
    mergeGraphs(handle, null);
  }

  @Override
  public void mergeGraphs(QuadsWriteHandle handle, Transaction transaction) {
    @SuppressWarnings("rawtypes")
    HandleImplementation baseHandle = HandleAccessor.as(handle);
    String mimetype = validateGraphsMimetype(baseHandle);
    services.mergeGraphs(requestLogger, handle, transaction);
    baseHandle.setMimetype(mimetype);
  }

  @Override
  public void mergeGraphsAs(Object quadsData) {
    mergeGraphsAs(quadsData, null);
  }

  @Override
  public void mergeGraphsAs(Object quadsData, Transaction transaction) {
    mergeGraphs( populateQuadsHandle(quadsData), transaction );
  }

  @Override
  public void replaceGraphs(QuadsWriteHandle handle) {
    replaceGraphs(handle, null);
  }

  @Override
  public void replaceGraphs(QuadsWriteHandle handle, Transaction transaction) {
    @SuppressWarnings("rawtypes")
    HandleImplementation baseHandle = HandleAccessor.as(handle);
    String mimetype = validateGraphsMimetype(baseHandle);
    services.writeGraphs(requestLogger, handle, transaction);
    baseHandle.setMimetype(mimetype);
  }

  @Override
  public void replaceGraphsAs(Object quadsData) {
    replaceGraphsAs(quadsData, null);
  }

  @Override
  public void replaceGraphsAs(Object quadsData, Transaction transaction) {
    replaceGraphs( populateQuadsHandle(quadsData), transaction );
  }

  @Override
  public void deleteGraphs() {
    deleteGraphs(null);
  }

  @Override
  public void deleteGraphs(Transaction transaction) {
    services.deleteGraphs(requestLogger, transaction);
  }

  @Override
  public GraphPermissions permission(String role, Capability... capabilities) {
    GraphPermissionsImpl perms = new GraphPermissionsImpl();
    perms.put(role, new HashSet<>(Arrays.asList(capabilities)));
    return perms;
  }

  @Override
  public String getDefaultMimetype() {
    return defaultMimetype;
  }

  @Override
  public void setDefaultMimetype(String mimetype) {
    this.defaultMimetype = mimetype;
  }

  @SuppressWarnings("rawtypes")
  private String validateGraphsMimetype(HandleImplementation baseHandle) {
    String mimetype = baseHandle.getMimetype();
    if ( mimetype == null ) {
      if ( defaultMimetype != null ) {
        baseHandle.setMimetype(defaultMimetype);
      } else {
        throw new IllegalArgumentException("You must either call setMimetype on your " +
          "handle or setDefaultMimetype on your GraphManager instance with a mimetype " +
          "from RDFMimeTypes");
      }
    }
    return mimetype;
  }

  @Override
  public GraphPermissions newGraphPermissions() {
    return new GraphPermissionsImpl();
  }
}
