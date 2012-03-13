package com.marklogic.client.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.AbstractDocumentBuffer.Metadata;
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

	public void delete(String uri, String transactionId) {
		logger.info("Deleting {} in transaction {}", uri, transactionId);

		ClientResponse response = makeDocumentResource(uri, null, transactionId).delete(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close(); 
		if (status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("delete failed "+status);
		}
	}
	public Map<String,List<String>> head(String uri, String transactionId) {
		logger.info("Requesting head for {} in transaction {}", uri, transactionId);

		ClientResponse response = makeDocumentResource(uri, null, transactionId).head();
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
	// TODO:  does the handle need to cache the response so it can close the response?
	public <T> T get(Class<T> as, String uri, String mimetype, Set<Metadata> categories, String transactionId) {
		logger.info("Getting {} in transaction {}", uri, transactionId);

		ClientResponse response =
			makeDocumentResource(uri, categories, transactionId).accept(mimetype).get(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.OK) {
			response.close(); 
			throw new RuntimeException("read failed "+status);
		}
		return response.getEntity(as);
	}
	public void put(String uri, String mimetype, Object value, Set<Metadata> categories, String transactionId) {
		logger.info("Putting {} in transaction {}", uri, transactionId);

		ClientResponse response =
			makeDocumentResource(uri, categories, transactionId).type(mimetype).put(ClientResponse.class, value);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		response.close(); 
		if (status != ClientResponse.Status.CREATED && status != ClientResponse.Status.NO_CONTENT) {
			throw new RuntimeException("write failed "+status);
		}
	}

	private WebResource makeDocumentResource(String uri, Set<Metadata> categories, String transactionId) {
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("uri", uri);
		if (categories == null || categories.size() == 0)
			queryParams.add("category", "content");
		else
			for (Metadata category: categories)
				queryParams.add("category", category.name().toLowerCase());
		if (transactionId != null)
			queryParams.add("txid", transactionId);
		return connection.path("documents").queryParams(queryParams);
	}
}
