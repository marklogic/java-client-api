package com.marklogic.client.iml;

import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;

public class JerseyServices implements RESTServices {
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
}
