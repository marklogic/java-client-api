/*
 * Copyright 2012-2014 MarkLogic Corporation
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

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.document.ContentDescriptor;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.io.marker.SearchReadHandle;
import com.marklogic.client.io.marker.StructureWriteHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.ElementLocator;
import com.marklogic.client.query.KeyLocator;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.RawQueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.SuggestDefinition;
import com.marklogic.client.query.ValueLocator;
import com.marklogic.client.query.ValueQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesListDefinition;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.util.RequestParameters;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.Boundary;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class JerseyServices implements RESTServices {
	static final private Logger logger = LoggerFactory
			.getLogger(JerseyServices.class);
	static final String ERROR_NS = "http://marklogic.com/rest-api";

	static final private String DOCUMENT_URI_PREFIX = "/documents?uri=";

	static final private int DELAY_FLOOR       =    125;
	static final private int DELAY_CEILING     =   2000;
	static final private int DELAY_MULTIPLIER  =     20;
	static final private int DEFAULT_MAX_DELAY = 120000;
	static final private int DEFAULT_MIN_RETRY =      8;

	static final private String MAX_DELAY_PROP = "com.marklogic.client.maximumRetrySeconds";
	static final private String MIN_RETRY_PROP = "com.marklogic.client.minimumRetries";

	static protected class HostnameVerifierAdapter extends AbstractVerifier {
		private SSLHostnameVerifier verifier;

		protected HostnameVerifierAdapter(SSLHostnameVerifier verifier) {
			super();
			this.verifier = verifier;
		}

		@Override
		public void verify(String hostname, String[] cns, String[] subjectAlts)
				throws SSLException {
			verifier.verify(hostname, cns, subjectAlts);
		}
	}

	private DatabaseClient databaseClient;
	private ApacheHttpClient4 client;
	private WebResource connection;

	private Random randRetry    = new Random();

	private int maxDelay = DEFAULT_MAX_DELAY;
	private int minRetry = DEFAULT_MIN_RETRY;

	private boolean checkFirstRequest = false;

	static protected class ThreadState {
		boolean isFirstRequest;
		ThreadState(boolean value) {
			isFirstRequest = value;
		}
	}

	// workaround: Jersey keeps the DIGEST nonce in a thread-local variable
	private final ThreadLocal<ThreadState> threadState = new ThreadLocal<ThreadState>() {
        @Override
        protected ThreadState initialValue() {
            return new ThreadState(checkFirstRequest);
        }
    };

	public JerseyServices() {
	}

	private FailedRequest extractErrorFields(ClientResponse response) {
		if ( response == null ) return null;
		InputStream is = response.getEntityInputStream();
		try {
			FailedRequest handler = FailedRequest.getFailedRequest(
					response.getStatus(), response.getType(), is);
			return handler;
		} catch (RuntimeException e) {
			throw (e);
		} finally {
			response.close();
		}
	}

	@Override
	public void connect(String host, int port, String user, String password,
			Authentication authenType, SSLContext context,
			SSLHostnameVerifier verifier) {
		X509HostnameVerifier x509Verifier = null;
		if (verifier == null) {
			if (context != null)
				x509Verifier = SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
			}
		else if (verifier == SSLHostnameVerifier.ANY)
			x509Verifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		else if (verifier == SSLHostnameVerifier.COMMON)
			x509Verifier = SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
		else if (verifier == SSLHostnameVerifier.STRICT)
			x509Verifier = SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
		else if (context != null)
			x509Verifier = new HostnameVerifierAdapter(verifier);
		else
			throw new IllegalArgumentException(
					"Null SSLContent but non-null SSLHostnameVerifier for client");

		connect(host, port, user, password, authenType, context, x509Verifier);
	}

	private void connect(String host, int port, String user, String password,
			Authentication authenType, SSLContext context,
			X509HostnameVerifier verifier) {
		if (logger.isDebugEnabled())
			logger.debug("Connecting to {} at {} as {}", new Object[] { host,
					port, user });

		if (host == null)
			throw new IllegalArgumentException("No host provided");

		if (authenType == null) {
			if (context != null) {
				authenType = Authentication.BASIC;
			}
		}

		if (authenType != null) {
			if (user == null)
				throw new IllegalArgumentException("No user provided");
			if (password == null)
				throw new IllegalArgumentException("No password provided");
		}

		if (connection != null)
			connection = null;
		if (client != null) {
			client.destroy();
			client = null;
		}

		String baseUri = ((context == null) ? "http" : "https") + "://" + host
				+ ":" + port + "/v1/";

		Properties props = System.getProperties();

		if (props.containsKey(MAX_DELAY_PROP)) {
			String maxDelayStr = props.getProperty(MAX_DELAY_PROP);
			if (maxDelayStr != null && maxDelayStr.length() > 0) {
				int max = Integer.parseInt(maxDelayStr);
				if (max > 0) {
					maxDelay = max * 1000;
				}
			}
		}
		if (props.containsKey(MIN_RETRY_PROP)) {
			String minRetryStr = props.getProperty(MIN_RETRY_PROP);
			if (minRetryStr != null && minRetryStr.length() > 0) {
				int min = Integer.parseInt(minRetryStr);
				if (min > 0) {
					minRetry = min;
				}
			}
		}

		// TODO: integrated control of HTTP Client and Jersey Client logging
		if (!props.containsKey("org.apache.commons.logging.Log")) {
			System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		}
		if (!props.containsKey("org.apache.commons.logging.simplelog.log.org.apache.http")) {
			System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http",
				"warn");
		}
		if (!props.containsKey("org.apache.commons.logging.simplelog.log.org.apache.http.wire")) {
			System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http.wire",
				"warn");
		}

		Scheme scheme = null;
		if (context == null) {
			SchemeSocketFactory socketFactory = PlainSocketFactory
					.getSocketFactory();
			scheme = new Scheme("http", port, socketFactory);
		} else {
			SSLSocketFactory socketFactory = new SSLSocketFactory(context,
					verifier);
			scheme = new Scheme("https", port, socketFactory);
		}
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(scheme);

		int maxRouteConnections = 100;
		int maxTotalConnections = 2 * maxRouteConnections;

		/*
		 * 4.2 PoolingClientConnectionManager connMgr = new
		 * PoolingClientConnectionManager(schemeRegistry);
		 * connMgr.setMaxTotal(maxTotalConnections);
		 * connMgr.setDefaultMaxPerRoute(maxRouteConnections);
		 * connMgr.setMaxPerRoute( new HttpRoute(new HttpHost(baseUri)),
		 *     maxRouteConnections);
		 */
		// start 4.1
		ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(
				schemeRegistry);
		connMgr.setMaxTotal(maxTotalConnections);
		connMgr.setDefaultMaxPerRoute(maxRouteConnections);
		connMgr.setMaxForRoute(new HttpRoute(new HttpHost(baseUri)),
				maxRouteConnections);
		// end 4.1

		// CredentialsProvider credentialsProvider = new
		// BasicCredentialsProvider();
		// credentialsProvider.setCredentials(new AuthScope(host, port),
		// new UsernamePasswordCredentials(user, password));

		HttpParams httpParams = new BasicHttpParams();

		if (authenType != null) {
			List<String> authpref = new ArrayList<String>();

			if (authenType == Authentication.BASIC)
				authpref.add(AuthPolicy.BASIC);
			else if (authenType == Authentication.DIGEST)
				authpref.add(AuthPolicy.DIGEST);
			else
				throw new MarkLogicInternalException(
						"Internal error - unknown authentication type: "
								+ authenType.name());

			httpParams.setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
		}

		httpParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        // HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);

        // long-term alternative to isFirstRequest alive
		// HttpProtocolParams.setUseExpectContinue(httpParams, false);
		// httpParams.setIntParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE, 1000);

		DefaultApacheHttpClient4Config config = new DefaultApacheHttpClient4Config();
		Map<String, Object> configProps = config.getProperties();
		configProps
				.put(ApacheHttpClient4Config.PROPERTY_PREEMPTIVE_BASIC_AUTHENTICATION,
						false);
		configProps.put(ApacheHttpClient4Config.PROPERTY_CONNECTION_MANAGER,
				connMgr);
		// ignored?
		configProps.put(ApacheHttpClient4Config.PROPERTY_FOLLOW_REDIRECTS,
				false);
		// configProps.put(ApacheHttpClient4Config.PROPERTY_CREDENTIALS_PROVIDER,
		// credentialsProvider);
		configProps.put(ApacheHttpClient4Config.PROPERTY_HTTP_PARAMS,
				httpParams);
		// switches from buffered to streamed in Jersey Client
		configProps.put(ApacheHttpClient4Config.PROPERTY_CHUNKED_ENCODING_SIZE,
				32 * 1024);

		client = ApacheHttpClient4.create(config);

		// System.setProperty("javax.net.debug", "all"); // all or ssl

		if (authenType == null) {
			checkFirstRequest = false;
		} else if (authenType == Authentication.BASIC) {
			checkFirstRequest = false;

			client.addFilter(new HTTPBasicAuthFilter(user, password));
		} else if (authenType == Authentication.DIGEST) {
			checkFirstRequest = true;

			// workaround for JerseyClient bug 1445
			client.addFilter(new DigestChallengeFilter());

			client.addFilter(new HTTPDigestAuthFilter(user, password));
		} else {
			throw new MarkLogicInternalException(
					"Internal error - unknown authentication type: "
							+ authenType.name());
		}

		// client.addFilter(new LoggingFilter(System.err));

		connection = client.resource(baseUri);
	}

	@Override
	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}
	@Override
	public void setDatabaseClient(DatabaseClient client) {
		this.databaseClient = client;
	}

	@Override
	public void release() {
		if (databaseClient != null) {
			databaseClient = null;
		}

		if (client == null)
			return;

		if (logger.isDebugEnabled())
			logger.debug("Releasing connection");

		connection = null;
		client.destroy();
		client = null;
	}

	private boolean isFirstRequest() {
		return threadState.get().isFirstRequest;
	}
	private void setFirstRequest(boolean value) {
		threadState.get().isFirstRequest = value;
	}
	private void checkFirstRequest() {
		if (checkFirstRequest)
			setFirstRequest(true);
	}

	private int makeFirstRequest(int retry) {
		ClientResponse response = connection.path("ping").head();
		int statusCode = response.getClientResponseStatus().getStatusCode();
		if (statusCode != ClientResponse.Status.SERVICE_UNAVAILABLE.getStatusCode()) {
			response.close();
			return 0;
		}

		MultivaluedMap<String, String> responseHeaders = response.getHeaders();
		response.close();

		String retryAfterRaw = responseHeaders.getFirst("Retry-After");
		int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
		return Math.max(retryAfter, calculateDelay(randRetry, retry));
	}

	@Override
	public void deleteDocument(RequestLogger reqlog, DocumentDescriptor desc,
			String transactionId, Set<Metadata> categories)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document delete for document identifier without uri");

		if (logger.isDebugEnabled())
			logger.debug("Deleting {} in transaction {}", uri, transactionId);

		WebResource webResource = makeDocumentResource(makeDocumentParams(uri,
				categories, transactionId, null));

		WebResource.Builder builder = addVersionHeader(desc,
				webResource.getRequestBuilder(), "If-Match");
		builder = addTransactionCookie(builder, transactionId);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.delete(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.NOT_FOUND) {
			response.close();
			throw new ResourceNotFoundException(
					"Could not delete non-existent document");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			FailedRequest failure = extractErrorFields(response);
			if (failure.getMessageCode().equals("RESTAPI-CONTENTNOVERSION"))
				throw new FailedRequestException(
						"Content version required to delete document", failure);
			throw new ForbiddenUserException(
					"User is not allowed to delete documents", failure);
		}
		if (status == ClientResponse.Status.PRECONDITION_FAILED) {
			FailedRequest failure = extractErrorFields(response);
			if (failure.getMessageCode().equals("RESTAPI-CONTENTWRONGVERSION"))
				throw new FailedRequestException(
						"Content version must match to delete document",
						failure);
			else if (failure.getMessageCode().equals("RESTAPI-EMPTYBODY"))
				throw new FailedRequestException(
						"Empty request body sent to server", failure);
			throw new FailedRequestException("Precondition Failed", failure);
		}
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("delete failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));

		response.close();
		logRequest(reqlog, "deleted %s document", uri);
	}

	@Override
	public boolean getDocument(RequestLogger reqlog, DocumentDescriptor desc,
			String transactionId, Set<Metadata> categories,
			RequestParameters extraParams,
			DocumentMetadataReadHandle metadataHandle,
			AbstractReadHandle contentHandle) throws ResourceNotFoundException,
			ForbiddenUserException, FailedRequestException {

		HandleImplementation metadataBase = HandleAccessor.checkHandle(
				metadataHandle, "metadata");
		HandleImplementation contentBase = HandleAccessor.checkHandle(
				contentHandle, "content");

		String metadataFormat = null;
		String metadataMimetype = null;
		if (metadataBase != null) {
			metadataFormat = metadataBase.getFormat().toString().toLowerCase();
			metadataMimetype = metadataBase.getMimetype();
		}

		String contentMimetype = null;
		if (contentBase != null) {
			contentMimetype = contentBase.getMimetype();
		}

		if (metadataBase != null && contentBase != null) {
			return getDocumentImpl(reqlog, desc, transactionId, categories,
					extraParams, metadataFormat, metadataHandle, contentHandle);
		} else if (metadataBase != null) {
			return getDocumentImpl(reqlog, desc, transactionId, categories,
					extraParams, metadataMimetype, metadataHandle);
		} else if (contentBase != null) {
			return getDocumentImpl(reqlog, desc, transactionId, null,
					extraParams, contentMimetype, contentHandle);
		}

		return false;
	}

	private boolean getDocumentImpl(RequestLogger reqlog,
			DocumentDescriptor desc, String transactionId,
			Set<Metadata> categories, RequestParameters extraParams,
			String mimetype, AbstractReadHandle handle)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document read for document identifier without uri");

		if (logger.isDebugEnabled())
			logger.debug("Getting {} in transaction {}", uri, transactionId);

		WebResource.Builder builder = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId, extraParams))
				.accept(mimetype);
		builder = addTransactionCookie(builder, transactionId);

		if (extraParams != null && extraParams.containsKey("range"))
			builder = builder.header("range", extraParams.get("range").get(0));

		builder = addVersionHeader(desc, builder, "If-None-Match");

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException(
					"Could not read non-existent document",
					extractErrorFields(response));
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException(
					"User is not allowed to read documents",
					extractErrorFields(response));
		if (status == ClientResponse.Status.NOT_MODIFIED) {
			response.close();
			return false;
		}
		if (status != ClientResponse.Status.OK
				&& status != ClientResponse.Status.PARTIAL_CONTENT)
			throw new FailedRequestException("read failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));

		logRequest(
				reqlog,
				"read %s document from %s transaction with %s mime type and %s metadata categories",
				uri, (transactionId != null) ? transactionId : "no",
				(mimetype != null) ? mimetype : "no",
				stringJoin(categories, ", ", "no"));

		HandleImplementation handleBase = HandleAccessor.as(handle);

		MultivaluedMap<String, String> responseHeaders = response.getHeaders();
		if (isExternalDescriptor(desc)) {
			updateVersion(desc, responseHeaders);
			updateDescriptor(desc, responseHeaders);
			copyDescriptor(desc, handleBase);
		} else {
			updateDescriptor(handleBase, responseHeaders);
		}

		Class as = handleBase.receiveAs();
		Object entity = response.hasEntity() ? response.getEntity(as) : null;

		if (entity == null ||
				(!InputStream.class.isAssignableFrom(as) && !Reader.class.isAssignableFrom(as)))
			response.close();

		handleBase.receiveContent((reqlog != null) ? reqlog.copyContent(entity)
				: entity);

		return true;
	}

    @Override
	public DocumentPage getBulkDocuments(RequestLogger reqlog,
			String transactionId, Set<Metadata> categories, 
			Format format, RequestParameters extraParams, boolean withContent, String... uris)
			throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		boolean hasMetadata = categories != null && categories.size() > 0;
		JerseyResultIterator iterator = 
			getBulkDocumentsImpl(reqlog, transactionId, categories, format, extraParams, withContent, uris);
		return convertToDocumentPage(iterator, withContent, hasMetadata);
	}

    @Override
	public DocumentPage getBulkDocuments(RequestLogger reqlog,
			QueryDefinition querydef,
			long start, long pageLength,
			String transactionId,
			SearchReadHandle searchHandle, QueryView view,
			Set<Metadata> categories, Format format, RequestParameters extraParams)
			throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		boolean hasMetadata = categories != null && categories.size() > 0;
		boolean hasContent = true;
		JerseyResultIterator iterator = 
			getBulkDocumentsImpl(reqlog, querydef, start, pageLength, transactionId, 
				searchHandle, view, categories, format, extraParams);
		return convertToDocumentPage(iterator, hasContent, hasMetadata);
	}

	private DocumentPage convertToDocumentPage(JerseyResultIterator iterator, boolean hasContent,
		boolean hasMetadata)
	{
		ArrayList<DocumentRecord> records = new ArrayList<DocumentRecord>();
        if ( iterator == null ) {
            return new DocumentPageImpl(records, 1, 0, 0, 0);
        }
		while (iterator.hasNext()) {
			JerseyResult result = iterator.next();
			DocumentRecord record;
			if ( hasContent && hasMetadata ) {
				JerseyResult metadata = result;
				JerseyResult content = iterator.next();
				record = new JerseyDocumentRecord(content, metadata);
			} else if ( hasContent ) {
				JerseyResult content = result;
				record = new JerseyDocumentRecord(content);
			} else if ( hasMetadata ) {
				JerseyResult metadata = result;
				record = new JerseyDocumentRecord(null, metadata);
			} else {
				throw new IllegalStateException("Should never have neither content nor metadata");
			}
			records.add(record);
		}
		return new DocumentPageImpl(records, iterator.getStart(), 
            iterator.getSize(), iterator.getPageSize(), iterator.getTotalSize());
	}

	private JerseyResultIterator getBulkDocumentsImpl(RequestLogger reqlog,
			String transactionId, Set<Metadata> categories, 
			Format format, RequestParameters extraParams, boolean withContent, String... uris)
			throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {

		String path = "documents";
		RequestParameters params = new RequestParameters();
		if ( extraParams != null ) params.putAll(extraParams);
		if (transactionId != null) params.add("txid",       transactionId);
		addCategoryParams(categories, params, withContent);
		if (format != null)        params.add("format",     format.toString().toLowerCase());
		for (String uri: uris) {
			params.add("uri", uri);
		}
		return getIteratedResourceImpl(DefaultJerseyResultIterator.class,
			reqlog, path, params, MultiPartMediaTypes.MULTIPART_MIXED);
	}

	private JerseyResultIterator getBulkDocumentsImpl(RequestLogger reqlog,
			QueryDefinition querydef, long start, long pageLength,
			String transactionId, SearchReadHandle searchHandle, QueryView view,
            Set<Metadata> categories, Format format, RequestParameters extraParams)
			throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		if ( extraParams != null ) params.putAll(extraParams);
		boolean withContent = true;
		addCategoryParams(categories, params, withContent);
		if (searchHandle != null && view != null) params.add("view", view.toString().toLowerCase());
		if (start > 1)             params.add("start",      Long.toString(start));
		if (pageLength > 0)        params.add("pageLength", Long.toString(pageLength));
		if (format != null)        params.add("format",     format.toString().toLowerCase());
		if (transactionId != null) params.add("txid",       transactionId);

		JerseySearchRequest request = 
			generateSearchRequest(reqlog, querydef, MultiPartMediaTypes.MULTIPART_MIXED, params);
        ClientResponse response = request.getResponse();
        if ( response == null ) return null;
        MultiPart entity = null;
        if ( searchHandle != null ) {
            if ( response.hasEntity() ) {
                entity = response.getEntity(MultiPart.class);
                if ( entity != null ) {
                    List<BodyPart> partList = entity.getBodyParts();
                    if ( partList != null && partList.size() > 0 ) {
                        BodyPart searchResponsePart = partList.get(0);
                        HandleImplementation handleBase = HandleAccessor.as(searchHandle);

                        InputStream stream = searchResponsePart.getEntityAs(InputStream.class);
                        handleBase.receiveContent(stream);
                        partList = partList.subList(1, partList.size());
                    }
                    return makeResults(JerseyServiceResultIterator.class, reqlog, "read", "resource", partList, response);
                }
            }
        }
        return makeResults(JerseyServiceResultIterator.class, reqlog, "read", "resource", response);
	}

	private boolean getDocumentImpl(RequestLogger reqlog,
			DocumentDescriptor desc, String transactionId,
			Set<Metadata> categories, RequestParameters extraParams,
			String metadataFormat, DocumentMetadataReadHandle metadataHandle,
			AbstractReadHandle contentHandle) throws ResourceNotFoundException,
			ForbiddenUserException, FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document read for document identifier without uri");

		assert metadataHandle != null : "metadataHandle is null";
		assert contentHandle != null : "contentHandle is null";

		if (logger.isDebugEnabled())
			logger.debug("Getting multipart for {} in transaction {}", uri,
					transactionId);

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transactionId, extraParams, true);
		docParams.add("format", metadataFormat);

		WebResource.Builder builder = makeDocumentResource(docParams).getRequestBuilder();
		builder = addTransactionCookie(builder, transactionId);
		builder = addVersionHeader(desc, builder, "If-None-Match");

		MediaType multipartType = Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.accept(multipartType).get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException(
					"Could not read non-existent document",
					extractErrorFields(response));
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException(
					"User is not allowed to read documents",
					extractErrorFields(response));
		if (status == ClientResponse.Status.NOT_MODIFIED) {
			response.close();
			return false;
		}
		if (status != ClientResponse.Status.OK)
			throw new FailedRequestException("read failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));

		logRequest(
				reqlog,
				"read %s document from %s transaction with %s metadata categories and content",
				uri, (transactionId != null) ? transactionId : "no",
				stringJoin(categories, ", ", "no"));

		MultiPart entity = response.hasEntity() ?
				response.getEntity(MultiPart.class) : null;
		if (entity == null)
			return false;

		List<BodyPart> partList = entity.getBodyParts();
		if (partList == null)
			return false;

		int partCount = partList.size();
		if (partCount == 0)
			return false;
		if (partCount != 2)
			throw new FailedRequestException("read expected 2 parts but got "
					+ partCount + " parts");

		HandleImplementation metadataBase = HandleAccessor.as(metadataHandle);
		HandleImplementation contentBase = HandleAccessor.as(contentHandle);

		BodyPart contentPart = partList.get(1);

		MultivaluedMap<String, String> responseHeaders = response.getHeaders();
		MultivaluedMap<String, String> contentHeaders = contentPart
				.getHeaders();
		if (isExternalDescriptor(desc)) {
			updateVersion(desc, responseHeaders);
			updateFormat(desc, responseHeaders);
			updateMimetype(desc, contentHeaders);
			updateLength(desc, contentHeaders);
			copyDescriptor(desc, contentBase);
		} else {
			updateFormat(contentBase, responseHeaders);
			updateMimetype(contentBase, contentHeaders);
			updateLength(contentBase, contentHeaders);
		}

		metadataBase.receiveContent(partList.get(0).getEntityAs(
				metadataBase.receiveAs()));

		Object contentEntity = contentPart.getEntityAs(contentBase.receiveAs());
		contentBase.receiveContent((reqlog != null) ? reqlog
				.copyContent(contentEntity) : contentEntity);

		response.close();

		return true;
	}

	@Override
	public DocumentDescriptor head(RequestLogger reqlog, String uri,
			String transactionId) throws ForbiddenUserException,
			FailedRequestException {
		ClientResponse response = headImpl(reqlog, uri, transactionId, makeDocumentResource(makeDocumentParams(uri,
				null, transactionId, null)));
		
		// 404
		if (response == null) return null;
		
		MultivaluedMap<String, String> responseHeaders = response.getHeaders();

		response.close();
		logRequest(reqlog, "checked %s document from %s transaction", uri,
				(transactionId != null) ? transactionId : "no");

		DocumentDescriptorImpl desc = new DocumentDescriptorImpl(uri, false);

		updateVersion(desc, responseHeaders);
		updateDescriptor(desc, responseHeaders);

		return desc;
	}
	
	@Override
	public boolean exists(String uri) throws ForbiddenUserException,
			FailedRequestException {
		return headImpl(null, uri, null, connection.path(uri)) == null ? false : true;
	}
	
	public ClientResponse headImpl(RequestLogger reqlog, String uri,
			String transactionId, WebResource webResource) {
		if (uri == null)
			throw new IllegalArgumentException(
					"Existence check for document identifier without uri");

		if (logger.isDebugEnabled())
			logger.debug("Requesting head for {} in transaction {}", uri,
					transactionId);

		WebResource.Builder builder = webResource.getRequestBuilder();
		builder = addTransactionCookie(builder, transactionId);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.head();
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status != ClientResponse.Status.OK) {
			if (status == ClientResponse.Status.NOT_FOUND) {
				response.close();
				return null;
			} else if (status == ClientResponse.Status.FORBIDDEN)
				throw new ForbiddenUserException(
						"User is not allowed to check the existence of documents",
						extractErrorFields(response));
			else
				throw new FailedRequestException(
						"Document existence check failed: "
								+ status.getReasonPhrase(),
						extractErrorFields(response));
		}
		return response;
	}

	@Override
	public void putDocument(RequestLogger reqlog, DocumentDescriptor desc,
			String transactionId, Set<Metadata> categories,
			RequestParameters extraParams,
			DocumentMetadataWriteHandle metadataHandle,
			AbstractWriteHandle contentHandle)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if (desc.getUri() == null)
			throw new IllegalArgumentException(
					"Document write for document identifier without uri");

		HandleImplementation metadataBase = HandleAccessor.checkHandle(
				metadataHandle, "metadata");
		HandleImplementation contentBase = HandleAccessor.checkHandle(
				contentHandle, "content");

		String metadataMimetype = null;
		if (metadataBase != null) {
			metadataMimetype = metadataBase.getMimetype();
		}

		Format descFormat = desc.getFormat();
		String contentMimetype = (descFormat != null && descFormat != Format.UNKNOWN) ? desc
				.getMimetype() : null;
		if (contentMimetype == null && contentBase != null) {
			Format contentFormat = contentBase.getFormat();
			if (descFormat != null && descFormat != contentFormat) {
				contentMimetype = descFormat.getDefaultMimetype();
			} else if (contentFormat != null && contentFormat != Format.UNKNOWN) {
				contentMimetype = contentBase.getMimetype();
			}
		}

		if (metadataBase != null && contentBase != null) {
			putPostDocumentImpl(reqlog, "put", desc, transactionId, categories,
					extraParams, metadataMimetype, metadataHandle,
					contentMimetype, contentHandle);
		} else if (metadataBase != null) {
			putPostDocumentImpl(reqlog, "put", desc, transactionId, categories, false,
					extraParams, metadataMimetype, metadataHandle);
		} else if (contentBase != null) {
			putPostDocumentImpl(reqlog, "put", desc, transactionId, null, true, 
					extraParams, contentMimetype, contentHandle);
		}
	}

	@Override
	public DocumentDescriptor postDocument(RequestLogger reqlog, DocumentUriTemplate template,
			String transactionId, Set<Metadata> categories, RequestParameters extraParams,
			DocumentMetadataWriteHandle metadataHandle, AbstractWriteHandle contentHandle)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		DocumentDescriptorImpl desc = new DocumentDescriptorImpl(false);

		HandleImplementation metadataBase = HandleAccessor.checkHandle(
				metadataHandle, "metadata");
		HandleImplementation contentBase = HandleAccessor.checkHandle(
				contentHandle, "content");

		String metadataMimetype = null;
		if (metadataBase != null) {
			metadataMimetype = metadataBase.getMimetype();
		}

		Format templateFormat = template.getFormat();
		String contentMimetype = (templateFormat != null && templateFormat != Format.UNKNOWN) ?
				template.getMimetype() : null;
		if (contentMimetype == null && contentBase != null) {
			Format contentFormat = contentBase.getFormat();
			if (templateFormat != null && templateFormat != contentFormat) {
				contentMimetype = templateFormat.getDefaultMimetype();
				desc.setFormat(templateFormat);
			} else if (contentFormat != null && contentFormat != Format.UNKNOWN) {
				contentMimetype = contentBase.getMimetype();
				desc.setFormat(contentFormat);
			}
		}
		desc.setMimetype(contentMimetype);

		if (extraParams == null)
			extraParams = new RequestParameters();

		String extension = template.getExtension();
		if (extension != null)
			extraParams.add("extension", extension);

		String directory = template.getDirectory();
		if (directory != null)
			extraParams.add("directory", directory);

		if (metadataBase != null && contentBase != null) {
			putPostDocumentImpl(reqlog, "post", desc, transactionId, categories, extraParams,
					metadataMimetype, metadataHandle, contentMimetype, contentHandle);
		} else if (contentBase != null) {
			putPostDocumentImpl(reqlog, "post", desc, transactionId, null, true, extraParams,
					contentMimetype, contentHandle);
		}

		return desc;
	}

	private void putPostDocumentImpl(RequestLogger reqlog, String method, DocumentDescriptor desc,
			String transactionId, Set<Metadata> categories, boolean isOnContent, RequestParameters extraParams,
			String mimetype, AbstractWriteHandle handle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		String uri = desc.getUri();

		HandleImplementation handleBase = HandleAccessor.as(handle);

		if (logger.isDebugEnabled())
			logger.debug("Sending {} document in transaction {}",
					(uri != null) ? uri : "new", transactionId);

		logRequest(
				reqlog,
				"writing %s document from %s transaction with %s mime type and %s metadata categories",
				(uri != null) ? uri : "new",
				(transactionId != null) ? transactionId : "no",
				(mimetype != null) ? mimetype : "no",
				stringJoin(categories, ", ", "no"));

		WebResource webResource = makeDocumentResource(
				makeDocumentParams(
						uri, categories, transactionId, extraParams, isOnContent
						));

		WebResource.Builder builder = webResource.type(
				(mimetype != null) ? mimetype : MediaType.WILDCARD);
		builder = addTransactionCookie(builder, transactionId);
		if (uri != null) {
			builder = addVersionHeader(desc, builder, "If-Match");
		}

		if ("patch".equals(method)) {
			builder = builder.header("X-HTTP-Method-Override", "PATCH");
			method  = "post";
		}
		boolean isResendable = handleBase.isResendable();

		ClientResponse response = null;
		ClientResponse.Status status = null;
		MultivaluedMap<String, String> responseHeaders = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			Object value = handleBase.sendContent();
			if (value == null)
				throw new IllegalArgumentException(
						"Document write with null value for " +
						((uri != null) ? uri : "new document"));

			if (isFirstRequest() && !isResendable && isStreaming(value)) {
				nextDelay = makeFirstRequest(retry);
				if (nextDelay != 0)
					continue;
			}

			if (value instanceof OutputStreamSender) {
				StreamingOutput sentStream =
					new StreamingOutputImpl((OutputStreamSender) value, reqlog);
				response =
					("put".equals(method)) ?
					builder.put(ClientResponse.class,  sentStream) :
					builder.post(ClientResponse.class, sentStream);
			} else {
				Object sentObj = (reqlog != null) ?
						reqlog.copyContent(value) : value;
				response =
					("put".equals(method)) ?
					builder.put(ClientResponse.class,  sentObj) :
					builder.post(ClientResponse.class, sentObj);
			}

			status = response.getClientResponseStatus();

			responseHeaders = response.getHeaders();
			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

 				break;
			}

			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			response.close();

			if (!isResendable) {
				checkFirstRequest();
				throw new ResourceNotResendableException(
						"Cannot retry request for " +
						 ((uri != null) ? uri : "new document"));
			}

			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException(
					"Could not write non-existent document",
					extractErrorFields(response));
		if (status == ClientResponse.Status.FORBIDDEN) {
			FailedRequest failure = extractErrorFields(response);
			if (failure.getMessageCode().equals("RESTAPI-CONTENTNOVERSION"))
				throw new FailedRequestException(
						"Content version required to write document", failure);
			throw new ForbiddenUserException(
					"User is not allowed to write documents", failure);
		}
		if (status == ClientResponse.Status.PRECONDITION_FAILED) {
			FailedRequest failure = extractErrorFields(response);
			if (failure.getMessageCode().equals("RESTAPI-CONTENTWRONGVERSION"))
				throw new FailedRequestException(
						"Content version must match to write document", failure);
			else if (failure.getMessageCode().equals("RESTAPI-EMPTYBODY"))
				throw new FailedRequestException(
						"Empty request body sent to server", failure);
			throw new FailedRequestException("Precondition Failed", failure);
		}
		if (status == null) {
			throw new FailedRequestException("write failed: Unknown Reason", extractErrorFields(response));
		}
		if (status != ClientResponse.Status.CREATED
				&& status != ClientResponse.Status.NO_CONTENT) {
			throw new FailedRequestException("write failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		if (uri == null) {
			String location = responseHeaders.getFirst("Location");
			if (location != null) {
				int offset = location.indexOf(DOCUMENT_URI_PREFIX);
				if (offset == -1)
					throw new MarkLogicInternalException(
							"document create produced invalid location: " + location);
				uri = location.substring(offset + DOCUMENT_URI_PREFIX.length());
				if (uri == null)
					throw new MarkLogicInternalException(
							"document create produced location without uri: " + location);
				desc.setUri(uri);
				updateVersion(desc, responseHeaders);
				updateDescriptor(desc, responseHeaders);
			}
		}

		response.close();
	}

	private void putPostDocumentImpl(RequestLogger reqlog, String method, DocumentDescriptor desc,
			String transactionId, Set<Metadata> categories, RequestParameters extraParams,
			String metadataMimetype, DocumentMetadataWriteHandle metadataHandle, String contentMimetype,
			AbstractWriteHandle contentHandle)
	throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		String uri = desc.getUri();

		if (logger.isDebugEnabled())
			logger.debug("Sending {} multipart document in transaction {}",
					(uri != null) ? uri : "new", transactionId);

		logRequest(
				reqlog,
				"writing %s document from %s transaction with %s metadata categories and content",
				(uri != null) ? uri : "new",
				(transactionId != null) ? transactionId : "no",
				stringJoin(categories, ", ", "no"));

		MultivaluedMap<String, String> docParams =
			makeDocumentParams(uri, categories, transactionId, extraParams, true);

		WebResource.Builder builder = makeDocumentResource(docParams).getRequestBuilder();
		builder = addTransactionCookie(builder, transactionId);
		if (uri != null) {
			builder = addVersionHeader(desc, builder, "If-Match");
		}

		MediaType multipartType = Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		MultivaluedMap<String, String> responseHeaders = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			MultiPart multiPart = new MultiPart();
			boolean hasStreamingPart = addParts(multiPart, reqlog,
					new String[] { metadataMimetype, contentMimetype },
					new AbstractWriteHandle[] { metadataHandle, contentHandle });

			if (isFirstRequest() && hasStreamingPart) {
				nextDelay = makeFirstRequest(retry);
				if (nextDelay != 0)
					continue;
			}

			// Must set multipart/mixed mime type explicitly on each request
			// because Jersey client 1.17 adapter for HttpClient switches
			// to application/octet-stream on retry
			WebResource.Builder requestBlder = builder.type(multipartType);
			response =
				("put".equals(method)) ?
				requestBlder.put(ClientResponse.class,  multiPart) :
				requestBlder.post(ClientResponse.class, multiPart);
			status = response.getClientResponseStatus();

			responseHeaders = response.getHeaders();
			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			response.close();

			if (hasStreamingPart) {
				throw new ResourceNotResendableException(
						"Cannot retry request for " +
						((uri != null) ? uri : "new document"));
			}

			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.NOT_FOUND) {
			response.close();
			throw new ResourceNotFoundException(
					"Could not write non-existent document");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			FailedRequest failure = extractErrorFields(response);
			if (failure.getMessageCode().equals("RESTAPI-CONTENTNOVERSION"))
				throw new FailedRequestException(
						"Content version required to write document", failure);
			throw new ForbiddenUserException(
					"User is not allowed to write documents", failure);
		}
		if (status == ClientResponse.Status.PRECONDITION_FAILED) {
			FailedRequest failure = extractErrorFields(response);
			if (failure.getMessageCode().equals("RESTAPI-CONTENTWRONGVERSION"))
				throw new FailedRequestException(
						"Content version must match to write document", failure);
			else if (failure.getMessageCode().equals("RESTAPI-EMPTYBODY"))
				throw new FailedRequestException(
						"Empty request body sent to server", failure);
			throw new FailedRequestException("Precondition Failed", failure);
		}
		if (status != ClientResponse.Status.CREATED
				&& status != ClientResponse.Status.NO_CONTENT) {
			throw new FailedRequestException("write failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		if (uri == null) {
			String location = responseHeaders.getFirst("Location");
			if (location != null) {
				int offset = location.indexOf(DOCUMENT_URI_PREFIX);
				if (offset == -1)
					throw new MarkLogicInternalException(
							"document create produced invalid location: " + location);
				uri = location.substring(offset + DOCUMENT_URI_PREFIX.length());
				if (uri == null)
					throw new MarkLogicInternalException(
							"document create produced location without uri: " + location);
				desc.setUri(uri);
				updateVersion(desc, responseHeaders);
				updateDescriptor(desc, responseHeaders);
			}
		}

		response.close();
	}

	@Override
	public void patchDocument(RequestLogger reqlog, DocumentDescriptor desc, String transactionId,
			Set<Metadata> categories, boolean isOnContent, DocumentPatchHandle patchHandle)
	throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		HandleImplementation patchBase = HandleAccessor.checkHandle(
				patchHandle, "patch");

		putPostDocumentImpl(reqlog, "patch", desc, transactionId, categories, isOnContent, null,
				patchBase.getMimetype(), patchHandle);
	}

	@Override
	public String openTransaction(String name, int timeLimit)
			throws ForbiddenUserException, FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Opening transaction");

		MultivaluedMap<String, String> transParams = null;
		if (name != null || timeLimit > 0) {
			transParams = new MultivaluedMapImpl();
			if (name != null)
				addEncodedParam(transParams, "name", name);
			if (timeLimit > 0)
				transParams.add("timeLimit", String.valueOf(timeLimit));
		}

		WebResource resource = (transParams != null) ? connection.path(
				"transactions").queryParams(transParams) : connection
				.path("transactions");

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = resource.post(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException(
					"User is not allowed to open transactions",
					extractErrorFields(response));
		if (status != ClientResponse.Status.SEE_OTHER)
			throw new FailedRequestException("transaction open failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));

		String location = response.getHeaders().getFirst("Location");
		response.close();
		if (location == null)
			throw new MarkLogicInternalException(
					"transaction open failed to provide location");
		if (!location.contains("/"))
			throw new MarkLogicInternalException(
					"transaction open produced invalid location: " + location);

		return location.substring(location.lastIndexOf("/") + 1);
	}

	@Override
	public void commitTransaction(String transactionId)
			throws ForbiddenUserException, FailedRequestException {
		completeTransaction(transactionId, "commit");
	}

	@Override
	public void rollbackTransaction(String transactionId)
			throws ForbiddenUserException, FailedRequestException {
		completeTransaction(transactionId, "rollback");
	}

	private void completeTransaction(String transactionId, String result)
			throws ForbiddenUserException, FailedRequestException {
		if (result == null)
			throw new MarkLogicInternalException(
					"transaction completion without operation");
		if (transactionId == null)
			throw new MarkLogicInternalException(
					"transaction completion without id: " + result);

		if (logger.isDebugEnabled())
			logger.debug("Completing transaction {} with {}", transactionId,
					result);

		MultivaluedMap<String, String> transParams = new MultivaluedMapImpl();
		transParams.add("result", result);

		WebResource webResource = connection.path("transactions/" + transactionId)
				.queryParams(transParams);

		WebResource.Builder builder = webResource.getRequestBuilder();
		builder = addTransactionCookie(builder, transactionId);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.post(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException(
					"User is not allowed to complete transaction with "
							+ result, extractErrorFields(response));
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("transaction " + result
					+ " failed: " + status.getReasonPhrase(),
					extractErrorFields(response));

		response.close();
	}

	private void addCategoryParams(Set<Metadata> categories, MultivaluedMap<String, String> params,
		boolean withContent)
	{
		if (withContent && categories == null || categories.size() == 0) {
			params.add("category", "content");
		} else {
			if (withContent) params.add("category", "content");
			if (categories.contains(Metadata.ALL)) {
				params.add("category", "metadata");
			} else {
				for (Metadata category : categories) {
					params.add("category", category.name().toLowerCase());
				}
			}
		}
	}
	private void addCategoryParams(Set<Metadata> categories, RequestParameters params,
		boolean withContent)
	{
		if (withContent && categories == null || categories.size() == 0) {
			params.add("category", "content");
		} else {
			if (withContent) params.add("category", "content");
			if (categories.contains(Metadata.ALL)) {
				params.add("category", "metadata");
			} else {
				for (Metadata category : categories) {
					params.add("category", category.name().toLowerCase());
				}
			}
		}
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri,
			Set<Metadata> categories, String transactionId,
			RequestParameters extraParams) {
		return makeDocumentParams(uri, categories, transactionId, extraParams,
				false);
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri,
			Set<Metadata> categories, String transactionId,
			RequestParameters extraParams, boolean withContent) {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();
		if (extraParams != null && extraParams.size() > 0) {
			for (Map.Entry<String, List<String>> entry : extraParams.entrySet()) {
				String extraKey = entry.getKey();
				if (!"range".equalsIgnoreCase(extraKey)) {
					addEncodedParam(docParams, extraKey, entry.getValue());
				}
			}
		}
		addEncodedParam(docParams, "uri", uri);
		if (categories == null || categories.size() == 0) {
			docParams.add("category", "content");
		} else {
			if (withContent)
				docParams.add("category", "content");
			if (categories.contains(Metadata.ALL)) {
				docParams.add("category", "metadata");
			} else {
				for (Metadata category : categories)
					docParams.add("category", category.name().toLowerCase());
			}
		}
		if (transactionId != null) {
			docParams.add("txid", transactionId);
		}
		return docParams;
	}

	private WebResource makeDocumentResource(
			MultivaluedMap<String, String> queryParams) {
		return connection.path("documents").queryParams(queryParams);
	}

	private boolean isExternalDescriptor(ContentDescriptor desc) {
		return desc != null && desc instanceof DocumentDescriptorImpl
				&& !((DocumentDescriptorImpl) desc).isInternal();
	}

	private void updateDescriptor(ContentDescriptor desc,
			MultivaluedMap<String, String> headers) {
		if (desc == null || headers == null)
			return;

		updateFormat(desc, headers);
		updateMimetype(desc, headers);
		updateLength(desc, headers);
	}

	private void copyDescriptor(DocumentDescriptor desc,
			HandleImplementation handleBase) {
		if (handleBase == null)
			return;

		handleBase.setFormat(desc.getFormat());
		handleBase.setMimetype(desc.getMimetype());
		handleBase.setByteLength(desc.getByteLength());
	}

	private void updateFormat(ContentDescriptor descriptor,
			MultivaluedMap<String, String> headers) {
		updateFormat(descriptor, getHeaderFormat(headers));
	}

	private void updateFormat(ContentDescriptor descriptor, Format format) {
		if (format != null) {
			descriptor.setFormat(format);
		}
	}

	private Format getHeaderFormat(MultivaluedMap<String, String> headers) {
		if (headers.containsKey("vnd.marklogic.document-format")) {
			List<String> values = headers.get("vnd.marklogic.document-format");
			if (values != null) {
				return Format.valueOf(values.get(0).toUpperCase());
			}
		}
		return null;
	}

	private Format getHeaderFormat(BodyPart part) {
		if (part.getHeaders().containsKey("vnd.marklogic.document-format")) {
			List<String> values = part.getHeaders().get("vnd.marklogic.document-format");
			if (values != null) {
				return Format.valueOf(values.get(0).toUpperCase());
			}
        } else {
            ContentDisposition contentDisposition = part.getContentDisposition();
            if ( contentDisposition != null ) {
                Map<String, String> parameters = contentDisposition.getParameters();
                if ( parameters != null && parameters.get("format") != null ) {
                    return Format.valueOf(parameters.get("format").toUpperCase());
                }
            }
		}
		return null;
	}

	private void updateMimetype(ContentDescriptor descriptor,
			MultivaluedMap<String, String> headers) {
		updateMimetype(descriptor, getHeaderMimetype(headers));
	}

	private void updateMimetype(ContentDescriptor descriptor, String mimetype) {
		if (mimetype != null) {
			descriptor.setMimetype(mimetype);
		}
	}

	private String getHeaderMimetype(Map<String, List<String>> headers) {
		if (headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
			List<String> values = headers.get(HttpHeaders.CONTENT_TYPE);
			if (values != null) {
				String contentType = values.get(0);
				String mimetype = contentType.contains(";") ? contentType
						.substring(0, contentType.indexOf(";")) : contentType;
				// TODO: if "; charset=foo" set character set
				if (mimetype != null && mimetype.length() > 0) {
					return mimetype;
				}
			}
		}
		return null;
	}

	private void updateLength(ContentDescriptor descriptor,
			MultivaluedMap<String, String> headers) {
		updateLength(descriptor, getHeaderLength(headers));
	}

	private void updateLength(ContentDescriptor descriptor, long length) {
		descriptor.setByteLength(length);
	}

	private long getHeaderLength(MultivaluedMap<String, String> headers) {
		if (headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
			List<String> values = headers.get(HttpHeaders.CONTENT_LENGTH);
			if (values != null) {
				return Long.valueOf(values.get(0));
			}
		}
		return ContentDescriptor.UNKNOWN_LENGTH;
	}

	private String getHeaderUri(ContentDisposition contentDisposition) {
        if ( contentDisposition != null ) {
            return contentDisposition.getFileName();
        }
		// if it's not found, just return null
		return null;
	}

	private void updateVersion(DocumentDescriptor descriptor,
			MultivaluedMap<String, String> headers) {
		long version = DocumentDescriptor.UNKNOWN_VERSION;
		if (headers.containsKey("ETag")) {
			List<String> values = headers.get("ETag");
			if (values != null) {
				// trim the double quotes
				String value = values.get(0);
				version = Long.valueOf(value.substring(1, value.length() - 1));
			}
		}
		descriptor.setVersion(version);
	}

	private WebResource.Builder addTransactionCookie(
			WebResource.Builder builder, String transactionId) {
		if (transactionId != null) {
			int pos = transactionId.indexOf("_");
			if (pos != -1) {
				String hostId = transactionId.substring(0, pos);
				builder.cookie(new Cookie("HostId", hostId));
			} else {
				throw new IllegalArgumentException(
						"transaction id without host id separator: "+transactionId
						);
			}
		}
		return builder;
	}

	private WebResource.Builder addVersionHeader(DocumentDescriptor desc,
			WebResource.Builder builder, String name) {
		if (desc != null && desc instanceof DocumentDescriptorImpl
				&& !((DocumentDescriptorImpl) desc).isInternal()) {
			long version = desc.getVersion();
			if (version != DocumentDescriptor.UNKNOWN_VERSION) {
				return builder.header(name, "\"" + String.valueOf(version)
						+ "\"");
			}
		}
		return builder;
	}

	@Override
	public <T> T search(RequestLogger reqlog, Class<T> as, QueryDefinition queryDef, String mimetype,
			long start, long len, QueryView view, String transactionId
	) throws ForbiddenUserException, FailedRequestException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		if (start > 1) {
			params.add("start", Long.toString(start));
		}

		if (len > 0) {
			params.add("pageLength", Long.toString(len));
		}

		if (transactionId != null) {
			params.add("txid", transactionId);
		}

		if (view != null && view != QueryView.DEFAULT) {
			if (view == QueryView.ALL) {
				params.add("view", "all");
			} else if (view == QueryView.RESULTS) {
				params.add("view", "results");
			} else if (view == QueryView.FACETS) {
				params.add("view", "facets");
			} else if (view == QueryView.METADATA) {
				params.add("view", "metadata");
			}
		}

		T entity = search(reqlog, as, queryDef, mimetype, params);

		logRequest(
				reqlog,
				"searched starting at %s with length %s in %s transaction with %s mime type",
				start, len, transactionId, mimetype);
		
		return entity;
	}
	@Override
	public <T> T search(
			RequestLogger reqlog, Class<T> as, QueryDefinition queryDef, String mimetype, String view
	) throws ForbiddenUserException, FailedRequestException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		if (view != null) {
			params.add("view", view);
		}

		return search(reqlog, as, queryDef, mimetype, params);
	}
	private <T> T search(RequestLogger reqlog, Class<T> as, QueryDefinition queryDef, String mimetype,
			MultivaluedMap<String, String> params
	) throws ForbiddenUserException, FailedRequestException {

        JerseySearchRequest request = generateSearchRequest(reqlog, queryDef, mimetype, params);

        ClientResponse response = request.getResponse();		
        if ( response == null ) return null;

		T entity = response.hasEntity() ? response.getEntity(as) : null;
		if (entity == null || (as != InputStream.class && as != Reader.class))
			response.close();

		return entity;
	}

    private JerseySearchRequest generateSearchRequest(RequestLogger reqlog, QueryDefinition queryDef, 
            String mimetype, MultivaluedMap<String, String> params) {
        return new JerseySearchRequest(reqlog, queryDef, mimetype, params);
    }

    private class JerseySearchRequest {
        RequestLogger reqlog;
        QueryDefinition queryDef;
        String mimetype;
        MultivaluedMap<String, String> params;

		WebResource.Builder builder = null;
		String structure = null;
		HandleImplementation baseHandle = null;

        JerseySearchRequest(RequestLogger reqlog, QueryDefinition queryDef, String mimetype, 
                MultivaluedMap<String, String> params) {
            this.reqlog = reqlog;
            this.queryDef = queryDef;
            this.mimetype = mimetype;
            this.params = params != null ? params : new MultivaluedMapImpl();
            addParams();
            init();
        }

        void addParams() {
            String directory = queryDef.getDirectory();
            if (directory != null) {
                addEncodedParam(params, "directory", directory);
            }

            addEncodedParam(params, "collection", queryDef.getCollections());

            String optionsName = queryDef.getOptionsName();
            if (optionsName != null && optionsName.length() > 0) {
                addEncodedParam(params, "options", optionsName);
            }

            ServerTransform transform = queryDef.getResponseTransform();
            if (transform != null) {
                transform.merge(params);
            }
        }

        void init() {
            if (queryDef instanceof RawQueryDefinition) {
                if (logger.isDebugEnabled())
                    logger.debug("Raw search");

                StructureWriteHandle handle =
                    ((RawQueryDefinition) queryDef).getHandle();

                baseHandle = HandleAccessor.checkHandle(handle, "search");

                Format payloadFormat = baseHandle.getFormat();
                if (payloadFormat == Format.UNKNOWN)
                    payloadFormat = null;
                else if (payloadFormat != Format.XML && payloadFormat != Format.JSON)
                    throw new IllegalArgumentException(
                            "Cannot perform raw search for "+payloadFormat.name());
                // Fix bug https://github.com/marklogic/java-client-api/issues/29
                // by not specifying format parameter when payloadFormat differs
                // may remove when https://bugtrack.marklogic.com/27638 is resolved
                else if (payloadFormat == Format.JSON && "xml".equals(params.getFirst("format")))
                    params.remove("format");

                String payloadMimetype = baseHandle.getMimetype();
                if (payloadFormat != null) {
                    if (payloadMimetype == null)
                        payloadMimetype = payloadFormat.getDefaultMimetype();
                } else if (payloadMimetype == null) {
                    payloadMimetype = "application/xml";
                }

                String path = (queryDef instanceof RawQueryByExampleDefinition) ?
                    "qbe" : "search";

                WebResource resource = connection.path(path).queryParams(params);
                builder = (payloadMimetype != null) ?
                    resource.type(payloadMimetype).accept(mimetype) :
                    resource.accept(mimetype);
            } else if (queryDef instanceof StringQueryDefinition) {
                String text = ((StringQueryDefinition) queryDef).getCriteria();
                if (logger.isDebugEnabled())
                    logger.debug("Searching for {}", text);

                if (text != null) {
                    addEncodedParam(params, "q", text);
                }

                builder = connection.path("search").queryParams(params)
                    .type("application/xml").accept(mimetype);
            } else if (queryDef instanceof KeyValueQueryDefinition) {
                if (logger.isDebugEnabled())
                    logger.debug("Searching for keys/values");

                Map<ValueLocator, String> pairs = ((KeyValueQueryDefinition) queryDef);
                for (Map.Entry<ValueLocator, String> entry: pairs.entrySet()) {
                    ValueLocator loc = entry.getKey();
                    if (loc instanceof KeyLocator) {
                        addEncodedParam(params, "key", ((KeyLocator) loc).getKey());
                    } else {
                        ElementLocator eloc = (ElementLocator) loc;
                        params.add("element", eloc.getElement().toString());
                        if (eloc.getAttribute() != null) {
                            params.add("attribute", eloc.getAttribute().toString());
                        }
                    }
                    addEncodedParam(params, "value", entry.getValue());
                }

                builder = connection.path("keyvalue").queryParams(params)
                    .accept(mimetype);
            } else if (queryDef instanceof StructuredQueryDefinition) {
                structure = ((StructuredQueryDefinition) queryDef).serialize();

                if (logger.isDebugEnabled())
                    logger.debug("Searching for structure {}", structure);

                builder = connection.path("search").queryParams(params)
                    .type("application/xml").accept(mimetype);
            } else if (queryDef instanceof DeleteQueryDefinition) {
                if (logger.isDebugEnabled())
                    logger.debug("Searching for deletes");

                builder = connection.path("search").queryParams(params)
                    .accept(mimetype);
            } else {
                throw new UnsupportedOperationException("Cannot search with "
                        + queryDef.getClass().getName());
            }

            if (params.containsKey("txid")) {
                builder = addTransactionCookie(builder, params.getFirst("txid"));
            }
        }

        ClientResponse getResponse() {
            ClientResponse response = null;
            ClientResponse.Status status = null;
            long startTime = System.currentTimeMillis();
            int nextDelay = 0;
            int retry = 0;
            for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
                if (nextDelay > 0) {
                    try {
                        Thread.sleep(nextDelay);
                    } catch (InterruptedException e) {
                    }
                }

                if (queryDef instanceof StringQueryDefinition) {
                    response = doGet(builder);
                } else if (queryDef instanceof KeyValueQueryDefinition) {
                    response = doGet(builder);
                } else if (queryDef instanceof StructuredQueryDefinition) {
                    response = doPost(reqlog, builder, structure, true);
                } else if (queryDef instanceof DeleteQueryDefinition) {
                    response = doGet(builder);
                } else if (queryDef instanceof RawQueryDefinition) {
                    response = doPost(reqlog, builder, baseHandle.sendContent(), true);
                } else {
                    throw new UnsupportedOperationException("Cannot search with "
                            + queryDef.getClass().getName());
                }

                status = response.getClientResponseStatus();

                if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
                    if (isFirstRequest())
                        setFirstRequest(false);

                    break;
                }

                MultivaluedMap<String, String> responseHeaders = response.getHeaders();
                String retryAfterRaw = responseHeaders.getFirst("Retry-After");
                int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

                response.close();

                nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
            }
            if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
                checkFirstRequest();
                throw new FailedRequestException(
                        "Service unavailable and maximum retry period elapsed: "+
                                Math.round((System.currentTimeMillis() - startTime) / 1000)+
                                " seconds after "+retry+" retries");
            }
            if (status == ClientResponse.Status.NOT_FOUND) {
				response.close();
				return null;
            }
            if (status == ClientResponse.Status.FORBIDDEN) {
                throw new ForbiddenUserException("User is not allowed to search",
                        extractErrorFields(response));
            }
            if (status != ClientResponse.Status.OK) {
                throw new FailedRequestException("search failed: "
                        + status.getReasonPhrase(), extractErrorFields(response));
            }
            return response;
        }
    }

	@Override
	public void deleteSearch(RequestLogger reqlog, DeleteQueryDefinition queryDef,
			String transactionId) throws ForbiddenUserException,
			FailedRequestException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		if (queryDef.getDirectory() != null) {
			addEncodedParam(params, "directory", queryDef.getDirectory());
		}

		addEncodedParam(params, "collection", queryDef.getCollections());

		if (transactionId != null) {
			params.add("txid", transactionId);
		}

		WebResource webResource = connection.path("search").queryParams(params);

		WebResource.Builder builder = webResource.getRequestBuilder();
		builder = addTransactionCookie(builder, transactionId);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.delete(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to delete",
					extractErrorFields(response));
		}

		if (status != ClientResponse.Status.NO_CONTENT) {
			throw new FailedRequestException("delete failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}
		
		logRequest(
				reqlog,
				"deleted search results in %s transaction",
				transactionId);
	}

	@Override
	public <T> T values(Class<T> as, ValuesDefinition valDef, String mimetype,
		long start, long pageLength, String transactionId
	) throws ForbiddenUserException, FailedRequestException {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();

		String optionsName = valDef.getOptionsName();
		if (optionsName != null && optionsName.length() > 0) {
			addEncodedParam(docParams, "options", optionsName);
		}

		if (valDef.getAggregate() != null) {
			addEncodedParam(docParams, "aggregate", valDef.getAggregate());
		}

		if (valDef.getAggregatePath() != null) {
			addEncodedParam(docParams, "aggregatePath",
					valDef.getAggregatePath());
		}

		if (valDef.getView() != null) {
			docParams.add("view", valDef.getView());
		}

		if (valDef.getDirection() != null) {
			if (valDef.getDirection() == ValuesDefinition.Direction.ASCENDING) {
				docParams.add("direction", "ascending");
			} else {
				docParams.add("direction", "descending");
			}
		}

		if (valDef.getFrequency() != null) {
			if (valDef.getFrequency() == ValuesDefinition.Frequency.FRAGMENT) {
				docParams.add("frequency", "fragment");
			} else {
				docParams.add("frequency", "item");
			}
		}

		if (start > 0) {
			docParams.add("start", Long.toString(start));
			if (pageLength > 0) {
				docParams.add("pageLength", Long.toString(pageLength));
			}
		}

		HandleImplementation baseHandle = null;

		if (valDef.getQueryDefinition() != null) {
			ValueQueryDefinition queryDef = valDef.getQueryDefinition();

			if (optionsName == null) {
				optionsName = queryDef.getOptionsName();
				if (optionsName != null) {
					addEncodedParam(docParams, "options", optionsName);
				}
			} else if (queryDef.getOptionsName() != null) {
				if (optionsName != queryDef.getOptionsName()
						&& logger.isWarnEnabled())
					logger.warn("values definition options take precedence over query definition options");
			}

			if (queryDef.getCollections() != null) {
				if (logger.isWarnEnabled())
					logger.warn("collections scope ignored for values query");
			}
			if (queryDef.getDirectory() != null) {
				if (logger.isWarnEnabled())
					logger.warn("directory scope ignored for values query");
			}

			if (queryDef instanceof StringQueryDefinition) {
				String text = ((StringQueryDefinition) queryDef).getCriteria();
				if (text != null) {
					addEncodedParam(docParams, "q", text);
				}
			} else if (queryDef instanceof StructuredQueryDefinition) {
				String structure = ((StructuredQueryDefinition) queryDef)
						.serialize();
				if (structure != null) {
					addEncodedParam(docParams, "structuredQuery", structure);
				}
			} else if (queryDef instanceof RawQueryDefinition) {
                StructureWriteHandle handle = ((RawQueryDefinition) queryDef).getHandle();
                baseHandle = HandleAccessor.checkHandle(handle, "values");
            } else {
				if (logger.isWarnEnabled())
					logger.warn("unsupported query definition: "
							+ queryDef.getClass().getName());
			}

			ServerTransform transform = queryDef.getResponseTransform();
			if (transform != null) {
				transform.merge(docParams);
			}
		}

		if (transactionId != null) {
			docParams.add("txid", transactionId);
		}

		String uri = "values";
		if (valDef.getName() != null) {
			uri += "/" + valDef.getName();
		}

		WebResource.Builder builder = connection.path(uri)
				.queryParams(docParams).accept(mimetype);
		builder = addTransactionCookie(builder, transactionId);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

            response = baseHandle == null ?
                doGet(builder) :
                doPost(null, builder.type(baseHandle.getMimetype()), baseHandle.sendContent(), baseHandle.isResendable());

			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to search",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("search failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		T entity = response.hasEntity() ? response.getEntity(as) : null;
		if (entity == null || (as != InputStream.class && as != Reader.class))
			response.close();

		return entity;
		
	}

	@Override
	public <T> T valuesList(Class<T> as, ValuesListDefinition valDef,
			String mimetype, String transactionId)
			throws ForbiddenUserException, FailedRequestException {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();

		String optionsName = valDef.getOptionsName();
		if (optionsName != null && optionsName.length() > 0) {
			addEncodedParam(docParams, "options", optionsName);
		}

		if (transactionId != null) {
			docParams.add("txid", transactionId);
		}

		String uri = "values";

		WebResource.Builder builder = connection.path(uri)
				.queryParams(docParams).accept(mimetype);
		builder = addTransactionCookie(builder, transactionId);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to search",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("search failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		T entity = response.hasEntity() ? response.getEntity(as) : null;
		if (entity == null || (as != InputStream.class && as != Reader.class))
			response.close();

		return entity;
	}

	@Override
	public <T> T optionsList(Class<T> as, String mimetype, String transactionId)
			throws ForbiddenUserException, FailedRequestException {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();

		if (transactionId != null) {
			docParams.add("txid", transactionId);
		}

		String uri = "config/query";

		WebResource.Builder builder = connection.path(uri)
				.queryParams(docParams).accept(mimetype);
		builder = addTransactionCookie(builder, transactionId);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to search",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("search failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		T entity = response.hasEntity() ? response.getEntity(as) : null;
		if (entity == null || (as != InputStream.class && as != Reader.class))
			response.close();

		return entity;
	}

	// namespaces, search options etc.
	@Override
	public <T> T getValue(RequestLogger reqlog, String type, String key,
			boolean isNullable, String mimetype, Class<T> as)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Getting {}/{}", type, key);

		WebResource.Builder builder = connection.path(type + "/" + key).accept(
				mimetype);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status != ClientResponse.Status.OK) {
			if (status == ClientResponse.Status.NOT_FOUND) {
				response.close();
				if (!isNullable)
					throw new ResourceNotFoundException("Could not get " + type
							+ "/" + key);
				return null;
			} else if (status == ClientResponse.Status.FORBIDDEN)
				throw new ForbiddenUserException("User is not allowed to read "
						+ type, extractErrorFields(response));
			else
				throw new FailedRequestException(type + " read failed: "
						+ status.getReasonPhrase(),
						extractErrorFields(response));
		}

		logRequest(reqlog, "read %s value with %s key and %s mime type", type,
				key, (mimetype != null) ? mimetype : null);

		T entity = response.hasEntity() ? response.getEntity(as) : null;
		if (entity == null || (as != InputStream.class && as != Reader.class))
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}

	@Override
	public <T> T getValues(RequestLogger reqlog, String type, String mimetype, Class<T> as)
	throws ForbiddenUserException, FailedRequestException {
		return getValues(reqlog, type, null, mimetype, as);
	}
	@Override
	public <T> T getValues(RequestLogger reqlog, String type, RequestParameters extraParams,
			String mimetype, Class<T> as)
	throws ForbiddenUserException, FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Getting {}", type);

		MultivaluedMap<String, String> requestParams = convertParams(extraParams);

		WebResource.Builder builder = (requestParams == null) ?
				connection.path(type).accept(mimetype) :
				connection.path(type).queryParams(requestParams).accept(mimetype);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to read "
					+ type, extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException(type + " read failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		logRequest(reqlog, "read %s values with %s mime type", type,
				(mimetype != null) ? mimetype : null);

		T entity = response.hasEntity() ? response.getEntity(as) : null;
		if (entity == null || (as != InputStream.class && as != Reader.class))
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}

	@Override
	public void postValue(RequestLogger reqlog, String type, String key,
			String mimetype, Object value)
			throws ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Posting {}/{}", type, key);

		putPostValueImpl(reqlog, "post", type, key, null, mimetype, value,
				ClientResponse.Status.CREATED);
	}
	@Override
	public void postValue(RequestLogger reqlog, String type, String key,
			RequestParameters extraParams
	) throws ResourceNotResendableException, ForbiddenUserException, FailedRequestException
	{
		if (logger.isDebugEnabled())
			logger.debug("Posting {}/{}", type, key);

		putPostValueImpl(reqlog, "post", type, key, extraParams, null, null,
				ClientResponse.Status.NO_CONTENT);
	}


	@Override
	public void putValue(RequestLogger reqlog, String type, String key,
			String mimetype, Object value) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Putting {}/{}", type, key);

		putPostValueImpl(reqlog, "put", type, key, null, mimetype, value,
				ClientResponse.Status.NO_CONTENT, ClientResponse.Status.CREATED);
	}

	@Override
	public void putValue(RequestLogger reqlog, String type, String key,
			RequestParameters extraParams, String mimetype, Object value)
			throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Putting {}/{}", type, key);

		putPostValueImpl(reqlog, "put", type, key, extraParams, mimetype,
				value, ClientResponse.Status.NO_CONTENT);
	}

	private void putPostValueImpl(RequestLogger reqlog, String method,
			String type, String key, RequestParameters extraParams,
			String mimetype, Object value,
			ClientResponse.Status... expectedStatuses) {
		if (key != null) {
			logRequest(reqlog, "writing %s value with %s key and %s mime type",
					type, key, (mimetype != null) ? mimetype : null);
		} else {
			logRequest(reqlog, "writing %s values with %s mime type", type,
					(mimetype != null) ? mimetype : null);
		}

		HandleImplementation handle = (value instanceof HandleImplementation) ?
				(HandleImplementation) value : null;

		MultivaluedMap<String, String> requestParams = convertParams(extraParams);

		String connectPath = null;
		WebResource.Builder builder = null;

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			Object nextValue = (handle != null) ? handle.sendContent() : value;

			Object sentValue = null;
			if (nextValue instanceof OutputStreamSender) {
				sentValue = new StreamingOutputImpl(
						(OutputStreamSender) nextValue, reqlog);
			} else {
				if (reqlog != null && retry == 0)
					sentValue = reqlog.copyContent(nextValue);
				else
					sentValue = nextValue;
			}

			boolean isStreaming = (isFirstRequest() || handle == null) ? isStreaming(sentValue)
					: false;

			boolean isResendable = (handle == null) ? !isStreaming :
				handle.isResendable();

			if (isFirstRequest() && !isResendable && isStreaming) {
				nextDelay = makeFirstRequest(retry);
				if (nextDelay != 0)
					continue;
			}

			if ("put".equals(method)) {
				if (builder == null) {
					connectPath = (key != null) ? type + "/" + key : type;
					WebResource resource = (requestParams == null) ?
						connection.path(connectPath) :
						connection.path(connectPath).queryParams(requestParams);
					builder = (mimetype == null) ?
						resource.getRequestBuilder() : resource.type(mimetype);
				}

				response = (sentValue == null) ?
						builder.put(ClientResponse.class) :
						builder.put(ClientResponse.class, sentValue);
			} else if ("post".equals(method)) {
				if (builder == null) {
					connectPath = type;
					WebResource resource = (requestParams == null) ?
						connection.path(connectPath) :
						connection.path(connectPath).queryParams(requestParams);
					builder = (mimetype == null) ?
						resource.getRequestBuilder() : resource.type(mimetype);
				}

				response = (sentValue == null) ?
					builder.post(ClientResponse.class) :
					builder.post(ClientResponse.class, sentValue);
			} else {
				throw new MarkLogicInternalException("unknown method type "
						+ method);
			}

			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			response.close();

			if (!isResendable) {
				checkFirstRequest();
				throw new ResourceNotResendableException(
						"Cannot retry request for " + connectPath);
			}

			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to write "
					+ type, extractErrorFields(response));
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException(type + " not found for write",
					extractErrorFields(response));
		boolean statusOk = false;
		for (ClientResponse.Status expectedStatus : expectedStatuses) {
			statusOk = statusOk || (status == expectedStatus);
			if (statusOk) {
				break;
			}
		}

		if (!statusOk) {
			throw new FailedRequestException(type + " write failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}
		response.close();

	}

	@Override
	public void deleteValue(RequestLogger reqlog, String type, String key)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Deleting {}/{}", type, key);

		WebResource builder = connection.path(type + "/" + key);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.delete(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to delete "
					+ type, extractErrorFields(response));
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException(type + " not found for delete",
					extractErrorFields(response));
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("delete failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));

		response.close();

		logRequest(reqlog, "deleted %s value with %s key", type, key);
	}

	@Override
	public void deleteValues(RequestLogger reqlog, String type)
			throws ForbiddenUserException, FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Deleting {}", type);

		WebResource builder = connection.path(type);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.delete(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to delete "
					+ type, extractErrorFields(response));
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("delete failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		response.close();

		logRequest(reqlog, "deleted %s values", type);
	}

	@Override
	public <R extends AbstractReadHandle> R getResource(RequestLogger reqlog,
			String path, RequestParameters params, R output)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String mimetype = outputBase.getMimetype();
		Class as = outputBase.receiveAs();

		WebResource.Builder builder = makeGetBuilder(path, params, mimetype);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = doGet(builder);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		checkStatus(response, status, "read", "resource", path,
				ResponseStatus.OK_OR_NO_CONTENT);

		if (as != null) {
			outputBase.receiveContent(makeResult(reqlog, "read", "resource",
					response, as));
		} else {
			response.close();
		}

		return output;
	}

	@Override
	public ServiceResultIterator getIteratedResource(RequestLogger reqlog,
			String path, RequestParameters params, String... mimetypes)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		return getIteratedResourceImpl(JerseyServiceResultIterator.class, reqlog, path, params, mimetypes);
	}

	private <U extends JerseyResultIterator> U getIteratedResourceImpl(Class<U> clazz, RequestLogger reqlog,
			String path, RequestParameters params, String... mimetypes)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {

		WebResource.Builder builder = makeGetBuilder(path, params, null);

		MediaType multipartType = Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = doGet(builder.accept(multipartType));
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}

		checkStatus(response, status, "read", "resource", path,
				ResponseStatus.OK_OR_NO_CONTENT);

		return makeResults(clazz, reqlog, "read", "resource", response);
	}

	@Override
	public <R extends AbstractReadHandle> R putResource(RequestLogger reqlog,
			String path, RequestParameters params, AbstractWriteHandle input,
			R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation inputBase = HandleAccessor.checkHandle(input,
				"write");
		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String inputMimetype = inputBase.getMimetype();
		boolean isResendable = inputBase.isResendable();
		String outputMimeType = null;
		Class as = null;
		if (outputBase != null) {
			outputMimeType = outputBase.getMimetype();
		
			as = outputBase.receiveAs();
		}
		WebResource.Builder builder = makePutBuilder(path, params,
				inputMimetype, outputMimeType);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = doPut(reqlog, builder, inputBase.sendContent(),
					!isResendable);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			response.close();

			if (!isResendable) {
				checkFirstRequest();
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			}

			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}

		checkStatus(response, status, "write", "resource", path,
				ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

		if (as != null) {
			outputBase.receiveContent(makeResult(reqlog, "write", "resource",
					response, as));
		} else {
			response.close();
		}

		return output;
	}

	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R putResource(
			RequestLogger reqlog, String path, RequestParameters params,
			W[] input, R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		if (input == null || input.length == 0)
			throw new IllegalArgumentException(
					"input not specified for multipart");

		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String outputMimetype = outputBase.getMimetype();
		Class as = outputBase.receiveAs();

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			MultiPart multiPart = new MultiPart();
			boolean hasStreamingPart = addParts(multiPart, reqlog, input);

			WebResource.Builder builder = makePutBuilder(path, params,
					multiPart, outputMimetype);

			response = doPut(builder, multiPart, hasStreamingPart);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			response.close();

			if (hasStreamingPart) {
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			}

			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}

		checkStatus(response, status, "write", "resource", path,
				ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

		if (as != null) {
			outputBase.receiveContent(makeResult(reqlog, "write", "resource",
					response, as));
		} else {
			response.close();
		}

		return output;
	}

	@Override
	public <R extends AbstractReadHandle> R postResource(RequestLogger reqlog,
			String path, RequestParameters params, AbstractWriteHandle input,
			R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation inputBase = HandleAccessor.checkHandle(input,
				"write");
		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String inputMimetype = inputBase.getMimetype();
		String outputMimetype = outputBase.getMimetype();
		boolean isResendable = inputBase.isResendable();
		Class as = outputBase.receiveAs();

		WebResource.Builder builder = makePostBuilder(path, params,
				inputMimetype, outputMimetype);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = doPost(reqlog, builder, inputBase.sendContent(),
					!isResendable);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			response.close();

			if (!isResendable) {
				checkFirstRequest();
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			}

			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}

		checkStatus(response, status, "apply", "resource", path,
				ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

		if (as != null) {
			outputBase.receiveContent(makeResult(reqlog, "apply", "resource",
					response, as));
		} else {
			response.close();
		}

		return output;
	}

	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
			RequestLogger reqlog, String path, RequestParameters params,
			W[] input, R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		return postResource(reqlog, path, params, input, null, output);
	}

	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
			RequestLogger reqlog, String path, RequestParameters params,
			W[] input, Map<String, List<String>>[] headers, R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation outputBase = HandleAccessor.checkHandle(output, "read");

		String outputMimetype = outputBase != null ? outputBase.getMimetype() : null;
		Class as = outputBase != null ? outputBase.receiveAs() : null;

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			MultiPart multiPart = new MultiPart();
			boolean hasStreamingPart = addParts(multiPart, reqlog, null, input, headers);

			WebResource.Builder builder = makePostBuilder(path, params,
					multiPart, outputMimetype);

			response = doPost(builder, multiPart, hasStreamingPart);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			response.close();

			if (hasStreamingPart) {
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			}

			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}

		checkStatus(response, status, "apply", "resource", path,
				ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

		if (as != null) {
			outputBase.receiveContent(makeResult(reqlog, "apply", "resource",
					response, as));
		} else {
			response.close();
		}

		return output;
	}

	@Override
	public void postBulkDocuments(
			RequestLogger reqlog, DocumentWriteSet writeSet,
			ServerTransform transform, Format defaultFormat, String transactionId)
		throws ForbiddenUserException,  FailedRequestException
	{
		postBulkDocuments(reqlog, writeSet, transform, transactionId, defaultFormat, null);
	}

	@Override
	public <R extends AbstractReadHandle> R postBulkDocuments(
			RequestLogger reqlog, DocumentWriteSet writeSet,
			ServerTransform transform, String transactionId, Format defaultFormat, R output)
		throws ForbiddenUserException,  FailedRequestException
	{
		ArrayList<AbstractWriteHandle> writeHandles = new ArrayList<AbstractWriteHandle>();
		ArrayList<Map<String, List<String>>> headerList = new ArrayList<Map<String, List<String>>>();
		for ( DocumentWriteOperation write: writeSet ) {
			HandleImplementation metadata =
				HandleAccessor.checkHandle(write.getMetadata(), "write");
			HandleImplementation content =
				HandleAccessor.checkHandle(write.getContent(), "write");
			if ( write.getOperationType() == 
					DocumentWriteOperation.OperationType.DISABLE_METADATA_DEFAULT )
			{
				MultivaluedMap headers = new MultivaluedMapImpl();
				headers.add(HttpHeaders.CONTENT_TYPE, metadata.getMimetype());
				headers.add("Content-Disposition", "inline; category=metadata");
				headerList.add(headers);
				writeHandles.add(write.getMetadata());
			} else if ( metadata != null ) {
				MultivaluedMap headers = new MultivaluedMapImpl();
				headers.add(HttpHeaders.CONTENT_TYPE, metadata.getMimetype());
				if ( write.getOperationType() == DocumentWriteOperation.OperationType.METADATA_DEFAULT ) {
					headers.add("Content-Disposition", "inline; category=metadata");
				} else {
					headers.add("Content-Disposition",
						ContentDisposition
							.type("attachment")
							.fileName(write.getUri())
							.build().toString() +
						"; category=metadata"
					);
				}
				headerList.add(headers);
				writeHandles.add(write.getMetadata());
			}
			if ( content != null ) {
				MultivaluedMap headers = new MultivaluedMapImpl();
				String mimeType = content.getMimetype();
				if ( mimeType == null && defaultFormat != null ) {
					mimeType = defaultFormat.getDefaultMimetype();
				}
				headers.add(HttpHeaders.CONTENT_TYPE, mimeType);
				headers.add("Content-Disposition",
					ContentDisposition
						.type("attachment")
						.fileName(write.getUri())
						.build().toString()
				);
				headerList.add(headers);
				writeHandles.add(write.getContent());
			}
		}
		RequestParameters params = new RequestParameters();
		if (transform != null) {
			transform.merge(params);
		}
		if ( transactionId != null ) params.add("txid", transactionId);
		return postResource(
			reqlog,
			"documents",
			params, 
			(AbstractWriteHandle[]) writeHandles.toArray(new AbstractWriteHandle[0]),
			(Map<String, List<String>>[]) headerList.toArray(new HashMap[0]),
			output);
	}

	@Override
	public ServiceResultIterator postIteratedResource(RequestLogger reqlog,
			String path, RequestParameters params, AbstractWriteHandle input,
			String... outputMimetypes) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		return postIteratedResourceImpl(JerseyServiceResultIterator.class,
			reqlog, path, params, input, outputMimetypes);
	}

	private <U extends JerseyResultIterator> U postIteratedResourceImpl(
			Class<U> clazz, RequestLogger reqlog,
			String path, RequestParameters params, AbstractWriteHandle input,
			String... outputMimetypes) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation inputBase = HandleAccessor.checkHandle(input,
				"write");

		String inputMimetype = inputBase.getMimetype();
		boolean isResendable = inputBase.isResendable();

		WebResource.Builder builder = makePostBuilder(path, params, inputMimetype, null);

		MediaType multipartType = Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			Object value = inputBase.sendContent();

			response = doPost(reqlog, builder.accept(multipartType), value, !isResendable);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			response.close();

			if (!isResendable) {
				checkFirstRequest();
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			}

			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}

		checkStatus(response, status, "apply", "resource", path,
				ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

		return makeResults(clazz, reqlog, "apply", "resource", response);
	}

	@Override
	public <W extends AbstractWriteHandle> ServiceResultIterator postIteratedResource(
			RequestLogger reqlog, String path, RequestParameters params,
			W[] input, String... outputMimetypes)
			throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		return postIteratedResourceImpl(JerseyServiceResultIterator.class,
			reqlog, path, params, input, outputMimetypes);
	}

	private <W extends AbstractWriteHandle, U extends JerseyResultIterator> U postIteratedResourceImpl(
			Class<U> clazz, RequestLogger reqlog, String path, RequestParameters params,
			W[] input, String... outputMimetypes)
			throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			MultiPart multiPart = new MultiPart();
			boolean hasStreamingPart = addParts(multiPart, reqlog, input);

			WebResource.Builder builder = makePostBuilder(
					path,
					params,
					multiPart,
					Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

			response = doPost(builder, multiPart, hasStreamingPart);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			response.close();

			if (hasStreamingPart) {
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			}

			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;
			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}

		checkStatus(response, status, "apply", "resource", path,
				ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

		return makeResults(clazz, reqlog, "apply", "resource", response);
	}

	@Override
	public <R extends AbstractReadHandle> R deleteResource(
			RequestLogger reqlog, String path, RequestParameters params,
			R output) throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String outputMimeType = null;
		Class as = null;
		if (outputBase != null) {
			outputMimeType = outputBase.getMimetype();
			as = outputBase.receiveAs();
		}
		WebResource.Builder builder = makeDeleteBuilder(reqlog, path, params,
				outputMimeType);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = doDelete(builder);
			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}

		checkStatus(response, status, "delete", "resource", path,
				ResponseStatus.OK_OR_NO_CONTENT);

		if (as != null) {
			outputBase.receiveContent(makeResult(reqlog, "delete", "resource",
					response, as));
		} else {
			response.close();
		}

		return output;
	}

	private WebResource.Builder makeGetBuilder(String path,
			RequestParameters params, Object mimetype) {
		if (path == null)
			throw new IllegalArgumentException("Read with null path");

		WebResource.Builder builder = makeBuilder(path, convertParams(params),
				null, mimetype);

		if (logger.isDebugEnabled())
			logger.debug(String.format("Getting %s as %s", path, mimetype));

		return builder;
	}

	private ClientResponse doGet(WebResource.Builder builder) {
		ClientResponse response = builder.get(ClientResponse.class);

		if (isFirstRequest())
			setFirstRequest(false);

		return response;
	}

	private WebResource.Builder makePutBuilder(String path,
			RequestParameters params, String inputMimetype,
			String outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Write with null path");

		WebResource.Builder builder = makeBuilder(path, convertParams(params),
				inputMimetype, outputMimetype);

		if (logger.isDebugEnabled())
			logger.debug("Putting {}", path);

		return builder;
	}

	private ClientResponse doPut(RequestLogger reqlog,
			WebResource.Builder builder, Object value, boolean isStreaming) {
		if (value == null)
			throw new IllegalArgumentException("Resource write with null value");

		if (isFirstRequest() && isStreaming(value))
			makeFirstRequest(0);

		ClientResponse response = null;
		if (value instanceof OutputStreamSender) {
			response = builder
					.put(ClientResponse.class, new StreamingOutputImpl(
							(OutputStreamSender) value, reqlog));
		} else {
			if (reqlog != null)
				response = builder.put(ClientResponse.class,
						reqlog.copyContent(value));
			else
				response = builder.put(ClientResponse.class, value);
		}

		if (isFirstRequest())
			setFirstRequest(false);

		return response;
	}

	private WebResource.Builder makePutBuilder(String path,
			RequestParameters params, MultiPart multiPart, String outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Write with null path");

		WebResource.Builder builder = makeBuilder(path, convertParams(params),
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE),
				outputMimetype);

		if (logger.isDebugEnabled())
			logger.debug("Putting multipart for {}", path);

		return builder;
	}

	private ClientResponse doPut(WebResource.Builder builder,
			MultiPart multiPart, boolean hasStreamingPart) {
		if (isFirstRequest() && hasStreamingPart)
			makeFirstRequest(0);

		ClientResponse response = builder.put(ClientResponse.class, multiPart);

		if (isFirstRequest())
			setFirstRequest(false);

		return response;
	}

	private WebResource.Builder makePostBuilder(String path,
			RequestParameters params, Object inputMimetype,
			Object outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Apply with null path");

		WebResource.Builder builder = makeBuilder(path, convertParams(params),
				inputMimetype, outputMimetype);

		if (logger.isDebugEnabled())
			logger.debug("Posting {}", path);

		return builder;
	}

	private ClientResponse doPost(RequestLogger reqlog,
			WebResource.Builder builder, Object value, boolean isStreaming) {
		if (isFirstRequest() && isStreaming(value))
			makeFirstRequest(0);

		ClientResponse response = null;
		if (value instanceof OutputStreamSender) {
			response = builder
					.post(ClientResponse.class, new StreamingOutputImpl(
							(OutputStreamSender) value, reqlog));
		} else {
			if (reqlog != null)
				response = builder.post(ClientResponse.class,
						reqlog.copyContent(value));
			else
				response = builder.post(ClientResponse.class, value);
		}

		if (isFirstRequest())
			setFirstRequest(false);

		return response;
	}

	private WebResource.Builder makePostBuilder(String path,
			RequestParameters params, MultiPart multiPart, Object outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Apply with null path");

		WebResource.Builder builder = makeBuilder(path, convertParams(params),
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE),
				outputMimetype);

		if (logger.isDebugEnabled())
			logger.debug("Posting multipart for {}", path);

		return builder;
	}

	private ClientResponse doPost(WebResource.Builder builder,
			MultiPart multiPart, boolean hasStreamingPart) {
		if (isFirstRequest() && hasStreamingPart)
			makeFirstRequest(0);

		ClientResponse response = builder.post(ClientResponse.class, multiPart);

		if (isFirstRequest())
			setFirstRequest(false);

		return response;
	}

	private WebResource.Builder makeDeleteBuilder(RequestLogger reqlog,
			String path, RequestParameters params, String mimetype) {
		if (path == null)
			throw new IllegalArgumentException("Delete with null path");

		WebResource.Builder builder = makeBuilder(path, convertParams(params),
				null, mimetype);

		if (logger.isDebugEnabled())
			logger.debug("Deleting {}", path);

		return builder;
	}

	private ClientResponse doDelete(WebResource.Builder builder) {
		ClientResponse response = builder.delete(ClientResponse.class);

		if (isFirstRequest())
			setFirstRequest(false);

		return response;
	}

	private MultivaluedMap<String, String> convertParams(
			RequestParameters params) {
		if (params == null || params.size() == 0)
			return null;

		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		for (Map.Entry<String, List<String>> entry : params.entrySet()) {
			addEncodedParam(requestParams, entry.getKey(), entry.getValue());
		}

		return requestParams;
	}

	private void addEncodedParam(MultivaluedMap<String, String> params,
			String key, List<String> values) {
		List<String> encodedParams = encodeParamValues(values);
		if (encodedParams != null && encodedParams.size() > 0)
			params.put(key, encodedParams);
	}

	private void addEncodedParam(MultivaluedMap<String, String> params,
			String key, String[] values) {
		List<String> encodedParams = encodeParamValues(values);
		if (encodedParams != null && encodedParams.size() > 0)
			params.put(key, encodedParams);
	}

	private void addEncodedParam(MultivaluedMap<String, String> params,
			String key, String value) {
		value = encodeParamValue(value);
		if (value == null)
			return;

		params.add(key, value);
	}

	private List<String> encodeParamValues(List<String> oldValues) {
		if (oldValues == null)
			return null;

		int oldSize = oldValues.size();
		if (oldSize == 0)
			return null;

		List<String> newValues = new ArrayList<String>(oldSize);
		for (String value : oldValues) {
			String newValue = encodeParamValue(value);
			if (newValue == null)
				continue;
			newValues.add(newValue);
		}

		return newValues;
	}

	private List<String> encodeParamValues(String[] oldValues) {
		if (oldValues == null)
			return null;

		int oldSize = oldValues.length;
		if (oldSize == 0)
			return null;

		List<String> newValues = new ArrayList<String>(oldSize);
		for (String value : oldValues) {
			String newValue = encodeParamValue(value);
			if (newValue == null)
				continue;
			newValues.add(newValue);
		}

		return newValues;
	}

	private String encodeParamValue(String value) {
		if (value == null)
			return null;

		return UriComponent.encode(value, UriComponent.Type.QUERY_PARAM)
				.replace("+", "%20");
	}

	private <W extends AbstractWriteHandle> boolean addParts(
			MultiPart multiPart, RequestLogger reqlog, W[] input) {
		return addParts(multiPart, reqlog, null, input, null);
	}

	private <W extends AbstractWriteHandle> boolean addParts(
			MultiPart multiPart, RequestLogger reqlog, String[] mimetypes,
			W[] input) {
		return addParts(multiPart, reqlog, null, input, null);
	}

	private <W extends AbstractWriteHandle> boolean addParts(
			MultiPart multiPart, RequestLogger reqlog, String[] mimetypes,
			W[] input, Map<String, List<String>>[] headers) {
		if (mimetypes != null && mimetypes.length != input.length)
			throw new IllegalArgumentException(
					"Mismatch between count of mimetypes and input");
		if (headers != null && headers.length != input.length)
			throw new IllegalArgumentException(
					"Mismatch between count of headers and input");

		multiPart.setMediaType(new MediaType("multipart", "mixed"));

		boolean hasStreamingPart = false;
		for (int i = 0; i < input.length; i++) {
			AbstractWriteHandle handle = input[i];
			HandleImplementation handleBase = HandleAccessor.checkHandle(
					handle, "write");

			if (!hasStreamingPart)
				hasStreamingPart = !handleBase.isResendable();

			Object value = handleBase.sendContent();

			String inputMimetype = null;
			if ( mimetypes != null ) inputMimetype = mimetypes[i];
			if ( inputMimetype == null && headers != null ) {
				inputMimetype = getHeaderMimetype(headers[i]);
			}
			if ( inputMimetype == null ) inputMimetype = handleBase.getMimetype();

			String[] typeParts = (inputMimetype != null && inputMimetype
					.contains("/")) ? inputMimetype.split("/", 2) : null;

			MediaType typePart = (typeParts != null) ? new MediaType(
					typeParts[0], typeParts[1]) : MediaType.WILDCARD_TYPE;

			BodyPart bodyPart = null;
			if (value instanceof OutputStreamSender) {
				bodyPart = new BodyPart(new StreamingOutputImpl(
						(OutputStreamSender) value, reqlog), typePart);
			} else {
				if (reqlog != null)
					bodyPart = new BodyPart(reqlog.copyContent(value), typePart);
				else
					bodyPart = new BodyPart(value, typePart);
			}
			if ( headers != null ) {
                MultivaluedMap mutableHeaders = bodyPart.getHeaders();
                mutableHeaders.putAll(headers[i]);
            }

			multiPart = multiPart.bodyPart(bodyPart);
		}

		return hasStreamingPart;
	}

	private WebResource.Builder makeBuilder(String path,
			MultivaluedMap<String, String> params, Object inputMimetype,
			Object outputMimetype) {
		WebResource resource = (params == null) ? connection.path(path)
				: connection.path(path).queryParams(params);

		WebResource.Builder builder = resource.getRequestBuilder();

		if (inputMimetype == null) {
		} else if (inputMimetype instanceof String) {
			builder = builder.type((String) inputMimetype);
		} else if (inputMimetype instanceof MediaType) {
			builder = builder.type((MediaType) inputMimetype);
		} else {
			throw new IllegalArgumentException(
					"Unknown input mimetype specifier "
							+ inputMimetype.getClass().getName());
		}

		if (outputMimetype == null) {
		} else if (outputMimetype instanceof String) {
			builder = builder.accept((String) outputMimetype);
		} else if (outputMimetype instanceof MediaType) {
			builder = builder.accept((MediaType) outputMimetype);
		} else {
			throw new IllegalArgumentException(
					"Unknown output mimetype specifier "
							+ outputMimetype.getClass().getName());
		}

		return builder;
	}

	private void checkStatus(ClientResponse response,
			ClientResponse.Status status, String operation, String entityType,
			String path, ResponseStatus expected) {
		if (!expected.isExpected(status)) {
			if (status == ClientResponse.Status.NOT_FOUND) {
				throw new ResourceNotFoundException("Could not " + operation
						+ " " + entityType + " at " + path,
						extractErrorFields(response));
			}
			if (status == ClientResponse.Status.FORBIDDEN) {
				throw new ForbiddenUserException("User is not allowed to "
						+ operation + " " + entityType + " at " + path,
						extractErrorFields(response));
			}
			throw new FailedRequestException("failed to " + operation + " "
					+ entityType + " at " + path + ": "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}
	}

	private <T> T makeResult(RequestLogger reqlog, String operation,
			String entityType, ClientResponse response, Class<T> as) {
		if (as == null) {
			return null;
		}

		logRequest(reqlog, "%s for %s", operation, entityType);

		T entity = response.hasEntity() ? response.getEntity(as) : null;
		if (entity == null || (as != InputStream.class && as != Reader.class))
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}

	private <U extends JerseyResultIterator> U makeResults(
			Class<U> clazz, RequestLogger reqlog,
			String operation, String entityType, ClientResponse response) {
        if ( response == null ) return null;
		MultiPart entity = response.hasEntity() ?
				response.getEntity(MultiPart.class) : null;
        response.close();
		if (entity == null) return null;
        return makeResults(clazz, reqlog, operation, entityType, entity.getBodyParts(), response);
	}

	private <U extends JerseyResultIterator> U makeResults(
			Class<U> clazz, RequestLogger reqlog,
			String operation, String entityType, List<BodyPart> partList, ClientResponse response) {
		logRequest(reqlog, "%s for %s", operation, entityType);

        if ( response == null ) return null;
		if (partList == null || partList.size() == 0) return null;

		try {
			java.lang.reflect.Constructor<U> constructor = 
				clazz.getConstructor(JerseyServices.class, RequestLogger.class, List.class);
            JerseyResultIterator result = constructor.newInstance(this, reqlog, partList);
			MultivaluedMap<String, String> headers = response.getHeaders();
            if (headers.containsKey("vnd.marklogic.start")) {
                result.setStart(Long.parseLong(headers.get("vnd.marklogic.start").get(0)));
            }
            if (headers.containsKey("vnd.marklogic.pageLength")) {
                result.setPageSize(Long.parseLong(headers.get("vnd.marklogic.pageLength").get(0)));
            }
            if (headers.containsKey("vnd.marklogic.result-estimate")) {
                result.setTotalSize(Long.parseLong(headers.get("vnd.marklogic.result-estimate").get(0)));
            }
            return (U) result;
		} catch (Throwable t) {
			throw new MarkLogicInternalException("Error instantiating " + clazz.getName(), t);
		}
	}

	private boolean isStreaming(Object value) {
		return !(value instanceof String || value instanceof byte[] || value instanceof File);
	}

	private void logRequest(RequestLogger reqlog, String message,
			Object... params) {
		if (reqlog == null)
			return;

		PrintStream out = reqlog.getPrintStream();
		if (out == null)
			return;

		if (params == null || params.length == 0) {
			out.println(message);
		} else {
			out.format(message, params);
			out.println();
		}
	}

	private String stringJoin(Collection collection, String separator,
			String defaultValue) {
		if (collection == null || collection.size() == 0)
			return defaultValue;

		StringBuilder builder = null;
		for (Object value : collection) {
			if (builder == null)
				builder = new StringBuilder();
			else
				builder.append(separator);

			builder.append(value);
		}

		return (builder != null) ? builder.toString() : null;
	}

	private int calculateDelay(Random rand, int i) {
		int min   =
			(i  > 6) ? DELAY_CEILING :
			(i == 0) ? DELAY_FLOOR   :
			           DELAY_FLOOR + (1 << i) * DELAY_MULTIPLIER;
		int range =
			(i >  6) ? DELAY_FLOOR          :
			(i == 0) ? 2 * DELAY_MULTIPLIER :
			(i == 6) ? DELAY_CEILING - min  :
				       (1 << i) * DELAY_MULTIPLIER;
		return min + randRetry.nextInt(range);
	}

	public class JerseyResult {
		private RequestLogger reqlog;
		private BodyPart part;
		private boolean extractedHeaders = false;
		private String uri;
		private Format format;
		private String mimetype;
		private long length;

		public JerseyResult(RequestLogger reqlog, BodyPart part) {
			this.reqlog = reqlog;
			this.part = part;
		}

		public <R extends AbstractReadHandle> R getContent(R handle) {
			if (part == null)
				throw new IllegalStateException("Content already retrieved");

			HandleImplementation handleBase = HandleAccessor.as(handle);

			extractHeaders();
			updateFormat(handleBase, format);
			updateMimetype(handleBase, mimetype);
			updateLength(handleBase, length);

			Object contentEntity = part.getEntityAs(handleBase.receiveAs());
			handleBase.receiveContent((reqlog != null) ? reqlog
					.copyContent(contentEntity) : contentEntity);

			part = null;
			reqlog = null;

			return handle;
		}

		public String getUri() {
			extractHeaders();
			return uri;
		}
		public Format getFormat() {
			extractHeaders();
			return format;
		}

		public String getMimetype() {
			extractHeaders();
			return mimetype;
		}

		public long getLength() {
			extractHeaders();
			return length;
		}

		private void extractHeaders() {
			if (part == null || extractedHeaders)
				return;
			MultivaluedMap<String, String> headers = part.getHeaders();
			format = getHeaderFormat(part);
			mimetype = getHeaderMimetype(headers);
			length = getHeaderLength(headers);
			uri = getHeaderUri(part.getContentDisposition());
			extractedHeaders = true;
		}
	}

	public class JerseyServiceResult extends JerseyResult implements ServiceResult {
		public JerseyServiceResult(RequestLogger reqlog, BodyPart part) {
			super(reqlog, part);
		}
	}

	public class JerseyResultIterator<T extends JerseyResult> {
		private RequestLogger reqlog;
		private Iterator<BodyPart> partQueue;
        private Class<T> clazz;
        private long start = -1;
        private long size = -1;
        private long pageSize = -1;
        private long totalSize = -1;

		public JerseyResultIterator(RequestLogger reqlog,
				List<BodyPart> partList, Class<T> clazz) {
			super();
            this.clazz = clazz;
            if (partList != null && partList.size() > 0) {
                this.size = partList.size();
                this.reqlog = reqlog;
                this.partQueue = new ConcurrentLinkedQueue<BodyPart>(
                        partList).iterator();
			}
		}

        public long getStart() {
            return start;
        }

        public JerseyResultIterator<T> setStart(long start) {
            this.start = start;
            return this;
        }

        public long getSize() {
            return size;
        }

        public JerseyResultIterator<T> setSize(long size) {
            this.size = new Long(size);
            return this;
        }

        public long getPageSize() {
            return pageSize;
        }

        public JerseyResultIterator<T> setPageSize(long pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public JerseyResultIterator<T> setTotalSize(long totalSize) {
            this.totalSize = totalSize;
            return this;
        }


		public boolean hasNext() {
			if (partQueue == null)
				return false;
			boolean hasNext = partQueue.hasNext();
			if (!partQueue.hasNext()) close();
			return hasNext;
		}

		public T next() {
			if (partQueue == null)
				return null;

			try {
				java.lang.reflect.Constructor<T> constructor = 
					clazz.getConstructor(JerseyServices.class, RequestLogger.class, BodyPart.class);
				return constructor.newInstance(new JerseyServices(), reqlog, partQueue.next());
			} catch (Throwable t) {
				throw new IllegalStateException("Error instantiating " + clazz.getName());
			} finally {
				if (!partQueue.hasNext())
					close();
			}
		}

		public void remove() {
			if (partQueue == null)
				return;
			partQueue.remove();
			if (!partQueue.hasNext()) close();
		}

		public void close() {
			partQueue = null;
			reqlog = null;
		}

		protected void finalize() throws Throwable {
			close();
			super.finalize();
		}
	}

	public class JerseyServiceResultIterator 
		extends JerseyResultIterator<JerseyServiceResult>
		implements ServiceResultIterator
	{
		public JerseyServiceResultIterator(RequestLogger reqlog,
				List<BodyPart> partList) {
            super(reqlog, partList, JerseyServiceResult.class);
		}
	}

	public class DefaultJerseyResultIterator 
		extends JerseyResultIterator<JerseyResult>
	{
		public DefaultJerseyResultIterator(RequestLogger reqlog,
				List<BodyPart> partList) {
            super(reqlog, partList, JerseyResult.class);
		}
	}

	public class JerseyDocumentRecord implements DocumentRecord {
		private JerseyResult content;
		private JerseyResult metadata;

		public JerseyDocumentRecord(JerseyResult content, JerseyResult metadata) {
			this.content = content;
			this.metadata = metadata;
		}

		public JerseyDocumentRecord(JerseyResult content) {
			this.content = content;
		}

		public String getUri() {
			if ( content == null && metadata != null ) {
				return metadata.getUri();
			} else if ( content != null ) {
				return content.getUri();
			} else {
				throw new IllegalStateException("Missing both content and metadata!");
			}
		}

		public Format getFormat() {
			return content.getFormat();
		}

		public String getMimetype() {
			return content.getMimetype();
		}

		public <T extends DocumentMetadataReadHandle> T getMetadata(T metadataHandle) {
			if ( metadata == null ) throw new IllegalStateException(
				"getMetadata called when no metadata is available");
			return metadata.getContent(metadataHandle);
		}

		public <T extends AbstractReadHandle> T getContent(T contentHandle) {
			if ( content == null ) throw new IllegalStateException(
				"getContent called when no content is available");
			return content.getContent(contentHandle);
		}
	}

	@Override
	public HttpClient getClientImplementation() {
		if (client == null)
			return null;
		return client.getClientHandler().getHttpClient();
	}

	@Override
	public <T> T suggest(Class<T> as, SuggestDefinition suggestionDef) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		String suggestCriteria = suggestionDef.getStringCriteria();
		String[] queries = suggestionDef.getQueryStrings();
		String optionsName = suggestionDef.getOptionsName();
		Integer limit = suggestionDef.getLimit();
		Integer cursorPosition = suggestionDef.getCursorPosition();

		if (suggestCriteria != null) {
			params.add("partial-q", suggestCriteria);
		}
		if (optionsName != null) {
			params.add("options", optionsName);
		}
		if (limit != null) {
			params.add("limit", Long.toString(limit));
		}
		if (cursorPosition != null) {
			params.add("cursor-position", Long.toString(cursorPosition));
		}
		if (queries != null) {
			for (String stringQuery : queries) {
				params.add("q", stringQuery);
			}
		}
		WebResource.Builder builder = null;
		builder = connection.path("suggest").queryParams(params)
				.accept("application/xml");
		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = builder.get(ClientResponse.class);

			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException(
					"User is not allowed to get suggestions",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("Suggest call failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		T entity = response.hasEntity() ? response.getEntity(as) : null;
		if (entity == null || (as != InputStream.class && as != Reader.class))
			response.close();

		return entity;
	}

	@Override
	public InputStream match(StructureWriteHandle document,
			String[] candidateRules, String mimeType, ServerTransform transform) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		HandleImplementation baseHandle = HandleAccessor.checkHandle(document, "match");
		if (candidateRules.length > 0) {
			for (String candidateRule : candidateRules) {
				params.add("rule", candidateRule);
			}
		}
		if (transform != null) {
			transform.merge(params);
		}
		WebResource.Builder builder = null;
		builder = connection.path("alert/match").queryParams(params)
				.accept("application/xml").type(mimeType);
		
		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = doPost(null, builder, baseHandle.sendContent(), false);

			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to match",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("match failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		InputStream entity = response.hasEntity() ?
				response.getEntity(InputStream.class) : null;
		if (entity == null)
			response.close();
		
		return entity;
	}

	@Override
	public InputStream match(QueryDefinition queryDef,
			long start, long pageLength, String[] candidateRules, ServerTransform transform) {
		if (queryDef == null) {
			throw new IllegalArgumentException("Cannot match null query");
		}

		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		if (start > 1) {
			params.add("start", Long.toString(start));
		}
		if (pageLength >= 0) {
			params.add("pageLength", Long.toString(pageLength));
		}
		if (transform != null) {
			transform.merge(params);
		}
		if (candidateRules.length > 0) {
			for (String candidateRule : candidateRules) {
				params.add("rule", candidateRule);
			}
		}

		if (queryDef.getOptionsName() != null) {
			params.add("options", queryDef.getOptionsName());
		}

		WebResource.Builder builder = null;
		String structure = null;
		HandleImplementation baseHandle = null;

		if (queryDef instanceof RawQueryDefinition) {
			StructureWriteHandle handle = ((RawQueryDefinition) queryDef).getHandle();
			baseHandle = HandleAccessor.checkHandle(handle, "match");

			if (logger.isDebugEnabled())
				logger.debug("Searching for structure {}", structure);

			builder = connection.path("alert/match").queryParams(params)
					.type("application/xml").accept("application/xml");
		} else if (queryDef instanceof StringQueryDefinition) {
			String text = ((StringQueryDefinition) queryDef).getCriteria();
			if (logger.isDebugEnabled())
				logger.debug("Searching for {} in transaction {}", text);

			if (text != null) {
				addEncodedParam(params, "q", text);
			}

			builder = connection.path("alert/match").queryParams(params)
					.accept("application/xml");
		} else if (queryDef instanceof StructuredQueryDefinition) {
			structure = ((StructuredQueryDefinition) queryDef).serialize();

			if (logger.isDebugEnabled())
				logger.debug("Searching for structure {} in transaction {}",
						structure);

			builder = connection.path("alert/match").queryParams(params)
					.type("application/xml").accept("application/xml");
		} else {
			throw new UnsupportedOperationException("Cannot match with "
					+ queryDef.getClass().getName());
		}
		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			if (queryDef instanceof StringQueryDefinition) {
				response = builder.get(ClientResponse.class);
			} else if (queryDef instanceof StructuredQueryDefinition) {
				response = builder.post(ClientResponse.class, structure);
			} else if (queryDef instanceof RawQueryDefinition) {
				response = doPost(null, builder, baseHandle.sendContent(), false);
			} else {
				throw new UnsupportedOperationException("Cannot match with "
						+ queryDef.getClass().getName());
			}

			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to match",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("match failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		InputStream entity = response.hasEntity() ?
				response.getEntity(InputStream.class) : null;
		if (entity == null)
			response.close();
		
		return entity;
	}

	@Override
	public InputStream match(String[] docIds, String[] candidateRules, ServerTransform transform) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		if (docIds.length > 0) {
			for (String docId : docIds) {
				params.add("uri", docId);
			}
		}
		if (candidateRules.length > 0) {
			for (String candidateRule : candidateRules) {
				params.add("rule", candidateRule);
			}
		}
		if (transform != null) {
			transform.merge(params);
		}
		WebResource.Builder builder = connection.path("alert/match").queryParams(params)
				.accept("application/xml");
		
		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try {
					Thread.sleep(nextDelay);
				} catch (InterruptedException e) {
				}
			}

			response = doGet(builder);

			status = response.getClientResponseStatus();

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest())
					setFirstRequest(false);

				break;
			}

			MultivaluedMap<String, String> responseHeaders = response.getHeaders();
			String retryAfterRaw = responseHeaders.getFirst("Retry-After");
			int retryAfter = (retryAfterRaw != null) ? Integer.valueOf(retryAfterRaw) : -1;

			response.close();

			nextDelay = Math.max(retryAfter, calculateDelay(randRetry, retry));
		}
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to match",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("match failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		InputStream entity = response.hasEntity() ?
				response.getEntity(InputStream.class) : null;
		if (entity == null)
			response.close();
		
		return entity;
	}

}
