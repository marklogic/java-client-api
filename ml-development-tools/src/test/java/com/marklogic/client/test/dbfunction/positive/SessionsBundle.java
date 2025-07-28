package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.

import com.marklogic.client.SessionState;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

import com.marklogic.client.impl.BaseProxy;

/**
 * Provides a set of operations on the database server
 */
public interface SessionsBundle {
    /**
     * Creates a SessionsBundle object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for executing database operations
     */
    static SessionsBundle on(DatabaseClient db) {
      return on(db, null);
    }
    /**
     * Creates a SessionsBundle object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * The service declaration uses a custom implementation of the same service instead
     * of the default implementation of the service by specifying an endpoint directory
     * in the modules database with the implementation. A service.json file with the
     * declaration can be read with FileHandle or a string serialization of the JSON
     * declaration with StringHandle.
     *
     * @param db	provides a client for communicating with the database server
     * @param serviceDeclaration	substitutes a custom implementation of the service
     * @return	an object for executing database operations
     */
    static SessionsBundle on(DatabaseClient db, JSONWriteHandle serviceDeclaration) {
        final class SessionsBundleImpl implements SessionsBundle {
            private DatabaseClient dbClient;
            private BaseProxy baseProxy;

            private BaseProxy.DBFunctionRequest req_beginTransaction;
            private BaseProxy.DBFunctionRequest req_getSessionField;
            private BaseProxy.DBFunctionRequest req_checkTransaction;
            private BaseProxy.DBFunctionRequest req_rollbackTransaction;
            private BaseProxy.DBFunctionRequest req_sleepify;
            private BaseProxy.DBFunctionRequest req_setSessionField;
            private BaseProxy.DBFunctionRequest req_beginTransactionNoSession;
            private BaseProxy.DBFunctionRequest req_setSessionFieldNoSession;

            private SessionsBundleImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                this.dbClient  = dbClient;
                this.baseProxy = new BaseProxy("/dbf/test/sessions/", servDecl);

                this.req_beginTransaction = this.baseProxy.request(
                    "beginTransaction.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS);
                this.req_getSessionField = this.baseProxy.request(
                    "getSessionField.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS);
                this.req_checkTransaction = this.baseProxy.request(
                    "checkTransaction.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC);
                this.req_rollbackTransaction = this.baseProxy.request(
                    "rollbackTransaction.sjs", BaseProxy.ParameterValuesKind.NONE);
                this.req_sleepify = this.baseProxy.request(
                    "sleepify.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC);
                this.req_setSessionField = this.baseProxy.request(
                    "setSessionField.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC);
                this.req_beginTransactionNoSession = this.baseProxy.request(
                    "beginTransactionNoSession.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS);
                this.req_setSessionFieldNoSession = this.baseProxy.request(
                    "setSessionFieldNoSession.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC);
            }
            @Override
            public SessionState newSessionState() {
              return baseProxy.newSessionState();
            }

            @Override
            public void beginTransaction(SessionState transaction, String uri, String text) {
                beginTransaction(
                    this.req_beginTransaction.on(this.dbClient), transaction, uri, text
                    );
            }
            private void beginTransaction(BaseProxy.DBFunctionRequest request, SessionState transaction, String uri, String text) {
              request
                      .withSession("transaction", transaction, false)
                      .withParams(
                          BaseProxy.atomicParam("uri", false, BaseProxy.StringType.fromString(uri)),
                          BaseProxy.atomicParam("text", false, BaseProxy.StringType.fromString(text))
                          ).responseNone();
            }

            @Override
            public Boolean getSessionField(SessionState timestamper, String fieldName, Long fieldValue) {
                return getSessionField(
                    this.req_getSessionField.on(this.dbClient), timestamper, fieldName, fieldValue
                    );
            }
            private Boolean getSessionField(BaseProxy.DBFunctionRequest request, SessionState timestamper, String fieldName, Long fieldValue) {
              return BaseProxy.BooleanType.toBoolean(
                request
                      .withSession("timestamper", timestamper, false)
                      .withParams(
                          BaseProxy.atomicParam("fieldName", false, BaseProxy.StringType.fromString(fieldName)),
                          BaseProxy.atomicParam("fieldValue", false, BaseProxy.UnsignedLongType.fromLong(fieldValue))
                          ).responseSingle(false, null)
                );
            }

            @Override
            public Boolean checkTransaction(SessionState transaction, String uri) {
                return checkTransaction(
                    this.req_checkTransaction.on(this.dbClient), transaction, uri
                    );
            }
            private Boolean checkTransaction(BaseProxy.DBFunctionRequest request, SessionState transaction, String uri) {
              return BaseProxy.BooleanType.toBoolean(
                request
                      .withSession("transaction", transaction, true)
                      .withParams(
                          BaseProxy.atomicParam("uri", false, BaseProxy.StringType.fromString(uri))
                          ).responseSingle(false, null)
                );
            }

            @Override
            public void rollbackTransaction(SessionState transaction) {
                rollbackTransaction(
                    this.req_rollbackTransaction.on(this.dbClient), transaction
                    );
            }
            private void rollbackTransaction(BaseProxy.DBFunctionRequest request, SessionState transaction) {
              request
                      .withSession("transaction", transaction, false).responseNone();
            }

            @Override
            public Boolean sleepify(SessionState sleeper, Integer sleeptime) {
                return sleepify(
                    this.req_sleepify.on(this.dbClient), sleeper, sleeptime
                    );
            }
            private Boolean sleepify(BaseProxy.DBFunctionRequest request, SessionState sleeper, Integer sleeptime) {
              return BaseProxy.BooleanType.toBoolean(
                request
                      .withSession("sleeper", sleeper, false)
                      .withParams(
                          BaseProxy.atomicParam("sleeptime", false, BaseProxy.UnsignedIntegerType.fromInteger(sleeptime))
                          ).responseSingle(false, null)
                );
            }

            @Override
            public Long setSessionField(SessionState timestamper, String fieldName) {
                return setSessionField(
                    this.req_setSessionField.on(this.dbClient), timestamper, fieldName
                    );
            }
            private Long setSessionField(BaseProxy.DBFunctionRequest request, SessionState timestamper, String fieldName) {
              return BaseProxy.UnsignedLongType.toLong(
                request
                      .withSession("timestamper", timestamper, false)
                      .withParams(
                          BaseProxy.atomicParam("fieldName", false, BaseProxy.StringType.fromString(fieldName))
                          ).responseSingle(false, null)
                );
            }

            @Override
            public void beginTransactionNoSession(String uri, String text) {
                beginTransactionNoSession(
                    this.req_beginTransactionNoSession.on(this.dbClient), uri, text
                    );
            }
            private void beginTransactionNoSession(BaseProxy.DBFunctionRequest request, String uri, String text) {
              request
                      .withParams(
                          BaseProxy.atomicParam("uri", false, BaseProxy.StringType.fromString(uri)),
                          BaseProxy.atomicParam("text", false, BaseProxy.StringType.fromString(text))
                          ).responseNone();
            }

            @Override
            public void setSessionFieldNoSession(String fieldName) {
                setSessionFieldNoSession(
                    this.req_setSessionFieldNoSession.on(this.dbClient), fieldName
                    );
            }
            private void setSessionFieldNoSession(BaseProxy.DBFunctionRequest request, String fieldName) {
              request
                      .withParams(
                          BaseProxy.atomicParam("fieldName", false, BaseProxy.StringType.fromString(fieldName))
                          ).responseNone();
            }
        }

        return new SessionsBundleImpl(db, serviceDeclaration);
    }
    /**
     * Creates an object to track a session for a set of operations
     * that require session state on the database server.
     *
     * @return	an object for session state
     */
    SessionState newSessionState();

  /**
   * Invokes the beginTransaction operation on the database server
   *
   * @param transaction	provides input
   * @param uri	provides input
   * @param text	provides input
   * 
   */
    void beginTransaction(SessionState transaction, String uri, String text);

  /**
   * Invokes the getSessionField operation on the database server
   *
   * @param timestamper	provides input
   * @param fieldName	provides input
   * @param fieldValue	provides input
   * @return	as output
   */
    Boolean getSessionField(SessionState timestamper, String fieldName, Long fieldValue);

  /**
   * Invokes the checkTransaction operation on the database server
   *
   * @param transaction	provides input
   * @param uri	provides input
   * @return	as output
   */
    Boolean checkTransaction(SessionState transaction, String uri);

  /**
   * Invokes the rollbackTransaction operation on the database server
   *
   * @param transaction	provides input
   * 
   */
    void rollbackTransaction(SessionState transaction);

  /**
   * Invokes the sleepify operation on the database server
   *
   * @param sleeper	provides input
   * @param sleeptime	provides input
   * @return	as output
   */
    Boolean sleepify(SessionState sleeper, Integer sleeptime);

  /**
   * Invokes the setSessionField operation on the database server
   *
   * @param timestamper	provides input
   * @param fieldName	provides input
   * @return	as output
   */
    Long setSessionField(SessionState timestamper, String fieldName);

  /**
   * Invokes the beginTransactionNoSession operation on the database server
   *
   * @param uri	provides input
   * @param text	provides input
   * 
   */
    void beginTransactionNoSession(String uri, String text);

  /**
   * Invokes the setSessionFieldNoSession operation on the database server
   *
   * @param fieldName	provides input
   * 
   */
    void setSessionFieldNoSession(String fieldName);

}
