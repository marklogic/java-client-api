package com.marklogic.client.iml;

import com.marklogic.client.DatabaseClientFactory.Authentication;

public interface RESTServices {
	public void connect(String host, int port, String user, String password, Authentication type);
	public void release();
}
