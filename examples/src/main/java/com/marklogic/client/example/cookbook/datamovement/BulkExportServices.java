/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook.datamovement;

// IMPORTANT: Do not edit. This file is generated.

import java.util.stream.Stream;
import com.marklogic.client.io.Format;
import java.io.Reader;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

import com.marklogic.client.impl.BaseProxy;

/**
 * Provides a set of operations on the database server
 */
public interface BulkExportServices {
    /**
     * Creates a BulkExportServices object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for executing database operations
     */
    static BulkExportServices on(DatabaseClient db) {
      return on(db, null);
    }
    /**
     * Creates a BulkExportServices object for executing operations on the database server.
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
    static BulkExportServices on(DatabaseClient db, JSONWriteHandle serviceDeclaration) {
        final class BulkExportServicesImpl implements BulkExportServices {
            private DatabaseClient dbClient;
            private BaseProxy baseProxy;

            private BaseProxy.DBFunctionRequest req_readJsonDocs;

            private BulkExportServicesImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                this.dbClient  = dbClient;
                this.baseProxy = new BaseProxy("/example/cookbook/bulkExport/", servDecl);

                this.req_readJsonDocs = this.baseProxy.request(
                    "readJsonDocs.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS);
            }

            @Override
            public Stream<Reader> readJsonDocs(Stream<String> uris) {
                return readJsonDocs(
                    this.req_readJsonDocs.on(this.dbClient), uris
                    );
            }
            private Stream<Reader> readJsonDocs(BaseProxy.DBFunctionRequest request, Stream<String> uris) {
              return BaseProxy.JsonDocumentType.toReader(
                request
                      .withParams(
                          BaseProxy.atomicParam("uris", false, BaseProxy.StringType.fromString(uris))
                          ).responseMultiple(true, Format.JSON)
                );
            }
        }

        return new BulkExportServicesImpl(db, serviceDeclaration);
    }

  /**
   * Invokes the readJsonDocs operation on the database server
   *
   * @param uris	provides input
   * @return	as output
   */
    Stream<Reader> readJsonDocs(Stream<String> uris);

}
