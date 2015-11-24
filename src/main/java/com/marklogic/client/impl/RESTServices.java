/*
 * Copyright 2012-2015 MarkLogic Corporation
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.Transaction;
import com.marklogic.client.bitemporal.TemporalDescriptor;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.impl.ServerEvaluationCallImpl.Context;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
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
import com.sun.jersey.api.client.ClientResponse;

public interface RESTServices {
	public void connect(String host, int port, String database, String user, String password, Authentication type,
			SSLContext context, SSLHostnameVerifier verifier);
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

	public DocumentPage getBulkDocuments(RequestLogger logger, Transaction transaction,
			Set<Metadata> categories, Format format, RequestParameters extraParams,
			boolean withContent, String... uris)
		throws ResourceNotFoundException, ForbiddenUserException,  FailedRequestException;
	public DocumentPage getBulkDocuments(RequestLogger logger, QueryDefinition querydef,
			long start, long pageLength, Transaction transaction, SearchReadHandle searchHandle, 
            QueryView view, Set<Metadata> categories, Format format, RequestParameters extraParams)
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

    public <T> T search(RequestLogger logger, Class <T> as, QueryDefinition queryDef, String mimetype,
    		long start, long len, QueryView view, Transaction transaction)
    	throws ForbiddenUserException, FailedRequestException;
	public <T> T search(RequestLogger reqlog, Class<T> as, QueryDefinition queryDef, String mimetype,
			String view)
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

	public <R extends AbstractReadHandle> R getResource(RequestLogger reqlog, String path, 
			Transaction transaction, RequestParameters params, R output)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	public ServiceResultIterator getIteratedResource(
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
	public ServiceResultIterator postIteratedResource(
			RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
			AbstractWriteHandle input, String... outputMimetypes)
		throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException;
	public <W extends AbstractWriteHandle> ServiceResultIterator postIteratedResource(
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

	// backdoor
	public Object getClientImplementation();

	public enum ResponseStatus {
		OK() {
			public boolean isExpected(ClientResponse.Status status) {
				return status == ClientResponse.Status.OK;
			}
		},
		CREATED() {
			public boolean isExpected(ClientResponse.Status status) {
				return status == ClientResponse.Status.CREATED;
			}
		},
		NO_CONTENT() {
			public boolean isExpected(ClientResponse.Status status) {
				return status == ClientResponse.Status.NO_CONTENT;
			}
		},
		OK_OR_NO_CONTENT() {
			public boolean isExpected(ClientResponse.Status status) {
				return (status == ClientResponse.Status.OK ||
						status == ClientResponse.Status.NO_CONTENT);
			}
		},
		CREATED_OR_NO_CONTENT() {
			public boolean isExpected(ClientResponse.Status status) {
				return (status == ClientResponse.Status.CREATED ||
						status == ClientResponse.Status.NO_CONTENT);
			}
		},
		OK_OR_CREATED_OR_NO_CONTENT() {
			public boolean isExpected(ClientResponse.Status status) {
				return (status == ClientResponse.Status.OK ||
						status == ClientResponse.Status.CREATED ||
						status == ClientResponse.Status.NO_CONTENT);
			}
		},
		SEE_OTHER() {
			public boolean isExpected(ClientResponse.Status status) {
				return status == ClientResponse.Status.SEE_OTHER;
			}
		};
		public boolean isExpected(ClientResponse.Status status) {
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
}
