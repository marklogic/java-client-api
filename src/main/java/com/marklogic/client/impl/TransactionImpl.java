/*
 * Copyright 2012-2015 MarkLogic Corporation
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

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.Transaction;
import com.marklogic.client.io.marker.StructureReadHandle;

class TransactionImpl implements Transaction {
	final static int DEFAULT_TIMELIMIT = -1;

	private RESTServices services;
	private String       transactionId;

	TransactionImpl(RESTServices services, String transactionId) {
		this.services = services;
		this.transactionId = transactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends StructureReadHandle> T readStatus(T handle) throws ForbiddenUserException, FailedRequestException {
		if (handle == null)
			throw new IllegalArgumentException("reading transaction status with null handle");

		HandleImplementation handleBase = HandleAccessor.checkHandle(handle, "structure");

		handleBase.receiveContent(
				services.getValue(
						null,
						"transactions",
						getTransactionId(),
						false,
						handleBase.getMimetype(),
						handleBase.receiveAs()
						)
				);

		return handle;
	}

	@Override
	public void commit() throws ForbiddenUserException, FailedRequestException {
		services.commitTransaction(getTransactionId());
	}

	@Override
	public void rollback() throws ForbiddenUserException, FailedRequestException {
		services.rollbackTransaction(getTransactionId());
	}

}
