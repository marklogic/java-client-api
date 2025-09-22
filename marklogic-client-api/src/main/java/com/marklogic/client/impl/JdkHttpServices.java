/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.*;
import com.marklogic.client.DatabaseClient.ConnectionResult;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;
import com.marklogic.client.bitemporal.TemporalDescriptor;
import com.marklogic.client.bitemporal.TemporalDocumentManager.ProtectionLevel;
import com.marklogic.client.document.*;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.*;
import com.marklogic.client.query.*;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.util.RequestParameters;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * Implementation of RESTServices using the JDK HttpClient instead of OkHttp.
 * This is a prototype implementation for a future major release.
 */
@SuppressWarnings("unused") // Fields will be used in complete implementation
public class JdkHttpServices implements RESTServices {

    private final String database;
    private final URI baseUri;
    private final HttpClient httpClient;
    private boolean released = false;

    public JdkHttpServices(String host, int port, String basePath, String database, SecurityContext securityContext) {
		if (host == null) {
			throw new IllegalArgumentException("No host provided");
		}
		if (securityContext == null) {
			throw new IllegalArgumentException("No security context provided");
		}

		this.database = database;
		this.baseUri = buildBaseUri(host, port, basePath, securityContext);

		HttpClient.Builder clientBuilder = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(30))
			.followRedirects(HttpClient.Redirect.NORMAL);

		// Configure SSL if present
		SSLContext sslContext = securityContext.getSSLContext();
		if (sslContext != null) {
			clientBuilder.sslContext(sslContext);

			// Configure SSL parameters if we have a hostname verifier
			if (securityContext.getSSLHostnameVerifier() != null) {
				SSLParameters sslParams = new SSLParameters();
				// Note: JDK HttpClient doesn't have direct hostname verifier support like OkHttp
				// In a full implementation, we would need to handle this differently
				clientBuilder.sslParameters(sslParams);
			}
		}

