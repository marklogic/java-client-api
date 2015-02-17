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
package com.marklogic.client;

import com.marklogic.client.io.marker.StructureReadHandle;

/**
 * Identifies and provides methods to complete a transaction.
 * 
 * To use Transaction, an application must authenticate as rest-writer or rest-admin.
 */
public interface Transaction {
	/**
	 * Returns the identifier for the transaction.  Ordinarily, you
	 * don't need to get the transaction id.  Instead, you pass the
	 * Transaction object to methods.
	 * @return	the transaction identifier
	 */
	public String getTransactionId();

	/**
	 * Reads the status for the transaction including whether the transaction
	 * has timed out.
	 * @param handle	a JSON or XML handle on the content of the status report
	 * @return	the status report handle
	 */
	public <T extends StructureReadHandle> T readStatus(T handle) throws ForbiddenUserException, FailedRequestException;

	/**
	 * Completes the transaction, making writes and deletes visible to other database clients.
	 * 
     * To call commit(), an application must authenticate as rest-writer or rest-admin.
	 */
	public void commit() throws ForbiddenUserException, FailedRequestException;
	/**
	 * Cancels the transaction, reverting the database to its state prior to the writes and deletes
	 * made in the transaction.
	 * 
     * To call rollback(), an application must authenticate as rest-writer or rest-admin.
	 */
    public void rollback() throws ForbiddenUserException, FailedRequestException;
}
