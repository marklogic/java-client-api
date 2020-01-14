/*
 * Copyright 2012-2019 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.BufferableHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.SuggestDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesListDefinition;
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
  String DISPOSITION_PARAM_CATEGORY = "category";

  String MIMETYPE_WILDCARD = "*/*";
  String MIMETYPE_TEXT_JSON = "text/json";
  String MIMETYPE_TEXT_XML = "text/xml";
  String MIMETYPE_APPLICATION_JSON = "application/json";
  String MIMETYPE_APPLICATION_XML = "application/xml";
  String MIMETYPE_MULTIPART_MIXED = "multipart/mixed";

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

  public void connect(String host, int port, String database, SecurityContext securityContext);
  public DatabaseClient getDatabaseClient();
  public void setDatabaseClient(DatabaseClient client);
  public void release();

  public TemporalDescriptor deleteDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                                           Set<Metadata> categories, RequestParameters extraParams)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  public boolean getDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                             Set<Metadata> categories, RequestParameters extraParams,
                             DocumentMetadataReadHandle metadataHandle, AbstractReadHandle contentHandle)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  public DocumentDescriptor head(RequestLogger logger, String uri, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  public DocumentPage getBulkDocuments(RequestLogger logger, long serverTimestamp, Transaction transaction,
                                       Set<Metadata> categories, Format format, RequestParameters extraParams,
                                       boolean withContent, String... uris)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
  public DocumentPage getBulkDocuments(RequestLogger logger, long serverTimestamp, QueryDefinition querydef,
                                       long start, long pageLength, Transaction transaction, SearchReadHandle searchHandle,
                                       QueryView view, Set<Metadata> categories, Format format, ServerTransform responseTransform, RequestParameters extraParams)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  public void postBulkDocuments(RequestLogger logger, DocumentWriteSet writeSet,
                                ServerTransform transform, Transaction transaction, Format defaultFormat)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
  public <T extends AbstractReadHandle> T postBulkDocuments(RequestLogger logger, DocumentWriteSet writeSet,
                                                            ServerTransform transform, Transaction transaction, Format defaultFormat, T output,
                                                            String temporalCollection)
    throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;

  public TemporalDescriptor putDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                                        Set<Metadata> categories, RequestParameters extraParams,
                                        DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ForbiddenUserException, FailedRequestException;

  public DocumentDescriptorImpl postDocument(RequestLogger logger, DocumentUriTemplate template,
                                             Transaction transaction, Set<Metadata> categories, RequestParameters extraParams,
                                             DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle)
    throws ResourceNotFoundException, ForbiddenUserException,
    FailedRequestException;

  public void patchDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                            Set<Metadata> categories, boolean isOnContent, DocumentPatchHandle patchHandle)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ForbiddenUserException, FailedRequestException;

  public <T extends SearchReadHandle> T search(RequestLogger logger, T searchHandle, QueryDefinition queryDef,
                                               long start, long len, QueryView view, Transaction transaction, String forestName)
    throws ForbiddenUserException, FailedRequestException;

  public void deleteSearch(RequestLogger logger, DeleteQueryDefinition queryDef, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;
  public void delete(RequestLogger logger, Transaction transaction, String... uris)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  public Transaction openTransaction(String name, int timeLimit)
    throws ForbiddenUserException, FailedRequestException;
  public void commitTransaction(Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;
  public void rollbackTransaction(Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  public <T> T values(Class <T> as, ValuesDefinition valdef, String mimetype, long start, long pageLength, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  public <T> T valuesList(Class <T> as, ValuesListDefinition valdef, String mimetype, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  public <T> T optionsList(Class <T> as, String mimetype, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;

  // namespaces, etc.
  public <T> T getValue(RequestLogger logger, String type, String key,
                        boolean isNullable, String mimetype, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public <T> T getValue(RequestLogger logger, String type, String key, Transaction transaction,
                        boolean isNullable, String mimetype, Class<T> as)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public <T> T getValues(RequestLogger logger, String type, String mimetype, Class<T> as)
    throws ForbiddenUserException, FailedRequestException;
  public <T> T getValues(RequestLogger reqlog, String type, RequestParameters extraParams,
                         String mimetype, Class<T> as)
    throws ForbiddenUserException, FailedRequestException;
  public void postValue(RequestLogger logger, String type, String key, String mimetype, Object value)
    throws ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  public void postValue(RequestLogger reqlog, String type, String key, RequestParameters extraParams)
    throws ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  public void putValue(RequestLogger logger, String type, String key,
                       String mimetype, Object value)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  public void putValue(RequestLogger logger, String type, String key, RequestParameters extraParams,
                       String mimetype, Object value)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  public void deleteValue(RequestLogger logger, String type, String key)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public void deleteValues(RequestLogger logger, String type)
    throws ForbiddenUserException, FailedRequestException;

  public <R extends UrisReadHandle> R uris(RequestLogger reqlog, Transaction transaction,
                                           QueryDefinition qdef, long start, String afterUri, long pageLength, String forestName, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public <R extends AbstractReadHandle> R getResource(RequestLogger reqlog, String path,
                                                      Transaction transaction, RequestParameters params, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public RESTServiceResultIterator getIteratedResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params, String... mimetypes)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  public <R extends AbstractReadHandle> R putResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R putResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;

  public <R extends AbstractReadHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, R output)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, Map<String, List<String>>[] headers, R output)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  public RESTServiceResultIterator postIteratedResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, String... outputMimetypes)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  public <W extends AbstractWriteHandle> RESTServiceResultIterator postIteratedResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    W[] input, String... outputMimetypes)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  public EvalResultIterator postEvalInvoke(RequestLogger reqlog, String code, String modulePath,
                                           ServerEvaluationCallImpl.Context evalContext, Map<String, Object> variables,
                                           EditableNamespaceContext namespaces, Transaction transaction)
    throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
    FailedRequestException;
  public <R extends AbstractReadHandle> R deleteResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params, R output)
    throws  ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public ConnectionResult checkConnection();
  
  // backdoor
  public Object getClientImplementation();

  public enum ResponseStatus {
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

  public <T> T suggest(Class<T> as, SuggestDefinition suggestionDef);

  public InputStream match(StructureWriteHandle document, String[] candidateRules, String mimeType, ServerTransform transform);
  public InputStream match(String[] docIds, String[] candidateRules, ServerTransform transform);
  public InputStream match(QueryDefinition queryDef, long start, long pageLength, String[] candidateRules, ServerTransform transform);

  public <R extends AbstractReadHandle> R getGraphUris(RequestLogger reqlog, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public <R extends AbstractReadHandle> R readGraph(RequestLogger reqlog, String uri, R output,
                                                    Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public void writeGraph(RequestLogger reqlog, String uri,
                         AbstractWriteHandle input, GraphPermissions permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public void writeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public Object deleteGraph(RequestLogger requestLogger, String uri,
                            Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;
  public void deleteGraphs(RequestLogger requestLogger, Transaction transaction)
    throws ForbiddenUserException, FailedRequestException;
  public <R extends AbstractReadHandle> R executeSparql(RequestLogger reqlog,
                                                        SPARQLQueryDefinition qdef, R output, long start, long pageLength,
                                                        Transaction transaction, boolean isUpdate);

  /**
   * Wraps a HEAD request for a simple URI
   * @param uri URL to which to make a HEAD request
   * @return true if the status response is 200, false if 404;
   */
  public boolean exists(String uri);

  public void mergeGraph(RequestLogger reqlog, String uri, AbstractWriteHandle input,
                         GraphPermissions permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException,
    FailedRequestException;

  public void mergeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public <R extends AbstractReadHandle> R getPermissions(RequestLogger reqlog, String uri,
                                                         R output, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public void deletePermissions(RequestLogger reqlog, String uri, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public void writePermissions(RequestLogger reqlog, String uri,
                               AbstractWriteHandle permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public void mergePermissions(RequestLogger reqlog, String uri,
                               AbstractWriteHandle permissions, Transaction transaction)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
  public <R extends AbstractReadHandle> R getThings(RequestLogger reqlog, String[] iris, R output)
    throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

  public interface RESTServiceResultIterator extends ServiceResultIterator {
    @Override
    public RESTServiceResult next();
  }
  public interface RESTServiceResult extends ServiceResult {
    public Map<String,List<String>> getHeaders();
  }

  public String advanceLsqt(RequestLogger reqlog, String temporalCollection, long lag);

  public void wipeDocument(RequestLogger requestLogger, String temporalDocumentURI, Transaction transaction,
                           RequestParameters extraParams);

  public void protectDocument(RequestLogger requestLogger, String temporalDocumentURI, Transaction transaction,
                              RequestParameters extraParams, ProtectionLevel level, String duration, Calendar expiryTime, String archivePath);
  public <R extends AbstractReadHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, R output, String operation)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
  public <R extends AbstractReadHandle> R postResource(
    RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
    AbstractWriteHandle input, R output, String operation, Map<String,List<String>> responseHeaders)
    throws ResourceNotFoundException, ResourceNotResendableException,
    ResourceNotResendableException, ForbiddenUserException, FailedRequestException;
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
    private String paramName;
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
        CallField callField = (CallField)arg0;
        if(this.getParamName()!=null && callField.getParamName()!=null && 
                this.getParamName().equals(callField.getParamName()))
            return true;
        return false;
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
    private String paramValue;
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
    private Stream<String> paramValues;
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
    private Stream<? extends BufferableHandle> paramValues;

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
	private String[] paramValues;
	public BufferedMultipleAtomicCallField(String paramName, Stream<String> paramValues) {
        this(paramName, paramValues.toArray(size -> new String[size]));
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
	        this(paramName, paramValues.toArray(size -> new BufferableHandle[size]));
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
    InputStream asInputStream();
    InputStreamHandle asInputStreamHandle();
    Reader asReader();
    ReaderHandle asReaderHandle();
    String            asString();
  }

  interface MultipleCallResponse extends CallResponse {
    Stream<byte[]>            asStreamOfBytes();
    Stream<InputStream>       asStreamOfInputStream();
    Stream<InputStreamHandle> asStreamOfInputStreamHandle();
    Stream<Reader>            asStreamOfReader();
    Stream<ReaderHandle>      asStreamOfReaderHandle();
    Stream<String>            asStreamOfString();
    byte[][]            asArrayOfBytes();
    InputStream[]       asArrayOfInputStream();
    InputStreamHandle[] asArrayOfInputStreamHandle();
    Reader[]            asArrayOfReader();
    ReaderHandle[]      asArrayOfReaderHandle();
    String[]            asArrayOfString();
  }
}
