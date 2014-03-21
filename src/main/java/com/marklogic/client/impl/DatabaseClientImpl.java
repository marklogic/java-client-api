/*
 * Copyright 2012-2014 MarkLogic Corporation
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
package com.marklogic.client.impl;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.document.BinaryDocumentManager;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.XMLDocumentManager;

public class DatabaseClientImpl implements DatabaseClient {
	static final private Logger logger = LoggerFactory.getLogger(DatabaseClientImpl.class);

	private RESTServices services;

	public DatabaseClientImpl(RESTServices services) {
		this.services = services;
		services.setDatabaseClient(this);
	}

	@Override
	public Transaction openTransaction() throws ForbiddenUserException, FailedRequestException {
		return new TransactionImpl(services, services.openTransaction(null, TransactionImpl.DEFAULT_TIMELIMIT));
	}

	@Override
	public Transaction openTransaction(String name) throws ForbiddenUserException, FailedRequestException {
		return new TransactionImpl(services, services.openTransaction(name, TransactionImpl.DEFAULT_TIMELIMIT));
	}

	@Override
	public Transaction openTransaction(String name, int timeLimit) throws ForbiddenUserException, FailedRequestException{
		return new TransactionImpl(services, services.openTransaction(name, timeLimit));
	}

	@Override
	public GenericDocumentManager newDocumentManager() {
		return new GenericDocumentImpl(services);
	}
	@Override
	public BinaryDocumentManager newBinaryDocumentManager() {
		return new BinaryDocumentImpl(services);
	}
	@Override
	public JSONDocumentManager newJSONDocumentManager() {
		return new JSONDocumentImpl(services);
	}
	@Override
	public TextDocumentManager newTextDocumentManager() {
		return new TextDocumentImpl(services);
	}
	@Override
	public XMLDocumentManager newXMLDocumentManager() {
		return new XMLDocumentImpl(services);
	}

	@Override
	public RequestLogger newLogger(OutputStream out) {
		return new RequestLoggerImpl(out);
	}

	@Override
	public QueryManager newQueryManager() {
        return new QueryManagerImpl(services);
	}

	@Override
	public ServerConfigurationManager newServerConfigManager() {
		return new ServerConfigurationManagerImpl(services);
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
}
