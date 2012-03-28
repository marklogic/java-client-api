package com.marklogic.client.impl;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

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
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.ValueLocator;
import com.marklogic.client.config.search.KeyValueQueryDefinition;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.StringQueryDefinition;
import com.marklogic.client.config.search.StructuredQueryDefinition;
import com.marklogic.client.io.OutputStreamSender;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.ApacheHttpClientState;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.Boundary;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;

public class JerseyServices implements RESTServices {
	static final private Logger logger = LoggerFactory
			.getLogger(JerseyServices.class);

	private ApacheHttpClient client;
	private WebResource      connection;

	public JerseyServices() {
	}

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
			if (context != null)
				type = Authentication.BASIC;
			else
				throw new IllegalArgumentException("No authentication type provided");
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

		// ClientConfig config = new DefaultClientConfig();
		// see also DefaultApacheHttpClient4Config()
		DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
		config.getProperties().put(ApacheHttpClientConfig.PROPERTY_PREEMPTIVE_AUTHENTICATION, true);
		if (context != null)
			// TODO: confirm that verifier can be null or supply default verifier that returns true
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(verifier, context));

		ApacheHttpClientState state = config.getState();
		state.setCredentials(null, host, port, user, password);

//		client = ApacheHttpClient4.create(config);
		client = ApacheHttpClient.create(config);
		if (type == Authentication.BASIC)
			client.addFilter(new HTTPBasicAuthFilter(user, password));
		else if (type == Authentication.DIGEST)
			client.addFilter(new HTTPDigestAuthFilter(user, password));
		else
			throw new MarkLogicInternalException(
					"Internal error - unknown authentication type: "
							+ type.name());
		connection = client.resource("http://" + host + ":" + port + "/v1/");

