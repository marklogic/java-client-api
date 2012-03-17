package com.marklogic.client.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.AbstractDocumentManager.Metadata;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.Boundary;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;

public class JerseyServices implements RESTServices {
	static final private Logger logger = LoggerFactory.getLogger(JerseyServices.class);

	private Client client;
	private WebResource connection;

	public JerseyServices () {
	}

	public void connect(String host, int port, String user, String password, Authentication type) {
		if (logger.isInfoEnabled())
			logger.info("Connecting to {} at {} as {}",new Object[]{host,port,user});

		ClientConfig config = new DefaultClientConfig();
		client = ApacheHttpClient.create(config);
		if (type == Authentication.BASIC)
			client.addFilter(new HTTPBasicAuthFilter(user, password));
		else if (type == Authentication.DIGEST)
			client.addFilter(new HTTPDigestAuthFilter(user, password));
		else
			throw new RuntimeException("Internal error - unknown authentication type: "+type.name());
		connection = client.resource("http://"+host+":"+port+"/v1/");
	}
	public void release() {
		logger.info("Releasing connection");

		connection = null;
		client.destroy();
	}

	public void delete(String uri, String transactionId, Set<Metadata> categories) {
		logger.info("Deleting {} in transaction {}", uri, transactionId);

		ClientResponse response = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId)
				).delete(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close(); 
		if (status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("delete failed "+status);
		}
	}
	// TODO:  does the handle need to cache the response so it can close the response?
	public <T> T get(String uri, String transactionId, Set<Metadata> categories, String mimetype, Class<T> as) {
		logger.info("Getting {} in transaction {}", uri, transactionId);

		ClientResponse response = makeDocumentResource(
				makeDocumentParams(uri, categories, transactionId)
			).accept(mimetype).get(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.OK) {
			response.close(); 
			throw new RuntimeException("read failed "+status);
		}
		return response.getEntity(as);
	}
	public Object[] get(String uri, String transactionId, Set<Metadata> categories, String[] mimetypes, Class[] as) {
		logger.info("Getting multipart for {} in transaction {}", uri, transactionId);

		if (mimetypes == null || mimetypes.length == 0)
			throw new RuntimeException("mime types not specified for read");
		if (as == null || as.length == 0)
			throw new RuntimeException("handle classes not specified for read");
		if (mimetypes.length != as.length)
			throw new RuntimeException("mistmatch between mime types and handle classes for read");

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri, categories, transactionId, true);
		if (mimetypes[0].startsWith("application/")) {
			docParams.add("format", mimetypes[0].substring("application/".length()));
		}

		ClientResponse response =
			makeDocumentResource(docParams).accept(Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE)).get(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.OK) {
			response.close(); 
			throw new RuntimeException("read failed "+status);
		}

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
			throw new RuntimeException(
					"read expected "+as.length+" parts but got "+partCount+" parts");

		Object[] parts = new Object[partCount];
		for (int i=0; i < partCount; i++) {
			parts[i] = partList.get(i).getEntityAs(as[i]);
		}

		return parts;
	}
	public Map<String,List<String>> head(String uri, String transactionId) {
		logger.info("Requesting head for {} in transaction {}", uri, transactionId);

		ClientResponse response = makeDocumentResource(
					makeDocumentParams(uri, null, transactionId)
				).head();
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close(); 
		if (status == ClientResponse.Status.NOT_FOUND) {
			return null;
		}
		if (status != ClientResponse.Status.OK) {
			throw new RuntimeException("head failed "+response.getClientResponseStatus());
		}
		return response.getHeaders();
	}
	public void put(String uri, String transactionId, Set<Metadata> categories, String mimetype, Object value) {
		logger.info("Putting {} in transaction {}", uri, transactionId);

		ClientResponse response = makeDocumentResource(
					makeDocumentParams(uri, categories, transactionId)
				).type(mimetype).put(ClientResponse.class, value);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close(); 
		if (status != ClientResponse.Status.CREATED && status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("write failed "+status);
		}
	}
	public void put(String uri, String transactionId, Set<Metadata> categories, String[] mimetypes, Object[] values) {
		logger.info("Putting multipart for {} in transaction {}", uri, transactionId);

		if (mimetypes == null || mimetypes.length == 0)
			throw new RuntimeException("mime types not specified for write");
		if (values == null || values.length == 0)
			throw new RuntimeException("values not specified for write");
		if (mimetypes.length != values.length)
			throw new RuntimeException("mistmatch between mime types and values for write");

		MultiPart multiPart = new MultiPart();
		multiPart.setMediaType(new MediaType("multipart","mixed"));
		for (int i=0; i < mimetypes.length; i++) {
			String[] typeParts = 
				mimetypes[i].contains("/") ? mimetypes[i].split("/", 2) : null;
			multiPart = multiPart.bodyPart(new BodyPart(values[i],
				typeParts != null ?
					new MediaType(typeParts[0],typeParts[1]) : MediaType.WILDCARD_TYPE
					));
		}

		MultivaluedMap<String, String> docParams = makeDocumentParams(uri, categories, transactionId, true);

		ClientResponse response =
			makeDocumentResource(docParams).type(Boundary.addBoundary(MultiPartMediaTypes.MULTIPART_MIXED_TYPE)).put(ClientResponse.class, multiPart);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close(); 
		if (status != ClientResponse.Status.CREATED && status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("write failed "+status);
		}
	}

	private MultivaluedMap<String, String> makeDocumentParams(String uri, Set<Metadata> categories, String transactionId) {
		return makeDocumentParams(uri, categories, transactionId, false);
	}
	private MultivaluedMap<String, String> makeDocumentParams(String uri, Set<Metadata> categories, String transactionId, boolean withContent) {
		MultivaluedMap<String, String> docParams = new MultivaluedMapImpl();
		docParams.add("uri", uri);
		if (categories == null || categories.size() == 0) {
			docParams.add("category", "content");
		} else {
			if (withContent)
				docParams.add("category", "content");
			if (categories.contains(Metadata.ALL)) {
				for (String category: new String[]{
					"collections", "permissions", "properties", "quality"
					})
					docParams.add("category", category);
			} else {
				for (Metadata category: categories)
					docParams.add("category", category.name().toLowerCase());
			}
		}
		if (transactionId != null)
			docParams.add("txid", transactionId);
		return docParams;
	}

	private WebResource makeDocumentResource(MultivaluedMap<String, String> queryParams) {
		return connection.path("documents").queryParams(queryParams);
	}
}
