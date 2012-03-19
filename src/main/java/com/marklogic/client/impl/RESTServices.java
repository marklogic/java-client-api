package com.marklogic.client.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.marklogic.client.AbstractDocumentManager.Metadata;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.ValueLocator;

public interface RESTServices {
	public void connect(String host, int port, String user, String password, Authentication type);
	public void release();

	public String openTransaction();

	public void deleteDocument(String uri, String transactionId, Set<Metadata> categories);
	public <T> T getDocument(String uri, String transactionId, Set<Metadata> categories, String mimetype, Class<T> as);
	public Object[] getDocument(String uri, String transactionId, Set<Metadata> categories, String[] mimetypes, Class[] as);
	public Map<String,List<String>> head(String uri, String transactionId);
	public void putDocument(String uri, String transactionId, Set<Metadata> categories, String mimetype, Object value);
	public void putDocument(String uri, String transactionId, Set<Metadata> categories, String[] mimetypes, Object[] values);

    public <T> T stringSearch(Class <T> as, String uri, String searchText, String transactionId);
    public <T> T keyValueSearch(Class <T> as, String uri, Map<ValueLocator, String> keyValues, String transactionId);

	public void commitTransaction(String transactionId);
	public void rollbackTransaction(String transactionId);
}
