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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.AbstractDocumentManager.Metadata;
import com.marklogic.client.BadRequestException;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DocumentIdentifier;
import com.marklogic.client.ElementLocator;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.KeyLocator;
import com.marklogic.client.MarkLogicInternalException;
import com.marklogic.client.RequestLogger;
import com.marklogic.client.RequestParameters;
import com.marklogic.client.RequestParametersAccessor;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ValueLocator;
import com.marklogic.client.config.KeyValueQueryDefinition;
import com.marklogic.client.config.QueryDefinition;
import com.marklogic.client.config.StringQueryDefinition;
import com.marklogic.client.config.StructuredQueryDefinition;
import com.marklogic.client.io.OutputStreamSender;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.Boundary;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;

public class JerseyServices implements RESTServices {
	static final private Logger logger = LoggerFactory.getLogger(JerseyServices.class);

	private ApacheHttpClient4 client;
	private WebResource       connection;
	private boolean           isFirstRequest = true;

	public JerseyServices() {
	}

	@Override
	public void connect(String host, int port, String user, String password, Authentication type, SSLContext context, HostnameVerifier verifier) {
		if (logger.isInfoEnabled())
			logger.info("Connecting to {} at {} as {}", new Object[] { host,
					port, user });

		if (host == null)
			throw new IllegalArgumentException("No host provided");
		if (user == null)
			throw new IllegalArgumentException("No user provided");
		if (password == null)
			throw new IllegalArgumentException("No password provided");
		if (type == null) {
			if (context != null) {
				type = Authentication.BASIC;
			} else {
				throw new IllegalArgumentException("No authentication type provided");
			}
		}

		if (connection != null)
			connection = null;
		if (client != null) {
			client.destroy();
			client = null;
		}

		// TODO: integrated control of HTTP Client and Jersey Client logging
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "warn");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "warn");

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
				new Scheme(
						(context != null) ? "https" : "http",
						port,
						PlainSocketFactory.getSocketFactory()
						));

		ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(schemeRegistry);
		connMgr.setDefaultMaxPerRoute(100);

		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(
			new AuthScope(host, port),
		    new UsernamePasswordCredentials(user, password)
			);

		List<String> authpref = new ArrayList<String>();
		if (type == Authentication.BASIC)
			authpref.add(AuthPolicy.BASIC);
		else if (type == Authentication.DIGEST)
			authpref.add(AuthPolicy.DIGEST);
		else
			throw new MarkLogicInternalException(
					"Internal error - unknown authentication type: "
							+ type.name());

		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter(AuthPNames.PROXY_AUTH_PREF,           authpref);
		// note that setting PROPERTY_FOLLOW_REDIRECTS below doesn't seem to work
		httpParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

		DefaultApacheHttpClient4Config config = new DefaultApacheHttpClient4Config();
		Map<String, Object> configProps = config.getProperties();
		configProps.put(ApacheHttpClient4Config.PROPERTY_PREEMPTIVE_BASIC_AUTHENTICATION, false);
		configProps.put(ApacheHttpClient4Config.PROPERTY_CONNECTION_MANAGER,    connMgr);
		configProps.put(ApacheHttpClient4Config.PROPERTY_FOLLOW_REDIRECTS,      false);
//		configProps.put(ApacheHttpClient4Config.PROPERTY_CREDENTIALS_PROVIDER,  credentialsProvider);
		configProps.put(ApacheHttpClient4Config.PROPERTY_HTTP_PARAMS,           httpParams);
//		configProps.put(ApacheHttpClient4Config.PROPERTY_CHUNKED_ENCODING_SIZE, 0);
		if (context != null)
			// TODO: confirm that verifier can be null or supply default verifier that returns true
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(verifier, context));

		// TODO: remove temporary hack when Maven build merge multipart before core in service definition
		Collections.addAll(
				config.getClasses(),
				com.sun.jersey.multipart.impl.MultiPartReaderClientSide.class,
				// com.sun.jersey.multipart.impl.MultiPartReaderServerSide
				com.sun.jersey.multipart.impl.MultiPartWriter.class,
				com.sun.jersey.multipart.impl.MultiPartConfigProvider.class
				// com.sun.jersey.multipart.impl.FormDataMultiPartDispatchProvider.class
		);

/* approach in Apache HTTP Client 3
		ApacheHttpClientState state = config.getState();
		state.setCredentials(null, host, port, user, password);
  */

		client = ApacheHttpClient4.create(config);

		HttpClient httpClient = client.getClientHandler().getHttpClient();
