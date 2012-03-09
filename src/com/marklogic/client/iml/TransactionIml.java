package com.marklogic.client.iml;

import com.marklogic.client.Transaction;

public class TransactionIml implements Transaction {

	private String transactionId;
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public void commit() {
		// TODO Auto-generated method stub

	}

	public void rollback() {
		// TODO Auto-generated method stub

	}

}
