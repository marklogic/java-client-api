package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.



import com.marklogic.client.DatabaseClient;

import com.marklogic.client.impl.BaseProxy;

/**
 * A most descriptive class.
 */
public class DescribedBundle {
    private BaseProxy baseProxy;

    /**
     * Creates a DescribedBundle object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for session state
     */
    public static DescribedBundle on(DatabaseClient db) {
        return new DescribedBundle(db);
    }

    /**
     * The constructor for a DescribedBundle object for executing operations on the database server.
     * @param db	provides a client for communicating with the database server
     */
    public DescribedBundle(DatabaseClient db) {
        baseProxy = new BaseProxy(db, "/dbf/test/described/");
    }

  /**
   * A most descriptive method.
   *
   * @param first	Descriptive input.
   * @return	Descriptive output.
   */
    public Boolean describer(Integer first) {
      return BaseProxy.BooleanType.toBoolean(
        baseProxy
        .request("describer.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC)
        .withSession()
        .withParams(
        BaseProxy.atomicParam("first", false, BaseProxy.IntegerType.fromInteger(first)))
        .withMethod("POST")
        .responseSingle(false, null)
        );
    }

}
