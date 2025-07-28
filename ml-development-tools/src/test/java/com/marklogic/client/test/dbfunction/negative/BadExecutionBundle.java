package com.marklogic.client.test.dbfunction.negative;

// IMPORTANT: Do not edit. This file is generated.

import com.marklogic.client.io.Format;
import java.io.Reader;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

import com.marklogic.client.impl.BaseProxy;

/**
 * Provides a set of operations on the database server
 */
public interface BadExecutionBundle {
    /**
     * Creates a BadExecutionBundle object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for executing database operations
     */
    static BadExecutionBundle on(DatabaseClient db) {
      return on(db, null);
    }
    /**
     * Creates a BadExecutionBundle object for executing operations on the database server.
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
    static BadExecutionBundle on(DatabaseClient db, JSONWriteHandle serviceDeclaration) {
        final class BadExecutionBundleImpl implements BadExecutionBundle {
            private DatabaseClient dbClient;
            private BaseProxy baseProxy;

            private BaseProxy.DBFunctionRequest req_errorMapping;

            private BadExecutionBundleImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                this.dbClient  = dbClient;
                this.baseProxy = new BaseProxy("/dbf/test/badExecution/", servDecl);

                this.req_errorMapping = this.baseProxy.request(
                    "errorMapping.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS);
            }

            @Override
            public Reader errorMapping(String errCode, String errMsg) {
                return errorMapping(
                    this.req_errorMapping.on(this.dbClient), errCode, errMsg
                    );
            }
            private Reader errorMapping(BaseProxy.DBFunctionRequest request, String errCode, String errMsg) {
              return BaseProxy.JsonDocumentType.toReader(
                request
                      .withParams(
                          BaseProxy.atomicParam("errCode", false, BaseProxy.StringType.fromString(errCode)),
                          BaseProxy.atomicParam("errMsg", true, BaseProxy.StringType.fromString(errMsg))
                          ).responseSingle(true, Format.JSON)
                );
            }
        }

        return new BadExecutionBundleImpl(db, serviceDeclaration);
    }

  /**
   * Invokes the errorMapping operation on the database server
   *
   * @param errCode	provides input
   * @param errMsg	provides input
   * @return	as output
   */
    Reader errorMapping(String errCode, String errMsg);

}
