package com.marklogic.client;

public interface Transaction {
	public String getTransactionId();
	public void commit();
    public void rollback();
}
