package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.

import com.marklogic.client.SessionState;


import com.marklogic.client.DatabaseClient;

import com.marklogic.client.impl.BaseProxy;

/**
 * Provides a set of operations on the database server
 */
public class SessionsBundle {
    private BaseProxy baseProxy;

    /**
     * Creates a SessionsBundle object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for session state
     */
    public static SessionsBundle on(DatabaseClient db) {
        return new SessionsBundle(db);
    }

    /**
     * The constructor for a SessionsBundle object for executing operations on the database server.
     * @param db	provides a client for communicating with the database server
     */
    public SessionsBundle(DatabaseClient db) {
        baseProxy = new BaseProxy(db, "/dbf/test/sessions/");
    }
    /**
     * Creates an object to track a session for a set of operations
     * that require session state on the database server.
     *
     * @return	an object for session state
     */
    public SessionState newSessionState() {
      return baseProxy.newSessionState();
    }

  /**
   * Invokes the setSessionFieldNoSession operation on the database server
   *
   * @param fieldName	provides input
   * 
   */
    public void setSessionFieldNoSession(String fieldName) {
      baseProxy
        .request("setSessionFieldNoSession.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC)
        .withSession()
        .withParams(
        BaseProxy.atomicParam("fieldName", false, BaseProxy.StringType.fromString(fieldName)))
        .withMethod("POST")
        .responseNone();
    }


  /**
   * Invokes the setSessionField operation on the database server
   *
   * @param timestamper	provides input
   * @param fieldName	provides input
   * @return	as output
   */
    public Long setSessionField(SessionState timestamper, String fieldName) {
      return BaseProxy.UnsignedLongType.toLong(
        baseProxy
        .request("setSessionField.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC)
        .withSession("timestamper", timestamper, false)
        .withParams(
        BaseProxy.atomicParam("fieldName", false, BaseProxy.StringType.fromString(fieldName)))
        .withMethod("POST")
        .responseSingle(false, null)
        );
    }


  /**
   * Invokes the beginTransactionNoSession operation on the database server
   *
   * @param uri	provides input
   * @param text	provides input
   * 
   */
    public void beginTransactionNoSession(String uri, String text) {
      baseProxy
        .request("beginTransactionNoSession.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS)
        .withSession()
        .withParams(
        BaseProxy.atomicParam("uri", false, BaseProxy.StringType.fromString(uri)),
        BaseProxy.atomicParam("text", false, BaseProxy.StringType.fromString(text)))
        .withMethod("POST")
        .responseNone();
    }


  /**
   * Invokes the sleepify operation on the database server
   *
   * @param sleeper	provides input
   * @param sleeptime	provides input
   * @return	as output
   */
    public Boolean sleepify(SessionState sleeper, Integer sleeptime) {
      return BaseProxy.BooleanType.toBoolean(
        baseProxy
        .request("sleepify.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC)
        .withSession("sleeper", sleeper, false)
        .withParams(
        BaseProxy.atomicParam("sleeptime", false, BaseProxy.UnsignedIntegerType.fromInteger(sleeptime)))
        .withMethod("POST")
        .responseSingle(false, null)
        );
    }


  /**
   * Invokes the checkTransaction operation on the database server
   *
   * @param transaction	provides input
   * @param uri	provides input
   * @return	as output
   */
    public Boolean checkTransaction(SessionState transaction, String uri) {
      return BaseProxy.BooleanType.toBoolean(
        baseProxy
        .request("checkTransaction.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC)
        .withSession("transaction", transaction, true)
        .withParams(
        BaseProxy.atomicParam("uri", false, BaseProxy.StringType.fromString(uri)))
        .withMethod("POST")
        .responseSingle(false, null)
        );
    }


  /**
   * Invokes the rollbackTransaction operation on the database server
   *
   * @param transaction	provides input
   * 
   */
    public void rollbackTransaction(SessionState transaction) {
      baseProxy
        .request("rollbackTransaction.sjs", BaseProxy.ParameterValuesKind.NONE)
        .withSession("transaction", transaction, false)
        .withParams(
        )
        .withMethod("POST")
        .responseNone();
    }


  /**
   * Invokes the getSessionField operation on the database server
   *
   * @param timestamper	provides input
   * @param fieldName	provides input
   * @param fieldValue	provides input
   * @return	as output
   */
    public Boolean getSessionField(SessionState timestamper, String fieldName, Long fieldValue) {
      return BaseProxy.BooleanType.toBoolean(
        baseProxy
        .request("getSessionField.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS)
        .withSession("timestamper", timestamper, false)
        .withParams(
        BaseProxy.atomicParam("fieldName", false, BaseProxy.StringType.fromString(fieldName)),
        BaseProxy.atomicParam("fieldValue", false, BaseProxy.UnsignedLongType.fromLong(fieldValue)))
        .withMethod("POST")
        .responseSingle(false, null)
        );
    }


  /**
   * Invokes the beginTransaction operation on the database server
   *
   * @param transaction	provides input
   * @param uri	provides input
   * @param text	provides input
   * 
   */
    public void beginTransaction(SessionState transaction, String uri, String text) {
      baseProxy
        .request("beginTransaction.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS)
        .withSession("transaction", transaction, false)
        .withParams(
        BaseProxy.atomicParam("uri", false, BaseProxy.StringType.fromString(uri)),
        BaseProxy.atomicParam("text", false, BaseProxy.StringType.fromString(text)))
        .withMethod("POST")
        .responseNone();
    }

}
