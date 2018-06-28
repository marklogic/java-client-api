package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.


import java.util.stream.Stream;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractWriteHandle;





import java.util.stream.Stream;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractWriteHandle;

import com.marklogic.client.SessionState;



import com.marklogic.client.DatabaseClient;

import com.marklogic.client.impl.AbstractProxy;

/**
 * Provides a set of operations on the database server
 */
public class SessionsBundle extends AbstractProxy {

    public static SessionsBundle on(DatabaseClient db) {
        return new SessionsBundle(db);
    }

    public SessionsBundle(DatabaseClient db) {
        super(db, "/dbf/test/sessions/");
    }
    public SessionState newSessionState() {
      return newSessionStateImpl();
    }

  /**
   * Invokes the setSessionFieldNoSession operation on the database server
   *
   * @param fieldName	provides input
   * 
   */
    public void setSessionFieldNoSession(String fieldName) {
      request("setSessionFieldNoSession.sjs", ParameterValuesKind.SINGLE_ATOMIC)
        .withSession()
        .withParams(
        atomicParam("fieldName", false, StringType.fromString(fieldName)))
        .withMethod("post")
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
      return UnsignedLongType.toLong(
        request("setSessionField.sjs", ParameterValuesKind.SINGLE_ATOMIC)
        .withSession("timestamper", timestamper, false)
        .withParams(
        atomicParam("fieldName", false, StringType.fromString(fieldName)))
        .withMethod("post")
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
      request("beginTransactionNoSession.sjs", ParameterValuesKind.MULTIPLE_ATOMICS)
        .withSession()
        .withParams(
        atomicParam("uri", false, StringType.fromString(uri)),
        atomicParam("text", false, StringType.fromString(text)))
        .withMethod("post")
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
      return BooleanType.toBoolean(
        request("sleepify.sjs", ParameterValuesKind.SINGLE_ATOMIC)
        .withSession("sleeper", sleeper, false)
        .withParams(
        atomicParam("sleeptime", false, UnsignedIntegerType.fromInteger(sleeptime)))
        .withMethod("post")
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
      return BooleanType.toBoolean(
        request("checkTransaction.sjs", ParameterValuesKind.SINGLE_ATOMIC)
        .withSession("transaction", transaction, true)
        .withParams(
        atomicParam("uri", false, StringType.fromString(uri)))
        .withMethod("post")
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
      request("rollbackTransaction.sjs", ParameterValuesKind.NONE)
        .withSession("transaction", transaction, false)
        .withParams(
        )
        .withMethod("post")
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
      return BooleanType.toBoolean(
        request("getSessionField.sjs", ParameterValuesKind.MULTIPLE_ATOMICS)
        .withSession("timestamper", timestamper, false)
        .withParams(
        atomicParam("fieldName", false, StringType.fromString(fieldName)),
        atomicParam("fieldValue", false, UnsignedLongType.fromLong(fieldValue)))
        .withMethod("post")
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
      request("beginTransaction.sjs", ParameterValuesKind.MULTIPLE_ATOMICS)
        .withSession("transaction", transaction, false)
        .withParams(
        atomicParam("uri", false, StringType.fromString(uri)),
        atomicParam("text", false, StringType.fromString(text)))
        .withMethod("post")
        .responseNone();
    }

}