        this.httpClient = clientBuilder.build();
    }

	private URI buildBaseUri(String host, int port, String basePath, SecurityContext securityContext) {
        StringBuilder uriBuilder = new StringBuilder();

        // Determine scheme based on SSL context
        String scheme = (securityContext.getSSLContext() != null) ? "https" : "http";
        uriBuilder.append(scheme).append("://").append(host).append(":").append(port);

        // Add base path if provided
        if (basePath != null && !basePath.trim().isEmpty()) {
            if (!basePath.startsWith("/")) {
                uriBuilder.append("/");
            }
            uriBuilder.append(basePath.trim());
            if (!basePath.endsWith("/")) {
                uriBuilder.append("/");
            }
        } else {
            uriBuilder.append("/");
        }

        // Add the v1 API path
        uriBuilder.append("v1/");

        return URI.create(uriBuilder.toString());
    }

    @Override
    public void release() {
        if (!released) {
            this.released = true;
        }
    }

    @Override
    public Object getClientImplementation() {
        return httpClient;
    }

    // Stub implementations for all other RESTServices methods
    // These would need to be properly implemented in a complete migration

    @Override
    public TemporalDescriptor deleteDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
											 Set<Metadata> categories, RequestParameters extraParams)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("deleteDocument not yet implemented");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean getDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                             Set<Metadata> categories, RequestParameters extraParams,
                             DocumentMetadataReadHandle metadataHandle, AbstractReadHandle contentHandle)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {

        String uri = desc.getUri();
        if (uri == null) {
            throw new IllegalArgumentException("Document read for document identifier without uri");
        }

        try {
            // Build the request URL
            URI requestUri = buildDocumentRequestUri(uri, categories, transaction, extraParams);

            // Create HTTP request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(requestUri)
                .timeout(Duration.ofSeconds(30))
                .GET();

            // Add headers for metadata and content handles
            HandleImplementation metadataBase = HandleAccessor.checkHandle(metadataHandle, "metadata");
            HandleImplementation contentBase = HandleAccessor.checkHandle(contentHandle, "content");

            // Set Accept header based on handle types
            String acceptHeader = buildAcceptHeader(metadataBase, contentBase);
            if (acceptHeader != null) {
                requestBuilder.header(HEADER_ACCEPT, acceptHeader);
            }

            // Add transaction ID if present
            if (transaction != null) {
                requestBuilder.header("ML-Transaction-ID", transaction.getTransactionId());
            }

            // Execute the request
            HttpRequest request = requestBuilder.build();
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // Handle response status codes
            int statusCode = response.statusCode();

            if (statusCode == STATUS_NOT_FOUND) {
                throw new ResourceNotFoundException("Could not read non-existent document");
            }
            if (statusCode == STATUS_FORBIDDEN) {
                throw new ForbiddenUserException("User is not allowed to read documents");
            }
            if (statusCode == STATUS_NOT_MODIFIED) {
                return false;
            }
            if (statusCode != STATUS_OK && statusCode != STATUS_PARTIAL_CONTENT) {
                throw new FailedRequestException("read failed: HTTP " + statusCode);
            }

            // Process the response body into the handle
            try (InputStream responseBody = response.body()) {
                if (contentBase != null) {
                    HandleAccessor.receiveContent(contentHandle, responseBody);
                }

                // For now, we'll skip metadata handling in this simplified implementation
                // A complete implementation would parse multipart responses for both content and metadata

                return true;
            }

        } catch (IOException | InterruptedException e) {
            throw new FailedRequestException("Failed to send HTTP request: " + e.getMessage(), e);
        }
    }

    /**
     * Builds the request URI for document operations
     */
    private URI buildDocumentRequestUri(String docUri, Set<Metadata> categories, Transaction transaction, RequestParameters extraParams) {
        StringBuilder uriBuilder = new StringBuilder(baseUri.toString());
        uriBuilder.append("documents");

        // Add query parameters
        List<String> params = new ArrayList<>();

        // Document URI
        params.add("uri=" + URLEncoder.encode(docUri, StandardCharsets.UTF_8));

        // Database parameter
        if (database != null) {
            params.add("database=" + URLEncoder.encode(database, StandardCharsets.UTF_8));
        }

        // Categories
        if (categories == null || categories.isEmpty()) {
            params.add("category=content");
        } else {
            for (Metadata category : categories) {
                params.add("category=" + category.name().toLowerCase());
            }
        }

        // Transaction
        if (transaction != null) {
            params.add("txid=" + transaction.getTransactionId());
        }

        // Extra parameters
        if (extraParams != null) {
            for (Map.Entry<String, List<String>> entry : extraParams.entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue()) {
                    params.add(URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" +
                              URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
            }
        }

        // Append query string
        if (!params.isEmpty()) {
            uriBuilder.append("?").append(String.join("&", params));
        }

        return URI.create(uriBuilder.toString());
    }

    /**
     * Builds the Accept header based on the handles provided
     */
    @SuppressWarnings("rawtypes")
    private String buildAcceptHeader(HandleImplementation metadataBase, HandleImplementation contentBase) {
        List<String> acceptTypes = new ArrayList<>();

        if (metadataBase != null) {
            String mimetype = metadataBase.getMimetype();
            if (mimetype != null) {
                acceptTypes.add(mimetype);
            }
        }

        if (contentBase != null) {
            String mimetype = contentBase.getMimetype();
            if (mimetype != null) {
                acceptTypes.add(mimetype);
            }
        }

        return acceptTypes.isEmpty() ? null : String.join(", ", acceptTypes);
    }

    @Override
    public DocumentDescriptor head(RequestLogger logger, String uri, Transaction transaction)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("head not yet implemented");
    }

    @Override
    public DocumentPage getBulkDocuments(RequestLogger logger, long serverTimestamp, Transaction transaction,
                                       Set<Metadata> categories, Format format, RequestParameters extraParams,
                                       boolean withContent, String... uris)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getBulkDocuments not yet implemented");
    }

    @Override
    public DocumentPage getBulkDocuments(RequestLogger logger, long serverTimestamp, SearchQueryDefinition querydef,
                                       long start, long pageLength, Transaction transaction, SearchReadHandle searchHandle,
                                       QueryView view, Set<Metadata> categories, Format format, ServerTransform responseTransform,
                                       RequestParameters extraParams, String forestName)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getBulkDocuments with search not yet implemented");
    }

    @Override
    public <T extends AbstractReadHandle> void postBulkDocuments(RequestLogger logger, DocumentWriteSet writeSet,
                                                            ServerTransform transform, Transaction transaction, Format defaultFormat, T output,
                                                            String temporalCollection, String extraContentDispositionParams)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("postBulkDocuments not yet implemented");
    }

    @Override
    public TemporalDescriptor putDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                                        Set<Metadata> categories, RequestParameters extraParams,
                                        DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle)
            throws ResourceNotFoundException, ResourceNotResendableException,
            ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("putDocument not yet implemented");
    }

    @Override
    public DocumentDescriptorImpl postDocument(RequestLogger logger, DocumentUriTemplate template,
                                             Transaction transaction, Set<Metadata> categories, RequestParameters extraParams,
                                             DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("postDocument not yet implemented");
    }

    @Override
    public void patchDocument(RequestLogger logger, DocumentDescriptor desc, Transaction transaction,
                            Set<Metadata> categories, boolean isOnContent, DocumentPatchHandle patchHandle)
            throws ResourceNotFoundException, ResourceNotResendableException,
            ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("patchDocument not yet implemented");
    }

    @Override
    public <T extends SearchReadHandle> T search(RequestLogger logger, T searchHandle, SearchQueryDefinition queryDef,
                                               long start, long len, QueryView view, Transaction transaction, String forestName)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("search not yet implemented");
    }

    @Override
    public void deleteSearch(RequestLogger logger, DeleteQueryDefinition queryDef, Transaction transaction)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("deleteSearch not yet implemented");
    }

    @Override
    public void delete(RequestLogger logger, Transaction transaction, Set<Metadata> categories, String... uris)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("delete not yet implemented");
    }

    @Override
    public Transaction openTransaction(String name, int timeLimit)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("openTransaction not yet implemented");
    }

    @Override
    public void commitTransaction(Transaction transaction)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("commitTransaction not yet implemented");
    }

    @Override
    public void rollbackTransaction(Transaction transaction)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("rollbackTransaction not yet implemented");
    }

    @Override
    public <T> T values(Class<T> as, ValuesDefinition valdef, String mimetype, long start, long pageLength, Transaction transaction)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("values not yet implemented");
    }

    @Override
    public <T> T valuesList(Class<T> as, ValuesListDefinition valdef, String mimetype, Transaction transaction)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("valuesList not yet implemented");
    }

    @Override
    public <T> T optionsList(Class<T> as, String mimetype, Transaction transaction)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("optionsList not yet implemented");
    }

    @Override
    public <T> T getValue(RequestLogger logger, String type, String key,
                        boolean isNullable, String mimetype, Class<T> as)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getValue not yet implemented");
    }

    @Override
    public <T> T getValue(RequestLogger logger, String type, String key, Transaction transaction,
                        boolean isNullable, String mimetype, Class<T> as)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getValue with transaction not yet implemented");
    }

    @Override
    public <T> T getValues(RequestLogger logger, String type, String mimetype, Class<T> as)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getValues not yet implemented");
    }

    @Override
    public <T> T getValues(RequestLogger reqlog, String type, RequestParameters extraParams,
                         String mimetype, Class<T> as)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getValues with params not yet implemented");
    }

    @Override
    public void putValue(RequestLogger logger, String type, String key,
                       String mimetype, Object value)
            throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("putValue not yet implemented");
    }

    @Override
    public void putValue(RequestLogger logger, String type, String key, RequestParameters extraParams,
                       String mimetype, Object value)
            throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("putValue with params not yet implemented");
    }

    @Override
    public void deleteValue(RequestLogger logger, String type, String key)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("deleteValue not yet implemented");
    }

    @Override
    public <R extends UrisReadHandle> R uris(RequestLogger reqlog, String method, SearchQueryDefinition qdef,
                       Boolean filtered, long start, String afterUri, long pageLength, String forestName, R output)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("uris not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R forestInfo(RequestLogger reqlog,
                       String method, RequestParameters params, SearchQueryDefinition qdef, R output)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("forestInfo not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R getResource(RequestLogger reqlog, String path,
                                                      Transaction transaction, RequestParameters params, R output)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getResource not yet implemented");
    }

    @Override
    public RESTServiceResultIterator getIteratedResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getIteratedResource not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R putResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
            AbstractWriteHandle input, R output)
            throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("putResource not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R putResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
            W[] input, R output)
            throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("putResource with array not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R postResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
            AbstractWriteHandle input, R output)
            throws ResourceNotFoundException, ResourceNotResendableException,
            ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("postResource not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
            W[] input, R output)
            throws ResourceNotFoundException, ResourceNotResendableException,
            ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("postResource with array not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
            W[] input, Map<String, List<String>>[] headers, R output)
            throws ResourceNotFoundException, ResourceNotResendableException,
            ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("postResource with headers not yet implemented");
    }

    @Override
    public RESTServiceResultIterator postIteratedResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
            AbstractWriteHandle input)
            throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("postIteratedResource not yet implemented");
    }

    @Override
    public RESTServiceResultIterator postMultipartForm(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params, List<ContentParam> contentParams)
            throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("postMultipartForm not yet implemented");
    }

    @Override
    public <W extends AbstractWriteHandle> RESTServiceResultIterator postIteratedResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
            W[] input)
            throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("postIteratedResource with array not yet implemented");
    }

    @Override
    public EvalResultIterator postEvalInvoke(RequestLogger reqlog, String code, String modulePath,
                                           ServerEvaluationCallImpl.Context evalContext, Map<String, Object> variables,
                                           EditableNamespaceContext namespaces, Transaction transaction)
            throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("postEvalInvoke not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R deleteResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params, R output)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("deleteResource not yet implemented");
    }

    @Override
    public ConnectionResult checkConnection() {
        throw new UnsupportedOperationException("checkConnection not yet implemented");
    }

    @Override
    public <T> T suggest(Class<T> as, SuggestDefinition suggestionDef) {
        throw new UnsupportedOperationException("suggest not yet implemented");
    }

    @Override
    public InputStream match(StructureWriteHandle document, String[] candidateRules, String mimeType, ServerTransform transform) {
        throw new UnsupportedOperationException("match not yet implemented");
    }

    @Override
    public InputStream match(String[] docIds, String[] candidateRules, ServerTransform transform) {
        throw new UnsupportedOperationException("match with docIds not yet implemented");
    }

    @Override
    public InputStream match(QueryDefinition queryDef, long start, long pageLength, String[] candidateRules, ServerTransform transform) {
        throw new UnsupportedOperationException("match with query not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R getGraphUris(RequestLogger reqlog, R output)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getGraphUris not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> void readGraph(RequestLogger reqlog, String uri, R output,
                                                    Transaction transaction)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("readGraph not yet implemented");
    }

    @Override
    public void writeGraph(RequestLogger reqlog, String uri,
                         AbstractWriteHandle input, GraphPermissions permissions, Transaction transaction)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("writeGraph not yet implemented");
    }

    @Override
    public void writeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("writeGraphs not yet implemented");
    }

    @Override
    public void deleteGraph(RequestLogger requestLogger, String uri,
                            Transaction transaction)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("deleteGraph not yet implemented");
    }

    @Override
    public void deleteGraphs(RequestLogger requestLogger, Transaction transaction)
            throws ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("deleteGraphs not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R executeSparql(RequestLogger reqlog,
                                                        SPARQLQueryDefinition qdef, R output, long start, long pageLength,
                                                        Transaction transaction, boolean isUpdate) {
        throw new UnsupportedOperationException("executeSparql not yet implemented");
    }

    @Override
    public boolean exists(String uri) {
        throw new UnsupportedOperationException("exists not yet implemented");
    }

    @Override
    public void mergeGraph(RequestLogger reqlog, String uri, AbstractWriteHandle input,
                         GraphPermissions permissions, Transaction transaction)
            throws ResourceNotFoundException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("mergeGraph not yet implemented");
    }

    @Override
    public void mergeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("mergeGraphs not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R getPermissions(RequestLogger reqlog, String uri,
                                                         R output, Transaction transaction)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getPermissions not yet implemented");
    }

    @Override
    public void deletePermissions(RequestLogger reqlog, String uri, Transaction transaction)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("deletePermissions not yet implemented");
    }

    @Override
    public void writePermissions(RequestLogger reqlog, String uri,
                               AbstractWriteHandle permissions, Transaction transaction)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("writePermissions not yet implemented");
    }

    @Override
    public void mergePermissions(RequestLogger reqlog, String uri,
                               AbstractWriteHandle permissions, Transaction transaction)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("mergePermissions not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R getThings(RequestLogger reqlog, String[] iris, R output)
            throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("getThings not yet implemented");
    }

    @Override
    public String advanceLsqt(RequestLogger reqlog, String temporalCollection, long lag) {
        throw new UnsupportedOperationException("advanceLsqt not yet implemented");
    }

    @Override
    public void wipeDocument(RequestLogger requestLogger, String temporalDocumentURI, Transaction transaction,
                           RequestParameters extraParams) {
        throw new UnsupportedOperationException("wipeDocument not yet implemented");
    }

    @Override
    public void protectDocument(RequestLogger requestLogger, String temporalDocumentURI, Transaction transaction,
                              RequestParameters extraParams, ProtectionLevel level, String duration, Calendar expiryTime, String archivePath) {
        throw new UnsupportedOperationException("protectDocument not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R postResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
            AbstractWriteHandle input, R output, String operation)
            throws ResourceNotFoundException, ResourceNotResendableException,
            ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("postResource with operation not yet implemented");
    }

    @Override
    public <R extends AbstractReadHandle> R postResource(
            RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
            AbstractWriteHandle input, R output, String operation, Map<String, List<String>> responseHeaders)
            throws ResourceNotFoundException, ResourceNotResendableException,
            ForbiddenUserException, FailedRequestException {
        throw new UnsupportedOperationException("postResource with operation and headers not yet implemented");
    }

    @Override
    public void patchDocument(RequestLogger reqlog, DocumentDescriptor desc, Transaction transaction, Set<Metadata> categories, boolean isOnContent,
                            RequestParameters extraParams, String sourceDocumentURI, DocumentPatchHandle patchHandle)
            throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
            FailedRequestException {
        throw new UnsupportedOperationException("patchDocument with extra params not yet implemented");
    }

    // API First Additions
    @Override
    public CallRequest makeEmptyRequest(String endpoint, HttpMethod method, SessionState session) {
        throw new UnsupportedOperationException("makeEmptyRequest not yet implemented");
    }

    @Override
    public CallRequest makeAtomicBodyRequest(String endpoint, HttpMethod method, SessionState session, CallField... params) {
        throw new UnsupportedOperationException("makeAtomicBodyRequest not yet implemented");
    }

    @Override
    public CallRequest makeNodeBodyRequest(String endpoint, HttpMethod method, SessionState session, CallField... params) {
        throw new UnsupportedOperationException("makeNodeBodyRequest not yet implemented");
    }
}
