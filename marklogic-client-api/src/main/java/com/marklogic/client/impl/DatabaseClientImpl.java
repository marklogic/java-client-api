/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.OutputStream;
import java.io.Serializable;

import com.marklogic.client.io.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.SPARQLQueryManager;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.DatabaseClientFactory.HandleFactoryRegistry;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.alerting.RuleManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.pojo.PojoRepository;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.impl.DataMovementManagerImpl;

public class DatabaseClientImpl implements DatabaseClient {
  static final private Logger logger = LoggerFactory.getLogger(DatabaseClientImpl.class);

  static private long serverVersion = Long.parseUnsignedLong("9000000");

  private final RESTServices          services;
  private final String                host;
  private final int                   port;
  private final String basePath;
  private final String                database;
  private final SecurityContext       securityContext;
  private final ConnectionType        connectionType;

  private HandleFactoryRegistry handleRegistry;

  public DatabaseClientImpl(RESTServices services, String host, int port, String basePath, String database,
                            SecurityContext securityContext, ConnectionType connectionType) {
    connectionType = (connectionType == null) ? DatabaseClient.ConnectionType.DIRECT : connectionType;

    this.services = services;
    this.host     = host;
    this.port     = port;
	this.basePath = basePath;
    this.database = database;
    this.securityContext = securityContext;
    this.connectionType  = connectionType;

    services.setDatabaseClient(this);
  }

  public long getServerVersion() {
    // no locking because duplicate concurrent setting would end up with the same value
    if (serverVersion == Long.parseUnsignedLong("9000000")) {
      try {
        String versionStr = getServices()
                .getResource(null, "internal/effective-version", null, null, new StringHandle())
                .get();
        if (versionStr != null && versionStr.length() > 0) {
          long version = Long.parseUnsignedLong(versionStr);
          if (serverVersion != version) {
            serverVersion = version;
          }
        }
      } catch(Throwable e) {
        if (serverVersion == Long.parseUnsignedLong("9000000")) {
          serverVersion = Long.parseUnsignedLong("9000001");
        }
      }
    }
    return serverVersion;
  }

  @Override
  public ConnectionType getConnectionType() {
    return connectionType;
  }

  public HandleFactoryRegistry getHandleRegistry() {
    return handleRegistry;
  }
  public void setHandleRegistry(HandleFactoryRegistry handleRegistry) {
    this.handleRegistry = handleRegistry;
  }

  @Override
  public Transaction openTransaction() throws ForbiddenUserException, FailedRequestException {
    return services.openTransaction(null, TransactionImpl.DEFAULT_TIMELIMIT);
  }

  @Override
  public Transaction openTransaction(String name) throws ForbiddenUserException, FailedRequestException {
    return services.openTransaction(name, TransactionImpl.DEFAULT_TIMELIMIT);
  }

  @Override
  public Transaction openTransaction(String name, int timeLimit) throws ForbiddenUserException, FailedRequestException{
    return services.openTransaction(name, timeLimit);
  }

  @Override
  public GenericDocumentManager newDocumentManager() {
    GenericDocumentImpl docMgr = new GenericDocumentImpl(services);
    docMgr.setHandleRegistry(getHandleRegistry());
    return docMgr;
  }
  @Override
  public BinaryDocumentManager newBinaryDocumentManager() {
    BinaryDocumentImpl docMgr = new BinaryDocumentImpl(services);
    docMgr.setHandleRegistry(getHandleRegistry());
    return docMgr;
  }
  @Override
  public JSONDocumentManager newJSONDocumentManager() {
    JSONDocumentImpl docMgr = new JSONDocumentImpl(services);
    docMgr.setHandleRegistry(getHandleRegistry());
    return docMgr;
  }
  @Override
  public TextDocumentManager newTextDocumentManager() {
    TextDocumentImpl docMgr = new TextDocumentImpl(services);
    docMgr.setHandleRegistry(getHandleRegistry());
    return docMgr;
  }
  @Override
  public XMLDocumentManager newXMLDocumentManager() {
    XMLDocumentImpl docMgr = new XMLDocumentImpl(services);
    docMgr.setHandleRegistry(getHandleRegistry());
    return docMgr;
  }

  @Override
  public RuleManager newRuleManager() {
    RuleManagerImpl ruleMgr = new RuleManagerImpl(services);
    ruleMgr.setHandleRegistry(getHandleRegistry());
    return ruleMgr;
  }
  @Override
  public QueryManager newQueryManager() {
    QueryManagerImpl queryMgr = new QueryManagerImpl(services);
    queryMgr.setHandleRegistry(getHandleRegistry());
    return queryMgr;
  }
  @Override
  public DataMovementManager newDataMovementManager() {
    return new DataMovementManagerImpl(this);
  }
  @Override
  public RowManager newRowManager() {
    RowManagerImpl rowMgr = new RowManagerImpl(services);
    rowMgr.setHandleRegistry(getHandleRegistry());
    return rowMgr;
  }
  @Override
  public ServerConfigurationManager newServerConfigManager() {
    ServerConfigurationManagerImpl configMgr =
      new ServerConfigurationManagerImpl(services);
    configMgr.setHandleRegistry(getHandleRegistry());
    return configMgr;
  }
  @Override
  public <T, ID extends Serializable> PojoRepository<T, ID> newPojoRepository(Class<T> clazz, Class<ID> idClass) {
    return new PojoRepositoryImpl<>(this, clazz, idClass);
  }

  @Override
  public RequestLogger newLogger(OutputStream out) {
    return new RequestLoggerImpl(out);
  }

  @Override
  public <T extends ResourceManager> T init(String resourceName, T resourceManager) {
    if (resourceManager == null)
      throw new IllegalArgumentException("Cannot initialize null resource manager");
    if (resourceName == null)
      throw new IllegalArgumentException("Cannot initialize resource manager with null resource name");
    if (resourceName.length() == 0)
      throw new IllegalArgumentException("Cannot initialize resource manager with empty resource name");

    ((ResourceManagerImplementation) resourceManager).init(
      new ResourceServicesImpl(services,resourceName)
    );

    return resourceManager;
  }

  @Override
  public void release() {
    if (logger.isInfoEnabled())
      logger.info("Releasing connection");

    if (services != null)
      services.release();
  }

  @Override
  protected void finalize() throws Throwable {
    release();
    super.finalize();
  }

  @Override
  public Object getClientImplementation() {
    if (services == null)
      return null;
    return services.getClientImplementation();
  }

  // undocumented backdoor access to JerseyServices
  public RESTServices getServices() {
    return services;
  }

  @Override
  public ServerEvaluationCall newServerEval() {
    return new ServerEvaluationCallImpl(services, getHandleRegistry());
  }

  @Override
  public GraphManager newGraphManager() {
    return new GraphManagerImpl<>(services, getHandleRegistry());
  }

  @Override
  public SPARQLQueryManager newSPARQLQueryManager() {
    // TODO Auto-generated method stub
    return new SPARQLQueryManagerImpl(services);
  }

  @Override
  public String getHost() {
    return host;
  }

  @Override
  public int getPort() {
    return port;
  }

  @Override
  public String getDatabase() {
    return database;
  }

  @Override
  public String getBasePath() {
	  return this.basePath;
  }

  @Override
  public SecurityContext getSecurityContext() {
    return securityContext;
  }

  @Override
  public ConnectionResult checkConnection() {
	return services.checkConnection();
  }
}
