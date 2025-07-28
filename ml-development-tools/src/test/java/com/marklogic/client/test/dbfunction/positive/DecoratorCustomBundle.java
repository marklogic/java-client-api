package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.

import com.marklogic.client.io.Format;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

import com.marklogic.client.impl.BaseProxy;

/**
 * Provides a set of operations on the database server
 */
public interface DecoratorCustomBundle {
    /**
     * Creates a DecoratorCustomBundle object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for executing database operations
     */
    static DecoratorCustomBundle on(DatabaseClient db) {
      return on(db, null);
    }
    /**
     * Creates a DecoratorCustomBundle object for executing operations on the database server.
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
    static DecoratorCustomBundle on(DatabaseClient db, JSONWriteHandle serviceDeclaration) {
        final class DecoratorCustomBundleImpl implements DecoratorCustomBundle {
            private DatabaseClient dbClient;
            private BaseProxy baseProxy;

            private BaseProxy.DBFunctionRequest req_docify;

            private DecoratorCustomBundleImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                this.dbClient  = dbClient;
                this.baseProxy = new BaseProxy("/dbf/test/decoratorCustom/", servDecl);

                this.req_docify = this.baseProxy.request(
                    "docify.xqy", BaseProxy.ParameterValuesKind.SINGLE_ATOMIC);
            }

            @Override
            public com.fasterxml.jackson.databind.JsonNode docify(String value) {
                return docify(
                    this.req_docify.on(this.dbClient), value
                    );
            }
            private com.fasterxml.jackson.databind.JsonNode docify(BaseProxy.DBFunctionRequest request, String value) {
              return BaseProxy.JsonDocumentType.toJsonNode(
                request
                      .withParams(
                          BaseProxy.atomicParam("value", true, BaseProxy.StringType.fromString(value))
                          ).responseSingle(true, Format.JSON)
                );
            }
        }

        return new DecoratorCustomBundleImpl(db, serviceDeclaration);
    }

  /**
   * Invokes the docify operation on the database server
   *
   * @param value	provides input
   * @return	as output
   */
    com.fasterxml.jackson.databind.JsonNode docify(String value);

}
