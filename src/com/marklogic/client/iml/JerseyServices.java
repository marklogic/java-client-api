package com.marklogic.client.iml;

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

class JerseyServices implements RESTServices {
	private Client client;
	private WebResource connection;

	JerseyServices () {
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

	public void delete(String uri) {
		ClientResponse response = makeDocumentResource(uri, null).delete(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		if (response.getClientResponseStatus() != ClientResponse.Status.OK)
			throw new RuntimeException("delete failed "+response.getClientResponseStatus());
	}
// TODO: use to verify existence and get format
	public void head(String uri) {
		ClientResponse response = makeDocumentResource(uri, null).head();
		// TODO: more fine-grained inspection of response status
		if (response.getClientResponseStatus() != ClientResponse.Status.OK)
			throw new RuntimeException("delete failed "+response.getClientResponseStatus());
	}
	public <T> T get(Class<T> as, String uri, String mimetype, Metadata... metadata) {
		ClientResponse response =
			makeDocumentResource(uri, metadata).accept(mimetype).get(ClientResponse.class);
		// TODO: more fine-grained inspection of response status
		if (response.getClientResponseStatus() != ClientResponse.Status.OK)
			throw new RuntimeException("read failed "+response.getClientResponseStatus());
		return response.getEntity(as);
	}
	public void put(String uri, String mimetype, Object value, Metadata... metadata) {
		ClientResponse response =
			makeDocumentResource(uri, metadata).type(mimetype).put(ClientResponse.class, value);
		// TODO: more fine-grained inspection of response status
		if (response.getClientResponseStatus() != ClientResponse.Status.OK)
			throw new RuntimeException("write failed "+response.getClientResponseStatus());
	}

	private WebResource makeDocumentResource(String uri, Metadata... metadata) {
		// TODO: move category from URI step to parameter
		String category = (metadata == null || metadata.length == 0) ? "content" :
			metadata[1].name().toLowerCase();
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("uri", uri);
		return connection.path("documents/"+category).queryParams(queryParams);
	}
}
