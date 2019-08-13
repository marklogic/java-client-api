package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.



import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

import com.marklogic.client.impl.BaseProxy;

/**
 * A most descriptive class.
 */
public interface DescribedBundle {
    /**
     * Creates a DescribedBundle object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for executing database operations
     */
    static DescribedBundle on(DatabaseClient db) {
      return on(db, null);
    }
    /**
     * Creates a DescribedBundle object for executing operations on the database server.
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
    static DescribedBundle on(DatabaseClient db, JSONWriteHandle serviceDeclaration) {
        final class DescribedBundleImpl implements DescribedBundle {
            private DatabaseClient dbClient;
            private BaseProxy baseProxy;

            private BaseProxy.DBFunctionRequest req_describer;

            private DescribedBundleImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                this.dbClient  = dbClient;
                this.baseProxy = new BaseProxy("/dbf/test/described/", servDecl);

                this.req_describer = this.baseProxy.request(
                    "describer.sjs", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC);
            }

            @Override
            public Boolean describer(Integer first) {
                return describer(
                    this.req_describer.on(this.dbClient), first
                    );
            }
            private Boolean describer(BaseProxy.DBFunctionRequest request, Integer first) {
              return BaseProxy.BooleanType.toBoolean(
                request
                      .withParams(
                          BaseProxy.atomicParam("first", false, BaseProxy.IntegerType.fromInteger(first))
                          ).responseSingle(false, null)
                );
            }
        }

        return new DescribedBundleImpl(db, serviceDeclaration);
    }

  /**
   * A most descriptive method.
   *
   * @param first	Descriptive input.
   * @return	Descriptive output.
   */
    Boolean describer(Integer first);

}
