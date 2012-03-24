package com.marklogic.client;

/**
 * Identifies and provides methods to complete a transaction.
 * 
 * To use Transaction, an application must authenticate as rest-writer or rest-admin.
 */
public interface Transaction {
	public String getTransactionId();

	/**
	 * Completes the transaction, making writes and deletes visible to other database clients.
	 * 
     * To call commit(), an application must authenticate as rest-writer or rest-admin.
     * 
	 * @throws ForbiddenUserException
	 * @throws FailedRequestException
	 */
	public void commit() throws ForbiddenUserException, FailedRequestException;
	/**
	 * Cancels the transaction, reverting the database to its state prior to the writes and deletes
	 * made in the transaction.
	 * 
     * To call rollback(), an application must authenticate as rest-writer or rest-admin.
     * 
	 * @throws ForbiddenUserException
	 * @throws FailedRequestException
	 */
    public void rollback() throws ForbiddenUserException, FailedRequestException;
}
