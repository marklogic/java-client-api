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
            private BaseProxy baseProxy;

            private SessionsBundleImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                baseProxy = new BaseProxy(dbClient, "/dbf/test/sessions/", servDecl);
            }
            @Override
            public SessionState newSessionState() {
              return baseProxy.newSessionState();
            }

            @Override
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


            @Override
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


            @Override
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


            @Override
            public void rollbackTransaction(SessionState transaction) {
              baseProxy
                .request("rollbackTransaction.sjs", BaseProxy.ParameterValuesKind.NONE)
                .withSession("transaction", transaction, false)
                .withParams(
                    )
                .withMethod("POST")
                .responseNone();
            }


            @Override
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


            @Override
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


            @Override
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


            @Override
            public void setSessionFieldNoSession(String fieldName) {
              baseProxy
                .request("setSessionFieldNoSession.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC)
                .withSession()
                .withParams(
                    BaseProxy.atomicParam("fieldName", false, BaseProxy.StringType.fromString(fieldName)))
                .withMethod("POST")
                .responseNone();
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
   * Invokes the beginTransactionNoSession operation on the database server
   *
   * @param uri	provides input
   * @param text	provides input
   * 
   */
    void beginTransactionNoSession(String uri, String text);

  /**
   * Invokes the checkTransaction operation on the database server
   *
   * @param transaction	provides input
   * @param uri	provides input
   * @return	as output
   */
    Boolean checkTransaction(SessionState transaction, String uri);

  /**
   * Invokes the sleepify operation on the database server
   *
   * @param sleeper	provides input
   * @param sleeptime	provides input
   * @return	as output
   */
    Boolean sleepify(SessionState sleeper, Integer sleeptime);

  /**
   * Invokes the rollbackTransaction operation on the database server
   *
   * @param transaction	provides input
   * 
   */
    void rollbackTransaction(SessionState transaction);

  /**
   * Invokes the setSessionField operation on the database server
   *
   * @param timestamper	provides input
   * @param fieldName	provides input
   * @return	as output
   */
    Long setSessionField(SessionState timestamper, String fieldName);

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
   * Invokes the beginTransaction operation on the database server
   *
   * @param transaction	provides input
   * @param uri	provides input
   * @param text	provides input
   * 
   */
    void beginTransaction(SessionState transaction, String uri, String text);

  /**
   * Invokes the setSessionFieldNoSession operation on the database server
   *
   * @param fieldName	provides input
   * 
   */
    void setSessionFieldNoSession(String fieldName);

}
