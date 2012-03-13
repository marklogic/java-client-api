package com.marklogic.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.Transaction;

class TransactionImpl implements Transaction {
	static final private Logger logger = LoggerFactory.getLogger(TransactionImpl.class);

	TransactionImpl(String transactionId) {
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
		logger.info("Committing transaction {}",transactionId);

		// TODO Auto-generated method stub
	}

	public void rollback() {
		logger.info("Rolling back transaction {}",transactionId);

		// TODO Auto-generated method stub
	}

}
