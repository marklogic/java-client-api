package com.marklogic.client.config;

import java.util.List;

import org.w3c.dom.Element;

/**
 * Objects implementing this interface can be annotated with metadata
 * from a client application, to provide information about particular
 * query configuration objects to the consuming application.
 */
public interface QueryAnnotatable  {

	public void addQueryAnnotation(Element annotation);
	public List<Element> getQueryAnnotations();
	
}
