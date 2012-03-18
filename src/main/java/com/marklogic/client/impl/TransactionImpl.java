package com.marklogic.client.impl;

import com.marklogic.client.Transaction;

class TransactionImpl implements Transaction {
	private RESTServices services;

	TransactionImpl(RESTServices services, String transactionId) {
		this.services = services;
		this.transactionId = transactionId;
	}

	private String transactionId;
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public void commit() {
		services.commitTransaction(getTransactionId());
	}

	public void rollback() {
		services.rollbackTransaction(getTransactionId());
	}

}
