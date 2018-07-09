package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.


import java.util.stream.Stream;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractWriteHandle;





import com.marklogic.client.DatabaseClient;

import com.marklogic.client.impl.BaseProxy;

/**
 * A most descriptive class.
 */
public class DescribedBundle {
    private BaseProxy baseProxy;

    public static DescribedBundle on(DatabaseClient db) {
        return new DescribedBundle(db);
    }

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
