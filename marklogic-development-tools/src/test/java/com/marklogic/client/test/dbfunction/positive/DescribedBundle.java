package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.


import java.util.stream.Stream;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractWriteHandle;





import com.marklogic.client.DatabaseClient;

import com.marklogic.client.impl.AbstractProxy;

/**
 * A most descriptive class.
 */
public class DescribedBundle extends AbstractProxy {

    public static DescribedBundle on(DatabaseClient db) {
        return new DescribedBundle(db);
    }

    public DescribedBundle(DatabaseClient db) {
        super(db, "/dbf/test/described/");
    }

  /**
   * A most descriptive method.
   *
   * @param first	Descriptive input.
   * @return	Descriptive output.
   */
    public Boolean describer(Integer first) {
      return BooleanType.toBoolean(
        request("describer.sjs", ParameterValuesKind.SINGLE_ATOMIC)
        .withSession()
        .withParams(
        atomicParam("first", false, IntegerType.fromInteger(first)))
        .withMethod("post")
        .responseSingle(false, null)
        );
    }

}
