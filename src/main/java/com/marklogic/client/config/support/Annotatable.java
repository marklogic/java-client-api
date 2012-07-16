package com.marklogic.client.config.support;

import java.util.List;

import com.marklogic.client.config.QueryOptions.QueryAnnotation;

/**
 * Provides simple interface for adding annotations and retrieving them
 */
public interface Annotatable<T> {
	T annotate(String xmlAnnotation);
	public List<QueryAnnotation> getAnnotations();
}