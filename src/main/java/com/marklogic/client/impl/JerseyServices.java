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

import org.apache.http.HttpHost;
import org.apache.http.auth.params.AuthPNames;
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
//import org.apache.http.impl.conn.PoolingClientConnectionManager;    // 4.2
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;   // 4.1
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
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.Boundary;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;

@SuppressWarnings({"unchecked", "rawtypes"})
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
	private boolean isFirstRequest = true;

    private boolean headFirst = false;

	public JerseyServices() {
        String head = System.getProperty("com.marklogic.client.headfirst");
        headFirst = ("true".equals(head) || "1".equals(head));
	}

	private FailedRequest extractErrorFields(ClientResponse response) {
		InputStream is = response.getEntityInputStream();
		try {
			FailedRequest handler = new FailedRequest(response.getStatus(), is);
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
		if (logger.isInfoEnabled())
			logger.info("Connecting to {} at {} as {}", new Object[] { host,
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

		String baseUri = ((context == null) ? "http" : "https") + "://" + host + ":" + port + "/v1/";

		// TODO: integrated control of HTTP Client and Jersey Client logging
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.httpclient.wire.header",
				"warn");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
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

        /* 4.2
		PoolingClientConnectionManager connMgr =
			new PoolingClientConnectionManager(schemeRegistry);
		connMgr.setMaxTotal(maxConnections);
		connMgr.setDefaultMaxPerRoute(maxConnections);
		connMgr.setMaxPerRoute(
				new HttpRoute(new HttpHost(baseUri)), maxConnections
				);
	    */
        // start 4.1
        ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(schemeRegistry);
        connMgr.setDefaultMaxPerRoute(100);
        // end 4.1

//		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//		credentialsProvider.setCredentials(new AuthScope(host, port),
//				new UsernamePasswordCredentials(user, password));

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

		// note that setting PROPERTY_FOLLOW_REDIRECTS below doesn't seem to
		// work
		httpParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

		DefaultApacheHttpClient4Config config = new DefaultApacheHttpClient4Config();
		Map<String, Object> configProps = config.getProperties();
		configProps.put(
				ApacheHttpClient4Config.PROPERTY_PREEMPTIVE_BASIC_AUTHENTICATION,
				false);
		configProps.put(ApacheHttpClient4Config.PROPERTY_CONNECTION_MANAGER,
				connMgr);
		configProps.put(ApacheHttpClient4Config.PROPERTY_FOLLOW_REDIRECTS,
				false);
		// configProps.put(ApacheHttpClient4Config.PROPERTY_CREDENTIALS_PROVIDER,
		// credentialsProvider);
		configProps.put(ApacheHttpClient4Config.PROPERTY_HTTP_PARAMS,
				httpParams);
		// configProps.put(ApacheHttpClient4Config.PROPERTY_CHUNKED_ENCODING_SIZE,
		// 0);

		client = ApacheHttpClient4.create(config);

		// System.setProperty("javax.net.debug", "all"); // all or ssl

		if (authenType != null) {
			if (authenType == Authentication.BASIC)
				client.addFilter(new HTTPBasicAuthFilter(user, password));
			else if (authenType == Authentication.DIGEST)
				client.addFilter(new HTTPDigestAuthFilter(user, password));
			else
				throw new MarkLogicInternalException(
					"Internal error - unknown authentication type: "
							+ authenType.name());
		}

		connection = client.resource(baseUri);
	}

	@Override
	public void release() {
		if (client == null)
			return;

		if (logger.isInfoEnabled())
			logger.info("Releasing connection");

		connection = null;
		// client.getClientHandler().getHttpClient().getConnectionManager().shutdown();
		client.destroy();
		client = null;

		isFirstRequest = true;
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

		if (logger.isInfoEnabled())
			logger.info("Deleting {} in transaction {}", uri, transactionId);

		WebResource webResource = makeDocumentResource(makeDocumentParams(uri,
				categories, transactionId, null));

		WebResource.Builder builder = addVersionHeader(desc, webResource.getRequestBuilder(), "If-Match");

		ClientResponse response = builder.delete(ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.NOT_FOUND) {
			response.close();
			throw new ResourceNotFoundException(
					"Could not delete non-existent document");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			// TODO: inspect response structure to distinguish from insufficient privilege
			if (desc instanceof DocumentDescriptorImpl && ((DocumentDescriptorImpl) desc).isInternal() == false &&
					desc.getVersion() == DocumentDescriptor.UNKNOWN_VERSION)
				throw new FailedRequestException("Content version required to delete document", extractErrorFields(response));
			throw new ForbiddenUserException("User is not allowed to delete documents",extractErrorFields(response));
		}
		if (status == ClientResponse.Status.PRECONDITION_FAILED) {
			response.close();
			throw new FailedRequestException(
					"Content version must match to delete document");
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
			ForbiddenUserException,  FailedRequestException {

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

		if (logger.isInfoEnabled())
			logger.info("Getting {} in transaction {}", uri, transactionId);

		WebResource.Builder builder = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId, extraParams))
				.accept(mimetype);

		if (extraParams != null && extraParams.containsKey("range"))
			builder = builder.header("range", extraParams.get("range").get(0));

		builder = addVersionHeader(desc, builder, "If-None-Match");

		ClientResponse response = builder.get(ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

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
		if (status != ClientResponse.Status.OK && status != ClientResponse.Status.PARTIAL_CONTENT)
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

		handleBase.receiveContent(
				(reqlog != null) ? reqlog.copyContent(entity) : entity);

		return true;
	}

	private boolean getDocumentImpl(RequestLogger reqlog,
			DocumentDescriptor desc, String transactionId,
			Set<Metadata> categories, RequestParameters extraParams,
			String metadataFormat, DocumentMetadataReadHandle metadataHandle,
			AbstractReadHandle contentHandle) throws 
			ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document read for document identifier without uri");

		assert metadataHandle != null : "metadataHandle is null";
		assert contentHandle  != null : "contentHandle is null";

		if (logger.isInfoEnabled())
			logger.info("Getting multipart for {} in transaction {}", uri,
				transactionId);

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transactionId, extraParams, true);
		docParams.add("format", metadataFormat);

		WebResource.Builder builder = makeDocumentResource(docParams).accept(
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		builder = addVersionHeader(desc, builder, "If-None-Match");

		ClientResponse response = builder.get(ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

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
		HandleImplementation contentBase  = HandleAccessor.as(contentHandle);

		BodyPart contentPart = partList.get(1);

		MultivaluedMap<String, String> responseHeaders = response.getHeaders();
		MultivaluedMap<String, String> contentHeaders  = contentPart.getHeaders();
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

		metadataBase.receiveContent(
				partList.get(0).getEntityAs(metadataBase.receiveAs())
				);

		Object contentEntity = contentPart.getEntityAs(
				contentBase.receiveAs());
		contentBase.receiveContent(
				(reqlog != null) ? reqlog.copyContent(contentEntity) : contentEntity);

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

		if (logger.isInfoEnabled())
			logger.info("Requesting head for {} in transaction {}", uri,
				transactionId);

		WebResource webResource = makeDocumentResource(makeDocumentParams(uri,
				null, transactionId, null));

		ClientResponse response = webResource.head();

		MultivaluedMap<String, String> responseHeaders = response.getHeaders();

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

		response.close();
		logRequest(reqlog, "checked %s document from %s transaction", uri,
				(transactionId != null) ? transactionId : "no");

		DocumentDescriptorImpl desc =
			new DocumentDescriptorImpl(uri,false);

		updateVersion(desc,responseHeaders);
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
		String contentMimetype = (descFormat != null && descFormat != Format.UNKNOWN) ?
				desc.getMimetype() : null;
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
			ForbiddenUserException, FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document write for document identifier without uri");

		Object value = HandleAccessor.as(handle).sendContent();
		if (value == null)
			throw new IllegalArgumentException(
					"Document write with null value for " + uri);

		if (logger.isInfoEnabled())
			logger.info("Putting {} in transaction {}", uri, transactionId);

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

		ClientResponse response = null;
		if (value instanceof OutputStreamSender) {
			if (isFirstRequest || headFirst)
				makeFirstRequest();
			response = builder
					.put(ClientResponse.class, new StreamingOutputImpl(
							(OutputStreamSender) value, reqlog));
			if (isFirstRequest)
				isFirstRequest = false;
		} else {
			if ((isFirstRequest || headFirst)
					&& (value instanceof InputStream || value instanceof Reader))
				makeFirstRequest();

			if (reqlog != null)
				response = builder.put(ClientResponse.class,
						reqlog.copyContent(value));
			else
				response = builder.put(ClientResponse.class, value);

			if (isFirstRequest)
				isFirstRequest = false;
		}

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException(
					"Could not write non-existent document",
					extractErrorFields(response));
		if (status == ClientResponse.Status.FORBIDDEN) {
			if (desc instanceof DocumentDescriptorImpl && ((DocumentDescriptorImpl) desc).isInternal() == false &&
					desc.getVersion() == DocumentDescriptor.UNKNOWN_VERSION)
				throw new FailedRequestException("Content version required to write document",extractErrorFields(response));
			throw new ForbiddenUserException("User is not allowed to write documents",extractErrorFields(response));
		}
		if (status == ClientResponse.Status.PRECONDITION_FAILED)
			throw new FailedRequestException(
					"Content version must match to write document",
					extractErrorFields(response));
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
			AbstractWriteHandle contentHandle) throws 
			ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		String uri = desc.getUri();
		if (uri == null)
			throw new IllegalArgumentException(
					"Document write for document identifier without uri");

		if (logger.isInfoEnabled())
			logger.info("Putting multipart for {} in transaction {}", uri,
				transactionId);

		logRequest(
				reqlog,
				"writing %s document from %s transaction with %s metadata categories and content",
				uri, (transactionId != null) ? transactionId : "no",
				stringJoin(categories, ", ", "no"));

		boolean hasStreamingPart = false;

		MultiPart multiPart = new MultiPart();
		multiPart.setMediaType(new MediaType("multipart", "mixed"));

		for (int i = 0; i < 2; i++) {
			String mimetype = null;
			Object value = null;
			if (i == 0) {
				mimetype = metadataMimetype;
				value = HandleAccessor.as(metadataHandle).sendContent();
			} else {
				mimetype = contentMimetype;
				value = HandleAccessor.as(contentHandle).sendContent();
			}

			String[] typeParts = (mimetype != null && mimetype.contains("/")) ?
					mimetype.split("/", 2) : null;

			MediaType typePart = (typeParts != null) ?
					new MediaType(typeParts[0], typeParts[1]) :
						MediaType.WILDCARD_TYPE;

			BodyPart bodyPart = null;
			if (value instanceof OutputStreamSender) {
				hasStreamingPart = true;
				bodyPart = new BodyPart(new StreamingOutputImpl(
						(OutputStreamSender) value, reqlog), typePart);
			} else {
				if (value instanceof InputStream || value instanceof Reader)
					hasStreamingPart = true;

				if (reqlog != null)
					bodyPart = new BodyPart(reqlog.copyContent(value), typePart);
				else
					bodyPart = new BodyPart(value, typePart);
			}

			multiPart = multiPart.bodyPart(bodyPart);
		}

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transactionId, extraParams, true);

		if ((isFirstRequest || headFirst) && hasStreamingPart)
			makeFirstRequest();

		WebResource.Builder builder = makeDocumentResource(docParams).type(
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE)
				);
		builder = addVersionHeader(desc, builder, "If-Match");

		ClientResponse response = builder.put(ClientResponse.class, multiPart);

		if (isFirstRequest)
			isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.NOT_FOUND) {
			response.close();
			throw new ResourceNotFoundException(
					"Could not write non-existent document");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			// TODO: inspect response structure to distinguish from insufficient privilege
			if (desc instanceof DocumentDescriptorImpl && ((DocumentDescriptorImpl) desc).isInternal() == false &&
					desc.getVersion() == DocumentDescriptor.UNKNOWN_VERSION)
				throw new FailedRequestException("Content version required to write document", extractErrorFields(response));
			throw new ForbiddenUserException("User is not allowed to write documents", extractErrorFields(response));
		}
		if (status == ClientResponse.Status.PRECONDITION_FAILED) {
			response.close();
			throw new FailedRequestException(
					"Content version must match to write document");
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
		if (logger.isInfoEnabled())
			logger.info("Opening transaction");

		MultivaluedMap<String, String> transParams = null;
		if (name != null || timeLimit > 0) {
			transParams = new MultivaluedMapImpl();
			if (name != null)
				transParams.add("name", name);
			if (timeLimit > 0)
				transParams.add("timeLimit", String.valueOf(timeLimit));
		}

		WebResource resource = (transParams != null) ? connection.path(
				"transactions").queryParams(transParams) : connection
				.path("transactions");

		ClientResponse response = resource.post(ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
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

		if (logger.isInfoEnabled())
			logger.info("Completing transaction {} with {}", transactionId, result);

		MultivaluedMap<String, String> transParams = new MultivaluedMapImpl();
		transParams.add("result", result);

		ClientResponse response = connection
				.path("transactions/" + transactionId).queryParams(transParams)
				.post(ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

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
				if (!"range".equalsIgnoreCase(extraKey))
					docParams.put(extraKey, entry.getValue());
			}
		}
		docParams.add("uri", uri);
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

	private WebResource.Builder addVersionHeader(DocumentDescriptor desc, WebResource.Builder builder, String name) {
		if (desc != null && desc instanceof DocumentDescriptorImpl &&
				!((DocumentDescriptorImpl) desc).isInternal()) {
			long version = desc.getVersion();
			if (version != DocumentDescriptor.UNKNOWN_VERSION) {
				return builder.header(name, "\""+String.valueOf(version)+"\"");
			}
		}
		return builder;
	}

	@Override
	public <T> T search(Class<T> as, QueryDefinition queryDef, String mimetype,
			long start, long len, QueryView view, String transactionId) 
	throws ForbiddenUserException, FailedRequestException {
		RequestParameters params = new RequestParameters();
		ClientResponse response = null;

		if (start > 1) {
			params.put("start", "" + start);
		}

		if (len > 0) {
			params.put("pageLength", "" + len);
		}

		if (view != null && view != QueryView.DEFAULT) {
			if (view == QueryView.ALL) {
				params.put("view", "all");
			} else if (view == QueryView.RESULTS) {
				params.put("view", "results");
			} else if (view == QueryView.FACETS) {
				params.put("view", "facets");
			} else if (view == QueryView.METADATA) {
				params.put("view", "metadata");
			}
		}

		if (queryDef.getDirectory() != null) {
			params.put("directory", queryDef.getDirectory());
		}

		for (String collection : queryDef.getCollections()) {
			params.put("collection", collection);
		}

		if (transactionId != null) {
			params.put("txid", transactionId);
		}

		String optionsName = queryDef.getOptionsName();
		if (optionsName != null && optionsName.length() > 0) {
			params.put("options", optionsName);
		}

		if (queryDef instanceof StringQueryDefinition) {
			String text = ((StringQueryDefinition) queryDef).getCriteria();
			if (logger.isInfoEnabled())
				logger.info("Searching for {} in transaction {}", text,
					transactionId);

            if (text != null) {
			    params.put("q", text);
            }

			response = connection.path("search")
					.queryParams(((RequestParametersImplementation) params).getMapImpl())
					.accept(mimetype).get(ClientResponse.class);

			if (isFirstRequest)
				isFirstRequest = false;
		} else if (queryDef instanceof KeyValueQueryDefinition) {
			Map<ValueLocator, String> pairs = ((KeyValueQueryDefinition) queryDef);
			if (logger.isInfoEnabled())
				logger.info("Searching for keys/values in transaction {}",
					transactionId);

			for (ValueLocator loc : pairs.keySet()) {
				if (loc instanceof KeyLocator) {
					params.put("key", ((KeyLocator) loc).getKey());
				} else {
					ElementLocator eloc = (ElementLocator) loc;
					params.put("element", eloc.getElement().toString());
					if (eloc.getAttribute() != null) {
						params.put("attribute", eloc.getAttribute().toString());
					}
				}
				params.put("value", pairs.get(loc));
			}

			response = connection.path("keyvalue")
					.queryParams(((RequestParametersImplementation) params).getMapImpl())
					.accept(mimetype).get(ClientResponse.class);

			if (isFirstRequest)
				isFirstRequest = false;
		} else if (queryDef instanceof StructuredQueryDefinition) {
			String structure = ((StructuredQueryDefinition) queryDef)
					.serialize();

            response = connection.path("search").queryParams(((RequestParametersImplementation) params).getMapImpl())
                    .type("application/xml")
                    .post(ClientResponse.class, structure);

		    isFirstRequest = false;
        } else if (queryDef instanceof DeleteQueryDefinition) {
        	if (logger.isInfoEnabled())
        		logger.info("Searching for deletes in transaction {}", transactionId);
            response = connection.path("search")
                    .queryParams(((RequestParametersImplementation) params).getMapImpl())
                    .accept(mimetype).get(ClientResponse.class);

            isFirstRequest = false;
        } else {
			throw new UnsupportedOperationException("Cannot search with "
					+ queryDef.getClass().getName());
		}

		ClientResponse.Status status = response.getClientResponseStatus();
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
    public void deleteSearch(DeleteQueryDefinition queryDef, String transactionId) throws ForbiddenUserException,
            FailedRequestException {
        RequestParameters params = new RequestParameters();
        ClientResponse response = null;

        if (queryDef.getDirectory() != null) {
            params.put("directory", queryDef.getDirectory());
        }

        for (String collection : queryDef.getCollections()) {
            params.put("collection", collection);
        }

        if (transactionId != null) {
            params.put("txid", transactionId);
        }

        response = connection.path("search")
                    .queryParams(((RequestParametersImplementation) params).getMapImpl())
                    .delete(ClientResponse.class);

        isFirstRequest = false;

        ClientResponse.Status status = response.getClientResponseStatus();

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
		ClientResponse response = null;

		String optionsName = valDef.getOptionsName();
		if (optionsName != null && optionsName.length() > 0) {
			docParams.add("options", optionsName);
		}

		if (valDef.getAggregate() != null) {
            for (String aggregate : valDef.getAggregate()) {
			    docParams.add("aggregate", aggregate);
            }
		}

		if (valDef.getAggregatePath() != null) {
			docParams.add("aggregatePath", valDef.getAggregatePath());
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
					docParams.add("options", optionsName);
				}
			} else if (queryDef.getOptionsName() != null) {
				if (optionsName != queryDef.getOptionsName() &&
						logger.isWarnEnabled())
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
	            	docParams.add("q", text);
	            }
			} else if (queryDef instanceof StructuredQueryDefinition) {
				String structure = ((StructuredQueryDefinition) queryDef)
						.serialize();
	            if (structure != null) {
	            		docParams.add(
	            			"structuredQuery",
	            			structure
	            			);
	            }
			} else {
				if (logger.isWarnEnabled())
					logger.warn("unsupported query definition: "+
							queryDef.getClass().getName());
			}
		}

		if (transactionId != null) {
			docParams.add("txid", transactionId);
		}

		String uri = "values";
		if (valDef.getName() != null) {
			uri += "/" + valDef.getName();
		}

		response = connection.path(uri).queryParams(docParams).accept(mimetype)
				.get(ClientResponse.class);
		isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
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
		ClientResponse response = null;

		String optionsName = valDef.getOptionsName();
		if (optionsName != null && optionsName.length() > 0) {
			docParams.add("options", optionsName);
		}

		if (transactionId != null) {
			docParams.add("txid", transactionId);
		}

		String uri = "values";

		response = connection.path(uri).queryParams(docParams).accept(mimetype)
				.get(ClientResponse.class);
		isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
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
		ClientResponse response = null;

		if (transactionId != null) {
			docParams.add("txid", transactionId);
		}

		String uri = "config/query";

		response = connection.path(uri).queryParams(docParams).accept(mimetype)
				.get(ClientResponse.class);
		isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
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
			String mimetype, Class<T> as) throws ForbiddenUserException,
			FailedRequestException {
		if (logger.isInfoEnabled())
			logger.info("Getting {}/{}", type, key);

		ClientResponse response = connection.path(type + "/" + key)
				.accept(mimetype).get(ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.OK) {
			if (status == ClientResponse.Status.NOT_FOUND) {
				response.close();
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
		if (logger.isInfoEnabled())
			logger.info("Getting {}", type);

		ClientResponse response = connection.path(type).accept(mimetype)
				.get(ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

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

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}

	@Override
	public void putValues(RequestLogger reqlog, String type, String mimetype,
			Object value) throws ForbiddenUserException, FailedRequestException {
		if (logger.isInfoEnabled())
			logger.info("Posting {}", type);

		putPostValueImpl(reqlog, "put", type, null, null, mimetype, value,
				ClientResponse.Status.NO_CONTENT);
	}

	@Override
	public void postValues(RequestLogger reqlog, String type, String mimetype,
			Object value) throws ForbiddenUserException, FailedRequestException {
		if (logger.isInfoEnabled())
			logger.info("Posting {}", type);

		putPostValueImpl(reqlog, "post", type, null, null, mimetype, value,
				ClientResponse.Status.NO_CONTENT);
	}

	@Override
	public void postValue(RequestLogger reqlog, String type, String key,
			String mimetype, Object value) throws ForbiddenUserException,
			FailedRequestException {
		if (logger.isInfoEnabled())
			logger.info("Posting {}/{}", type, key);

		putPostValueImpl(reqlog, "post", type, key, null, mimetype, value,
				ClientResponse.Status.CREATED);
	}

	@Override
	public void putValue(RequestLogger reqlog, String type, String key,
			String mimetype, Object value) throws ResourceNotFoundException,
			ForbiddenUserException, FailedRequestException {
		if (logger.isInfoEnabled())
			logger.info("Putting {}/{}", type, key);

		putPostValueImpl(reqlog, "put", type, key, null, mimetype, value,
				ClientResponse.Status.NO_CONTENT, ClientResponse.Status.CREATED);
	}

	@Override
	public void putValue(RequestLogger reqlog, String type, String key,
			RequestParameters extraParams, String mimetype, Object value)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		if (logger.isInfoEnabled())
			logger.info("Putting {}/{}", type, key);

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

		boolean hasStreamingPart = false;

		Object sentValue = null;
		if (value instanceof OutputStreamSender) {
			hasStreamingPart = true;
			sentValue = new StreamingOutputImpl((OutputStreamSender) value,
					reqlog);
		} else {
			if (value instanceof InputStream || value instanceof Reader)
				hasStreamingPart = true;

			if (reqlog != null)
				sentValue = reqlog.copyContent(value);
			else
				sentValue = value;
		}

		MultivaluedMap<String, String> requestParams = convertParams(extraParams);

		ClientResponse response = null;
		if ("put".equals(method)) {
			if ((isFirstRequest || headFirst) && hasStreamingPart)
				makeFirstRequest();

			String connectPath = (key != null) ? type + "/" + key : type;

			WebResource resource = (requestParams == null) ? connection
					.path(connectPath) : connection.path(connectPath)
					.queryParams(requestParams);

			response = resource.type(mimetype).put(ClientResponse.class,
					sentValue);

			if (isFirstRequest)
				isFirstRequest = false;
		} else if ("post".equals(method)) {
			if ((isFirstRequest || headFirst) && hasStreamingPart)
				makeFirstRequest();

			WebResource resource = (requestParams == null) ? connection
					.path(type) : connection.path(type).queryParams(
					requestParams);

			response = resource.type(mimetype).post(ClientResponse.class,
					sentValue);

			if (isFirstRequest)
				isFirstRequest = false;
		} else {
			throw new MarkLogicInternalException("unknown method type "
					+ method);
		}

		ClientResponse.Status status = response.getClientResponseStatus();
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
		if (logger.isInfoEnabled())
			logger.info("Deleting {}/{}", type, key);

		ClientResponse response = connection.path(type + "/" + key).delete(
				ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

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
		if (logger.isInfoEnabled())
			logger.info("Deleting {}", type);

		ClientResponse response = connection.path(type).delete(
				ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

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
	public <T> T getResource(RequestLogger reqlog, String path,
			RequestParameters params, String mimetype, Class<T> as)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		ClientResponse response = doGet(path, params, mimetype);

		checkStatus(response, "read", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.NO_CONTENT);

		return makeResult(reqlog, "read", "resource", response, as);
	}

	@Override
	public ServiceResultIterator getResource(RequestLogger reqlog, String path,
			RequestParameters params, String[] mimetypes)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		ClientResponse response = doGet(path, params,
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		checkStatus(response, "read", "resource", path,
				(response.hasEntity() ||
						(mimetypes != null && mimetypes.length > 0)) ?
					ResponseStatus.OK : ResponseStatus.NO_CONTENT);

		return makeResults(reqlog, "read", "resource", response);
	}

	@Override
	public <T> T putResource(RequestLogger reqlog, String path,
			RequestParameters params, String inputMimetype, Object value,
			String outputMimetype, Class<T> as)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		ClientResponse response = doPut(reqlog, path, params, inputMimetype,
				value, outputMimetype);

		checkStatus(response, "write", "resource", path,
				(as != null) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResult(reqlog, "write", "resource", response, as);
	}

	@Override
	public <T> T putResource(RequestLogger reqlog, String path,
			RequestParameters params, String[] inputMimetypes, Object[] values,
			String outputMimetype, Class<T> as)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		ClientResponse response = doPut(reqlog, path, params, inputMimetypes,
				values, outputMimetype);

		checkStatus(response, "write", "resource", path,
				(as != null) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResult(reqlog, "write", "resource", response, as);
	}

	@Override
	public <T> T postResource(RequestLogger reqlog, String path,
			RequestParameters params, String inputMimetype, Object value,
			String outputMimetype, Class<T> as) throws ResourceNotFoundException,
			ForbiddenUserException, FailedRequestException {
		ClientResponse response = doPost(reqlog, path, params, inputMimetype,
				value, outputMimetype);

		checkStatus(response, "apply", "resource", path,
				(as != null) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResult(reqlog, "apply", "resource", response, as);
	}

	@Override
	public <T> T postResource(RequestLogger reqlog, String path,
			RequestParameters params, String[] inputMimetypes, Object[] values,
			String outputMimetype, Class<T> as) throws ResourceNotFoundException,
			ForbiddenUserException, FailedRequestException {
		ClientResponse response = doPost(reqlog, path, params, inputMimetypes,
				values, outputMimetype);

		checkStatus(response, "apply", "resource", path,
				(as != null) ? ResponseStatus.OK
						: ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResult(reqlog, "apply", "resource", response, as);
	}

	@Override
	public ServiceResultIterator postResource(RequestLogger reqlog, String path,
			RequestParameters params, String inputMimetype, Object value,
			String[] outputMimetypes)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		ClientResponse response = doPost(reqlog, path, params, inputMimetype,
				value,
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		checkStatus(response, "apply", "resource", path,
				(response.hasEntity() ||
						(outputMimetypes != null && outputMimetypes.length > 0)) ?
					ResponseStatus.OK : ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResults(reqlog, "apply", "resource", response);
	}

	@Override
	public ServiceResultIterator postResource(RequestLogger reqlog, String path,
			RequestParameters params, String[] inputMimetypes, Object[] values,
			String[] outputMimetypes)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		ClientResponse response = doPost(reqlog, path, params, inputMimetypes,
				values,
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		checkStatus(response, "apply", "resource", path,
				(response.hasEntity() ||
						(outputMimetypes != null && outputMimetypes.length > 0)) ?
					ResponseStatus.OK : ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResults(reqlog, "apply", "resource", response);
	}

	@Override
	public <T> T deleteResource(RequestLogger reqlog, String path,
			RequestParameters params, String outputMimetype, Class<T> as)
			throws ResourceNotFoundException, ForbiddenUserException,
			FailedRequestException {
		ClientResponse response = doDelete(reqlog, path, params, outputMimetype);

		checkStatus(response, "delete", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.NO_CONTENT);

		return makeResult(reqlog, "delete", "resource", response, as);
	}

	private ClientResponse doGet(String path, RequestParameters params,
			Object mimetype) {
		if (path == null)
			throw new IllegalArgumentException("Read with null path");

		WebResource.Builder builder = makeBuilder(path,
				((RequestParametersImplementation) params).getMapImpl(), null, mimetype);

		if (logger.isInfoEnabled())
			logger.info(String.format("Getting %s as %s", path, mimetype));

		ClientResponse response = builder.get(ClientResponse.class);

		if (isFirstRequest)
			isFirstRequest = false;

		return response;
	}

	private ClientResponse doPut(RequestLogger reqlog, String path,
			RequestParameters params, Object inputMimetype, Object value,
			String outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Write with null path");

		WebResource.Builder builder = makeBuilder(path,
				((RequestParametersImplementation) params).getMapImpl(), inputMimetype,
				outputMimetype);

		if (logger.isInfoEnabled())
			logger.info("Putting {}", path);

		ClientResponse response = null;
		if (value instanceof OutputStreamSender) {
			if (isFirstRequest || headFirst)
				makeFirstRequest();

			response = builder
					.put(ClientResponse.class, new StreamingOutputImpl(
							(OutputStreamSender) value, reqlog));
		} else {
			if ((isFirstRequest || headFirst)
					&& (value instanceof InputStream || value instanceof Reader))
				makeFirstRequest();

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

	private ClientResponse doPut(RequestLogger reqlog, String path,
			RequestParameters params, String[] inputMimetypes, Object[] values,
			String outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Write with null path");

		MultiPart multiPart = new MultiPart();
		boolean hasStreamingPart = addParts(reqlog, multiPart, inputMimetypes,
				values);

		WebResource.Builder builder = makeBuilder(path,
				((RequestParametersImplementation) params).getMapImpl(),
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE),
				outputMimetype);

		if (logger.isInfoEnabled())
			logger.info("Putting multipart for {}", path);

		if (isFirstRequest && hasStreamingPart)
			makeFirstRequest();

		ClientResponse response = builder.put(ClientResponse.class, multiPart);

		if (isFirstRequest)
			isFirstRequest = false;

		return response;
	}

	private ClientResponse doPost(RequestLogger reqlog, String path,
			RequestParameters params, Object inputMimetype, Object value,
			Object outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Apply with null path");

		WebResource.Builder builder = makeBuilder(path,
				((RequestParametersImplementation) params).getMapImpl(), inputMimetype,
				outputMimetype);

		if (logger.isInfoEnabled())
			logger.info("Posting {}", path);

		ClientResponse response = null;
		if (value instanceof OutputStreamSender) {
			if (isFirstRequest || headFirst)
				makeFirstRequest();

			response = builder
					.post(ClientResponse.class, new StreamingOutputImpl(
							(OutputStreamSender) value, reqlog));
		} else {
			if ((isFirstRequest || headFirst)
					&& (value instanceof InputStream || value instanceof Reader))
				makeFirstRequest();

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

	private ClientResponse doPost(RequestLogger reqlog, String path,
			RequestParameters params, String[] inputMimetypes, Object[] values,
			Object outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Apply with null path");

		MultiPart multiPart = new MultiPart();
		boolean hasStreamingPart = addParts(reqlog, multiPart, inputMimetypes,
				values);

		WebResource.Builder builder = makeBuilder(path,
				((RequestParametersImplementation) params).getMapImpl(),
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE),
				outputMimetype);

		if (logger.isInfoEnabled())
			logger.info("Posting multipart for {}", path);

		if ((isFirstRequest || headFirst) && hasStreamingPart)
			makeFirstRequest();

		ClientResponse response = builder.post(ClientResponse.class, multiPart);

		if (isFirstRequest)
			isFirstRequest = false;

		return response;
	}

	private ClientResponse doDelete(RequestLogger reqlog, String path,
			RequestParameters params, String mimetype) {
		if (path == null)
			throw new IllegalArgumentException("Delete with null path");

		WebResource.Builder builder = makeBuilder(path,
				((RequestParametersImplementation) params).getMapImpl(), null, mimetype);

		if (logger.isInfoEnabled())
			logger.info("Deleting {}", path);

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
		requestParams.putAll(params);
		return requestParams;
	}

	private boolean addParts(RequestLogger reqlog, MultiPart multiPart,
			String[] mimetypes, Object[] values) {
		if (mimetypes == null || mimetypes.length == 0)
			throw new IllegalArgumentException(
					"mime types not specified for multipart");

		if (values == null || values.length == 0)
			throw new IllegalArgumentException(
					"values not specified for multipart");

		if (mimetypes.length != values.length)
			throw new IllegalArgumentException(
					"mistmatch between mime types and values for multipart");

		multiPart.setMediaType(new MediaType("multipart", "mixed"));

		boolean hasStreamingPart = false;
		for (int i = 0; i < mimetypes.length; i++) {
			if (mimetypes[i] == null)
				throw new IllegalArgumentException("null mimetype: " + i);

			String[] typeParts = mimetypes[i].contains("/") ? mimetypes[i]
					.split("/", 2) : null;

			MediaType typePart = (typeParts != null) ? new MediaType(
					typeParts[0], typeParts[1]) : MediaType.WILDCARD_TYPE;

			BodyPart bodyPart = null;
			if (values[i] instanceof OutputStreamSender) {
				hasStreamingPart = true;
				bodyPart = new BodyPart(new StreamingOutputImpl(
						(OutputStreamSender) values[i], reqlog), typePart);
			} else {
				if (values[i] instanceof InputStream
						|| values[i] instanceof Reader)
					hasStreamingPart = true;

				if (reqlog != null)
					bodyPart = new BodyPart(reqlog.copyContent(values[i]),
							typePart);
				else
					bodyPart = new BodyPart(values[i], typePart);
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

	private void checkStatus(ClientResponse response, String operation,
			String entityType, String path, ResponseStatus expected) {
		ClientResponse.Status status = response.getClientResponseStatus();
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

	private ServiceResultIterator makeResults(RequestLogger reqlog, String operation,
			String entityType, ClientResponse response) {
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
		private BodyPart      part;
		private boolean       extractedHeaders = false;
		private Format        format;
		private String        mimetype;
		private long          length;
		public JerseyResult(RequestLogger reqlog, BodyPart part) {
			super();
			this.reqlog = reqlog;
			this.part   = part;
		}
		@Override
		public <R extends AbstractReadHandle> R getContent(R handle) {
			if (part == null)
				throw new IllegalStateException("Content already retrieved");

			HandleImplementation handleBase = HandleAccessor.as(handle);

			extractHeaders();
			updateFormat(handleBase,   format);
			updateMimetype(handleBase, mimetype);
			updateLength(handleBase,   length);

			Object contentEntity = part.getEntityAs(handleBase.receiveAs());
			handleBase.receiveContent( (reqlog != null) ?
					reqlog.copyContent(contentEntity) : contentEntity);

			part   = null;
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
			format   = getHeaderFormat(headers);
			mimetype = getHeaderMimetype(headers);
			length   = getHeaderLength(headers);
			extractedHeaders = true;
		}
	}
	public class JerseyResultIterator implements ServiceResultIterator {
		private RequestLogger      reqlog;
		private ClientResponse     response;
		private Iterator<BodyPart> partQueue;
		public JerseyResultIterator(
				RequestLogger reqlog, ClientResponse response, List<BodyPart> partList
				) {
			super();
			if (response != null) {
				if (partList != null && partList.size() > 0) {
					this.reqlog    = reqlog;
					this.response  = response;
					this.partQueue = new ConcurrentLinkedQueue<BodyPart>(partList).iterator();
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
}
