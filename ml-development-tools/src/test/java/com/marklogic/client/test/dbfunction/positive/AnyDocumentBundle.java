package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.

import java.util.stream.Stream;
import com.marklogic.client.io.Format;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.marker.JSONWriteHandle;

import com.marklogic.client.impl.BaseProxy;

/**
 * Provides a set of operations on the database server
 */
public interface AnyDocumentBundle {
    /**
     * Creates a AnyDocumentBundle object for executing operations on the database server.
     *
     * The DatabaseClientFactory class can create the DatabaseClient parameter. A single
     * client object can be used for any number of requests and in multiple threads.
     *
     * @param db	provides a client for communicating with the database server
     * @return	an object for executing database operations
     */
    static AnyDocumentBundle on(DatabaseClient db) {
      return on(db, null);
    }
    /**
     * Creates a AnyDocumentBundle object for executing operations on the database server.
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
    static AnyDocumentBundle on(DatabaseClient db, JSONWriteHandle serviceDeclaration) {
        final class AnyDocumentBundleImpl implements AnyDocumentBundle {
            private DatabaseClient dbClient;
            private BaseProxy baseProxy;

            private BaseProxy.DBFunctionRequest req_sendReceiveDocs;

            private AnyDocumentBundleImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                this.dbClient  = dbClient;
                this.baseProxy = new BaseProxy("/dbf/test/anyDocument/", servDecl);

                this.req_sendReceiveDocs = this.baseProxy.request(
                    "sendReceiveDocs.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_MIXED);
            }

            @Override
            public Stream<com.marklogic.client.io.InputStreamHandle> sendReceiveDocs(Stream<String> uris, Stream<com.marklogic.client.io.InputStreamHandle> docs) {
                return sendReceiveDocs(
                    this.req_sendReceiveDocs.on(this.dbClient), uris, docs
                    );
            }
            private Stream<com.marklogic.client.io.InputStreamHandle> sendReceiveDocs(BaseProxy.DBFunctionRequest request, Stream<String> uris, Stream<com.marklogic.client.io.InputStreamHandle> docs) {
/* TODO:
    generate code that
        instead of wrapping with BaseProxy.AnyDocumentType.toInputStreamHandle()
        calls .asStreamOfHandles()
 */
                return request
                        .withParams(
                                BaseProxy.atomicParam("uris", true, BaseProxy.StringType.fromString(uris)),
                                BaseProxy.documentParam("docs", true, docs)
                        ).responseMultiple(true, Format.UNKNOWN)
                        .asStreamOfHandles(null, new com.marklogic.client.io.InputStreamHandle());
            }
        }

        return new AnyDocumentBundleImpl(db, serviceDeclaration);
    }

  /**
   * Invokes the sendReceiveDocs operation on the database server
   *
   * @param uris	provides input
   * @param docs	provides input
   * @return	as output
   */
    Stream<com.marklogic.client.io.InputStreamHandle> sendReceiveDocs(Stream<String> uris, Stream<com.marklogic.client.io.InputStreamHandle> docs);

}
