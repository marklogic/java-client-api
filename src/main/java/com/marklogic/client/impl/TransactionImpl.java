package com.marklogic.client.impl;

import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.Transaction;

class TransactionImpl implements Transaction {
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

	public void commit() throws ForbiddenUserException, FailedRequestException {
		services.commitTransaction(getTransactionId());
	}

	public void rollback() throws ForbiddenUserException, FailedRequestException {
		services.rollbackTransaction(getTransactionId());
	}

}