// NOTE:  can get Apache HTTPClient object with client getClientHandler().getHttpClient() 
	}

	public void release() {
		logger.info("Releasing connection");

		connection = null;
		client.destroy();
	}

	public void deleteDocument(DocumentIdentifier docId, String transactionId, Set<Metadata> categories)
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

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException("Could not delete non-existent document");
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to delete documents");
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("delete failed: "+status.getReasonPhrase());
	}

	// TODO: does an Input Stream or Reader handle need to cache the response so
	// it can close the response?

	public <T> T getDocument(DocumentIdentifier docId, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String mimetype, Class<T> as)
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
			resource = resource.header("range", extraParams.get("range"));

		ClientResponse response = resource.get(ClientResponse.class);

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

		updateDocumentIdentifier(docId, response.getHeaders());

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return entity;
	}

	public Object[] getDocument(DocumentIdentifier docId, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String[] mimetypes, Class[] as)
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
			parts[i] = partList.get(i).getEntityAs(as[i]);
		}

		response.close();

		return parts;
	}

	public boolean head(DocumentIdentifier docId, String transactionId) throws ForbiddenUserException, FailedRequestException {
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

		updateDocumentIdentifier(docId, headers);

		return true;
	}
	public void putDocument(DocumentIdentifier docId, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String mimetype, Object value)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Document write with null document identifier");
		String uri = docId.getUri();
		if (uri == null)
			throw new IllegalArgumentException("Document write for document identifier without uri");
		if (value == null)
			throw new IllegalArgumentException("Document write with null value for "+uri);

		logger.info("Putting {} in transaction {}", uri, transactionId);

		WebResource webResource = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId, extraParams)
			);
		WebResource.Builder builder = (mimetype != null) ?
			webResource.type(mimetype) : webResource.getRequestBuilder();
		ClientResponse response = builder.put(ClientResponse.class,
						(value instanceof OutputStreamSender) ?
								new StreamingOutputImpl((OutputStreamSender) value) : value);

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
	public void putDocument(DocumentIdentifier docId, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String[] mimetypes, Object[] values)
	throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		if (docId == null)
			throw new IllegalArgumentException("Document write with null document identifier");
		String uri = docId.getUri();
		if (uri == null)
			throw new IllegalArgumentException("Document write for document identifier without uri");
		if (values == null || values.length == 0)
			throw new IllegalArgumentException("Document write with null values for "+uri);

		logger.info("Putting multipart for {} in transaction {}", uri, transactionId);

		if (mimetypes == null || mimetypes.length == 0)
			throw new BadRequestException("mime types not specified for write");
		if (values == null || values.length == 0)
			throw new BadRequestException("values not specified for write");
		if (mimetypes.length != values.length)
			throw new BadRequestException(
					"mistmatch between mime types and values for write");

		MultiPart multiPart = new MultiPart();
		multiPart.setMediaType(new MediaType("multipart", "mixed"));
		for (int i = 0; i < mimetypes.length; i++) {
			String[] typeParts = mimetypes[i].contains("/") ? mimetypes[i]
					.split("/", 2) : null;
			multiPart = multiPart
					.bodyPart(new BodyPart(
							(values[i] instanceof OutputStreamSender) ? new StreamingOutputImpl(
									(OutputStreamSender) values[i]) : values[i],
							typeParts != null ? new MediaType(typeParts[0],
									typeParts[1]) : MediaType.WILDCARD_TYPE));
		}

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri,
				categories, transactionId, extraParams, true);

		ClientResponse response = makeDocumentResource(docParams).type(
				Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE))
				.put(ClientResponse.class, multiPart);

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

	public String openTransaction() throws ForbiddenUserException, FailedRequestException {
		logger.info("Opening transaction");

		MultivaluedMap<String, String> transParams = new MultivaluedMapImpl();
		transParams.add("name", "java-client-" + new Random().nextLong());

		ClientResponse response = connection.path("transactions")
				.queryParams(transParams).post(ClientResponse.class);

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

	public void commitTransaction(String transactionId) throws ForbiddenUserException, FailedRequestException {
		logger.info("Committing transaction {}", transactionId);

		if (transactionId == null)
			throw new MarkLogicInternalException("Committing transaction without id");

		ClientResponse response = connection.path(
				"transactions/" + transactionId).put(ClientResponse.class);

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to commit transactions");
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("transaction commit failed: " + status.getReasonPhrase());
	}

	public void rollbackTransaction(String transactionId) throws ForbiddenUserException, FailedRequestException {
		logger.info("Rolling back transaction {}", transactionId);

		if (transactionId == null)
			throw new MarkLogicInternalException("Rolling back transaction without id");

		ClientResponse response = connection.path(
				"transactions/" + transactionId).delete(ClientResponse.class);
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to rollback transactions");
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("transaction rollback failed: " + status.getReasonPhrase());
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri, Set<Metadata> categories, String transactionId, Map<String,String> extraParams) {
		return makeDocumentParams(uri, categories, transactionId, extraParams, false);
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri,
			Set<Metadata> categories, String transactionId, Map<String, String> extraParams, boolean withContent) {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();
		if (extraParams != null && extraParams.size() > 0) {
			for (Map.Entry<String, String> entry: extraParams.entrySet()) {
				String extraKey = entry.getKey();
				if (!"range".equalsIgnoreCase(extraKey))
					docParams.putSingle(extraKey, entry.getValue());
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

    // FIXME: is this even close to reasonable?
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
        } else if (queryDef instanceof StructuredQueryDefinition) {
            String structure = ((StructuredQueryDefinition) queryDef).serialize();
            response = connection.path("search").type("application/xml").post(ClientResponse.class, structure);
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
	public <T> T getValue(String type, String key, String mimetype, Class<T> as)
	throws ForbiddenUserException, FailedRequestException {
		logger.info("Getting {}/{}", type, key);

		ClientResponse response = connection.path(type+"/"+key).accept(mimetype).get(ClientResponse.class);

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

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return entity;
	}
	public <T> T getValues(String type, String mimetype, Class<T> as)
	throws ForbiddenUserException, FailedRequestException {
		logger.info("Getting {}", type);

		ClientResponse response = connection.path(type).accept(mimetype).get(ClientResponse.class);

		ClientResponse.Status status = response.getClientResponseStatus();
		if (status == ClientResponse.Status.FORBIDDEN) {
			response.close();
			throw new ForbiddenUserException("User is not allowed to read "+type);
		}
		if (status != ClientResponse.Status.OK) {
			response.close();
			throw new FailedRequestException(type+" read failed: " + status.getReasonPhrase());
		}

		T entity = response.getEntity(as);
		if (as != InputStream.class && as != Reader.class)
			response.close();

		return entity;
	}
	public void postValue(String type, String key, String mimetype, Object value)
	throws ForbiddenUserException, FailedRequestException {
		putPostValueImpl("post", type, key, mimetype, value);
	}
	public void putValue(String type, String key, String mimetype, Object value)
	throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		putPostValueImpl("put", type, key, mimetype, value);
	}
	private void putPostValueImpl(String method, String type, String key, String mimetype, Object value) {
		logger.info("Putting {}/{}", type, key);

		Object sentValue = (value instanceof OutputStreamSender) ?
				new StreamingOutputImpl((OutputStreamSender) value) : value;

		ClientResponse response = null;
		ClientResponse.Status expectedStatus = null;
		if ("put".equals(method)) {
			response = connection.path(type+"/"+key).type(mimetype).put(ClientResponse.class, sentValue);
			expectedStatus = ClientResponse.Status.NO_CONTENT;
		} else if ("post".equals(method)) {
			response = connection.path(type).type(mimetype).post(ClientResponse.class, sentValue);
			expectedStatus = ClientResponse.Status.CREATED;
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
	public void deleteValue(String type, String key) throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException {
		logger.info("Deleting {}/{}", type, key);

		ClientResponse response = connection.path(type+"/"+key).delete(ClientResponse.class);

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to delete "+type);
		if (status == ClientResponse.Status.NOT_FOUND)
			throw new ResourceNotFoundException(type+" not found for delete");
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("delete failed: " + status.getReasonPhrase());
	}
	public void deleteValues(String type) throws ForbiddenUserException, FailedRequestException {
		logger.info("Deleting {}", type);

		ClientResponse response = connection.path(type).delete(ClientResponse.class);

		ClientResponse.Status status = response.getClientResponseStatus();
		response.close();
		if (status == ClientResponse.Status.FORBIDDEN)
			throw new ForbiddenUserException("User is not allowed to delete "+type);
		if (status != ClientResponse.Status.NO_CONTENT)
			throw new FailedRequestException("delete failed: " + status.getReasonPhrase());
	}

	// backdoors for testing
	public Client getClient() {
		return client;
	}
	public WebResource getConnection() {
		return connection;
	}
}
