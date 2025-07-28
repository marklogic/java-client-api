package com.marklogic.client.test.dbfunction.positive;

// IMPORTANT: Do not edit. This file is generated.

import com.marklogic.client.io.Format;
import java.util.stream.Stream;


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

            private BaseProxy.DBFunctionRequest req_sendReceiveOptionalDoc;
            private BaseProxy.DBFunctionRequest req_sendReceiveAnyDocs;
            private BaseProxy.DBFunctionRequest req_sendReceiveRequiredDoc;
            private BaseProxy.DBFunctionRequest req_sendReceiveManyDocs;
            private BaseProxy.DBFunctionRequest req_sendReceiveMappedDoc;

            private AnyDocumentBundleImpl(DatabaseClient dbClient, JSONWriteHandle servDecl) {
                this.dbClient  = dbClient;
                this.baseProxy = new BaseProxy("/dbf/test/anyDocument/", servDecl);

                this.req_sendReceiveOptionalDoc = this.baseProxy.request(
                    "sendReceiveOptionalDoc.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_MIXED);
                this.req_sendReceiveAnyDocs = this.baseProxy.request(
                    "sendReceiveAnyDocs.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_MIXED);
                this.req_sendReceiveRequiredDoc = this.baseProxy.request(
                    "sendReceiveRequiredDoc.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_MIXED);
                this.req_sendReceiveManyDocs = this.baseProxy.request(
                    "sendReceiveManyDocs.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_MIXED);
                this.req_sendReceiveMappedDoc = this.baseProxy.request(
                    "sendReceiveMappedDoc.sjs", BaseProxy.ParameterValuesKind.MULTIPLE_MIXED);
            }

            @Override
            public com.marklogic.client.io.InputStreamHandle sendReceiveOptionalDoc(String uri, com.marklogic.client.io.InputStreamHandle doc) {
                return sendReceiveOptionalDoc(
                    this.req_sendReceiveOptionalDoc.on(this.dbClient), uri, doc
                    );
            }
            private com.marklogic.client.io.InputStreamHandle sendReceiveOptionalDoc(BaseProxy.DBFunctionRequest request, String uri, com.marklogic.client.io.InputStreamHandle doc) {
              return request
                      .withParams(
                          BaseProxy.atomicParam("uri", true, BaseProxy.StringType.fromString(uri)),
                          BaseProxy.documentParam("doc", true, doc)
                          ).responseSingle(true, Format.UNKNOWN)
                      .asHandle(new com.marklogic.client.io.InputStreamHandle());
            }

            @Override
            public Stream<com.marklogic.client.io.InputStreamHandle> sendReceiveAnyDocs(Stream<String> uris, Stream<com.marklogic.client.io.InputStreamHandle> docs) {
                return sendReceiveAnyDocs(
                    this.req_sendReceiveAnyDocs.on(this.dbClient), uris, docs
                    );
            }
            private Stream<com.marklogic.client.io.InputStreamHandle> sendReceiveAnyDocs(BaseProxy.DBFunctionRequest request, Stream<String> uris, Stream<com.marklogic.client.io.InputStreamHandle> docs) {
              return request
                      .withParams(
                          BaseProxy.atomicParam("uris", true, BaseProxy.StringType.fromString(uris)),
                          BaseProxy.documentParam("docs", true, docs)
                          ).responseMultiple(true, Format.UNKNOWN)
                      .asStreamOfHandles(null, new com.marklogic.client.io.InputStreamHandle());
            }

            @Override
            public com.marklogic.client.io.InputStreamHandle sendReceiveRequiredDoc(String uri, com.marklogic.client.io.InputStreamHandle doc) {
                return sendReceiveRequiredDoc(
                    this.req_sendReceiveRequiredDoc.on(this.dbClient), uri, doc
                    );
            }
            private com.marklogic.client.io.InputStreamHandle sendReceiveRequiredDoc(BaseProxy.DBFunctionRequest request, String uri, com.marklogic.client.io.InputStreamHandle doc) {
              return request
                      .withParams(
                          BaseProxy.atomicParam("uri", false, BaseProxy.StringType.fromString(uri)),
                          BaseProxy.documentParam("doc", false, doc)
                          ).responseSingle(false, Format.UNKNOWN)
                      .asHandle(new com.marklogic.client.io.InputStreamHandle());
            }

            @Override
            public Stream<com.marklogic.client.io.InputStreamHandle> sendReceiveManyDocs(Stream<String> uris, Stream<com.marklogic.client.io.InputStreamHandle> docs) {
                return sendReceiveManyDocs(
                    this.req_sendReceiveManyDocs.on(this.dbClient), uris, docs
                    );
            }
            private Stream<com.marklogic.client.io.InputStreamHandle> sendReceiveManyDocs(BaseProxy.DBFunctionRequest request, Stream<String> uris, Stream<com.marklogic.client.io.InputStreamHandle> docs) {
              return request
                      .withParams(
                          BaseProxy.atomicParam("uris", false, BaseProxy.StringType.fromString(uris)),
                          BaseProxy.documentParam("docs", false, docs)
                          ).responseMultiple(false, Format.UNKNOWN)
                      .asStreamOfHandles(null, new com.marklogic.client.io.InputStreamHandle());
            }

            @Override
            public com.marklogic.client.io.StringHandle sendReceiveMappedDoc(String uri, com.marklogic.client.io.StringHandle doc) {
                return sendReceiveMappedDoc(
                    this.req_sendReceiveMappedDoc.on(this.dbClient), uri, doc
                    );
            }
            private com.marklogic.client.io.StringHandle sendReceiveMappedDoc(BaseProxy.DBFunctionRequest request, String uri, com.marklogic.client.io.StringHandle doc) {
              return request
                      .withParams(
                          BaseProxy.atomicParam("uri", true, BaseProxy.StringType.fromString(uri)),
                          BaseProxy.documentParam("doc", true, doc)
                          ).responseSingle(true, Format.UNKNOWN)
                      .asHandle(new com.marklogic.client.io.StringHandle());
            }
        }

        return new AnyDocumentBundleImpl(db, serviceDeclaration);
    }

  /**
   * Invokes the sendReceiveOptionalDoc operation on the database server
   *
   * @param uri	provides input
   * @param doc	provides input
   * @return	as output
   */
    com.marklogic.client.io.InputStreamHandle sendReceiveOptionalDoc(String uri, com.marklogic.client.io.InputStreamHandle doc);

  /**
   * Invokes the sendReceiveAnyDocs operation on the database server
   *
   * @param uris	provides input
   * @param docs	provides input
   * @return	as output
   */
    Stream<com.marklogic.client.io.InputStreamHandle> sendReceiveAnyDocs(Stream<String> uris, Stream<com.marklogic.client.io.InputStreamHandle> docs);

  /**
   * Invokes the sendReceiveRequiredDoc operation on the database server
   *
   * @param uri	provides input
   * @param doc	provides input
   * @return	as output
   */
    com.marklogic.client.io.InputStreamHandle sendReceiveRequiredDoc(String uri, com.marklogic.client.io.InputStreamHandle doc);

  /**
   * Invokes the sendReceiveManyDocs operation on the database server
   *
   * @param uris	provides input
   * @param docs	provides input
   * @return	as output
   */
    Stream<com.marklogic.client.io.InputStreamHandle> sendReceiveManyDocs(Stream<String> uris, Stream<com.marklogic.client.io.InputStreamHandle> docs);

  /**
   * Invokes the sendReceiveMappedDoc operation on the database server
   *
   * @param uri	provides input
   * @param doc	provides input
   * @return	as output
   */
    com.marklogic.client.io.StringHandle sendReceiveMappedDoc(String uri, com.marklogic.client.io.StringHandle doc);

}
