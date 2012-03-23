package com.marklogic.client;

public interface Transaction {
	public String getTransactionId();
	public void commit() throws ForbiddenUserException, FailedRequestException;
    public void rollback() throws ForbiddenUserException, FailedRequestException;
}
