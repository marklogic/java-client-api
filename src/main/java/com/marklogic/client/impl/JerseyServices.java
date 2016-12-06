/*
 * Copyright 2012-2016 MarkLogic Corporation
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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.DatatypeConverter;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.Transaction;
import com.marklogic.client.bitemporal.TemporalDescriptor;
import com.marklogic.client.bitemporal.TemporalDocumentManager.ProtectionLevel;
import com.marklogic.client.document.ContentDescriptor;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonParserHandle;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.ContentHandle;
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
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.RawQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.SuggestDefinition;
import com.marklogic.client.query.ValueLocator;
import com.marklogic.client.query.ValueQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.client.query.ValuesListDefinition;
import com.marklogic.client.semantics.Capability;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.GraphPermissions;
import com.marklogic.client.semantics.SPARQLBinding;
import com.marklogic.client.semantics.SPARQLBindings;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLRuleset;
import com.marklogic.client.util.EditableNamespaceContext;
import com.marklogic.client.util.RequestLogger;
import com.marklogic.client.util.RequestParameters;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
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
	static final String ERROR_NS = "http://marklogic.com/xdmp/error";

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
	private String database = null;
	private ThreadSafeClientConnManager connMgr;
	private ApacheHttpClient4 client;
	private WebResource connection;
	private boolean released = false;

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
		if ( response.getStatus() == 401 ) {
			FailedRequest failure = new FailedRequest();
			failure.setMessageString("Unauthorized");
			failure.setStatusString("Failed Auth");
			return failure;
		}
		String responseBody = response.getEntity(String.class);
		try {
			InputStream is = new ByteArrayInputStream(responseBody.getBytes("UTF-8"));
			FailedRequest handler = FailedRequest.getFailedRequest(
					response.getStatus(), response.getType(), is);
			if ( handler.getMessage() == null ) {
				handler.setMessageString(responseBody);
			}
			return handler;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 is unsupported", e);
		} finally {
			response.close();
		}
	}

	@Override
	public void connect(String host, int port, String database, String user, String password,
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

		connect(host, port, database, user, password, authenType, context, x509Verifier);
	}

	private void connect(String host, int port, String database, String user, String password,
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

		if (authenType != null && (authenType != Authentication.KERBEROS && authenType != Authentication.CERTIFICATE)) {
			if (user == null)
				throw new IllegalArgumentException("No user provided");
			if (password == null)
				throw new IllegalArgumentException("No password provided");
		}

		if (connection != null)
			connection = null;
		if (connMgr != null) {
			connMgr.shutdown();
			connMgr = null;
		}
		if (client != null) {
			client.destroy();
			client = null;
		}

		this.database = database;

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
		connMgr = new ThreadSafeClientConnManager(
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

		if (authenType != null && authenType != Authentication.CERTIFICATE) {
			List<String> authpref = new ArrayList<>();
			if (authenType == Authentication.BASIC)
				authpref.add(AuthPolicy.BASIC);
			else if (authenType == Authentication.DIGEST)
				authpref.add(AuthPolicy.DIGEST);
			else if (authenType == Authentication.KERBEROS)
				authpref.add(AuthPolicy.SPNEGO);
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
		configProps.put(ApacheHttpClient4Config.PROPERTY_DISABLE_COOKIES, true);
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

		if (authenType == null || authenType == Authentication.CERTIFICATE) {
			checkFirstRequest = false;
		} else if (authenType == Authentication.BASIC) {
			checkFirstRequest = false;

			client.addFilter(new HTTPBasicAuthFilter(user, password));
		} else if (authenType == Authentication.DIGEST) {
			checkFirstRequest = true;

			// workaround for JerseyClient bug 1445
			client.addFilter(new DigestChallengeFilter());

			client.addFilter(new HTTPDigestAuthFilter(user, password));
		} else if (authenType == Authentication.KERBEROS) {
			checkFirstRequest = false;
			client.addFilter(new HTTPKerberosAuthFilter(host, user));
		}
		else {
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

	private WebResource getConnection() {
		if ( connection != null ) return connection;
		else if ( released ) throw new IllegalStateException(
				"You cannot use this connected object anymore--connection has already been released");
		else throw new MarkLogicInternalException("Cannot proceed--connection is null for unknown reason");
	}

	@Override
	public void release() {
		released = true;
		if (databaseClient != null) {
			databaseClient = null;
		}

		if (client == null)
			return;

		if (logger.isDebugEnabled())
			logger.debug("Releasing connection");

		connection = null;
		connMgr.shutdown();
		connMgr = null;
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
		ClientResponse response = getConnection().path("ping").head();
		int statusCode = response.getClientResponseStatus().getStatusCode();
		if (statusCode != ClientResponse.Status.SERVICE_UNAVAILABLE.getStatusCode()) {
			response.close();
			return 0;
		}

		MultivaluedMap<String, String> responseHeaders = response.getHeaders();
		response.close();

		String retryAfterRaw = responseHeaders.getFirst("Retry-After");
		int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
		return Math.max(retryAfter, calculateDelay(randRetry, retry));
	}

	private RequestParameters addTemporalProtectionParams(RequestParameters params, String uri, ProtectionLevel level,
			String duration, Calendar expiryTime, String archivePath) {
		if (params == null)
			params = new RequestParameters();
		params.add("uri", uri);
		params.add("level", level.toString());
		if (duration != null)
			params.add("duration", duration);
		if (expiryTime != null) {
			String formattedSystemTime = DatatypeConverter.printDateTime(expiryTime);
			params.add("expireTime", formattedSystemTime);
		}
		if (archivePath != null)
			params.add("archivePath", archivePath);
		return params;
	}

	@Override
	public void protectDocument(RequestLogger requestLogger, String temporalDocumentURI, Transaction transaction,
			RequestParameters extraParams, ProtectionLevel level, String duration, 
			Calendar expiryTime, String archivePath) {
		if (temporalDocumentURI == null)
			throw new IllegalArgumentException(
					"Document protection for document identifier without uri");
		extraParams = addTemporalProtectionParams(extraParams, temporalDocumentURI, level, duration, expiryTime, archivePath);
		if (logger.isDebugEnabled())
			logger.debug("Protecting {} in transaction {}", temporalDocumentURI, getTransactionId(transaction));

		postResource(requestLogger, "documents/protection", transaction, extraParams, null, null, "protect");
	}
	@Override
	public void wipeDocument(RequestLogger reqlog, String temporalDocumentURI, Transaction transaction,
			RequestParameters extraParams) {
		if (logger.isDebugEnabled())
			logger.debug("Wiping {} in transaction {}", temporalDocumentURI, getTransactionId(transaction));
		extraParams.add("result", "wiped");
		extraParams.add("uri", temporalDocumentURI);
		deleteResource(reqlog, "documents", transaction, extraParams, null);
		logRequest(reqlog, "wiped %s document", temporalDocumentURI);
	}

	@Override
	public TemporalDescriptor deleteDocument(RequestLogger reqlog, DocumentDescriptor desc,
			Transaction transaction, Set<Metadata> categories, RequestParameters extraParams)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document delete for document identifier without uri");

		if (logger.isDebugEnabled())
			logger.debug("Deleting {} in transaction {}", uri, getTransactionId(transaction));

		WebResource webResource = makeDocumentResource(makeDocumentParams(uri,
				categories, transaction, extraParams));

		WebResource.Builder builder = addVersionHeader(desc,
				webResource.getRequestBuilder(), "If-Match");
		addTransactionScopedCookies(builder, webResource, transaction);

		Function<WebResource.Builder, ClientResponse> doDeleteFunction = funcBuilder -> funcBuilder
				.delete(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doDeleteFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
		int statusCode = response.getStatus();

		if (status == ClientResponse.Status.NOT_FOUND) {
			response.close();
			throw new ResourceNotFoundException(
					"Could not delete non-existent document");
		}
		if (statusCode == 428) {
			FailedRequest failure = extractErrorFields(response);
			if (failure.getMessageCode().equals("RESTAPI-CONTENTNOVERSION")) {
				throw new FailedRequestException(
						"Content version required to delete document", failure);
			}
			throw new FailedRequestException(
					"Precondition required to delete document", failure);
		} else if (status == ClientResponse.Status.FORBIDDEN) {
			FailedRequest failure = extractErrorFields(response);
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
		MultivaluedMap<String, String> responseHeaders = response.getHeaders();
		TemporalDescriptor temporalDesc = updateTemporalSystemTime(desc, responseHeaders);

		response.close();
		logRequest(reqlog, "deleted %s document", uri);
		return temporalDesc;
	}

	@Override
	public boolean getDocument(RequestLogger reqlog, DocumentDescriptor desc,
			Transaction transaction, Set<Metadata> categories,
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
			return getDocumentImpl(reqlog, desc, transaction, categories,
					extraParams, metadataFormat, metadataHandle, contentHandle);
		} else if (metadataBase != null) {
			return getDocumentImpl(reqlog, desc, transaction, categories,
					extraParams, metadataMimetype, metadataHandle);
		} else if (contentBase != null) {
			return getDocumentImpl(reqlog, desc, transaction, null,
					extraParams, contentMimetype, contentHandle);
		}

		return false;
	}

	private int getRetryAfterTime(ClientResponse response) {
		MultivaluedMap<String, String> responseHeaders = response.getHeaders();
		String retryAfterRaw = responseHeaders.getFirst("Retry-After");
		return (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
	}

	private ClientResponse makeRequest(Builder builder, Function<Builder, ClientResponse> doFunction, Consumer<Boolean> resendableConsumer) {
		ClientResponse response = null;
		ClientResponse.Status status = null;
		long startTime = System.currentTimeMillis();
		int nextDelay = 0;
		int retry = 0;
		/*
		 * This loop is for retrying the request if the service is unavailable
		 */
		for (; retry < minRetry || (System.currentTimeMillis() - startTime) < maxDelay; retry++) {
			if (nextDelay > 0) {
				try { Thread.sleep(nextDelay);} catch (InterruptedException e) {}
			}

			/*
			 * Execute the function which is passed as an argument
			 * in order to get the ClientResponse 
			 */
			response = doFunction.apply(builder);
			status = response.getClientResponseStatus();
			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE) {
				if (isFirstRequest()) setFirstRequest(false);
				/*
				 * If we don't get a service unavailable status, we break 
				 * from the retrying loop and return the response
				 */
				break;
			}
			/*
			 * This code will be executed whenever the service is unavailable.
			 * When the service becomes unavailable, we close the ClientResponse
			 * we got and retry it to try and get a new ClientResponse
			 */
			response.close();
			/*
			 * There are scenarios where we don't want to retry and we just want to
			 * throw ResourceNotResendableException. In that case, we pass that code from 
			 * the caller through the Consumer and execute it here. In the rest of the
			 * scenarios, we pass it as null and it is just a no-operation.
			 */
			if(resendableConsumer != null) resendableConsumer.accept(null);
			/*
			 * Calculate the delay before which we shouldn't retry
			 */
			nextDelay = Math.max(getRetryAfterTime(response), calculateDelay(randRetry, retry));
		}
		/*
		 * If the service is still unavailable after all the retries, we throw a 
		 * FailedRequestException indicating that the service is unavailable.
		 */
		if (status == ClientResponse.Status.SERVICE_UNAVAILABLE) {
			checkFirstRequest();
			throw new FailedRequestException(
					"Service unavailable and maximum retry period elapsed: "+
						    Math.round((System.currentTimeMillis() - startTime) / 1000)+
						    " seconds after "+retry+" retries");
		}
		/*
		 * Once we break from the retry loop, we just return the ClientResponse
		 * back to the caller in order to proceed with the flow
		 */
		return response;
	}

	private boolean getDocumentImpl(RequestLogger reqlog,
			DocumentDescriptor desc, Transaction transaction,
			Set<Metadata> categories, RequestParameters extraParams,
			String mimetype, AbstractReadHandle handle)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document read for document identifier without uri");

		if (logger.isDebugEnabled())
			logger.debug("Getting {} in transaction {}", uri, getTransactionId(transaction));

		addPointInTimeQueryParam(extraParams, handle);

		WebResource webResource = makeDocumentResource(
				makeDocumentParams(uri, categories, transaction, extraParams));
		WebResource.Builder builder = webResource.accept(mimetype);
		addTransactionScopedCookies(builder, webResource, transaction);

		if (extraParams != null && extraParams.containsKey("range"))
			builder = builder.header("range", extraParams.get("range").get(0));

		builder = addVersionHeader(desc, builder, "If-None-Match");

		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> funcBuilder
				.get(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
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
				uri, (transaction != null) ? transaction.getTransactionId() : "no",
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
	public DocumentPage getBulkDocuments(RequestLogger reqlog, long serverTimestamp,
			Transaction transaction, Set<Metadata> categories, 
			Format format, RequestParameters extraParams, boolean withContent, String... uris)
			throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		boolean hasMetadata = categories != null && categories.size() > 0;
		JerseyResultIterator iterator = 
			getBulkDocumentsImpl(reqlog, serverTimestamp, transaction, categories, format, extraParams, withContent, uris);
		return new JerseyDocumentPage(iterator, withContent, hasMetadata);
	}

	@Override
	public DocumentPage getBulkDocuments(RequestLogger reqlog, long serverTimestamp,
			QueryDefinition querydef,
			long start, long pageLength,
			Transaction transaction,
			SearchReadHandle searchHandle, QueryView view,
			Set<Metadata> categories, Format format, RequestParameters extraParams)
			throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		boolean hasMetadata = categories != null && categories.size() > 0;
		boolean hasContent = true;
		JerseyResultIterator iterator = 
			getBulkDocumentsImpl(reqlog, serverTimestamp, querydef, start, pageLength, transaction, 
				searchHandle, view, categories, format, extraParams);
		return new JerseyDocumentPage(iterator, hasContent, hasMetadata);
	}

	private class JerseyDocumentPage extends BasicPage<DocumentRecord> implements DocumentPage, Iterator<DocumentRecord> {
		private JerseyResultIterator iterator;
		private Iterator<DocumentRecord> docRecordIterator;
		private boolean hasMetadata;
		private boolean hasContent;

		JerseyDocumentPage(JerseyResultIterator iterator, boolean hasContent, boolean hasMetadata) {
			super(
				new ArrayList<DocumentRecord>().iterator(),
				iterator != null ? iterator.getStart() : 1,
				iterator != null ? iterator.getPageSize() : 0,
				iterator != null ? iterator.getTotalSize() : 0
			);
			this.iterator = iterator;
			this.hasContent = hasContent;
			this.hasMetadata = hasMetadata;
			if ( iterator == null ) {
				setSize(0);
			} else if ( hasContent && hasMetadata ) {
				setSize(iterator.getSize() / 2);
			} else {
				setSize(iterator.getSize());
			}
		}

		@Override
		public Iterator<DocumentRecord> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			if ( iterator == null ) return false;
			return iterator.hasNext();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public DocumentRecord next() {
			if ( iterator == null ) throw new NoSuchElementException("No documents available");
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
			return record;
		}

		@Override
		public <T extends AbstractReadHandle> T nextContent(T contentHandle) {
			return next().getContent(contentHandle);
		}

		@Override
		public void close() {
			if ( iterator != null ) iterator.close();
		}
	}

	private JerseyResultIterator getBulkDocumentsImpl(RequestLogger reqlog, long serverTimestamp,
			Transaction transaction, Set<Metadata> categories, 
			Format format, RequestParameters extraParams, boolean withContent, String... uris)
			throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {

		String path = "documents";
		RequestParameters params = new RequestParameters();
		if ( extraParams != null ) params.putAll(extraParams);
		if (serverTimestamp != -1) params.add("timestamp", Long.toString(serverTimestamp));
		addCategoryParams(categories, params, withContent);
		if (format != null)        params.add("format",     format.toString().toLowerCase());
		for (String uri: uris) {
			params.add("uri", uri);
		}
		JerseyResultIterator iterator = getIteratedResourceImpl(DefaultJerseyResultIterator.class,
			reqlog, path, transaction, params, MultiPartMediaTypes.MULTIPART_MIXED);
		if ( iterator != null ) {
			if ( iterator.getStart() == -1 ) iterator.setStart(1);
			if ( iterator.getSize() != -1 ) {
				if ( iterator.getPageSize() == -1 ) iterator.setPageSize(iterator.getSize());
				if ( iterator.getTotalSize() == -1 )  iterator.setTotalSize(iterator.getSize());
			}
		}
		return iterator;
	}

	private JerseyResultIterator getBulkDocumentsImpl(RequestLogger reqlog, long serverTimestamp,
			QueryDefinition querydef, long start, long pageLength,
			Transaction transaction, SearchReadHandle searchHandle, QueryView view,
            Set<Metadata> categories, Format format, RequestParameters extraParams)
			throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		if ( extraParams != null ) params.putAll(extraParams);
		boolean withContent = true;
		addCategoryParams(categories, params, withContent);
		if (searchHandle != null && view != null) params.add("view", view.toString().toLowerCase());
		if (start > 1)             params.add("start",      Long.toString(start));
		if (pageLength >= 0)       params.add("pageLength", Long.toString(pageLength));
		if (serverTimestamp != -1) params.add("timestamp",  Long.toString(serverTimestamp));
		addPointInTimeQueryParam(params, searchHandle);
		if (format != null)        params.add("format",     format.toString().toLowerCase());
        HandleImplementation handleBase = HandleAccessor.as(searchHandle);
		if ( format == null && searchHandle != null ) {
			if ( Format.XML == handleBase.getFormat() ) {
				params.add("format", "xml");
			} else if ( Format.JSON == handleBase.getFormat() ) {
				params.add("format", "json");
			}
		}

		JerseySearchRequest request = 
			generateSearchRequest(reqlog, querydef, MultiPartMediaTypes.MULTIPART_MIXED, transaction, params, null);
        ClientResponse response = request.getResponse();
        if ( response == null ) return null;
        MultiPart entity = null;
        if ( searchHandle != null ) {
            updateServerTimestamp(handleBase, response.getHeaders());
            if ( response.hasEntity() ) {
                entity = response.getEntity(MultiPart.class);
                if ( entity != null ) {
                    List<BodyPart> partList = entity.getBodyParts();
                    if ( partList != null && partList.size() > 0 ) {
                        BodyPart searchResponsePart = partList.get(0);
                        handleBase.receiveContent(
                            searchResponsePart.getEntityAs(handleBase.receiveAs())
                        );
                        partList = partList.subList(1, partList.size());
                    }
                    Closeable closeable = new MultipartCloseable(response, entity);
                    return makeResults(JerseyServiceResultIterator.class, reqlog, "read", "resource",
                        partList, response, closeable);
                }
            }
        }
        return makeResults(JerseyServiceResultIterator.class, reqlog, "read", "resource", response);
	}

	private boolean getDocumentImpl(RequestLogger reqlog,
			DocumentDescriptor desc, Transaction transaction,
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
					getTransactionId(transaction));

		addPointInTimeQueryParam(extraParams, contentHandle);

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transaction, extraParams, true);
		docParams.add("format", metadataFormat);

		WebResource webResource = makeDocumentResource(docParams);
		WebResource.Builder builder = webResource.getRequestBuilder();
		addTransactionScopedCookies(builder, webResource, transaction);
		builder = addVersionHeader(desc, builder, "If-None-Match");

		MediaType multipartType = Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE);
		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> funcBuilder.accept(multipartType)
				.get(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
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
				uri, (transaction != null) ? transaction.getTransactionId() : "no",
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
			updateDescriptor(contentBase, responseHeaders);
		}

		metadataBase.receiveContent(partList.get(0).getEntityAs(
				metadataBase.receiveAs()));

		Object contentEntity = contentPart.getEntityAs(contentBase.receiveAs());
		contentBase.receiveContent((reqlog != null) ? reqlog
				.copyContent(contentEntity) : contentEntity);

		try { entity.close(); } catch (IOException e) {}
		response.close();

		return true;
	}

	@Override
	public DocumentDescriptor head(RequestLogger reqlog, String uri,
			Transaction transaction) throws ForbiddenUserException,
			FailedRequestException {
		ClientResponse response = headImpl(reqlog, uri, transaction, makeDocumentResource(makeDocumentParams(uri,
				null, transaction, null)));
		
		// 404
		if (response == null) return null;
		
		MultivaluedMap<String, String> responseHeaders = response.getHeaders();

		response.close();
		logRequest(reqlog, "checked %s document from %s transaction", uri,
				(transaction != null) ? transaction.getTransactionId() : "no");

		DocumentDescriptorImpl desc = new DocumentDescriptorImpl(uri, false);

		updateVersion(desc, responseHeaders);
		updateDescriptor(desc, responseHeaders);

		return desc;
	}
	
	@Override
	public boolean exists(String uri) throws ForbiddenUserException,
			FailedRequestException {
		return headImpl(null, uri, null, getConnection().path(uri)) == null ? false : true;
	}
	
	public ClientResponse headImpl(RequestLogger reqlog, String uri,
			Transaction transaction, WebResource webResource) {
		if (uri == null)
			throw new IllegalArgumentException(
					"Existence check for document identifier without uri");

		if (logger.isDebugEnabled())
			logger.debug("Requesting head for {} in transaction {}", uri,
					getTransactionId(transaction));

		WebResource.Builder builder = webResource.getRequestBuilder();
		addTransactionScopedCookies(builder, webResource, transaction);

		Function<WebResource.Builder, ClientResponse> doHeadFunction = funcBuilder -> funcBuilder
				.head();
		ClientResponse response = makeRequest(builder, doHeadFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
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
	public TemporalDescriptor putDocument(RequestLogger reqlog, DocumentDescriptor desc,
			Transaction transaction, Set<Metadata> categories,
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
			return putPostDocumentImpl(reqlog, "put", desc, transaction, categories,
					extraParams, metadataMimetype, metadataHandle,
					contentMimetype, contentHandle);
		} else if (metadataBase != null) {
			return putPostDocumentImpl(reqlog, "put", desc, transaction, categories, false,
					extraParams, metadataMimetype, metadataHandle);
		} else if (contentBase != null) {
			return putPostDocumentImpl(reqlog, "put", desc, transaction, null, true, 
					extraParams, contentMimetype, contentHandle);
		}
		throw new IllegalArgumentException("Either metadataHandle or contentHandle must not be null");
	}

	@Override
	public DocumentDescriptorImpl postDocument(RequestLogger reqlog, DocumentUriTemplate template,
			Transaction transaction, Set<Metadata> categories, RequestParameters extraParams,
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
			putPostDocumentImpl(reqlog, "post", desc, transaction, categories, extraParams,
					metadataMimetype, metadataHandle, contentMimetype, contentHandle);
		} else if (contentBase != null) {
			putPostDocumentImpl(reqlog, "post", desc, transaction, null, true, extraParams,
					contentMimetype, contentHandle);
		}

		return desc;
	}

	private TemporalDescriptor putPostDocumentImpl(RequestLogger reqlog, String method, DocumentDescriptor desc,
			Transaction transaction, Set<Metadata> categories, boolean isOnContent, RequestParameters extraParams,
			String mimetype, AbstractWriteHandle handle)
	throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		String uri = desc.getUri();

		HandleImplementation handleBase = HandleAccessor.as(handle);

		if (logger.isDebugEnabled())
			logger.debug("Sending {} document in transaction {}",
					(uri != null) ? uri : "new", getTransactionId(transaction));

		logRequest(
				reqlog,
				"writing %s document from %s transaction with %s mime type and %s metadata categories",
				(uri != null) ? uri : "new",
				(transaction != null) ? transaction.getTransactionId() : "no",
				(mimetype != null) ? mimetype : "no",
				stringJoin(categories, ", ", "no"));

		WebResource webResource = makeDocumentResource(
				makeDocumentParams(
						uri, categories, transaction, extraParams, isOnContent
						));

		WebResource.Builder builder = webResource.type(
				(mimetype != null) ? mimetype : MediaType.WILDCARD);
		addTransactionScopedCookies(builder, webResource, transaction);
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
		int statusCode = -1;
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
			statusCode = response.getStatus();

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

			int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
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
			throw new ResourceNotFoundException(
					"Could not write non-existent document",
					extractErrorFields(response));
		}
		if (statusCode == 428) {
			FailedRequest failure = extractErrorFields(response);
			if (failure.getMessageCode().equals("RESTAPI-CONTENTNOVERSION")) {
				throw new FailedRequestException(
						"Content version required to write document", failure);
			}
			throw new FailedRequestException(
					"Precondition required to write document", failure);
		} else if (status == ClientResponse.Status.FORBIDDEN) {
			FailedRequest failure = extractErrorFields(response);
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
		TemporalDescriptor temporalDesc = updateTemporalSystemTime(desc, responseHeaders);
		response.close();
		return temporalDesc;
	}

	private TemporalDescriptor putPostDocumentImpl(RequestLogger reqlog, String method, DocumentDescriptor desc,
			Transaction transaction, Set<Metadata> categories, RequestParameters extraParams,
			String metadataMimetype, DocumentMetadataWriteHandle metadataHandle, String contentMimetype,
			AbstractWriteHandle contentHandle)
	throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		String uri = desc.getUri();

		if (logger.isDebugEnabled())
			logger.debug("Sending {} multipart document in transaction {}",
					(uri != null) ? uri : "new", getTransactionId(transaction));

		logRequest(
				reqlog,
				"writing %s document from %s transaction with %s metadata categories and content",
				(uri != null) ? uri : "new",
				(transaction != null) ? transaction.getTransactionId() : "no",
				stringJoin(categories, ", ", "no"));

		MultivaluedMap<String, String> docParams =
			makeDocumentParams(uri, categories, transaction, extraParams, true);

		WebResource webResource = makeDocumentResource(docParams);
		WebResource.Builder builder = webResource.getRequestBuilder();
		addTransactionScopedCookies(builder, webResource, transaction);
		if (uri != null) {
			builder = addVersionHeader(desc, builder, "If-Match");
		}

		MediaType multipartType = Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int statusCode = -1;
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
			statusCode = response.getStatus();

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

			int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
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
		if (statusCode == 428) {
			FailedRequest failure = extractErrorFields(response);
			if (failure.getMessageCode().equals("RESTAPI-CONTENTNOVERSION")) {
				throw new FailedRequestException(
						"Content version required to write document", failure);
			}
			throw new FailedRequestException(
					"Precondition required to write document", failure);
		} else if (status == ClientResponse.Status.FORBIDDEN) {
			FailedRequest failure = extractErrorFields(response);
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
		TemporalDescriptor temporalDesc = updateTemporalSystemTime(desc, responseHeaders);
		response.close();
		return temporalDesc;
	}

	@Override
	public void patchDocument(RequestLogger reqlog, DocumentDescriptor desc, Transaction transaction,
			Set<Metadata> categories, boolean isOnContent, DocumentPatchHandle patchHandle)
	throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		patchDocument(reqlog, desc, transaction, categories, isOnContent, null, null, patchHandle);
	}

	@Override
	public void patchDocument(RequestLogger reqlog, DocumentDescriptor desc, Transaction transaction,
			Set<Metadata> categories, boolean isOnContent, RequestParameters extraParams, String sourceDocumentURI, 
			DocumentPatchHandle patchHandle)
			throws ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation patchBase = HandleAccessor.checkHandle(patchHandle, "patch");

		if(sourceDocumentURI != null)
			extraParams.add("source-document", sourceDocumentURI);
		putPostDocumentImpl(reqlog, "patch", desc, transaction, categories, isOnContent, extraParams, patchBase.getMimetype(),
				patchHandle);
	}

	@Override
	public Transaction openTransaction(String name, int timeLimit)
			throws ForbiddenUserException, FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Opening transaction");

		MultivaluedMap<String, String> transParams = new MultivaluedMapImpl();
		if (name != null || timeLimit > 0) {
			if (name != null)
				addEncodedParam(transParams, "name", name);
			if (timeLimit > 0)
				transParams.add("timeLimit", String.valueOf(timeLimit));
		}
		if ( database != null ) {
			addEncodedParam(transParams, "database", database);
		}

		WebResource resource = (transParams != null) ? getConnection().path(
				"transactions").queryParams(transParams) : getConnection()
				.path("transactions");
		WebResource.Builder builder = resource.getRequestBuilder();

		Function<WebResource.Builder, ClientResponse> doPostFunction = funcBuilder -> funcBuilder
				.post(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doPostFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException(
					"User is not allowed to open transactions",
					extractErrorFields(response));
		if (status != ClientResponse.Status.SEE_OTHER)
			throw new FailedRequestException("transaction open failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));

		String location = response.getHeaders().getFirst("Location");
		String hostId = null;
		for ( NewCookie newCookie : response.getCookies() ) {
			if ( "HostId".equalsIgnoreCase(newCookie.getName()) ) {
				hostId =  newCookie.getValue();
				break;
			}
		}
		response.close();
		if (location == null)
			throw new MarkLogicInternalException(
					"transaction open failed to provide location");
		if (!location.contains("/"))
			throw new MarkLogicInternalException(
					"transaction open produced invalid location: " + location);

		String transactionId = location.substring(location.lastIndexOf("/") + 1);
		return new TransactionImpl(this, transactionId, response.getCookies());
	}

	@Override
	public void commitTransaction(Transaction transaction)
			throws ForbiddenUserException, FailedRequestException {
		completeTransaction(transaction, "commit");
	}

	@Override
	public void rollbackTransaction(Transaction transaction)
			throws ForbiddenUserException, FailedRequestException {
		completeTransaction(transaction, "rollback");
	}

	private void completeTransaction(Transaction transaction, String result)
			throws ForbiddenUserException, FailedRequestException {
		if (result == null)
			throw new MarkLogicInternalException(
					"transaction completion without operation");
		if (transaction == null)
			throw new MarkLogicInternalException(
					"transaction completion without id: " + result);

		if (logger.isDebugEnabled())
			logger.debug("Completing transaction {} with {}", transaction.getTransactionId(),
					result);

		MultivaluedMap<String, String> transParams = new MultivaluedMapImpl();
		transParams.add("result", result);

		WebResource webResource = getConnection().path("transactions/" + transaction.getTransactionId())
				.queryParams(transParams);

		WebResource.Builder builder = webResource.getRequestBuilder();
		addTransactionScopedCookies(builder, webResource, transaction);

		Function<WebResource.Builder, ClientResponse> doPostFunction = funcBuilder -> funcBuilder
				.post(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doPostFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();

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
			Set<Metadata> categories, Transaction transaction,
			RequestParameters extraParams) {
		return makeDocumentParams(uri, categories, transaction, extraParams,
				false);
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri,
			Set<Metadata> categories, Transaction transaction,
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
		if ( database != null ) {
			addEncodedParam(docParams, "database", database);
		}
		if (categories == null || categories.size() == 0) {
			docParams.add("category", "content");
		} else {
			if (withContent)
				docParams.add("category", "content");
			if (categories.contains(Metadata.ALL)) {
				docParams.add("category", "metadata");
			} else {
				for (Metadata category : categories)
					docParams.add("category", category.toString().toLowerCase());
			}
		}
		if (transaction != null) {
			docParams.add("txid", transaction.getTransactionId());
		}
		return docParams;
	}

	private WebResource makeDocumentResource(
			MultivaluedMap<String, String> queryParams) {
		return getConnection().path("documents").queryParams(queryParams);
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
		updateServerTimestamp(desc, headers);
	}

	private TemporalDescriptor updateTemporalSystemTime(DocumentDescriptor desc,
			MultivaluedMap<String, String> headers)
	{
		if (headers == null) return null;
		
		DocumentDescriptorImpl temporalDescriptor;
		if ( desc instanceof DocumentDescriptorImpl ) {
			temporalDescriptor = (DocumentDescriptorImpl) desc;
		} else {
			temporalDescriptor = new DocumentDescriptorImpl(desc.getUri(), false);
		}
		temporalDescriptor.setTemporalSystemTime(getHeaderTemporalSystemTime(headers));
		return temporalDescriptor;
	}

	private String getHeaderTemporalSystemTime(MultivaluedMap<String, String> headers) {
		if (headers.containsKey("x-marklogic-system-time")) {
			List<String> values = headers.get("x-marklogic-system-time");
			if (values != null) {
				return values.get(0);
			}
		}
		return null;
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
        ContentDisposition contentDisposition = part.getContentDisposition();
		if (part.getHeaders().containsKey("vnd.marklogic.document-format")) {
			String value = part.getHeaders().getFirst("vnd.marklogic.document-format");
			if (value != null) {
				return Format.valueOf(value.toUpperCase());
			}
		} else if ( contentDisposition != null ) {
            Map<String, String> parameters = contentDisposition.getParameters();
            if ( parameters != null && parameters.get("format") != null ) {
                return Format.valueOf(parameters.get("format").toUpperCase());
            }
		} else if ( part.getHeaders().containsKey("Content-Type") ) {
			String value = part.getHeaders().getFirst("Content-Type");
			if (value != null) {
				return Format.getFromMimetype(value);
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

	private void updateServerTimestamp(ContentDescriptor descriptor,
			MultivaluedMap<String,String> headers) {
		updateServerTimestamp(descriptor, getHeaderServerTimestamp(headers));
	}

	private long getHeaderServerTimestamp(MultivaluedMap<String,String> headers) {
		List<String> values = headers.get("ML-Effective-Timestamp");
		if (values != null) {
			String timestamp = values.get(0);
			if (timestamp != null && timestamp.length() > 0) {
				return Long.parseLong(timestamp);
			}
		}
		return -1;
	}

	private void updateServerTimestamp(ContentDescriptor descriptor, long timestamp) {
		if ( descriptor instanceof HandleImplementation ) {
			if ( descriptor != null && timestamp != -1 ) {
				((HandleImplementation) descriptor).setResponseServerTimestamp(timestamp);
			}
		}
	}

	private long getHeaderLength(MultivaluedMap<String, String> headers) {
		if (headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
			List<String> values = headers.get(HttpHeaders.CONTENT_LENGTH);
			if (values != null) {
				return Long.parseLong(values.get(0));
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
				version = Long.parseLong(value.substring(1, value.length() - 1));
			}
		}
		descriptor.setVersion(version);
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
	public <T extends SearchReadHandle> T search(RequestLogger reqlog, T searchHandle,
			QueryDefinition queryDef, long start, long len, QueryView view, Transaction transaction, String forestName)
		throws ForbiddenUserException, FailedRequestException
	{
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		if (start > 1) {
			params.add("start", Long.toString(start));
		}

		if (len > 0) {
			params.add("pageLength", Long.toString(len));
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

		addPointInTimeQueryParam(params, searchHandle);

		@SuppressWarnings("rawtypes")
		HandleImplementation searchBase = HandleAccessor.checkHandle(searchHandle, "search");

		Format searchFormat = searchBase.getFormat();
		switch(searchFormat) {
			case UNKNOWN:
				searchFormat = Format.XML;
				break;
			case JSON:
			case XML:
				break;
			default:
				throw new UnsupportedOperationException("Only XML and JSON search results are possible.");
		}

		String mimetype = searchFormat.getDefaultMimetype();

		JerseySearchRequest request = generateSearchRequest(reqlog, queryDef, mimetype, transaction, params, forestName);

		ClientResponse response = request.getResponse();
		if ( response == null ) return null;
		
		Class as = searchBase.receiveAs();

		Object entity = response.hasEntity() ? response.getEntity(as) : null;
		if (entity == null || (as != InputStream.class && as != Reader.class))
			response.close();
		searchBase.receiveContent(entity);
		updateDescriptor(searchBase, response.getHeaders());

		logRequest( reqlog,
			"searched starting at %s with length %s in %s transaction with %s mime type",
			start, len, getTransactionId(transaction), mimetype);

		return searchHandle;
	}

    private JerseySearchRequest generateSearchRequest(RequestLogger reqlog, QueryDefinition queryDef, 
            String mimetype, Transaction transaction, MultivaluedMap<String, String> params, String forestName) {
        if ( params == null ) params = new MultivaluedMapImpl();
        if ( database != null ) addEncodedParam(params, "database", database);
        if ( forestName != null ) addEncodedParam(params, "forest-name", forestName);
        return new JerseySearchRequest(reqlog, queryDef, mimetype, transaction, params);
    }

    private class JerseySearchRequest {
        RequestLogger reqlog;
        QueryDefinition queryDef;
        String mimetype;
        MultivaluedMap<String, String> params;
        Transaction transaction;

        WebResource webResource = null;
        WebResource.Builder builder = null;
        String structure = null;
        HandleImplementation baseHandle = null;

        JerseySearchRequest(RequestLogger reqlog, QueryDefinition queryDef, String mimetype, 
                Transaction transaction, MultivaluedMap<String, String> params) {
            this.reqlog = reqlog;
            this.queryDef = queryDef;
            this.mimetype = mimetype;
            this.transaction = transaction;
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

            if (transaction != null) {
                params.add("txid", transaction.getTransactionId());
            }
        }

        void init() {
            if (queryDef instanceof StringQueryDefinition) {
                String text = ((StringQueryDefinition) queryDef).getCriteria();

                if (text != null) {
                    addEncodedParam(params, "q", text);
                }
            }
            if (queryDef instanceof StructuredQueryDefinition) {
                structure = ((StructuredQueryDefinition) queryDef).serialize();

                if (logger.isDebugEnabled()) {
                    String text = ((StringQueryDefinition) queryDef).getCriteria();
                    String qtextMessage = text == null ? "" : " and string query \"" + text + "\"";
                    logger.debug("Searching for structure {}{}", structure, qtextMessage);
                }

                String payloadMimetype = "application/xml";
                if (queryDef instanceof RawQueryDefinition) {
                    StructureWriteHandle handle = ((RawQueryDefinition) queryDef).getHandle();
                    baseHandle = HandleAccessor.checkHandle(handle, "search");

                    Format payloadFormat = getStructuredQueryFormat(baseHandle);
                    payloadMimetype = getMimetypeWithDefaultXML(payloadFormat, baseHandle);
                }

                webResource = getConnection().path("search").queryParams(params);
                builder = (payloadMimetype != null) ?
                    webResource.type(payloadMimetype).accept(mimetype) :
                    webResource.accept(mimetype);
            } else if (queryDef instanceof RawQueryDefinition) {
                if (logger.isDebugEnabled())
                    logger.debug("Raw search");

                StructureWriteHandle handle = ((RawQueryDefinition) queryDef).getHandle();
                baseHandle = HandleAccessor.checkHandle(handle, "search");

                Format payloadFormat = getStructuredQueryFormat(baseHandle);
                String payloadMimetype = getMimetypeWithDefaultXML(payloadFormat, baseHandle);

                String path = (queryDef instanceof RawQueryByExampleDefinition) ?
                    "qbe" : "search";

                webResource = getConnection().path(path).queryParams(params);
                builder = (payloadMimetype != null) ?
                    webResource.type(payloadMimetype).accept(mimetype) :
                    webResource.accept(mimetype);
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

                webResource = getConnection().path("keyvalue").queryParams(params);
                builder = webResource.accept(mimetype);
            } else if (queryDef instanceof CombinedQueryDefinition) {
                structure = ((CombinedQueryDefinition) queryDef).serialize();

                if (logger.isDebugEnabled())
                    logger.debug("Searching for combined query {}", structure);

                webResource = getConnection().path("search").queryParams(params);
                builder = webResource.type("application/xml").accept(mimetype);
            } else if (queryDef instanceof StringQueryDefinition) {
                String text = ((StringQueryDefinition) queryDef).getCriteria();
                if (logger.isDebugEnabled())
                    logger.debug("Searching for string [{}]", text);

                webResource = getConnection().path("search").queryParams(params);
                builder = webResource.type("application/xml").accept(mimetype);
            } else if (queryDef instanceof DeleteQueryDefinition) {
                if (logger.isDebugEnabled())
                    logger.debug("Searching for deletes");

                webResource = getConnection().path("search").queryParams(params);
                builder = webResource.accept(mimetype);
            } else {
                throw new UnsupportedOperationException("Cannot search with "
                        + queryDef.getClass().getName());
            }

            addTransactionScopedCookies(builder, webResource, transaction);
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

                if (queryDef instanceof KeyValueQueryDefinition) {
                    response = doGet(builder);
                } else if (queryDef instanceof StructuredQueryDefinition) {
                    response = doPost(reqlog, builder, structure, true);
                } else if (queryDef instanceof CombinedQueryDefinition) {
                    response = doPost(reqlog, builder, structure, true);
                } else if (queryDef instanceof DeleteQueryDefinition) {
                    response = doGet(builder);
                } else if (queryDef instanceof RawQueryDefinition) {
                    response = doPost(reqlog, builder, baseHandle.sendContent(), true);
                } else if (queryDef instanceof StringQueryDefinition) {
                    response = doGet(builder);
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
                int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;

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

    private Format getStructuredQueryFormat(HandleImplementation baseHandle) {
        Format payloadFormat = baseHandle.getFormat();
        if (payloadFormat == Format.UNKNOWN) {
            payloadFormat = null;
        } else if (payloadFormat != Format.XML && payloadFormat != Format.JSON) {
            throw new IllegalArgumentException(
                    "Cannot perform raw search for format "+payloadFormat.name());
        }
        return payloadFormat;
    }

    private String getMimetypeWithDefaultXML(Format payloadFormat, HandleImplementation baseHandle) {
        String payloadMimetype = baseHandle.getMimetype();
        if (payloadFormat != null) {
            if (payloadMimetype == null)
                payloadMimetype = payloadFormat.getDefaultMimetype();
        } else if (payloadMimetype == null) {
            payloadMimetype = "application/xml";
        }
        return payloadMimetype;
    }

	@Override
	public void deleteSearch(RequestLogger reqlog, DeleteQueryDefinition queryDef,
			Transaction transaction) throws ForbiddenUserException,
			FailedRequestException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		if (queryDef.getDirectory() != null) {
			addEncodedParam(params, "directory", queryDef.getDirectory());
		}

		addEncodedParam(params, "collection", queryDef.getCollections());

		if (transaction != null) {
			params.add("txid", transaction.getTransactionId());
		}
		if ( database != null ) {
			addEncodedParam(params, "database", database);
		}

		WebResource webResource = getConnection().path("search").queryParams(params);

		WebResource.Builder builder = webResource.getRequestBuilder();
		addTransactionScopedCookies(builder, webResource, transaction);

		Function<WebResource.Builder, ClientResponse> doDeleteFunction = funcBuilder -> funcBuilder
				.delete(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doDeleteFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to delete",
					extractErrorFields(response));
		}

		if (status != ClientResponse.Status.NO_CONTENT) {
			throw new FailedRequestException("delete failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		response.close();

		logRequest(
				reqlog,
				"deleted search results in %s transaction",
				getTransactionId(transaction));
	}

	@Override
	public void delete(RequestLogger logger, Transaction transaction, String... uris)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		addEncodedParam(((RequestParametersImplementation) params).getMapImpl(), "uri", uris);
		deleteResource(logger, "documents", transaction, params, null);
	}

	@Override
	public <T> T values(Class<T> as, ValuesDefinition valDef, String mimetype,
			long start, long pageLength, Transaction transaction)
		throws ForbiddenUserException, FailedRequestException
	{
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
				if (!queryDef.getOptionsName().equals(optionsName)
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
			}
			if (queryDef instanceof StructuredQueryDefinition) {
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

		if (transaction != null) {
			docParams.add("txid", transaction.getTransactionId());
		}

		String uri = "values";
		if (valDef.getName() != null) {
			uri += "/" + valDef.getName();
		}

		WebResource webResource = makeWebResource(uri, docParams);
		WebResource.Builder builder = makeBuilder(webResource, null, mimetype);
		addTransactionScopedCookies(builder, webResource, transaction);

		final HandleImplementation tempBaseHandle = baseHandle;
		Function<WebResource.Builder, ClientResponse> doGetFunction =
			funcBuilder -> doGet(funcBuilder);
		Function<WebResource.Builder, ClientResponse> doPostFunction =
			funcBuilder -> doPost(null, funcBuilder.type(tempBaseHandle.getMimetype()),
					tempBaseHandle.sendContent(), tempBaseHandle.isResendable());
		ClientResponse response = baseHandle == null ? makeRequest(builder, doGetFunction, null)
				: makeRequest(builder, doPostFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();

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
			String mimetype, Transaction transaction)
			throws ForbiddenUserException, FailedRequestException {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();

		String optionsName = valDef.getOptionsName();
		if (optionsName != null && optionsName.length() > 0) {
			addEncodedParam(docParams, "options", optionsName);
		}

		if (transaction != null) {
			docParams.add("txid", transaction.getTransactionId());
		}

		String uri = "values";

		WebResource webResource = makeWebResource(uri, docParams);
		WebResource.Builder builder = makeBuilder(webResource, null, mimetype);
		addTransactionScopedCookies(builder, webResource, transaction);

		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> funcBuilder
				.get(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();

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
	public <T> T optionsList(Class<T> as, String mimetype, Transaction transaction)
			throws ForbiddenUserException, FailedRequestException {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();

		if (transaction != null) {
			docParams.add("txid", transaction.getTransactionId());
		}

		String uri = "config/query";

		WebResource webResource = getConnection().path(uri)
				.queryParams(docParams);
		WebResource.Builder builder = webResource.accept(mimetype);
		addTransactionScopedCookies(builder, webResource, transaction);

		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> funcBuilder
				.get(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();

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

		WebResource.Builder builder = makeBuilder(type + "/" + key, null, null, mimetype);

		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> funcBuilder
				.get(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();

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
				getConnection().path(type).accept(mimetype) :
				getConnection().path(type).queryParams(requestParams).accept(mimetype);

		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> funcBuilder
				.get(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
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
						getConnection().path(connectPath) :
						getConnection().path(connectPath).queryParams(requestParams);
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
						getConnection().path(connectPath) :
						getConnection().path(connectPath).queryParams(requestParams);
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

			int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
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

		WebResource.Builder builder = getConnection().path(type + "/" + key)
			.getRequestBuilder();

		Function<WebResource.Builder, ClientResponse> doDeleteFunction = funcBuilder -> funcBuilder
				.delete(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doDeleteFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
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

		WebResource.Builder builder = getConnection().path(type).getRequestBuilder();

		Function<WebResource.Builder, ClientResponse> doDeleteFunction = funcBuilder -> funcBuilder
				.delete(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doDeleteFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
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
	public <R extends UrisReadHandle> R uris(RequestLogger reqlog, Transaction transaction,
			QueryDefinition qdef, long start, long pageLength, String forestName, R output)
			throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		if ( forestName != null )        params.add("forest-name", forestName);
		if (start > 1)                   params.add("start",       Long.toString(start));
		if (pageLength >= 1)             params.add("pageLength",  Long.toString(pageLength));
		if (qdef.getDirectory() != null) params.add("directory",   qdef.getDirectory());
		if (qdef.getCollections() != null ) {
			for ( String collection : qdef.getCollections() ) {
				params.add("collection", collection);
			}
		}
		if (qdef.getOptionsName()!= null && qdef.getOptionsName().length() > 0) {
			params.add("options", qdef.getOptionsName());
		}
		if (qdef instanceof RawQueryByExampleDefinition) {
			throw new UnsupportedOperationException("Cannot search with RawQueryByExampleDefinition");
		} else if (qdef instanceof RawQueryDefinition) {
			logger.debug("Raw uris query");

			StructureWriteHandle input = ((RawQueryDefinition) qdef).getHandle();

			return postResource(reqlog, "internal/uris", transaction, params, input, output);
		} else {
			if (qdef instanceof StringQueryDefinition) {
				String text = ((StringQueryDefinition) qdef).getCriteria();
				if (text != null) {
					params.add("q", text);
				}
			}
			if (qdef instanceof StructuredQueryDefinition) {
				String structure = ((StructuredQueryDefinition) qdef).serialize();

				String text = ((StringQueryDefinition) qdef).getCriteria();
				String qtextMessage = text == null ? "" : " and string query \"" + text + "\"";
				logger.debug("Query uris with structured query {}{}", structure, qtextMessage);
				if (structure != null) {
					params.add("structuredQuery", structure);
				}
			} else if (qdef instanceof CombinedQueryDefinition) {
				String structure = ((CombinedQueryDefinition) qdef).serialize();

				logger.debug("Query uris with combined query {}", structure);
				if (structure != null) {
					params.add("structuredQuery", structure);
				}
			} else if (qdef instanceof StringQueryDefinition) {
				String text = ((StringQueryDefinition) qdef).getCriteria();
				logger.debug("Query uris with string query \"{}\"", text);
			} else {
				throw new UnsupportedOperationException("Cannot query uris with " +
						qdef.getClass().getName());
			}
			return getResource(reqlog, "internal/uris", transaction, params, output);
		}
	}

	@Override
	public <R extends AbstractReadHandle> R getResource(RequestLogger reqlog,
			String path, Transaction transaction, RequestParameters params, R output)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if ( params == null ) params = new RequestParameters();
		if ( transaction != null ) params.add("txid", transaction.getTransactionId());
		addPointInTimeQueryParam(params, output);
		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String mimetype = outputBase.getMimetype();
		Class as = outputBase.receiveAs();

		WebResource webResource = makeGetWebResource(path, params, mimetype);
		WebResource.Builder builder = makeBuilder(webResource, null, mimetype);
		addTransactionScopedCookies(builder, webResource, transaction);

		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> doGet(funcBuilder);
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
		checkStatus(response, status, "read", "resource", path,
				ResponseStatus.OK_OR_NO_CONTENT);

		updateDescriptor(outputBase, response.getHeaders());
		if (as != null) {
			outputBase.receiveContent(makeResult(reqlog, "read", "resource",
					response, as));
		} else {
			response.close();
		}

		return output;
	}

	@Override
	public RESTServiceResultIterator getIteratedResource(RequestLogger reqlog,
			String path, Transaction transaction, RequestParameters params, String... mimetypes)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		return getIteratedResourceImpl(JerseyServiceResultIterator.class, reqlog, path, transaction, params, mimetypes);
	}

	private <U extends JerseyResultIterator> U getIteratedResourceImpl(Class<U> clazz, RequestLogger reqlog,
			String path, Transaction transaction, RequestParameters params, String... mimetypes)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if ( params == null ) params = new RequestParameters();
		if (transaction != null) params.add("txid", transaction.getTransactionId());

		WebResource webResource = makeGetWebResource(path, params, null);
		WebResource.Builder builder = makeBuilder(webResource, null, null);
		addTransactionScopedCookies(builder, webResource, transaction);

		MediaType multipartType = Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE);

		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> doGet(funcBuilder.accept(multipartType));
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
		checkStatus(response, status, "read", "resource", path,
				ResponseStatus.OK_OR_NO_CONTENT);

		return makeResults(clazz, reqlog, "read", "resource", response);
	}

	@Override
	public <R extends AbstractReadHandle> R putResource(RequestLogger reqlog,
			String path, Transaction transaction, RequestParameters params, AbstractWriteHandle input,
			R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		if ( params == null ) params = new RequestParameters();
		if ( transaction != null ) params.add("txid", transaction.getTransactionId());
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
		WebResource webResource = makePutWebResource(path, params);
		WebResource.Builder builder = makeBuilder(webResource, inputMimetype, outputMimeType);
		addTransactionScopedCookies(builder, webResource, transaction);

		Consumer<Boolean> resendableConsumer = (resendable) -> {
			if (!isResendable) {
				checkFirstRequest();
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			}
		};

		Function<WebResource.Builder, ClientResponse> doPutFunction = funcBuilder -> doPut(reqlog,
				funcBuilder, inputBase.sendContent(),
				!isResendable);
		ClientResponse response = makeRequest(builder, doPutFunction, resendableConsumer);
		ClientResponse.Status status = response.getClientResponseStatus();

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
			RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
			W[] input, R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		if (input == null || input.length == 0)
			throw new IllegalArgumentException(
					"input not specified for multipart");
		if ( params == null ) params = new RequestParameters();
		if ( transaction != null ) params.add("txid", transaction.getTransactionId());

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

			WebResource webResource = makePutWebResource(path, params);
			WebResource.Builder builder = makeBuilder(webResource, multiPart, outputMimetype);
			addTransactionScopedCookies(builder, webResource, transaction);

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

			int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
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
			String path, Transaction transaction, RequestParameters params,
			AbstractWriteHandle input, R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		return postResource(reqlog, path, transaction, params, input, output, "apply");
	}

	@Override
	public <R extends AbstractReadHandle> R postResource(RequestLogger reqlog,
			String path, Transaction transaction, RequestParameters params,
			AbstractWriteHandle input, R output, String operation) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		if ( params == null ) params = new RequestParameters();
		if ( transaction != null ) params.add("txid", transaction.getTransactionId());

		HandleImplementation inputBase = HandleAccessor.checkHandle(input,
				"write");
		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String inputMimetype = null;
		if(inputBase != null) {
			inputMimetype = inputBase.getMimetype();
			if ( inputMimetype == null &&
					(Format.JSON == inputBase.getFormat() || Format.XML == inputBase.getFormat()) )
			{
				inputMimetype = inputBase.getFormat().getDefaultMimetype();
			}
		}
		String outputMimetype = outputBase == null ? null : outputBase.getMimetype();
		boolean isResendable = inputBase == null ? true : inputBase.isResendable();
		Class as = outputBase == null ? null : outputBase.receiveAs();

		WebResource webResource = makePostWebResource(path, params);
		WebResource.Builder builder = makeBuilder(webResource, inputMimetype, outputMimetype);
		addTransactionScopedCookies(builder, webResource, transaction);

		Consumer<Boolean> resendableConsumer = (resendable) -> {
			if (!isResendable) {
				checkFirstRequest();
				throw new ResourceNotResendableException("Cannot retry request for " + path);
			}
		};
		Object value = inputBase == null ? null :inputBase.sendContent();
		Function<WebResource.Builder, ClientResponse> doPostFunction = funcBuilder -> doPost(reqlog, funcBuilder,
				value, !isResendable);

		ClientResponse response = makeRequest(builder, doPostFunction, resendableConsumer);
		ClientResponse.Status status = response.getClientResponseStatus();
		checkStatus(response, status, operation, "resource", path,
				ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

		if (as != null) {
			outputBase.receiveContent(makeResult(reqlog, operation, "resource",
					response, as));
		} else {
			response.close();
		}

		return output;
	}

	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
			RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
			W[] input, R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		return postResource(reqlog, path, transaction, params, input, null, output);
	}

	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
			RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
			W[] input, Map<String, List<String>>[] headers, R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		if ( params == null ) params = new RequestParameters();
		if ( transaction != null ) params.add("txid", transaction.getTransactionId());

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

			WebResource webResource = makePostWebResource(path, params);
			WebResource.Builder builder = makeBuilder(webResource, multiPart, outputMimetype);
			addTransactionScopedCookies(builder, webResource, transaction);

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

			int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
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
			ServerTransform transform, Transaction transaction, Format defaultFormat)
		throws ForbiddenUserException,  FailedRequestException
	{
		postBulkDocuments(reqlog, writeSet, transform, transaction, defaultFormat, null, null);
	}

	@Override
	public <R extends AbstractReadHandle> R postBulkDocuments(
			RequestLogger reqlog, DocumentWriteSet writeSet,
			ServerTransform transform, Transaction transaction, Format defaultFormat, R output,
			String temporalCollection)
		throws ForbiddenUserException,  FailedRequestException
	{
		List<AbstractWriteHandle> writeHandles = new ArrayList<>();
		List<Map<String, List<String>>> headerList = new ArrayList<>();
		for ( DocumentWriteOperation write: writeSet ) {
			String temporalDocumentURI = write.getTemporalDocumentURI();
			HandleImplementation metadata =
				HandleAccessor.checkHandle(write.getMetadata(), "write");
			HandleImplementation content =
				HandleAccessor.checkHandle(write.getContent(), "write");
			String contentDispositionTemporal = "";
			if (temporalDocumentURI != null) {
				contentDispositionTemporal = "; temporal-document="+temporalDocumentURI;
			}
			if ( write.getOperationType() == 
					DocumentWriteOperation.OperationType.DISABLE_METADATA_DEFAULT )
			{
				MultivaluedMap headers = new MultivaluedMapImpl();
				headers.add(HttpHeaders.CONTENT_TYPE, metadata.getMimetype());
				headers.add("Content-Disposition", "inline; category=metadata"+contentDispositionTemporal);
				headerList.add(headers);
				writeHandles.add(write.getMetadata());
			} else if ( metadata != null ) {
				MultivaluedMap headers = new MultivaluedMapImpl();
				headers.add(HttpHeaders.CONTENT_TYPE, metadata.getMimetype());
				if ( write.getOperationType() == DocumentWriteOperation.OperationType.METADATA_DEFAULT ) {
					headers.add("Content-Disposition", "inline; category=metadata"+contentDispositionTemporal);
				} else {
					headers.add("Content-Disposition",
						ContentDisposition
							.type("attachment")
							.fileName(write.getUri())
							.build().toString() +
						"; category=metadata"+contentDispositionTemporal
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
						.build().toString()+contentDispositionTemporal
				);
				headerList.add(headers);
				writeHandles.add(write.getContent());
			}
		}
		RequestParameters params = new RequestParameters();
		if (transform != null) {
			transform.merge(params);
		}
		if (temporalCollection != null) params.add("temporal-collection", temporalCollection);
		return postResource(
			reqlog,
			"documents",
			transaction,
			params, 
			(AbstractWriteHandle[]) writeHandles.toArray(new AbstractWriteHandle[0]),
			(Map<String, List<String>>[]) headerList.toArray(new HashMap[0]),
			output);
	}

	public class JerseyEvalResultIterator implements EvalResultIterator {
		private JerseyResultIterator iterator;

		JerseyEvalResultIterator(JerseyResultIterator iterator) {
			this.iterator = iterator;
		}

		@Override
		public Iterator<EvalResult> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			if ( iterator == null ) return false;
			return iterator.hasNext();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public EvalResult next() {
			if ( iterator == null ) throw new NoSuchElementException("No results available");
			JerseyResult jerseyResult = iterator.next();
			EvalResult result = new JerseyEvalResult(jerseyResult);
			return result;
		}

		@Override
		public void close() {
			if ( iterator != null ) iterator.close();
		}
	}
	public class JerseyEvalResult implements EvalResult {
		private JerseyResult content;

		public JerseyEvalResult(JerseyResult content) {
			this.content = content;
		}

		@Override
		public Format getFormat() {
			return content.getFormat();
		}

		@Override
		public EvalResult.Type getType() {
			String contentType = content.getHeader("Content-Type");
			String xPrimitive = content.getHeader("X-Primitive");
			if ( contentType != null ) {
				if ( "application/json".equals(contentType) ) {
					if ( "null-node()".equals(xPrimitive) ) {
						return EvalResult.Type.NULL;
					} else {
						return EvalResult.Type.JSON;
					}
				} else if ( "text/json".equals(contentType) ) {
					return EvalResult.Type.JSON;
				} else if ( "application/xml".equals(contentType) ) {
					return EvalResult.Type.XML;
				} else if ( "text/xml".equals(contentType) ) {
					return EvalResult.Type.XML;
				} else if ( "application/x-unknown-content-type".equals(contentType) &&
							"binary()".equals(xPrimitive) )
				{
					return EvalResult.Type.BINARY;
				} else if ( "application/octet-stream".equals(contentType) &&
							"node()".equals(xPrimitive) )
				{
					return EvalResult.Type.BINARY;
				}
			}
			if ( xPrimitive == null ) {
				return EvalResult.Type.OTHER;
			} else if ( "string".equals(xPrimitive) || "untypedAtomic".equals(xPrimitive) ) {
				return EvalResult.Type.STRING;
			} else if ( "boolean".equals(xPrimitive) ) {
				return EvalResult.Type.BOOLEAN;
			} else if ( "attribute()".equals(xPrimitive) ) {
				return EvalResult.Type.ATTRIBUTE;
			} else if ( "comment()".equals(xPrimitive) ) {
				return EvalResult.Type.COMMENT;
			} else if ( "processing-instruction()".equals(xPrimitive) ) {
				return EvalResult.Type.PROCESSINGINSTRUCTION;
			} else if ( "text()".equals(xPrimitive) ) {
				return EvalResult.Type.TEXTNODE;
			} else if ( "binary()".equals(xPrimitive) ) {
				return EvalResult.Type.BINARY;
			} else if ( "duration".equals(xPrimitive) ) {
				return EvalResult.Type.DURATION;
			} else if ( "date".equals(xPrimitive) ) {
				return EvalResult.Type.DATE;
			} else if ( "anyURI".equals(xPrimitive) ) {
				return EvalResult.Type.ANYURI;
			} else if ( "hexBinary".equals(xPrimitive) ) {
				return EvalResult.Type.HEXBINARY;
			} else if ( "base64Binary".equals(xPrimitive) ) {
				return EvalResult.Type.BASE64BINARY;
			} else if ( "dateTime".equals(xPrimitive) ) {
				return EvalResult.Type.DATETIME;
			} else if ( "decimal".equals(xPrimitive) ) {
				return EvalResult.Type.DECIMAL;
			} else if ( "double".equals(xPrimitive) ) {
				return EvalResult.Type.DOUBLE;
			} else if ( "float".equals(xPrimitive) ) {
				return EvalResult.Type.FLOAT;
			} else if ( "gDay".equals(xPrimitive) ) {
				return EvalResult.Type.GDAY;
			} else if ( "gMonth".equals(xPrimitive) ) {
				return EvalResult.Type.GMONTH;
			} else if ( "gMonthDay".equals(xPrimitive) ) {
				return EvalResult.Type.GMONTHDAY;
			} else if ( "gYear".equals(xPrimitive) ) {
				return EvalResult.Type.GYEAR;
			} else if ( "gYearMonth".equals(xPrimitive) ) {
				return EvalResult.Type.GYEARMONTH;
			} else if ( "integer".equals(xPrimitive) ) {
				return EvalResult.Type.INTEGER;
			} else if ( "QName".equals(xPrimitive) ) {
				return EvalResult.Type.QNAME;
			} else if ( "time".equals(xPrimitive) ) {
				return EvalResult.Type.TIME;
			}
			return EvalResult.Type.OTHER;
		}

		@Override
		public <H extends AbstractReadHandle> H get(H handle) {
			if ( getType() == EvalResult.Type.NULL && handle instanceof StringHandle ) {
				return (H) ((StringHandle) handle).with(null);
			} else if ( getType() == EvalResult.Type.NULL && handle instanceof BytesHandle ) {
				return (H) ((BytesHandle) handle).with(null);
			} else {
				return content.getContent(handle);
			}
		}

		@Override
		public <T> T getAs(Class<T> clazz) {
			if ( getType() == EvalResult.Type.NULL ) return null;
			if (clazz == null) throw new IllegalArgumentException("clazz cannot be null");

			ContentHandle<T> readHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(clazz);
			if ( readHandle == null ) return null;
			readHandle = get(readHandle);
			if ( readHandle == null ) return null;
			return readHandle.get();
		}

		@Override
		public String getString() {
			if ( getType() == EvalResult.Type.NULL ) {
				return null;
			} else {
				return content.getEntityAs(String.class);
			}
		}

		@Override
		public Number getNumber() {
			if      ( getType() == EvalResult.Type.DECIMAL ) return new BigDecimal(getString());
			else if ( getType() == EvalResult.Type.DOUBLE )  return Double.valueOf(getString());
			else if ( getType() == EvalResult.Type.FLOAT )   return Float.valueOf(getString());
			// MarkLogic integers can be much larger than Java integers, so we'll use Long instead
			else if ( getType() == EvalResult.Type.INTEGER ) return Long.valueOf(getString());
			else return new BigDecimal(getString());
		}

		@Override
		public Boolean getBoolean() {
			return Boolean.valueOf(getString());
		}

	}

	@Override
	public EvalResultIterator postEvalInvoke(
			RequestLogger reqlog, String code, String modulePath, 
			ServerEvaluationCallImpl.Context context,
			Map<String, Object> variables, EditableNamespaceContext namespaces,
			Transaction transaction)
		throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException
	{
		String formUrlEncodedPayload;
		String path;
		RequestParameters params = new RequestParameters();
		try {
			StringBuffer sb = new StringBuffer();
			if ( context == ServerEvaluationCallImpl.Context.ADHOC_XQUERY ) {
				path = "eval";
				sb.append("xquery=");
				sb.append(URLEncoder.encode(code, "UTF-8"));
			} else if ( context == ServerEvaluationCallImpl.Context.ADHOC_JAVASCRIPT ) {
				path = "eval";
				sb.append("javascript=");
				sb.append(URLEncoder.encode(code, "UTF-8"));
			} else if ( context == ServerEvaluationCallImpl.Context.INVOKE ) {
				path = "invoke";
				sb.append("module=");
				sb.append(URLEncoder.encode(modulePath, "UTF-8"));
			} else {
				throw new IllegalStateException("Invalid eval context: " + context);
			}
			if ( variables != null && variables.size() > 0 ) {
				int i=0;
				for ( String name : variables.keySet() ) {
					String namespace = "";
					String localname = name;
					if ( namespaces != null ) {
						for ( String prefix : namespaces.keySet() ) {
							if ( name != null && prefix != null &&
								 name.startsWith(prefix + ":") )
							{
								localname = name.substring(prefix.length() + 1);
								namespace = namespaces.get(prefix);
							}
						}
					}
					// set the variable namespace
					sb.append("&evn" + i + "=");
					sb.append(URLEncoder.encode(namespace, "UTF-8"));
					// set the variable localname
					sb.append("&evl" + i + "=");
					sb.append(URLEncoder.encode(localname, "UTF-8"));

					String value;
					String type = null;
					Object valueObject = variables.get(name);
					if ( valueObject == null ) {
						value = "null";
						type = "null-node()";
					} else if ( valueObject instanceof JacksonHandle ||
								valueObject instanceof JacksonParserHandle ) {
						JsonNode jsonNode = null;
						if ( valueObject instanceof JacksonHandle ) {
							jsonNode = ((JacksonHandle) valueObject).get();
						} else if ( valueObject instanceof JacksonParserHandle ) {
							jsonNode = ((JacksonParserHandle) valueObject).get().readValueAs(JsonNode.class);
						}
						value = jsonNode.toString();
						type = getJsonType(jsonNode);
					} else if ( valueObject instanceof AbstractWriteHandle ) {
						value = HandleAccessor.contentAsString((AbstractWriteHandle) valueObject);
						HandleImplementation valueBase = HandleAccessor.as((AbstractWriteHandle) valueObject);
						Format format = valueBase.getFormat();
						//TODO: figure out what type should be
						// I see element() and document-node() are two valid types
						if ( format == Format.XML ) {
							type = "document-node()";
						} else if ( format == Format.JSON ) {
							JsonNode jsonNode = new JacksonParserHandle().getMapper().readTree(value);
							type = getJsonType(jsonNode);
						} else if ( format == Format.TEXT ) {
							/* Comment next line until 32608 is resolved
							type = "text()";
							// until then, use the following line */
							type = "xs:untypedAtomic";
						} else if ( format == Format.BINARY ) {
							throw new UnsupportedOperationException("Binary format is not supported for variables");
						} else {
							throw new UnsupportedOperationException("Undefined format is not supported for variables. " +
								"Please set the format on your handle for variable " + name + ".");
						}
					} else if ( valueObject instanceof String ||
								valueObject instanceof Boolean ||
								valueObject instanceof Number ) {
						value = valueObject.toString();
						// when we send type "xs:untypedAtomic" via XDBC, the server attempts to intelligently decide
						// how to cast the type
						type = "xs:untypedAtomic";
					} else {
						throw new IllegalArgumentException("Variable with name=" +
							name + " is of unsupported type" +
							valueObject.getClass() + ". Supported types are String, Boolean, Number, " +
							"or AbstractWriteHandle");
					}

					// set the variable value
					sb.append("&evv" + i + "=");
					sb.append(URLEncoder.encode(value, "UTF-8"));
					// set the variable type
					sb.append("&evt" + i + "=" + type);
					i++;
				}
			}
			formUrlEncodedPayload = sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 is unsupported", e);
		} catch (IOException e) {
			throw new MarkLogicIOException(e);
		}
		StringHandle input = new StringHandle(formUrlEncodedPayload)
			.withMimetype("application/x-www-form-urlencoded");
		return new JerseyEvalResultIterator( postIteratedResourceImpl(DefaultJerseyResultIterator.class,
			reqlog, path, transaction, params, input) );
	}

	private String getJsonType(JsonNode jsonNode) {
		if ( jsonNode instanceof ArrayNode ) {
			return "json:array";
		} else if ( jsonNode instanceof ObjectNode ) {
			return "json:object";
		} else {
			throw new IllegalArgumentException("When using JacksonHandle or " +
					"JacksonParserHandle with ServerEvaluationCall the content must be " +
					"a valid array or object");
		}
	}

	@Override
	public RESTServiceResultIterator postIteratedResource(RequestLogger reqlog,
			String path, Transaction transaction, RequestParameters params, AbstractWriteHandle input,
			String... outputMimetypes) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		return postIteratedResourceImpl(JerseyServiceResultIterator.class,
			reqlog, path, transaction, params, input, outputMimetypes);
	}

	private <U extends JerseyResultIterator> U postIteratedResourceImpl(
			Class<U> clazz, RequestLogger reqlog,
			String path, Transaction transaction, RequestParameters params,
			AbstractWriteHandle input, String... outputMimetypes) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		if ( params == null ) params = new RequestParameters();
		if ( transaction != null ) params.add("txid", transaction.getTransactionId());
		HandleImplementation inputBase = HandleAccessor.checkHandle(input,
				"write");

		String inputMimetype = inputBase.getMimetype();
		boolean isResendable = inputBase.isResendable();

		WebResource webResource = makePostWebResource(path, params);
		WebResource.Builder builder = makeBuilder(webResource, inputMimetype, null);
		addTransactionScopedCookies(builder, webResource, transaction);

		MediaType multipartType = Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE);

		Consumer<Boolean> resendableConsumer = (resendable) -> {
			if (!isResendable) {
				checkFirstRequest();
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			}
		};
		Function<WebResource.Builder, ClientResponse> doPostFunction = funcBuilder -> doPost(reqlog, funcBuilder.accept(multipartType), inputBase.sendContent(), !isResendable);
		ClientResponse response = makeRequest(builder, doPostFunction, resendableConsumer);
		ClientResponse.Status status = response.getClientResponseStatus();

		checkStatus(response, status, "apply", "resource", path,
				ResponseStatus.OK_OR_CREATED_OR_NO_CONTENT);

		return makeResults(clazz, reqlog, "apply", "resource", response);
	}

	@Override
	public <W extends AbstractWriteHandle> RESTServiceResultIterator postIteratedResource(
			RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
			W[] input, String... outputMimetypes)
			throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		return postIteratedResourceImpl(JerseyServiceResultIterator.class,
			reqlog, path, transaction, params, input, outputMimetypes);
	}

	private <W extends AbstractWriteHandle, U extends JerseyResultIterator> U postIteratedResourceImpl(
			Class<U> clazz, RequestLogger reqlog, String path, Transaction transaction,
			RequestParameters params, W[] input, String... outputMimetypes)
			throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		if ( params == null ) params = new RequestParameters();
		if ( transaction != null ) params.add("txid", transaction.getTransactionId());
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

			WebResource webResource = makePostWebResource(path, params);
			WebResource.Builder builder = makeBuilder(
				webResource,
				multiPart,
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));
			addTransactionScopedCookies(builder, webResource, transaction);

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

			int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;
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
			RequestLogger reqlog, String path, Transaction transaction, RequestParameters params,
			R output) throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if ( params == null ) params = new RequestParameters();
		if ( transaction != null ) params.add("txid", transaction.getTransactionId());
		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String outputMimeType = null;
		Class as = null;
		if (outputBase != null) {
			outputMimeType = outputBase.getMimetype();
			as = outputBase.receiveAs();
		}
		WebResource webResource = makeDeleteWebResource(path, params);
		WebResource.Builder builder = makeBuilder(webResource, null, outputMimeType);
		addTransactionScopedCookies(builder, webResource, transaction);

		Function<WebResource.Builder, ClientResponse> doDeleteFunction = funcBuilder -> doDelete(funcBuilder);
		ClientResponse response = makeRequest(builder, doDeleteFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
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

	private WebResource makeGetWebResource(String path,
			RequestParameters params, Object mimetype) {
		if (path == null)
			throw new IllegalArgumentException("Read with null path");

		if (logger.isDebugEnabled())
			logger.debug(String.format("Getting %s as %s", path, mimetype));

		return makeWebResource(path, convertParams(params));
	}

	private ClientResponse doGet(WebResource.Builder builder) {
		ClientResponse response = builder.get(ClientResponse.class);

		if (isFirstRequest())
			setFirstRequest(false);

		return response;
	}

	private WebResource makePutWebResource(String path,
			RequestParameters params) {
		if (path == null)
			throw new IllegalArgumentException("Write with null path");

		if (logger.isDebugEnabled())
			logger.debug("Putting {}", path);

		return makeWebResource(path, convertParams(params));
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

	private ClientResponse doPut(WebResource.Builder builder,
			MultiPart multiPart, boolean hasStreamingPart) {
		if (isFirstRequest() && hasStreamingPart)
			makeFirstRequest(0);

		ClientResponse response = builder.put(ClientResponse.class, multiPart);

		if (isFirstRequest())
			setFirstRequest(false);

		return response;
	}

	private WebResource makePostWebResource(String path, RequestParameters params) {
		if (path == null)
			throw new IllegalArgumentException("Apply with null path");

		if (logger.isDebugEnabled())
			logger.debug("Posting {}", path);

		return makeWebResource(path, convertParams(params));
	}

	private ClientResponse doPost(RequestLogger reqlog,
			WebResource.Builder builder, Object value, boolean isStreaming) {
		if (isFirstRequest() && isStreaming(value))
			makeFirstRequest(0);

		ClientResponse response = null;
		if(value == null) {
			response = builder.post(ClientResponse.class);
		}
		else if (value instanceof OutputStreamSender) {
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

	private ClientResponse doPost(WebResource.Builder builder,
			MultiPart multiPart, boolean hasStreamingPart) {
		if (isFirstRequest() && hasStreamingPart)
			makeFirstRequest(0);

		ClientResponse response = builder.post(ClientResponse.class, multiPart);

		if (isFirstRequest())
			setFirstRequest(false);

		return response;
	}

	private WebResource makeDeleteWebResource(String path, RequestParameters params) {
		if (path == null)
			throw new IllegalArgumentException("Delete with null path");

		if (logger.isDebugEnabled())
			logger.debug("Deleting {}", path);

		return makeWebResource(path, convertParams(params));
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

		List<String> newValues = new ArrayList<>(oldSize);
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

		List<String> newValues = new ArrayList<>(oldSize);
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

	private void addPointInTimeQueryParam(Map<String, List<String>> params, Object outputHandle) {
		HandleImplementation handleBase = HandleAccessor.as(outputHandle);
		if ( params != null && handleBase != null &&
		        handleBase.getPointInTimeQueryTimestamp() != -1 )
		{
			logger.trace("param timestamp=[" + handleBase.getPointInTimeQueryTimestamp() + "]");
			if ( params instanceof RequestParameters ) {
				((RequestParameters)            params).add("timestamp",
					Long.toString(handleBase.getPointInTimeQueryTimestamp()));
			} else if ( params instanceof MultivaluedMap ) {
				((MultivaluedMap) params).add("timestamp",
					Long.toString(handleBase.getPointInTimeQueryTimestamp()));
			}
		}
	}

	private void addTransactionScopedCookies(WebResource.Builder builder, WebResource webResource,
		Transaction transaction)
	{
		if ( transaction != null && transaction.getCookies() != null ) {
			if ( builder == null ) {
				throw new MarkLogicInternalException("no builder available to set cookies");
			}
			if ( webResource == null ) {
				throw new MarkLogicInternalException("no webResource available to get the URI");
			}
			URI uri = webResource.getURI();
			for ( NewCookie cookie : transaction.getCookies() ) {
				// don't forward the cookie if it requires https and we're not using https
				if ( cookie.isSecure() && ! "https".equals(uri.getScheme()) ) {
					continue;
				}
				// don't forward the cookie if it requires a path and we're using a different path
				if ( cookie.getPath() != null ) {
					if ( uri.getPath() == null || ! uri.getPath().startsWith(cookie.getPath()) ) {
						continue;
					}
				}
				// don't forward the cookie if it requires a domain and we're using a different domain
				if ( cookie.getDomain() != null ) {
					if ( uri.getHost() == null || ! uri.getHost().equals(cookie.getDomain()) ) {
						continue;
					}
				}
				// don't forward the cookie if it has 0 for max age
				if ( cookie.getMaxAge() == 0 ) { continue; }
				// don't forward the cookie if it has a max age and we're past the max age
				if ( cookie.getMaxAge() > 0 ) {
					Calendar expiration = (Calendar) ((TransactionImpl) transaction).getCreatedTimestamp().clone();
					expiration.roll(Calendar.SECOND, cookie.getMaxAge());
					if ( System.currentTimeMillis() > expiration.getTimeInMillis() ) {
						continue;
					}
				}
				builder.cookie(cookie);
			}
		}
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

	private WebResource makeWebResource(String path,
			MultivaluedMap<String, String> params) {
		if ( params == null ) params = new MultivaluedMapImpl();
		if ( database != null ) {
			addEncodedParam(params, "database", database);
		}
		return getConnection().path(path).queryParams(params);
	}

	private WebResource.Builder makeBuilder(WebResource webResource,
		Object inputMimetype, Object outputMimetype) {

		WebResource.Builder builder = webResource.getRequestBuilder();

		if (inputMimetype == null) {
		} else if (inputMimetype instanceof String) {
			builder = builder.type((String) inputMimetype);
		} else if (inputMimetype instanceof MediaType) {
			builder = builder.type((MediaType) inputMimetype);
		} else if (inputMimetype instanceof MultiPart) {
			builder = builder.type(Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));
			if (logger.isDebugEnabled())
				logger.debug("Sending multipart for {}", webResource.getURI().getPath());
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

  private WebResource.Builder makeBuilder(String path,
			MultivaluedMap<String, String> params, Object inputMimetype,
			Object outputMimetype) {
		return makeBuilder(makeWebResource(path, params), inputMimetype, outputMimetype);
	}

	private void checkStatus(ClientResponse response,
			ClientResponse.Status status, String operation, String entityType,
			String path, ResponseStatus expected) {
		if (!expected.isExpected(status)) {
			FailedRequest failure = extractErrorFields(response);
			if (status == ClientResponse.Status.NOT_FOUND) {
				throw new ResourceNotFoundException("Could not " + operation
						+ " " + entityType + " at " + path,
						failure);
			}
			if ("RESTAPI-CONTENTNOVERSION".equals(failure.getMessageCode())) {
				throw new FailedRequestException("Content version required to " +
					operation + " " + entityType + " at " + path, failure);
			} else if (status == ClientResponse.Status.FORBIDDEN) {
				throw new ForbiddenUserException("User is not allowed to "
						+ operation + " " + entityType + " at " + path,
						failure);
			}
			throw new FailedRequestException("failed to " + operation + " "
					+ entityType + " at " + path + ": "
					+ status.getReasonPhrase(), failure);
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

		List<BodyPart> partList = (entity == null) ? null : entity.getBodyParts();
		Closeable closeable = new MultipartCloseable(response, entity);
		return makeResults(clazz, reqlog, operation, entityType, partList, response, closeable);
	}

	private <U extends JerseyResultIterator> U makeResults(
			Class<U> clazz, RequestLogger reqlog,
			String operation, String entityType, List<BodyPart> partList, ClientResponse response,
			Closeable closeable) {
		logRequest(reqlog, "%s for %s", operation, entityType);

        if ( response == null ) return null;

		try {
			java.lang.reflect.Constructor<U> constructor = 
				clazz.getConstructor(JerseyServices.class, RequestLogger.class, List.class, Closeable.class);
			JerseyResultIterator result = constructor.newInstance(this, reqlog, partList, closeable);
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

	public class MultipartCloseable implements Closeable {
		private ClientResponse response;
		private MultiPart multiPart;

		public MultipartCloseable(ClientResponse response, MultiPart multiPart) {
			this.response = response;
			this.multiPart = multiPart;
		}
		@Override
		public void close() throws IOException {
			if ( multiPart != null ) multiPart.close();
			if ( response   != null ) response.close();
		}
	}

	public class JerseyResult {
		private RequestLogger reqlog;
		private BodyPart part;
		private boolean extractedHeaders = false;
		private MultivaluedMap<String, String> headers = null;
		private String uri;
		private Format format;
		private String mimetype;
		private long length;

		public JerseyResult(RequestLogger reqlog, BodyPart part) {
			this.reqlog = reqlog;
			this.part = part;
		}

		public <T> T getEntityAs(Class<T> clazz) {
			return part.getEntityAs(clazz);
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

		public <T> T getContentAs(Class<T> clazz) {
			ContentHandle<T> readHandle = DatabaseClientFactory.getHandleRegistry().makeHandle(clazz);
			readHandle = getContent(readHandle);
			if ( readHandle == null ) return null;
			return readHandle.get();
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

		public Map<String,List<String>> getHeaders() {
			extractHeaders();
			return headers;
		}

		public String getHeader(String name) {
			extractHeaders();
			return headers.getFirst(name);
		}

		private void extractHeaders() {
			if (part == null || extractedHeaders)
				return;
			headers = part.getHeaders();
			format = getHeaderFormat(part);
			mimetype = getHeaderMimetype(headers);
			length = getHeaderLength(headers);
			uri = getHeaderUri(part.getContentDisposition());
			extractedHeaders = true;
		}
	}

	public class JerseyServiceResult extends JerseyResult implements RESTServices.RESTServiceResult {
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
        private Closeable closeable;

		public JerseyResultIterator(RequestLogger reqlog,
				List<BodyPart> partList, Class<T> clazz, Closeable closeable) {
            this.clazz = clazz;
            this.reqlog = reqlog;
            if (partList != null && partList.size() > 0) {
                this.size = partList.size();
                this.partQueue = new ConcurrentLinkedQueue<>(
                        partList).iterator();
            } else {
                this.size = 0;
            }
			this.closeable = closeable;
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
            this.size = size;
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
				throw new IllegalStateException("Error instantiating " + clazz.getName(), t);
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
			if ( closeable != null ) {
				try { closeable.close(); } catch (IOException e) {}
			}
		}

		@Override
		protected void finalize() throws Throwable {
			close();
			super.finalize();
		}
	}

	public class JerseyServiceResultIterator 
		extends JerseyResultIterator<JerseyServiceResult>
		implements RESTServiceResultIterator
	{
		public JerseyServiceResultIterator(RequestLogger reqlog,
				List<BodyPart> partList, Closeable closeable) {
			super(reqlog, partList, JerseyServiceResult.class, closeable);
		}
	}

	public class DefaultJerseyResultIterator 
		extends JerseyResultIterator<JerseyResult>
		implements Iterator<JerseyResult>
	{
		public DefaultJerseyResultIterator(RequestLogger reqlog,
				List<BodyPart> partList, Closeable closeable) {
			super(reqlog, partList, JerseyResult.class, closeable);
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

		@Override
		public String getUri() {
			if ( content == null && metadata != null ) {
				return metadata.getUri();
			} else if ( content != null ) {
				return content.getUri();
			} else {
				throw new IllegalStateException("Missing both content and metadata!");
			}
		}

		@Override
		public Format getFormat() {
			return content.getFormat();
		}

		@Override
		public String getMimetype() {
			return content.getMimetype();
		}

		@Override
		public <T extends DocumentMetadataReadHandle> T getMetadata(T metadataHandle) {
			if ( metadata == null ) throw new IllegalStateException(
				"getMetadata called when no metadata is available");
			return metadata.getContent(metadataHandle);
		}

		@Override
		public <T> T getMetadataAs(Class<T> as) {
			if ( as == null ) throw new IllegalStateException(
				"getMetadataAs cannot accept null");
			return metadata.getContentAs(as);
		}

		@Override
		public <T extends AbstractReadHandle> T getContent(T contentHandle) {
			if ( content == null ) throw new IllegalStateException(
				"getContent called when no content is available");
			return content.getContent(contentHandle);
		}

		@Override
		public <T> T getContentAs(Class<T> as) {
			if ( as == null ) throw new IllegalStateException(
				"getContentAs cannot accept null");
			return content.getContentAs(as);
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
		builder = makeBuilder("suggest", params, null, "application/xml");

		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> funcBuilder
				.get(ClientResponse.class);
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
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
		if (candidateRules != null) {
			for (String candidateRule : candidateRules) {
				params.add("rule", candidateRule);
			}
		}
		if (transform != null) {
			transform.merge(params);
		}
		WebResource.Builder builder = null;
		builder = makeBuilder("alert/match", params, "application/xml", mimeType);

		Function<WebResource.Builder, ClientResponse> doPostFunction = funcBuilder -> doPost(null, funcBuilder, baseHandle.sendContent(), false);
		ClientResponse response = makeRequest(builder, doPostFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
		
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

		if (queryDef instanceof StringQueryDefinition) {
			String text = ((StringQueryDefinition) queryDef).getCriteria();
			if (text != null) {
				addEncodedParam(params, "q", text);
			}
		}
		if (queryDef instanceof StructuredQueryDefinition) {
			structure = ((StructuredQueryDefinition) queryDef).serialize();

			if (logger.isDebugEnabled())
				logger.debug("Searching for structure {}",
						structure);

			builder = makeBuilder("alert/match", params, "application/xml", "application/xml");
		} else if (queryDef instanceof RawQueryDefinition) {
			StructureWriteHandle handle = ((RawQueryDefinition) queryDef).getHandle();
			baseHandle = HandleAccessor.checkHandle(handle, "match");

			if (logger.isDebugEnabled())
				logger.debug("Searching for structure {}", structure);

			builder = makeBuilder("alert/match", params, "application/xml", "application/xml");
		} else if (queryDef instanceof StringQueryDefinition) {
			String text = ((StringQueryDefinition) queryDef).getCriteria();
			if (logger.isDebugEnabled())
				logger.debug("Searching for string [{}]", text);

			builder = makeBuilder("alert/match", params, null, "application/xml"); 
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

			if (queryDef instanceof StructuredQueryDefinition) {
				response = builder.post(ClientResponse.class, structure);
			} else if (queryDef instanceof RawQueryDefinition) {
				response = doPost(null, builder, baseHandle.sendContent(), false);
			} else if (queryDef instanceof StringQueryDefinition) {
				response = builder.get(ClientResponse.class);
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
			int retryAfter = (retryAfterRaw != null) ? Integer.parseInt(retryAfterRaw) : -1;

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
		WebResource.Builder builder = makeBuilder("alert/match", params, "application/xml", "application/xml");
		
		Function<WebResource.Builder, ClientResponse> doGetFunction = funcBuilder -> doGet(funcBuilder);
		ClientResponse response = makeRequest(builder, doGetFunction, null);
		ClientResponse.Status status = response.getClientResponseStatus();
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

	private void addGraphUriParam(RequestParameters params, String uri) {
		if ( uri == null || uri.equals(GraphManager.DEFAULT_GRAPH) ) {
			params.add("default", "");
		} else {
			params.add("graph", uri);
		}
	}

	private void addPermsParams(RequestParameters params, GraphPermissions permissions) {
		if ( permissions != null ) {
			for ( Map.Entry<String,Set<Capability>> entry : permissions.entrySet() ) {
				if ( entry.getValue() != null ) {
					for ( Capability capability : entry.getValue() ) {
						params.add("perm:" + entry.getKey(), capability.toString().toLowerCase());
					}
				}
			}
		}
	}

	@Override
	public <R extends AbstractReadHandle> R getGraphUris(RequestLogger reqlog, R output)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		return getResource(reqlog, "graphs", null, null, output);
	}

	@Override
	public <R extends AbstractReadHandle> R readGraph(RequestLogger reqlog, String uri, R output,
		Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		addGraphUriParam(params, uri);
		return getResource(reqlog, "graphs", transaction, params, output);
	}

	@Override
	public void writeGraph(RequestLogger reqlog, String uri,
		AbstractWriteHandle input, GraphPermissions permissions, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		addGraphUriParam(params, uri);
		addPermsParams(params, permissions);
		putResource(reqlog, "graphs", transaction, params, input, null);
	}

	@Override
	public void writeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		putResource(reqlog, "graphs", transaction, params, input, null);
	}

	@Override
	public void mergeGraph(RequestLogger reqlog, String uri,
		AbstractWriteHandle input, GraphPermissions permissions, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		addGraphUriParam(params, uri);
		addPermsParams(params, permissions);
		postResource(reqlog, "graphs", transaction, params, input, null);
	}

	@Override
	public void mergeGraphs(RequestLogger reqlog, AbstractWriteHandle input, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		postResource(reqlog, "graphs", transaction, params, input, null);
	}

	@Override
	public <R extends AbstractReadHandle> R getPermissions(RequestLogger reqlog, String uri,
			R output,Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		addGraphUriParam(params, uri);
		params.add("category", "permissions");
		return getResource(reqlog, "graphs", transaction, params, output);
	}

	@Override
	public void deletePermissions(RequestLogger reqlog, String uri, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		addGraphUriParam(params, uri);
		params.add("category", "permissions");
		deleteResource(reqlog, "graphs", transaction, params, null);
	}

	@Override
	public void writePermissions(RequestLogger reqlog, String uri,
			AbstractWriteHandle permissions, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		addGraphUriParam(params, uri);
		params.add("category", "permissions");
		putResource(reqlog, "graphs", transaction, params, permissions, null);
	}

	@Override
	public void mergePermissions(RequestLogger reqlog, String uri,
			AbstractWriteHandle permissions, Transaction transaction)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		addGraphUriParam(params, uri);
		params.add("category", "permissions");
		postResource(reqlog, "graphs", transaction, params, permissions, null);
	}

	@Override
	public Object deleteGraph(RequestLogger reqlog, String uri, Transaction transaction)
		throws ForbiddenUserException, FailedRequestException
	{
		RequestParameters params = new RequestParameters();
		addGraphUriParam(params, uri);
		return deleteResource(reqlog, "graphs", transaction, params, null);

	}
	
	@Override
	public void deleteGraphs(RequestLogger reqlog, Transaction transaction)
		throws ForbiddenUserException, FailedRequestException
	{
		deleteResource(reqlog, "graphs", transaction, null, null);
	}

	@Override
	public <R extends AbstractReadHandle> R getThings(RequestLogger reqlog, String[] iris, R output)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException
	{
		if ( iris == null ) throw new IllegalArgumentException("iris cannot be null");
		RequestParameters params = new RequestParameters();
		for ( String iri : iris ) {
			params.add("iri", iri);
		}
		return getResource(reqlog, "graphs/things", null, params, output);
	}

	@Override
	public <R extends AbstractReadHandle> R executeSparql(RequestLogger reqlog, 
		SPARQLQueryDefinition qdef, R output, long start, long pageLength,
		Transaction transaction, boolean isUpdate)
	{
		if ( qdef == null )   throw new IllegalArgumentException("qdef cannot be null");
		if ( output == null ) throw new IllegalArgumentException("output cannot be null");
		RequestParameters params = new RequestParameters();
		if (start > 1)             params.add("start",      Long.toString(start));
		if (pageLength >= 0)       params.add("pageLength", Long.toString(pageLength));
		if (qdef.getOptimizeLevel() >= 0) {
			params.add("optimize", Integer.toString(qdef.getOptimizeLevel()));
		}
		if (qdef.getCollections() != null ) {
			for ( String collection : qdef.getCollections() ) {
				params.add("collection", collection);
			}
		}
		addPermsParams(params, qdef.getUpdatePermissions());
		String sparql = qdef.getSparql();
		SPARQLBindings bindings = qdef.getBindings();
		for ( Map.Entry<String,List<SPARQLBinding>> entry : bindings.entrySet() ) {
			String paramName = "bind:" + entry.getKey();
			String typeOrLang = "";
			for ( SPARQLBinding binding : entry.getValue() ) {
				if ( binding.getDatatype() != null ) {
					typeOrLang = ":" + binding.getDatatype();
				} else if ( binding.getLanguageTag() != null ) {
					typeOrLang = "@" + binding.getLanguageTag().toLanguageTag();
				}
				params.add(paramName + typeOrLang, binding.getValue());
			}
		}
		QueryDefinition constrainingQuery = qdef.getConstrainingQueryDefinition();
		StructureWriteHandle input;
		if ( constrainingQuery != null ) {
			if (qdef.getOptionsName()!= null && qdef.getOptionsName().length() > 0) {
				params.add("options", qdef.getOptionsName());
			}
			if ( constrainingQuery instanceof RawCombinedQueryDefinition ) {
				CombinedQueryDefinition combinedQdef = new CombinedQueryBuilderImpl().combine(
					(RawCombinedQueryDefinition) constrainingQuery, null, null, sparql);
				Format format = combinedQdef.getFormat();
				input = new StringHandle(combinedQdef.serialize()).withFormat(format);
			} else if ( constrainingQuery instanceof RawStructuredQueryDefinition ) {
				CombinedQueryDefinition combinedQdef = new CombinedQueryBuilderImpl().combine(
					(RawStructuredQueryDefinition) constrainingQuery, null, null, sparql);
				Format format = combinedQdef.getFormat();
				input = new StringHandle(combinedQdef.serialize()).withFormat(format);
			} else if ( constrainingQuery instanceof StringQueryDefinition ||
						constrainingQuery instanceof StructuredQueryDefinition ) {
				String stringQuery = constrainingQuery instanceof StringQueryDefinition ?
					((StringQueryDefinition) constrainingQuery).getCriteria() : null;
				StructuredQueryDefinition structuredQuery =
					constrainingQuery instanceof StructuredQueryDefinition ?
						(StructuredQueryDefinition) constrainingQuery : null;
				CombinedQueryDefinition combinedQdef = new CombinedQueryBuilderImpl().combine(
					structuredQuery, null, stringQuery, sparql);
				input = new StringHandle(combinedQdef.serialize()).withMimetype("application/xml");
			} else {
			    throw new IllegalArgumentException(
			        "Constraining query must be of type SPARQLConstrainingQueryDefinition");
			}
		} else {
			String mimetype = isUpdate ? "application/sparql-update" : "application/sparql-query";
			input = new StringHandle(sparql).withMimetype(mimetype);
		}
		if (qdef.getBaseUri() != null) {
			params.add("base", qdef.getBaseUri());
		}
		if (qdef.getDefaultGraphUris() != null) {
		    for (String defaultGraphUri : qdef.getDefaultGraphUris()) {
		        params.add("default-graph-uri", defaultGraphUri);
		    }
		}
		if (qdef.getNamedGraphUris() != null) {
			for (String namedGraphUri : qdef.getNamedGraphUris()) {
				params.add("named-graph-uri", namedGraphUri);
			}
		}
		if (qdef.getUsingGraphUris() != null) {
			for (String usingGraphUri : qdef.getUsingGraphUris()) {
				params.add("using-graph-uri", usingGraphUri);
			}
		}
		if (qdef.getUsingNamedGraphUris() != null) {
			for (String usingNamedGraphUri : qdef.getUsingNamedGraphUris()) {
				params.add("using-named-graph-uri", usingNamedGraphUri);
			}
		}
		
		// rulesets
		if (qdef.getRulesets() != null) {
		    for (SPARQLRuleset ruleset : qdef.getRulesets()) {
		        params.add("ruleset", ruleset.getName());
		    }
		}
		if (qdef.getIncludeDefaultRulesets() != null) {
			params.add("default-rulesets", qdef.getIncludeDefaultRulesets() ? "include" : "exclude");
		}

		return postResource(reqlog, "/graphs/sparql", transaction, params, input, output);
	}

	private String getTransactionId(Transaction transaction) {
		if ( transaction == null ) return null;
		return transaction.getTransactionId();
	}

}
