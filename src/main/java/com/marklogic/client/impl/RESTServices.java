package com.marklogic.client.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.marklogic.client.BadRequestException;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.AbstractDocumentManager.Metadata;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.config.search.QueryDefinition;
import com.marklogic.client.config.search.SearchOptions;

public interface RESTServices {
	public void connect(String host, int port, String user, String password, Authentication type);
	public void release();

	public void deleteDocument(String uri, String transactionId, Set<Metadata> categories)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	public <T> T getDocument(String uri, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String mimetype, Class<T> as)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	public Object[] getDocument(String uri, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String[] mimetypes, Class[] as)
		throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	public Map<String,List<String>> head(String uri, String transactionId)
		throws ForbiddenUserException, FailedRequestException;
	public void putDocument(String uri, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String mimetype, Object value)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	public void putDocument(String uri, String transactionId, Set<Metadata> categories, Map<String,String> extraParams, String[] mimetypes, Object[] values)
		throws BadRequestException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException;

    public <T> T search(Class <T> as, QueryDefinition queryDef, String mimetype, long start, String transactionId)
    	throws ForbiddenUserException, FailedRequestException;

	public String openTransaction() throws ForbiddenUserException, FailedRequestException;
	public void commitTransaction(String transactionId) throws ForbiddenUserException, FailedRequestException;
	public void rollbackTransaction(String transactionId) throws ForbiddenUserException, FailedRequestException;

	public SearchOptions getOptions(String searchOptionsName);
	public void putOptions(String searchOptionsName, SearchOptions options);
	public void deleteOptions(String searchOptionsName);

	// namespaces, etc.
	public <T> T getValue(String type, String key, String mimetype, Class<T> as)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	public <T> T getValues(String type, String mimetype, Class<T> as)
		throws ForbiddenUserException, FailedRequestException;
	public void postValue(String type, String key, String mimetype, Object value)
		throws ForbiddenUserException, FailedRequestException;
	public void putValue(String type, String key, String mimetype, Object value)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	public void deleteValue(String type, String key)
		throws ResourceNotFoundException, ForbiddenUserException, FailedRequestException;
	public void deleteValues(String type)
		throws ForbiddenUserException, FailedRequestException;
}
