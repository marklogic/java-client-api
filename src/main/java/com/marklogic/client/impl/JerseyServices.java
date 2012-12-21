/*
 * Copyright 2012 MarkLogic Corporation
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ResourceNotResendableException;
import com.marklogic.client.document.ContentDescriptor;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager.Metadata;
import com.marklogic.client.extensions.ResourceServices.ServiceResult;
import com.marklogic.client.extensions.ResourceServices.ServiceResultIterator;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.OutputStreamSender;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataReadHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.ElementLocator;
import com.marklogic.client.query.KeyLocator;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager.QueryView;
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
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.Boundary;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class JerseyServices implements RESTServices {
	static final private Logger logger = LoggerFactory
			.getLogger(JerseyServices.class);
	static final String ERROR_NS = "http://marklogic.com/rest-api";

	protected class HostnameVerifierAdapter extends AbstractVerifier {
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

	private ApacheHttpClient4 client;
	private WebResource connection;

	private int maxRetries = 64;
	private int delayMillis = 125;

	private boolean isFirstRequest = false;

	public JerseyServices() {
	}

	private FailedRequest extractErrorFields(ClientResponse response) {
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
		if (verifier == null)
			;
		else if (verifier == SSLHostnameVerifier.ANY)
			x509Verifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		else if (verifier == SSLHostnameVerifier.COMMON)
			x509Verifier = SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
		else if (verifier == SSLHostnameVerifier.STRICT)
			x509Verifier = SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
		else if (context != null && verifier != null)
			x509Verifier = new HostnameVerifierAdapter(verifier);
		else if (context != null)
			x509Verifier = SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
		else if (verifier != null)
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

		// TODO: integrated control of HTTP Client and Jersey Client logging
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http",
				"warn");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http.wire",
				"warn");

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

		int maxConnections = 100;

		/*
		 * 4.2 PoolingClientConnectionManager connMgr = new
		 * PoolingClientConnectionManager(schemeRegistry);
		 * connMgr.setMaxTotal(maxConnections);
		 * connMgr.setDefaultMaxPerRoute(maxConnections);
		 * connMgr.setMaxPerRoute( new HttpRoute(new HttpHost(baseUri)),
		 * maxConnections );
		 */
		// start 4.1
		ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(
				schemeRegistry);
		connMgr.setDefaultMaxPerRoute(maxConnections);
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
		// long-term alternative to isFirstRequest
		// httpParams.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE,
		// true);
		// httpParams.setIntParameter( CoreProtocolPNames.WAIT_FOR_CONTINUE,
		// 1000);

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
			isFirstRequest = false;
		} else if (authenType == Authentication.BASIC) {
			isFirstRequest = false;

			client.addFilter(new HTTPBasicAuthFilter(user, password));
		} else if (authenType == Authentication.DIGEST) {
			isFirstRequest = true;

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
	public void release() {
		if (client == null)
			return;

		if (logger.isDebugEnabled())
			logger.debug("Releasing connection");

		connection = null;
		client.destroy();
		client = null;
	}

	private void makeFirstRequest() {
		connection.path("ping").head().close();
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

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.delete(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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

		if (extraParams != null && extraParams.containsKey("range"))
			builder = builder.header("range", extraParams.get("range").get(0));

		builder = addVersionHeader(desc, builder, "If-None-Match");

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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
		Object entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		handleBase.receiveContent((reqlog != null) ? reqlog.copyContent(entity)
				: entity);

		return true;
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

		WebResource.Builder builder = makeDocumentResource(docParams).accept(
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		builder = addVersionHeader(desc, builder, "If-None-Match");

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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

		MultiPart entity = response.getEntity(MultiPart.class);
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
		if (uri == null)
			throw new IllegalArgumentException(
					"Existence check for document identifier without uri");

		if (logger.isDebugEnabled())
			logger.debug("Requesting head for {} in transaction {}", uri,
					transactionId);

		WebResource webResource = makeDocumentResource(makeDocumentParams(uri,
				null, transactionId, null));

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = webResource.head();
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		MultivaluedMap<String, String> responseHeaders = response.getHeaders();

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

		response.close();
		logRequest(reqlog, "checked %s document from %s transaction", uri,
				(transactionId != null) ? transactionId : "no");

		DocumentDescriptorImpl desc = new DocumentDescriptorImpl(uri, false);

		updateVersion(desc, responseHeaders);
		updateDescriptor(desc, responseHeaders);

		return desc;
	}

	@Override
	public void putDocument(RequestLogger reqlog, DocumentDescriptor desc,
			String transactionId, Set<Metadata> categories,
			RequestParameters extraParams,
			DocumentMetadataWriteHandle metadataHandle,
			AbstractWriteHandle contentHandle)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
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
			putDocumentImpl(reqlog, desc, transactionId, categories,
					extraParams, metadataMimetype, metadataHandle,
					contentMimetype, contentHandle);
		} else if (metadataBase != null) {
			putDocumentImpl(reqlog, desc, transactionId, categories,
					extraParams, metadataMimetype, metadataHandle);
		} else if (contentBase != null) {
			putDocumentImpl(reqlog, desc, transactionId, null, extraParams,
					contentMimetype, contentHandle);
		}
	}

	private void putDocumentImpl(RequestLogger reqlog, DocumentDescriptor desc,
			String transactionId, Set<Metadata> categories,
			RequestParameters extraParams, String mimetype,
			AbstractWriteHandle handle) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document write for document identifier without uri");

		HandleImplementation handleBase = HandleAccessor.as(handle);

		if (logger.isDebugEnabled())
			logger.debug("Putting {} in transaction {}", uri, transactionId);

		logRequest(
				reqlog,
				"writing %s document from %s transaction with %s mime type and %s metadata categories",
				uri, (transactionId != null) ? transactionId : "no",
				(mimetype != null) ? mimetype : "no",
				stringJoin(categories, ", ", "no"));

		WebResource webResource = makeDocumentResource(makeDocumentParams(uri,
				categories, transactionId, extraParams));
		WebResource.Builder builder = webResource
				.type((mimetype != null) ? mimetype : MediaType.WILDCARD);

		builder = addVersionHeader(desc, builder, "If-Match");

		boolean isResendable = handleBase.isResendable();

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			Object value = handleBase.sendContent();
			if (value == null)
				throw new IllegalArgumentException(
						"Document write with null value for " + uri);

			if (isFirstRequest && isStreaming(value))
				makeFirstRequest();

			if (value instanceof OutputStreamSender) {
				response = builder.put(ClientResponse.class,
						new StreamingOutputImpl((OutputStreamSender) value,
								reqlog));
			} else {
				if (reqlog != null)
					response = builder.put(ClientResponse.class,
							reqlog.copyContent(value));
				else
					response = builder.put(ClientResponse.class, value);
			}

			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			else if (!isResendable)
				throw new ResourceNotResendableException(
						"Cannot retry request for " + uri);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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
		if (status != ClientResponse.Status.CREATED
				&& status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("write failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));

		response.close();
	}

	private void putDocumentImpl(RequestLogger reqlog, DocumentDescriptor desc,
			String transactionId, Set<Metadata> categories,
			RequestParameters extraParams, String metadataMimetype,
			DocumentMetadataWriteHandle metadataHandle, String contentMimetype,
			AbstractWriteHandle contentHandle)
			throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document write for document identifier without uri");

		if (logger.isDebugEnabled())
			logger.debug("Putting multipart for {} in transaction {}", uri,
					transactionId);

		logRequest(
				reqlog,
				"writing %s document from %s transaction with %s metadata categories and content",
				uri, (transactionId != null) ? transactionId : "no",
				stringJoin(categories, ", ", "no"));

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transactionId, extraParams, true);

		WebResource.Builder builder = makeDocumentResource(docParams).type(
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));
		builder = addVersionHeader(desc, builder, "If-Match");

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			MultiPart multiPart = new MultiPart();
			boolean hasStreamingPart = addParts(multiPart, reqlog,
					new String[] { metadataMimetype, contentMimetype },
					new AbstractWriteHandle[] { metadataHandle, contentHandle });

			if (isFirstRequest)
				makeFirstRequest();

			response = builder.put(ClientResponse.class, multiPart);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			else if (hasStreamingPart)
				throw new ResourceNotResendableException(
						"Cannot retry request for " + uri);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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
				&& status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("write failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));

		response.close();
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
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = resource.post(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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
					"transaction open produced invalid location " + location);

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

		WebResource builder = connection.path("transactions/" + transactionId)
				.queryParams(transParams);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.post(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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
		if (transactionId != null)
			docParams.add("txid", transactionId);
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

	private void updateMimetype(ContentDescriptor descriptor,
			MultivaluedMap<String, String> headers) {
		updateMimetype(descriptor, getHeaderMimetype(headers));
	}

	private void updateMimetype(ContentDescriptor descriptor, String mimetype) {
		if (mimetype != null) {
			descriptor.setMimetype(mimetype);
		}
	}

	private String getHeaderMimetype(MultivaluedMap<String, String> headers) {
		if (headers.containsKey("Content-Type")) {
			List<String> values = headers.get("Content-Type");
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
		if (headers.containsKey("Content-Length")) {
			List<String> values = headers.get("Content-Length");
			if (values != null) {
				return Long.valueOf(values.get(0));
			}
		}
		return ContentDescriptor.UNKNOWN_LENGTH;
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
	public <T> T search(Class<T> as, QueryDefinition queryDef, String mimetype,
			long start, long len, QueryView view, String transactionId)
			throws ForbiddenUserException, FailedRequestException {
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

		if (queryDef.getDirectory() != null) {
			addEncodedParam(params, "directory", queryDef.getDirectory());
		}

		addEncodedParam(params, "collection", queryDef.getCollections());

		if (transactionId != null) {
			params.add("txid", transactionId);
		}

		String optionsName = queryDef.getOptionsName();
		if (optionsName != null && optionsName.length() > 0) {
			addEncodedParam(params, "options", optionsName);
		}

		WebResource.Builder builder = null;
		String structure = null;
		if (queryDef instanceof StringQueryDefinition) {
			String text = ((StringQueryDefinition) queryDef).getCriteria();
			if (logger.isDebugEnabled())
				logger.debug("Searching for {} in transaction {}", text,
						transactionId);

			if (text != null) {
				addEncodedParam(params, "q", text);
			}

			builder = connection.path("search").queryParams(params)
					.accept(mimetype);
		} else if (queryDef instanceof KeyValueQueryDefinition) {
			if (logger.isDebugEnabled())
				logger.debug("Searching for keys/values in transaction {}",
						transactionId);

			Map<ValueLocator, String> pairs = ((KeyValueQueryDefinition) queryDef);
			for (ValueLocator loc : pairs.keySet()) {
				if (loc instanceof KeyLocator) {
					addEncodedParam(params, "key", ((KeyLocator) loc).getKey());
				} else {
					ElementLocator eloc = (ElementLocator) loc;
					params.add("element", eloc.getElement().toString());
					if (eloc.getAttribute() != null) {
						params.add("attribute", eloc.getAttribute().toString());
					}
				}
				addEncodedParam(params, "value", pairs.get(loc));
			}

			builder = connection.path("keyvalue").queryParams(params)
					.accept(mimetype);
		} else if (queryDef instanceof StructuredQueryDefinition) {
			structure = ((StructuredQueryDefinition) queryDef).serialize();

			if (logger.isDebugEnabled())
				logger.debug("Searching for structure {} in transaction {}",
						structure, transactionId);

			builder = connection.path("search").queryParams(params)
					.type("application/xml").accept(mimetype);
		} else if (queryDef instanceof DeleteQueryDefinition) {
			if (logger.isDebugEnabled())
				logger.debug("Searching for deletes in transaction {}",
						transactionId);

			builder = connection.path("search").queryParams(params)
					.accept(mimetype);
		} else {
			throw new UnsupportedOperationException("Cannot search with "
					+ queryDef.getClass().getName());
		}

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			if (queryDef instanceof StringQueryDefinition) {
				response = builder.get(ClientResponse.class);
			} else if (queryDef instanceof KeyValueQueryDefinition) {
				response = builder.get(ClientResponse.class);
			} else if (queryDef instanceof StructuredQueryDefinition) {
				response = builder.post(ClientResponse.class, structure);
			} else if (queryDef instanceof DeleteQueryDefinition) {
				response = builder.get(ClientResponse.class);
			} else {
				throw new UnsupportedOperationException("Cannot search with "
						+ queryDef.getClass().getName());
			}

			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to search",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("search failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return entity;
	}

	@Override
	public void deleteSearch(DeleteQueryDefinition queryDef,
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

		WebResource builder = connection.path("search").queryParams(params);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.delete(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to delete",
					extractErrorFields(response));
		}

		if (status != ClientResponse.Status.NO_CONTENT) {
			throw new FailedRequestException("delete failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}
	}

	@Override
	public <T> T values(Class<T> as, ValuesDefinition valDef, String mimetype,
			String transactionId) throws ForbiddenUserException,
			FailedRequestException {
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
			} else {
				if (logger.isWarnEnabled())
					logger.warn("unsupported query definition: "
							+ queryDef.getClass().getName());
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

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to search",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("search failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
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

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to search",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("search failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
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

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException("User is not allowed to search",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("search failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
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
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}

	@Override
	public <T> T getValues(RequestLogger reqlog, String type, String mimetype,
			Class<T> as) throws ForbiddenUserException, FailedRequestException {
		if (logger.isDebugEnabled())
			logger.debug("Getting {}", type);

		WebResource.Builder builder = connection.path(type).accept(mimetype);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.get(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
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

		HandleImplementation handle = (value instanceof HandleImplementation) ? (HandleImplementation) value
				: null;

		MultivaluedMap<String, String> requestParams = convertParams(extraParams);

		String connectPath = null;
		WebResource.Builder builder = null;

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
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

			boolean isStreaming = (isFirstRequest || handle == null) ? isStreaming(sentValue)
					: false;

			boolean isResendable = (handle == null) ? !isStreaming : handle
					.isResendable();

			if ("put".equals(method)) {
				if (isFirstRequest && isStreaming)
					makeFirstRequest();

				if (builder == null) {
					connectPath = (key != null) ? type + "/" + key : type;
					WebResource resource = (requestParams == null) ? connection
							.path(connectPath) : connection.path(connectPath)
							.queryParams(requestParams);
					builder = resource.type(mimetype);
				}

				response = builder.put(ClientResponse.class, sentValue);
			} else if ("post".equals(method)) {
				if (isFirstRequest && isStreaming)
					makeFirstRequest();

				if (builder == null) {
					connectPath = type;
					WebResource resource = (requestParams == null) ? connection
							.path(connectPath) : connection.path(connectPath)
							.queryParams(requestParams);
					builder = resource.type(mimetype);
				}

				response = builder.post(ClientResponse.class, sentValue);
			} else {
				throw new MarkLogicInternalException("unknown method type "
						+ method);
			}

			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			else if (!isResendable)
				throw new ResourceNotResendableException(
						"Cannot retry request for " + connectPath);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.delete(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.delete(ClientResponse.class);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

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
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = doGet(builder);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		checkStatus(response, status, "read", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.NO_CONTENT);

		if (as != null)
			outputBase.receiveContent(makeResult(reqlog, "read", "resource",
					response, as));

		return output;
	}

	@Override
	public ServiceResultIterator getIteratedResource(RequestLogger reqlog,
			String path, RequestParameters params, String... mimetypes)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		WebResource.Builder builder = makeGetBuilder(path, params,
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = doGet(builder);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		checkStatus(
				response,
				status,
				"read",
				"resource",
				path,
				(response.hasEntity() || (mimetypes != null && mimetypes.length > 0)) ? ResponseStatus.OK
						: ResponseStatus.NO_CONTENT);

		return makeResults(reqlog, "read", "resource", response);
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
		String outputMimetype = outputBase.getMimetype();
		boolean isResendable = inputBase.isResendable();
		Class as = outputBase.receiveAs();

		WebResource.Builder builder = makePutBuilder(path, params,
				inputMimetype, outputMimetype);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = doPut(reqlog, builder, inputBase.sendContent(),
					!isResendable);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			else if (!isResendable)
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		checkStatus(response, status, "write", "resource", path,
				(as != null) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		if (as != null)
			outputBase.receiveContent(makeResult(reqlog, "write", "resource",
					response, as));

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
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			MultiPart multiPart = new MultiPart();
			boolean hasStreamingPart = addParts(multiPart, reqlog, input);

			WebResource.Builder builder = makePutBuilder(path, params,
					multiPart, outputMimetype);

			response = doPut(builder, multiPart, hasStreamingPart);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			else if (hasStreamingPart)
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		checkStatus(response, status, "write", "resource", path,
				(as != null) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		if (as != null)
			outputBase.receiveContent(makeResult(reqlog, "write", "resource",
					response, as));

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
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = doPost(reqlog, builder, inputBase.sendContent(),
					!isResendable);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			else if (!isResendable)
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		checkStatus(response, status, "apply", "resource", path,
				(as != null) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		if (as != null)
			outputBase.receiveContent(makeResult(reqlog, "apply", "resource",
					response, as));

		return output;
	}

	@Override
	public <R extends AbstractReadHandle, W extends AbstractWriteHandle> R postResource(
			RequestLogger reqlog, String path, RequestParameters params,
			W[] input, R output) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String outputMimetype = outputBase.getMimetype();
		Class as = outputBase.receiveAs();

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			MultiPart multiPart = new MultiPart();
			boolean hasStreamingPart = addParts(multiPart, reqlog, input);

			WebResource.Builder builder = makePostBuilder(path, params,
					multiPart, outputMimetype);

			response = doPost(builder, multiPart, hasStreamingPart);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			else if (hasStreamingPart)
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		checkStatus(response, status, "apply", "resource", path,
				(as != null) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		if (as != null)
			outputBase.receiveContent(makeResult(reqlog, "apply", "resource",
					response, as));

		return output;
	}

	@Override
	public ServiceResultIterator postIteratedResource(RequestLogger reqlog,
			String path, RequestParameters params, AbstractWriteHandle input,
			String... outputMimetypes) throws ResourceNotFoundException,
			ResourceNotResendableException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation inputBase = HandleAccessor.checkHandle(input,
				"write");

		String inputMimetype = inputBase.getMimetype();
		boolean isResendable = inputBase.isResendable();

		WebResource.Builder builder = makePostBuilder(path, params,
				inputMimetype,
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			Object value = inputBase.sendContent();

			response = doPost(reqlog, builder, value, !isResendable);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			else if (!isResendable)
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		checkStatus(
				response,
				status,
				"apply",
				"resource",
				path,
				(response.hasEntity() || (outputMimetypes != null && outputMimetypes.length > 0)) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResults(reqlog, "apply", "resource", response);
	}

	@Override
	public <W extends AbstractWriteHandle> ServiceResultIterator postIteratedResource(
			RequestLogger reqlog, String path, RequestParameters params,
			W[] input, String... outputMimetypes)
			throws ResourceNotFoundException, ResourceNotResendableException,
			ForbiddenUserException, FailedRequestException {
		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			MultiPart multiPart = new MultiPart();
			boolean hasStreamingPart = addParts(multiPart, reqlog, input);

			WebResource.Builder builder = makePostBuilder(
					path,
					params,
					multiPart,
					Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

			response = doPost(builder, multiPart, hasStreamingPart);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			else if (hasStreamingPart)
				throw new ResourceNotResendableException(
						"Cannot retry request for " + path);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		checkStatus(
				response,
				status,
				"apply",
				"resource",
				path,
				(response.hasEntity() || (outputMimetypes != null && outputMimetypes.length > 0)) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResults(reqlog, "apply", "resource", response);
	}

	@Override
	public <R extends AbstractReadHandle> R deleteResource(
			RequestLogger reqlog, String path, RequestParameters params,
			R output) throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		HandleImplementation outputBase = HandleAccessor.checkHandle(output,
				"read");

		String outputMimetype = outputBase.getMimetype();
		Class as = outputBase.receiveAs();

		WebResource.Builder builder = makeDeleteBuilder(reqlog, path, params,
				outputMimetype);

		ClientResponse response = null;
		ClientResponse.Status status = null;
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = doDelete(builder);
			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}
		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		checkStatus(response, status, "delete", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.NO_CONTENT);

		if (as != null)
			outputBase.receiveContent(makeResult(reqlog, "delete", "resource",
					response, as));

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

		if (isFirstRequest)
			isFirstRequest = false;

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

		if (isFirstRequest && isStreaming(value))
			makeFirstRequest();

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

		if (isFirstRequest)
			isFirstRequest = false;

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
		if (isFirstRequest)
			makeFirstRequest();

		ClientResponse response = builder.put(ClientResponse.class, multiPart);

		if (isFirstRequest)
			isFirstRequest = false;

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
		if (isFirstRequest && isStreaming(value))
			makeFirstRequest();

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

		if (isFirstRequest)
			isFirstRequest = false;

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
		if (isFirstRequest)
			makeFirstRequest();

		ClientResponse response = builder.post(ClientResponse.class, multiPart);

		if (isFirstRequest)
			isFirstRequest = false;

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

		if (isFirstRequest)
			isFirstRequest = false;

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
		return addParts(multiPart, reqlog, null, input);
	}

	private <W extends AbstractWriteHandle> boolean addParts(
			MultiPart multiPart, RequestLogger reqlog, String[] mimetypes,
			W[] input) {
		if (mimetypes != null && mimetypes.length != input.length)
			throw new IllegalArgumentException(
					"Mismatch between mimetypes and input");

		multiPart.setMediaType(new MediaType("multipart", "mixed"));

		boolean hasStreamingPart = false;
		for (int i = 0; i < input.length; i++) {
			AbstractWriteHandle handle = input[i];
			HandleImplementation handleBase = HandleAccessor.checkHandle(
					handle, "write");
			Object value = handleBase.sendContent();
			String inputMimetype = (mimetypes != null) ? mimetypes[i]
					: handleBase.getMimetype();

			if (!hasStreamingPart)
				hasStreamingPart = !handleBase.isResendable();

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
		if (as == null)
			return null;

		logRequest(reqlog, "%s for %s", operation, entityType);

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}

	private ServiceResultIterator makeResults(RequestLogger reqlog,
			String operation, String entityType, ClientResponse response) {
		logRequest(reqlog, "%s for %s", operation, entityType);

		MultiPart entity = response.getEntity(MultiPart.class);
		if (entity == null)
			return null;

		List<BodyPart> partList = entity.getBodyParts();
		if (partList == null || partList.size() == 0) {
			response.close();
			return null;
		}

		return new JerseyResultIterator(reqlog, response, partList);
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

	public class JerseyResult implements ServiceResult {
		private RequestLogger reqlog;
		private BodyPart part;
		private boolean extractedHeaders = false;
		private Format format;
		private String mimetype;
		private long length;

		public JerseyResult(RequestLogger reqlog, BodyPart part) {
			super();
			this.reqlog = reqlog;
			this.part = part;
		}

		@Override
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

		@Override
		public Format getFormat() {
			extractHeaders();
			return format;
		}

		@Override
		public String getMimetype() {
			extractHeaders();
			return mimetype;
		}

		@Override
		public long getLength() {
			extractHeaders();
			return length;
		}

		private void extractHeaders() {
			if (part == null || extractedHeaders)
				return;
			MultivaluedMap<String, String> headers = part.getHeaders();
			format = getHeaderFormat(headers);
			mimetype = getHeaderMimetype(headers);
			length = getHeaderLength(headers);
			extractedHeaders = true;
		}
	}

	public class JerseyResultIterator implements ServiceResultIterator {
		private RequestLogger reqlog;
		private ClientResponse response;
		private Iterator<BodyPart> partQueue;

		public JerseyResultIterator(RequestLogger reqlog,
				ClientResponse response, List<BodyPart> partList) {
			super();
			if (response != null) {
				if (partList != null && partList.size() > 0) {
					this.reqlog = reqlog;
					this.response = response;
					this.partQueue = new ConcurrentLinkedQueue<BodyPart>(
							partList).iterator();
				} else {
					response.close();
				}
			}
		}

		@Override
		public boolean hasNext() {
			if (partQueue == null)
				return false;
			boolean hasNext = partQueue.hasNext();
			if (!partQueue.hasNext())
				close();
			return hasNext;
		}

		@Override
		public ServiceResult next() {
			if (partQueue == null)
				return null;

			ServiceResult result = new JerseyResult(reqlog, partQueue.next());
			if (!partQueue.hasNext())
				close();

			return result;
		}

		@Override
		public void remove() {
			if (partQueue == null)
				return;
			partQueue.remove();
			if (!partQueue.hasNext())
				close();
		}

		@Override
		public void close() {
			if (response != null) {
				response.close();
				response = null;
			}
			reqlog = null;
		}

		@Override
		protected void finalize() throws Throwable {
			close();
			partQueue = null;
			super.finalize();
		}
	}

	@Override
	public Object getClientImplementation() {
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
			params.add("pqtext", suggestCriteria);
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
		int retry = 0;
		for (; retry < maxRetries; retry++) {
			response = builder.get(ClientResponse.class);

			status = response.getClientResponseStatus();

			if (isFirstRequest)
				isFirstRequest = false;

			if (status != ClientResponse.Status.SERVICE_UNAVAILABLE
					|| !"1".equals(response.getHeaders()
							.getFirst("Retry-After")))
				break;
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
			}
		}

		if (retry >= maxRetries)
			throw new FailedRequestException(
					"Service unavailable and retries exhausted");

		if (status == ClientResponse.Status.FORBIDDEN) {
			throw new ForbiddenUserException(
					"User is not allowed to get suggestions",
					extractErrorFields(response));
		}
		if (status != ClientResponse.Status.OK) {
			throw new FailedRequestException("Suggest call failed: "
					+ status.getReasonPhrase(), extractErrorFields(response));
		}

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return entity;

	}
}
