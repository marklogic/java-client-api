/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.SessionState;
import com.marklogic.client.Transaction;
import com.marklogic.client.bitemporal.TemporalDescriptor;
import com.marklogic.client.bitemporal.TemporalDocumentManager.ProtectionLevel;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.DatabaseClient.ConnectionResult;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.marker.*;
import com.marklogic.client.query.*;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.util.RequestParameters;

public interface RESTServices {

  String AUTHORIZATION_TYPE_SAML = "SAML";
  String AUTHORIZATION_PARAM_TOKEN = "token";
  String HEADER_ACCEPT = "Accept";
  String HEADER_AUTHORIZATION = "Authorization";
  String HEADER_COOKIE = "Cookie";
  String HEADER_ERROR_FORMAT = "X-Error-Accept";
  String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
  String HEADER_CONTENT_LENGTH = "Content-Length";
  String HEADER_CONTENT_TYPE = "Content-Type";
  String HEADER_ETAG = "ETag";
  String HEADER_ML_EFFECTIVE_TIMESTAMP = "ML-Effective-Timestamp";
  String HEADER_SET_COOKIE = "Set-Cookie";
  String HEADER_VND_MARKLOGIC_DOCUMENT_FORMAT = "vnd.marklogic.document-format";
  String HEADER_VND_MARKLOGIC_START = "vnd.marklogic.start";
  String HEADER_VND_MARKLOGIC_PAGELENGTH = "vnd.marklogic.pageLength";
  String HEADER_VND_MARKLOGIC_RESULT_ESTIMATE = "vnd.marklogic.result-estimate";
  String HEADER_X_MARKLOGIC_SYSTEM_TIME = "x-marklogic-system-time";
  String HEADER_ML_LSQT = "ML-LSQT";
  String HEADER_X_PRIMITIVE = "X-Primitive";

  String DISPOSITION_TYPE_ATTACHMENT = "attachment";
  String DISPOSITION_TYPE_INLINE = "inline";
  String DISPOSITION_PARAM_FILENAME = "filename";
  String DISPOSITION_PARAM_TEMPORALDOC = "temporal-document";
  String DISPOSITION_PARAM_CATEGORY = "category";

  String MIMETYPE_WILDCARD = "*/*";
  String MIMETYPE_TEXT_JSON = "text/json";
  String MIMETYPE_TEXT_XML = "text/xml";
  String MIMETYPE_APPLICATION_JSON = "application/json";
  String MIMETYPE_APPLICATION_XML = "application/xml";
  String MIMETYPE_MULTIPART_MIXED = "multipart/mixed";
  String MIMETYPE_MULTIPART_FORM = "multipart/form-data";

  int STATUS_OK = 200;
  int STATUS_CREATED = 201;
  int STATUS_NO_CONTENT = 204;
  int STATUS_PARTIAL_CONTENT = 206;
  int STATUS_SEE_OTHER = 303;
  int STATUS_NOT_MODIFIED = 304;
  int STATUS_UNAUTHORIZED = 401;
  int STATUS_FORBIDDEN = 403;
  int STATUS_NOT_FOUND = 404;
  int STATUS_PRECONDITION_FAILED = 412;
  int STATUS_PRECONDITION_REQUIRED = 428;
  int STATUS_BAD_GATEWAY = 502;
  int STATUS_SERVICE_UNAVAILABLE = 503;
  int STATUS_GATEWAY_TIMEOUT = 504;

  String MAX_DELAY_PROP = "com.marklogic.client.maximumRetrySeconds";
  String MIN_RETRY_PROP = "com.marklogic.client.minimumRetries";

  Set<Integer> getRetryStatus();
  int getMaxDelay();
  void setMaxDelay(int maxDelay);

  void connect(String host, int port, String basePath, String database, SecurityContext securityContext);
  DatabaseClient getDatabaseClient();
  void setDatabaseClient(DatabaseClient client);
  void release();

