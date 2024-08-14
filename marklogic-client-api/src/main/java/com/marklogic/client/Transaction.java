/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client;

import com.marklogic.client.impl.ClientCookie;
import com.marklogic.client.io.marker.StructureReadHandle;

import java.util.List;

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
  String getTransactionId();

  /**
   * Returns the host identifier for the transaction that binds this
   * transaction with the host e-node for the transaction.  Ordinarily, you
   * will not need to do anything with the host id.  Instead, you pass the
   * Transaction object to methods.
   * @return	the host identifier
   */
  String getHostId();

  /**
   * Returns any cookies sent in the response to open the transaction.  This is
   * specifically to support cookies used by a load balancer to keep all
   * requests associated with a transaction on the host where that transaction
   * originated.
   * @return	the cookies sent in the response to open the transaction
   */
  List<ClientCookie> getCookies();

  /**
   * Reads the status for the transaction including whether the transaction
   * has timed out.
   * @param handle	a JSON or XML handle on the content of the status report
   * @param <T> the type of StructureReadHandle handle to return
   * @return	the status report handle
   */
  <T extends StructureReadHandle> T readStatus(T handle) throws ForbiddenUserException, FailedRequestException;

  /**
   * Completes the transaction, making writes and deletes visible to other database clients.
   *
   * To call commit(), an application must authenticate as rest-writer or rest-admin.
   */
  void commit() throws ForbiddenUserException, FailedRequestException;
  /**
   * Cancels the transaction, reverting the database to its state prior to the writes and deletes
   * made in the transaction.
   *
   * To call rollback(), an application must authenticate as rest-writer or rest-admin.
   */
  void rollback() throws ForbiddenUserException, FailedRequestException;
}