/*
		// preemptive digest authentication
        AuthCache authCache = new BasicAuthCache();
        DigestScheme digestAuth = new DigestScheme();
//		digestAuth.overrideParamter("realm", "some realm");
//		digestAuth.overrideParamter("nonce", "whatever");
        authCache.put(
        		new HttpHost(
        				host,
        				port,
        				(context != null) ? "https" : "http"),
        		digestAuth);
        BasicHttpContext localcontext = new BasicHttpContext();
        localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
 */

		if (type == Authentication.BASIC)
			client.addFilter(new HTTPBasicAuthFilter(user, password));
		else if (type == Authentication.DIGEST)
			client.addFilter(new HTTPDigestAuthFilter(user, password));
		else
			throw new MarkLogicInternalException(
					"Internal error - unknown authentication type: "
							+ type.name());

		connection = client.resource("http://" + host + ":" + port + "/v1/");
	}

	@Override
	public void release() {
		if (client == null)
			return;

		logger.info("Releasing connection");

		connection = null;
//		client.getClientHandler().getHttpClient().getConnectionManager().shutdown();
		client.destroy();
		client = null;

		isFirstRequest = true;
	}
	private void makeFirstRequest() {
		connection.path("current/datetime").head();
	}

	@Override
	public void deleteDocument(RequestLogger reqlog, DocumentIdentifier docId, String transactionId, Set<Metadata> categories)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Document delete with null document identifier");

		String uri = docId.getUri();
		if (uri == null)
			throw new IllegalArgumentException("Document delete for document identifier without uri");

		logger.info("Deleting {} in transaction {}", uri, transactionId);

		ClientResponse response = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId, null)).delete(
				ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException("Could not delete non-existent document");
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to delete documents");
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("delete failed: "+status.getReasonPhrase());

		logRequest(reqlog, "deleted %s document", uri);
	}

	// TODO: does an Input Stream or Reader handle need to cache the response so
	// it can close the response?

	@Override
	public <T> T getDocument(RequestLogger reqlog, DocumentIdentifier docId, String transactionId, Set<Metadata> categories, RequestParameters extraParams, String mimetype, Class<T> as)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Document read with null document identifier");

		String uri = docId.getUri();
		if (uri == null)
			throw new IllegalArgumentException("Document read for document identifier without uri");

		logger.info("Getting {} in transaction {}", uri, transactionId);

		WebResource.Builder resource = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId, extraParams)
				).accept(mimetype);
		if (extraParams != null && extraParams.containsKey("range"))
			resource = resource.header("range", extraParams.get("range").get(0));

		ClientResponse response = resource.get(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.NOT_FOUND) {
			response.close();
			throw new ResourceNotFoundException("Could not read non-existent document");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			response.close();
			throw new ForbiddenUserException("User is not allowed to read documents");
		}
		if (status != ClientResponse.Status.OK) {
			response.close();
			throw new FailedRequestException("read failed: "+status.getReasonPhrase());
		}

		logRequest(reqlog, "read %s document from %s transaction with %s mime type and %s metadata categories",
				uri,
				(transactionId != null) ? transactionId : "no",
				(mimetype != null) ? mimetype : "no",
				stringJoin(categories, ", ", "no")
				);

		updateDocumentIdentifier(docId, response.getHeaders());

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}

	@Override
	public Object[] getDocument(RequestLogger reqlog, DocumentIdentifier docId, String transactionId, Set<Metadata> categories, RequestParameters extraParams, String[] mimetypes, Class[] as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Document read with null document identifier");

		String uri = docId.getUri();
		if (uri == null)
			throw new IllegalArgumentException("Document read for document identifier without uri");

		logger.info("Getting multipart for {} in transaction {}", uri, transactionId);

		if (mimetypes == null || mimetypes.length == 0)
			throw new BadRequestException("mime types not specified for read");
		if (as == null || as.length == 0)
			throw new BadRequestException("handle classes not specified for read");
		if (mimetypes.length != as.length)
			throw new BadRequestException(
					"mistmatch between mime types and handle classes for read");

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transactionId, extraParams, true);
		if (mimetypes[0].startsWith("application/")) {
			docParams.add("format",
					mimetypes[0].substring("application/".length()));
		}

		ClientResponse response = makeDocumentResource(docParams).accept(
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE))
				.get(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.NOT_FOUND) {
			response.close();
			throw new ResourceNotFoundException("Could not read non-existent document");
		}
		if (status == ClientResponse.Status.FORBIDDEN) {
			response.close();
			throw new ForbiddenUserException("User is not allowed to read documents");
		}
		if (status != ClientResponse.Status.OK) {
			response.close();
			throw new FailedRequestException("read failed: "+status.getReasonPhrase());
		}

		logRequest(reqlog, "read %s document from %s transaction with %s metadata categories and content",
				uri,
				(transactionId != null) ? transactionId : "no",
				stringJoin(categories, ", ", "no")
				);

		updateDocumentIdentifier(docId, response.getHeaders());

		MultiPart entity = response.getEntity(MultiPart.class);
		if (entity == null)
			return null;

		List<BodyPart> partList = entity.getBodyParts();
		if (partList == null)
			return null;

		int partCount = partList.size();
		if (partCount == 0)
			return null;
		if (partCount != as.length)
			throw new FailedRequestException("read expected " + as.length
					+ " parts but got " + partCount + " parts");

		Object[] parts = new Object[partCount];
		for (int i = 0; i < partCount; i++) {
			Object part = partList.get(i).getEntityAs(as[i]);
			parts[i] = (reqlog != null) ? reqlog.copyContent(part) : part;
		}

		response.close();

		return parts;
	}

	@Override
	public boolean head(RequestLogger reqlog, DocumentIdentifier docId, String transactionId) throws ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Existence check with null document identifier");

		String uri = docId.getUri();
		if (uri == null)
			throw new IllegalArgumentException("Existence check for document identifier without uri");

		logger.info("Requesting head for {} in transaction {}", uri,
				transactionId);

		ClientResponse response = makeDocumentResource(
				makeDocumentParams(uri, null, transactionId, null)).head();

		MultivaluedMap<String, String> headers = response.getHeaders();

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.NOT_FOUND)
			return false;
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to check the existence of documents");
		if (status != ClientResponse.Status.OK)
			throw new FailedRequestException("Document existence check failed: "+status.getReasonPhrase());

		logRequest(reqlog, "checked %s document from %s transaction",
				uri,
				(transactionId != null) ? transactionId : "no"
				);

		updateDocumentIdentifier(docId, headers);

		return true;
	}
	@Override
	public void putDocument(RequestLogger reqlog, DocumentIdentifier docId, String transactionId, Set<Metadata> categories, RequestParameters extraParams, String mimetype, Object value)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Document write with null document identifier");

		String uri = docId.getUri();
		if (uri == null)
			throw new IllegalArgumentException("Document write for document identifier without uri");

		if (value == null)
			throw new IllegalArgumentException("Document write with null value for "+uri);

		logger.info("Putting {} in transaction {}", uri, transactionId);

		logRequest(reqlog, "writing %s document from %s transaction with %s mime type and %s metadata categories",
				uri,
				(transactionId != null) ? transactionId : "no",
				(mimetype != null) ? mimetype : "no",
				stringJoin(categories, ", ", "no")
				);

		WebResource webResource = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId, extraParams)
			);
		WebResource.Builder builder = webResource.type(
			(mimetype != null) ? mimetype : MediaType.WILDCARD
			);

		ClientResponse response = null;
		if (value instanceof OutputStreamSender) {
			if (isFirstRequest) makeFirstRequest();
			response = builder.put(ClientResponse.class, new StreamingOutputImpl((OutputStreamSender) value, reqlog));
			if (isFirstRequest) isFirstRequest = false;
		} else {
			if (isFirstRequest && (value instanceof InputStream || value instanceof Reader))
				makeFirstRequest();

			if (reqlog != null)
				response = builder.put(ClientResponse.class, reqlog.copyContent(value));
			else
				response = builder.put(ClientResponse.class, value);

			if (isFirstRequest) isFirstRequest = false;
		}

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException("Could not write non-existent document");
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to write documents");
		if (status != ClientResponse.Status.CREATED
				&& status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("write failed: "+status.getReasonPhrase());
	}
	@Override
	public void putDocument(RequestLogger reqlog, DocumentIdentifier docId, String transactionId, Set<Metadata> categories, RequestParameters extraParams, String[] mimetypes, Object[] values)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Document write with null document identifier");

		String uri = docId.getUri();
		if (uri == null)
			throw new IllegalArgumentException("Document write for document identifier without uri");

		if (mimetypes == null || mimetypes.length == 0)
			throw new IllegalArgumentException("mime types not specified for write");

		if (values == null || values.length == 0)
			throw new IllegalArgumentException("values not specified for write");

		if (mimetypes.length != values.length)
			throw new IllegalArgumentException(
					"mistmatch between mime types and values for write");

		logger.info("Putting multipart for {} in transaction {}", uri, transactionId);

		logRequest(reqlog, "writing %s document from %s transaction with %s metadata categories and content",
				uri,
				(transactionId != null) ? transactionId : "no",
				stringJoin(categories, ", ", "no")
				);

		boolean hasStreamingPart = false;

		MultiPart multiPart = new MultiPart();
		multiPart.setMediaType(new MediaType("multipart", "mixed"));
		for (int i = 0; i < mimetypes.length; i++) {
			if (mimetypes[i] == null)
				throw new IllegalArgumentException("null mimetype: "+i);

			String[] typeParts = mimetypes[i].contains("/") ?
					mimetypes[i].split("/", 2) : null;

			MediaType typePart = (typeParts != null) ?
					new MediaType(typeParts[0], typeParts[1]) :
					MediaType.WILDCARD_TYPE;

			BodyPart bodyPart = null;
			if (values[i] instanceof OutputStreamSender) {
				hasStreamingPart = true;
				bodyPart = new BodyPart(new StreamingOutputImpl((OutputStreamSender) values[i], reqlog), typePart);
			} else {
				if (values[i] instanceof InputStream || values[i] instanceof Reader)
					hasStreamingPart = true;

				if (reqlog != null)
					bodyPart = new BodyPart(reqlog.copyContent(values[i]), typePart);
				else
					bodyPart = new BodyPart(values[i], typePart);
			}

			multiPart = multiPart.bodyPart(bodyPart);
		}

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transactionId, extraParams, true);

		if (isFirstRequest && hasStreamingPart) makeFirstRequest();

		ClientResponse response = makeDocumentResource(docParams).type(
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE))
				.put(ClientResponse.class, multiPart);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException("Could not write non-existent document");
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to write documents");
		if (status != ClientResponse.Status.CREATED
				&& status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("write failed: "+status.getReasonPhrase());
	}

	@Override
	public String openTransaction() throws ForbiddenUserException, FailedRequestException {
		logger.info("Opening transaction");

		MultivaluedMap<String, String> transParams = new MultivaluedMapImpl();
		transParams.add("name", "java-client-" + new Random().nextLong());

		ClientResponse response = connection.path("transactions").queryParams(transParams).post(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.FORBIDDEN) {
			response.close();
			throw new ForbiddenUserException("User is not allowed to open transactions");
		}
		if (status != ClientResponse.Status.SEE_OTHER) {
			response.close();
			throw new FailedRequestException("transaction open failed: " + status.getReasonPhrase());
		}

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
	public void commitTransaction(String transactionId) throws ForbiddenUserException, FailedRequestException {
		logger.info("Committing transaction {}", transactionId);

		if (transactionId == null)
			throw new MarkLogicInternalException("Committing transaction without id");

		ClientResponse response = connection.path(
				"transactions/" + transactionId).put(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to commit transactions");
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("transaction commit failed: " + status.getReasonPhrase());
	}

	@Override
	public void rollbackTransaction(String transactionId) throws ForbiddenUserException, FailedRequestException {
		logger.info("Rolling back transaction {}", transactionId);

		if (transactionId == null)
			throw new MarkLogicInternalException("Rolling back transaction without id");

		ClientResponse response = connection.path(
				"transactions/" + transactionId).delete(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to rollback transactions");
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("transaction rollback failed: " + status.getReasonPhrase());
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri, Set<Metadata> categories, String transactionId, RequestParameters extraParams) {
		return makeDocumentParams(uri, categories, transactionId, extraParams, false);
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri, Set<Metadata> categories,
			String transactionId, RequestParameters extraParams, boolean withContent) {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();
		if (extraParams != null && extraParams.size() > 0) {
			for (Map.Entry<String, List<String>> entry: extraParams.entrySet()) {
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
				for (String category : new String[] { "collections",
						"permissions", "properties", "quality" })
					docParams.add("category", category);
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

	private void updateDocumentIdentifier(DocumentIdentifier docId, MultivaluedMap<String, String> headers) {
		if (headers == null) return;

		List<String> values = null;
		if (docId.getMimetype() == null && headers.containsKey("Content-Type")) {
			values = headers.get("Content-Type");
			if (values != null) {
				String type = values.get(0);
				docId.setMimetype(
						type.contains(";") ? type.substring(0, type.indexOf(";")) : type
						);
			}
		}
		if (headers.containsKey("Content-Length")) {
			values = headers.get("Content-Length");
			if (values != null) {
				docId.setByteLength(
						Integer.valueOf(values.get(0))
						);
			}
		}
	}

	@Override
    public <T> T search(Class<T> as, QueryDefinition queryDef, String mimetype, long start, String transactionId)
    throws ForbiddenUserException, FailedRequestException {
        MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();
        ClientResponse response = null;
        
        if (start > 1) {
            docParams.add("start", ""+start);
        }

        String optionsName = queryDef.getOptionsName();
        if (optionsName != null && optionsName.length() > 0) {
            docParams.add("options", optionsName);
        }
        
        if (queryDef instanceof StringQueryDefinition) {
            String text = ((StringQueryDefinition) queryDef).getCriteria();
            logger.info("Searching for {} in transaction {}", text, transactionId);

            docParams.add("q", text);

    		response = connection.path("search").queryParams(docParams).accept(mimetype).get(ClientResponse.class);

    		if (isFirstRequest) isFirstRequest = false;
        } else if (queryDef instanceof KeyValueQueryDefinition) {
            Map<ValueLocator, String> pairs = ((KeyValueQueryDefinition) queryDef);
            logger.info("Searching for keys/values in transaction {}", transactionId);

            for (ValueLocator loc : pairs.keySet()) {
                if (loc instanceof KeyLocator) {
                    docParams.add("key", ((KeyLocator) loc).getKey());
                } else {
                    ElementLocator eloc = (ElementLocator) loc;
                    docParams.add("element", eloc.getElement().toString());
                    if (eloc.getAttribute() != null) {
                        docParams.add("attribute", eloc.getAttribute().toString());
                    }
                }
                docParams.add("value", pairs.get(loc));
            }

    		response = connection.path("keyvalue").queryParams(docParams).accept(mimetype).get(ClientResponse.class);

    		if (isFirstRequest) isFirstRequest = false;
        } else if (queryDef instanceof StructuredQueryDefinition) {
            String structure = ((StructuredQueryDefinition) queryDef).serialize();

    		response = connection.path("search").type("application/xml").post(ClientResponse.class, structure);

    		if (isFirstRequest) isFirstRequest = false;
        } else {
            throw new UnsupportedOperationException("Cannot search with " + queryDef.getClass().getName());
        }
        
        ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.FORBIDDEN) {
            response.close();
			throw new ForbiddenUserException("User is not allowed to search");
		}
        if (status != ClientResponse.Status.OK) {
            response.close();
            throw new FailedRequestException("search failed: "+status.getReasonPhrase());
        }

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return entity;
    }


	// namespaces, search options etc.
	@Override
	public <T> T getValue(RequestLogger reqlog, String type, String key, String mimetype, Class<T> as)
	throws ForbiddenUserException, FailedRequestException {
		logger.info("Getting {}/{}", type, key);

		ClientResponse response = connection.path(type+"/"+key).accept(mimetype).get(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.OK) {
			response.close();
			if (status == ClientResponse.Status.NOT_FOUND)
				return null;
			else if (status == ClientResponse.Status.FORBIDDEN)
				throw new ForbiddenUserException("User is not allowed to read "+type);
			else
				throw new FailedRequestException(type+" read failed: " + status.getReasonPhrase());
		}

		logRequest(reqlog, "read %s value with %s key and %s mime type",
				type,
				key,
				(mimetype != null) ? mimetype : null
				);

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}
	@Override
	public <T> T getValues(RequestLogger reqlog, String type, String mimetype, Class<T> as)
	throws ForbiddenUserException, FailedRequestException {
		logger.info("Getting {}", type);

		ClientResponse response = connection.path(type).accept(mimetype).get(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.FORBIDDEN) {
			response.close();
			throw new ForbiddenUserException("User is not allowed to read "+type);
		}
		if (status != ClientResponse.Status.OK) {
			response.close();
			throw new FailedRequestException(type+" read failed: " + status.getReasonPhrase());
		}

		logRequest(reqlog, "read %s values with %s mime type",
				type,
				(mimetype != null) ? mimetype : null
				);

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}
	@Override
	public void putValues(RequestLogger reqlog, String type, String mimetype, Object value)
	throws ForbiddenUserException, FailedRequestException {
		logger.info("Posting {}", type);

		putPostValueImpl(reqlog, "put", type, null, null, mimetype, value, ClientResponse.Status.NO_CONTENT);
	}
	@Override
	public void postValues(RequestLogger reqlog, String type, String mimetype, Object value)
	throws ForbiddenUserException, FailedRequestException {
		logger.info("Posting {}", type);

		putPostValueImpl(reqlog, "post", type, null, null, mimetype, value, ClientResponse.Status.NO_CONTENT);
	}
	@Override
	public void postValue(RequestLogger reqlog, String type, String key, String mimetype, Object value)
	throws ForbiddenUserException, FailedRequestException {
		logger.info("Posting {}/{}", type, key);

		putPostValueImpl(reqlog, "post", type, key, null, mimetype, value, ClientResponse.Status.CREATED);
	}
	@Override
	public void putValue(RequestLogger reqlog, String type, String key, String mimetype, Object value)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		logger.info("Putting {}/{}", type, key);

		putPostValueImpl(reqlog, "put", type, key, null, mimetype, value, ClientResponse.Status.NO_CONTENT);
	}
	@Override
	public void putValue(RequestLogger reqlog, String type, String key, RequestParameters extraParams, String mimetype, Object value)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		logger.info("Putting {}/{}", type, key);

		putPostValueImpl(reqlog, "put", type, key, extraParams, mimetype, value, ClientResponse.Status.NO_CONTENT);
	}
	private void putPostValueImpl(
		RequestLogger reqlog, String method, String type, String key, RequestParameters extraParams, String mimetype, Object value, ClientResponse.Status expectedStatus
	) {
		if (key != null) {
			logRequest(reqlog, "writing %s value with %s key and %s mime type",
					type,
					key,
					(mimetype != null) ? mimetype : null
					);
		} else {
			logRequest(reqlog, "writing %s values with %s mime type",
					type,
					(mimetype != null) ? mimetype : null
					);
		}

		boolean hasStreamingPart = false;

		Object sentValue = null;
		if (value instanceof OutputStreamSender) {
			hasStreamingPart = true;
			sentValue = new StreamingOutputImpl((OutputStreamSender) value, reqlog);
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
			if (isFirstRequest && hasStreamingPart) makeFirstRequest();

			String connectPath = (key != null) ? type+"/"+key : type;

			WebResource resource = (requestParams == null) ?
				connection.path(connectPath) :
				connection.path(connectPath).queryParams(requestParams);

			response = resource.type(mimetype).put(ClientResponse.class, sentValue);

			if (isFirstRequest) isFirstRequest = false;
		} else if ("post".equals(method)) {
			if (isFirstRequest && hasStreamingPart) makeFirstRequest();

			WebResource resource = (requestParams == null) ?
				connection.path(type) :
				connection.path(type).queryParams(requestParams);

			response = resource.type(mimetype).post(ClientResponse.class, sentValue);

			if (isFirstRequest) isFirstRequest = false;
		} else {
			throw new MarkLogicInternalException("unknown method type " + method);
		}

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to write "+type);
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException(type+" not found for write");
		if (status != expectedStatus)
			throw new FailedRequestException(type+" write failed: " + status.getReasonPhrase());
	}
	@Override
	public void deleteValue(RequestLogger reqlog, String type, String key) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		logger.info("Deleting {}/{}", type, key);

		ClientResponse response = connection.path(type+"/"+key).delete(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to delete "+type);
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException(type+" not found for delete");
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("delete failed: " + status.getReasonPhrase());

		logRequest(reqlog, "deleted %s value with %s key",
				type,
				key
				);
	}
	@Override
	public void deleteValues(RequestLogger reqlog, String type) throws ForbiddenUserException, FailedRequestException {
		logger.info("Deleting {}", type);

		ClientResponse response = connection.path(type).delete(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to delete "+type);
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("delete failed: " + status.getReasonPhrase());

		logRequest(reqlog, "deleted %s values",
				type
				);
	}

	@Override
	public <T> T getResource(RequestLogger reqlog, String path, RequestParameters params, String mimetype, Class<T> as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ClientResponse response = doGet(path, params, mimetype);

		checkStatus(response, "read", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.NO_CONTENT);

		return makeResult(reqlog, "read", "resource", response, as);
	}
	@Override
	public Object[] getResource(RequestLogger reqlog, String path, RequestParameters params, String[] mimetypes, Class[] as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ClientResponse response =
			doGet(path, params, Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		checkStatus(response, "read", "resource", path,
				(as != null && as.length > 0) ? ResponseStatus.OK : ResponseStatus.NO_CONTENT);

		return makeResults(reqlog, "read", "resource", response, as);
	}

	@Override
	public <T> T putResource(RequestLogger reqlog, String path, RequestParameters params, String inputMimetype, Object value, String outputMimetype, Class<T> as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ClientResponse response = doPut(reqlog, path, params, inputMimetype, value, outputMimetype);

		checkStatus(response, "write", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResult(reqlog, "write", "resource", response, as);
	}
	@Override
	public <T> T putResource(RequestLogger reqlog, String path, RequestParameters params, String[] inputMimetypes, Object[] values, String outputMimetype, Class<T> as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ClientResponse response = doPut(reqlog, path, params, inputMimetypes, values, outputMimetype);

		checkStatus(response, "write", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResult(reqlog, "write", "resource", response, as);
	}

	@Override
	public Object postResource(RequestLogger reqlog, String path, RequestParameters params, String inputMimetype, Object value, String outputMimetype, Class as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ClientResponse response = doPost(reqlog, path, params, inputMimetype, value, outputMimetype);

		checkStatus(response, "apply", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResult(reqlog, "apply", "resource", response, as);
	}
	@Override
	public Object postResource(RequestLogger reqlog, String path, RequestParameters params, String[] inputMimetypes, Object[] values, String outputMimetype, Class as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ClientResponse response = doPost(reqlog, path, params, inputMimetypes, values, outputMimetype);

		checkStatus(response, "apply", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResult(reqlog, "apply", "resource", response, as);
	}
	@Override
	public Object[] postResource(RequestLogger reqlog, String path, RequestParameters params, String inputMimetype, Object value, String[] outputMimetypes, Class[] as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ClientResponse response = doPost(reqlog, path, params, inputMimetype, value, Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		checkStatus(response, "apply", "resource", path,
				(as != null && as.length > 0) ? ResponseStatus.OK : ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResults(reqlog, "apply", "resource", response, as);
	}
	@Override
	public Object[] postResource(RequestLogger reqlog, String path, RequestParameters params, String[] inputMimetypes, Object[] values, String[] outputMimetypes, Class[] as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ClientResponse response = doPost(reqlog, path, params, inputMimetypes, values, Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE));

		checkStatus(response, "apply", "resource", path,
				(as != null && as.length > 0) ? ResponseStatus.OK : ResponseStatus.CREATED_OR_NO_CONTENT);

		return makeResults(reqlog, "apply", "resource", response, as);
	}

	@Override
	public <T> T deleteResource(RequestLogger reqlog, String path, RequestParameters params, String outputMimetype, Class<T> as)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		ClientResponse response = doDelete(reqlog, path, params, outputMimetype);

		checkStatus(response, "delete", "resource", path,
				(as != null) ? ResponseStatus.OK : ResponseStatus.NO_CONTENT);

		return makeResult(reqlog, "delete", "resource", response, as);
	}

	private ClientResponse doGet(String path, RequestParameters params, Object mimetype) {
		if (path == null)
			throw new IllegalArgumentException("Read with null path");

		WebResource.Builder builder = makeBuilder(path, RequestParametersAccessor.getMap(params), null, mimetype);

		logger.info("Getting {}", path);

		ClientResponse response = builder.get(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		return response;
	}
	private ClientResponse doPut(RequestLogger reqlog, String path, RequestParameters params, Object inputMimetype, Object value, String outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Write with null path");

		WebResource.Builder builder = makeBuilder(path, RequestParametersAccessor.getMap(params), inputMimetype, outputMimetype);

		logger.info("Putting {}", path);

		ClientResponse response = null;
		if (value instanceof OutputStreamSender) {
			if (isFirstRequest) makeFirstRequest();

			response = builder.put(ClientResponse.class, new StreamingOutputImpl((OutputStreamSender) value, reqlog));
		} else {
			if (isFirstRequest && (value instanceof InputStream || value instanceof Reader))
				makeFirstRequest();

			if (reqlog != null)
				response = builder.put(ClientResponse.class, reqlog.copyContent(value));
			else
				response = builder.put(ClientResponse.class, value);
		}

		if (isFirstRequest) isFirstRequest = false;

		return response;
	}
	private ClientResponse doPut(RequestLogger reqlog, String path, RequestParameters params, String[] inputMimetypes, Object[] values, String outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Write with null path");

		MultiPart multiPart = new MultiPart();
		boolean hasStreamingPart = addParts(reqlog, multiPart, inputMimetypes, values);

		WebResource.Builder builder = makeBuilder(path, RequestParametersAccessor.getMap(params), 
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE), outputMimetype);

		logger.info("Putting multipart for {}", path);

		if (isFirstRequest && hasStreamingPart) makeFirstRequest();

		ClientResponse response = builder.put(ClientResponse.class, multiPart);

		if (isFirstRequest) isFirstRequest = false;

		return response;
	}
	private ClientResponse doPost(RequestLogger reqlog, String path, RequestParameters params, Object inputMimetype, Object value, Object outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Apply with null path");

		WebResource.Builder builder = makeBuilder(path, RequestParametersAccessor.getMap(params), inputMimetype, outputMimetype);

		logger.info("Posting {}", path);

		ClientResponse response = null;
		if (value instanceof OutputStreamSender) {
			if (isFirstRequest) makeFirstRequest();

			response = builder.post(ClientResponse.class, new StreamingOutputImpl((OutputStreamSender) value, reqlog));
		} else {
			if (isFirstRequest && (value instanceof InputStream || value instanceof Reader))
				makeFirstRequest();

			if (reqlog != null)
				response = builder.post(ClientResponse.class, reqlog.copyContent(value));
			else
				response = builder.post(ClientResponse.class, value);
		}

		if (isFirstRequest) isFirstRequest = false;

		return response;
	}
	private ClientResponse doPost(RequestLogger reqlog, String path, RequestParameters params, String[] inputMimetypes, Object[] values, Object outputMimetype) {
		if (path == null)
			throw new IllegalArgumentException("Apply with null path");

		MultiPart multiPart = new MultiPart();
		boolean hasStreamingPart = addParts(reqlog, multiPart, inputMimetypes, values);

		WebResource.Builder builder = makeBuilder(path, RequestParametersAccessor.getMap(params), 
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE), outputMimetype);

		logger.info("Posting multipart for {}", path);

		if (isFirstRequest && hasStreamingPart) makeFirstRequest();

		ClientResponse response = builder.post(ClientResponse.class, multiPart);

		if (isFirstRequest) isFirstRequest = false;

		return response;
	}
	private ClientResponse doDelete(RequestLogger reqlog, String path, RequestParameters params, String mimetype) {
		if (path == null)
			throw new IllegalArgumentException("Delete with null path");

		WebResource.Builder builder = makeBuilder(path, RequestParametersAccessor.getMap(params), null, mimetype);

		logger.info("Deleting {}", path);

		ClientResponse response = builder.delete(ClientResponse.class);

		if (isFirstRequest) isFirstRequest = false;

		return response;
	}

	private MultivaluedMap<String, String> convertParams(RequestParameters params) {
		if (params == null || params.size() == 0)
			return null;

		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		requestParams.putAll(params);
		return requestParams;
	}
	private boolean addParts(RequestLogger reqlog, MultiPart multiPart, String[] mimetypes, Object[] values) {
		if (mimetypes == null || mimetypes.length == 0)
			throw new IllegalArgumentException("mime types not specified for multipart");

		if (values == null || values.length == 0)
			throw new IllegalArgumentException("values not specified for multipart");

		if (mimetypes.length != values.length)
			throw new IllegalArgumentException("mistmatch between mime types and values for multipart");

		multiPart.setMediaType(new MediaType("multipart", "mixed"));

		boolean hasStreamingPart = false;
		for (int i = 0; i < mimetypes.length; i++) {
			if (mimetypes[i] == null)
				throw new IllegalArgumentException("null mimetype: "+i);

			String[] typeParts = mimetypes[i].contains("/") ?
					mimetypes[i].split("/", 2) : null;

			MediaType typePart = (typeParts != null) ?
					new MediaType(typeParts[0], typeParts[1]) :
					MediaType.WILDCARD_TYPE;

			BodyPart bodyPart = null;
			if (values[i] instanceof OutputStreamSender) {
				hasStreamingPart = true;
				bodyPart = new BodyPart(new StreamingOutputImpl((OutputStreamSender) values[i], reqlog), typePart);
			} else {
				if (values[i] instanceof InputStream || values[i] instanceof Reader)
					hasStreamingPart = true;

				if (reqlog != null)
					bodyPart = new BodyPart(reqlog.copyContent(values[i]), typePart);
				else
					bodyPart = new BodyPart(values[i], typePart);
			}

			multiPart = multiPart.bodyPart(bodyPart);
		}

		return hasStreamingPart;
	}
	private WebResource.Builder makeBuilder(String path, MultivaluedMap<String, String> params, Object inputMimetype, Object outputMimetype) {
		WebResource resource = (params == null) ?
				connection.path(path) :
				connection.path(path).queryParams(params);

		WebResource.Builder builder = resource.getRequestBuilder();

		if (inputMimetype == null) {
		} else if (inputMimetype instanceof String) {
			builder = builder.type((String) inputMimetype);
		} else if (inputMimetype instanceof MediaType) {
			builder = builder.type((MediaType) inputMimetype);
		} else {
			throw new IllegalArgumentException("Unknown input mimetype specifier "+inputMimetype.getClass().getName());
		}

		if (outputMimetype == null) {
		} else if (outputMimetype instanceof String) {
			builder = builder.accept((String) outputMimetype);
		} else if (outputMimetype instanceof MediaType) {
			builder = builder.accept((MediaType) outputMimetype);
		} else {
			throw new IllegalArgumentException("Unknown output mimetype specifier "+outputMimetype.getClass().getName());
		}

		return builder;
	}
	private void checkStatus(ClientResponse response, String operation, String entityType, String path, ResponseStatus expected) {
		ClientResponse.Status status = response.getClientResponseStatus();
		if (! expected.isExpected(status))
		{
			response.close();
			if (status == ClientResponse.Status.NOT_FOUND) {
				throw new ResourceNotFoundException("Could not "+operation+" "+entityType+" at "+path);
			}
			if (status == ClientResponse.Status.FORBIDDEN) {
				throw new ForbiddenUserException("User is not allowed to "+operation+" "+entityType+" at "+path);
			}
			throw new FailedRequestException("failed to "+operation+" "+entityType+" at "+path+": "+status.getReasonPhrase());
		}
	}
	private <T> T makeResult(RequestLogger reqlog, String operation, String entityType, ClientResponse response, Class<T> as) {
		if (as == null)
			return null;

		logRequest(reqlog, "%s for %s", operation, entityType);

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return (reqlog != null) ? reqlog.copyContent(entity) : entity;
	}
	private Object[] makeResults(RequestLogger reqlog, String operation, String entityType, ClientResponse response, Class[] as) {
		if (as == null || as.length == 0)
			return null;

		logRequest(reqlog, "%s for %s", operation, entityType);

		MultiPart entity = response.getEntity(MultiPart.class);
		if (entity == null)
			return null;

		List<BodyPart> partList = entity.getBodyParts();
		if (partList == null)
			return null;

		int partCount = partList.size();
		if (partCount == 0)
			return null;
		if (partCount != as.length)
			throw new FailedRequestException("read expected " + as.length
					+ " parts but got " + partCount + " parts");

		Object[] parts = new Object[partCount];
		for (int i = 0; i < partCount; i++) {
			Object part = partList.get(i).getEntityAs(as[i]);
			parts[i] = (reqlog != null) ? reqlog.copyContent(part) : part;
		}

		response.close();

		return parts;
	}

	private void logRequest(RequestLogger reqlog, String message, Object... params) {
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

	private String stringJoin(Collection collection, String separator, String defaultValue) {
		if (collection == null || collection.size() == 0)
			return defaultValue;

		StringBuilder builder = null;
		for (Object value: collection) {
			if (builder == null)
				builder = new StringBuilder();
			else
				builder.append(separator);

			builder.append(value);
		}

		return (builder != null) ? builder.toString() : null;
	}

	// backdoors for testing
	public Client getClient() {
		return client;
	}
	public WebResource getConnection() {
		return connection;
	}
}