  TemporalDescriptor deleteDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                                           Set<Metadata> categories, RequestParameters extraParams)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  boolean getDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                             Set<Metadata> categories, RequestParameters extraParams,
                             DocumentMetadataReadHandle metadataHandle, AbstractReadHandle contentHandle)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  DocumentDescriptor head(RequestLogger logger, String uri, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  DocumentPage getBulkDocuments(RequestLogger logger, long serverTimestamp, Transaction transaction,
                                       Set<Metadata> categories, Format format, RequestParameters extraParams,
                                       boolean withContent, String... uris)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
  DocumentPage getBulkDocuments(RequestLogger logger, long serverTimestamp, SearchQueryDefinition querydef,
                                       long start, long pageLength, Transaction transaction, SearchReadHandle searchHandle,
                                       QueryView view, Set<Metadata> categories, Format format, ServerTransform responseTransform,
                                       RequestParameters extraParams, String forestName)
          throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  <T extends AbstractReadHandle> T postBulkDocuments(RequestLogger logger, DocumentWriteSet writeSet,
                                                            ServerTransform transform, Transaction transaction, Format defaultFormat, T output,
                                                            String temporalCollection, String extraContentDispositionParams)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  TemporalDescriptor putDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                                        Set<Metadata> categories, RequestParameters extraParams,
                                        DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ForbiddenUserException, FailedRequestException;

  DocumentDescriptorImpl postDocument(RequestLogger logger, DocumentUriTemplate template,
                                             Transaction transaction, Set<Metadata> categories, RequestParameters extraParams,
                                             DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle)
    throws ResourceNotFoundException, ForbiddenUserException,
    FailedRequestException;

  void patchDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                            Set<Metadata> categories, boolean isOnContent, DocumentPatchHandle patchHandle)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ForbiddenUserException, FailedRequestException;

  <T extends SearchReadHandle> T search(RequestLogger logger, T searchHandle, SearchQueryDefinition queryDef,
                                               long start, long len, QueryView view, Transaction transaction, String forestName)
    throws ForbiddenUserException, FailedRequestException;

  void deleteSearch(RequestLogger logger, DeleteQueryDefinition queryDef, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;
  void delete(RequestLogger logger, Transaction transaction, Set<Metadata> categories, String... uris)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  Transaction openTransaction(String name, int timeLimit)
    throws ForbiddenUserException, FailedRequestException;
  void commitTransaction(Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;
  void rollbackTransaction(Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  <T> T values(Class <T> as, ValuesDefinition valdef, String mimetype, long start, long pageLength, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  <T> T valuesList(Class <T> as, ValuesListDefinition valdef, String mimetype, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  <T> T optionsList(Class <T> as, String mimetype, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  // namespaces, etc.
  <T> T getValue(RequestLogger logger, String type, String key,
                        boolean isNullable, String mimetype, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  <T> T getValue(RequestLogger logger, String type, String key, Transaction transaction,
                        boolean isNullable, String mimetype, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  <T> T getValues(RequestLogger logger, String type, String mimetype, Class<T> as)
    throws ForbiddenUserException, FailedRequestException;
  <T> T getValues(RequestLogger reqlog, String type, RequestParameters extraParams,
                         String mimetype, Class<T> as)
    throws ForbiddenUserException, FailedRequestException;
  void postValue(RequestLogger logger, String type, String key, String mimetype, Object value)
    throws ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  void postValue(RequestLogger reqlog, String type, String key, RequestParameters extraParams)
    throws ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  void putValue(RequestLogger logger, String type, String key,
                       String mimetype, Object value)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  void putValue(RequestLogger logger, String type, String key, RequestParameters extraParams,
                       String mimetype, Object value)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  void deleteValue(RequestLogger logger, String type, String key)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  void deleteValues(RequestLogger logger, String type)
    throws ForbiddenUserException, FailedRequestException;

  <R extends AbstractReadHandle> R getSystemSchema(RequestLogger reqlog, String schemaName, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  <R extends UrisReadHandle> R uris(RequestLogger reqlog, String method, SearchQueryDefinition qdef,
                       Boolean filtered, long start, String afterUri, long pageLength, String forestName, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  <R extends AbstractReadHandle> R forestInfo(RequestLogger reqlog,
                       String method, RequestParameters params, SearchQueryDefinition qdef, R output
    ) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  <R extends AbstractReadHandle> R getResource(RequestLogger reqlog, String path,
                                                        Transaction transaction, RequestParameters params, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  RESTServiceResultIterator getIteratedResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  <R extends AbstractReadHandle> R putResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  <R extends AbstractReadHandle, W extends AbstractWriteHandle> R putResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;

  <R extends AbstractReadHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ForbiddenUserException, FailedRequestException;
  <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ForbiddenUserException, FailedRequestException;
  <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, Map<String, List<String>>[] headers, R output)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ForbiddenUserException, FailedRequestException;
  RESTServiceResultIterator postIteratedResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  RESTServiceResultIterator postMultipartForm(
          RequestLogger reqlog, String path, Transaction transaction, RequestParameters params, List<ContentParam> contentParams)
          throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
          FailedRequestException;
  <W extends AbstractWriteHandle> RESTServiceResultIterator postIteratedResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  EvalResultIterator postEvalInvoke(RequestLogger reqlog, String code, String modulePath,
                                           ServerEvaluationCallImpl.Context evalContext, Map<String, Object> variables,
                                           EditableNamespaceContext namespaces, Transaction transaction)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  <R extends AbstractReadHandle> R deleteResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params, R output)
    throws  ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  ConnectionResult checkConnection();

  // backdoor
  Object getClientImplementation();

  enum ResponseStatus {
    OK() {
      @Override
      public boolean isExpected(int status) {
        return status == STATUS_OK;
      }
    },
    CREATED() {
      @Override
      public boolean isExpected(int status) {
        return status == STATUS_CREATED;
      }
    },
    NO_CONTENT() {
      @Override
      public boolean isExpected(int status) {
        return status == STATUS_NO_CONTENT;
      }
    },
    OK_OR_NO_CONTENT() {
      @Override
      public boolean isExpected(int status) {
        return (status == STATUS_OK ||
          status == STATUS_NO_CONTENT);
      }
    },
    CREATED_OR_NO_CONTENT() {
      @Override
      public boolean isExpected(int status) {
        return (status == STATUS_CREATED ||
          status == STATUS_NO_CONTENT);
      }
    },
    OK_OR_CREATED_OR_NO_CONTENT() {
      @Override
      public boolean isExpected(int status) {
        return (status == STATUS_OK ||
          status == STATUS_CREATED ||
          status == STATUS_NO_CONTENT);
      }
    },
    SEE_OTHER() {
      @Override
      public boolean isExpected(int status) {
        return status == STATUS_SEE_OTHER;
      }
    };
    public boolean isExpected(int status) {
      return false;
    }
  }

  <T> T suggest(Class<T> as, SuggestDefinition suggestionDef);

  InputStream match(StructureWriteHandle document, String[] candidateRules, String mimeType, ServerTransform transform);
  InputStream match(String[] docIds, String[] candidateRules, ServerTransform transform);
  InputStream match(QueryDefinition queryDef, long start, long pageLength, String[] candidateRules, ServerTransform transform);

  <R extends AbstractReadHandle> R getGraphUris(RequestLogger reqlog, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  <R extends AbstractReadHandle> R readGraph(RequestLogger reqlog, String uri, R output,
                                                    Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  void writeGraph(RequestLogger reqlog, String uri,
                         AbstractWriteHandle input, GraphPermissions permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  void writeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  Object deleteGraph(RequestLogger requestLogger, String uri,
                            Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;
  void deleteGraphs(RequestLogger requestLogger, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;
  <R extends AbstractReadHandle> R executeSparql(RequestLogger reqlog,
                                                        SPARQLQueryDefinition qdef, R output, long start, long pageLength,
                                                        Transaction transaction, boolean isUpdate);

  /**
   * Wraps a HEAD request for a simple URI
   * @param uri URL to which to make a HEAD request
   * @return true if the status response is 200, false if 404;
   */
  boolean exists(String uri);

  void mergeGraph(RequestLogger reqlog, String uri, AbstractWriteHandle input,
                         GraphPermissions permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException,
    FailedRequestException;

  void mergeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  <R extends AbstractReadHandle> R getPermissions(RequestLogger reqlog, String uri,
                                                         R output, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  void deletePermissions(RequestLogger reqlog, String uri, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  void writePermissions(RequestLogger reqlog, String uri,
                               AbstractWriteHandle permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  void mergePermissions(RequestLogger reqlog, String uri,
                               AbstractWriteHandle permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  <R extends AbstractReadHandle> R getThings(RequestLogger reqlog, String[] iris, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  interface RESTServiceResultIterator extends ServiceResultIterator {
    @Override
    RESTServiceResult next();
  }
  interface RESTServiceResult extends ServiceResult {
    Map<String,List<String>> getHeaders();
  }

  String advanceLsqt(RequestLogger reqlog, String temporalCollection, long lag);

  void wipeDocument(RequestLogger requestLogger, String temporalDocumentURI, Transaction transaction,
                           RequestParameters extraParams);

  void protectDocument(RequestLogger requestLogger, String temporalDocumentURI, Transaction transaction,
                              RequestParameters extraParams, ProtectionLevel level, String duration, Calendar expiryTime, String archivePath);
  <R extends AbstractReadHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, R output, String operation)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ForbiddenUserException, FailedRequestException;
  <R extends AbstractReadHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, R output, String operation, Map<String,List<String>> responseHeaders)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ForbiddenUserException, FailedRequestException;
  void patchDocument(RequestLogger reqlog, DocumentDescriptor desc, Transaction transaction, Set<Metadata> categories, boolean isOnContent,
                     RequestParameters extraParams, String sourceDocumentURI, DocumentPatchHandle patchHandle)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;

  // API First Additions
  CallRequest makeEmptyRequest(String endpoint, HttpMethod method, SessionState session);

  CallRequest makeAtomicBodyRequest(String endpoint, HttpMethod method, SessionState session, CallField... params);

  CallRequest makeNodeBodyRequest(String endpoint, HttpMethod method, SessionState session, CallField... params);

  enum HttpMethod {POST}

  abstract class CallField {
    final private String paramName;
    CallField(String paramName) {
      this.paramName = paramName;
    }
    public String getParamName() {
      return paramName;
    }
    public CallField toBuffered() {
    	return this;
    }

    @Override
    public int hashCode() {
        return getParamName().hashCode();
    }

    @Override
    public boolean equals(Object arg0) {
      if (!(arg0 instanceof CallField)) return false;
      CallField callField = (CallField)arg0;
      return this.getParamName() != null && callField.getParamName() != null &&
              this.getParamName().equals(callField.getParamName());
    }
  }
  abstract class MultipleAtomicCallField extends CallField {
    MultipleAtomicCallField(String paramName) {
            super(paramName);
        }
    abstract Stream<String> getParamValues();
  }
  abstract class MultipleNodeCallField extends CallField {
    MultipleNodeCallField(String paramName) {
            super(paramName);
        }
    abstract Stream<? extends AbstractWriteHandle> getParamValues();
  }
  class SingleNodeCallField extends CallField {
    private BufferableHandle paramValue;

    public SingleNodeCallField(String paramName, BufferableHandle paramValue) {
            super(paramName);
      this.paramValue = paramValue;
    }

    public BufferableHandle getParamValue() {
      return paramValue;
        }

    void setParamValue(BufferableHandle paramValue) {
      this.paramValue = paramValue;
    }

    public SingleNodeCallField toBuffered() {
      return new SingleNodeCallField(super.getParamName(), NodeConverter.bufferAsBytes(paramValue));
  }
  }
  class SingleAtomicCallField extends CallField {
    private final String paramValue;
    public SingleAtomicCallField(String paramName, String paramValue) {
      super(paramName);
      this.paramValue = paramValue;
    }
    public String getParamValue() {
      return paramValue;
    }
    @Override
    public SingleAtomicCallField toBuffered() {
    	return this;
    }
  }
  class UnbufferedMultipleAtomicCallField extends MultipleAtomicCallField {
    private final Stream<String> paramValues;
    public UnbufferedMultipleAtomicCallField(String paramName, Stream<String> paramValues) {
      super(paramName);
      this.paramValues = paramValues;
    }

    @Override
    public BufferedMultipleAtomicCallField toBuffered() {
    	return new BufferedMultipleAtomicCallField(super.getParamName(), paramValues);
    }

    @Override
    public Stream<String> getParamValues() {
		return paramValues;
	}
  }
  class UnbufferedMultipleNodeCallField extends MultipleNodeCallField {
    private final Stream<? extends BufferableHandle> paramValues;

    public UnbufferedMultipleNodeCallField(String paramName, Stream<? extends BufferableHandle> paramValues) {
      super(paramName);
      this.paramValues = paramValues;
    }

    @Override
    public Stream<? extends BufferableHandle> getParamValues() {
      return paramValues;
    }

    @Override
    public BufferedMultipleNodeCallField toBuffered() {
    	return new BufferedMultipleNodeCallField(super.getParamName(), paramValues);
    }

  }
  class BufferedMultipleAtomicCallField extends MultipleAtomicCallField {
	private final String[] paramValues;
	public BufferedMultipleAtomicCallField(String paramName, Stream<String> paramValues) {
        this(paramName, paramValues.toArray(String[]::new));
    }

	public BufferedMultipleAtomicCallField(String paramName, String[] paramValues) {
	    super(paramName);
	    this.paramValues = paramValues;
	}

	@Override
	public Stream<String> getParamValues() {
		return Stream.of(paramValues);
	}
  }
  class BufferedMultipleNodeCallField extends MultipleNodeCallField {
	  private BufferableHandle[] paramValues;

	  public BufferedMultipleNodeCallField(String paramName, Stream<? extends BufferableHandle> paramValues) {
	        this(paramName, paramValues.toArray(BufferableHandle[]::new));
	  }
	  public BufferedMultipleNodeCallField(String paramName, BufferableHandle[] paramValues) {
	      super(paramName);
	      this.paramValues = paramValues;
	  }
	  @Override
	  public Stream<BufferableHandle> getParamValues() {
			return Stream.of(paramValues);
	  }
	  public BufferableHandle[] getParamValuesArray() {
	      return this.paramValues;
	  }
	  void setParamValues(BufferableHandle[] paramValues) {
	      this.paramValues = paramValues;
	  }

	  public BufferedMultipleNodeCallField toBuffered() {
          return new BufferedMultipleNodeCallField(super.getParamName(), NodeConverter.bufferAsBytes(paramValues));
      }
  }

  interface CallRequest {
    boolean hasStreamingPart();
    SessionState getSession();
    String getEndpoint();
    HttpMethod getHttpMethod();
    CallResponse withEmptyResponse();
    SingleCallResponse withDocumentResponse(Format format);
    MultipleCallResponse withMultipartMixedResponse(Format format);
  }

  interface CallResponse {
    boolean isNull();
    int     getStatusCode();
    String  getStatusMsg();
    String  getErrorBody();
    void close();
  }

  interface SingleCallResponse extends CallResponse {
    byte[]            asBytes();
    <C,R> C           asContent(BufferableContentHandle<C,R> outputHandle);
    <T extends BufferableContentHandle<?,?>> T asHandle(T outputHandle);
    InputStream       asInputStream();
    InputStreamHandle asInputStreamHandle();
    Reader            asReader();
    ReaderHandle      asReaderHandle();
    String            asString();
    boolean           asEndpointState(BytesHandle endpointStateHandle);
  }

  interface MultipleCallResponse extends CallResponse {
    Stream<byte[]>            asStreamOfBytes();
    <C,R> Stream<C>           asStreamOfContent(BytesHandle endpointStateHandle, BufferableContentHandle<C,R> outputHandle);
    <T extends BufferableContentHandle<?,?>> Stream<T>
        asStreamOfHandles(BytesHandle endpointStateHandle, T outputHandle);
    Stream<InputStream>       asStreamOfInputStream();
    Stream<InputStreamHandle> asStreamOfInputStreamHandle();
    Stream<Reader>            asStreamOfReader();
    Stream<ReaderHandle>      asStreamOfReaderHandle();
    Stream<String>            asStreamOfString();
    byte[][]            asArrayOfBytes();
    <C,R> C[]           asArrayOfContent(BytesHandle endpointStateHandle, BufferableContentHandle<C,R> outputHandle);
    <C,R> BufferableContentHandle<C,R>[]
        asArrayOfHandles(BytesHandle endpointStateHandle, BufferableContentHandle<C,R> outputHandle);
    InputStream[]       asArrayOfInputStream();
    InputStreamHandle[] asArrayOfInputStreamHandle();
    Reader[]            asArrayOfReader();
    ReaderHandle[]      asArrayOfReaderHandle();
    String[]            asArrayOfString();
  }
}
