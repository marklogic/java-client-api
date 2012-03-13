package com.marklogic.client.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.marklogic.client.AbstractDocumentBuffer.Metadata;
import com.marklogic.client.DatabaseClientFactory.Authentication;

public interface RESTServices {
	public void connect(String host, int port, String user, String password, Authentication type);
	public void release();

	public void delete(String uri, String transactionId);
	public <T> T get(Class<T> as, String uri, String mimetype, Set<Metadata> categories, String transactionId);
	public Map<String,List<String>> head(String uri, String transactionId);
	public void put(String uri, String mimetype, Object value, Set<Metadata> categories, String transactionId);
}
