package com.marklogic.client.iml;

import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import com.marklogic.client.AbstractDocument.Metadata;
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
	private Client client;
	private WebResource connection;

	public JerseyServices () {
	}

	public void connect(String host, int port, String user, String password, Authentication type) {
		ClientConfig config = new DefaultClientConfig();
		client = ApacheHttpClient.create(config);
		if (type == Authentication.BASIC)
			client.addFilter(new HTTPBasicAuthFilter(user, password));
		else if (type == Authentication.DIGEST)
			client.addFilter(new HTTPDigestAuthFilter(user, password));
		else
			throw new RuntimeException("Internal error - unknown authentication type: "+type.name());
		connection = client.resource("http://"+host+":"+port+"/");
	}
	public void release() {
		connection = null;
		client.destroy();
	}

	public void delete(String uri, String transactionId) {
		ClientResponse response = makeDocumentResource(uri, null, transactionId).delete(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		if (response.getClientResponseStatus() != ClientResponse.Status.NO_CONTENT)
			throw new RuntimeException("delete failed "+response.getClientResponseStatus());
	}
// TODO: use to verify existence and get format
	public void head(String uri, String transactionId) {
		ClientResponse response = makeDocumentResource(uri, null, transactionId).head();
		// TODO: more fine-grained inspection of response status
		if (response.getClientResponseStatus() != ClientResponse.Status.OK)
			throw new RuntimeException("delete failed "+response.getClientResponseStatus());
	}
	public <T> T get(Class<T> as, String uri, String mimetype, Set<Metadata> categories, String transactionId) {
		ClientResponse response =
			makeDocumentResource(uri, categories, transactionId).accept(mimetype).get(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		if (response.getClientResponseStatus() != ClientResponse.Status.OK)
			throw new RuntimeException("read failed "+response.getClientResponseStatus());
		return response.getEntity(as);
	}
	public void put(String uri, String mimetype, Object value, Set<Metadata> categories, String transactionId) {
		ClientResponse response =
			makeDocumentResource(uri, categories, transactionId).type(mimetype).put(ClientResponse.class, value);
		// TODO: more fine-grained inspection of response status
		ClientResponse.Status status = response.getClientResponseStatus();
		if (status != ClientResponse.Status.CREATED && status != ClientResponse.Status.NO_CONTENT)
			throw new RuntimeException("write failed "+response.getClientResponseStatus());
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
