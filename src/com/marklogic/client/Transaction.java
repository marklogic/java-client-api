package com.marklogic.client;

public interface Transaction {
	public void commit();
    public void rollback();
}
